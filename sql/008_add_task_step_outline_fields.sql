-- 云课堂 CloudClassroom：任务章节大纲分层（节点/内容）字段增量（MySQL 8）
-- 用途：支持“大纲分为节点和内容，节点可折叠”

USE cloud_classroom;

-- step_type：1=节点（分组/目录），2=内容（可学习的章节）
-- parent_id：内容所属节点；节点目前约定 parent_id 为空

ALTER TABLE task_step
  ADD COLUMN step_type TINYINT NOT NULL DEFAULT 2 COMMENT '步骤类型：1节点 2内容' AFTER task_id,
  ADD COLUMN parent_id BIGINT DEFAULT NULL COMMENT '父节点步骤ID（可空）' AFTER step_type;

CREATE INDEX idx_task_step_task_parent_sort
  ON task_step (task_id, parent_id, sort_no, id);
