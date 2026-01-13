-- 云课堂 CloudClassroom：初始化数据（最小可用）

USE cloud_classroom;

-- ===== 1) 角色 =====
INSERT INTO sys_role (role_code, role_name)
VALUES
  ('STUDENT', '学生'),
  ('TEACHER', '老师'),
  ('ADMIN', '管理员')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- ===== 2) 权限点（最小集合，后续可扩展） =====
INSERT INTO sys_permission (perm_code, perm_name)
VALUES
  ('auth:me', '查看当前登录信息'),
  ('course:create', '创建课程'),
  ('course:manage_members', '管理课程成员'),
  ('material:upload', '上传课程资料'),
  ('material:progress', '提交学习进度'),
  ('assignment:create', '布置作业'),
  ('assignment:submit', '提交作业'),
  ('assignment:grade', '批改作业'),
  ('exam:create', '创建考试'),
  ('exam:submit', '提交考试'),
  ('chat:send', '发送消息')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- ===== 3) 角色权限（简单配置：老师/管理员拥有更多） =====
-- 为了简单，本脚本用子查询按编码关联。
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p
WHERE r.role_code IN ('ADMIN')
;

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p
WHERE r.role_code IN ('TEACHER')
  AND p.perm_code IN ('auth:me','course:create','course:manage_members','material:upload','assignment:create','assignment:grade','exam:create','chat:send')
;

INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r
JOIN sys_permission p
WHERE r.role_code IN ('STUDENT')
  AND p.perm_code IN ('auth:me','material:progress','assignment:submit','exam:submit','chat:send')
;

-- ===== 4) 可选：预置一个管理员账号（密码先占位，后端启动后再改为 BCrypt） =====
-- 注意：此处 password_hash 为占位字符串，后续后端实现会提供“初始化管理员”接口/脚本。
INSERT INTO sys_user (username, password_hash, nickname, status)
VALUES ('admin', '{INIT}', '平台管理员', 1)
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

-- 绑定 admin 角色
INSERT IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u
JOIN sys_role r
WHERE u.username = 'admin' AND r.role_code = 'ADMIN';
