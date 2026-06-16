package com.deepsleep.aspect;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME_ATTR = "requestStartTime";

    public static final String CURRENT_USER_ID_ATTR = "currentUserId";
    private static final String PUBLIC_REQUEST_USER_ID = "N/A";

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) {
        request.setAttribute(REQUEST_START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            @Nullable Exception ex
    ) {

        Object userIdAttr = request.getAttribute(CURRENT_USER_ID_ATTR);
        String userId = userIdAttr == null ?
                PUBLIC_REQUEST_USER_ID : userIdAttr.toString();

        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME_ATTR);
        long costMs = startTime == null ? -1 : System.currentTimeMillis() - startTime;

        log.info("处理请求 | 请求接口：{} {} | httpStatus={} userId={} costMs={} ex={}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                userId,
                costMs,
                ex == null ? null : ex.getClass().getSimpleName()
        );

    }
}
