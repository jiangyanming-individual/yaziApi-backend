package com.jiang.springbootinit.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.constant.CommonConstant;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.jiang.springbootinit.model.entity.User;
import com.jiang.springbootinit.model.entity.UserInterfaceInfo;
import com.jiang.springbootinit.model.vo.UserInterfaceInfoVO;
import com.jiang.springbootinit.service.UserInterfaceInfoService;
import com.jiang.springbootinit.mapper.UserInterfaceInfoMapper;
import com.jiang.springbootinit.service.UserService;
import com.jiang.springbootinit.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jiangyanming
* @description 针对表【user_interface_info(yazi_Api.`interface_info`)】的数据库操作Service实现
* @createDate 2024-05-15 20:53:22
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService{

    private final static Gson GSON = new Gson();
    @Resource
    private UserService userService;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = userInterfaceInfo.getId();
        long userId = userInterfaceInfo.getUserId();
        long interfaceInfoId = userInterfaceInfo.getInterfaceInfoId();
        int totalNum = userInterfaceInfo.getTotalNum();
        int leftNum = userInterfaceInfo.getLeftNum();
        int status = userInterfaceInfo.getStatus();

        // 创建时，参数不能为空
        if (add) {
           if (interfaceInfoId <= 0 || userId <= 0 ) {
               throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户或者接口不存在");
           }
        }
        // 有参数则校验
        if (leftNum<0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用次数不能小于0");
        }

    }
    @Override
    public QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (userInterfaceInfoQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();
        long id = userInterfaceInfoQueryRequest.getId();
        long userId = userInterfaceInfoQueryRequest.getUserId();
        long interfaceInfoId = userInterfaceInfoQueryRequest.getInterfaceInfoId();
        int totalNum = userInterfaceInfoQueryRequest.getTotalNum();
        int leftNum = userInterfaceInfoQueryRequest.getLeftNum();
        // 拼接查询条件
        if (id <0 || userId <0 && interfaceInfoId <0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数id异常");
        }
        queryWrapper.like("id", id).or().
                like("userId", userId).or().
                like("interfaceInfoId",interfaceInfoId);
        queryWrapper.eq("totalNum", totalNum);
        queryWrapper.eq("leftNum", leftNum);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
    @Override
    public UserInterfaceInfoVO getUserInterfaceInfoVO(UserInterfaceInfo userInterfaceInfo, HttpServletRequest request) {
        UserInterfaceInfoVO userInterfaceInfoVO = UserInterfaceInfoVO.objToVo(userInterfaceInfo);
        return userInterfaceInfoVO;
    }

    @Override
    public Page<UserInterfaceInfoVO> getUserInterfaceInfoVOPage(Page<UserInterfaceInfo> userInterfaceInfoPage, HttpServletRequest request) {
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoPage.getRecords();
        Page<UserInterfaceInfoVO> userInterfaceInfoVOPage = new Page<>(userInterfaceInfoPage.getCurrent(), userInterfaceInfoPage.getSize(), userInterfaceInfoPage.getTotal());
        if (CollectionUtils.isEmpty(userInterfaceInfoList)) {
            return userInterfaceInfoVOPage;
        }
        // 1. 关联查询用户信息 Set<userId>
        Set<Long> userIdSet = userInterfaceInfoList.stream().map(UserInterfaceInfo::getUserId)
                .collect(Collectors.toSet());
        //得到一个HashMap <userId,List<User>>, 以userId进行分组
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        User loginUser = userService.getLoginUserPermitNull(request);
        // stream 流式计算：obj -> Vo
        List<UserInterfaceInfoVO> userInterfaceInfoVOList = userInterfaceInfoList.stream().map(userInterfaceInfo -> {
            UserInterfaceInfoVO userInterfaceInfoVO = UserInterfaceInfoVO.objToVo(userInterfaceInfo);
            return userInterfaceInfoVO;
        }).collect(Collectors.toList());
        userInterfaceInfoVOPage.setRecords(userInterfaceInfoVOList);
        return userInterfaceInfoVOPage;
    }

    /**
     * 统计调用接口的次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    @Override
    public Boolean invokeCount(long interfaceInfoId, long userId) {
        if (interfaceInfoId<=0 || userId<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口或者用户不存在");
        }
        UpdateWrapper<UserInterfaceInfo> userInterfaceInfoUpdateWrapper = new UpdateWrapper<>();
        userInterfaceInfoUpdateWrapper.eq("userId", userId);
        userInterfaceInfoUpdateWrapper.eq("interfaceInfoId", interfaceInfoId);
        //剩余调用次数还要 >1
        userInterfaceInfoUpdateWrapper.gt("leftNum", 0);
        userInterfaceInfoUpdateWrapper.setSql("leftNum=leftNum -1,totalNum=totalNum + 1");
        return this.update(userInterfaceInfoUpdateWrapper);
    }
}

