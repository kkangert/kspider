package top.kangert.kspider.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.kangert.kspider.entity.Application;
import top.kangert.kspider.service.ApplicationService;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;

@RestController
@RequestMapping("/app")
public class ApplicationController extends BaseController{

    @Autowired
    private ApplicationService applicationService;
    
    @PostMapping("/add")
    public BaseResponse addApp(@RequestBody Map<String, Object> params) {
        applicationService.addItem(params);
        return successResponse();
    }

    @PostMapping("/query")
    public BaseResponse queryApp(@RequestBody Map<String, Object> params) {
        PageInfo<Application> queryItem = applicationService.queryItem(params);
        return successResponse(queryItem);
    }

    @PostMapping("/edit")
    public BaseResponse editApp(@RequestBody Map<String, Object> params) {
        applicationService.editItem(params);
        return successResponse();
    }

    @PostMapping("/delete")
    public BaseResponse deleteApp(@RequestBody Map<String, Object> param) {
        applicationService.delete(param);
        return successResponse();
    }
}
