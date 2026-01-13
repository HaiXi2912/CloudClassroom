package com.cloudclassroom;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.mapper.FileObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 资料模块联调冒烟：
 * - 老师创建课程
 * - 老师创建资料（引用 file_object）
 * - 学生加入课程
 * - 学生查看资料并提交/查询学习进度
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MaterialFlowTest {

  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private FileObjectMapper fileObjectMapper;

  @Test
  void material_and_progress_flow_should_work() {
    AuthTestHelper.RegisterAndLoginResult teacher = AuthTestHelper.registerAndLogin(
        rest,
        "t_mat_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "老师资料A",
        "TEACHER");

    AuthTestHelper.RegisterAndLoginResult student = AuthTestHelper.registerAndLogin(
        rest,
        "s_mat_" + (System.currentTimeMillis() % 1_000_000_000L),
        "123456",
        "学生资料A",
        "STUDENT");

    Long courseId = CourseTestHelper.createCourse(rest, teacher.token, "资料流程课");
    String courseCode = CourseTestHelper.getCourseCode(rest, teacher.token, courseId);
    CourseTestHelper.joinCourse(rest, student.token, courseCode);

    // 准备一个 file_object（本待办不做上传接口，测试直接插入）
    FileObject f = new FileObject();
    f.setOriginalName("a.pdf");
    f.setStoredPath("test/a.pdf");
    f.setContentType("application/pdf");
    f.setFileExt("pdf");
    f.setFileSize(123L);
    f.setSha256(null);
    f.setCreatedBy(null);
    f.setCreatedAt(LocalDateTime.now());
    f.setDeleted(0);
    fileObjectMapper.insert(f);

    HttpHeaders tHeaders = new HttpHeaders();
    tHeaders.setContentType(MediaType.APPLICATION_JSON);
    tHeaders.setBearerAuth(teacher.token);

    Map<String, Object> createMaterialBody = Map.of(
        "courseId", courseId,
        "fileId", f.getId(),
        "title", "第一份资料",
        "materialType", 1,
        "sortNo", 1
    );

    ResponseEntity<ApiResponse> createResp = rest.postForEntity(
        "/api/teacher/materials",
        new HttpEntity<>(createMaterialBody, tHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, createResp.getStatusCode());
    assertNotNull(createResp.getBody());
    assertEquals(0, createResp.getBody().getCode());
    Long materialId = ((Number) createResp.getBody().getData()).longValue();

    // 老师列表应包含
    ResponseEntity<ApiResponse> tListResp = rest.exchange(
        "/api/teacher/materials?courseId=" + courseId,
        HttpMethod.GET,
        new HttpEntity<>(tHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, tListResp.getStatusCode());
    assertNotNull(tListResp.getBody());
    assertEquals(0, tListResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> tList = (List<Map<String, Object>>) tListResp.getBody().getData();
    assertNotNull(tList);
    assertTrue(tList.stream().anyMatch(m -> materialId.equals(((Number) m.get("id")).longValue())));

    // 学生列表应包含
    HttpHeaders sHeaders = new HttpHeaders();
    sHeaders.setContentType(MediaType.APPLICATION_JSON);
    sHeaders.setBearerAuth(student.token);

    ResponseEntity<ApiResponse> sListResp = rest.exchange(
        "/api/student/materials?courseId=" + courseId,
        HttpMethod.GET,
        new HttpEntity<>(sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, sListResp.getStatusCode());
    assertNotNull(sListResp.getBody());
    assertEquals(0, sListResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> sList = (List<Map<String, Object>>) sListResp.getBody().getData();
    assertNotNull(sList);
    assertEquals(1, sList.size());

    // 提交进度
    Map<String, Object> upsertBody = Map.of(
        "progressPercent", 30,
        "lastPosition", 12
    );

    ResponseEntity<ApiResponse> upsertResp = rest.postForEntity(
        "/api/student/materials/" + materialId + "/progress",
        new HttpEntity<>(upsertBody, sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, upsertResp.getStatusCode());
    assertNotNull(upsertResp.getBody());
    assertEquals(0, upsertResp.getBody().getCode());

    // 查询进度
    ResponseEntity<ApiResponse> progressResp = rest.exchange(
        "/api/student/materials/" + materialId + "/progress",
        HttpMethod.GET,
        new HttpEntity<>(sHeaders),
        ApiResponse.class);

    assertEquals(HttpStatus.OK, progressResp.getStatusCode());
    assertNotNull(progressResp.getBody());
    assertEquals(0, progressResp.getBody().getCode());

    @SuppressWarnings("unchecked")
    Map<String, Object> p = (Map<String, Object>) progressResp.getBody().getData();
    assertNotNull(p);
    assertEquals(30, ((Number) p.get("progressPercent")).intValue());
    assertEquals(12, ((Number) p.get("lastPosition")).intValue());
  }
}
