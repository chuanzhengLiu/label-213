package com.scenic.manager.interceptor;

import com.scenic.manager.utils.JwtUtil;
import com.scenic.manager.utils.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 */
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!StringUtils.hasText(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或token已过期\",\"data\":null}");
            return false;
        }

        // 验证token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token已过期\",\"data\":null}");
            return false;
        }

        // 从token中获取用户信息并存储到ThreadLocal
        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);

        // 验证Redis中的token
        String redisKey = "token:" + userId;
        Object redisToken = redisTemplate.opsForValue().get(redisKey);
        if (redisToken == null || !redisToken.equals(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"token已失效\",\"data\":null}");
            return false;
        }

        // 存储到ThreadLocal
        ThreadLocalUtil.setUser(userId, username, role);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal
        ThreadLocalUtil.clear();
    }
}
