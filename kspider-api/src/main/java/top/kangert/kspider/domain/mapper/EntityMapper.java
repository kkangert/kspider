package top.kangert.kspider.domain.mapper;


import java.util.List;

/**
 * 实体映射接口
 *
 * @param <E> entity 类型
 * @param <D> dto 类型
 */
public interface EntityMapper<E, D> {

    /**
     * 将 entity 映射为 dto
     *
     * @param entity 类型
     * @return
     */
    D toDto(E entity);

    /**
     * 将 dto 映射为 entity
     *
     * @param dto
     * @return
     */
    E toEntity(D dto);

    /**
     * entity 结合映射为 dto 集合
     *
     * @param entityList
     * @return
     */
    List<D> toDto(List<E> entityList);

    /**
     * dto 集合映射为 entity 集合
     *
     * @param dtoList
     * @return
     */
    List<E> toEntity(List<D> dtoList);
}
