package top.kangert.kspider.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import top.kangert.kspider.domain.SpiderFlow;

import java.util.List;

public interface SpiderFlowRepository extends JpaRepository<SpiderFlow, Long>, JpaSpecificationExecutor<SpiderFlow> {

    List<SpiderFlow> findByFlowIdNot(Long flowId);
    
    SpiderFlow findSpiderFlowByFlowId(Long id);
}
