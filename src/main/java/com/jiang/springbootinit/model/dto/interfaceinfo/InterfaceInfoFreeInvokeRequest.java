package com.jiang.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
/**
 * 获取第一次的免费调用次数请求
 */
@Data
public class InterfaceInfoFreeInvokeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;


    private static final long serialVersionUID = 1L;

}
