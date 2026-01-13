package com.cloudclassroom.modules.task.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.service.FileObjectService;
import com.cloudclassroom.modules.task.dto.TaskStepAttachmentRow;
import com.cloudclassroom.modules.task.dto.TaskLinkRow;
import com.cloudclassroom.modules.task.dto.TaskRow;
import com.cloudclassroom.modules.task.dto.TaskStepRow;
import com.cloudclassroom.modules.task.entity.Task;
import com.cloudclassroom.modules.task.entity.TaskLink;
import com.cloudclassroom.modules.task.entity.TaskStep;
import com.cloudclassroom.modules.task.entity.TaskStepAttachment;
import com.cloudclassroom.modules.task.service.TaskService;
import com.cloudclassroom.security.SecurityUtil;
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
 * 老师端：任务（学习路径）管理。
 */
@RestController
@RequestMapping("/api/teacher/tasks")
@RequireRole({"TEACHER"})
public class TeacherTaskController {

  private final TaskService taskService;
  private final FileObjectService fileObjectService;

  public TeacherTaskController(TaskService taskService, FileObjectService fileObjectService) {
    this.taskService = taskService;
    this.fileObjectService = fileObjectService;
  }

  @PostMapping
  public ApiResponse<Long> create(@Valid @RequestBody CreateTaskRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    Task t = taskService.createTask(teacherId, req.getCourseId(), req.getTitle(), req.getDescription(), req.getSortNo());
    return ApiResponse.ok(t.getId());
  }

  @GetMapping
  public ApiResponse<List<TaskRow>> list(@RequestParam("courseId") @NotNull @Min(1) Long courseId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listTeacherTasks(teacherId, courseId));
  }

  @PostMapping("/{taskId}/steps")
  public ApiResponse<Long> createStep(@PathVariable Long taskId, @Valid @RequestBody CreateStepRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    TaskStep s = taskService.createStep(
        teacherId,
        taskId,
        req.getTitle(),
        req.getContent(),
        req.getSortNo(),
        req.getStepType(),
        req.getParentId()
    );
    return ApiResponse.ok(s.getId());
  }

  @GetMapping("/{taskId}/steps")
  public ApiResponse<List<TaskStepRow>> listSteps(@PathVariable Long taskId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listStepsForTeacher(teacherId, taskId));
  }

  @PutMapping("/steps/{stepId}")
  public ApiResponse<Void> updateStep(@PathVariable Long stepId, @RequestBody UpdateStepRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    taskService.updateStep(
        teacherId,
        stepId,
        req.getTitle(),
        req.getContent(),
        req.getSortNo(),
        req.getStepType(),
        req.getParentId()
    );
    return ApiResponse.ok();
  }

  @GetMapping("/steps/{stepId}/attachments")
  public ApiResponse<List<TaskStepAttachmentRow>> listStepAttachments(@PathVariable Long stepId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listStepAttachmentsForTeacher(teacherId, stepId));
  }

  @PostMapping("/steps/{stepId}/attachments")
  public ApiResponse<Long> createStepAttachment(@PathVariable Long stepId, @Valid @RequestBody CreateAttachmentRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    TaskStepAttachment a = taskService.createStepAttachment(
        teacherId,
        stepId,
        req.getKind(),
        req.getTitle(),
        req.getFileId(),
        req.getUrl(),
        req.getSortNo()
    );
    return ApiResponse.ok(a.getId());
  }

  @DeleteMapping("/attachments/{attachmentId}")
  public ApiResponse<Void> deleteAttachment(@PathVariable Long attachmentId) {
    Long teacherId = SecurityUtil.requireUserId();
    taskService.deleteStepAttachment(teacherId, attachmentId);
    return ApiResponse.ok();
  }

  @DeleteMapping("/steps/{stepId}")
  public ApiResponse<Void> deleteStep(@PathVariable Long stepId) {
    Long teacherId = SecurityUtil.requireUserId();
    taskService.deleteStep(teacherId, stepId);
    return ApiResponse.ok();
  }

  @GetMapping("/attachments/{attachmentId}/file")
  public ResponseEntity<byte[]> downloadAttachmentFile(@PathVariable Long attachmentId) {
    Long teacherId = SecurityUtil.requireUserId();
    FileObject fo = taskService.getAttachmentFileForTeacher(teacherId, attachmentId);
    byte[] bytes = fileObjectService.readBytes(fo);

    String filename = fo.getOriginalName() == null ? "file" : fo.getOriginalName();
    String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encoded);
    return ResponseEntity.ok().headers(headers).body(bytes);
  }

  @PostMapping("/{taskId}/links")
  public ApiResponse<Long> createLink(@PathVariable Long taskId, @Valid @RequestBody CreateLinkRequest req) {
    Long teacherId = SecurityUtil.requireUserId();
    TaskLink link = taskService.createLink(teacherId, taskId, req.getStepId(), req.getLinkType(), req.getRefId(), req.getSortNo());
    return ApiResponse.ok(link.getId());
  }

  @GetMapping("/{taskId}/links")
  public ApiResponse<List<TaskLinkRow>> listLinks(@PathVariable Long taskId) {
    Long teacherId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listLinksForTeacher(teacherId, taskId));
  }

  @DeleteMapping("/links/{linkId}")
  public ApiResponse<Void> deleteLink(@PathVariable Long linkId) {
    Long teacherId = SecurityUtil.requireUserId();
    taskService.deleteLink(teacherId, linkId);
    return ApiResponse.ok();
  }

  public static class CreateTaskRequest {

    @NotNull(message = "courseId 不能为空")
    private Long courseId;

    @NotBlank(message = "title 不能为空")
    private String title;

    private String description;

    private Integer sortNo;

    public Long getCourseId() {
      return courseId;
    }

    public void setCourseId(Long courseId) {
      this.courseId = courseId;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public Integer getSortNo() {
      return sortNo;
    }

    public void setSortNo(Integer sortNo) {
      this.sortNo = sortNo;
    }
  }

  public static class CreateStepRequest {

    @NotBlank(message = "title 不能为空")
    private String title;

    /**
     * 可选：1=节点 2=内容。默认 2。
     */
    private Integer stepType;

    /**
     * 可选：父节点ID（仅内容可设置）。
     */
    private Long parentId;

    private String content;

    private Integer sortNo;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public Integer getStepType() {
      return stepType;
    }

    public void setStepType(Integer stepType) {
      this.stepType = stepType;
    }

    public Long getParentId() {
      return parentId;
    }

    public void setParentId(Long parentId) {
      this.parentId = parentId;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public Integer getSortNo() {
      return sortNo;
    }

    public void setSortNo(Integer sortNo) {
      this.sortNo = sortNo;
    }
  }

  public static class CreateLinkRequest {

    private Long stepId;

    @NotNull(message = "linkType 不能为空")
    private Integer linkType;

    @NotNull(message = "refId 不能为空")
    private Long refId;

    private Integer sortNo;

    public Long getStepId() {
      return stepId;
    }

    public void setStepId(Long stepId) {
      this.stepId = stepId;
    }

    public Integer getLinkType() {
      return linkType;
    }

    public void setLinkType(Integer linkType) {
      this.linkType = linkType;
    }

    public Long getRefId() {
      return refId;
    }

    public void setRefId(Long refId) {
      this.refId = refId;
    }

    public Integer getSortNo() {
      return sortNo;
    }

    public void setSortNo(Integer sortNo) {
      this.sortNo = sortNo;
    }
  }

  public static class UpdateStepRequest {

    /**
     * 可选：更新标题。
     */
    private String title;

    /**
     * 可选：更新内容。
     */
    private String content;

    /**
     * 可选：更新排序号。
     */
    private Integer sortNo;

    /**
     * 可选：更新类型（1节点 2内容）。
     */
    private Integer stepType;

    /**
     * 可选：更新父节点ID。
     */
    private Long parentId;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }

    public Integer getSortNo() {
      return sortNo;
    }

    public void setSortNo(Integer sortNo) {
      this.sortNo = sortNo;
    }

    public Integer getStepType() {
      return stepType;
    }

    public void setStepType(Integer stepType) {
      this.stepType = stepType;
    }

    public Long getParentId() {
      return parentId;
    }

    public void setParentId(Long parentId) {
      this.parentId = parentId;
    }
  }

  public static class CreateAttachmentRequest {

    @NotNull(message = "kind 不能为空")
    private Integer kind;

    @NotBlank(message = "title 不能为空")
    private String title;

    private Long fileId;

    private String url;

    private Integer sortNo;

    public Integer getKind() {
      return kind;
    }

    public void setKind(Integer kind) {
      this.kind = kind;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public Long getFileId() {
      return fileId;
    }

    public void setFileId(Long fileId) {
      this.fileId = fileId;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public Integer getSortNo() {
      return sortNo;
    }

    public void setSortNo(Integer sortNo) {
      this.sortNo = sortNo;
    }
  }
}
