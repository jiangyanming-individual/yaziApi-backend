package com.jiang.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户模糊查询
 */
@Data
public class UserSearchRequest implements Serializable {



    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;




    private static final long serialVersionUID = 1L;
}
