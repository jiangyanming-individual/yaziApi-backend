package com.jiang.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.jiang.springbootinit.annotation.AuthCheck;
import com.jiang.springbootinit.common.BaseResponse;
import com.jiang.springbootinit.common.DeleteRequest;
import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.common.ResultUtils;
import com.jiang.springbootinit.constant.UserConstant;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.exception.ThrowUtils;
import com.jiang.springbootinit.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.jiang.springbootinit.model.dto.interfaceinfo.InterfaceInfoQueryRequest;
import com.jiang.springbootinit.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.jiang.springbootinit.model.entity.InterfaceInfo;
import com.jiang.springbootinit.model.entity.User;
import com.jiang.springbootinit.model.vo.InterfaceInfoVO;
import com.jiang.springbootinit.service.InterfaceInfoService;
import com.jiang.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * api接口 controller
 *
 */
@RestController
@RequestMapping("/InterfaceInfo")
@Slf4j
public class InterfaceinfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查
    /**
     * 创建
     *
     * @param InterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest InterfaceInfoAddRequest, HttpServletRequest request) {
        if (InterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoAddRequest, InterfaceInfo);
        interfaceInfoService.validInterfaceInfo(InterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        InterfaceInfo.setUserId(loginUser.getId());
        boolean result = interfaceInfoService.save(InterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newInterfaceInfoId = InterfaceInfo.getId();
        return ResultUtils.success(newInterfaceInfoId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = interfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param InterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest InterfaceInfoUpdateRequest) {
        if (InterfaceInfoUpdateRequest == null || InterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoUpdateRequest, InterfaceInfo);
        // 参数校验
        interfaceInfoService.validInterfaceInfo(InterfaceInfo, false);
        long id = InterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = interfaceInfoService.updateById(InterfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<InterfaceInfoVO> getInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        InterfaceInfo InterfaceInfo = interfaceInfoService.getById(id);
        if (InterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVO(InterfaceInfo, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        //分页
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(interfaceInfoPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listMyInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
            HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        interfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = interfaceInfoQueryRequest.getCurrent();
        long size = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        //传入 current ,size,total
        Page<InterfaceInfo> InterfaceInfoPage = interfaceInfoService.page(new Page<>(current, size),
                interfaceInfoService.getQueryWrapper(interfaceInfoQueryRequest));
        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(InterfaceInfoPage, request));
    }


//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     * @param InterfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<InterfaceInfoVO>> searchInterfaceInfoVOByPage(@RequestBody InterfaceInfoQueryRequest InterfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long size = InterfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<InterfaceInfo> InterfaceInfoPage = interfaceInfoService.searchFromEs(InterfaceInfoQueryRequest);
//        return ResultUtils.success(interfaceInfoService.getInterfaceInfoVOPage(InterfaceInfoPage, request));
//    }

    /**
     * 编辑（用户）
     *
     * @param InterfaceInfoEditRequest
     * @param request
     * @return
     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editInterfaceInfo(@RequestBody InterfaceInfoEditRequest InterfaceInfoEditRequest, HttpServletRequest request) {
//        if (InterfaceInfoEditRequest == null || InterfaceInfoEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        InterfaceInfo InterfaceInfo = new InterfaceInfo();
//        BeanUtils.copyProperties(InterfaceInfoEditRequest, InterfaceInfo);
//        List<String> tags = InterfaceInfoEditRequest.getTags();
//        if (tags != null) {
//            InterfaceInfo.setTags(GSON.toJson(tags));
//        }
//        // 参数校验
//        interfaceInfoService.validInterfaceInfo(InterfaceInfo, false);
//        User loginUser = userService.getLoginUser(request);
//        long id = InterfaceInfoEditRequest.getId();
//        // 判断是否存在
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
//        ThrowUtils.throwIf(oldInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = interfaceInfoService.updateById(InterfaceInfo);
//        return ResultUtils.success(result);
//    }

}
