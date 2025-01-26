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
    slogan      varchar(1024)                          null comment '个性签名',
    profile     text                                   null comment '个人介绍',
    gender      tinyint      default 0                 not null comment '性别 0 - 女｜1 - 男',
    open_id     varchar(256)                           null comment '公众号openId',
    user_avatar varchar(1024)                          null comment '用户头像',
    phone       varchar(256)                           null comment '电话号码',
    email       varchar(512)                           null comment '邮箱',
    user_role   varchar(256) default 'user'            not null comment 'user admin ban',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- 用户关注表
create table if not exists user_follow
(
    id          bigint primary key                 not null comment 'id',
    followed_id bigint                             not null comment '被关注者 id',
    user_id     bigint                             not null comment '关注者 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_followed_id (followed_id),
    index idx_user_id (user_id)
) comment '用户关注表' collate = utf8mb4_unicode_ci;