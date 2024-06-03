package com.jiang.springbootinit.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiang.apicommon.model.entity.InterfaceInfo;
import com.jiang.apicommon.model.entity.UserInterfaceInfo;
import com.jiang.springbootinit.annotation.AuthCheck;
import com.jiang.springbootinit.common.BaseResponse;
import com.jiang.springbootinit.common.ResultUtils;
import com.jiang.springbootinit.mapper.UserInterfaceInfoMapper;
import com.jiang.springbootinit.model.vo.AnalysisInvokeVO;
import com.jiang.springbootinit.service.InterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnaysisInvokeController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    /**
     * 调用接口统计分析
     * @return
     */
    @GetMapping("/invoke/interfaceInfo")
    @AuthCheck(mustRole = "admin") //必须为管理员才能使用
    public BaseResponse<List<AnalysisInvokeVO>> getAnalysisInvoke() {

        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.getUserInterfaceInfoList(3);
        //分组
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream().collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",interfaceInfoIdObjMap.keySet());
        //查询所有符合条件的接口
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);

        //转换为Vo
        List<AnalysisInvokeVO> analysisInterfaceInfo = list.stream().map(interfaceInfo -> {
            AnalysisInvokeVO analysisInvokeVO = new AnalysisInvokeVO();
            BeanUtils.copyProperties(interfaceInfo, analysisInvokeVO);
            //设置totalNum
            int totalNum=interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            analysisInvokeVO.setTotalNum(totalNum);
            return analysisInvokeVO;
        }).collect(Collectors.toList()); //最后收集成列表；

        return ResultUtils.success(analysisInterfaceInfo);
    }
}
