package top.kangert.kspider.service;

import java.util.Map;

import top.kangert.kspider.entity.Application;
import top.kangert.kspider.util.PageInfo;

public interface ApplicationService {

    void addItem(Map<String, Object> params);

    PageInfo<Application> queryItem(Map<String, Object> params);

    void editItem(Map<String, Object> params);

    void delete(Map<String, Object> param);
    
}
