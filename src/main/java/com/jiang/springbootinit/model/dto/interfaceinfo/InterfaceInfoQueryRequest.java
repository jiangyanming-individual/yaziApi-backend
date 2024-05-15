package com.jiang.springbootinit.model.dto.interfaceinfo;

import com.jiang.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

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
     * 请求参数
     */
    private String requestParams;
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


    private static final long serialVersionUID = 1L;
}