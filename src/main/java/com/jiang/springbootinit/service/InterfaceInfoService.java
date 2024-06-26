package com.jiang.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.apicommon.model.entity.InterfaceInfo;
import com.jiang.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.springbootinit.model.vo.InterfaceInfoVO;
import javax.servlet.http.HttpServletRequest;

/**
* @author jiangyanming
* @description 针对表【interface_info(yazi_Api.`interface_info`)】的数据库操作Service
* @createDate 2024-04-29 13:47:39
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
    /**
     * 校验
     *
     * @param interfaceInfo
     * @param add
     */
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param interfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest interfaceInfoQueryRequest);


    /**
     * 获取接口封装
     *
     * @param interfaceInfo
     * @param request
     * @return
     */
    InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo, HttpServletRequest request);

    /**
     * 分页获取接口封装
     *
     * @param interfaceInfoPage
     * @param request
     * @return
     */
    Page<InterfaceInfoVO> getInterfaceInfoVOPage(Page<InterfaceInfo> interfaceInfoPage, HttpServletRequest request);

}
