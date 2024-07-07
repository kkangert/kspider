package top.kangert.kspider.listener;

import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.listener.SpiderListener;
import top.kangert.kspider.util.SeleniumResponseHolder;

import org.springframework.stereotype.Component;

@Component
public class SeleniumListener implements SpiderListener {

    @Override
    public void beforeStart(SpiderContext context) {

    }

    @Override
    public void afterEnd(SpiderContext context) {
        SeleniumResponseHolder.clear(context);
    }

}
