package com.jiang.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.jiang.springbootinit.annotation.AuthCheck;
import com.jiang.springbootinit.common.*;
import com.jiang.springbootinit.constant.UserConstant;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.exception.ThrowUtils;
import com.jiang.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.jiang.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.jiang.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.jiang.springbootinit.model.entity.UserInterfaceInfo;
import com.jiang.springbootinit.model.entity.User;
import com.jiang.springbootinit.model.vo.UserInterfaceInfoVO;
import com.jiang.springbootinit.service.UserInterfaceInfoService;
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
@RequestMapping("/UserInterfaceInfo")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();
    // region 增删改查
    /**
     * 创建
     *
     * @param UserInterfaceInfoAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest UserInterfaceInfoAddRequest, HttpServletRequest request) {
        if (UserInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo UserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(UserInterfaceInfoAddRequest, UserInterfaceInfo);
        userInterfaceInfoService.validUserInterfaceInfo(UserInterfaceInfo, true);
        User loginUser = userService.getLoginUser(request);
        UserInterfaceInfo.setUserId(loginUser.getId());
        boolean result = userInterfaceInfoService.save(UserInterfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newUserInterfaceInfoId = UserInterfaceInfo.getId();
        return ResultUtils.success(newUserInterfaceInfoId);
    }
    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserInterfaceInfo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldUserInterfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = userInterfaceInfoService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param UserInterfaceInfoUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterfaceInfo(@RequestBody UserInterfaceInfoUpdateRequest UserInterfaceInfoUpdateRequest) {
        if (UserInterfaceInfoUpdateRequest == null || UserInterfaceInfoUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo UserInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(UserInterfaceInfoUpdateRequest, UserInterfaceInfo);
        // 参数校验
        userInterfaceInfoService.validUserInterfaceInfo(UserInterfaceInfo, false);
        long id = UserInterfaceInfoUpdateRequest.getId();
        // 判断是否存在
        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = userInterfaceInfoService.updateById(UserInterfaceInfo);
        return ResultUtils.success(result);
    }
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UserInterfaceInfoVO> getUserInterfaceInfoVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserInterfaceInfo UserInterfaceInfo = userInterfaceInfoService.getById(id);
        if (UserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        UserInterfaceInfoVO userInterfaceInfoVO = userInterfaceInfoService.getUserInterfaceInfoVO(UserInterfaceInfo, request);
        return ResultUtils.success(userInterfaceInfoVO);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfoVO>> listUserInterfaceInfoVOByPage(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
                                                                                 HttpServletRequest request) {
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        //分页
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(userInterfaceInfoPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfoVO>> listMyUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest,
            HttpServletRequest request) {
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        userInterfaceInfoQueryRequest.setUserId(loginUser.getId());
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        //传入 current ,size,total
        Page<UserInterfaceInfo> UserInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size),
                userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));
        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(UserInterfaceInfoPage, request));
    }

//    /**
//     * 分页搜索（从 ES 查询，封装类）
//     * @param UserInterfaceInfoQueryRequest
//     * @param request
//     * @return
//     */
//    @PostMapping("/search/page/vo")
//    public BaseResponse<Page<UserInterfaceInfoVO>> searchUserInterfaceInfoVOByPage(@RequestBody UserInterfaceInfoQueryRequest UserInterfaceInfoQueryRequest,
//            HttpServletRequest request) {
//        long size = UserInterfaceInfoQueryRequest.getPageSize();
//        // 限制爬虫
//        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
//        Page<UserInterfaceInfo> UserInterfaceInfoPage = userInterfaceInfoService.searchFromEs(UserInterfaceInfoQueryRequest);
//        return ResultUtils.success(userInterfaceInfoService.getUserInterfaceInfoVOPage(UserInterfaceInfoPage, request));
//    }

    /**
     * 编辑（用户）
     *
     * @param UserInterfaceInfoEditRequest
     * @param request
     * @return
     */
//    @PostMapping("/edit")
//    public BaseResponse<Boolean> editUserInterfaceInfo(@RequestBody UserInterfaceInfoEditRequest UserInterfaceInfoEditRequest, HttpServletRequest request) {
//        if (UserInterfaceInfoEditRequest == null || UserInterfaceInfoEditRequest.getId() <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        UserInterfaceInfo UserInterfaceInfo = new UserInterfaceInfo();
//        BeanUtils.copyProperties(UserInterfaceInfoEditRequest, UserInterfaceInfo);
//        List<String> tags = UserInterfaceInfoEditRequest.getTags();
//        if (tags != null) {
//            UserInterfaceInfo.setTags(GSON.toJson(tags));
//        }
//        // 参数校验
//        userInterfaceInfoService.validUserInterfaceInfo(UserInterfaceInfo, false);
//        User loginUser = userService.getLoginUser(request);
//        long id = UserInterfaceInfoEditRequest.getId();
//        // 判断是否存在
//        UserInterfaceInfo oldUserInterfaceInfo = userInterfaceInfoService.getById(id);
//        ThrowUtils.throwIf(oldUserInterfaceInfo == null, ErrorCode.NOT_FOUND_ERROR);
//        // 仅本人或管理员可编辑
//        if (!oldUserInterfaceInfo.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
//        boolean result = userInterfaceInfoService.updateById(UserInterfaceInfo);
//        return ResultUtils.success(result);
//    }

}
