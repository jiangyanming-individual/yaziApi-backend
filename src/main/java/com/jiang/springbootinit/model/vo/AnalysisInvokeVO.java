package com.jiang.springbootinit.model.vo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jiang.apicommon.model.entity.InterfaceInfo;
import com.jiang.springbootinit.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 接口调用分析VO
 *
 */
@Data
public class AnalysisInvokeVO extends InterfaceInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 统计调用接口次数
     */
    private Integer totalNum;

}
