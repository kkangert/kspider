package top.kangert.kspider.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import top.kangert.kspider.entity.User;

// @Repository
public interface UserRepository extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User>{
     User findByUsername(String username);
}
