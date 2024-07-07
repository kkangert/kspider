package top.kangert.kspider.service.impl;

import top.kangert.kspider.dao.VariableRepository;
import top.kangert.kspider.domain.Variable;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.expression.ExpressionGlobalVariables;
import top.kangert.kspider.service.BaseService;
import top.kangert.kspider.service.VariableService;
import top.kangert.kspider.util.PageInfo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VariableServiceImpl extends BaseService implements VariableService {

    @Resource
    private VariableRepository variableRepository;

    @PostConstruct
    private void resetGlobalVariables() {
        Map<String, String> variables = variableRepository.findAll().stream()
                .collect(Collectors.toMap(Variable::getName, Variable::getVal));
        ExpressionGlobalVariables.reset(variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Variable addItem(Map<String, Object> params) {
        // 检查参数
        checkParams(params, new String[] { "varId", "name", "val" });

        // 对象转换
        Variable variable = transformEntity(params, Variable.class);

        // 新增全局变量
        Variable var = variableRepository.save(variable);

        // 重置全局变量
        resetGlobalVariables();

        return var;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Map<String, Object> params) {
        // 检查参数
        checkParams(params, new String[] { "varId" });

        // 获取变量ID
        Long varId = Long.parseLong((String) params.get("varId"));

        // 删除全局变量
        variableRepository.deleteById(varId);

        // 重置全局变量
        resetGlobalVariables();

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editItem(Map<String, Object> params) {
        // 检查参数
        checkParams(params, new String[] { "varId", "name", "val" });

        // 对象转换
        Variable variable = transformEntity(params, Variable.class);

        // 编辑全局变量
        variableRepository.save(variable);

        // 重置全局变量
        resetGlobalVariables();

    }

    @Override
    public Variable queryItem(Long varId) {
        if (varId != null) {
            throw new BaseException(ExceptionCodes.ARGUMENTS_ERROR);
        }

        Variable var = variableRepository.findVariableByVarId(varId);

        return var;
    }

    @Override
    public PageInfo<Variable> queryItems(Map<String, Object> params) {
        // 参数检查
        checkParams(params, null);

        // 处理分页
        Pageable pageable = processPage(params);

        // 查询数据
        Page<Variable> page = variableRepository.findAll(multipleConditionsBuilder(params), pageable);

        return new PageInfo<Variable>(page);
    }
}
