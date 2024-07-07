package top.kangert.kspider.domain.mapper;

import top.kangert.kspider.domain.DataSource;
import top.kangert.kspider.domain.dto.DataSourceDTO;
import top.kangert.kspider.domain.mapper.EntityMapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 数据源mapper
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataSourceMapper extends EntityMapper<DataSource, DataSourceDTO> {

}