package top.kangert.kspider.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.kangert.kspider.controller.BaseController;
import top.kangert.kspider.domain.SpiderTask;
import top.kangert.kspider.service.SpiderTaskService;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;

@RestController
@RequestMapping("/task")
public class SpiderTaskController extends BaseController {
    @Autowired
    private SpiderTaskService spiderTaskService;

    @PostMapping("/query")
    public BaseResponse queryTaskList(@RequestBody Map<String, Object> params) {
        PageInfo<SpiderTask> queryItem = spiderTaskService.queryItems(params);
        return successResponse(queryItem);
    }

    @PostMapping("/add")
    public BaseResponse addTask(@RequestBody Map<String, Object> params) {
        SpiderTask spiderTask = spiderTaskService.add(params);
        return successResponse(spiderTask);
    }

    @PostMapping("/edit")
    public BaseResponse editTask(@RequestBody Map<String, Object> params) {
        spiderTaskService.edit(params);
        return successResponse();
    }

    @PostMapping("/delete")
    public BaseResponse deleteTask(@RequestBody Map<String, Object> params) {
        spiderTaskService.deleteItem(params);
        return successResponse();
    }

    @PostMapping("/run")
    public BaseResponse run(@RequestBody Map<String, Object> params) {
        spiderTaskService.run(params);
        return successResponse();
    }

    @PostMapping("/stop")
    public BaseResponse stop(@RequestBody Map<String, Object> params) {
        spiderTaskService.stop(params);
        return successResponse();
    }

    @PostMapping("/download")
    public void download(@RequestBody Map<String, Object> params) {
        spiderTaskService.download(params, response);
    }
}
