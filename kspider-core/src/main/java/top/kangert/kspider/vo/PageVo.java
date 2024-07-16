package top.kangert.kspider.vo;

public class PageVo {
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
}
