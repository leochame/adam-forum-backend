package com.adam.common.auth.annotation;

import com.adam.common.core.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/21 10:00
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {

    /**
     * 需要检验的权限（多选为满足一个即可）
     *
     * @return 权限
     */
    UserRoleEnum[] mustRole() default {};
}
