package top.kangert.kspider.service;

import lombok.extern.slf4j.Slf4j;
import top.kangert.kspider.entity.BaseEntity;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.hutool.core.bean.BeanUtil;

import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseService {

    /**
     * 处理分页器
     * 
     * @param params 请求参数
     * @return 分页对象
     */
    protected Pageable processPage(Map<String, Object> params) {
        Integer currentPage = (Integer) params.get("currentPage");
        Integer pageSize = (Integer) params.get("pageSize");
        return PageRequest.of(currentPage != null ? currentPage - 1 : 1, pageSize != null ? pageSize : 5);
    }

    /**
     * 多条件查询构造器（默认AND多条件）
     * 
     * @param <T> 构造该对象的多条件查询
     * @param obj 该对象
     * @return 对于的多条件构造器
     */
    protected <T extends Object> Specification<T> multipleConditionsBuilder(T obj) {
        return multipleConditionsBuilder(obj, false);
    }

    /**
     * 多条件查询构造器
     * 
     * @param <T> 构造该对象的多条件查询
     * @param obj 该对象
     * @return 对于的多条件构造器
     */
    protected <T extends Object> Specification<T> multipleConditionsBuilder(T obj, Boolean isOr) {
        // 获取类
        Class<?> clazz = obj.getClass();

        Specification<T> specification = (Specification<T>) (root, query,
                criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();

            // 获取该对象所有字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Object value;
                try {
                    value = field.get(obj);

                    if (!("currentPage").equals(field.getName()) && !("pageSize").equals(field.getName())) {
                        if (null == value) {
                            continue;
                        }

                        if (value instanceof String) {
                            Predicate p1 = criteriaBuilder.like(root.get(field.getName()), "%" + field.get(obj) + "%");
                            list.add(p1);
                        } else {
                            Predicate p1 = criteriaBuilder.equal(root.get(field.getName()), field.get(obj));
                            list.add(p1);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
            if (isOr) {
                return criteriaBuilder.or(list.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
        return specification;

    }

    /**
     * 多条件查询构造器（默认AND多条件）
     * 
     * @param <T>    构造该对象的多条件查询
     * @param params 查询参数
     * @return 对于的多条件构造器
     */
    protected <T extends Object> Specification<T> multipleConditionsBuilder(Map<String, Object> params) {
        return multipleConditionsBuilder(params, false);
    }

    /**
     * 多条件查询构造器
     * 
     * @param <T>    构造该对象的多条件查询
     * @param params 查询参数
     * @param isOr   是否使用OR语句
     * @return 对于的多条件构造器
     */
    protected <T extends Object> Specification<T> multipleConditionsBuilder(Map<String, Object> params, Boolean isOr) {
        // 判空
        if (null == params)
            throw new BaseException(ExceptionCodes.ERROR);

        // 创建查询条件
        Specification<T> specification = (Specification<T>) (root, query,
                criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();

            params.entrySet().stream().forEach(item -> {
                Object value = params.get(item.getKey());
                if (!("currentPage").equals(item.getKey()) && !("pageSize").equals(item.getKey())) {
                    if (null != value) {
                        if (value instanceof String) {
                            Predicate p1 = criteriaBuilder.like(root.get(item.getKey()), "%" + value + "%");
                            list.add(p1);
                        } else {
                            Predicate p1 = criteriaBuilder.equal(root.get(item.getKey()), value);
                            list.add(p1);
                        }
                    }
                }
            });

            if (isOr) {
                return criteriaBuilder.or(list.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };

        return specification;
    }

    /**
     * 参数校验
     * 
     * @param params 参数map
     * @param keys   参数key数组
     * @throws BaseException 基础异常信息
     */
    protected void checkParams(Map<String, Object> params, String[] keys) {

        if (keys != null) {
            Boolean isError = false;
            StringBuffer stringBuffer = new StringBuffer();
            for (String key : keys) {
                if (!params.containsKey(key)) {
                    isError = true;
                    stringBuffer.append(key + "、");
                }
            }

            // 删除最后一个符号
            if (stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }

            if (isError) {
                throw new BaseException(ExceptionCodes.ERROR, "指定参数不存在: " + stringBuffer.toString() + ", 请检查!");
            }
        }
    }

    /**
     * Map参数转内部实体对象
     * 
     * @param <T>    内部实体对象
     * @param params Map参数
     * @param clazz  实体对象字节码
     * @return 转换后的实体对象
     */
    protected <T extends BaseEntity> T transformEntity(Map<String, Object> params, Class<T> clazz) {
        T anyObj = null;
        try {
            anyObj = BeanUtil.toBean(params, clazz);
        } catch (Exception e) {
            throw new BaseException(ExceptionCodes.ENTITY_TRANFORM_ERROR);
        }
        return anyObj;
    }

    /**
     * 对象值拷贝
     * 
     * @param source
     * @param target
     */
    protected void copyProperties(Map<String, Object> source, Object target) {
        BeanUtil.copyProperties(source, target, new String[] { "createTime", "updateTime" });
    }
}