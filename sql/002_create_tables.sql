-- 云课堂 CloudClassroom：建表脚本（MySQL 8）
-- 命名：小写 + 下划线；主键 bigint；时间 datetime(3)；逻辑删除 deleted。

USE cloud_classroom;

-- ===== 1) 权限与用户（RBAC） =====
DROP TABLE IF EXISTS sys_role_permission;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_permission;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
  password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希（BCrypt）',
  nickname VARCHAR(50) NOT NULL COMMENT '昵称',
  avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号（可选）',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱（可选）',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  last_login_at DATETIME(3) DEFAULT NULL COMMENT '最后登录时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username (username),
  KEY idx_sys_user_status (status),
  KEY idx_sys_user_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

CREATE TABLE sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  role_code VARCHAR(30) NOT NULL COMMENT '角色编码：STUDENT/TEACHER/ADMIN',
  role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

CREATE TABLE sys_user_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_role (user_id, role_id),
  KEY idx_sys_user_role_user_id (user_id),
  KEY idx_sys_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';

CREATE TABLE sys_permission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  perm_code VARCHAR(80) NOT NULL COMMENT '权限编码，例如 course:create',
  perm_name VARCHAR(100) NOT NULL COMMENT '权限名称',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_permission_code (perm_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限点表';

CREATE TABLE sys_role_permission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_role_permission (role_id, permission_id),
  KEY idx_sys_role_permission_role_id (role_id),
  KEY idx_sys_role_permission_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ===== 2) 课程 =====
DROP TABLE IF EXISTS course_member;
DROP TABLE IF EXISTS course;

CREATE TABLE course (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  teacher_id BIGINT NOT NULL COMMENT '创建老师ID',
  course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
  course_code VARCHAR(30) NOT NULL COMMENT '课程码（唯一，可用于加入）',
  description VARCHAR(500) DEFAULT NULL COMMENT '课程简介',
  cover_url VARCHAR(255) DEFAULT NULL COMMENT '封面URL',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_course_code (course_code),
  KEY idx_course_teacher_id (teacher_id),
  KEY idx_course_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程表';

CREATE TABLE course_member (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  member_role TINYINT NOT NULL DEFAULT 1 COMMENT '成员角色：1学生 2助教/老师（预留）',
  joined_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '加入时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_course_member (course_id, user_id),
  KEY idx_course_member_course_id (course_id),
  KEY idx_course_member_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程成员表';

-- ===== 3) 文件与资料 =====
DROP TABLE IF EXISTS material_progress;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS file_object;

CREATE TABLE file_object (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
  stored_path VARCHAR(300) NOT NULL COMMENT '存储相对路径（后端本地存储）',
  content_type VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  file_ext VARCHAR(20) DEFAULT NULL COMMENT '扩展名',
  file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  sha256 VARCHAR(64) DEFAULT NULL COMMENT 'sha256（可选，用于去重/校验）',
  created_by BIGINT DEFAULT NULL COMMENT '上传者ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_file_object_created_by (created_by),
  KEY idx_file_object_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件对象表';

CREATE TABLE material (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  file_id BIGINT NOT NULL COMMENT '文件ID',
  title VARCHAR(200) NOT NULL COMMENT '资料标题',
  material_type TINYINT NOT NULL COMMENT '资料类型：1PDF 2PPT 3DOCX 4其他',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_by BIGINT NOT NULL COMMENT '创建者（老师）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_material_course_id (course_id),
  KEY idx_material_file_id (file_id),
  KEY idx_material_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程资料表';

CREATE TABLE material_progress (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  material_id BIGINT NOT NULL COMMENT '资料ID',
  user_id BIGINT NOT NULL COMMENT '学生ID',
  progress_percent INT NOT NULL DEFAULT 0 COMMENT '进度百分比：0-100',
  last_position INT NOT NULL DEFAULT 0 COMMENT '最近位置（页码/秒数，前端定义）',
  last_study_at DATETIME(3) DEFAULT NULL COMMENT '最近学习时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_material_progress (material_id, user_id),
  KEY idx_material_progress_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资料学习进度表';

-- ===== 4) 作业 =====
DROP TABLE IF EXISTS assignment_submission_answer;
DROP TABLE IF EXISTS assignment_submission;
DROP TABLE IF EXISTS assignment_question;
DROP TABLE IF EXISTS assignment;

CREATE TABLE assignment (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  title VARCHAR(200) NOT NULL COMMENT '作业标题',
  content TEXT DEFAULT NULL COMMENT '作业说明',
  publish_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发布时间',
  due_at DATETIME(3) DEFAULT NULL COMMENT '截止时间（可为空表示不截止）',
  total_score INT NOT NULL DEFAULT 100 COMMENT '总分（客观题自动计分用）',
  created_by BIGINT NOT NULL COMMENT '创建者（老师）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_assignment_course_id (course_id),
  KEY idx_assignment_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业表';

CREATE TABLE assignment_question (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  assignment_id BIGINT NOT NULL COMMENT '作业ID',
  question_type TINYINT NOT NULL COMMENT '题型：1单选 2多选 3判断 4简答（预留）',
  question_text TEXT NOT NULL COMMENT '题干',
  option_a VARCHAR(500) DEFAULT NULL COMMENT '选项A',
  option_b VARCHAR(500) DEFAULT NULL COMMENT '选项B',
  option_c VARCHAR(500) DEFAULT NULL COMMENT '选项C',
  option_d VARCHAR(500) DEFAULT NULL COMMENT '选项D',
  correct_answer VARCHAR(50) DEFAULT NULL COMMENT '正确答案（客观题：A/B/C/D/TRUE/FALSE）',
  score INT NOT NULL DEFAULT 5 COMMENT '分值',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_assignment_question_assignment_id (assignment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业题目表';

CREATE TABLE assignment_submission (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  assignment_id BIGINT NOT NULL COMMENT '作业ID',
  student_id BIGINT NOT NULL COMMENT '学生ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1未提交 2已提交 3已批改',
  submit_at DATETIME(3) DEFAULT NULL COMMENT '提交时间',
  auto_score INT NOT NULL DEFAULT 0 COMMENT '客观题自动得分',
  manual_score INT NOT NULL DEFAULT 0 COMMENT '主观题人工得分（预留）',
  total_score INT NOT NULL DEFAULT 0 COMMENT '总得分',
  teacher_comment VARCHAR(500) DEFAULT NULL COMMENT '老师评语',
  graded_by BIGINT DEFAULT NULL COMMENT '批改老师ID',
  graded_at DATETIME(3) DEFAULT NULL COMMENT '批改时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_assignment_submission (assignment_id, student_id),
  KEY idx_assignment_submission_student_id (student_id),
  KEY idx_assignment_submission_assignment_id (assignment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业提交表';

CREATE TABLE assignment_submission_answer (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  submission_id BIGINT NOT NULL COMMENT '提交ID',
  question_id BIGINT NOT NULL COMMENT '题目ID',
  answer_text VARCHAR(200) DEFAULT NULL COMMENT '答案（客观题：A/B/C/D/TRUE/FALSE）',
  is_correct TINYINT NOT NULL DEFAULT 0 COMMENT '是否正确：1是 0否',
  score INT NOT NULL DEFAULT 0 COMMENT '得分',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_assignment_submission_answer (submission_id, question_id),
  KEY idx_assignment_submission_answer_submission_id (submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作业答题表';

-- ===== 5) 考试 =====
DROP TABLE IF EXISTS exam_attempt_answer;
DROP TABLE IF EXISTS exam_attempt;
DROP TABLE IF EXISTS exam_question;
DROP TABLE IF EXISTS exam;

CREATE TABLE exam (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  title VARCHAR(200) NOT NULL COMMENT '考试标题',
  description VARCHAR(500) DEFAULT NULL COMMENT '考试说明',
  start_at DATETIME(3) NOT NULL COMMENT '开始时间',
  end_at DATETIME(3) NOT NULL COMMENT '结束时间',
  duration_minutes INT NOT NULL DEFAULT 60 COMMENT '时长（分钟）',
  total_score INT NOT NULL DEFAULT 100 COMMENT '总分',
  created_by BIGINT NOT NULL COMMENT '创建者（老师）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_exam_course_id (course_id),
  KEY idx_exam_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试表';

CREATE TABLE exam_question (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  exam_id BIGINT NOT NULL COMMENT '考试ID',
  question_type TINYINT NOT NULL COMMENT '题型：1单选 3判断（最小实现）',
  question_text TEXT NOT NULL COMMENT '题干',
  option_a VARCHAR(500) DEFAULT NULL COMMENT '选项A',
  option_b VARCHAR(500) DEFAULT NULL COMMENT '选项B',
  option_c VARCHAR(500) DEFAULT NULL COMMENT '选项C',
  option_d VARCHAR(500) DEFAULT NULL COMMENT '选项D',
  correct_answer VARCHAR(50) DEFAULT NULL COMMENT '正确答案',
  score INT NOT NULL DEFAULT 5 COMMENT '分值',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_exam_question_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试题目表';

CREATE TABLE exam_attempt (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  exam_id BIGINT NOT NULL COMMENT '考试ID',
  student_id BIGINT NOT NULL COMMENT '学生ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1进行中 2已提交',
  start_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '开始作答时间',
  submit_at DATETIME(3) DEFAULT NULL COMMENT '提交时间',
  total_score INT NOT NULL DEFAULT 0 COMMENT '得分',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_exam_attempt (exam_id, student_id),
  KEY idx_exam_attempt_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试作答记录表';

CREATE TABLE exam_attempt_answer (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  attempt_id BIGINT NOT NULL COMMENT '作答ID',
  question_id BIGINT NOT NULL COMMENT '题目ID',
  answer_text VARCHAR(200) DEFAULT NULL COMMENT '答案',
  is_correct TINYINT NOT NULL DEFAULT 0 COMMENT '是否正确：1是 0否',
  score INT NOT NULL DEFAULT 0 COMMENT '得分',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_exam_attempt_answer (attempt_id, question_id),
  KEY idx_exam_attempt_answer_attempt_id (attempt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='考试答题表';

-- ===== 5.1) 题库（按课程） =====
DROP TABLE IF EXISTS question_bank_question;

CREATE TABLE question_bank_question (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  question_type TINYINT NOT NULL COMMENT '题型：1单选 2多选 3判断 4简答（预留）',
  question_text TEXT NOT NULL COMMENT '题干',
  option_a VARCHAR(500) DEFAULT NULL COMMENT '选项A',
  option_b VARCHAR(500) DEFAULT NULL COMMENT '选项B',
  option_c VARCHAR(500) DEFAULT NULL COMMENT '选项C',
  option_d VARCHAR(500) DEFAULT NULL COMMENT '选项D',
  correct_answer VARCHAR(50) DEFAULT NULL COMMENT '正确答案（客观题：A/B/C/D/TRUE/FALSE）',
  score INT NOT NULL DEFAULT 5 COMMENT '分值',
  created_by BIGINT NOT NULL COMMENT '创建者（老师）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_question_bank_course_id (course_id),
  KEY idx_question_bank_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题库题目表（按课程）';

-- ===== 6) 私聊 =====
DROP TABLE IF EXISTS chat_message;
DROP TABLE IF EXISTS chat_conversation_member;
DROP TABLE IF EXISTS chat_conversation;
DROP TABLE IF EXISTS chat_friend_request;
DROP TABLE IF EXISTS chat_friend;

CREATE TABLE chat_conversation (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  conversation_type TINYINT NOT NULL DEFAULT 1 COMMENT '会话类型：1私聊',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天会话表';

CREATE TABLE chat_conversation_member (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  conversation_id BIGINT NOT NULL COMMENT '会话ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  joined_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '加入时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_chat_conversation_member (conversation_id, user_id),
  KEY idx_chat_conversation_member_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话成员表';

CREATE TABLE chat_message (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID（也用于按ID增量拉取）',
  conversation_id BIGINT NOT NULL COMMENT '会话ID',
  sender_id BIGINT NOT NULL COMMENT '发送者ID',
  content TEXT NOT NULL COMMENT '消息内容（文本）',
  sent_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '发送时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_chat_message_conversation_id_id (conversation_id, id),
  KEY idx_chat_message_sender_id (sender_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天消息表';

-- ===== 6.1) 好友与好友申请 =====
CREATE TABLE chat_friend (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  friend_user_id BIGINT NOT NULL COMMENT '好友用户ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_chat_friend (user_id, friend_user_id),
  KEY idx_chat_friend_user_id (user_id),
  KEY idx_chat_friend_friend_user_id (friend_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友关系表（双向存两条）';

CREATE TABLE chat_friend_request (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  from_user_id BIGINT NOT NULL COMMENT '申请人ID',
  to_user_id BIGINT NOT NULL COMMENT '被申请人ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1待处理 2已通过 3已拒绝',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  handled_at DATETIME(3) DEFAULT NULL COMMENT '处理时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_chat_friend_request_to_user_id_status (to_user_id, status),
  KEY idx_chat_friend_request_from_user_id_status (from_user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='好友申请表';

-- ===== 7) 任务（学习路径）与成绩权重 =====
DROP TABLE IF EXISTS task_step_progress;
DROP TABLE IF EXISTS task_link;
DROP TABLE IF EXISTS task_step;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS course_grade_config;

CREATE TABLE task (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  title VARCHAR(200) NOT NULL COMMENT '任务标题',
  description VARCHAR(500) DEFAULT NULL COMMENT '任务描述',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_by BIGINT NOT NULL COMMENT '创建者（老师）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_task_course_id (course_id),
  KEY idx_task_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务（学习路径）表';

CREATE TABLE task_step (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  step_type TINYINT NOT NULL DEFAULT 2 COMMENT '步骤类型：1节点 2内容',
  parent_id BIGINT DEFAULT NULL COMMENT '父节点步骤ID（可空）',
  title VARCHAR(200) NOT NULL COMMENT '步骤标题',
  content TEXT DEFAULT NULL COMMENT '步骤内容（Markdown/HTML，前端约定）',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_task_step_task_id (task_id),
  KEY idx_task_step_task_parent_sort (task_id, parent_id, sort_no, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务步骤表';

CREATE TABLE task_link (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  step_id BIGINT DEFAULT NULL COMMENT '步骤ID（可空，表示挂在任务下）',
  link_type TINYINT NOT NULL COMMENT '关联类型：1作业 2考试 3资料（预留）',
  ref_id BIGINT NOT NULL COMMENT '关联对象ID（assignmentId/examId/materialId）',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_task_link (task_id, step_id, link_type, ref_id),
  KEY idx_task_link_task_id (task_id),
  KEY idx_task_link_step_id (step_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务关联表（作业/考试/资料等）';

CREATE TABLE task_step_progress (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  step_id BIGINT NOT NULL COMMENT '步骤ID',
  user_id BIGINT NOT NULL COMMENT '学生ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1未完成 2已完成',
  completed_at DATETIME(3) DEFAULT NULL COMMENT '完成时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_task_step_progress (step_id, user_id),
  KEY idx_task_step_progress_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务步骤完成进度表';

CREATE TABLE course_grade_config (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  course_id BIGINT NOT NULL COMMENT '课程ID',
  weight_task INT NOT NULL DEFAULT 30 COMMENT '任务权重（0-100）',
  weight_assignment INT NOT NULL DEFAULT 30 COMMENT '作业权重（0-100）',
  weight_exam INT NOT NULL DEFAULT 40 COMMENT '考试权重（0-100）',
  created_by BIGINT NOT NULL COMMENT '配置者（老师）',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_course_grade_config (course_id),
  KEY idx_course_grade_config_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程成绩权重配置表';
