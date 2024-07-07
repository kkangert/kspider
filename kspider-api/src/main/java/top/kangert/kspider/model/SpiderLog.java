package top.kangert.kspider.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.exceptions.ExceptionUtil;

/**
 * 爬虫日志
 */
@Getter
@Setter
public class SpiderLog {

    private String level;

    private String message;

    private List<Object> variables;

    public SpiderLog(String level, String message, List<Object> variables) {
        if (variables != null && variables.size() > 0) {
            List<Object> nVariables = new ArrayList<>(variables.size());
            for (Object object : variables) {
                if (object instanceof Throwable) {
                    nVariables.add(ExceptionUtil.getMessage((Throwable) object));
                } else {
                    nVariables.add(object);
                }
            }
            this.variables = nVariables;
        }
        this.level = level;
        this.message = message;
    }
}
