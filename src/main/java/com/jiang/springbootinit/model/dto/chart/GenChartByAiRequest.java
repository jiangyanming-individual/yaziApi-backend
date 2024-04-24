package com.jiang.springbootinit.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Lenovo
 * @date 2024/4/18
 * @time 20:31
 * @project springboot-init
 **/
@Data
public class GenChartByAiRequest implements Serializable {

    private static final long serialVersionUID = 2345395149646652616L;
    /**
     * 图表的名称
     */
    private String chartName;
    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;


}
