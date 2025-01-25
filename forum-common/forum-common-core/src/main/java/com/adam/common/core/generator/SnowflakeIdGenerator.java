package com.adam.common.core.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 雪花算法生成器（单例模式）
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2025/1/25 12:01
 */
public class SnowflakeIdGenerator {

    // 使用一个静态变量来持有唯一实例
    private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator();

    // Snowflake ID 生成器实例
    private static final long workerId = 1; // 工作节点 ID
    private static final long datacenterId = 1; // 数据中心 ID
    private final Snowflake snowflake;

    // 私有构造函数，防止外部实例化
    private SnowflakeIdGenerator() {
        this.snowflake = IdUtil.getSnowflake(workerId, datacenterId);
    }

    // 提供一个公共方法获取唯一实例
    public static SnowflakeIdGenerator getInstance() {
        return INSTANCE;
    }

    // 生成唯一 ID 的方法
    public long nextId() {
        return snowflake.nextId();
    }
}