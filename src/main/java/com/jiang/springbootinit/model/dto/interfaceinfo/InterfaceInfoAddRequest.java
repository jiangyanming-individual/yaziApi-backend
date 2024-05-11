package com.jiang.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名字
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
    private String responseHeader;

    /**
     * 请求类型
     */
    private String method;

    private static final long serialVersionUID = 1L;
}