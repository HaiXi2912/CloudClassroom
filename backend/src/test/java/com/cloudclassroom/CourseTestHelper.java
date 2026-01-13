package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试辅助：课程相关常用操作。
 */
public class CourseTestHelper {

  public static Long createCourse(TestRestTemplate rest, String teacherToken, String courseName) {
    Map<String, Object> req = new HashMap<>();
    req.put("courseName", courseName);
    req.put("description", "测试课程");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(teacherToken);

    ResponseEntity<ApiResponse> resp = rest.postForEntity("/api/teacher/courses", new HttpEntity<>(req, headers), ApiResponse.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> course = (Map<String, Object>) resp.getBody().getData();
    assertNotNull(course);
    assertNotNull(course.get("id"));
    return ((Number) course.get("id")).longValue();
  }

  public static String getCourseCode(TestRestTemplate rest, String teacherToken, Long courseId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(teacherToken);

    ResponseEntity<ApiResponse> resp = rest.exchange(
        "/api/teacher/courses",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    List<Map<String, Object>> list = (List<Map<String, Object>>) resp.getBody().getData();
    assertNotNull(list);

    for (Map<String, Object> row : list) {
      if (row == null) {
        continue;
      }
      Object idObj = row.get("id");
      if (idObj instanceof Number && ((Number) idObj).longValue() == courseId) {
        Object codeObj = row.get("courseCode");
        if (codeObj != null) {
          return String.valueOf(codeObj);
        }
      }
    }

    fail("未找到课程码");
    return null;
  }

  public static void joinCourse(TestRestTemplate rest, String studentToken, String courseCode) {
    Map<String, Object> req = new HashMap<>();
    req.put("courseCode", courseCode);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(studentToken);

    ResponseEntity<ApiResponse> resp = rest.postForEntity("/api/student/courses/join", new HttpEntity<>(req, headers), ApiResponse.class);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());
  }
}
