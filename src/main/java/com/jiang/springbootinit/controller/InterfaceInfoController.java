package com.jiang.springbootinit.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.jiang.apicommon.model.entity.InterfaceInfo;
import com.jiang.apicommon.model.entity.User;
import com.jiang.apicommon.model.entity.UserInterfaceInfo;
import com.jiang.springbootinit.annotation.AuthCheck;
import com.jiang.springbootinit.common.*;
import com.jiang.springbootinit.constant.UserConstant;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.exception.ThrowUtils;
import com.jiang.springbootinit.model.dto.interfaceinfo.*;
import com.jiang.springbootinit.model.enums.InterfaceInfoStatusEnum;
import com.jiang.springbootinit.model.vo.InterfaceInfoVO;
import com.jiang.springbootinit.model.vo.UserInterfaceInfoVO;
import com.jiang.springbootinit.service.InterfaceInfoService;
import com.jiang.springbootinit.service.UserInterfaceInfoService;
import com.jiang.springbootinit.service.UserService;
import com.jiang.yaziapiclientsdk.client.YaZiApiClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.optional.qual.Present;
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
public class InterfaceInfoController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private YaZiApiClient yaZiApiClient;

    private final static Gson GSON = new Gson();


    private final  static  int FREE_COUNT =10;

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
        InterfaceInfoVO interfaceInfoVO = interfaceInfoService.getInterfaceInfoVO(InterfaceInfo, request);
        return ResultUtils.success(interfaceInfoVO);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param interfaceInfoQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page/vo")
    public BaseResponse<Page<InterfaceInfoVO>> listInterfaceInfoVOByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest,
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


    /**
     * 上线接口
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) //校验权限
    public BaseResponse<Boolean> onlineInterfaceInfo(@RequestBody IdRequest idRequest,HttpServletRequest request) {
        //判断接口是否存在
        long id = idRequest.getId();
        if ( idRequest == null || id<= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求接口参数不合法");
        }
        // 判断是否存在

        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"接口不存在");
        }
        //测试接口是否可用：
        com.jiang.yaziapiclientsdk.model.User user = new com.jiang.yaziapiclientsdk.model.User();
        user.setUserName("jiangyanming");
        String username = yaZiApiClient.getUsernameByPost(user);
        //校验返回值：
        if (StringUtils.isBlank(username)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"调用接口失败");
        }
        //更新接口状态：
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 下线接口
     *
     * @param idRequest
     * @return
     */
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE) //自定义管理员注解
    public BaseResponse<Boolean> offlineInterfaceInfo(@RequestBody IdRequest idRequest) {
        //判断接口是否存在
        long id = idRequest.getId();
        if ( idRequest == null || id<= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求接口参数不合法");
        }
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"接口不存在");
        }
        //更新接口状态：
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(id);
        //设置接口为下线
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        return ResultUtils.success(result);
    }

    /**
     * 接口调用
     * @param interfaceInfoInovkeRequest
     * @return
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInovkeRequest interfaceInfoInovkeRequest,HttpServletRequest request) {
        //判断接口是否存在
        if (interfaceInfoInovkeRequest == null || interfaceInfoInovkeRequest.getId()<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求接口为空");
        }
        long id = interfaceInfoInovkeRequest.getId();
        String userRequestParams = interfaceInfoInovkeRequest.getUserRequestParams();
        // 判断是否存在
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(id);
        if (oldInterfaceInfo == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"接口不存在");
        }
        //校验接口是否可用
        if (oldInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"接口已关闭");
        }
        //获得ak 和sk
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        System.out.println("accessKey:"+accessKey);
        String secretKey = loginUser.getSecretKey();
        //引入客户端SDK 用于API签名认证，即对数据进行加密
        YaZiApiClient tempyaZiApiClient = new YaZiApiClient(accessKey,secretKey);
        Gson gson = new Gson();
        //调用接口
        com.jiang.yaziapiclientsdk.model.User user = gson.fromJson(userRequestParams, com.jiang.yaziapiclientsdk.model.User.class);
        String result = tempyaZiApiClient.getUsernameByPost(user);
//        System.out.println("接口调用结果result:" + result);
        //返回结果
        return ResultUtils.success(result);
    }

    /**
     * 给每个用户给每个接口申请免费10次调用机会
     * @param request
     * @return
     */
    @PostMapping("/getFreeInvokeCount")
    public BaseResponse<UserInterfaceInfoVO> getFreeInvokeCount(@RequestBody InterfaceInfoFreeInvokeRequest interfaceInfoFreeInvokeRequest, HttpServletRequest request) {
        //判断接口是否存在
        if (interfaceInfoFreeInvokeRequest == null || interfaceInfoFreeInvokeRequest.getId() <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数异常");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户未登录");
        }
        //判断接口是否存在；
        long interfaceInfoId = interfaceInfoFreeInvokeRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(interfaceInfoId);

        if (oldInterfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求接口不存在");
        }
        //查询是否已经获取过免费次数
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId",loginUser.getId());
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId",interfaceInfoId);
        //先判断是否已经免费获取，如果没有就直接插入；
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);

        UserInterfaceInfo newUserInterfaceInfo = new UserInterfaceInfo();
        UserInterfaceInfoVO userInterfaceInfoVO = new UserInterfaceInfoVO();
        if (userInterfaceInfo == null){
            newUserInterfaceInfo.setUserId(loginUser.getId());
            newUserInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            newUserInterfaceInfo.setLeftNum(FREE_COUNT); //免费10次；
            boolean result = userInterfaceInfoService.save(newUserInterfaceInfo);
            if (!result){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"插入调用免费次数失败");
            }
        }else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"已经免费获取过次数");
        }
        BeanUtils.copyProperties(newUserInterfaceInfo,userInterfaceInfoVO);
        return ResultUtils.success(userInterfaceInfoVO);
    }


    /**
     * 查询每个用户剩余次数
     * @param request
     * @return
     */
    @PostMapping("/getLeftInvokeCount")
    public BaseResponse<Integer> getLeftFreeInvokeCount(@RequestBody InterfaceInfoFreeInvokeRequest interfaceInfoFreeInvokeRequest, HttpServletRequest request) {
        //判断接口是否存在
        if (interfaceInfoFreeInvokeRequest == null || interfaceInfoFreeInvokeRequest.getId() <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数异常");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"用户未登录");
        }
        //判断接口是否存在；
        long interfaceInfoId = interfaceInfoFreeInvokeRequest.getId();
        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(interfaceInfoId);

        if (oldInterfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求接口不存在");
        }
        //查询是否已经获取过免费次数
        QueryWrapper<UserInterfaceInfo> userInterfaceInfoQueryWrapper = new QueryWrapper<>();
        userInterfaceInfoQueryWrapper.eq("userId",loginUser.getId());
        userInterfaceInfoQueryWrapper.eq("interfaceInfoId",interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(userInterfaceInfoQueryWrapper);

        if (userInterfaceInfo == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"还没有开通该接口");
        }
        return ResultUtils.success(userInterfaceInfo.getLeftNum());
    }

}
