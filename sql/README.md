# 数据库脚本说明

本目录用于存放：

- 建库脚本（cloud_classroom）
- 建表脚本（全部字段小写 + 下划线命名）
- 初始化数据脚本（可用于演示）

> 默认连接：localhost:3306，密码 123456。

## 全新初始化（推荐）

按顺序执行：

1. `001_create_database.sql`
2. `002_create_tables.sql`
3. `003_seed_data.sql`

## 老数据库增量升级

如果你是早期建的库，后续新增模块（例如“任务/学习路径”）可能会出现接口报错：

- `java.sql.SQLSyntaxErrorException: Table 'cloud_classroom.task' doesn't exist`
- `java.sql.SQLSyntaxErrorException: Table 'cloud_classroom.question_bank_question' doesn't exist`

这代表数据库缺少新表结构。

可以执行：

- `004_add_task_and_grade_tables.sql`
- `005_add_question_bank_tables.sql`
- `006_add_task_step_attachments_and_progress.sql`

该脚本只会 `CREATE TABLE IF NOT EXISTS`，不会 drop 旧表。
