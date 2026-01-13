-- 云课堂 CloudClassroom：创建数据库
-- 说明：使用 utf8mb4，适配中文与 emoji。

CREATE DATABASE IF NOT EXISTS cloud_classroom
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

-- 建议使用该库
USE cloud_classroom;
