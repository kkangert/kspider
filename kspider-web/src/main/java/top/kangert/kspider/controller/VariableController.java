package top.kangert.kspider.controller;

import top.kangert.kspider.controller.BaseController;
import top.kangert.kspider.domain.Variable;
import top.kangert.kspider.service.VariableService;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/variable")
public class VariableController extends BaseController {

    @Resource
    private VariableService variableService;

    @PostMapping("/add")
    public BaseResponse addVar(@RequestBody Map<String, Object> params) {
        variableService.addItem(params);
        return successResponse();
    }

    @PostMapping("/delete")
    public BaseResponse deleteVar(@RequestBody Map<String, Object> params) {
        variableService.deleteItem(params);
        return successResponse();
    }

    @PostMapping("/edit")
    public BaseResponse editVar(@RequestBody Map<String, Object> params) {
        variableService.editItem(params);
        return successResponse();
    }

    @PostMapping("/query")
    public BaseResponse queryVar(@RequestBody Map<String, Object> params) {
        PageInfo<Variable> pageInfo = variableService.queryItems(params);
        return successResponse(pageInfo);
    }

}
