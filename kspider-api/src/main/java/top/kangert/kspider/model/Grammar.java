package top.kangert.kspider.model;

import lombok.Getter;
import lombok.Setter;
import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.annotation.Return;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于前端语法提示
 */
@Getter
@Setter
public class Grammar {

    /**
     * 所属类（Java类）名称
     */
    private String owner;

    /**
     * 所属类（Java类）的方法名称
     */
    private String method;

    /**
     * 描述信息
     */
    private String comment;

    /**
     * 使用示例
     */
    private String example;

    /**
     * 函数变量名称
     */
    private String function;

    /**
     * 函数返回值类型
     */
    private List<String> returns;

    /***
     * 查找前端语法提示对象
     * 
     * @param clazz      所属Java类
     * @param function   函数变量名称
     * @param owner      所属类（Java类）名称
     * @param mustStatic 是否必须为静态类型
     * @return 前端语法提示对象列表
     */
    public static List<Grammar> findGrammars(Class<?> clazz, String function, String owner, boolean mustStatic) {
        Method[] methods = clazz.getDeclaredMethods();
        List<Grammar> grammars = new ArrayList<>();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && (Modifier.isStatic(method.getModifiers()) || !mustStatic)) {
                Grammar grammar = new Grammar();
                grammar.setMethod(method.getName());
                Comment comment = method.getAnnotation(Comment.class);
                if (comment != null) {
                    grammar.setComment(comment.value());
                }
                Example example = method.getAnnotation(Example.class);
                if (example != null) {
                    grammar.setExample(example.value());
                }
                Return returns = method.getAnnotation(Return.class);
                if (returns != null) {
                    Class<?>[] classes = returns.value();
                    List<String> returnTypes = new ArrayList<>();
                    for (int i = 0; i < classes.length; i++) {
                        returnTypes.add(classes[i].getSimpleName());
                    }
                    grammar.setReturns(returnTypes);
                } else {
                    grammar.setReturns(Collections.singletonList(method.getReturnType().getSimpleName()));
                }
                grammar.setFunction(function);
                grammar.setOwner(owner);
                grammars.add(grammar);
            }
        }
        return grammars;
    }
}
