package com.cloudclassroom.modules.material.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.material.dto.MaterialProgressRow;
import com.cloudclassroom.modules.material.dto.MaterialRow;
import com.cloudclassroom.modules.material.entity.FileObject;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 学生端：查看课程资料与提交学习进度。
 */
@RestController
@RequestMapping("/api/student/materials")
@RequireRole({"STUDENT"})
public class StudentMaterialController {

  private final MaterialService materialService;
  private final FileObjectService fileObjectService;

  public StudentMaterialController(MaterialService materialService, FileObjectService fileObjectService) {
    this.materialService = materialService;
    this.fileObjectService = fileObjectService;
  }

  @GetMapping
  public ApiResponse<List<MaterialRow>> list(
      @RequestParam("courseId") @NotNull(message = "courseId 不能为空") @Min(value = 1, message = "courseId 必须 >= 1") Long courseId
  ) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(materialService.listStudentMaterials(studentId, courseId));
  }

  @GetMapping("/{materialId}/progress")
  @RequirePerm({"material:progress"})
  public ApiResponse<MaterialProgressRow> myProgress(@PathVariable Long materialId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(materialService.getMyProgress(studentId, materialId));
  }

  @PostMapping("/{materialId}/progress")
  @RequirePerm({"material:progress"})
  public ApiResponse<Void> upsertProgress(@PathVariable Long materialId, @Valid @RequestBody UpsertProgressRequest req) {
    Long studentId = SecurityUtil.requireUserId();
    materialService.upsertMyProgress(studentId, materialId, req.getProgressPercent(), req.getLastPosition());
    return ApiResponse.ok();
  }

  /**
   * 下载资料文件（学生端）。
   */
  @GetMapping("/{materialId}/file")
  public ResponseEntity<byte[]> downloadFile(@PathVariable Long materialId) {
    Long studentId = SecurityUtil.requireUserId();
    FileObject fo = materialService.getFileObjectForStudent(studentId, materialId);
    byte[] bytes = fileObjectService.readBytes(fo);

    String filename = fo.getOriginalName() == null ? "file" : fo.getOriginalName();
    String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);

    return ResponseEntity.ok().headers(headers).body(bytes);
  }

  public static class UpsertProgressRequest {

    @NotNull(message = "progressPercent 不能为空")
    @Min(value = 0, message = "progressPercent 必须在 0~100 之间")
    @Max(value = 100, message = "progressPercent 必须在 0~100 之间")
    private Integer progressPercent;

    @Min(value = 0, message = "lastPosition 必须 >= 0")
    private Integer lastPosition;

    public Integer getProgressPercent() {
      return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
      this.progressPercent = progressPercent;
    }

    public Integer getLastPosition() {
      return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
      this.lastPosition = lastPosition;
    }
  }
}
