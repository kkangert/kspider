package top.kangert.kspider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import top.kangert.kspider.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long>, JpaSpecificationExecutor<Application>{

    Application findByAppName(String AppName);

    Application findByAppSecretKey(String appSecretKey);
    
}
