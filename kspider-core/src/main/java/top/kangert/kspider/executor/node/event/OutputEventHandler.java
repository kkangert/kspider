package top.kangert.kspider.executor.node.event;

import top.kangert.kspider.config.SpiderConfig;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.node.OutputExecutor;
import top.kangert.kspider.executor.node.event.OutputEventPublisher.OutputEventBean;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.model.SpiderOutput;
import top.kangert.kspider.support.DataSourceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OutputEventHandler {

    /**
     * 输出到数据库中的表名
     */
    String OUTPUT_TABLE_NAME = "tableName";

    /**
     * CSV 文件的名称
     */
    String OUTPUT_CSV_NAME = "csvName";

    /**
     * CSV 文件的编码
     */
    String OUTPUT_CSV_ENCODING = "csvEncoding";

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private DataSourceManager dataSourceManager;

    @EventListener(value = OutputEventBean.class, condition = "#eventBean.event.equals(T(top.kangert.kspider.constant.OutputType).DATABASE.variableName)")
    private void outputDatabase(OutputEventBean eventBean) {
        SpiderNode node = eventBean.getNode();
        List<SpiderOutput.OutputItem> outputItems = eventBean.getOutputItems();

        // 获取数据源 ID
        String dsId = node.getJsonProperty(Constants.DATASOURCE_ID);
        // 获取表名
        String tableName = node.getJsonProperty(OUTPUT_TABLE_NAME);

        if (StrUtil.isBlank(dsId)) {
            log.warn("数据源 ID 不能为空");
        } else if (StrUtil.isBlank(tableName)) {
            log.warn("表名不能为空");
        } else {
            if (outputItems == null || outputItems.isEmpty()) {
                return;
            }

            JdbcTemplate template = new JdbcTemplate(dataSourceManager.getDataSource(Long.parseLong(dsId)));

            StringBuilder preSql = new StringBuilder("INSERT INTO ");
            preSql.append(tableName);
            preSql.append(" (");
            StringBuilder nextSql = new StringBuilder(" VALUES (");

            // 设置字段名和对应的占位符
            for (int i = 0; i < outputItems.size(); i++) {
                SpiderOutput.OutputItem item = outputItems.get(i);
                if (StrUtil.isNotBlank(item.getName())) {
                    if (i == outputItems.size() - 1) {
                        preSql.append(item.getName());
                        preSql.append(")");
                        nextSql.append("?)");
                    } else {
                        preSql.append(item.getName());
                        preSql.append(",");
                        nextSql.append("?,");
                    }
                }
            }

            List<Object> values = outputItems.stream().map(item -> item.getValue()).collect(Collectors.toList());

            if (!values.isEmpty()) {
                try {
                    // 执行 sql
                    template.update(preSql.append(nextSql).toString(), values.toArray());
                } catch (Exception e) {
                    log.error("执行 sql 出错", e);
                    ExceptionUtil.wrapAndThrow(e);
                }
            }
        }
    }

    @EventListener(value = OutputEventBean.class, condition = "#eventBean.event.equals(T(top.kangert.kspider.constant.OutputType).CSV.variableName)")
    private void outputCSV(OutputEventBean eventBean) {
        SpiderContext context = eventBean.getContext();
        SpiderNode node = eventBean.getNode();
        List<SpiderOutput.OutputItem> outputItems = eventBean.getOutputItems();

        // 获取文件名
        String csvName = node.getJsonProperty(OUTPUT_CSV_NAME);
        if (StrUtil.isBlank(csvName)) {
            // 生成一个不重复的hash字符串
            csvName = StrUtil.uuid().substring(0, RandomUtil.randomInt(8, 12));
        }
        if (outputItems == null || outputItems.isEmpty()) {
            return;
        }
        String key = context.getId() + "-" + node.getNodeId();
        Map<String, CSVPrinter> cachePrinter = OutputExecutor.getCachePrinter();
        CSVPrinter printer = cachePrinter.get(key);

        // 所有的记录值
        List<String> records = new ArrayList<>(outputItems.size());
        // 生成头部列表
        List<String> headers = outputItems.stream().map(item -> item.getName()).collect(Collectors.toList());

        try {
            if (printer == null) {
                synchronized (cachePrinter) {
                    printer = cachePrinter.get(key);
                    if (printer == null) {
                        CSVFormat format = CSVFormat.DEFAULT.withHeader(headers.toArray(new String[headers.size()]));
                        String fileName = spiderConfig.getWorkspace() + File.separator + "files" + File.separator
                                + context.getFlowId() + "_" + context.getTaskId() + File.separator + csvName + ".csv";
                        FileOutputStream os = new FileOutputStream(fileName);
                        String csvEncoding = node.getJsonProperty(OUTPUT_CSV_ENCODING);
                        if ("UTF-8BOM".equals(csvEncoding)) {
                            csvEncoding = csvEncoding.substring(0, 5);
                            byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
                            os.write(bom);
                            os.flush();
                        }
                        OutputStreamWriter osw = new OutputStreamWriter(os, csvEncoding);
                        printer = new CSVPrinter(osw, format);
                        cachePrinter.put(key, printer);
                    }
                }
            }

            // 转储数据
            for (int i = 0; i < headers.size(); i++) {
                SpiderOutput.OutputItem item = outputItems.get(i);
                if (item.getValue() != null) {
                    records.add(item.getValue().toString());
                }
            }
            // 打印数据
            synchronized (cachePrinter) {
                if (!records.isEmpty()) {
                    printer.printRecord(records);
                }
            }

        } catch (IOException e) {
            log.error("文件输出出现错误", e);
            ExceptionUtil.wrapAndThrow(e);
        }
    }
}
