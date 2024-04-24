package com.jiang.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lenovo
 * @date 2024/4/19
 * @time 14:26
 * @project springboot-init
 **/
@Data
public class BiGenResponse implements Serializable {


    private static final long serialVersionUID = 3794688012972941684L;
    /***
     * 新⽣成的图表id
     */
    private long chartId;

    /**
     * 图表信息
     */
    private String genChart;

    /**
     * 图表类型
     */
    private String genResult;

}
