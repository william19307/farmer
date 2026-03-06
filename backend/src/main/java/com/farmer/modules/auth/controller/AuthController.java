package com.farmer.modules.auth.controller;

import com.farmer.common.exception.BizException;
import com.farmer.common.exception.ErrorCode;
import com.farmer.common.result.Result;
import com.farmer.modules.auth.dto.LoginRequest;
import com.farmer.modules.auth.dto.LoginResponse;
import com.farmer.modules.auth.security.JwtTokenUtil;
import com.farmer.modules.auth.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "认证管理", description = "登录/登出")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BizException(ErrorCode.LOGIN_FAILED);
        } catch (DisabledException e) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userPrincipal.getId(), userPrincipal.getUsername());

        // Store token in Redis whitelist
        String redisKey = "token:" + token;
        redisTemplate.opsForValue().set(redisKey, userPrincipal.getId(),
                jwtTokenUtil.getExpiration(), TimeUnit.SECONDS);

        return Result.success(LoginResponse.builder()
                .token(token)
                .username(userPrincipal.getUsername())
                .userId(userPrincipal.getId())
                .regionId(userPrincipal.getRegionId())
                .roleId(userPrincipal.getRoleId())
                .expiresIn(jwtTokenUtil.getExpiration())
                .build());
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            redisTemplate.delete("token:" + token);
        }
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserPrincipal> getCurrentUserInfo(Authentication authentication) {
        if (authentication == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        return Result.success((UserPrincipal) authentication.getPrincipal());
    }
}
