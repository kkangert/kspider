package top.kangert.kspider.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import top.kangert.kspider.domain.Variable;

public interface VariableRepository extends JpaRepository<Variable, Long>, JpaSpecificationExecutor<Variable> {

    Variable findByName(String variableName);

    /**
     * 通过全局变量ID查询对象
     * 
     * @param varId 全局变量ID
     * @return 实体对象
     */
    Variable findVariableByVarId(Long varId);
}
