package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件上传下载联调冒烟：
 * - 老师上传文件得到 fileId
 * - 老师创建 material 绑定 fileId
 * - 学生下载 material 文件得到原始内容
 * - 未加入课程的学生下载应被拒绝
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileFlowTest {

  @Autowired
  private TestRestTemplate rest;

  @Test
  void upload_and_download_should_work_with_security_check() {
    AuthTestHelper.RegisterAndLoginResult teacher = AuthTestHelper.registerAndLogin(
        rest,
        "t_file_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "老师文件A",
        "TEACHER");

    AuthTestHelper.RegisterAndLoginResult student = AuthTestHelper.registerAndLogin(
        rest,
        "s_file_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "学生文件B",
        "STUDENT");

    AuthTestHelper.RegisterAndLoginResult outsider = AuthTestHelper.registerAndLogin(
        rest,
        "s_out_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "学生路人C",
        "STUDENT");

    Long courseId = CourseTestHelper.createCourse(rest, teacher.token, "文件流程课");
    String courseCode = CourseTestHelper.getCourseCode(rest, teacher.token, courseId);
    CourseTestHelper.joinCourse(rest, student.token, courseCode);

    // 1) 上传文件（txt，作为最小可用演示）
    byte[] content = "hello cloudclassroom".getBytes(StandardCharsets.UTF_8);
    Long fileId = uploadFile(teacher.token, content, "hello.txt");
    assertNotNull(fileId);

    // 2) 创建资料记录，绑定 fileId
    Long materialId = createMaterial(teacher.token, courseId, fileId, "文件资料1", 4);
    assertNotNull(materialId);

    // 3) 学生下载资料文件，内容应一致
    ResponseEntity<byte[]> dl = downloadMaterialFile(student.token, materialId);
    assertEquals(HttpStatus.OK, dl.getStatusCode());
    assertNotNull(dl.getBody());
    assertArrayEquals(content, dl.getBody());

    // 4) 未加入课程的学生下载应失败（统一 JSON）
    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> denied = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) downloadMaterialFileAsApi(outsider.token, materialId);
    assertEquals(HttpStatus.OK, denied.getStatusCode());
    assertNotNull(denied.getBody());
    assertNotEquals(0, denied.getBody().getCode());
  }

  private Long uploadFile(String token, byte[] bytes, String filename) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    ByteArrayResource resource = new ByteArrayResource(bytes) {
      @Override
      public String getFilename() {
        return filename;
      }
    };

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", resource);

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.postForEntity(
        "/api/teacher/files/upload",
        new HttpEntity<>(body, headers),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> data = (Map<String, Object>) resp.getBody().getData();
    assertNotNull(data);
    assertNotNull(data.get("id"));
    return ((Number) data.get("id")).longValue();
  }

  private Long createMaterial(String teacherToken, Long courseId, Long fileId, String title, Integer materialType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(teacherToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> req = new HashMap<>();
    req.put("courseId", courseId);
    req.put("fileId", fileId);
    req.put("title", title);
    req.put("materialType", materialType);
    req.put("sortNo", 0);

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.postForEntity(
        "/api/teacher/materials",
        new HttpEntity<>(req, headers),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(0, resp.getBody().getCode());
    assertNotNull(resp.getBody().getData());
    return ((Number) resp.getBody().getData()).longValue();
  }

  private ResponseEntity<byte[]> downloadMaterialFile(String token, Long materialId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    return rest.exchange(
        "/api/student/materials/" + materialId + "/file",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        byte[].class);
  }

  private ResponseEntity<ApiResponse<?>> downloadMaterialFileAsApi(String token, Long materialId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    @SuppressWarnings("unchecked")
    ResponseEntity<ApiResponse<?>> resp = (ResponseEntity<ApiResponse<?>>) (ResponseEntity<?>) rest.exchange(
        "/api/student/materials/" + materialId + "/file",
        HttpMethod.GET,
        new HttpEntity<>(headers),
        ApiResponse.class);

    return resp;
  }
}
