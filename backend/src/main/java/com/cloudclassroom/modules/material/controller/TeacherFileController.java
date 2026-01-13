package com.cloudclassroom.modules.material.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.service.FileObjectService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequirePerm;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 老师端：文件上传（本地存储）。
 */
@RestController
@RequestMapping("/api/teacher/files")
@RequireRole({"TEACHER"})
public class TeacherFileController {

  private final FileObjectService fileObjectService;

  public TeacherFileController(FileObjectService fileObjectService) {
    this.fileObjectService = fileObjectService;
  }

  /**
   * 上传文件，返回 fileId 与基础信息。
   */
  @PostMapping("/upload")
  @RequirePerm({"material:upload"})
  public ApiResponse<FileUploadRow> upload(@RequestParam("file") MultipartFile file) {
    Long teacherId = SecurityUtil.requireUserId();
    FileObject fo = fileObjectService.upload(teacherId, file);

    FileUploadRow row = new FileUploadRow();
    row.setId(fo.getId());
    row.setOriginalName(fo.getOriginalName());
    row.setContentType(fo.getContentType());
    row.setFileExt(fo.getFileExt());
    row.setFileSize(fo.getFileSize());
    row.setSha256(fo.getSha256());
    return ApiResponse.ok(row);
  }

  public static class FileUploadRow {
    private Long id;
    private String originalName;
    private String contentType;
    private String fileExt;
    private Long fileSize;
    private String sha256;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getOriginalName() {
      return originalName;
    }

    public void setOriginalName(String originalName) {
      this.originalName = originalName;
    }

    public String getContentType() {
      return contentType;
    }

    public void setContentType(String contentType) {
      this.contentType = contentType;
    }

    public String getFileExt() {
      return fileExt;
    }

    public void setFileExt(String fileExt) {
      this.fileExt = fileExt;
    }

    public Long getFileSize() {
      return fileSize;
    }

    public void setFileSize(Long fileSize) {
      this.fileSize = fileSize;
    }

    public String getSha256() {
      return sha256;
    }

    public void setSha256(String sha256) {
      this.sha256 = sha256;
    }
  }
}
