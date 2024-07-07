package top.kangert.kspider.executor.node;

import top.kangert.kspider.KspiderRuntime;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.dao.SpiderFlowRepository;
import top.kangert.kspider.domain.SpiderFlow;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.service.SpiderFlowService;
import top.kangert.kspider.util.SpiderFlowUtils;
import top.kangert.kspider.websocket.WebSocketEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 子流程执行器
 */
@Component
@Slf4j
public class ProcessExecutor implements NodeExecutor {

    @Autowired
    private KspiderRuntime spider;

    @Autowired
    private SpiderFlowService spiderFlowService;

    @Autowired
    private SpiderFlowRepository spiderFlowRepository;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        Long flowId = Long.parseLong(node.getJsonProperty(Constants.FLOW_ID));
        SpiderFlow spiderFlow = spiderFlowService.getById(flowId);
        if (spiderFlow != null) {
            log.info("执行子流程：{}", spiderFlow.getName());
            context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, "spiderFlowName", spiderFlow.getName());
            SpiderNode root = SpiderFlowUtils.parseJsonToSpiderNode(spiderFlow.getJson());
            spider.executeNode(null, root, context, variables);
        } else {
            log.info("执行子流程：{} 失败，找不到该子流程", flowId);
            context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, "spiderFlowId", flowId);
        }
    }

    @Override
    public String supportType() {
        return "process";
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "子流程", "子流程", "iconfont icon-shouye_dongtaihui", "用于执行其他爬虫流程");
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<>();

        // 其他爬虫流程选择
        List<ConfigItem.SelectItem> spiderList = new ArrayList<>();
        List<SpiderFlow> spiderFlows = spiderFlowRepository.findAll();
        for (SpiderFlow spiderFlow : spiderFlows) {
            ConfigItem.SelectItem item = new ConfigItem.SelectItem(spiderFlow.getName(), spiderFlow.getFlowId(),
                    ConfigItem.DataType.INT);
            spiderList.add(item);
        }

        ConfigItem otherKspider = new ConfigItem("子流程", ConfigItem.ComponentType.EL_SELECT, ConfigItem.DataType.INT,
                Constants.FLOW_ID, "请选择其他爬虫流程", "", null, spiderList);
        configItemList.add(otherKspider);
        return configItemList;
    }

}
