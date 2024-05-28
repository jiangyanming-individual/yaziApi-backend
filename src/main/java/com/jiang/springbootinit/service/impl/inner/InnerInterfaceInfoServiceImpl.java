package com.jiang.springbootinit.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiang.apicommon.model.entity.InterfaceInfo;
import com.jiang.apicommon.service.InnerInterfaceInfoService;
import com.jiang.springbootinit.common.ErrorCode;
import com.jiang.springbootinit.exception.BusinessException;
import com.jiang.springbootinit.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 查询接口是否存在
 */


@DubboService //表示是dubbo服务的提供者
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {

        if (StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求的url和method为空");
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<InterfaceInfo>();
        queryWrapper.eq("url",url);
        queryWrapper.eq("method",method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
