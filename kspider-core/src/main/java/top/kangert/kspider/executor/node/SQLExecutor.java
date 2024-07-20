package top.kangert.kspider.executor.node;

import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.Grammar;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.support.DataSourceManager;
import top.kangert.kspider.support.ExpressionEngine;
import top.kangert.kspider.support.Grammarly;
import top.kangert.kspider.util.ExtractUtils;
import top.kangert.kspider.websocket.WebSocketEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/**
 * SQL 执行器
 */
@Component
@Slf4j
public class SQLExecutor implements NodeExecutor, Grammarly {

    /**
     * SQL
     */
    String SQL = "sql";

    /**
     * 语句类型
     */
    String STATEMENT_TYPE = "statementType";

    /**
     * 是否输出到 SqlRowSet
     */
    String SELECT_RESULT_SQL_ROW_SET = "isSqlRowSet";


    public static final String STATEMENT_SELECT = "select";
    public static final String STATEMENT_SELECT_ONE = "selectOne";
    public static final String STATEMENT_SELECT_INT = "selectInt";
    public static final String STATEMENT_INSERT = "insert";
    public static final String STATEMENT_UPDATE = "update";
    public static final String STATEMENT_DELETE = "delete";
    public static final String STATEMENT_INSERT_PK = "insertOfPk";

    @Resource
    private ExpressionEngine expressionEngine;

    @Resource
    private DataSourceManager dataSourceManager;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String dsId = node.getJsonProperty(Constants.DATASOURCE_ID);
        String sql = node.getJsonProperty(SQL);
        if (StrUtil.isBlank(dsId)) {
            log.warn("数据源 ID 不能为空");
        } else if (StrUtil.isBlank(sql)) {
            log.warn("sql 不能为空");
        } else {
            JdbcTemplate template = new JdbcTemplate(dataSourceManager.getDataSource(Long.parseLong(dsId)));
            // 把变量替换成占位符
            List<String> parameters = ExtractUtils.getMatchers(sql, "#(.*?)#", true);
            sql = sql.replaceAll("#(.*?)#", "?");
            try {
                Object sqlObject = expressionEngine.execute(sql, variables);
                if (sqlObject == null) {
                    log.warn("获取的 sql 为空");
                    return;
                }
                sql = sqlObject.toString();
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, SQL, sql);
            } catch (Exception e) {
                log.error("获取 sql 出错", e);
                ExceptionUtil.wrapAndThrow(e);
            }

            int size = parameters.size();
            Object[] params = new Object[size];
            boolean hasList = false;
            int parameterSize = 0;
            for (int i = 0; i < size; i++) {
                Object parameter = expressionEngine.execute(parameters.get(i), variables);
                if (parameter != null) {
                    // 当参数中存在 List 或者数组时，认为是批量操作
                    if (parameter instanceof List) {
                        hasList = true;
                        parameterSize = Math.max(parameterSize, ((List<?>) parameter).size());
                    } else if (parameter.getClass().isArray()) {
                        hasList = true;
                        parameterSize = Math.max(parameterSize, Array.getLength(parameter));
                    }
                }
                params[i] = parameter;
            }

            String statementType = node.getJsonProperty(STATEMENT_TYPE);
            log.debug("执行 sql：{}", sql);
            if (STATEMENT_SELECT.equals(statementType)) {
                boolean isSqlRowSet = Constants.YES.equals(node.getJsonProperty(SELECT_RESULT_SQL_ROW_SET));
                try {
                    if (isSqlRowSet) {
                        variables.put(Constants.SQL_RESULT, template.queryForRowSet(sql, params));
                    } else {
                        variables.put(Constants.SQL_RESULT, template.queryForList(sql, params));
                    }
                } catch (Exception e) {
                    variables.put(Constants.SQL_RESULT, null);
                    log.error("执行 sql 出错", e);
                    ExceptionUtil.wrapAndThrow(e);
                }
            } else if (STATEMENT_SELECT_ONE.equals(statementType)) {
                Map<String, Object> rs;
                try {
                    rs = template.queryForMap(sql, params);
                    variables.put(Constants.SQL_RESULT, rs);
                } catch (Exception e) {
                    variables.put(Constants.SQL_RESULT, null);
                    log.error("执行 sql 出错", e);
                    ExceptionUtil.wrapAndThrow(e);
                }
            } else if (STATEMENT_SELECT_INT.equals(statementType)) {
                Integer rs;
                try {
                    rs = template.queryForObject(sql, Integer.class, params);
                    rs = rs == null ? 0 : rs;
                    variables.put(Constants.SQL_RESULT, rs);
                } catch (Exception e) {
                    variables.put(Constants.SQL_RESULT, 0);
                    log.error("执行 sql 出错", e);
                    ExceptionUtil.wrapAndThrow(e);
                }
            } else if (STATEMENT_UPDATE.equals(statementType) || STATEMENT_INSERT.equals(statementType) || STATEMENT_DELETE.equals(statementType)) {
                try {
                    int updateCount = 0;
                    if (hasList) {
						/*
						  批量操作时，将参数 Object[] 转化为 List<Object[]>
						  当参数不为数组或 List 时，自动转为 Object[]
						  当数组或 List 长度不足时，自动取最后一项补齐
						 */
                        int[] rs = template.batchUpdate(sql, convertParameters(params, parameterSize));
                        if (rs.length > 0) {
                            updateCount = Arrays.stream(rs).sum();
                        }
                    } else {
                        updateCount = template.update(sql, params);
                    }
                    variables.put(Constants.SQL_RESULT, updateCount);
                } catch (Exception e) {
                    log.error("执行 sql 出错", e);
                    variables.put(Constants.SQL_RESULT, -1);
                    ExceptionUtil.wrapAndThrow(e);
                }
            } else if (STATEMENT_INSERT_PK.equals(statementType)) {
                try {
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    final String insertSQL = sql;
                    template.update(con -> {
                        PreparedStatement ps = con.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
                        new ArgumentPreparedStatementSetter(params).setValues(ps);
                        return ps;
                    }, keyHolder);
                    variables.put(Constants.SQL_RESULT, keyHolder.getKey().intValue());
                } catch (Exception e) {
                    log.error("执行 sql 出错", e);
                    variables.put(Constants.SQL_RESULT, -1);
                    ExceptionUtil.wrapAndThrow(e);
                }
            }
        }
    }

    @Override
    public String supportType() {
        return "sql";
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "SQL", "SQL", "iconfont icon-file-SQL", "主要用于与数据库交互（查询/修改/插入/删除等等）");
    }

    private List<Object[]> convertParameters(Object[] params, int length) {
        List<Object[]> result = new ArrayList<>(length);
        int size = params.length;
        for (int i = 0; i < length; i++) {
            Object[] parameters = new Object[size];
            for (int j = 0; j < size; j++) {
                parameters[j] = getValue(params[j], i);
            }
            result.add(parameters);
        }
        return result;
    }

    private Object getValue(Object object, int index) {
        if (object == null) {
            return null;
        } else if (object instanceof List) {
            List<?> list = (List<?>) object;
            int size = list.size();
            if (size > 0) {
                return list.get(Math.min(list.size() - 1, index));
            }
        } else if (object.getClass().isArray()) {
            int size = Array.getLength(object);
            if (size > 0) {
                Array.get(object, Math.min(-1, index));
            }
        } else {
            return object;
        }
        return null;
    }

    @Override
    public List<Grammar> grammars() {
        Grammar grammar = new Grammar();
        grammar.setComment("执行 SQL 结果");
        grammar.setFunction(Constants.SQL_RESULT);
        grammar.setReturns(Arrays.asList("List<Map<String,Object>>", "int"));
        return Collections.singletonList(grammar);
    }


}
