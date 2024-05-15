# 数据库初始化

-- 创建库
create database if not exists yazi_Api;

-- 切换库
use yazi_Api;

-- 用户表
-- yaziApi.`user`
create table if not exists yazi_Api.`user`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userAccount` varchar(256) not null comment '账号',
    `userPassword` varchar(512) not null comment '密码',
    `userName` varchar(256)  null comment '用户昵称',
    `accessKey` varchar(512) null comment 'accessKey',
    `secretKey` varchar(512) null comment 'accessKey',
    `userAvatar` varchar(1024)  null comment '用户头像',
    `userRole` varchar(256) default 'user' not null comment '用户角色：user/admin',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment 'yaziApi.`user`' collate = utf8mb4_unicode_ci;


-- 接口信息
-- yazi_Api.`interface_info`
create table if not exists yazi_Api.`interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `name` varchar(256) not null comment '接口信息',
    `description` varchar(256) not null comment '描述',
    `url` varchar(512) not null comment '用户昵称',
    `requestParams` text not null comment '请求参数',
    `requestHeader` text not null comment '请求头',
    `reponseHeader` text not null comment '响应头',
    `status` int default 0 not null comment '接口状态（0-关闭 1-开启）',
    `userid` bigint not null comment '创建人',
    `method` varchar(256) not null comment '请求类型',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment 'yazi_Api.`interface_info`';




insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('严瑞霖', '孔修洁', 'www.signe-kling.info', 'ubQ', 'Vz', 6, '31Y');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('蒋博涛', '潘浩然', 'www.lera-lehner.info', 'Hiu', 'QEZs', 287015996, 'mok');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('莫聪健', '梁文昊', 'www.herschel-bode.com', 'qPF', '9Fi', 3244680751, 'zj');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('薛健柏', '戴思淼', 'www.matilde-huels.com', 'Whe', 'ur3H', 6043, '9rB');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('许鑫鹏', '段文', 'www.bernetta-predovic.info', 'XUF0', 'IvDn3', 1238, '0aRxT');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('贺弘文', '王文博', 'www.rafael-walter.biz', 'XIVHY', '1F', 3, 'QD');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('彭子涵', '邱昊天', 'www.isabel-larkin.co', 'UhZal', '0z', 444201419, 'jVXe');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('黎弘文', '史烨华', 'www.lamont-blanda.biz', 'tP1', 'eb', 61843007, 'ui');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('于睿渊', '侯乐驹', 'www.senaida-dare.biz', '20vg8', 'r1Q9', 6298, '0m3');
insert into yazi_Api.`interface_info` (`name`, `description`, `url`, `requestHeader`, `reponseHeader`, `userid`, `method`) values ('段鸿煊', '孟子涵', 'www.zoila-casper.net', 'LL', '0u', 46273, 'b0');


create table if not exists yazi_Api.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '接口调用者',
    `interfaceInfoId` bigint not null comment '接口调用者',
    `totalNum` int default 0 not null comment '总的调用次数',
    `leftlNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '接口调用状态（0-关闭 1-开启）',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment 'yazi_Api.`interface_info`';

