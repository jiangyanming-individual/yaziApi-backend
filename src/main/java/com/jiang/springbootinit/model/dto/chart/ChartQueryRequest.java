package com.jiang.springbootinit.model.dto.chart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.jiang.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图表的名称
     */
    private String chartName;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 创建图标用户id
     */
    private Long userId;
    /**
     * 图表类型
     */
    private String chartType;


    private static final long serialVersionUID = 1L;


}