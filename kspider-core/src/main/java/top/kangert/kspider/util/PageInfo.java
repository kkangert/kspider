package top.kangert.kspider.util;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页对象实体
 * @param <T> 数据对象实体
 */
public class PageInfo<T> {
    /**
     * 一页的数据量
     */
    private int pageSize = 0;

    /**
     * 当前页
     */
    private int currentPage = 0;

    /**
     * 总共多少页
     */
    private int pageNums = 0;

    /**
     * 总数据量
     */
    private int total = 0;

    /**
     * 单页数据
     */
    private List<T> data;

    public PageInfo(Page<T> page) {
        setCurrentPage(page.getNumber() + 1);
        setPageSize(page.getSize());
        setPageNums(page.getTotalPages());
        setTotal((int) page.getTotalElements());
        setData(page.toList());
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageNums() {
        return pageNums;
    }

    public void setPageNums(int pageNums) {
        this.pageNums = pageNums;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}