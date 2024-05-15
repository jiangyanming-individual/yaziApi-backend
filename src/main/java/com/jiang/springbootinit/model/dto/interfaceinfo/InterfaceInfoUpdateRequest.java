package com.jiang.springbootinit.model.dto.interfaceinfo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 接口信息
     */
    private String name;


    /**
     * 请求参数
     */
    private String requestParams;

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
    private String responseHeader;

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
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}