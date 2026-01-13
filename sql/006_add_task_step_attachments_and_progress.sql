-- 云课堂 CloudClassroom：增量建表脚本（MySQL 8）
-- 用途：章节（task_step）富文本 + 附件（PDF/PPT/视频） + 学习完成进度。
-- 本脚本不会 DROP 任何表，只会在表不存在时创建。

USE cloud_classroom;

-- ===== 7.2) 章节附件 =====

CREATE TABLE IF NOT EXISTS task_step_attachment (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  step_id BIGINT NOT NULL COMMENT '章节ID（task_step.id）',
  kind TINYINT NOT NULL COMMENT '附件类型：1PDF 2PPT 3视频',
  title VARCHAR(200) DEFAULT NULL COMMENT '附件标题（可选）',
  file_id BIGINT DEFAULT NULL COMMENT '文件ID（file_object.id，可空）',
  url VARCHAR(1000) DEFAULT NULL COMMENT '外链URL（可选）',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  KEY idx_task_step_attachment_step_id (step_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='章节附件表';

-- 学生对“正文”的完成进度（必须读完正文才能完成章节）
CREATE TABLE IF NOT EXISTS task_step_content_progress (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  step_id BIGINT NOT NULL COMMENT '章节ID',
  user_id BIGINT NOT NULL COMMENT '学生ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1未完成 2已完成',
  completed_at DATETIME(3) DEFAULT NULL COMMENT '完成时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_task_step_content_progress (step_id, user_id),
  KEY idx_task_step_content_progress_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='章节正文完成进度表';

-- 学生对“附件”的完成进度（必须完成所有附件才能完成章节）
CREATE TABLE IF NOT EXISTS task_step_attachment_progress (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  attachment_id BIGINT NOT NULL COMMENT '附件ID（task_step_attachment.id）',
  user_id BIGINT NOT NULL COMMENT '学生ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1未完成 2已完成',
  completed_at DATETIME(3) DEFAULT NULL COMMENT '完成时间',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  PRIMARY KEY (id),
  UNIQUE KEY uk_task_step_attachment_progress (attachment_id, user_id),
  KEY idx_task_step_attachment_progress_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='章节附件完成进度表';
