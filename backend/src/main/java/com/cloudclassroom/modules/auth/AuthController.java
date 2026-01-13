package com.cloudclassroom.modules.auth;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.rbac.service.RbacService;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.service.SysUserService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.JwtTokenService;
import com.cloudclassroom.security.annotation.RequirePerm;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 认证接口：注册、登录、查看当前登录用户。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final SysUserService sysUserService;
  private final RbacService rbacService;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  public AuthController(SysUserService sysUserService, RbacService rbacService, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
    this.sysUserService = sysUserService;
    this.rbacService = rbacService;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenService = jwtTokenService;
  }

  /**
   * 注册（学生/老师）。
   */
  @PostMapping("/register")
  public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest req) {
    SysUser existed = sysUserService.findByUsername(req.getUsername());
    if (existed != null) {
      throw new BusinessException(40002, "用户名已存在");
    }

    SysUser user = new SysUser();
    user.setUsername(req.getUsername());
    user.setNickname(req.getNickname());
    user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
    user.setStatus(1);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());
    user.setDeleted(0);

    sysUserService.insert(user);

    // 绑定角色（最小实现：一个用户一个角色）
    String roleCode = normalizeRegisterRole(req.getRole());
    rbacService.bindUserRole(user.getId(), roleCode);

    // 说明：角色绑定（sys_user_role）会在下一步 RBAC 阶段完善。
    return ApiResponse.ok();
  }

  /**
   * 登录（返回 token）。
   */
  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
    SysUser user = sysUserService.findByUsername(req.getUsername());
    if (user == null || user.getStatus() == null || user.getStatus() == 0) {
      throw new BusinessException(40003, "账号或密码错误");
    }

    // 初始化管理员占位处理：避免数据库里留明文
    if ("{INIT}".equals(user.getPasswordHash())) {
      // 默认把 admin 的初始密码设为 123456（BCrypt）
      user.setPasswordHash(passwordEncoder.encode("123456"));
      sysUserService.updateById(user);
      throw new BusinessException(40004, "管理员账号已初始化，请使用默认密码 123456 重新登录");
    }

    if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
      throw new BusinessException(40003, "账号或密码错误");
    }

    // 角色：从数据库读取
    String roleCode = rbacService.findUserRoleCode(user.getId());
    if (roleCode == null) {
      roleCode = "STUDENT";
    }
    String token = jwtTokenService.generateToken(user.getId(), roleCode);

    LoginResponse resp = new LoginResponse();
    resp.setToken(token);
    resp.setUserId(user.getId());
    resp.setNickname(user.getNickname());
    resp.setRole(roleCode);

    return ApiResponse.ok(resp);
  }

  /**
   * 获取当前登录信息。
   */
  @GetMapping("/me")
  @RequirePerm("auth:me")
  public ApiResponse<MeResponse> me(Authentication authentication) {
    if (authentication == null) {
      throw new BusinessException(40100, "未登录");
    }

    MeResponse resp = new MeResponse();
    resp.setUserId(authentication.getName());
    resp.setAuthorities(authentication.getAuthorities().toString());
    // 约定：authority 里包含 ROLE_xxx
    resp.setRole(extractRole(authentication.getAuthorities().toString()));
    return ApiResponse.ok(resp);
  }

  /**
   * 获取个人信息（用于右上角头像/个人信息设置）。
   */
  @GetMapping("/profile")
  public ApiResponse<ProfileResponse> profile() {
    Long userId = SecurityUtil.requireUserId();
    SysUser user = sysUserService.findById(userId);
    if (user == null || user.getDeleted() == null || user.getDeleted() != 0) {
      throw new BusinessException(40400, "用户不存在");
    }

    ProfileResponse resp = new ProfileResponse();
    resp.setUserId(user.getId());
    resp.setUsername(user.getUsername());
    resp.setNickname(user.getNickname());
    resp.setAvatarUrl(user.getAvatarUrl());
    return ApiResponse.ok(resp);
  }

  /**
   * 更新个人信息。
   */
  @PutMapping("/profile")
  public ApiResponse<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
    Long userId = SecurityUtil.requireUserId();
    SysUser user = sysUserService.findById(userId);
    if (user == null || user.getDeleted() == null || user.getDeleted() != 0) {
      throw new BusinessException(40400, "用户不存在");
    }

    String nickname = req.getNickname() == null ? "" : req.getNickname().trim();
    if (nickname.isEmpty()) {
      throw new BusinessException(40040, "昵称不能为空");
    }
    if (nickname.length() > 50) {
      throw new BusinessException(40041, "昵称过长");
    }

    user.setNickname(nickname);
    user.setAvatarUrl(req.getAvatarUrl());
    user.setUpdatedAt(LocalDateTime.now());
    sysUserService.updateById(user);

    ProfileResponse resp = new ProfileResponse();
    resp.setUserId(user.getId());
    resp.setUsername(user.getUsername());
    resp.setNickname(user.getNickname());
    resp.setAvatarUrl(user.getAvatarUrl());
    return ApiResponse.ok(resp);
  }

  /**
   * 修改密码。
   */
  @PutMapping("/password")
  public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
    Long userId = SecurityUtil.requireUserId();
    SysUser user = sysUserService.findById(userId);
    if (user == null || user.getDeleted() == null || user.getDeleted() != 0) {
      throw new BusinessException(40400, "用户不存在");
    }

    String oldPwd = req.getOldPassword() == null ? "" : req.getOldPassword();
    String newPwd = req.getNewPassword() == null ? "" : req.getNewPassword();

    if (newPwd.length() < 6) {
      throw new BusinessException(40042, "新密码至少 6 位");
    }
    if (newPwd.length() > 50) {
      throw new BusinessException(40043, "新密码过长");
    }
    if (!passwordEncoder.matches(oldPwd, user.getPasswordHash())) {
      throw new BusinessException(40044, "旧密码不正确");
    }

    user.setPasswordHash(passwordEncoder.encode(newPwd));
    user.setUpdatedAt(LocalDateTime.now());
    sysUserService.updateById(user);
    return ApiResponse.ok();
  }

  private static String normalizeRegisterRole(String role) {
    if (role == null) {
      return "STUDENT";
    }
    String r = role.trim().toUpperCase();
    if ("TEACHER".equals(r)) {
      return "TEACHER";
    }
    return "STUDENT";
  }

  private static String extractRole(String authoritiesText) {
    if (authoritiesText == null) {
      return null;
    }
    // 示例：[ROLE_STUDENT]
    String t = authoritiesText.replace("[", "").replace("]", "");
    if (t.startsWith("ROLE_")) {
      return t.substring(5);
    }
    return t;
  }

  public static class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名格式不正确")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    // 角色：STUDENT/TEACHER（最小实现先不入库，后续 RBAC 阶段落表）
    private String role;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }
  }

  public static class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public static class LoginResponse {
    private Long userId;
    private String nickname;
    private String role;
    private String token;

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }

  public static class MeResponse {
    private String userId;
    private String authorities;
    private String role;

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getAuthorities() {
      return authorities;
    }

    public void setAuthorities(String authorities) {
      this.authorities = authorities;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }
  }

  public static class ProfileResponse {
    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;

    public Long getUserId() {
      return userId;
    }

    public void setUserId(Long userId) {
      this.userId = userId;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getAvatarUrl() {
      return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
    }
  }

  public static class UpdateProfileRequest {
    @NotBlank(message = "nickname 不能为空")
    private String nickname;
    private String avatarUrl;

    public String getNickname() {
      return nickname;
    }

    public void setNickname(String nickname) {
      this.nickname = nickname;
    }

    public String getAvatarUrl() {
      return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
    }
  }

  public static class ChangePasswordRequest {
    @NotNull(message = "oldPassword 不能为空")
    private String oldPassword;

    @NotNull(message = "newPassword 不能为空")
    private String newPassword;

    public String getOldPassword() {
      return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
      this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
      return newPassword;
    }

    public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
    }
  }
}
