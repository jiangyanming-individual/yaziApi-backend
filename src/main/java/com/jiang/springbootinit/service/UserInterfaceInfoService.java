package com.jiang.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiang.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.jiang.springbootinit.model.entity.UserInterfaceInfo;
import com.jiang.springbootinit.model.entity.UserInterfaceInfo;
import com.jiang.springbootinit.model.vo.UserInterfaceInfoVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author jiangyanming
* @description 针对表【user_interface_info(yazi_Api.`interface_info`)】的数据库操作Service
* @createDate 2024-05-15 20:53:22
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {


    /**
     * 校验
     *
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 获取查询条件
     *
     * @param userInterfaceInfoQueryRequest
     * @return
     */
    QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest);


    /**
     * 获取接口封装
     *
     * @param userInterfaceInfo
     * @param request
     * @return
     */
    UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo, HttpServletRequest request);

    /**
     * 分页获取接口封装
     *
     * @param interfaceInfoPage
     * @param request
     * @return
     */
    Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> interfaceInfoPage, HttpServletRequest request);



    Boolean invokeCount(long interfaceInfoId, long userId);
}
