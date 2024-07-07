package top.kangert.kspider.executor.function;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.dao.VariableRepository;
import top.kangert.kspider.domain.Variable;
import top.kangert.kspider.executor.FunctionExecutor;
import top.kangert.kspider.service.VariableService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 全局变量相关方法
 */
@Component
@Comment("全局变量相关方法")
public class GlobalVariableFunctionExecutor implements FunctionExecutor {

    private static VariableService variableService;

    @Autowired
    private static VariableRepository variableRepository;

    @Autowired
    public void setVariableService(VariableService variableService) {
        GlobalVariableFunctionExecutor.variableService = variableService;
    }

    @Override
    public String getFunctionPrefix() {
        return "gv";
    }

    @Comment("更新全局变量")
    @Example("${gv.update('variableName', '1')}")
    public static void update(String variableName, String variableValue) {
        Variable var = variableRepository.findByName(variableName);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("varId", var.getVarId());
        params.put("name", variableName);
        params.put("val", variableValue);
        variableService.editItem(params);
    }

}
