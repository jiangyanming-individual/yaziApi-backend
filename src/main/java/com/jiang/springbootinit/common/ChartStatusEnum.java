package com.jiang.springbootinit.common;

/**
 * @author Lenovo
 * @date 2024/4/21
 * @time 20:30
 * @project springboot-init
 **/
public enum ChartStatusEnum {

    SUCCEED_STATUS("succeed"),
    WAIT_STATUS("wait"),
    RUNNING_STATUS("running"),
    FAILED_STATUS("failed");

    private String value;
    ChartStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
