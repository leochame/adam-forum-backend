package com.adam.common.auth.aop;

import com.adam.common.auth.annotation.CheckRole;
import com.adam.common.auth.security.SecurityContext;
import com.adam.common.core.constant.ErrorCodeEnum;
import com.adam.common.core.enums.UserRoleEnum;
import com.adam.common.core.exception.BusinessException;
import com.adam.common.core.exception.ThrowUtils;
import com.adam.common.core.model.vo.UserBasicInfoVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;

/**
 * CheckRole 注解实现
 *
 * @author <a href="https://github.com/IceProgramer">chenjiahan</a>
 * @create 2024/12/21 10:18
 */
@Component
@Aspect
public class CheckRoleAspect {
    /**
     * 执行拦截
     **/
    @Around("@annotation(checkRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, CheckRole checkRole) throws Throwable {
        UserRoleEnum[] mustRoles = checkRole.mustRole();

        // 当前登录用户
        UserBasicInfoVO user = SecurityContext.getCurrentUser();
        ThrowUtils.throwIf(ObjectUtils.isEmpty(user), ErrorCodeEnum.NOT_LOGIN_ERROR);
        String userRole = user.getUserRole();

        // 必须有该权限才通过
        boolean hasPermission = false;
        for (UserRoleEnum mustRole : mustRoles) {
            if (mustRole.getValue().equals(userRole)) {
                hasPermission = true;
                break;
            }
        }
        if (!hasPermission) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("无权限，需要 ");
            Arrays.stream(mustRoles)
                    .forEach(userRoleEnum -> stringBuffer.append(userRoleEnum.getValue()).append(" "));
            stringBuffer.append("权限");
            throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR, stringBuffer.toString());
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }

}
