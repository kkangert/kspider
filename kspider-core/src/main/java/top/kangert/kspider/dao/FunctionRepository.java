package top.kangert.kspider.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import top.kangert.kspider.domain.Function;

public interface FunctionRepository extends JpaRepository<Function, Long>, JpaSpecificationExecutor<Function> {

    /**
     * 通过ID查找函数对象
     * 
     * @param id 函数ID
     * @return 函数对象
     */
    Function findFunctionByFuncId(Long id);
}
