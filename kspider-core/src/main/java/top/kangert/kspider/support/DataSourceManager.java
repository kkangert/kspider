package top.kangert.kspider.support;

import com.zaxxer.hikari.HikariDataSource;

import top.kangert.kspider.domain.dto.DataSourceDTO;
import top.kangert.kspider.service.DataSourceService;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源管理
 */
@Component
public class DataSourceManager {

    private Map<Long, DataSource> datasourceMap = new ConcurrentHashMap<>();

    @Resource
    private DataSourceService dataSourceService;

    public DataSource createDataSource(String className, String url, String username, String password) {
        HikariDataSource datasource = new HikariDataSource();
        datasource.setDriverClassName(className);
        datasource.setJdbcUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setAutoCommit(true);
        datasource.setMinimumIdle(1);
        return datasource;
    }

    public void remove(Long dataSourceId) {
        DataSource dataSource = datasourceMap.get(dataSourceId);
        if (dataSource != null) {
            HikariDataSource ds = (HikariDataSource) dataSource;
            ds.close();
            datasourceMap.remove(dataSourceId);
        }
    }

    public DataSource getDataSource(Long dataSourceId) {
        DataSource dataSource = datasourceMap.get(dataSourceId);
        if (dataSource == null) {
            DataSourceDTO ds = dataSourceService.getById(dataSourceId);
            if (ds != null) {
                dataSource = createDataSource(ds.getDriverClassName(), ds.getJdbcUrl(), ds.getUsername(), ds.getPassword());
                datasourceMap.put(dataSourceId, dataSource);
            }
        }
        return dataSource;
    }
}
