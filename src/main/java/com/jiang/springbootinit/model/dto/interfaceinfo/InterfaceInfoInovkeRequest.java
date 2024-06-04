package com.jiang.springbootinit.model.dto.interfaceinfo;

import lombok.Data;
import java.io.Serializable;

/**
 * 获取第一次的免费调用次数
 */
@Data
public class InterfaceInfoInovkeRequest implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     *
     * 请求参数
     */
    private String userRequestParams;

    private static final long serialVersionUID = 1L;

}
