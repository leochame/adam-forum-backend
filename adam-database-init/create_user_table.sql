-- 用户表
-- @author: IceProgrammer
-- @github: https://github.com/Ice-Programmer
-- @createTime: 2024-12-12 20:30

-- 创建表
create database if not exists adam_user;

-- 使用表
use adam_user;

-- 用户表
create table if not exists user
(
    id          bigint primary key                     not null comment 'id',
    username    varchar(256)                           not null comment '用户昵称',
    account     varchar(256)                           not null comment '账号',
    password    varchar(512)                           not null comment '密码',
    gender      tinyint      default 0                 not null comment '性别 0 - 女｜1 - 男',
    open_id     varchar(256)                           null comment '公众号openId',
    user_avatar varchar(1024)                          null comment '用户头像',
    phone       varchar(256)                           null comment '电话号码',
    email       varchar(512)                           null comment '邮箱',
    user_role   varchar(256) default 'user'            not null comment 'user admin ban',
    slogan      varchar(1024)                          null comment '个性签名',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;