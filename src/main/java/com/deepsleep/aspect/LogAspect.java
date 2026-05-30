package com.deepsleep.aspect;

import com.deepsleep.context.UserContext;
import com.deepsleep.data.po.OperationLog;
import com.deepsleep.data.po.User;
import com.deepsleep.exception.BusinessException;
import com.deepsleep.mapper.OperationLogMapper;
import com.deepsleep.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final OperationLogMapper operationLogMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;// Jackson用于序列化参数

    @Around("@annotation(com.deepsleep.annotation.Log)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable{
        OperationLog log = new OperationLog();

        // 操作者信息
        Long userId = UserContext.getUserId();
        log.setOperatorId(userId);
        User user = userMapper.selectById(userId);
        log.setOperatorName(user!=null ? user.getName():"未知");

        // 操作描述
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        com.deepsleep.annotation.Log annotation = signature.getMethod().getAnnotation(com.deepsleep.annotation.Log.class);
        log.setOperation(annotation.value());

        // 请求方法与路径
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        log.setMethod(request.getMethod()+" "+request.getRequestURI());

        // 请求参数序列化
        try {
            Object[] args = pjp.getArgs();
            log.setParams(objectMapper.writeValueAsString(args));
        } catch (Exception e) {
            log.setParams("参数序列化失败");
        }

        // 执行方法并记录成功或失败
        try {
            Object result = pjp.proceed();
            log.setStatus(1); // 成功
            operationLogMapper.insert(log);
            return result;
        } catch (BusinessException e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMsg());
            operationLogMapper.insert(log);
            throw e;// 直接抛出让处理器处理
        } catch (Exception e) {
            log.setStatus(0);
            log.setErrorMsg(e.getMessage());
            operationLogMapper.insert(log);
            throw e;
        }
    }
}
