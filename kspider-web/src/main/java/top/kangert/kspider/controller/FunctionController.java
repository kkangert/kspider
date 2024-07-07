package top.kangert.kspider.controller;

import top.kangert.kspider.controller.BaseController;
import top.kangert.kspider.domain.Function;
import top.kangert.kspider.service.FunctionService;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/function")
public class FunctionController extends BaseController {

    @Resource
    private FunctionService functionService;

    @PostMapping("/query")
    public BaseResponse quryFunc(@RequestBody Map<String, Object> params) {
        PageInfo<Function> pageInfo = functionService.queryItems(params);
        return successResponse(pageInfo);
    }

    @PostMapping("/add")
    public BaseResponse addFunc(@RequestBody Map<String, Object> params) {
        functionService.addItem(params);
        return successResponse();
    }

    @PostMapping("/edit")
    public BaseResponse editFunc(@RequestBody Map<String, Object> params) {
        functionService.editItem(params);
        return successResponse();
    }

    @PostMapping("/delete")
    public BaseResponse delFunc(@RequestBody Map<String, Object> params) {
        functionService.deleteItem(params);
        return successResponse();
    }
}
