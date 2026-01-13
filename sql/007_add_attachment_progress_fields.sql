-- 云课堂 CloudClassroom：增量脚本（MySQL 8）
-- 用途：为章节附件学习进度增加可校验的“观看/翻页进度”，用于严格完成判定。
-- 注意：本脚本只做 ALTER TABLE，不会 DROP 数据。

USE cloud_classroom;

ALTER TABLE task_step_attachment_progress
  ADD COLUMN progress_percent INT NOT NULL DEFAULT 0 COMMENT '进度百分比：0-100' AFTER status,
  ADD COLUMN position_seconds INT DEFAULT NULL COMMENT '视频：当前播放秒数（可空）' AFTER progress_percent,
  ADD COLUMN duration_seconds INT DEFAULT NULL COMMENT '视频：总时长秒数（可空）' AFTER position_seconds,
  ADD COLUMN last_reported_at DATETIME(3) DEFAULT NULL COMMENT '最近上报时间（可空）' AFTER duration_seconds;

-- 可选索引（用于统计/查询优化）
CREATE INDEX idx_task_step_attachment_progress_attachment_id ON task_step_attachment_progress (attachment_id);
