package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.exam.controller.StudentExamController;
import com.cloudclassroom.modules.exam.controller.TeacherExamController;
import com.cloudclassroom.modules.exam.entity.ExamQuestion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 考试模块联调冒烟：
 * - 老师创建课程
 * - 老师创建考试（组卷）
 * - 学生加入课程
 * - 学生拉题目并提交
 * - 学生查询成绩
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExamFlowTest {

  @Autowired
  private TestRestTemplate rest;

  @Test
  void exam_create_submit_and_score_should_work() {
    AuthTestHelper.RegisterAndLoginResult teacher = AuthTestHelper.registerAndLogin(
        rest,
        "t_exam_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "老师考试A",
        "TEACHER");

    AuthTestHelper.RegisterAndLoginResult student = AuthTestHelper.registerAndLogin(
        rest,
        "s_exam_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "学生考试A",
        "STUDENT");

    Long courseId = CourseTestHelper.createCourse(rest, teacher.token, "考试流程课");
    String courseCode = CourseTestHelper.getCourseCode(rest, teacher.token, courseId);
    CourseTestHelper.joinCourse(rest, student.token, courseCode);

    // 老师创建考试（单选+判断）
    TeacherExamController.CreateExamRequest createReq = new TeacherExamController.CreateExamRequest();
    createReq.setCourseId(courseId);
    createReq.setTitle("第一次考试");
    createReq.setDescription("请认真作答");
    createReq.setStartAt(LocalDateTime.now().minusMinutes(1));
    createReq.setEndAt(LocalDateTime.now().plusMinutes(30));
    createReq.setDurationMinutes(30);

    List<ExamQuestion> qs = new ArrayList<>();

    ExamQuestion q1 = new ExamQuestion();
    q1.setQuestionType(1);
    q1.setQuestionText("Java 是什么？");
    q1.setOptionA("编程语言");
    q1.setOptionB("数据库");
    q1.setOptionC("操作系统");
    q1.setOptionD("浏览器");
    q1.setCorrectAnswer("A");
    q1.setScore(5);
    q1.setSortNo(1);
    qs.add(q1);

    ExamQuestion q2 = new ExamQuestion();
    q2.setQuestionType(3);
    q2.setQuestionText("Spring Boot 可以用于快速构建后端服务。判断对错（TRUE/FALSE）");
    q2.setCorrectAnswer("TRUE");
    q2.setScore(5);
    q2.setSortNo(2);
    qs.add(q2);

    createReq.setQuestions(qs);

    HttpHeaders tHeaders = new HttpHeaders();
    tHeaders.setContentType(MediaType.APPLICATION_JSON);
    tHeaders.setBearerAuth(teacher.token);

    ResponseEntity<ApiResponse> createResp = rest.postForEntity(
        "/api/teacher/exams",
        new HttpEntity<>(createReq, tHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, createResp.getStatusCode());
    assertNotNull(createResp.getBody());
    assertEquals(0, createResp.getBody().getCode());
    Long examId = ((Number) createResp.getBody().getData()).longValue();

    // 学生拉题目
    HttpHeaders sHeaders = new HttpHeaders();
    sHeaders.setContentType(MediaType.APPLICATION_JSON);
    sHeaders.setBearerAuth(student.token);

    ResponseEntity<ApiResponse> qResp = rest.exchange(
        "/api/student/exams/" + examId + "/questions",
        HttpMethod.GET,
        new HttpEntity<>(sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, qResp.getStatusCode());
    assertNotNull(qResp.getBody());
    assertEquals(0, qResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> qList = (List<Map<String, Object>>) qResp.getBody().getData();
    assertNotNull(qList);
    assertEquals(2, qList.size());

    Long q1Id = ((Number) qList.get(0).get("id")).longValue();
    Long q2Id = ((Number) qList.get(1).get("id")).longValue();

    // 学生提交
    StudentExamController.SubmitRequest submitReq = new StudentExamController.SubmitRequest();

    List<StudentExamController.AnswerItem> answers = new ArrayList<>();
    StudentExamController.AnswerItem a1 = new StudentExamController.AnswerItem();
    a1.setQuestionId(q1Id);
    a1.setAnswerText("A");
    answers.add(a1);

    StudentExamController.AnswerItem a2 = new StudentExamController.AnswerItem();
    a2.setQuestionId(q2Id);
    a2.setAnswerText("TRUE");
    answers.add(a2);

    submitReq.setAnswers(answers);

    ResponseEntity<ApiResponse> submitResp = rest.postForEntity(
        "/api/student/exams/" + examId + "/submit",
        new HttpEntity<>(submitReq, sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, submitResp.getStatusCode());
    assertNotNull(submitResp.getBody());
    assertEquals(0, submitResp.getBody().getCode());

    // 学生查询成绩
    ResponseEntity<ApiResponse> attemptResp = rest.exchange(
        "/api/student/exams/" + examId + "/my-attempt",
        HttpMethod.GET,
        new HttpEntity<>(sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, attemptResp.getStatusCode());
    assertNotNull(attemptResp.getBody());
    assertEquals(0, attemptResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> attempt = (Map<String, Object>) attemptResp.getBody().getData();
    assertNotNull(attempt);

    Integer totalScore = attempt.get("totalScore") == null ? 0 : ((Number) attempt.get("totalScore")).intValue();
    assertEquals(10, totalScore);

    Integer status = attempt.get("status") == null ? 0 : ((Number) attempt.get("status")).intValue();
    assertEquals(2, status);
  }
}
