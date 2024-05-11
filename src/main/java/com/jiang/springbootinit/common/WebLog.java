package com.jiang.springbootinit.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Queue;

/**
 * 日志封装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebLog {

    private String description;
    private String username;
    private Long startTime;
    private Integer spendTime;
    private String basePath;
    private String uri;
    private String url;
    private String method;
    private String ip;
    private Object parameter;
    private Object result;



}
