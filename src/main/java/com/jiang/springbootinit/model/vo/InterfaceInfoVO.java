package com.jiang.springbootinit.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiang.springbootinit.model.entity.InterfaceInfo;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *
 */
@Data
public class InterfaceInfoVO implements Serializable {

    private final static Gson GSON = new Gson();
    /**
     * 主键
     */
    private Long id;

    /**
     * 接口信息
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 用户昵称
     */
    private String url;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应头
     */
    private String reponseHeader;

    /**
     * 接口状态（0-关闭 1-开启）
     */
    private Integer status;

    /**
     * 创建人
     */
    private Long userId;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param InterfaceInfoVO
     * @return
     */
    public static InterfaceInfo voToObj(InterfaceInfoVO InterfaceInfoVO) {
        if (InterfaceInfoVO == null) {
            return null;
        }
        InterfaceInfo InterfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(InterfaceInfoVO, InterfaceInfo);
        return InterfaceInfo;
    }

    /**
     * 对象转包装类
     * @param InterfaceInfo
     * @return
     */
    public static InterfaceInfoVO objToVo(InterfaceInfo InterfaceInfo) {
        if (InterfaceInfo == null) {
            return null;
        }
        InterfaceInfoVO InterfaceInfoVO = new InterfaceInfoVO();
        BeanUtils.copyProperties(InterfaceInfo, InterfaceInfoVO);
        return InterfaceInfoVO;
    }
}
