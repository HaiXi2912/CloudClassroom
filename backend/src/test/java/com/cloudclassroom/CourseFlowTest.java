package com.cloudclassroom;

import com.cloudclassroom.modules.rbac.service.RbacService;
import com.cloudclassroom.modules.user.entity.SysUser;
import com.cloudclassroom.modules.user.service.SysUserService;
import com.cloudclassroom.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 课程模块联调冒烟：
 * - 老师创建课程
 * - 学生用课程码加入
 * - 双方分别查看列表
 * - 老师查看成员并移除
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CourseFlowTest {

  @Autowired
  private org.springframework.boot.test.web.client.TestRestTemplate restTemplate;

  @Autowired
  private SysUserService sysUserService;

  @Autowired
  private RbacService rbacService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenService jwtTokenService;

  @Test
  void 课程创建_加入_移除_流程应可用() {
    SysUser teacher = new SysUser();
    teacher.setUsername("tea_course_" + (System.currentTimeMillis() % 1_000_000_000L));
    teacher.setNickname("老师课程测试");
    teacher.setPasswordHash(passwordEncoder.encode("123456"));
    teacher.setStatus(1);
    teacher.setCreatedAt(LocalDateTime.now());
    teacher.setUpdatedAt(LocalDateTime.now());
    teacher.setDeleted(0);
    sysUserService.insert(teacher);
    rbacService.bindUserRole(teacher.getId(), "TEACHER");

    SysUser student = new SysUser();
    student.setUsername("stu_course_" + (System.currentTimeMillis() % 1_000_000_000L));
    student.setNickname("学生课程测试");
    student.setPasswordHash(passwordEncoder.encode("123456"));
    student.setStatus(1);
    student.setCreatedAt(LocalDateTime.now());
    student.setUpdatedAt(LocalDateTime.now());
    student.setDeleted(0);
    sysUserService.insert(student);
    rbacService.bindUserRole(student.getId(), "STUDENT");

    String teacherToken = jwtTokenService.generateToken(teacher.getId(), "TEACHER");
    String studentToken = jwtTokenService.generateToken(student.getId(), "STUDENT");

    HttpHeaders teacherHeaders = new HttpHeaders();
    teacherHeaders.setBearerAuth(teacherToken);

    // 1) 老师创建课程
    Map<String, Object> createBody = Map.of(
        "courseName", "Java入门",
        "description", "第一门课"
    );
    ResponseEntity<ApiEnvelope> createResp = restTemplate.exchange(
        "/api/teacher/courses",
        HttpMethod.POST,
        new HttpEntity<>(createBody, teacherHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, createResp.getStatusCode());
    assertNotNull(createResp.getBody());
    assertEquals(0, createResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> course = (Map<String, Object>) createResp.getBody().getData();
    assertNotNull(course);
    assertNotNull(course.get("id"));
    assertNotNull(course.get("courseCode"));

    Long courseId = ((Number) course.get("id")).longValue();
    String courseCode = String.valueOf(course.get("courseCode"));

    // 2) 学生用课程码加入
    HttpHeaders studentHeaders = new HttpHeaders();
    studentHeaders.setBearerAuth(studentToken);

    Map<String, Object> joinBody = Map.of("courseCode", courseCode);
    ResponseEntity<ApiEnvelope> joinResp = restTemplate.exchange(
        "/api/student/courses/join",
        HttpMethod.POST,
        new HttpEntity<>(joinBody, studentHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, joinResp.getStatusCode());
    assertNotNull(joinResp.getBody());
    assertEquals(0, joinResp.getBody().getCode());

    // 3) 学生课程列表应包含
    ResponseEntity<ApiEnvelope> stuListResp = restTemplate.exchange(
        "/api/student/courses",
        HttpMethod.GET,
        new HttpEntity<>(studentHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, stuListResp.getStatusCode());
    assertNotNull(stuListResp.getBody());
    assertEquals(0, stuListResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> stuCourses = (List<Map<String, Object>>) stuListResp.getBody().getData();
    assertNotNull(stuCourses);
    assertTrue(stuCourses.stream().anyMatch(c -> courseId.equals(((Number) c.get("id")).longValue())));

    // 4) 老师成员列表应包含学生
    ResponseEntity<ApiEnvelope> membersResp = restTemplate.exchange(
        "/api/teacher/courses/" + courseId + "/members",
        HttpMethod.GET,
        new HttpEntity<>(teacherHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, membersResp.getStatusCode());
    assertNotNull(membersResp.getBody());
    assertEquals(0, membersResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> members = (List<Map<String, Object>>) membersResp.getBody().getData();
    assertNotNull(members);
    assertTrue(members.stream().anyMatch(m -> student.getId().equals(((Number) m.get("userId")).longValue())));

    // 5) 老师移除学生
    Map<String, Object> removeBody = Map.of("userId", student.getId());
    ResponseEntity<ApiEnvelope> removeResp = restTemplate.exchange(
        "/api/teacher/courses/" + courseId + "/members/remove",
        HttpMethod.POST,
        new HttpEntity<>(removeBody, teacherHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, removeResp.getStatusCode());
    assertNotNull(removeResp.getBody());
    assertEquals(0, removeResp.getBody().getCode());

    // 6) 学生课程列表应不包含
    ResponseEntity<ApiEnvelope> stuListResp2 = restTemplate.exchange(
        "/api/student/courses",
        HttpMethod.GET,
        new HttpEntity<>(studentHeaders),
        ApiEnvelope.class
    );
    assertEquals(HttpStatus.OK, stuListResp2.getStatusCode());
    assertNotNull(stuListResp2.getBody());
    assertEquals(0, stuListResp2.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> stuCourses2 = (List<Map<String, Object>>) stuListResp2.getBody().getData();
    assertNotNull(stuCourses2);
    assertTrue(stuCourses2.stream().noneMatch(c -> courseId.equals(((Number) c.get("id")).longValue())));
  }

  public static class ApiEnvelope {
    private int code;
    private String message;
    private Object data;
    private long timestamp;

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Object getData() {
      return data;
    }

    public void setData(Object data) {
      this.data = data;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(long timestamp) {
      this.timestamp = timestamp;
    }
  }
}
