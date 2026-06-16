package com.deepsleep.aspect;

import com.deepsleep.annotation.RequireRole;
import com.deepsleep.context.UserContext;
import com.deepsleep.data.enums.ResultCode;
import com.deepsleep.data.enums.RoleEnum;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.util.JwtUtil;
import com.deepsleep.util.RedisUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Around("@annotation(com.deepsleep.annotation.RequireLogin) || " +
            "@annotation(com.deepsleep.annotation.RequireRole)")
    public Object auth(ProceedingJoinPoint pjp) throws Throwable{
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();

        String header = request.getHeader("Authorization");

        if(header==null||!header.startsWith("Bearer ")){
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 解析成功后检查是否在黑名单
        String token = header.substring(7);
        String blacklistKey = "blacklist:" + token;
        if (redisUtil.hasKey(blacklistKey)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        Claims claims = jwtUtil.parseToken(header.substring(7));
        Long userId = claims.get("userId", Long.class);
        Integer role = claims.get("role", Integer.class);
        UserContext.set(userId, role);

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        RequireRole requireRole = signature.getMethod().getAnnotation(RequireRole.class);
        if(requireRole!=null){
            RoleEnum userRole = RoleEnum.fromCode(role);
            //允许value里边的角色访问
            boolean matched = Arrays.stream(requireRole.value()).anyMatch(r -> r == userRole);
            if(!matched) throw new BusinessException(ResultCode.FORBIDDEN);
        }

        request.setAttribute(LogInterceptor.CURRENT_USER_ID_ATTR, UserContext.getUserId());

        try {
            return pjp.proceed();
        }finally{
            UserContext.remove();
        }
    }
}
