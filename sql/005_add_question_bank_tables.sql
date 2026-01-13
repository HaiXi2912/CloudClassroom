-- 增量升级脚本：补齐题库模块相关表（适用于老版本数据库）
-- 说明：仅 CREATE TABLE IF NOT EXISTS，不会 drop 旧表。

CREATE TABLE IF NOT EXISTS question_bank_question (
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
