package com.cloudclassroom.modules.material.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.material.dto.MaterialRow;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.entity.Material;
import com.cloudclassroom.modules.material.service.FileObjectService;
import com.cloudclassroom.modules.material.service.MaterialService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 老师端：课程资料管理（仅记录资料与文件关联，不包含上传）。
 */
@RestController
@RequestMapping("/api/teacher/materials")
@RequireRole({"TEACHER"})
public class TeacherMaterialController {

  private final MaterialService materialService;
  private final FileObjectService fileObjectService;

  public TeacherMaterialController(MaterialService materialService, FileObjectService fileObjectService) {
    this.materialService = materialService;
    this.fileObjectService = fileObjectService;
  }

  @PostMapping
  @RequirePerm({"material:upload"})
  public ApiResponse<Long> create(@Valid @RequestBody CreateMaterialRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    Material m = materialService.createMaterial(
        teacherId,
        req.getCourseId(),
        req.getFileId(),
        req.getTitle(),
        req.getMaterialType(),
        req.getSortNo()
    );
    return ApiResponse.ok(m.getId());
  }

  @GetMapping
  public ApiResponse<List<MaterialRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(materialService.listTeacherMaterials(teacherId, courseId));
  }

  /**
   * 下载资料文件（老师端）。
   */
  @GetMapping("/{materialId}/file")
  @RequirePerm({"material:upload"})
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long materialId) {
    Long teacherId = SecurityUtil.requireUserId();
    FileObject fo = materialService.getFileObjectForTeacher(teacherId, materialId);
    byte[] bytes = fileObjectService.readBytes(fo);

    String filename = fo.getOriginalName() == null ? "file" : fo.getOriginalName();
    String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);

    return ResponseEntity.ok().headers(headers).body(bytes);
  }

  public static class CreateMaterialRequest {

    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    @NotNull(message = "fileId 不能为空")
    private Long fileId;

    @NotBlank(message = "title 不能为空")
    private String title;

    @NotNull(message = "materialType 不能为空")
    private Integer materialType;

    private Integer sortNo;

    public Long getCourseId() {
      return courseId;
    }

    public void setCourseId(Long courseId) {
      this.courseId = courseId;
    }

    public Long getFileId() {
      return fileId;
    }

    public void setFileId(Long fileId) {
      this.fileId = fileId;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public Integer getMaterialType() {
      return materialType;
    }

    public void setMaterialType(Integer materialType) {
      this.materialType = materialType;
    }

    public Integer getSortNo() {
      return sortNo;
    }

    public void setSortNo(Integer sortNo) {
      this.sortNo = sortNo;
    }
  }
}
