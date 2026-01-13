-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: cloud_classroom
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `assignment`
--

DROP TABLE IF EXISTS `assignment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID',
  `title` varchar(200) NOT NULL COMMENT '浣滀笟鏍囬',
  `content` text COMMENT '浣滀笟璇存槑',
  `publish_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍙戝竷鏃堕棿',
  `due_at` datetime(3) DEFAULT NULL COMMENT '鎴鏃堕棿锛堝彲涓虹┖琛ㄧず涓嶆埅姝級',
  `total_score` int NOT NULL DEFAULT '100' COMMENT '鎬诲垎锛堝瑙傞鑷姩璁″垎鐢級',
  `created_by` bigint NOT NULL COMMENT '鍒涘缓鑰咃紙鑰佸笀锛?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_assignment_course_id` (`course_id`),
  KEY `idx_assignment_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣滀笟琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment`
--

LOCK TABLES `assignment` WRITE;
/*!40000 ALTER TABLE `assignment` DISABLE KEYS */;
INSERT INTO `assignment` VALUES (1,4,'绗竴娆′綔涓?,'璇峰畬鎴愰鐩?,'2026-01-13 02:16:16.009','2026-01-14 02:16:15.977',15,42,'2026-01-13 02:16:16.009','2026-01-13 02:16:16.009',0),(2,6,'绗竴娆′綔涓?,'璇峰畬鎴愰鐩?,'2026-01-13 02:28:36.752','2026-01-14 02:28:36.728',15,51,'2026-01-13 02:28:36.752','2026-01-13 02:28:36.752',0),(3,9,'绗竴娆′綔涓?,'璇峰畬鎴愰鐩?,'2026-01-13 03:04:11.944','2026-01-14 03:04:11.916',15,62,'2026-01-13 03:04:11.945','2026-01-13 03:04:11.945',0),(4,13,'绗竴娆′綔涓?,'璇峰畬鎴愰鐩?,'2026-01-13 03:18:59.823','2026-01-14 03:18:59.794',15,75,'2026-01-13 03:18:59.823','2026-01-13 03:18:59.823',0),(5,17,'绗竴娆′綔涓?,'璇峰畬鎴愰鐩?,'2026-01-13 03:29:33.812','2026-01-14 03:29:33.784',15,90,'2026-01-13 03:29:33.812','2026-01-13 03:29:33.812',0),(6,22,'绗竴娆′綔涓?,'璇峰畬鎴愰鐩?,'2026-01-13 03:39:50.198','2026-01-14 03:39:50.169',15,108,'2026-01-13 03:39:50.198','2026-01-13 03:39:50.198',0);
/*!40000 ALTER TABLE `assignment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment_question`
--

DROP TABLE IF EXISTS `assignment_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignment_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `assignment_id` bigint NOT NULL COMMENT '浣滀笟ID',
  `question_type` tinyint NOT NULL COMMENT '棰樺瀷锛?鍗曢€?2澶氶€?3鍒ゆ柇 4绠€绛旓紙棰勭暀锛?,
  `question_text` text NOT NULL COMMENT '棰樺共',
  `option_a` varchar(500) DEFAULT NULL COMMENT '閫夐」A',
  `option_b` varchar(500) DEFAULT NULL COMMENT '閫夐」B',
  `option_c` varchar(500) DEFAULT NULL COMMENT '閫夐」C',
  `option_d` varchar(500) DEFAULT NULL COMMENT '閫夐」D',
  `correct_answer` varchar(50) DEFAULT NULL COMMENT '姝ｇ‘绛旀锛堝瑙傞锛欰/B/C/D/TRUE/FALSE锛?,
  `score` int NOT NULL DEFAULT '5' COMMENT '鍒嗗€?,
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '鎺掑簭鍙?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_assignment_question_assignment_id` (`assignment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣滀笟棰樼洰琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment_question`
--

LOCK TABLES `assignment_question` WRITE;
/*!40000 ALTER TABLE `assignment_question` DISABLE KEYS */;
INSERT INTO `assignment_question` VALUES (1,1,1,'1+1=锛?,'1','2','3','4','B',5,1,'2026-01-13 02:16:16.033',0),(2,1,4,'绠€杩颁綘瀵逛簯璇惧爞鐨勭悊瑙?,NULL,NULL,NULL,NULL,NULL,10,2,'2026-01-13 02:16:16.049',0),(3,2,1,'1+1=锛?,'1','2','3','4','B',5,1,'2026-01-13 02:28:36.770',0),(4,2,4,'绠€杩颁綘瀵逛簯璇惧爞鐨勭悊瑙?,NULL,NULL,NULL,NULL,NULL,10,2,'2026-01-13 02:28:36.783',0),(5,3,1,'1+1=锛?,'1','2','3','4','B',5,1,'2026-01-13 03:04:11.957',0),(6,3,4,'绠€杩颁綘瀵逛簯璇惧爞鐨勭悊瑙?,NULL,NULL,NULL,NULL,NULL,10,2,'2026-01-13 03:04:11.970',0),(7,4,1,'1+1=锛?,'1','2','3','4','B',5,1,'2026-01-13 03:18:59.840',0),(8,4,4,'绠€杩颁綘瀵逛簯璇惧爞鐨勭悊瑙?,NULL,NULL,NULL,NULL,NULL,10,2,'2026-01-13 03:18:59.856',0),(9,5,1,'1+1=锛?,'1','2','3','4','B',5,1,'2026-01-13 03:29:33.825',0),(10,5,4,'绠€杩颁綘瀵逛簯璇惧爞鐨勭悊瑙?,NULL,NULL,NULL,NULL,NULL,10,2,'2026-01-13 03:29:33.838',0),(11,6,1,'1+1=锛?,'1','2','3','4','B',5,1,'2026-01-13 03:39:50.214',0),(12,6,4,'绠€杩颁綘瀵逛簯璇惧爞鐨勭悊瑙?,NULL,NULL,NULL,NULL,NULL,10,2,'2026-01-13 03:39:50.234',0);
/*!40000 ALTER TABLE `assignment_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment_submission`
--

DROP TABLE IF EXISTS `assignment_submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignment_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `assignment_id` bigint NOT NULL COMMENT '浣滀笟ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '鐘舵€侊細1鏈彁浜?2宸叉彁浜?3宸叉壒鏀?,
  `submit_at` datetime(3) DEFAULT NULL COMMENT '鎻愪氦鏃堕棿',
  `auto_score` int NOT NULL DEFAULT '0' COMMENT '瀹㈣棰樿嚜鍔ㄥ緱鍒?,
  `manual_score` int NOT NULL DEFAULT '0' COMMENT '涓昏棰樹汉宸ュ緱鍒嗭紙棰勭暀锛?,
  `total_score` int NOT NULL DEFAULT '0' COMMENT '鎬诲緱鍒?,
  `teacher_comment` varchar(500) DEFAULT NULL COMMENT '鑰佸笀璇勮',
  `graded_by` bigint DEFAULT NULL COMMENT '鎵规敼鑰佸笀ID',
  `graded_at` datetime(3) DEFAULT NULL COMMENT '鎵规敼鏃堕棿',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assignment_submission` (`assignment_id`,`student_id`),
  KEY `idx_assignment_submission_student_id` (`student_id`),
  KEY `idx_assignment_submission_assignment_id` (`assignment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣滀笟鎻愪氦琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment_submission`
--

LOCK TABLES `assignment_submission` WRITE;
/*!40000 ALTER TABLE `assignment_submission` DISABLE KEYS */;
INSERT INTO `assignment_submission` VALUES (1,1,43,3,'2026-01-13 02:16:16.169',5,8,13,'鍥炵瓟杈冨畬鏁?,42,'2026-01-13 02:16:16.227','2026-01-13 02:16:16.121','2026-01-13 02:16:16.227',0),(2,2,52,3,'2026-01-13 02:28:36.904',5,8,13,'鍥炵瓟杈冨畬鏁?,51,'2026-01-13 02:28:36.956','2026-01-13 02:28:36.849','2026-01-13 02:28:36.956',0),(3,3,63,3,'2026-01-13 03:04:12.088',5,8,13,'鍥炵瓟杈冨畬鏁?,62,'2026-01-13 03:04:12.140','2026-01-13 03:04:12.037','2026-01-13 03:04:12.140',0),(4,4,76,3,'2026-01-13 03:18:59.978',5,8,13,'鍥炵瓟杈冨畬鏁?,75,'2026-01-13 03:19:00.027','2026-01-13 03:18:59.926','2026-01-13 03:19:00.027',0),(5,5,91,3,'2026-01-13 03:29:33.953',5,8,13,'鍥炵瓟杈冨畬鏁?,90,'2026-01-13 03:29:34.008','2026-01-13 03:29:33.910','2026-01-13 03:29:34.008',0),(6,6,109,3,'2026-01-13 03:39:50.352',5,8,13,'鍥炵瓟杈冨畬鏁?,108,'2026-01-13 03:39:50.408','2026-01-13 03:39:50.301','2026-01-13 03:39:50.408',0);
/*!40000 ALTER TABLE `assignment_submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignment_submission_answer`
--

DROP TABLE IF EXISTS `assignment_submission_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignment_submission_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `submission_id` bigint NOT NULL COMMENT '鎻愪氦ID',
  `question_id` bigint NOT NULL COMMENT '棰樼洰ID',
  `answer_text` varchar(200) DEFAULT NULL COMMENT '绛旀锛堝瑙傞锛欰/B/C/D/TRUE/FALSE锛?,
  `is_correct` tinyint NOT NULL DEFAULT '0' COMMENT '鏄惁姝ｇ‘锛?鏄?0鍚?,
  `score` int NOT NULL DEFAULT '0' COMMENT '寰楀垎',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assignment_submission_answer` (`submission_id`,`question_id`),
  KEY `idx_assignment_submission_answer_submission_id` (`submission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣滀笟绛旈琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignment_submission_answer`
--

LOCK TABLES `assignment_submission_answer` WRITE;
/*!40000 ALTER TABLE `assignment_submission_answer` DISABLE KEYS */;
INSERT INTO `assignment_submission_answer` VALUES (1,1,1,'B',1,5,'2026-01-13 02:16:16.142',0),(2,1,2,'鎴戣涓轰簯璇惧爞鏄竴涓敮鎸佹暀瀛︿簰鍔ㄧ殑骞冲彴銆?,0,0,'2026-01-13 02:16:16.155',0),(3,2,3,'B',1,5,'2026-01-13 02:28:36.878',0),(4,2,4,'鎴戣涓轰簯璇惧爞鏄竴涓敮鎸佹暀瀛︿簰鍔ㄧ殑骞冲彴銆?,0,0,'2026-01-13 02:28:36.893',0),(5,3,5,'B',1,5,'2026-01-13 03:04:12.061',0),(6,3,6,'鎴戣涓轰簯璇惧爞鏄竴涓敮鎸佹暀瀛︿簰鍔ㄧ殑骞冲彴銆?,0,0,'2026-01-13 03:04:12.073',0),(7,4,7,'B',1,5,'2026-01-13 03:18:59.952',0),(8,4,8,'鎴戣涓轰簯璇惧爞鏄竴涓敮鎸佹暀瀛︿簰鍔ㄧ殑骞冲彴銆?,0,0,'2026-01-13 03:18:59.965',0),(9,5,9,'B',1,5,'2026-01-13 03:29:33.928',0),(10,5,10,'鎴戣涓轰簯璇惧爞鏄竴涓敮鎸佹暀瀛︿簰鍔ㄧ殑骞冲彴銆?,0,0,'2026-01-13 03:29:33.941',0),(11,6,11,'B',1,5,'2026-01-13 03:39:50.324',0),(12,6,12,'鎴戣涓轰簯璇惧爞鏄竴涓敮鎸佹暀瀛︿簰鍔ㄧ殑骞冲彴銆?,0,0,'2026-01-13 03:39:50.342',0);
/*!40000 ALTER TABLE `assignment_submission_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_conversation`
--

DROP TABLE IF EXISTS `chat_conversation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `conversation_type` tinyint NOT NULL DEFAULT '1' COMMENT '浼氳瘽绫诲瀷锛?绉佽亰',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑱婂ぉ浼氳瘽琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_conversation`
--

LOCK TABLES `chat_conversation` WRITE;
/*!40000 ALTER TABLE `chat_conversation` DISABLE KEYS */;
INSERT INTO `chat_conversation` VALUES (1,1,'2026-01-13 03:19:04.416','2026-01-13 03:19:04.416',0),(2,1,'2026-01-13 03:29:38.476','2026-01-13 03:29:38.476',0),(3,1,'2026-01-13 03:39:55.050','2026-01-13 03:39:55.050',0);
/*!40000 ALTER TABLE `chat_conversation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_conversation_member`
--

DROP TABLE IF EXISTS `chat_conversation_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_conversation_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `conversation_id` bigint NOT NULL COMMENT '浼氳瘽ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `joined_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍔犲叆鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_chat_conversation_member` (`conversation_id`,`user_id`),
  KEY `idx_chat_conversation_member_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浼氳瘽鎴愬憳琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_conversation_member`
--

LOCK TABLES `chat_conversation_member` WRITE;
/*!40000 ALTER TABLE `chat_conversation_member` DISABLE KEYS */;
INSERT INTO `chat_conversation_member` VALUES (1,1,79,'2026-01-13 03:19:04.429',0),(2,1,80,'2026-01-13 03:19:04.450',0),(3,2,94,'2026-01-13 03:29:38.488',0),(4,2,95,'2026-01-13 03:29:38.499',0),(5,3,112,'2026-01-13 03:39:55.060',0),(6,3,113,'2026-01-13 03:39:55.070',0);
/*!40000 ALTER TABLE `chat_conversation_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID锛堜篃鐢ㄤ簬鎸塈D澧為噺鎷夊彇锛?,
  `conversation_id` bigint NOT NULL COMMENT '浼氳瘽ID',
  `sender_id` bigint NOT NULL COMMENT '鍙戦€佽€匢D',
  `content` text NOT NULL COMMENT '娑堟伅鍐呭锛堟枃鏈級',
  `sent_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍙戦€佹椂闂?,
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_chat_message_conversation_id_id` (`conversation_id`,`id`),
  KEY `idx_chat_message_sender_id` (`sender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑱婂ぉ娑堟伅琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1,1,79,'浣犲ソ锛屾垜鏄疉','2026-01-13 03:19:04.484',0),(2,1,80,'鏀跺埌锛屾垜鏄疊','2026-01-13 03:19:04.508',0),(3,2,94,'浣犲ソ锛屾垜鏄疉','2026-01-13 03:29:38.526',0),(4,2,95,'鏀跺埌锛屾垜鏄疊','2026-01-13 03:29:38.549',0),(5,3,112,'浣犲ソ锛屾垜鏄疉','2026-01-13 03:39:55.098',0),(6,3,113,'鏀跺埌锛屾垜鏄疊','2026-01-13 03:39:55.120',0);
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `teacher_id` bigint NOT NULL COMMENT '鍒涘缓鑰佸笀ID',
  `course_name` varchar(100) NOT NULL COMMENT '璇剧▼鍚嶇О',
  `course_code` varchar(30) NOT NULL COMMENT '璇剧▼鐮侊紙鍞竴锛屽彲鐢ㄤ簬鍔犲叆锛?,
  `description` varchar(500) DEFAULT NULL COMMENT '璇剧▼绠€浠?,
  `cover_url` varchar(255) DEFAULT NULL COMMENT '灏侀潰URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '鐘舵€侊細1鍚敤 0绂佺敤',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_course_teacher_id` (`teacher_id`),
  KEY `idx_course_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,21,'Java鍏ラ棬','KZFYZM','绗竴闂ㄨ',NULL,1,'2026-01-13 01:25:30.334','2026-01-13 01:25:30.334',0),(2,28,'Java鍏ラ棬','D99RRY','绗竴闂ㄨ',NULL,1,'2026-01-13 01:27:36.186','2026-01-13 01:27:36.186',0),(3,37,'Java鍏ラ棬','J2JS2G','绗竴闂ㄨ',NULL,1,'2026-01-13 02:14:08.997','2026-01-13 02:14:08.997',0),(4,42,'浣滀笟娴佺▼璇?,'XFATKA','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 02:16:15.893','2026-01-13 02:16:15.893',0),(5,46,'Java鍏ラ棬','PQFAPK','绗竴闂ㄨ',NULL,1,'2026-01-13 02:16:20.312','2026-01-13 02:16:20.312',0),(6,51,'浣滀笟娴佺▼璇?,'JD7V7P','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 02:28:36.665','2026-01-13 02:28:36.665',0),(7,55,'Java鍏ラ棬','NJAHZ2','绗竴闂ㄨ',NULL,1,'2026-01-13 02:28:40.789','2026-01-13 02:28:40.789',0),(8,57,'璧勬枡娴佺▼璇?,'8PQFBF','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 02:28:41.247','2026-01-13 02:28:41.247',0),(9,62,'浣滀笟娴佺▼璇?,'ENBM3Y','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:04:11.847','2026-01-13 03:04:11.847',0),(10,66,'Java鍏ラ棬','KNDRXH','绗竴闂ㄨ',NULL,1,'2026-01-13 03:04:16.657','2026-01-13 03:04:16.657',0),(11,68,'鑰冭瘯娴佺▼璇?,'ZKPWAS','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:04:17.300','2026-01-13 03:04:17.300',0),(12,70,'璧勬枡娴佺▼璇?,'JEKA2W','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:04:18.003','2026-01-13 03:04:18.003',0),(13,75,'浣滀笟娴佺▼璇?,'36RP8Y','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:18:59.727','2026-01-13 03:18:59.727',0),(14,81,'Java鍏ラ棬','GYKU6R','绗竴闂ㄨ',NULL,1,'2026-01-13 03:19:04.820','2026-01-13 03:19:04.820',0),(15,83,'鑰冭瘯娴佺▼璇?,'5CAJF5','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:19:05.330','2026-01-13 03:19:05.330',0),(16,85,'璧勬枡娴佺▼璇?,'EGVDEZ','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:19:06.009','2026-01-13 03:19:06.009',0),(17,90,'浣滀笟娴佺▼璇?,'ADNHXN','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:29:33.709','2026-01-13 03:29:33.709',0),(18,96,'Java鍏ラ棬','9GBKYQ','绗竴闂ㄨ',NULL,1,'2026-01-13 03:29:38.862','2026-01-13 03:29:38.862',0),(19,98,'鑰冭瘯娴佺▼璇?,'GCUMAR','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:29:39.369','2026-01-13 03:29:39.369',0),(20,100,'鏂囦欢娴佺▼璇?,'KD52K8','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:29:40.157','2026-01-13 03:29:40.157',0),(21,103,'璧勬枡娴佺▼璇?,'E4Y2FH','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:29:40.811','2026-01-13 03:29:40.811',0),(22,108,'浣滀笟娴佺▼璇?,'2ANQ4A','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:39:50.096','2026-01-13 03:39:50.096',0),(23,114,'Java鍏ラ棬','DKCRWB','绗竴闂ㄨ',NULL,1,'2026-01-13 03:39:55.413','2026-01-13 03:39:55.413',0),(24,116,'鑰冭瘯娴佺▼璇?,'SKN3JS','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:39:55.936','2026-01-13 03:39:55.936',0),(25,118,'鏂囦欢娴佺▼璇?,'XPHJCP','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:39:56.779','2026-01-13 03:39:56.779',0),(26,121,'璧勬枡娴佺▼璇?,'XN3GYR','娴嬭瘯璇剧▼',NULL,1,'2026-01-13 03:39:57.475','2026-01-13 03:39:57.475',0),(27,124,'test','VJDJQ3','',NULL,1,'2026-01-13 04:31:18.011','2026-01-13 04:31:18.011',0),(28,130,'Tmp Course','TX87CA','tmp',NULL,1,'2026-01-13 07:36:02.522','2026-01-13 07:36:02.522',0),(29,131,'Tmp Course','MPYH5Z','tmp',NULL,1,'2026-01-13 07:37:19.979','2026-01-13 07:37:19.979',0),(30,132,'Tmp Course','96MH6V','tmp',NULL,1,'2026-01-13 07:39:27.878','2026-01-13 07:39:27.878',0),(31,133,'Tmp Course','SNRRW2','tmp',NULL,1,'2026-01-13 08:00:22.381','2026-01-13 08:00:22.382',0),(32,134,'Tmp Course','G847GW','tmp',NULL,1,'2026-01-13 08:00:32.358','2026-01-13 08:00:32.358',0);
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_grade_config`
--

DROP TABLE IF EXISTS `course_grade_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_grade_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `course_id` bigint NOT NULL COMMENT '鐠囧墽鈻糏D',
  `weight_task` int NOT NULL DEFAULT '30' COMMENT '娴犺濮熼弶鍐櫢閿?-100閿?,
  `weight_assignment` int NOT NULL DEFAULT '30' COMMENT '娴ｆ粈绗熼弶鍐櫢閿?-100閿?,
  `weight_exam` int NOT NULL DEFAULT '40' COMMENT '閼板啳鐦弶鍐櫢閿?-100閿?,
  `created_by` bigint NOT NULL COMMENT '闁板秶鐤嗛懓鍜冪礄閼颁礁绗€閿?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_grade_config` (`course_id`),
  KEY `idx_course_grade_config_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐠囧墽鈻奸幋鎰摋閺夊啴鍣搁柊宥囩枂鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_grade_config`
--

LOCK TABLES `course_grade_config` WRITE;
/*!40000 ALTER TABLE `course_grade_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_grade_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_member`
--

DROP TABLE IF EXISTS `course_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `member_role` tinyint NOT NULL DEFAULT '1' COMMENT '鎴愬憳瑙掕壊锛?瀛︾敓 2鍔╂暀/鑰佸笀锛堥鐣欙級',
  `joined_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍔犲叆鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_member` (`course_id`,`user_id`),
  KEY `idx_course_member_course_id` (`course_id`),
  KEY `idx_course_member_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼鎴愬憳琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_member`
--

LOCK TABLES `course_member` WRITE;
/*!40000 ALTER TABLE `course_member` DISABLE KEYS */;
INSERT INTO `course_member` VALUES (1,1,22,1,'2026-01-13 01:25:30.379',0),(2,2,29,1,'2026-01-13 01:27:36.219',1),(3,3,38,1,'2026-01-13 02:14:09.035',1),(4,4,43,1,'2026-01-13 02:16:15.959',0),(5,5,47,1,'2026-01-13 02:16:20.334',1),(6,6,52,1,'2026-01-13 02:28:36.711',0),(7,7,56,1,'2026-01-13 02:28:40.806',1),(8,8,58,1,'2026-01-13 02:28:41.277',0),(9,9,63,1,'2026-01-13 03:04:11.901',0),(10,10,67,1,'2026-01-13 03:04:16.681',1),(11,11,69,1,'2026-01-13 03:04:17.341',0),(12,12,71,1,'2026-01-13 03:04:18.034',0),(13,13,76,1,'2026-01-13 03:18:59.780',0),(14,14,82,1,'2026-01-13 03:19:04.840',1),(15,15,84,1,'2026-01-13 03:19:05.366',0),(16,16,86,1,'2026-01-13 03:19:06.043',0),(17,17,91,1,'2026-01-13 03:29:33.766',0),(18,18,97,1,'2026-01-13 03:29:38.885',1),(19,19,99,1,'2026-01-13 03:29:39.396',0),(20,20,101,1,'2026-01-13 03:29:40.183',0),(21,21,104,1,'2026-01-13 03:29:40.839',0),(22,22,109,1,'2026-01-13 03:39:50.156',0),(23,23,115,1,'2026-01-13 03:39:55.433',1),(24,24,117,1,'2026-01-13 03:39:55.969',0),(25,25,119,1,'2026-01-13 03:39:56.812',0),(26,26,122,1,'2026-01-13 03:39:57.509',0),(27,27,123,1,'2026-01-13 04:38:11.378',0);
/*!40000 ALTER TABLE `course_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID',
  `title` varchar(200) NOT NULL COMMENT '鑰冭瘯鏍囬',
  `description` varchar(500) DEFAULT NULL COMMENT '鑰冭瘯璇存槑',
  `start_at` datetime(3) NOT NULL COMMENT '寮€濮嬫椂闂?,
  `end_at` datetime(3) NOT NULL COMMENT '缁撴潫鏃堕棿',
  `duration_minutes` int NOT NULL DEFAULT '60' COMMENT '鏃堕暱锛堝垎閽燂級',
  `total_score` int NOT NULL DEFAULT '100' COMMENT '鎬诲垎',
  `created_by` bigint NOT NULL COMMENT '鍒涘缓鑰咃紙鑰佸笀锛?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_exam_course_id` (`course_id`),
  KEY `idx_exam_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑰冭瘯琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES (1,11,'绗竴娆¤€冭瘯','璇疯鐪熶綔绛?,'2026-01-13 03:03:17.357','2026-01-13 03:34:17.357',30,10,68,'2026-01-13 03:04:17.381','2026-01-13 03:04:17.381',0),(2,15,'绗竴娆¤€冭瘯','璇疯鐪熶綔绛?,'2026-01-13 03:18:05.377','2026-01-13 03:49:05.377',30,10,83,'2026-01-13 03:19:05.403','2026-01-13 03:19:05.403',0),(3,19,'绗竴娆¤€冭瘯','璇疯鐪熶綔绛?,'2026-01-13 03:28:39.406','2026-01-13 03:59:39.406',30,10,98,'2026-01-13 03:29:39.426','2026-01-13 03:29:39.426',0),(4,24,'绗竴娆¤€冭瘯','璇疯鐪熶綔绛?,'2026-01-13 03:38:55.980','2026-01-13 04:09:55.980',30,10,116,'2026-01-13 03:39:56.000','2026-01-13 03:39:56.000',0);
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_attempt`
--

DROP TABLE IF EXISTS `exam_attempt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_attempt` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `exam_id` bigint NOT NULL COMMENT '鑰冭瘯ID',
  `student_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '鐘舵€侊細1杩涜涓?2宸叉彁浜?,
  `start_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '寮€濮嬩綔绛旀椂闂?,
  `submit_at` datetime(3) DEFAULT NULL COMMENT '鎻愪氦鏃堕棿',
  `total_score` int NOT NULL DEFAULT '0' COMMENT '寰楀垎',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_attempt` (`exam_id`,`student_id`),
  KEY `idx_exam_attempt_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑰冭瘯浣滅瓟璁板綍琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_attempt`
--

LOCK TABLES `exam_attempt` WRITE;
/*!40000 ALTER TABLE `exam_attempt` DISABLE KEYS */;
INSERT INTO `exam_attempt` VALUES (1,1,69,2,'2026-01-13 03:04:17.470','2026-01-13 03:04:17.470',10,'2026-01-13 03:04:17.470','2026-01-13 03:04:17.470',0),(2,2,84,2,'2026-01-13 03:19:05.472','2026-01-13 03:19:05.472',10,'2026-01-13 03:19:05.472','2026-01-13 03:19:05.472',0),(3,3,99,2,'2026-01-13 03:29:39.491','2026-01-13 03:29:39.491',10,'2026-01-13 03:29:39.491','2026-01-13 03:29:39.491',0),(4,4,117,2,'2026-01-13 03:39:56.074','2026-01-13 03:39:56.074',10,'2026-01-13 03:39:56.074','2026-01-13 03:39:56.074',0);
/*!40000 ALTER TABLE `exam_attempt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_attempt_answer`
--

DROP TABLE IF EXISTS `exam_attempt_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_attempt_answer` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `attempt_id` bigint NOT NULL COMMENT '浣滅瓟ID',
  `question_id` bigint NOT NULL COMMENT '棰樼洰ID',
  `answer_text` varchar(200) DEFAULT NULL COMMENT '绛旀',
  `is_correct` tinyint NOT NULL DEFAULT '0' COMMENT '鏄惁姝ｇ‘锛?鏄?0鍚?,
  `score` int NOT NULL DEFAULT '0' COMMENT '寰楀垎',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_attempt_answer` (`attempt_id`,`question_id`),
  KEY `idx_exam_attempt_answer_attempt_id` (`attempt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑰冭瘯绛旈琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_attempt_answer`
--

LOCK TABLES `exam_attempt_answer` WRITE;
/*!40000 ALTER TABLE `exam_attempt_answer` DISABLE KEYS */;
INSERT INTO `exam_attempt_answer` VALUES (1,1,1,'A',1,5,'2026-01-13 03:04:17.470',0),(2,1,2,'TRUE',1,5,'2026-01-13 03:04:17.470',0),(3,2,3,'A',1,5,'2026-01-13 03:19:05.472',0),(4,2,4,'TRUE',1,5,'2026-01-13 03:19:05.472',0),(5,3,5,'A',1,5,'2026-01-13 03:29:39.491',0),(6,3,6,'TRUE',1,5,'2026-01-13 03:29:39.491',0),(7,4,7,'A',1,5,'2026-01-13 03:39:56.074',0),(8,4,8,'TRUE',1,5,'2026-01-13 03:39:56.074',0);
/*!40000 ALTER TABLE `exam_attempt_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_question`
--

DROP TABLE IF EXISTS `exam_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `exam_id` bigint NOT NULL COMMENT '鑰冭瘯ID',
  `question_type` tinyint NOT NULL COMMENT '棰樺瀷锛?鍗曢€?3鍒ゆ柇锛堟渶灏忓疄鐜帮級',
  `question_text` text NOT NULL COMMENT '棰樺共',
  `option_a` varchar(500) DEFAULT NULL COMMENT '閫夐」A',
  `option_b` varchar(500) DEFAULT NULL COMMENT '閫夐」B',
  `option_c` varchar(500) DEFAULT NULL COMMENT '閫夐」C',
  `option_d` varchar(500) DEFAULT NULL COMMENT '閫夐」D',
  `correct_answer` varchar(50) DEFAULT NULL COMMENT '姝ｇ‘绛旀',
  `score` int NOT NULL DEFAULT '5' COMMENT '鍒嗗€?,
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '鎺掑簭鍙?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_exam_question_exam_id` (`exam_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑰冭瘯棰樼洰琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_question`
--

LOCK TABLES `exam_question` WRITE;
/*!40000 ALTER TABLE `exam_question` DISABLE KEYS */;
INSERT INTO `exam_question` VALUES (1,1,1,'Java 鏄粈涔堬紵','缂栫▼璇█','鏁版嵁搴?,'鎿嶄綔绯荤粺','娴忚鍣?,'A',5,1,'2026-01-13 03:04:17.401',0),(2,1,3,'Spring Boot 鍙互鐢ㄤ簬蹇€熸瀯寤哄悗绔湇鍔°€傚垽鏂閿欙紙TRUE/FALSE锛?,NULL,NULL,NULL,NULL,'TRUE',5,2,'2026-01-13 03:04:17.416',0),(3,2,1,'Java 鏄粈涔堬紵','缂栫▼璇█','鏁版嵁搴?,'鎿嶄綔绯荤粺','娴忚鍣?,'A',5,1,'2026-01-13 03:19:05.414',0),(4,2,3,'Spring Boot 鍙互鐢ㄤ簬蹇€熸瀯寤哄悗绔湇鍔°€傚垽鏂閿欙紙TRUE/FALSE锛?,NULL,NULL,NULL,NULL,'TRUE',5,2,'2026-01-13 03:19:05.423',0),(5,3,1,'Java 鏄粈涔堬紵','缂栫▼璇█','鏁版嵁搴?,'鎿嶄綔绯荤粺','娴忚鍣?,'A',5,1,'2026-01-13 03:29:39.435',0),(6,3,3,'Spring Boot 鍙互鐢ㄤ簬蹇€熸瀯寤哄悗绔湇鍔°€傚垽鏂閿欙紙TRUE/FALSE锛?,NULL,NULL,NULL,NULL,'TRUE',5,2,'2026-01-13 03:29:39.446',0),(7,4,1,'Java 鏄粈涔堬紵','缂栫▼璇█','鏁版嵁搴?,'鎿嶄綔绯荤粺','娴忚鍣?,'A',5,1,'2026-01-13 03:39:56.013',0),(8,4,3,'Spring Boot 鍙互鐢ㄤ簬蹇€熸瀯寤哄悗绔湇鍔°€傚垽鏂閿欙紙TRUE/FALSE锛?,NULL,NULL,NULL,NULL,'TRUE',5,2,'2026-01-13 03:39:56.024',0);
/*!40000 ALTER TABLE `exam_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_object`
--

DROP TABLE IF EXISTS `file_object`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_object` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `original_name` varchar(255) NOT NULL COMMENT '鍘熷鏂囦欢鍚?,
  `stored_path` varchar(300) NOT NULL COMMENT '瀛樺偍鐩稿璺緞锛堝悗绔湰鍦板瓨鍌級',
  `content_type` varchar(100) DEFAULT NULL COMMENT 'MIME绫诲瀷',
  `file_ext` varchar(20) DEFAULT NULL COMMENT '鎵╁睍鍚?,
  `file_size` bigint NOT NULL DEFAULT '0' COMMENT '鏂囦欢澶у皬锛堝瓧鑺傦級',
  `sha256` varchar(64) DEFAULT NULL COMMENT 'sha256锛堝彲閫夛紝鐢ㄤ簬鍘婚噸/鏍￠獙锛?,
  `created_by` bigint DEFAULT NULL COMMENT '涓婁紶鑰匢D',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_file_object_created_by` (`created_by`),
  KEY `idx_file_object_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏂囦欢瀵硅薄琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_object`
--

LOCK TABLES `file_object` WRITE;
/*!40000 ALTER TABLE `file_object` DISABLE KEYS */;
INSERT INTO `file_object` VALUES (1,'a.pdf','test/a.pdf','application/pdf','pdf',123,NULL,NULL,'2026-01-13 02:28:41.286',0),(2,'a.pdf','test/a.pdf','application/pdf','pdf',123,NULL,NULL,'2026-01-13 03:04:18.044',0),(3,'a.pdf','test/a.pdf','application/pdf','pdf',123,NULL,NULL,'2026-01-13 03:19:06.054',0),(4,'hello.txt','2026/01/0839135a-a647-452d-a796-3e6a7ce7b77b.txt','text/plain','txt',20,'935bb89bd54b875ea4e2f54f9c631d9eb760457afc489f70eff3cd1ff6bf807a',100,'2026-01-13 03:29:40.254',0),(5,'a.pdf','test/a.pdf','application/pdf','pdf',123,NULL,NULL,'2026-01-13 03:29:40.849',0),(6,'hello.txt','2026/01/e7175ba5-8779-49ea-a6ca-f4c37eb33883.txt','text/plain','txt',20,'935bb89bd54b875ea4e2f54f9c631d9eb760457afc489f70eff3cd1ff6bf807a',118,'2026-01-13 03:39:56.900',0),(7,'a.pdf','test/a.pdf','application/pdf','pdf',123,NULL,NULL,'2026-01-13 03:39:57.519',0),(8,'鏈嶈缃戝簵杩涢攢瀛樼郴缁?pptx','2026/01/9bf69bba-ad96-4f24-a650-6d2bdbbb1d7d.pptx','application/vnd.openxmlformats-officedocument.presentationml.presentation','pptx',19979762,'51faba11d14f826a7a74b61d113484d47d90de75c7ce313e38307946f4c565e3',124,'2026-01-13 10:08:15.852',0),(9,'鏈嶈缃戝簵杩涢攢瀛樼郴缁?pptx','2026/01/b6e4d22a-e85a-4223-951f-c9e3e3043588.pptx','application/vnd.openxmlformats-officedocument.presentationml.presentation','pptx',19979762,'51faba11d14f826a7a74b61d113484d47d90de75c7ce313e38307946f4c565e3',124,'2026-01-13 10:08:27.216',0);
/*!40000 ALTER TABLE `file_object` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `material`
--

DROP TABLE IF EXISTS `material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `course_id` bigint NOT NULL COMMENT '璇剧▼ID',
  `file_id` bigint NOT NULL COMMENT '鏂囦欢ID',
  `title` varchar(200) NOT NULL COMMENT '璧勬枡鏍囬',
  `material_type` tinyint NOT NULL COMMENT '璧勬枡绫诲瀷锛?PDF 2PPT 3DOCX 4鍏朵粬',
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '鎺掑簭鍙?,
  `created_by` bigint NOT NULL COMMENT '鍒涘缓鑰咃紙鑰佸笀锛?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  KEY `idx_material_course_id` (`course_id`),
  KEY `idx_material_file_id` (`file_id`),
  KEY `idx_material_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璇剧▼璧勬枡琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material`
--

LOCK TABLES `material` WRITE;
/*!40000 ALTER TABLE `material` DISABLE KEYS */;
INSERT INTO `material` VALUES (1,8,1,'绗竴浠借祫鏂?,1,1,57,'2026-01-13 02:28:41.320','2026-01-13 02:28:41.320',0),(2,12,2,'绗竴浠借祫鏂?,1,1,70,'2026-01-13 03:04:18.072','2026-01-13 03:04:18.072',0),(3,16,3,'绗竴浠借祫鏂?,1,1,85,'2026-01-13 03:19:06.090','2026-01-13 03:19:06.090',0),(4,20,4,'鏂囦欢璧勬枡1',4,0,100,'2026-01-13 03:29:40.288','2026-01-13 03:29:40.288',0),(5,21,5,'绗竴浠借祫鏂?,1,1,103,'2026-01-13 03:29:40.873','2026-01-13 03:29:40.873',0),(6,25,6,'鏂囦欢璧勬枡1',4,0,118,'2026-01-13 03:39:56.932','2026-01-13 03:39:56.932',0),(7,26,7,'绗竴浠借祫鏂?,1,1,121,'2026-01-13 03:39:57.541','2026-01-13 03:39:57.541',0);
/*!40000 ALTER TABLE `material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `material_progress`
--

DROP TABLE IF EXISTS `material_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `material_id` bigint NOT NULL COMMENT '璧勬枡ID',
  `user_id` bigint NOT NULL COMMENT '瀛︾敓ID',
  `progress_percent` int NOT NULL DEFAULT '0' COMMENT '杩涘害鐧惧垎姣旓細0-100',
  `last_position` int NOT NULL DEFAULT '0' COMMENT '鏈€杩戜綅缃紙椤电爜/绉掓暟锛屽墠绔畾涔夛級',
  `last_study_at` datetime(3) DEFAULT NULL COMMENT '鏈€杩戝涔犳椂闂?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_progress` (`material_id`,`user_id`),
  KEY `idx_material_progress_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勬枡瀛︿範杩涘害琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material_progress`
--

LOCK TABLES `material_progress` WRITE;
/*!40000 ALTER TABLE `material_progress` DISABLE KEYS */;
INSERT INTO `material_progress` VALUES (1,1,58,30,12,'2026-01-13 02:28:41.382','2026-01-13 02:28:41.382','2026-01-13 02:28:41.382',0),(2,2,71,30,12,'2026-01-13 03:04:18.130','2026-01-13 03:04:18.130','2026-01-13 03:04:18.130',0),(3,3,86,30,12,'2026-01-13 03:19:06.147','2026-01-13 03:19:06.147','2026-01-13 03:19:06.147',0),(4,5,104,30,12,'2026-01-13 03:29:40.921','2026-01-13 03:29:40.921','2026-01-13 03:29:40.921',0),(5,7,122,30,12,'2026-01-13 03:39:57.587','2026-01-13 03:39:57.587','2026-01-13 03:39:57.587',0);
/*!40000 ALTER TABLE `material_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_bank_question`
--

DROP TABLE IF EXISTS `question_bank_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_bank_question` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `course_id` bigint NOT NULL COMMENT '鐠囧墽鈻糏D',
  `question_type` tinyint NOT NULL COMMENT '妫版ê鐎烽敍?閸楁洟? 2婢舵岸? 3閸掋倖鏌?4缁?鐡熼敍鍫?閻ｆ瑱绱?,
  `question_text` text NOT NULL COMMENT '妫版ê鍏?,
  `option_a` varchar(500) DEFAULT NULL COMMENT '闁銆岮',
  `option_b` varchar(500) DEFAULT NULL COMMENT '闁銆岯',
  `option_c` varchar(500) DEFAULT NULL COMMENT '闁銆岰',
  `option_d` varchar(500) DEFAULT NULL COMMENT '闁銆岲',
  `correct_answer` varchar(50) DEFAULT NULL COMMENT '濮濓絿鈥樼粵鏃€?閿涘牆?鐟欏倿?閿涙/B/C/D/TRUE/FALSE閿?,
  `score` int NOT NULL DEFAULT '5' COMMENT '閸掑棗?',
  `created_by` bigint NOT NULL COMMENT '閸掓稑缂撻懓鍜冪礄閼颁礁绗€閿?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  KEY `idx_question_bank_course_id` (`course_id`),
  KEY `idx_question_bank_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='妫版ê绨辨０妯兼窗鐞?绱欓幐澶?缁嬪绱?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_bank_question`
--

LOCK TABLES `question_bank_question` WRITE;
/*!40000 ALTER TABLE `question_bank_question` DISABLE KEYS */;
/*!40000 ALTER TABLE `question_bank_question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `perm_code` varchar(80) NOT NULL COMMENT '鏉冮檺缂栫爜锛屼緥濡?course:create',
  `perm_name` varchar(100) NOT NULL COMMENT '鏉冮檺鍚嶇О',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_permission_code` (`perm_code`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏉冮檺鐐硅〃';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (1,'auth:me','鏌ョ湅褰撳墠鐧诲綍淇℃伅','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(2,'course:create','鍒涘缓璇剧▼','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(3,'course:manage_members','绠＄悊璇剧▼鎴愬憳','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(4,'material:upload','涓婁紶璇剧▼璧勬枡','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(5,'material:progress','鎻愪氦瀛︿範杩涘害','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(6,'assignment:create','甯冪疆浣滀笟','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(7,'assignment:submit','鎻愪氦浣滀笟','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(8,'assignment:grade','鎵规敼浣滀笟','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(9,'exam:create','鍒涘缓鑰冭瘯','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(10,'exam:submit','鎻愪氦鑰冭瘯','2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0),(11,'chat:send','鍙戦€佹秷鎭?,'2026-01-13 00:13:35.406','2026-01-13 00:13:35.406',0);
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `role_code` varchar(30) NOT NULL COMMENT '瑙掕壊缂栫爜锛歋TUDENT/TEACHER/ADMIN',
  `role_name` varchar(50) NOT NULL COMMENT '瑙掕壊鍚嶇О',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='瑙掕壊琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'STUDENT','瀛︾敓','2026-01-13 00:13:35.393','2026-01-13 00:13:35.393',0),(2,'TEACHER','鑰佸笀','2026-01-13 00:13:35.393','2026-01-13 00:13:35.393',0),(3,'ADMIN','绠＄悊鍛?,'2026-01-13 00:13:35.393','2026-01-13 00:13:35.393',0);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `role_id` bigint NOT NULL COMMENT '瑙掕壊ID',
  `permission_id` bigint NOT NULL COMMENT '鏉冮檺ID',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_permission` (`role_id`,`permission_id`),
  KEY `idx_sys_role_permission_role_id` (`role_id`),
  KEY `idx_sys_role_permission_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='瑙掕壊鏉冮檺鍏宠仈琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (1,3,6,'2026-01-13 00:13:35.416',0),(2,3,8,'2026-01-13 00:13:35.416',0),(3,3,7,'2026-01-13 00:13:35.416',0),(4,3,1,'2026-01-13 00:13:35.416',0),(5,3,11,'2026-01-13 00:13:35.416',0),(6,3,2,'2026-01-13 00:13:35.416',0),(7,3,3,'2026-01-13 00:13:35.416',0),(8,3,9,'2026-01-13 00:13:35.416',0),(9,3,10,'2026-01-13 00:13:35.416',0),(10,3,5,'2026-01-13 00:13:35.416',0),(11,3,4,'2026-01-13 00:13:35.416',0),(16,2,6,'2026-01-13 00:13:35.430',0),(17,2,8,'2026-01-13 00:13:35.430',0),(18,2,1,'2026-01-13 00:13:35.430',0),(19,2,11,'2026-01-13 00:13:35.430',0),(20,2,2,'2026-01-13 00:13:35.430',0),(21,2,3,'2026-01-13 00:13:35.430',0),(22,2,9,'2026-01-13 00:13:35.430',0),(23,2,4,'2026-01-13 00:13:35.430',0),(31,1,7,'2026-01-13 00:13:35.440',0),(32,1,1,'2026-01-13 00:13:35.440',0),(33,1,11,'2026-01-13 00:13:35.440',0),(34,1,10,'2026-01-13 00:13:35.440',0),(35,1,5,'2026-01-13 00:13:35.440',0);
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `username` varchar(50) NOT NULL COMMENT '鐢ㄦ埛鍚嶏紙鍞竴锛?,
  `password_hash` varchar(100) NOT NULL COMMENT '瀵嗙爜鍝堝笇锛圔Crypt锛?,
  `nickname` varchar(50) NOT NULL COMMENT '鏄电О',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '澶村儚URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '鎵嬫満鍙凤紙鍙€夛級',
  `email` varchar(100) DEFAULT NULL COMMENT '閭锛堝彲閫夛級',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '鐘舵€侊細1鍚敤 0绂佺敤',
  `last_login_at` datetime(3) DEFAULT NULL COMMENT '鏈€鍚庣櫥褰曟椂闂?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '鏇存柊鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  KEY `idx_sys_user_status` (`status`),
  KEY `idx_sys_user_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','{INIT}','骞冲彴绠＄悊鍛?,NULL,NULL,NULL,1,NULL,'2026-01-13 00:13:35.448','2026-01-13 00:13:35.448',0),(2,'tea236676981','$2a$10$kTNfbi2DR8h7GEFjsLPkTOn2/.qC4Mai43F/fMtiFKOb/caABr1Ay','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 00:51:17.766','2026-01-13 00:51:17.766',0),(3,'admin236677998','$2a$10$dOWh7eSIz1K5Jxia0fXUD.xGjyq91vr7AcqPjx5S.TpdsuZlNC.ta','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 00:51:18.069','2026-01-13 00:51:18.069',0),(4,'tea237089511','$2a$10$rNkGWS.gw4FbZ4kmWtseBe5.w/6hDtFy5yM62TJYNgMKAf4rQMzuS','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 00:58:10.401','2026-01-13 00:58:10.401',0),(5,'admin237090720','$2a$10$JBEvI6uTZY0OLazrEWR.w.R3pnaCpN/5PQGqTaOQWTo69piPnDAWC','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 00:58:10.805','2026-01-13 00:58:10.805',0),(6,'stu_api_237582733','$2a$10$Xv/55qF4SYiuDy7omyx9Vu8h1qEW.PdNWV3dTvOJmmO/g/jT/Yo9S','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:06:22.830','2026-01-13 01:06:22.830',0),(7,'admin_api_237583690','$2a$10$5ZuqsWhjSHI7WrQiYgs4cOaxn30bpAsl6RwI/cl3hnPXxifuNRsoy','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:06:23.767','2026-01-13 01:06:23.767',0),(8,'user_api_237583792','$2a$10$csZKg6hwqjcXzS0BvDb0sOcuNGEVbK.h3OKM.ah6BJGC0ZIMo3oNq','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:06:23.867','2026-01-13 01:06:23.867',0),(9,'tea237587018','$2a$10$DzWeTXSmh0wnEEdwWxZKk.eK42hH8E.Qlz3Mf3q9pVJPn4DQsB8bq','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:06:27.249','2026-01-13 01:06:27.249',0),(10,'admin237587420','$2a$10$n0H.2wVD6At.f47mqqEAeeSG6Flewqh7L3jLxGvRxk3WMeG9IkZoW','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 01:06:27.516','2026-01-13 01:06:27.516',0),(11,'stu_api_237670070','$2a$10$9.Hl9wWTL75Q83pm9jkAYuYJ.7//VtN7plZf8s5nzNsxxNKuFkmXS','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:07:50.168','2026-01-13 01:07:50.168',0),(12,'admin_api_237670998','$2a$10$9XNCFpBh2EaSH9Sa7IyEkuyg2lDOWbpoQOckPDDJ7UBaHYsvSXIea','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:07:51.079','2026-01-13 01:07:51.079',0),(13,'user_api_237671108','$2a$10$3Iix2Tv4VY/38JI9welt7OMBq/ROerI2CYpo97ypPEpivXocRMUey','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 01:07:51.193','2026-01-13 01:07:51.726',0),(14,'tea237675051','$2a$10$f4OaXuLIH5rQ/fbiVcFvJevkTPgdxzv4VWypeLvXOH/PnakfsR926','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:07:55.211','2026-01-13 01:07:55.211',0),(15,'admin237675362','$2a$10$48Y/zvjKpiv9KLF022scD.MkMDs.jfstZSX406eZdcxbgTHzVfOMC','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 01:07:55.442','2026-01-13 01:07:55.442',0),(16,'stu_api_238725196','$2a$10$LNNY796MmdIlTl4b9WyBgeoympqe70fECWcZJPGRXx2gLc5ot.iHG','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:25:25.277','2026-01-13 01:25:25.277',0),(17,'admin_api_238726057','$2a$10$gFVy6mcQ0NZOeEiQccbSbeckcX8bw/W7Ie30fW/0CwIexAkabwVb.','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:25:26.143','2026-01-13 01:25:26.143',0),(18,'user_api_238726181','$2a$10$3Zeu.VOZu3wwMSBQNSf2k.Dc7n/uh9hrro4ND1.0k8TOjyJZUX97S','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 01:25:26.254','2026-01-13 01:25:26.624',0),(19,'tea238729590','$2a$10$U2.I72sfU5KwcBc/GePfteIw..cGfjfTXtcLM1Jcx7OvFgaq90OMC','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:25:29.755','2026-01-13 01:25:29.755',0),(20,'admin238729923','$2a$10$is.S0ISSin.UPdWSCfW3HufbMmUQeKIw7zRzhyOnxMYfvgfpO4TNq','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 01:25:30.014','2026-01-13 01:25:30.014',0),(21,'tea_course_238730092','$2a$10$fGI1AeNwJVHXBa7fmqX0/O4EGPDtgNdXYSAQ9K4dv0BoZAe3v.9XS','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:25:30.181','2026-01-13 01:25:30.181',0),(22,'stu_course_238730206','$2a$10$21O8I5zplfzfVlecQ2ChwOeetGLtiPlzWWaTmLsqpdt29gPjjIrzy','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:25:30.289','2026-01-13 01:25:30.289',0),(23,'stu_api_238851146','$2a$10$mDS90HtvoIp3Z7G9rXqT/O581b4RTkl3DVMBuMcCm9zGp04jwmgj.','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:27:31.238','2026-01-13 01:27:31.238',0),(24,'admin_api_238852152','$2a$10$.z.nwbmwbnBe2fcGcW4A6e.xurI4NIW2Ns4U1r6bVdLq2QoSlWFBC','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:27:32.233','2026-01-13 01:27:32.233',0),(25,'user_api_238852260','$2a$10$LMz3184zw1/Rj4dKiiJpmupbSKDPQBYgyBrCXVhzQyCUA2rqkmZri','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 01:27:32.343','2026-01-13 01:27:32.747',0),(26,'tea238855516','$2a$10$1q/HJ5X2jQ/iWxBT2m0Fi.Tfb0I3pPztgPTLGjJeRVis9o3UJ/FAe','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:27:35.666','2026-01-13 01:27:35.666',0),(27,'admin238855829','$2a$10$dXfPj9FuS6RXAirsoc1Gn.XenxlKgeZ.nxORttlF6h.9QGia8fZOO','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 01:27:35.904','2026-01-13 01:27:35.904',0),(28,'tea_course_238855968','$2a$10$hIKzOYyA8ZJ1fFPYcoJx/eynadw4y.7RQsoYlI2eih5hNkVfs8jZW','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:27:36.044','2026-01-13 01:27:36.044',0),(29,'stu_course_238856066','$2a$10$P2ZmjU6rjz0NMJT0eN72EevTFudlFuB6s4KF8guRlkmR/0z9X/kWC','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 01:27:36.143','2026-01-13 01:27:36.143',0),(30,'stu_api_241642602','$2a$10$yETBtcYazz6GfTDOfBj/2OHU1AElo3P7JfzPrk4bU4G/DbrVnd.2.','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:02.706','2026-01-13 02:14:02.706',0),(31,'admin_api_241643641','$2a$10$CwEaVw/cZSgI93.2htZ6lu1Hg0v1Rvso.2tCzBZjwLO8KoenFaJOO','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:03.726','2026-01-13 02:14:03.726',0),(32,'user_api_241643762','$2a$10$squhad.VgvE3J2JDy3u9B.u2w.feUOmia6SRz9Rh816jqY4Rui9My','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 02:14:03.841','2026-01-13 02:14:04.226',0),(33,'t_assign_1','$2a$10$ArryPKWcHM.c9coe.7JMKOsbtUOjQfCOCGKYHPCVVnScLV6u27H5.','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:04.510','2026-01-13 02:14:04.510',0),(34,'s_assign_1','$2a$10$vbb94NHW8npaOdfxHPSPT.qsfut1cTFkqfEeHpawS0cwQa9Wo1jUO','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:04.764','2026-01-13 02:14:04.764',0),(35,'tea241648257','$2a$10$.Um7Ph93snikBTnM7SngOeYK58LSwc7FnaFu6Cp4aXCYF8JdZgY22','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:08.425','2026-01-13 02:14:08.425',0),(36,'admin241648592','$2a$10$xnv7LUmd5WGra/DzenagtOHC/8rfQBjtWsHDEyahpJzQDq3PazR2u','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:08.675','2026-01-13 02:14:08.675',0),(37,'tea_course_241648734','$2a$10$Kb7PGbLboBCkfyRTbEAo9uvf5Pez2xpZu.LEiggxPwuOYF17bIVLS','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:08.819','2026-01-13 02:14:08.819',0),(38,'stu_course_241648843','$2a$10$Y1iSGzofw3AbaFNVRHp1nOOC9u7Lk.RRyZJ9T.CzlrebeoDQF2ody','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:14:08.930','2026-01-13 02:14:08.930',0),(39,'stu_api_241773546','$2a$10$DvvC8Q3704cE8ItKBz.ipOIv.Lg.8Auqw7PSBZVHkiHOW0wWbOQiK','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:13.652','2026-01-13 02:16:13.652',0),(40,'admin_api_241774572','$2a$10$/UNRQGVfmuN1hp7Y2iD6TeyBNSfNoclETwoq9On/brYVRCOHh91g2','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:14.654','2026-01-13 02:16:14.654',0),(41,'user_api_241774690','$2a$10$j.QbBClBMAlCOMxKexnFWO3v/irGWmOUQ0yRL1DDrADYP2hM6kTri','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 02:16:14.773','2026-01-13 02:16:15.155',0),(42,'t_assign_241775290','$2a$10$IHSniLxnwliLTnTa8r6ab.03uSn5qPrKLCVcwSupQZAjXyY048gam','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:15.453','2026-01-13 02:16:15.453',0),(43,'s_assign_241775608','$2a$10$MG3rS2/Sch2aJYJpww8d6ewgGF9eXEkJd09BjoFUpfKinShmtNJy2','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:15.718','2026-01-13 02:16:15.718',0),(44,'tea241779605','$2a$10$4iwVQ1hN.HDgwv6BQUF1eeVlW0IYJH3HmTQKfRSbY4bvj0.krjkfC','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:19.756','2026-01-13 02:16:19.756',0),(45,'admin241779947','$2a$10$kA.pwElt2FNJTVp7HTOjf.pwfT70kyHOOeuOkOiIc0Pij3xaRQ8ii','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:20.026','2026-01-13 02:16:20.026',0),(46,'tea_course_241780080','$2a$10$hLv3WqjOld4Erv8V1ZS6m.EGzBU0j/8IJ4dScNC1zWyWZvoNwythi','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:20.159','2026-01-13 02:16:20.159',0),(47,'stu_course_241780182','$2a$10$Rb.lzQqGgf6BYcS6N38.8uzhW5L8jcrOoxZVUTKqJlC0nWIcLuTQC','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:16:20.260','2026-01-13 02:16:20.260',0),(48,'stu_api_242514581','$2a$10$6BiOW8zXV0AY.2I8yN7PIeHTAmv9HinDDK96iSPNcdITrRs3wLFc6','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:34.658','2026-01-13 02:28:34.658',0),(49,'admin_api_242515409','$2a$10$jSzfbFbuCVF8agh3vLIhTO4.c3kZGDOqwWEQFBm.jdDAxw7QXxJzG','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:35.482','2026-01-13 02:28:35.482',0),(50,'user_api_242515519','$2a$10$lIf3TIhQUQzxa.cmCuPAq.y3h3EGK6FFWX2tQ8lxnxvpulEIWOeFG','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 02:28:35.600','2026-01-13 02:28:35.990',0),(51,'t_assign_242516117','$2a$10$7t1U4XBQKRh9Li5HoDnekO3J5HXUVL95PGdi1cgwwxHFNQCjOZms.','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:36.257','2026-01-13 02:28:36.257',0),(52,'s_assign_242516400','$2a$10$.tqthwf9Sske4Z1HwgbaievY/MGBV9KEJuJdFwLP5N.cmbwcRXTZG','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:36.500','2026-01-13 02:28:36.500',0),(53,'tea242520164','$2a$10$iH3ONg7fepCm7lUyo5F9fuSKnBxu0kZ/N20wzC3kSs95BgAJ9Xu0y','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:40.307','2026-01-13 02:28:40.307',0),(54,'admin242520458','$2a$10$ewGYuATUBtALH5ZjV1pqn.jXsfgBxMVhc.FZJC018ffiIdUsJVUAu','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:40.534','2026-01-13 02:28:40.534',0),(55,'tea_course_242520584','$2a$10$k4oiq/jNjy3cn2/C89vIF.FZuNTnBQjYcXED5wLaRyo2p1Rjs8eu2','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:40.660','2026-01-13 02:28:40.660',0),(56,'stu_course_242520680','$2a$10$Z5w7yseOcmNuZSlvKrW10.LJwCbxioxq2Ra4wiANdzm53m/Q9qTj2','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:40.761','2026-01-13 02:28:40.761',0),(57,'t_mat_242520866','$2a$10$uIFSGFyk12HY2P2RVc6f/ufksS1iRDeEb6fwdTKplmaQ6RUAou90u','鑰佸笀璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:40.941','2026-01-13 02:28:40.941',0),(58,'s_mat_242521046','$2a$10$evODT/YGh59mf1E1eqsvk.VDWL.iOJItVM08Uo0pLN79zKIRnJxKG','瀛︾敓璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 02:28:41.132','2026-01-13 02:28:41.132',0),(59,'stu_api_244649518','$2a$10$DsqWe35wMRWnDXvqHdar5eAzE65LOPkPr2e60ZDZDbfngHAvCqviW','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:09.608','2026-01-13 03:04:09.608',0),(60,'admin_api_244650545','$2a$10$5Ak3CbMNpu/ohUoXA9O09ezp3WWhjzAx/4HT6sE0zxzTQ6js..t12','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:10.630','2026-01-13 03:04:10.630',0),(61,'user_api_244650655','$2a$10$Eo8YWs1T6tzYBqVM5ERmEOByhJWhbF65Oj5HaQa1SxeAP68Lt4Po.','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 03:04:10.734','2026-01-13 03:04:11.143',0),(62,'t_assign_244651277','$2a$10$eO5.BH3MGJvcHTWn4A78XeZluhKTfg2oiqBxnokY4rMUqGlpIL436','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:11.442','2026-01-13 03:04:11.442',0),(63,'s_assign_244651588','$2a$10$4BCosaId4CFn.l4lfIlxqu54rKQbCsUflk3q4/Kcm5ByDdPhGfxgC','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:11.691','2026-01-13 03:04:11.691',0),(64,'tea244655944','$2a$10$zKVyxACK6vqDcIA7Z00tO.UxBjLCMiTJbqKn8/t2F4TVR/yBVizB.','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:16.094','2026-01-13 03:04:16.094',0),(65,'admin244656262','$2a$10$gZ96svl8F0uwl3bJ0qajpuQBywI3.mB9c0UlH1BWg473P6VZx6TfK','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:16.347','2026-01-13 03:04:16.347',0),(66,'tea_course_244656409','$2a$10$aWMDifkU8P5M68D0kYJITuzrQ840cp67B8dgKBjPHmNuVTqsP1XUC','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:16.500','2026-01-13 03:04:16.500',0),(67,'stu_course_244656534','$2a$10$G5PuA57Qwy/prS77W4sCMeykgli2iPgUkYnK5nseekcb3Ri0zXsBO','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:16.620','2026-01-13 03:04:16.620',0),(68,'t_exam_244656779','$2a$10$tkvQHrwisBPWArk.evbqGurHaAhZhJF1o8yLtU3CtGfZXz04IWHK2','鑰佸笀鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:16.867','2026-01-13 03:04:16.867',0),(69,'s_exam_244657066','$2a$10$8VkcztmlYMnUqubOHb.JU.QLOCYWgoWjwvXW4r7/oSf18m4ueSg0m','瀛︾敓鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:17.161','2026-01-13 03:04:17.161',0),(70,'t_mat_244657561','$2a$10$PDtOr67nU7JB49ZsosLxF.6qQaIge69O1eVTCXB6te7y6G0Mw2UcO','鑰佸笀璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:17.660','2026-01-13 03:04:17.660',0),(71,'s_mat_244657782','$2a$10$o5KSyN0Pxw0b/fzVl9X8tOAfnSoQniRMLksFNuvpz1GaIzm.s2Ih.','瀛︾敓璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:04:17.873','2026-01-13 03:04:17.873',0),(72,'stu_api_245537218','$2a$10$MlYs4MJTjMBXjygsaO8JDe8QQAjEzeh8SEGG0l5tjj737jJS1dyvm','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:18:57.342','2026-01-13 03:18:57.342',0),(73,'admin_api_245538386','$2a$10$rV93eFDQfKgtDn.fy8EcyuqyhBFoTVElaSAMXfec2ARUZ5Z6dXWoG','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:18:58.470','2026-01-13 03:18:58.470',0),(74,'user_api_245538494','$2a$10$PZvznl2jr0g33.8xHdux/ubL3MADWhLQk3c7gg2Gj495Gn5n.eAjO','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 03:18:58.581','2026-01-13 03:18:59.020',0),(75,'t_assign_245539160','$2a$10$39ZE7ux6nbF.uhyqCsI0XeKxGOQU154ceDE6J5UKMwISXYyDSyGnC','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:18:59.320','2026-01-13 03:18:59.320',0),(76,'s_assign_245539480','$2a$10$bd1mw2bUUwEecvYlq8XFd.Tc8B0PSBWz8/xE2pa78h7qjweyQ1Gly','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:18:59.579','2026-01-13 03:18:59.579',0),(77,'tea245543538','$2a$10$m8mVEyJ3heBfokLSl3LkNemdOwME3ROCqze2do9gZqll84/1zAQ0.','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:03.676','2026-01-13 03:19:03.676',0),(78,'admin245543832','$2a$10$7OTQosmIBhzB8Vm8NgK2Euq6TddBZLvcRkW/J7EUyMjkxWBDEcwcO','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:03.915','2026-01-13 03:19:03.915',0),(79,'t_chat_245543970','$2a$10$NwEkmoFYN/cho7z06ymtouNrDH81tOKqH9BHg5UC.BsXvDzXhVafu','鑰佸笀鑱婂ぉA',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:04.060','2026-01-13 03:19:04.061',0),(80,'s_chat_245544179','$2a$10$Lpo1RP/ODXU/OKP2Yxj.quDd9yIErRL9nYszPurTwhhmjE0TzZNre','瀛︾敓鑱婂ぉB',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:04.268','2026-01-13 03:19:04.268',0),(81,'tea_course_245544587','$2a$10$HDMzf4YZnUvrF0ItLiUpaumw9BxbmIBf8K5xMLFjvTEQsY5EYBc3.','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:04.677','2026-01-13 03:19:04.677',0),(82,'stu_course_245544701','$2a$10$BoZNnJSK9gyPzOOtCAbs0ug2T8d2wjFkq9hqqhp1xgktcWXplOXOu','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:04.787','2026-01-13 03:19:04.787',0),(83,'t_exam_245544919','$2a$10$gQX9r0FSYPRGBCdrJEoU6O5o4ES1iRLr/N3uJY2KDya3LWuQ2uM.m','鑰佸笀鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:05.008','2026-01-13 03:19:05.008',0),(84,'s_exam_245545119','$2a$10$djBt.cpu2dIL4qWFWpXkQeNtJtQWikGR9X9yxCarxqc2Lrd2tIyvy','瀛︾敓鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:05.203','2026-01-13 03:19:05.203',0),(85,'t_mat_245545552','$2a$10$HtTlMJ8L939s8dEnHDMrVeDIjV8kvS0a4TdCFXDJP1fZGFKYZBdyW','鑰佸笀璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:05.648','2026-01-13 03:19:05.648',0),(86,'s_mat_245545783','$2a$10$uhz.5.RA6qZgEV/1FCvcxe3wsNUCtCrDa7hP9W7RLzpQ4Bvfddyta','瀛︾敓璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:19:05.878','2026-01-13 03:19:05.878',0),(87,'stu_api_246171423','$2a$10$tWBGHNhptlYh6Aa.8xTfKeb3yhC0jlR2xvLt55T3lqsK3m1MU.bU.','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:31.517','2026-01-13 03:29:31.517',0),(88,'admin_api_246172354','$2a$10$6CI80j39gT6PPQVAggQM9.E/WYafK/ltylcGx0i5ugfWGFNWMDDUu','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:32.435','2026-01-13 03:29:32.435',0),(89,'user_api_246172469','$2a$10$vMpDOlsz5BLeSQpB3pDu3ebNm7MVRYFY3PjgwuJ7cFUGDKpWjR2Zu','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 03:29:32.548','2026-01-13 03:29:33.006',0),(90,'t_assign_246173152','$2a$10$vRJwfdeVwoL7rlYL0tUTUuIfna5w65EtB75RQlfNlcn4VWbkXttMi','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:33.311','2026-01-13 03:29:33.311',0),(91,'s_assign_246173459','$2a$10$1wcWm0O/HzxpfGES97OrcO1vwdWwIH7h9fGUEtmDmoJlXPSTGo8f6','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:33.553','2026-01-13 03:29:33.553',0),(92,'tea246177577','$2a$10$uO99xRvlXFMFL/TSrlQZEO9AHZGJ3nDhfAiiYTa0845m1LTmbj1C.','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:37.716','2026-01-13 03:29:37.716',0),(93,'admin246177872','$2a$10$Lh2sBJPFAdn.qEQdddbvPOibq2ivyl4ntA98wlN9ACKuR3yo3/7gS','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:37.958','2026-01-13 03:29:37.958',0),(94,'t_chat_246178022','$2a$10$QXDREsYvAFzkVH1bRCg7muEJZY7CR5.ZUIklZOZnICleSTlkasx..','鑰佸笀鑱婂ぉA',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:38.116','2026-01-13 03:29:38.116',0),(95,'s_chat_246178258','$2a$10$JLTzamI7shmnQivFDFdK3O9UdNTX4RezxSzb6y3n7Rb14HCmZmj3C','瀛︾敓鑱婂ぉB',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:38.344','2026-01-13 03:29:38.344',0),(96,'tea_course_246178635','$2a$10$sSvWyyGnIl/gdq0hWcXy3.0rdl22TF.FnGauKEt1.7vxeSwV2bGgK','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:38.718','2026-01-13 03:29:38.718',0),(97,'stu_course_246178746','$2a$10$cqjgUACxSqKjud6bT/bIEeCnglOtdGHGH78V0vk2EWZcEn3j5P/Wm','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:38.826','2026-01-13 03:29:38.826',0),(98,'t_exam_246178968','$2a$10$YPlXEiAKD58aRYf8nW3wHO4mc81jqDp/nnAOIh7hEZCVk1P2ZyV7K','鑰佸笀鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:39.053','2026-01-13 03:29:39.053',0),(99,'s_exam_246179162','$2a$10$nJYyelZ5Wl1WOXZWmEcmtuaF7eo7EbhK9MnROaTcI9Z03a04Rae6O','瀛︾敓鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:39.248','2026-01-13 03:29:39.248',0),(100,'t_file_246179563','$2a$10$/yph7ALLo2tuK33j/pAOOO8QEfeQzUb6452foeHmaPFHasfgI2uvW','鑰佸笀鏂囦欢A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:39.650','2026-01-13 03:29:39.650',0),(101,'s_file_246179770','$2a$10$md/07olyuwjtoD5mC1Dok.EaGzBBgO/V73I0OVKifQVdbwQHitlJK','瀛︾敓鏂囦欢B',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:39.857','2026-01-13 03:29:39.857',0),(102,'s_out_246179967','$2a$10$5lMve0Vi7YU1JFd8VpPpm.IwtXZRMeUQsrn9PkP1g9UH./WUfzmg2','瀛︾敓璺汉C',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:40.044','2026-01-13 03:29:40.044',0),(103,'t_mat_246180379','$2a$10$UgQZgm20ARaOJyYR9Y9ghOU1Pvvu9qa1i4zfUHU9/u4z85q0KNnra','鑰佸笀璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:40.472','2026-01-13 03:29:40.472',0),(104,'s_mat_246180595','$2a$10$fDlwiNOy7KdyXOKkkxt.CeoQHuUt5s1U7Ov7bCbdLtLZER3B9R3vW','瀛︾敓璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:29:40.685','2026-01-13 03:29:40.685',0),(105,'stu_api_246787560','$2a$10$4FtNuZGy77HfnX6kSZad9ehuj1TUfC0O9AqY9OUrx2EL/2NV4M4RG','瀛︾敓API娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:47.664','2026-01-13 03:39:47.664',0),(106,'admin_api_246788622','$2a$10$/bKHQO86zdJkctqlytu4euJ6AbChxGZO28Aw4HvJblyQtVY66Qoz2','绠＄悊鍛楢PI娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:48.713','2026-01-13 03:39:48.713',0),(107,'user_api_246788738','$2a$10$a0iG6SRgXKceupN0o9KG2eQV22z9Egtf4Vh9nqeVDoKxqSJDrWyQm','鐢ㄦ埛API娴嬭瘯',NULL,NULL,NULL,0,NULL,'2026-01-13 03:39:48.819','2026-01-13 03:39:49.318',0),(108,'t_assign_246789469','$2a$10$eqXgEcHNfu161rdJcyJQ5unFTvk5i3hDLw//aB/W3y.1thb...cyi','鑰佸笀A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:49.656','2026-01-13 03:39:49.656',0),(109,'s_assign_246789824','$2a$10$czEAgciPqxfozwk10TzFnuVvfinZKtpZ5j.8aMBpV6DfUv22iuLoy','瀛︾敓A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:49.931','2026-01-13 03:39:49.931',0),(110,'tea246794205','$2a$10$ub.ZEH6utf.bweEWWclWruojhYhBS0GDv6ZuXMkcSNtx9zxBdQqfO','鑰佸笀娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:54.342','2026-01-13 03:39:54.342',0),(111,'admin246794494','$2a$10$e6fRZHFsSudtoMd217I30.KvyFNFzsV1.rxxoONeeMY8NwEyZc3ie','绠＄悊鍛樻祴璇?,NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:54.574','2026-01-13 03:39:54.574',0),(112,'t_chat_246794627','$2a$10$zUT66xWC3pZPqIiEKKuPXeu5ZcYKQXaxuSSGBs3YzRPgI7tMzVqKK','鑰佸笀鑱婂ぉA',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:54.716','2026-01-13 03:39:54.716',0),(113,'s_chat_246794842','$2a$10$OLQ.E1aUOU0Q.HS0IunHfOwd6uMSPR6nzxfk88qisZ1ln2s4Oq8Jy','瀛︾敓鑱婂ぉB',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:54.918','2026-01-13 03:39:54.918',0),(114,'tea_course_246795198','$2a$10$25Lup0G72Y7JQdNfd7MGJuBUFaDPSkrUSfD1oj4rztSpgwo0nTm0O','鑰佸笀璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:55.278','2026-01-13 03:39:55.278',0),(115,'stu_course_246795299','$2a$10$sJbRmmCNJldAIjr9RFiVsOFbfEKRDJxQoa8myQeFajjNLR8vBA1Qu','瀛︾敓璇剧▼娴嬭瘯',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:55.376','2026-01-13 03:39:55.376',0),(116,'t_exam_246795516','$2a$10$teF2Zd5/wo.oqmy1X4nYAuOPiK35lGOO9KPFnms7kGyDK2I/Jv1Da','鑰佸笀鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:55.607','2026-01-13 03:39:55.607',0),(117,'s_exam_246795727','$2a$10$SQf1mq0nb3CXic8uSlehPe5mHuqw5LA/LmDbna5DRbqqQxbsyl/5C','瀛︾敓鑰冭瘯A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:55.808','2026-01-13 03:39:55.808',0),(118,'t_file_246796164','$2a$10$2TpeJ8twjd7tLkk0/qbCJu8gfYdWmpNiJoVZ8IdyM.ccRg3/1F8IO','鑰佸笀鏂囦欢A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:56.257','2026-01-13 03:39:56.257',0),(119,'s_file_246796370','$2a$10$55sdoz4X9TjHtdAzFabFf.uTKnIVNqHH.9L/0pAB1CYzaCoIeBpWy','瀛︾敓鏂囦欢B',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:56.450','2026-01-13 03:39:56.450',0),(120,'s_out_246796570','$2a$10$VunjURQBQ1Q0REt6qYZ5K.tnwrUfXiXlCYZ4FJ0Sh4Xx3TNyADr6q','瀛︾敓璺汉C',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:56.654','2026-01-13 03:39:56.654',0),(121,'t_mat_246797003','$2a$10$QkV3nzF4Cox.iWQBPiR9VO4Ns271tfkP6O3gRKNn0Bk/yjaXvmyuO','鑰佸笀璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:57.098','2026-01-13 03:39:57.098',0),(122,'s_mat_246797230','$2a$10$JzgGErzr30HtWFCfLg3eQuFlmjcAGHTOOeE.wOdSF0ItWv38ANih.','瀛︾敓璧勬枡A',NULL,NULL,NULL,1,NULL,'2026-01-13 03:39:57.332','2026-01-13 03:39:57.332',0),(123,'test','$2a$10$kfUU2AXOxkW80vcbmD2p.u7xzROdUL5kWGoOHJUlFCCUU4L/fP.XO','1',NULL,NULL,NULL,1,NULL,'2026-01-13 03:59:37.578','2026-01-13 03:59:37.578',0),(124,'laoshi','$2a$10$SREWI6ZOchTPGFZvXHf2ju1sZpMgseWiK5N/IsCcORx6CRa8s/gES','2',NULL,NULL,NULL,1,NULL,'2026-01-13 04:00:38.592','2026-01-13 04:00:38.592',0),(125,'t49907','$2a$10$Ntb/tMHPVt9I0rbpiDDRU.h7hVCFDKQuxGrqrRMEng26X42J9PrFy','??',NULL,NULL,NULL,1,NULL,'2026-01-13 06:41:35.397','2026-01-13 06:41:35.397',0),(126,'t2705','$2a$10$BbFX9gKrEDddI5E19zPpGurVXS/zA19Vp5jQHnIEerquXmlXCwRoi','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:34:33.525','2026-01-13 07:34:33.526',0),(127,'t15274','$2a$10$RtdvWDkp.aHMUiYRvjysYus5RyY4lfYxIkpizqJpxFpuq9QCta6IW','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:34:47.165','2026-01-13 07:34:47.165',0),(128,'t81954','$2a$10$npDtmUe7JYJ3psibOnoUleEKN/wFJWlQw1q4HDo.Rt9nw1NaapePu','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:35:14.560','2026-01-13 07:35:14.560',0),(129,'t52667','$2a$10$dcFX8r0XXj9h4KUwRkgVMeCUwKb/n2rzrSiJQpngCXa6pfcTHWs6.','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:35:41.563','2026-01-13 07:35:41.563',0),(130,'t97530','$2a$10$q1CeaVCqXPE8eYT8fHYMwezZ0e1LBsqKa884O.wHrql1aqGv2KeMq','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:36:01.804','2026-01-13 07:36:01.804',0),(131,'t9678','$2a$10$Vcs.xLrmSg8KuyYt/4fTtOBg33Jta5H/khSobvM8F2XtB781C9nE.','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:37:19.427','2026-01-13 07:37:19.427',0),(132,'t22149','$2a$10$qtE39.j7ziuyHBNGMcfHAuWURCrOhy/LljovaVEuZ7L7rWFmW0JHe','T',NULL,NULL,NULL,1,NULL,'2026-01-13 07:39:27.436','2026-01-13 07:39:27.436',0),(133,'t83936','$2a$10$lm7UH6NfTKZ838H3P7o0ieVn42vBwDafYf3KABKWn1ziWMBilAdXC','T',NULL,NULL,NULL,1,NULL,'2026-01-13 08:00:22.181','2026-01-13 08:00:22.181',0),(134,'t70799','$2a$10$LkZqcxqiqhgMUhxsgqOClOWFxJZPv5E8tWOH5bWUGC9Hng3zVVgwa','T',NULL,NULL,NULL,1,NULL,'2026-01-13 08:00:32.201','2026-01-13 08:00:32.201',0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '涓婚敭ID',
  `user_id` bigint NOT NULL COMMENT '鐢ㄦ埛ID',
  `role_id` bigint NOT NULL COMMENT '瑙掕壊ID',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '鍒涘缓鏃堕棿',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '閫昏緫鍒犻櫎锛?鍚?1鏄?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_role` (`user_id`,`role_id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛瑙掕壊鍏宠仈琛?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,3,'2026-01-13 00:13:35.459',0),(2,2,2,'2026-01-13 00:51:17.803',0),(3,3,3,'2026-01-13 00:51:18.085',0),(4,4,2,'2026-01-13 00:58:10.440',0),(5,5,3,'2026-01-13 00:58:10.824',0),(6,6,1,'2026-01-13 01:06:23.234',0),(7,7,3,'2026-01-13 01:06:23.783',0),(8,8,1,'2026-01-13 01:06:23.882',0),(9,9,2,'2026-01-13 01:06:27.267',0),(10,10,3,'2026-01-13 01:06:27.530',0),(11,11,1,'2026-01-13 01:07:50.543',0),(12,12,3,'2026-01-13 01:07:51.097',0),(13,13,1,'2026-01-13 01:07:51.212',0),(14,14,2,'2026-01-13 01:07:55.228',0),(15,15,3,'2026-01-13 01:07:55.457',0),(16,16,1,'2026-01-13 01:25:25.654',0),(17,17,3,'2026-01-13 01:25:26.170',0),(18,18,1,'2026-01-13 01:25:26.269',0),(19,19,2,'2026-01-13 01:25:29.774',0),(20,20,3,'2026-01-13 01:25:30.032',0),(21,21,2,'2026-01-13 01:25:30.195',0),(22,22,1,'2026-01-13 01:25:30.306',0),(23,23,1,'2026-01-13 01:27:31.660',0),(24,24,3,'2026-01-13 01:27:32.251',0),(25,25,1,'2026-01-13 01:27:32.358',0),(26,26,2,'2026-01-13 01:27:35.703',0),(27,27,3,'2026-01-13 01:27:35.921',0),(28,28,2,'2026-01-13 01:27:36.058',0),(29,29,1,'2026-01-13 01:27:36.157',0),(30,30,1,'2026-01-13 02:14:03.172',0),(31,31,3,'2026-01-13 02:14:03.751',0),(32,32,1,'2026-01-13 02:14:03.856',0),(33,33,2,'2026-01-13 02:14:04.531',0),(34,34,1,'2026-01-13 02:14:04.786',0),(35,35,2,'2026-01-13 02:14:08.442',0),(36,36,3,'2026-01-13 02:14:08.690',0),(37,37,2,'2026-01-13 02:14:08.834',0),(38,38,1,'2026-01-13 02:14:08.945',0),(39,39,1,'2026-01-13 02:16:14.096',0),(40,40,3,'2026-01-13 02:16:14.680',0),(41,41,1,'2026-01-13 02:16:14.789',0),(42,42,2,'2026-01-13 02:16:15.472',0),(43,43,1,'2026-01-13 02:16:15.738',0),(44,44,2,'2026-01-13 02:16:19.782',0),(45,45,3,'2026-01-13 02:16:20.041',0),(46,46,2,'2026-01-13 02:16:20.173',0),(47,47,1,'2026-01-13 02:16:20.283',0),(48,48,1,'2026-01-13 02:28:35.026',0),(49,49,3,'2026-01-13 02:28:35.510',0),(50,50,1,'2026-01-13 02:28:35.614',0),(51,51,2,'2026-01-13 02:28:36.276',0),(52,52,1,'2026-01-13 02:28:36.538',0),(53,53,2,'2026-01-13 02:28:40.321',0),(54,54,3,'2026-01-13 02:28:40.550',0),(55,55,2,'2026-01-13 02:28:40.673',0),(56,56,1,'2026-01-13 02:28:40.771',0),(57,57,2,'2026-01-13 02:28:40.956',0),(58,58,1,'2026-01-13 02:28:41.143',0),(59,59,1,'2026-01-13 03:04:10.092',0),(60,60,3,'2026-01-13 03:04:10.646',0),(61,61,1,'2026-01-13 03:04:10.755',0),(62,62,2,'2026-01-13 03:04:11.464',0),(63,63,1,'2026-01-13 03:04:11.710',0),(64,64,2,'2026-01-13 03:04:16.121',0),(65,65,3,'2026-01-13 03:04:16.366',0),(66,66,2,'2026-01-13 03:04:16.517',0),(67,67,1,'2026-01-13 03:04:16.634',0),(68,68,2,'2026-01-13 03:04:16.950',0),(69,69,1,'2026-01-13 03:04:17.177',0),(70,70,2,'2026-01-13 03:04:17.674',0),(71,71,1,'2026-01-13 03:04:17.891',0),(72,72,1,'2026-01-13 03:18:57.820',0),(73,73,3,'2026-01-13 03:18:58.485',0),(74,74,1,'2026-01-13 03:18:58.600',0),(75,75,2,'2026-01-13 03:18:59.340',0),(76,76,1,'2026-01-13 03:18:59.595',0),(77,77,2,'2026-01-13 03:19:03.691',0),(78,78,3,'2026-01-13 03:19:03.929',0),(79,79,2,'2026-01-13 03:19:04.074',0),(80,80,1,'2026-01-13 03:19:04.283',0),(81,81,2,'2026-01-13 03:19:04.692',0),(82,82,1,'2026-01-13 03:19:04.800',0),(83,83,2,'2026-01-13 03:19:05.020',0),(84,84,1,'2026-01-13 03:19:05.216',0),(85,85,2,'2026-01-13 03:19:05.665',0),(86,86,1,'2026-01-13 03:19:05.891',0),(87,87,1,'2026-01-13 03:29:31.882',0),(88,88,3,'2026-01-13 03:29:32.461',0),(89,89,1,'2026-01-13 03:29:32.562',0),(90,90,2,'2026-01-13 03:29:33.331',0),(91,91,1,'2026-01-13 03:29:33.569',0),(92,92,2,'2026-01-13 03:29:37.733',0),(93,93,3,'2026-01-13 03:29:37.984',0),(94,94,2,'2026-01-13 03:29:38.153',0),(95,95,1,'2026-01-13 03:29:38.358',0),(96,96,2,'2026-01-13 03:29:38.735',0),(97,97,1,'2026-01-13 03:29:38.839',0),(98,98,2,'2026-01-13 03:29:39.065',0),(99,99,1,'2026-01-13 03:29:39.262',0),(100,100,2,'2026-01-13 03:29:39.664',0),(101,101,1,'2026-01-13 03:29:39.871',0),(102,102,1,'2026-01-13 03:29:40.058',0),(103,103,2,'2026-01-13 03:29:40.484',0),(104,104,1,'2026-01-13 03:29:40.706',0),(105,105,1,'2026-01-13 03:39:48.132',0),(106,106,3,'2026-01-13 03:39:48.728',0),(107,107,1,'2026-01-13 03:39:48.837',0),(108,108,2,'2026-01-13 03:39:49.679',0),(109,109,1,'2026-01-13 03:39:49.948',0),(110,110,2,'2026-01-13 03:39:54.364',0),(111,111,3,'2026-01-13 03:39:54.590',0),(112,112,2,'2026-01-13 03:39:54.733',0),(113,113,1,'2026-01-13 03:39:54.932',0),(114,114,2,'2026-01-13 03:39:55.290',0),(115,115,1,'2026-01-13 03:39:55.393',0),(116,116,2,'2026-01-13 03:39:55.625',0),(117,117,1,'2026-01-13 03:39:55.822',0),(118,118,2,'2026-01-13 03:39:56.271',0),(119,119,1,'2026-01-13 03:39:56.462',0),(120,120,1,'2026-01-13 03:39:56.667',0),(121,121,2,'2026-01-13 03:39:57.111',0),(122,122,1,'2026-01-13 03:39:57.347',0),(123,123,1,'2026-01-13 03:59:37.607',0),(124,124,2,'2026-01-13 04:00:38.604',0),(125,125,2,'2026-01-13 06:41:35.428',0),(126,126,2,'2026-01-13 07:34:33.554',0),(127,127,2,'2026-01-13 07:34:47.179',0),(128,128,2,'2026-01-13 07:35:14.573',0),(129,129,2,'2026-01-13 07:35:41.575',0),(130,130,2,'2026-01-13 07:36:01.819',0),(131,131,2,'2026-01-13 07:37:19.453',0),(132,132,2,'2026-01-13 07:39:27.460',0),(133,133,2,'2026-01-13 08:00:22.194',0),(134,134,2,'2026-01-13 08:00:32.214',0);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `course_id` bigint NOT NULL COMMENT '鐠囧墽鈻糏D',
  `title` varchar(200) NOT NULL COMMENT '娴犺濮熼弽鍥?',
  `description` varchar(500) DEFAULT NULL COMMENT '娴犺濮熼幓蹇氬牚',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '閻樿埖?閿?閸?鏁?0閸嬫粎鏁?,
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '閹烘帒绨崣',
  `created_by` bigint NOT NULL COMMENT '閸掓稑缂撻懓鍜冪礄閼颁礁绗€閿?,
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  KEY `idx_task_course_id` (`course_id`),
  KEY `idx_task_created_by` (`created_by`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娴犺濮熼敍鍫?娑旂姾鐭惧鍕剁礆鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
INSERT INTO `task` VALUES (1,27,'1','1',1,0,124,'2026-01-13 08:32:37.386','2026-01-13 08:32:37.386',0);
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_link`
--

DROP TABLE IF EXISTS `task_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_link` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `task_id` bigint NOT NULL COMMENT '娴犺濮烮D',
  `step_id` bigint DEFAULT NULL COMMENT '濮濄儵?ID閿涘牆褰茬粚鐚寸礉鐞涖劎銇氶幐鍌氭躬娴犺濮熸稉瀣剁礆',
  `link_type` tinyint NOT NULL COMMENT '閸忓疇浠堢猾璇茬€烽敍?娴ｆ粈绗?2閼板啳鐦?3鐠у嫭鏋￠敍鍫?閻ｆ瑱绱?,
  `ref_id` bigint NOT NULL COMMENT '閸忓疇浠堢€电钖処D閿涘潊ssignmentId/examId/materialId閿?,
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '閹烘帒绨崣',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_link` (`task_id`,`step_id`,`link_type`,`ref_id`),
  KEY `idx_task_link_task_id` (`task_id`),
  KEY `idx_task_link_step_id` (`step_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娴犺濮熼崗瀹犱粓鐞?绱欐担婊€绗?閼板啳鐦?鐠у嫭鏋＄粵澶涚礆';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_link`
--

LOCK TABLES `task_link` WRITE;
/*!40000 ALTER TABLE `task_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_step`
--

DROP TABLE IF EXISTS `task_step`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_step` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `task_id` bigint NOT NULL COMMENT '娴犺濮烮D',
  `step_type` tinyint NOT NULL DEFAULT '2' COMMENT '??????????? 2???',
  `parent_id` bigint DEFAULT NULL COMMENT '????????D??????',
  `title` varchar(200) NOT NULL COMMENT '濮濄儵?閺嶅洭?',
  `content` text COMMENT '濮濄儵?閸愬懎?閿涘湣arkdown/HTML閿涘苯澧犵粩?瀹崇€规熬绱?,
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '閹烘帒绨崣',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  KEY `idx_task_step_task_id` (`task_id`),
  KEY `idx_task_step_task_parent_sort` (`task_id`,`parent_id`,`sort_no`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娴犺濮熷銉?鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_step`
--

LOCK TABLES `task_step` WRITE;
/*!40000 ALTER TABLE `task_step` DISABLE KEYS */;
INSERT INTO `task_step` VALUES (1,1,2,NULL,'123','',1,'2026-01-13 08:47:11.377','2026-01-13 10:07:07.810',0),(2,1,2,NULL,'test','',2,'2026-01-13 09:47:55.714','2026-01-13 10:07:07.488',0);
/*!40000 ALTER TABLE `task_step` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_step_attachment`
--

DROP TABLE IF EXISTS `task_step_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_step_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `step_id` bigint NOT NULL COMMENT '缁旂姾濡璉D閿涘澅ask_step.id閿?,
  `kind` tinyint NOT NULL COMMENT '闂勫嫪娆㈢猾璇茬€烽敍?PDF 2PPT 3鐟欏棝?',
  `title` varchar(200) DEFAULT NULL COMMENT '闂勫嫪娆㈤弽鍥?閿涘牆褰查柅澶涚礆',
  `file_id` bigint DEFAULT NULL COMMENT '閺傚洣娆D閿涘潚ile_object.id閿涘苯褰茬粚鐚寸礆',
  `url` varchar(1000) DEFAULT NULL COMMENT '婢舵牠鎽糢RL閿涘牆褰查柅澶涚礆',
  `sort_no` int NOT NULL DEFAULT '0' COMMENT '閹烘帒绨崣',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  KEY `idx_task_step_attachment_step_id` (`step_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='缁旂姾濡梽鍕鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_step_attachment`
--

LOCK TABLES `task_step_attachment` WRITE;
/*!40000 ALTER TABLE `task_step_attachment` DISABLE KEYS */;
INSERT INTO `task_step_attachment` VALUES (1,2,2,'PPT',9,NULL,1,'2026-01-13 10:08:33.727','2026-01-13 10:08:33.727',0);
/*!40000 ALTER TABLE `task_step_attachment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_step_attachment_progress`
--

DROP TABLE IF EXISTS `task_step_attachment_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_step_attachment_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `attachment_id` bigint NOT NULL COMMENT '闂勫嫪娆D閿涘澅ask_step_attachment.id閿?,
  `user_id` bigint NOT NULL COMMENT '鐎涳妇鏁揑D',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '閻樿埖?閿?閺?鐣幋?2瀹告彃鐣幋',
  `progress_percent` int NOT NULL DEFAULT '0' COMMENT '鏉╂稑瀹抽惂鎯у瀻濮ｆ棑绱?-100',
  `position_seconds` int DEFAULT NULL COMMENT '鐟欏棝?閿涙艾缍嬮崜宥嗘尡閺€鍓?閺佸府绱欓崣?鈹栭敍',
  `duration_seconds` int DEFAULT NULL COMMENT '鐟欏棝?閿涙碍?閺冨爼鏆辩粔鎺撴殶閿涘牆褰茬粚鐚寸礆',
  `last_reported_at` datetime(3) DEFAULT NULL COMMENT '閺?绻庢稉濠冨Г閺冨爼妫块敍鍫濆讲缁岀尨绱?,
  `completed_at` datetime(3) DEFAULT NULL COMMENT '鐎瑰本鍨氶弮鍫曟？',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_step_attachment_progress` (`attachment_id`,`user_id`),
  KEY `idx_task_step_attachment_progress_user_id` (`user_id`),
  KEY `idx_task_step_attachment_progress_attachment_id` (`attachment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='缁旂姾濡梽鍕鐎瑰本鍨氭潻娑樺鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_step_attachment_progress`
--

LOCK TABLES `task_step_attachment_progress` WRITE;
/*!40000 ALTER TABLE `task_step_attachment_progress` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_step_attachment_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_step_content_progress`
--

DROP TABLE IF EXISTS `task_step_content_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_step_content_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `step_id` bigint NOT NULL COMMENT '缁旂姾濡璉D',
  `user_id` bigint NOT NULL COMMENT '鐎涳妇鏁揑D',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '閻樿埖?閿?閺?鐣幋?2瀹告彃鐣幋',
  `completed_at` datetime(3) DEFAULT NULL COMMENT '鐎瑰本鍨氶弮鍫曟？',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_step_content_progress` (`step_id`,`user_id`),
  KEY `idx_task_step_content_progress_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='缁旂姾濡锝嗘瀮鐎瑰本鍨氭潻娑樺鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_step_content_progress`
--

LOCK TABLES `task_step_content_progress` WRITE;
/*!40000 ALTER TABLE `task_step_content_progress` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_step_content_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task_step_progress`
--

DROP TABLE IF EXISTS `task_step_progress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task_step_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '娑撳鏁璉D',
  `step_id` bigint NOT NULL COMMENT '濮濄儵?ID',
  `user_id` bigint NOT NULL COMMENT '鐎涳妇鏁揑D',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '閻樿埖?閿?閺?鐣幋?2瀹告彃鐣幋',
  `completed_at` datetime(3) DEFAULT NULL COMMENT '鐎瑰本鍨氶弮鍫曟？',
  `created_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '閸掓稑缂撻弮鍫曟？',
  `updated_at` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '閺囧瓨鏌婇弮鍫曟？',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '闁槒绶崚鐘绘珟閿?閸?1閺?,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_step_progress` (`step_id`,`user_id`),
  KEY `idx_task_step_progress_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='娴犺濮熷銉?鐎瑰本鍨氭潻娑樺鐞?;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task_step_progress`
--

LOCK TABLES `task_step_progress` WRITE;
/*!40000 ALTER TABLE `task_step_progress` DISABLE KEYS */;
/*!40000 ALTER TABLE `task_step_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'cloud_classroom'
--

--
-- Dumping routines for database 'cloud_classroom'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-13 10:31:47
