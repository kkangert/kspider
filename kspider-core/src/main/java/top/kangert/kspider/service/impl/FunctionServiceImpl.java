package top.kangert.kspider.service.impl;

import top.kangert.kspider.dao.FunctionRepository;
import top.kangert.kspider.domain.Function;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.script.ScriptManager;
import top.kangert.kspider.service.BaseService;
import top.kangert.kspider.service.FunctionService;
import top.kangert.kspider.util.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
@Slf4j
public class FunctionServiceImpl extends BaseService implements FunctionService {

    @Resource
    private FunctionRepository functionRepository;

    /**
     * 初始化或者重置自定义函数
     */
    @PostConstruct
    private void initializeFunctions() {
        try {
            ScriptManager.lock();
            ScriptManager.clearFunctions();
            functionRepository.findAll().forEach(function -> ScriptManager.registerFunction(function.getName(),
                    function.getParameter(), function.getScript()));
        } finally {
            ScriptManager.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Function addItem(Map<String, Object> params) {
        // 检查参数
        checkParams(params, new String[] { "name", "parameter", "script" });

        try {
            // 实体转换
            Function func = transformEntity(params, Function.class);

            // 函数校验
            ScriptManager.validScript(func.getName(), func.getParameter(), func.getScript());

            // 添加
            Function tempFunc = functionRepository.save(func);

            // 重新加载自定义函数
            initializeFunctions();

            return tempFunc;
        } catch (Exception e) {
            throw new BaseException(ExceptionCodes.SCRIPT_VALID_ERROR);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Map<String, Object> params) {
        // 参数检查
        checkParams(params, new String[] { "funcId" });

        Long funcId = Long.parseLong((String) params.get("funcId"));

        functionRepository.deleteById(funcId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editItem(Map<String, Object> params) {

        // 参数检查
        checkParams(params, new String[] { "name", "parameter", "script" });

        try {
            // 实体转换
            Function func = transformEntity(params, Function.class);

            // 函数校验
            ScriptManager.validScript(func.getName(), func.getParameter(), func.getScript());

            // 修改
            functionRepository.save(func);

            // 重新加载自定义函数
            initializeFunctions();

        } catch (Exception e) {
            throw new BaseException(ExceptionCodes.SCRIPT_VALID_ERROR);
        }

    }

    @Override
    public Function queryItem(Long id) {

        if (id != null) {
            throw new BaseException(ExceptionCodes.ARGUMENTS_ERROR);
        }

        Function function = functionRepository.findFunctionByFuncId(id);

        return function;
    }

    @Override
    public PageInfo<Function> queryItems(Map<String, Object> params) {
        // 参数检查
        checkParams(params, null);

        // 处理分页
        Pageable pageable = processPage(params);

        // 查询数据
        Page<Function> page = functionRepository.findAll(multipleConditionsBuilder(params), pageable);

        return new PageInfo<Function>(page);
    }

}
