package top.kangert.kspider.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import cn.hutool.core.convert.Convert;
import top.kangert.kspider.entity.Application;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.repository.ApplicationRepository;
import top.kangert.kspider.service.ApplicationService;
import top.kangert.kspider.service.BaseService;
import top.kangert.kspider.util.PageInfo;

@Service
public class ApplicationServiceImpl extends BaseService implements ApplicationService{

    @Autowired
    private ApplicationRepository applicationRepository;

    @Override
    public void addItem(Map<String, Object> params) {
        checkParams(params, new String[]{"appName", "appSecretKey"});

        Application application = transformEntity(params, Application.class);

        Application appSecretKey = applicationRepository.findByAppSecretKey(application.getAppSecretKey());
        if (appSecretKey != null) {
            throw new BaseException(1, "密钥已存在");
        }
        try {
            applicationRepository.save(application);
        } catch (Exception e) {
            throw new BaseException(1, "添加失败"+e.getMessage());
        }
    }

    @Override
    public PageInfo<Application> queryItem(Map<String, Object> params) {
        checkParams(params, null);
        
        // 处理分页
        Pageable pageable = processPage(params);

        // 查询数据
        Page<Application> page = applicationRepository.findAll(multipleConditionsBuilder(params), pageable);

        return new PageInfo<Application>(page);
    }

    @Override
    public void editItem(Map<String, Object> params) {
        checkParams(params, new String[]{"appId"});

        Application application = transformEntity(params, Application.class);

        try {
            applicationRepository.save(application);
        } catch (Exception e) {
            throw new BaseException(1, "修改失败"+e.getMessage());
        }
    }

    @Override
    public void delete(Map<String, Object> param) {
        checkParams(param, new String[]{"appId"});
        Long appId = Convert.toLong(param.get("appId"));
        try {
            applicationRepository.deleteById(appId);
        } catch (Exception e) {
            throw new BaseException(1, "删除失败"+e.getMessage());
        }
    }


    
}
