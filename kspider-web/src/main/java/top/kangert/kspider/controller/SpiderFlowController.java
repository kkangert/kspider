package top.kangert.kspider.controller;

import top.kangert.kspider.controller.BaseController;
import top.kangert.kspider.domain.SpiderFlow;
import top.kangert.kspider.io.Line;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.service.SpiderFlowService;
import top.kangert.kspider.support.ExecutorFactory;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;
import top.kangert.kspider.vo.SpiderNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kspider")
@Slf4j
public class SpiderFlowController extends BaseController {

    @Autowired
    private SpiderFlowService spiderFlowService;

    @Autowired
    private ExecutorFactory executorFactory;

    @PostMapping(value = "/query")
    public BaseResponse querySpiderFlowList(@RequestBody Map<String, Object> params) {
        PageInfo<SpiderFlow> spiderFlowPage = spiderFlowService.queryItems(params);
        return successResponse(spiderFlowPage);
    }

    @GetMapping(value = "/flowJson/{flowId}")
    public BaseResponse querySpiderFlowJson(@PathVariable("flowId") Long flowId) {
        SpiderFlow spiderFlow = spiderFlowService.queryItem(flowId);
        return successResponse(spiderFlow);
    }

    @PostMapping("/add")
    public BaseResponse addSpiderFlow(@RequestBody Map<String, Object> params) {
        spiderFlowService.addItem(params);
        return successResponse();
    }

    @PostMapping("/edit")
    public BaseResponse editSpiderFlow(@RequestBody Map<String, Object> params) {
        spiderFlowService.editItem(params);
        return successResponse();
    }

    @PostMapping("/delete")
    public BaseResponse deleteSpiderFlow(@RequestBody Map<String, Object> params) {
        spiderFlowService.deleteItem(params);
        return successResponse();
    }

    @GetMapping("/log")
    public BaseResponse log(Long id, Long taskId, String keywords, Long index, Integer count,
            Boolean reversed, Boolean matchCase, Boolean regex) {
        List<Line> log = spiderFlowService.log(id, taskId, keywords, index, count, reversed, matchCase, regex);
        return successResponse(log);
    }

    @PostMapping("/nodeList")
    public BaseResponse shapes() {
        List<Shape> shapes = executorFactory.shapes();
        return successResponse(shapes);
    }

    @PostMapping("/nodeConfigItem")
    public BaseResponse nodeConfigItemList(@RequestBody Map<String, String> params) {
        Long flowId = Long.parseLong(params.get("flowId"));
        String nodeId = params.get("nodeId");
        String nodeName = params.get("nodeName");
        List<ConfigItem> configItems = Collections.emptyList();
        if (flowId != null && nodeId != null) {

            // 获取当前爬虫配置项
            configItems = executorFactory.configItemList(nodeName);

            if (configItems.size() <= 0) {
                return successResponse(configItems);
            }

            // 获取当前爬虫
            SpiderFlow spiderFlow = spiderFlowService.queryItem(flowId);

            if (StrUtil.isBlank(spiderFlow.getJson())) {
                return successResponse(configItems);
            }

            // 获取当前爬虫json数据
            List<SpiderNodeVo> nodeList = JSONUtil
                    .toList(JSONUtil.parseObj(spiderFlow.getJson()).get("nodeList").toString(), SpiderNodeVo.class);

            // 回填配置项的值
            for (ConfigItem configItem : configItems) {
                for (SpiderNodeVo spiderNodeVo : nodeList) {
                    if (StrUtil.equals(nodeId, spiderNodeVo.getNodeId())) {
                        List<ConfigItem> form = spiderNodeVo.getForm();
                        for (ConfigItem item : form) {
                            if (StrUtil.equals(configItem.getPropName(), item.getPropName())) {
                                configItem.setValue(ConfigItem.transform(item.getValue(), item.getDataType()));
                            }
                        }
                    }
                }
            }
        }
        return successResponse(configItems);
    }
}
