package top.kangert.kspider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

// 取消 UserDetailsServiceAutoConfiguration 自动配置
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class, scanBasePackages = "top.kangert.*")
@EnableScheduling
@EnableJpaAuditing
public class KspiderApplication {

    public static void main(String[] args) {
        SpringApplication.run(KspiderApplication.class, args);
    }

}
