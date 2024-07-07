package top.kangert.kspider.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.util.BaseResponse;
import top.kangert.kspider.util.PageInfo;
import top.kangert.kspider.vo.PageVo;

/**
 * 控制器的基类
 */
public class BaseController {
    @Autowired
    HttpServletRequest request;

    @Autowired
    HttpServletResponse response;

    /**
     * 操作成功
     * 
     * @return 成功响应
     */
    protected BaseResponse successResponse() {
        BaseResponse res = new BaseResponse();
        res.setCode(ExceptionCodes.SUCCESS.getCode());
        res.setMessage(ExceptionCodes.SUCCESS.getMessage());
        res.setData(null);
        res.setPageInfo(null);
        return res;
    }

    protected BaseResponse successResponse(Object data) {
        BaseResponse res = new BaseResponse();
        res.setCode(ExceptionCodes.SUCCESS.getCode());
        res.setMessage(ExceptionCodes.SUCCESS.getMessage());
        res.setData(data);
        res.setPageInfo(null);
        return res;
    }

    /**
     * 操作成功响应
     * 
     * @param pageInfo
     * @return
     */
    protected BaseResponse successResponse(PageInfo<?> pageInfo) {
        BaseResponse res = new BaseResponse();
        res.setCode(ExceptionCodes.SUCCESS.getCode());
        res.setMessage(ExceptionCodes.SUCCESS.getMessage());
        res.setData(pageInfo.getData());

        PageVo pageVo = new PageVo();
        pageVo.setCurrentPage(pageInfo.getCurrentPage());
        pageVo.setPageNums(pageInfo.getPageNums());
        pageVo.setPageSize(pageInfo.getPageSize());
        pageVo.setTotal(pageInfo.getTotal());
        res.setPageInfo(pageVo);
        return res;
    }
}
