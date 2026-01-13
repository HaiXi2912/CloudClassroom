-- 云课堂 CloudClassroom：增量建表脚本（MySQL 8）
-- 用途：老数据库可能早期只执行过 001/002，缺少后续模块的表结构。
-- 本脚本不会 DROP 任何表，只会在表不存在时创建。

USE cloud_classroom;

-- ===== 7) 任务（学习路径）与成绩权重（增量） =====

CREATE TABLE IF NOT EXISTS task (
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

CREATE TABLE IF NOT EXISTS task_step (
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

CREATE TABLE IF NOT EXISTS task_link (
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

CREATE TABLE IF NOT EXISTS task_step_progress (
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

CREATE TABLE IF NOT EXISTS course_grade_config (
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
