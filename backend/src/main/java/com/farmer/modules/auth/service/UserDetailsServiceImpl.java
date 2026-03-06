package com.farmer.modules.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.farmer.common.exception.BizException;
import com.farmer.common.exception.ErrorCode;
import com.farmer.modules.auth.entity.SysUser;
import com.farmer.modules.auth.mapper.SysRoleMapper;
import com.farmer.modules.auth.mapper.SysUserMapper;
import com.farmer.modules.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        List<String> permissions = user.getRoleId() != null
                ? roleMapper.selectPermCodesByRoleId(user.getRoleId())
                : List.of();
        return new UserPrincipal(user, permissions);
    }

    public UserDetails loadUserById(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        List<String> permissions = user.getRoleId() != null
                ? roleMapper.selectPermCodesByRoleId(user.getRoleId())
                : List.of();
        return new UserPrincipal(user, permissions);
    }
}
