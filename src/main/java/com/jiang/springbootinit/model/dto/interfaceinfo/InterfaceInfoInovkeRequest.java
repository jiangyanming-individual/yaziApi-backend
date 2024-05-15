package com.jiang.springbootinit.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
@Data
public class InterfaceInfoInovkeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 请求参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;

}
