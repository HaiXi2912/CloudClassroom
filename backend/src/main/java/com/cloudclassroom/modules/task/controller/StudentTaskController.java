package com.cloudclassroom.modules.task.controller;

import com.cloudclassroom.common.ApiResponse;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.service.FileObjectService;
import com.cloudclassroom.modules.task.dto.StepStudyStatusRow;
import com.cloudclassroom.modules.task.dto.TaskLinkRow;
import com.cloudclassroom.modules.task.dto.TaskRow;
import com.cloudclassroom.modules.task.dto.TaskStepRow;
import com.cloudclassroom.modules.task.service.TaskService;
import com.cloudclassroom.security.SecurityUtil;
import com.cloudclassroom.security.annotation.RequireRole;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 学生端：任务（学习路径）。
 */
@RestController
@RequestMapping("/api/student/tasks")
@RequireRole({"STUDENT"})
public class StudentTaskController {

  private final TaskService taskService;
  private final FileObjectService fileObjectService;

  public StudentTaskController(TaskService taskService, FileObjectService fileObjectService) {
    this.taskService = taskService;
    this.fileObjectService = fileObjectService;
  }

  @GetMapping
  public ApiResponse<List<TaskRow>> list(@RequestParam("courseId") @NotNull @Min(1) Long courseId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listStudentTasks(studentId, courseId));
  }

  @GetMapping("/{taskId}/steps")
  public ApiResponse<List<TaskStepRow>> listSteps(@PathVariable Long taskId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listStepsForStudent(studentId, taskId));
  }

  @PostMapping("/steps/{stepId}/done")
  public ApiResponse<Void> markDone(@PathVariable Long stepId, @Valid @RequestBody MarkDoneRequest req) {
    Long studentId = SecurityUtil.requireUserId();
    taskService.markStepDone(studentId, stepId, req.getDone() != null && req.getDone() == 1);
    return ApiResponse.ok();
  }

  /**
   * 获取某个章节的学习状态（正文 + 附件）。
   */
  @GetMapping("/steps/{stepId}/study")
  public ApiResponse<StepStudyStatusRow> studyStatus(@PathVariable Long stepId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.getStepStudyStatusForStudent(studentId, stepId));
  }

  /**
   * 上报章节正文已学习完成。
   */
  @PostMapping("/steps/{stepId}/content/done")
  public ApiResponse<Void> markContentDone(@PathVariable Long stepId) {
    Long studentId = SecurityUtil.requireUserId();
    taskService.markStepContentDone(studentId, stepId);
    return ApiResponse.ok();
  }

  /**
   * 上报附件已学习完成。
   */
  @PostMapping("/attachments/{attachmentId}/done")
  public ApiResponse<Void> markAttachmentDone(@PathVariable Long attachmentId) {
    Long studentId = SecurityUtil.requireUserId();
    taskService.markAttachmentDone(studentId, attachmentId);
    return ApiResponse.ok();
  }

  /**
   * 上报附件学习进度（用于严格完成校验）。
   */
  @PostMapping("/attachments/{attachmentId}/progress")
  public ApiResponse<Void> reportAttachmentProgress(@PathVariable Long attachmentId,
                                                    @Valid @RequestBody AttachmentProgressRequest req) {
    Long studentId = SecurityUtil.requireUserId();
    taskService.reportAttachmentProgress(studentId,
        attachmentId,
        req.getProgressPercent(),
        req.getPositionSeconds(),
        req.getDurationSeconds());
    return ApiResponse.ok();
  }

  /**
   * 下载/预览附件文件（学生端）。
   */
  @GetMapping("/attachments/{attachmentId}/file")
  public ResponseEntity<byte[]> downloadAttachmentFile(@PathVariable Long attachmentId) {
    Long studentId = SecurityUtil.requireUserId();
    FileObject fo = taskService.getAttachmentFileForStudent(studentId, attachmentId);
    byte[] bytes = fileObjectService.readBytes(fo);

    String filename = fo.getOriginalName() == null ? "file" : fo.getOriginalName();
    String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encoded);
    return ResponseEntity.ok().headers(headers).body(bytes);
  }

  @GetMapping("/{taskId}/links")
  public ApiResponse<List<TaskLinkRow>> listLinks(@PathVariable Long taskId) {
    Long studentId = SecurityUtil.requireUserId();
    return ApiResponse.ok(taskService.listLinksForStudent(studentId, taskId));
  }

  public static class MarkDoneRequest {

    /**
     * 1完成 0未完成。
     */
    @NotNull(message = "done 不能为空")
    private Integer done;

    public Integer getDone() {
      return done;
    }

    public void setDone(Integer done) {
      this.done = done;
    }
  }

  public static class AttachmentProgressRequest {

    /**
     * 进度百分比：0-100（可空）。
     */
    private Integer progressPercent;

    /**
     * 视频：当前播放秒数（可空）。
     */
    private Integer positionSeconds;

    /**
     * 视频：总时长秒数（可空）。
     */
    private Integer durationSeconds;

    public Integer getProgressPercent() {
      return progressPercent;
    }

    public void setProgressPercent(Integer progressPercent) {
      this.progressPercent = progressPercent;
    }

    public Integer getPositionSeconds() {
      return positionSeconds;
    }

    public void setPositionSeconds(Integer positionSeconds) {
      this.positionSeconds = positionSeconds;
    }

    public Integer getDurationSeconds() {
      return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
      this.durationSeconds = durationSeconds;
    }
  }
}
