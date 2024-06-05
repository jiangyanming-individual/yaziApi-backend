package com.jiang.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户模糊查询
 */
@Data
public class UserSearchRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;


    private static final long serialVersionUID = 1L;
}
