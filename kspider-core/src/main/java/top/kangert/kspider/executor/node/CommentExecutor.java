package top.kangert.kspider.executor.node;

import org.springframework.stereotype.Component;

import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 注释执行器
 */
@Component
public class CommentExecutor implements NodeExecutor {

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {

    }

    @Override
    public String supportType() {
        return "comment";
    }

    @Override
    public Shape shape() {
        Shape shape = new Shape();
        shape.setName(supportType());
        shape.setLabel("备注");
        shape.setIcon("ele-ChatDotSquare");
        shape.setDesc("无实际作用，仅仅用于解释说明");
        return shape;
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<>();
        ConfigItem remark = new ConfigItem("备注", ConfigItem.ComponentType.EL_INPUT, ConfigItem.DataType.STRING,
                "remark", "请输入备注信息", "", null, null);
        configItemList.add(remark);
        return configItemList;
    }

}
