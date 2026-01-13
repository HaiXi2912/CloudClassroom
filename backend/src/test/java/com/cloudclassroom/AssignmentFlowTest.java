package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.assignment.controller.StudentAssignmentController;
import com.cloudclassroom.modules.assignment.controller.TeacherAssignmentController;
import com.cloudclassroom.modules.assignment.dto.SubmissionRow;
import com.cloudclassroom.modules.assignment.entity.AssignmentQuestion;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AssignmentFlowTest {

  @Autowired
  private TestRestTemplate rest;

  @Test
  void teacher_assign_student_submit_then_grade_should_work() {
    // 1) 老师注册/登录
    AuthTestHelper.RegisterAndLoginResult teacher = AuthTestHelper.registerAndLogin(rest,
                "t_assign_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "老师A",
        "TEACHER");

    // 2) 学生注册/登录
    AuthTestHelper.RegisterAndLoginResult student = AuthTestHelper.registerAndLogin(rest,
        "s_assign_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "学生A",
        "STUDENT");

    // 3) 老师创建课程
    Long courseId = CourseTestHelper.createCourse(rest, teacher.token, "作业流程课");

    // 4) 学生通过课程码加入
    String courseCode = CourseTestHelper.getCourseCode(rest, teacher.token, courseId);
    CourseTestHelper.joinCourse(rest, student.token, courseCode);

    // 5) 老师布置作业（单选+简答）
    TeacherAssignmentController.CreateAssignmentRequest createReq = new TeacherAssignmentController.CreateAssignmentRequest();
    createReq.setCourseId(courseId);
    createReq.setTitle("第一次作业");
    createReq.setContent("请完成题目");
    createReq.setDueAt(LocalDateTime.now().plusDays(1));

    List<AssignmentQuestion> questions = new ArrayList<>();

    AssignmentQuestion q1 = new AssignmentQuestion();
    q1.setQuestionType(1);
    q1.setQuestionText("1+1=？");
    q1.setOptionA("1");
    q1.setOptionB("2");
    q1.setOptionC("3");
    q1.setOptionD("4");
    q1.setCorrectAnswer("B");
    q1.setScore(5);
    q1.setSortNo(1);
    questions.add(q1);

    AssignmentQuestion q2 = new AssignmentQuestion();
    q2.setQuestionType(4);
    q2.setQuestionText("简述你对云课堂的理解");
    q2.setScore(10);
    q2.setSortNo(2);
    questions.add(q2);

    createReq.setQuestions(questions);

    HttpHeaders tHeaders = new HttpHeaders();
    tHeaders.setContentType(MediaType.APPLICATION_JSON);
    tHeaders.setBearerAuth(teacher.token);

    ResponseEntity<ApiResponse> createResp = rest.postForEntity(
        "/api/teacher/assignments",
        new HttpEntity<>(createReq, tHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, createResp.getStatusCode());
    assertNotNull(createResp.getBody());
    assertEquals(0, createResp.getBody().getCode());
    Long assignmentId = ((Number) createResp.getBody().getData()).longValue();

    // 6) 学生拉题目并提交（客观题应自动得分）
    HttpHeaders sHeaders = new HttpHeaders();
    sHeaders.setContentType(MediaType.APPLICATION_JSON);
    sHeaders.setBearerAuth(student.token);

    ResponseEntity<ApiResponse> qResp = rest.exchange(
        "/api/student/assignments/" + assignmentId + "/questions",
        HttpMethod.GET,
        new HttpEntity<>(sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, qResp.getStatusCode());
    assertNotNull(qResp.getBody());
    assertEquals(0, qResp.getBody().getCode());

    // 题目ID从返回中取：data 是 List<Map>
    List<Map<String, Object>> qList = (List<Map<String, Object>>) qResp.getBody().getData();
    assertNotNull(qList);
    assertEquals(2, qList.size());

    Long q1Id = ((Number) qList.get(0).get("id")).longValue();
    Long q2Id = ((Number) qList.get(1).get("id")).longValue();

    StudentAssignmentController.SubmitRequest submitReq = new StudentAssignmentController.SubmitRequest();

    List<StudentAssignmentController.AnswerItem> answers = new ArrayList<>();
    StudentAssignmentController.AnswerItem a1 = new StudentAssignmentController.AnswerItem();
    a1.setQuestionId(q1Id);
    a1.setAnswerText("B");
    answers.add(a1);

    StudentAssignmentController.AnswerItem a2 = new StudentAssignmentController.AnswerItem();
    a2.setQuestionId(q2Id);
    a2.setAnswerText("我认为云课堂是一个支持教学互动的平台。");
    answers.add(a2);

    submitReq.setAnswers(answers);

    ResponseEntity<ApiResponse> submitResp = rest.postForEntity(
        "/api/student/assignments/" + assignmentId + "/submit",
        new HttpEntity<>(submitReq, sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, submitResp.getStatusCode());
    assertNotNull(submitResp.getBody());
    assertEquals(0, submitResp.getBody().getCode());

    // 7) 老师查看提交列表，拿到 submissionId
    ResponseEntity<ApiResponse> subsResp = rest.exchange(
        "/api/teacher/assignments/" + assignmentId + "/submissions",
        HttpMethod.GET,
        new HttpEntity<>(tHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, subsResp.getStatusCode());
    assertNotNull(subsResp.getBody());
    assertEquals(0, subsResp.getBody().getCode());

    List<Map<String, Object>> subs = (List<Map<String, Object>>) subsResp.getBody().getData();
    assertNotNull(subs);
    assertEquals(1, subs.size());

    Long submissionId = ((Number) subs.get(0).get("id")).longValue();
    Integer autoScore = subs.get(0).get("autoScore") == null ? 0 : ((Number) subs.get(0).get("autoScore")).intValue();
    assertEquals(5, autoScore);

    // 8) 老师批改（给简答加分）
    TeacherAssignmentController.GradeRequest gradeReq = new TeacherAssignmentController.GradeRequest();
    gradeReq.setManualScore(8);
    gradeReq.setTeacherComment("回答较完整");

    ResponseEntity<ApiResponse> gradeResp = rest.postForEntity(
        "/api/teacher/assignments/submissions/" + submissionId + "/grade",
        new HttpEntity<>(gradeReq, tHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, gradeResp.getStatusCode());
    assertNotNull(gradeResp.getBody());
    assertEquals(0, gradeResp.getBody().getCode());

    // 9) 再次查询提交列表，断言总分
    ResponseEntity<ApiResponse> subsResp2 = rest.exchange(
        "/api/teacher/assignments/" + assignmentId + "/submissions",
        HttpMethod.GET,
        new HttpEntity<>(tHeaders),
        ApiResponse.class);

    List<Map<String, Object>> subs2 = (List<Map<String, Object>>) subsResp2.getBody().getData();
    assertNotNull(subs2);
    assertEquals(1, subs2.size());

    Integer totalScore = subs2.get(0).get("totalScore") == null ? 0 : ((Number) subs2.get(0).get("totalScore")).intValue();
    assertEquals(13, totalScore);

    Integer status = subs2.get(0).get("status") == null ? 0 : ((Number) subs2.get(0).get("status")).intValue();
    assertEquals(3, status);
  }
}
