-- 帖子表
-- @author: IceProgrammer
-- @github: https://github.com/Ice-Programmer
-- @createTime: 2025-01-18 21:44

-- 创建表
create database if not exists adam_post;

-- 使用表
use adam_post;

-- 帖子表
create table if not exists post
(
    id          bigint auto_increment primary key  not null comment 'id',
    user_id     bigint                             not null comment '创建用户',
    title       varchar(512)                       not null comment '标题',
    content     text                               null comment '内容',
    thumb_num   int      default 0                 not null comment '点赞数',
    favour_num  int      default 0                 not null comment '收藏数',
    comment_num int      default 0                 not null comment '评论数',
    address     varchar(128)                       null comment '发布地点',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_user_id (user_id)
) comment '帖子表' collate = utf8mb4_unicode_ci;

-- 帖子图片关联表
create table if not exists post_image
(
    id          bigint auto_increment primary key  not null comment 'id',
    user_id     bigint                             not null comment '创建用户',
    post_id     bigint                             not null comment '帖子id',
    image       varchar(256)                       not null comment '图片',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_user_id (user_id),
    index idx_post_id (post_id)
) comment '帖子图片表' collate = utf8mb4_unicode_ci;

-- 帖子标签表
create table if not exists tag
(
    id          bigint auto_increment primary key  not null comment 'id',
    user_id     bigint                             not null comment '创建用户',
    name        varchar(128)                       not null comment '标签名称',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '标签表' collate = utf8mb4_unicode_ci;

-- 帖子标签关联表
create table if not exists post_tag
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    tag_id      bigint                             not null comment '标签 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_post_id (post_id),
    index idx_tag_id (tag_id)
) comment '帖子标签关联表';

-- 帖子点赞表
create table if not exists post_thumb
(
    id          bigint auto_increment primary key  not null comment 'id',
    post_id     bigint                             not null comment '帖子id',
    user_id     bigint                             not null comment '创建id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_user_id (user_id),
    index idx_post_id (post_id)
) comment '帖子点赞表' collate = utf8mb4_unicode_ci;

-- 帖子收藏表
create table if not exists post_favour
(
    id          bigint auto_increment comment 'id' primary key,
    post_id     bigint                             not null comment '帖子 id',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_post_id (post_id),
    index idx_userId (user_id)
) comment '帖子收藏表';

-- 帖子评论表
create table if not exists post_comment
(
    id          bigint auto_increment primary key  not null comment '评论 id',
    post_id     bigint                             not null comment '帖子 id',
    user_id     bigint                             not null comment '评论用户 id',
    parent_id   bigint                             null comment '父评论 id，支持嵌套评论，顶级评论为 null',
    content     varchar(1024)                      not null comment '评论内容',
    create_time datetime default CURRENT_TIMESTAMP not null comment '评论时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    index idx_post_id (post_id),
    index idx_user_id (user_id),
    index idx_parent_id (parent_id)
) comment '帖子评论表' collate = utf8mb4_unicode_ci;
