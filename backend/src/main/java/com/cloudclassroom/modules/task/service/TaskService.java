package com.cloudclassroom.modules.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.assignment.entity.Assignment;
import com.cloudclassroom.modules.assignment.mapper.AssignmentMapper;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.entity.CourseMember;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import com.cloudclassroom.modules.exam.entity.Exam;
import com.cloudclassroom.modules.exam.mapper.ExamMapper;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.mapper.FileObjectMapper;
import com.cloudclassroom.modules.task.dto.StepStudyStatusRow;
import com.cloudclassroom.modules.task.dto.TaskStepAttachmentRow;
import com.cloudclassroom.modules.task.dto.TaskLinkRow;
import com.cloudclassroom.modules.task.dto.TaskRow;
import com.cloudclassroom.modules.task.dto.TaskStepRow;
import com.cloudclassroom.modules.task.entity.Task;
import com.cloudclassroom.modules.task.entity.TaskLink;
import com.cloudclassroom.modules.task.entity.TaskStep;
import com.cloudclassroom.modules.task.entity.TaskStepAttachment;
import com.cloudclassroom.modules.task.entity.TaskStepAttachmentProgress;
import com.cloudclassroom.modules.task.entity.TaskStepContentProgress;
import com.cloudclassroom.modules.task.entity.TaskStepProgress;
import com.cloudclassroom.modules.task.mapper.TaskStepAttachmentMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepAttachmentProgressMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepContentProgressMapper;
import com.cloudclassroom.modules.task.mapper.TaskLinkMapper;
import com.cloudclassroom.modules.task.mapper.TaskMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepProgressMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 任务（学习路径）服务：任务/步骤/完成进度/关联作业考试。
 */
@Service
public class TaskService {

  private static final int STEP_TYPE_NODE = 1;
  private static final int STEP_TYPE_CONTENT = 2;

  private final TaskMapper taskMapper;
  private final TaskStepMapper taskStepMapper;
  private final TaskStepProgressMapper taskStepProgressMapper;
  private final TaskLinkMapper taskLinkMapper;
  private final CourseMapper courseMapper;
  private final CourseMemberMapper courseMemberMapper;
  private final AssignmentMapper assignmentMapper;
  private final ExamMapper examMapper;

  private final TaskStepAttachmentMapper taskStepAttachmentMapper;
  private final TaskStepContentProgressMapper taskStepContentProgressMapper;
  private final TaskStepAttachmentProgressMapper taskStepAttachmentProgressMapper;

  private final FileObjectMapper fileObjectMapper;

  public TaskService(TaskMapper taskMapper,
                     TaskStepMapper taskStepMapper,
                     TaskStepProgressMapper taskStepProgressMapper,
                     TaskLinkMapper taskLinkMapper,
                     CourseMapper courseMapper,
                     CourseMemberMapper courseMemberMapper,
                     AssignmentMapper assignmentMapper,
                     ExamMapper examMapper,
                     TaskStepAttachmentMapper taskStepAttachmentMapper,
                     TaskStepContentProgressMapper taskStepContentProgressMapper,
                     TaskStepAttachmentProgressMapper taskStepAttachmentProgressMapper,
                     FileObjectMapper fileObjectMapper) {
    this.taskMapper = taskMapper;
    this.taskStepMapper = taskStepMapper;
    this.taskStepProgressMapper = taskStepProgressMapper;
    this.taskLinkMapper = taskLinkMapper;
    this.courseMapper = courseMapper;
    this.courseMemberMapper = courseMemberMapper;
    this.assignmentMapper = assignmentMapper;
    this.examMapper = examMapper;

    this.taskStepAttachmentMapper = taskStepAttachmentMapper;
    this.taskStepContentProgressMapper = taskStepContentProgressMapper;
    this.taskStepAttachmentProgressMapper = taskStepAttachmentProgressMapper;

    this.fileObjectMapper = fileObjectMapper;
  }

  public Task createTask(Long teacherId, Long courseId, String title, String description, Integer sortNo) {
    Course course = requireTeacherOwnsCourse(teacherId, courseId);
    if (course.getStatus() == null || course.getStatus() == 0) {
      throw new BusinessException(40010, "课程已停用");
    }

    Task t = new Task();
    t.setCourseId(courseId);
    t.setTitle(title);
    t.setDescription(description);
    t.setStatus(1);
    t.setSortNo(sortNo == null ? 0 : sortNo);
    t.setCreatedBy(teacherId);
    t.setCreatedAt(LocalDateTime.now());
    t.setUpdatedAt(LocalDateTime.now());
    t.setDeleted(0);
    taskMapper.insert(t);
    return t;
  }

  public List<TaskRow> listTeacherTasks(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);

    List<Task> list = taskMapper.selectList(new LambdaQueryWrapper<Task>()
        .eq(Task::getCourseId, courseId)
        .eq(Task::getDeleted, 0)
        .orderByAsc(Task::getSortNo)
        .orderByDesc(Task::getId));

    return list.stream().map(this::toTaskRow).collect(Collectors.toList());
  }

  public List<TaskRow> listStudentTasks(Long studentId, Long courseId) {
    requireStudentInCourse(studentId, courseId);

    List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
        .eq(Task::getCourseId, courseId)
        .eq(Task::getDeleted, 0)
        .eq(Task::getStatus, 1)
        .orderByAsc(Task::getSortNo)
        .orderByDesc(Task::getId));

    if (tasks.isEmpty()) {
      return List.of();
    }

    List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

    List<TaskStep> steps = taskStepMapper.selectList(new LambdaQueryWrapper<TaskStep>()
        .in(TaskStep::getTaskId, taskIds)
        .eq(TaskStep::getDeleted, 0));

    Map<Long, Long> stepIdToTaskId = new HashMap<>();
    Map<Long, Integer> taskTotalSteps = new HashMap<>();

    for (TaskStep s : steps) {
      stepIdToTaskId.put(s.getId(), s.getTaskId());
      if (!isNodeStep(s.getStepType())) {
        taskTotalSteps.put(s.getTaskId(), taskTotalSteps.getOrDefault(s.getTaskId(), 0) + 1);
      }
    }

    Set<Long> allStepIds = steps.stream()
      .filter(s -> !isNodeStep(s.getStepType()))
      .map(TaskStep::getId)
      .collect(Collectors.toSet());
    Map<Long, Integer> taskDoneSteps = new HashMap<>();

    if (!allStepIds.isEmpty()) {
      List<TaskStepProgress> progresses = taskStepProgressMapper.selectList(new LambdaQueryWrapper<TaskStepProgress>()
          .in(TaskStepProgress::getStepId, allStepIds)
          .eq(TaskStepProgress::getUserId, studentId)
          .eq(TaskStepProgress::getStatus, 2)
          .eq(TaskStepProgress::getDeleted, 0));

      for (TaskStepProgress p : progresses) {
        Long taskId = stepIdToTaskId.get(p.getStepId());
        if (taskId == null) {
          continue;
        }
        taskDoneSteps.put(taskId, taskDoneSteps.getOrDefault(taskId, 0) + 1);
      }
    }

    List<TaskRow> rows = new ArrayList<>();
    for (Task t : tasks) {
      TaskRow r = toTaskRow(t);

      int total = taskTotalSteps.getOrDefault(t.getId(), 0);
      int done = taskDoneSteps.getOrDefault(t.getId(), 0);
      r.setTotalSteps(total);
      r.setDoneSteps(done);
      r.setProgressPercent(calcPercent(done, total));

      rows.add(r);
    }

    return rows;
  }

  public TaskStep createStep(Long teacherId,
                             Long taskId,
                             String title,
                             String content,
                             Integer sortNo,
                             Integer stepType,
                             Long parentId) {
    Task task = requireTeacherOwnsTask(teacherId, taskId);

    int finalType = normalizeStepType(stepType);
    Long finalParentId = normalizeAndValidateParentId(task.getId(), finalType, parentId, null);

    int finalSort = sortNo == null ? nextStepSortNo(task.getId(), finalParentId) : sortNo;

    TaskStep s = new TaskStep();
    s.setTaskId(task.getId());
    s.setStepType(finalType);
    s.setParentId(finalParentId);
    s.setTitle(title);
    s.setContent(finalType == STEP_TYPE_NODE ? (content == null ? "" : content) : content);
    s.setSortNo(finalSort);
    s.setCreatedAt(LocalDateTime.now());
    s.setUpdatedAt(LocalDateTime.now());
    s.setDeleted(0);

    taskStepMapper.insert(s);
    return s;
  }

  public List<TaskStepRow> listStepsForTeacher(Long teacherId, Long taskId) {
    requireTeacherOwnsTask(teacherId, taskId);

    List<TaskStep> list = taskStepMapper.selectList(new LambdaQueryWrapper<TaskStep>()
        .eq(TaskStep::getTaskId, taskId)
        .eq(TaskStep::getDeleted, 0)
        .orderByAsc(TaskStep::getParentId)
        .orderByAsc(TaskStep::getSortNo)
        .orderByAsc(TaskStep::getId));

    return list.stream().map(s -> {
      TaskStepRow r = toStepRow(s);
      r.setDone(null);
      return r;
    }).collect(Collectors.toList());
  }

  public List<TaskStepRow> listStepsForStudent(Long studentId, Long taskId) {
    Task task = requireStudentCanAccessTask(studentId, taskId);
    if (task.getStatus() == null || task.getStatus() == 0) {
      throw new BusinessException(40421, "任务不存在或已停用");
    }

    List<TaskStep> list = taskStepMapper.selectList(new LambdaQueryWrapper<TaskStep>()
        .eq(TaskStep::getTaskId, taskId)
        .eq(TaskStep::getDeleted, 0)
      .orderByAsc(TaskStep::getParentId)
      .orderByAsc(TaskStep::getSortNo)
        .orderByAsc(TaskStep::getId));

    if (list.isEmpty()) {
      return List.of();
    }

    List<Long> stepIds = list.stream().map(TaskStep::getId).collect(Collectors.toList());
    Map<Long, Integer> doneMap = new HashMap<>();

    List<TaskStepProgress> progresses = taskStepProgressMapper.selectList(new LambdaQueryWrapper<TaskStepProgress>()
        .in(TaskStepProgress::getStepId, stepIds)
        .eq(TaskStepProgress::getUserId, studentId)
        .eq(TaskStepProgress::getDeleted, 0));

    Set<Long> nodeStepIds = list.stream()
      .filter(s -> isNodeStep(s.getStepType()))
      .map(TaskStep::getId)
      .collect(Collectors.toSet());

    for (TaskStepProgress p : progresses) {
      if (nodeStepIds.contains(p.getStepId())) {
        continue;
      }
      if (p.getStatus() != null && p.getStatus() == 2) {
        doneMap.put(p.getStepId(), 1);
      }
    }

    return list.stream().map(s -> {
      TaskStepRow r = toStepRow(s);
      r.setDone(doneMap.getOrDefault(s.getId(), 0));
      return r;
    }).collect(Collectors.toList());
  }

  public void markStepDone(Long studentId, Long stepId, boolean done) {
    TaskStep step = taskStepMapper.selectById(stepId);
    if (step == null || step.getDeleted() == null || step.getDeleted() != 0) {
      throw new BusinessException(40422, "步骤不存在");
    }

    if (isNodeStep(step.getStepType())) {
      throw new BusinessException(40062, "节点不可标记完成");
    }

    Task task = requireStudentCanAccessTask(studentId, step.getTaskId());
    if (task.getStatus() == null || task.getStatus() == 0) {
      throw new BusinessException(40421, "任务不存在或已停用");
    }

    if (done) {
      ensureStepStudyCompleted(studentId, step);
    }

    TaskStepProgress existed = taskStepProgressMapper.selectOne(new LambdaQueryWrapper<TaskStepProgress>()
        .eq(TaskStepProgress::getStepId, stepId)
        .eq(TaskStepProgress::getUserId, studentId)
        .last("LIMIT 1"));

    int targetStatus = done ? 2 : 1;

    if (existed == null) {
      TaskStepProgress p = new TaskStepProgress();
      p.setStepId(stepId);
      p.setUserId(studentId);
      p.setStatus(targetStatus);
      p.setCompletedAt(done ? LocalDateTime.now() : null);
      p.setCreatedAt(LocalDateTime.now());
      p.setUpdatedAt(LocalDateTime.now());
      p.setDeleted(0);
      taskStepProgressMapper.insert(p);
      return;
    }

    existed.setDeleted(0);
    existed.setStatus(targetStatus);
    existed.setCompletedAt(done ? LocalDateTime.now() : null);
    existed.setUpdatedAt(LocalDateTime.now());
    taskStepProgressMapper.updateById(existed);
  }

  /**
   * 老师端：查询某个步骤的附件列表。
   */
  public List<TaskStepAttachmentRow> listStepAttachmentsForTeacher(Long teacherId, Long stepId) {
    TaskStep step = requireTeacherOwnsStep(teacherId, stepId);

    List<TaskStepAttachment> list = taskStepAttachmentMapper.selectList(new LambdaQueryWrapper<TaskStepAttachment>()
        .eq(TaskStepAttachment::getStepId, step.getId())
        .eq(TaskStepAttachment::getDeleted, 0)
        .orderByAsc(TaskStepAttachment::getSortNo)
        .orderByAsc(TaskStepAttachment::getId));

    return toAttachmentRows(list, null);
  }

  /**
   * 老师端：新增步骤附件。
   */
  public TaskStepAttachment createStepAttachment(Long teacherId,
                                                 Long stepId,
                                                 Integer kind,
                                                 String title,
                                                 Long fileId,
                                                 String url,
                                                 Integer sortNo) {
    TaskStep step = requireTeacherOwnsStep(teacherId, stepId);

    if (isNodeStep(step.getStepType())) {
      throw new BusinessException(40061, "节点不能绑定附件");
    }

    if (kind == null || (kind != 1 && kind != 2 && kind != 3)) {
      throw new BusinessException(40061, "kind 无效");
    }
    if (title == null || title.trim().isEmpty()) {
      throw new BusinessException(40001, "title 不能为空");
    }

    boolean hasFile = fileId != null && fileId > 0;
    boolean hasUrl = url != null && !url.trim().isEmpty();
    if (hasFile == hasUrl) {
      throw new BusinessException(40061, "fileId 与 url 必须二选一");
    }

    if (hasFile) {
      FileObject fo = fileObjectMapper.selectById(fileId);
      if (fo == null || fo.getDeleted() == null || fo.getDeleted() != 0) {
        throw new BusinessException(40420, "文件不存在");
      }
    }

    int finalSort = sortNo == null ? nextAttachmentSortNo(step.getId()) : sortNo;

    TaskStepAttachment a = new TaskStepAttachment();
    a.setStepId(step.getId());
    a.setKind(kind);
    a.setTitle(title);
    a.setFileId(hasFile ? fileId : null);
    a.setUrl(hasUrl ? url : null);
    a.setSortNo(finalSort);
    a.setCreatedAt(LocalDateTime.now());
    a.setUpdatedAt(LocalDateTime.now());
    a.setDeleted(0);
    taskStepAttachmentMapper.insert(a);
    return a;
  }

  /**
   * 老师端：删除步骤附件。
   */
  public void deleteStepAttachment(Long teacherId, Long attachmentId) {
    TaskStepAttachment a = taskStepAttachmentMapper.selectById(attachmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      return;
    }
    requireTeacherOwnsStep(teacherId, a.getStepId());
    taskStepAttachmentMapper.deleteById(attachmentId);
  }

  /**
   * 老师端：删除步骤（节点/内容）。
   *
   * <p>节点删除：会把其子内容章节的 parentId 置空（变为“未分组内容”），再删除节点本身。
   * 内容删除：会同步删除该内容的附件、关联、以及学生进度（均为逻辑删除）。
   */
  public void deleteStep(Long teacherId, Long stepId) {
    TaskStep step = requireTeacherOwnsStep(teacherId, stepId);

    // 1) 如果是节点：先把子内容章节移到“未分组”，避免悬挂。
    if (isNodeStep(step.getStepType())) {
      taskStepMapper.update(
          null,
          new LambdaUpdateWrapper<TaskStep>()
              .eq(TaskStep::getTaskId, step.getTaskId())
              .eq(TaskStep::getParentId, step.getId())
              .set(TaskStep::getParentId, null)
      );
    } else {
      // 2) 内容删除：删除附件与进度
      List<TaskStepAttachment> as = taskStepAttachmentMapper.selectList(new LambdaQueryWrapper<TaskStepAttachment>()
          .eq(TaskStepAttachment::getStepId, step.getId())
          .eq(TaskStepAttachment::getDeleted, 0));
      List<Long> attachmentIds = as.stream().map(TaskStepAttachment::getId).collect(Collectors.toList());

      if (!attachmentIds.isEmpty()) {
        taskStepAttachmentProgressMapper.delete(new LambdaQueryWrapper<TaskStepAttachmentProgress>()
            .in(TaskStepAttachmentProgress::getAttachmentId, attachmentIds));
      }

      taskStepAttachmentMapper.delete(new LambdaQueryWrapper<TaskStepAttachment>()
          .eq(TaskStepAttachment::getStepId, step.getId()));

      taskStepContentProgressMapper.delete(new LambdaQueryWrapper<TaskStepContentProgress>()
          .eq(TaskStepContentProgress::getStepId, step.getId()));

      taskStepProgressMapper.delete(new LambdaQueryWrapper<TaskStepProgress>()
          .eq(TaskStepProgress::getStepId, step.getId()));
    }

    // 3) 删除关联（节点理论上不会有，但这里做兜底）
    taskLinkMapper.delete(new LambdaQueryWrapper<TaskLink>()
        .eq(TaskLink::getStepId, step.getId()));

    // 4) 删除步骤本身（逻辑删除）
    taskStepMapper.deleteById(step.getId());
  }

  /**
   * 老师端：获取附件文件对象（用于下载）。
   */
  public FileObject getAttachmentFileForTeacher(Long teacherId, Long attachmentId) {
    TaskStepAttachment a = taskStepAttachmentMapper.selectById(attachmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      throw new BusinessException(40463, "附件不存在");
    }
    requireTeacherOwnsStep(teacherId, a.getStepId());
    if (a.getFileId() == null) {
      throw new BusinessException(40063, "该附件不是文件类型");
    }

    FileObject fo = fileObjectMapper.selectById(a.getFileId());
    if (fo == null || fo.getDeleted() == null || fo.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }
    return fo;
  }

  /**
   * 学生端：获取章节学习状态（正文完成 + 附件完成）。
   */
  public StepStudyStatusRow getStepStudyStatusForStudent(Long studentId, Long stepId) {
    TaskStep step = requireStudentCanAccessStep(studentId, stepId);

    Integer contentStatus = 1;
    TaskStepContentProgress cp = taskStepContentProgressMapper.selectOne(new LambdaQueryWrapper<TaskStepContentProgress>()
        .eq(TaskStepContentProgress::getStepId, step.getId())
        .eq(TaskStepContentProgress::getUserId, studentId)
        .eq(TaskStepContentProgress::getDeleted, 0)
        .last("LIMIT 1"));
    if (cp != null && cp.getStatus() != null && cp.getStatus() == 2) {
      contentStatus = 2;
    }

    List<TaskStepAttachment> attachments = taskStepAttachmentMapper.selectList(new LambdaQueryWrapper<TaskStepAttachment>()
        .eq(TaskStepAttachment::getStepId, step.getId())
        .eq(TaskStepAttachment::getDeleted, 0)
        .orderByAsc(TaskStepAttachment::getSortNo)
        .orderByAsc(TaskStepAttachment::getId));

    List<Long> attachmentIds = attachments.stream().map(TaskStepAttachment::getId).collect(Collectors.toList());
    Map<Long, TaskStepAttachmentProgress> progressMap = new HashMap<>();
    if (!attachmentIds.isEmpty()) {
      List<TaskStepAttachmentProgress> ps = taskStepAttachmentProgressMapper.selectList(
          new LambdaQueryWrapper<TaskStepAttachmentProgress>()
              .in(TaskStepAttachmentProgress::getAttachmentId, attachmentIds)
              .eq(TaskStepAttachmentProgress::getUserId, studentId)
              .eq(TaskStepAttachmentProgress::getDeleted, 0));
      for (TaskStepAttachmentProgress p : ps) {
        progressMap.put(p.getAttachmentId(), p);
      }
    }

    List<TaskStepAttachmentRow> rows = toAttachmentRowsForStudent(attachments, progressMap);

    StepStudyStatusRow out = new StepStudyStatusRow();
    out.setStepId(step.getId());
    out.setContentStatus(contentStatus);
    out.setAttachments(rows);
    return out;
  }

  /**
   * 学生端：将章节正文标记为已完成。
   */
  public void markStepContentDone(Long studentId, Long stepId) {
    TaskStep step = requireStudentCanAccessStep(studentId, stepId);
    requireTaskActiveForStudent(studentId, step.getTaskId());

    TaskStepContentProgress existed = taskStepContentProgressMapper.selectOne(new LambdaQueryWrapper<TaskStepContentProgress>()
        .eq(TaskStepContentProgress::getStepId, stepId)
        .eq(TaskStepContentProgress::getUserId, studentId)
        .last("LIMIT 1"));

    if (existed == null) {
      TaskStepContentProgress p = new TaskStepContentProgress();
      p.setStepId(stepId);
      p.setUserId(studentId);
      p.setStatus(2);
      p.setCompletedAt(LocalDateTime.now());
      p.setCreatedAt(LocalDateTime.now());
      p.setUpdatedAt(LocalDateTime.now());
      p.setDeleted(0);
      taskStepContentProgressMapper.insert(p);
      return;
    }

    existed.setDeleted(0);
    existed.setStatus(2);
    existed.setCompletedAt(LocalDateTime.now());
    existed.setUpdatedAt(LocalDateTime.now());
    taskStepContentProgressMapper.updateById(existed);
  }

  /**
   * 学生端：将附件标记为已完成。
   */
  public void markAttachmentDone(Long studentId, Long attachmentId) {
    TaskStepAttachment a = taskStepAttachmentMapper.selectById(attachmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      throw new BusinessException(40463, "附件不存在");
    }

    TaskStep step = requireStudentCanAccessStep(studentId, a.getStepId());
    requireTaskActiveForStudent(studentId, step.getTaskId());

    TaskStepAttachmentProgress existed = taskStepAttachmentProgressMapper.selectOne(new LambdaQueryWrapper<TaskStepAttachmentProgress>()
        .eq(TaskStepAttachmentProgress::getAttachmentId, attachmentId)
        .eq(TaskStepAttachmentProgress::getUserId, studentId)
        .last("LIMIT 1"));

    // 对文件型附件（PDF/PPT/视频），不允许“手动点完成”绕过进度。
    // url 外链无法可靠校验，仍允许按原方式完成。
    boolean trackable = a.getUrl() == null || a.getUrl().trim().isEmpty();
    if (trackable && a.getKind() != null && (a.getKind() == 1 || a.getKind() == 2 || a.getKind() == 3)) {
      if (existed == null || existed.getStatus() == null || existed.getStatus() != 2) {
        throw new BusinessException(40064, "请完整学习该附件后自动完成");
      }
      // 已达标：直接返回（保持幂等）
      return;
    }

    if (existed == null) {
      TaskStepAttachmentProgress p = new TaskStepAttachmentProgress();
      p.setAttachmentId(attachmentId);
      p.setUserId(studentId);
      p.setStatus(2);
      p.setProgressPercent(100);
      p.setCompletedAt(LocalDateTime.now());
      p.setCreatedAt(LocalDateTime.now());
      p.setUpdatedAt(LocalDateTime.now());
      p.setLastReportedAt(LocalDateTime.now());
      p.setDeleted(0);
      taskStepAttachmentProgressMapper.insert(p);
      return;
    }

    existed.setDeleted(0);
    existed.setStatus(2);
    existed.setProgressPercent(existed.getProgressPercent() == null ? 100 : Math.max(existed.getProgressPercent(), 100));
    existed.setCompletedAt(LocalDateTime.now());
    existed.setUpdatedAt(LocalDateTime.now());
    existed.setLastReportedAt(LocalDateTime.now());
    taskStepAttachmentProgressMapper.updateById(existed);
  }

  /**
   * 学生端：上报附件学习进度（用于严格完成校验）。
   */
  public void reportAttachmentProgress(Long studentId,
                                      Long attachmentId,
                                      Integer progressPercent,
                                      Integer positionSeconds,
                                      Integer durationSeconds) {
    TaskStepAttachment a = taskStepAttachmentMapper.selectById(attachmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      throw new BusinessException(40463, "附件不存在");
    }

    TaskStep step = requireStudentCanAccessStep(studentId, a.getStepId());
    requireTaskActiveForStudent(studentId, step.getTaskId());

    int pp = progressPercent == null ? 0 : progressPercent;
    if (pp < 0) pp = 0;
    if (pp > 100) pp = 100;

    // 视频：若提供 position/duration，则用它计算更可信的进度
    if (a.getKind() != null && a.getKind() == 3 && durationSeconds != null && durationSeconds > 0 && positionSeconds != null && positionSeconds >= 0) {
      int calc = (int) Math.floor((positionSeconds * 100.0) / durationSeconds);
      if (calc < 0) calc = 0;
      if (calc > 100) calc = 100;
      pp = Math.max(pp, calc);
    }

    boolean completed = pp >= 100;

    TaskStepAttachmentProgress existed = taskStepAttachmentProgressMapper.selectOne(new LambdaQueryWrapper<TaskStepAttachmentProgress>()
        .eq(TaskStepAttachmentProgress::getAttachmentId, attachmentId)
        .eq(TaskStepAttachmentProgress::getUserId, studentId)
        .last("LIMIT 1"));

    if (existed == null) {
      TaskStepAttachmentProgress p = new TaskStepAttachmentProgress();
      p.setAttachmentId(attachmentId);
      p.setUserId(studentId);
      p.setStatus(completed ? 2 : 1);
      p.setProgressPercent(pp);
      p.setPositionSeconds(positionSeconds);
      p.setDurationSeconds(durationSeconds);
      p.setCompletedAt(completed ? LocalDateTime.now() : null);
      p.setLastReportedAt(LocalDateTime.now());
      p.setCreatedAt(LocalDateTime.now());
      p.setUpdatedAt(LocalDateTime.now());
      p.setDeleted(0);
      taskStepAttachmentProgressMapper.insert(p);
      return;
    }

    existed.setDeleted(0);
    existed.setProgressPercent(existed.getProgressPercent() == null ? pp : Math.max(existed.getProgressPercent(), pp));
    existed.setPositionSeconds(positionSeconds);
    existed.setDurationSeconds(durationSeconds);
    existed.setLastReportedAt(LocalDateTime.now());

    if ((existed.getStatus() == null || existed.getStatus() != 2) && completed) {
      existed.setStatus(2);
      existed.setCompletedAt(LocalDateTime.now());
    }

    existed.setUpdatedAt(LocalDateTime.now());
    taskStepAttachmentProgressMapper.updateById(existed);
  }

  /**
   * 学生端：获取附件文件对象（用于下载/预览）。
   */
  public FileObject getAttachmentFileForStudent(Long studentId, Long attachmentId) {
    TaskStepAttachment a = taskStepAttachmentMapper.selectById(attachmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      throw new BusinessException(40463, "附件不存在");
    }

    TaskStep step = requireStudentCanAccessStep(studentId, a.getStepId());
    Task task = requireStudentCanAccessTask(studentId, step.getTaskId());
    if (task.getStatus() == null || task.getStatus() == 0) {
      throw new BusinessException(40421, "任务不存在或已停用");
    }

    if (a.getFileId() == null) {
      throw new BusinessException(40063, "该附件不是文件类型");
    }
    FileObject fo = fileObjectMapper.selectById(a.getFileId());
    if (fo == null || fo.getDeleted() == null || fo.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }
    return fo;
  }

  private TaskStep requireTeacherOwnsStep(Long teacherId, Long stepId) {
    TaskStep step = taskStepMapper.selectById(stepId);
    if (step == null || step.getDeleted() == null || step.getDeleted() != 0) {
      throw new BusinessException(40422, "步骤不存在");
    }
    requireTeacherOwnsTask(teacherId, step.getTaskId());
    return step;
  }

  private TaskStep requireStudentCanAccessStep(Long studentId, Long stepId) {
    TaskStep step = taskStepMapper.selectById(stepId);
    if (step == null || step.getDeleted() == null || step.getDeleted() != 0) {
      throw new BusinessException(40422, "步骤不存在");
    }
    requireStudentCanAccessTask(studentId, step.getTaskId());
    return step;
  }

  private void requireTaskActiveForStudent(Long studentId, Long taskId) {
    Task task = requireStudentCanAccessTask(studentId, taskId);
    if (task.getStatus() == null || task.getStatus() == 0) {
      throw new BusinessException(40421, "任务不存在或已停用");
    }
  }

  private void ensureStepStudyCompleted(Long studentId, TaskStep step) {
    // 正文：有内容才强制
    boolean hasContent = step.getContent() != null && !step.getContent().trim().isEmpty();
    if (hasContent) {
      TaskStepContentProgress cp = taskStepContentProgressMapper.selectOne(new LambdaQueryWrapper<TaskStepContentProgress>()
          .eq(TaskStepContentProgress::getStepId, step.getId())
          .eq(TaskStepContentProgress::getUserId, studentId)
          .eq(TaskStepContentProgress::getDeleted, 0)
          .last("LIMIT 1"));
      if (cp == null || cp.getStatus() == null || cp.getStatus() != 2) {
        throw new BusinessException(40062, "请先学习完章节正文后再标记完成");
      }
    }

    List<TaskStepAttachment> attachments = taskStepAttachmentMapper.selectList(new LambdaQueryWrapper<TaskStepAttachment>()
        .eq(TaskStepAttachment::getStepId, step.getId())
        .eq(TaskStepAttachment::getDeleted, 0));

    if (attachments.isEmpty()) {
      return;
    }

    List<Long> attachmentIds = attachments.stream().map(TaskStepAttachment::getId).collect(Collectors.toList());
    List<TaskStepAttachmentProgress> ps = taskStepAttachmentProgressMapper.selectList(
        new LambdaQueryWrapper<TaskStepAttachmentProgress>()
            .in(TaskStepAttachmentProgress::getAttachmentId, attachmentIds)
            .eq(TaskStepAttachmentProgress::getUserId, studentId)
            .eq(TaskStepAttachmentProgress::getDeleted, 0));

    Set<Long> doneIds = new HashSet<>();
    for (TaskStepAttachmentProgress p : ps) {
      if (p.getStatus() != null && p.getStatus() == 2) {
        doneIds.add(p.getAttachmentId());
      }
    }

    for (Long id : attachmentIds) {
      if (!doneIds.contains(id)) {
        throw new BusinessException(40062, "请先学习完章节正文和所有附件后再标记完成");
      }
    }
  }

  private int nextAttachmentSortNo(Long stepId) {
    List<TaskStepAttachment> list = taskStepAttachmentMapper.selectList(new LambdaQueryWrapper<TaskStepAttachment>()
        .eq(TaskStepAttachment::getStepId, stepId)
        .eq(TaskStepAttachment::getDeleted, 0)
        .orderByDesc(TaskStepAttachment::getSortNo)
        .last("LIMIT 1"));
    if (list.isEmpty() || list.get(0).getSortNo() == null) {
      return 1;
    }
    return list.get(0).getSortNo() + 1;
  }

  private List<TaskStepAttachmentRow> toAttachmentRows(List<TaskStepAttachment> list, Set<Long> doneAttachmentIds) {
    if (list == null || list.isEmpty()) {
      return List.of();
    }

    Set<Long> fileIds = list.stream()
        .map(TaskStepAttachment::getFileId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Map<Long, FileObject> fileMap = new HashMap<>();
    if (!fileIds.isEmpty()) {
      List<FileObject> fos = fileObjectMapper.selectList(new LambdaQueryWrapper<FileObject>()
          .in(FileObject::getId, fileIds)
          .eq(FileObject::getDeleted, 0));
      for (FileObject fo : fos) {
        fileMap.put(fo.getId(), fo);
      }
    }

    List<TaskStepAttachmentRow> rows = new ArrayList<>();
    for (TaskStepAttachment a : list) {
      TaskStepAttachmentRow r = new TaskStepAttachmentRow();
      r.setId(a.getId());
      r.setStepId(a.getStepId());
      r.setKind(a.getKind());
      r.setTitle(a.getTitle());
      r.setFileId(a.getFileId());
      r.setUrl(a.getUrl());
      r.setSortNo(a.getSortNo());

      if (doneAttachmentIds != null && doneAttachmentIds.contains(a.getId())) {
        r.setDone(1);
      } else if (doneAttachmentIds != null) {
        r.setDone(0);
      }

      if (a.getFileId() != null) {
        FileObject fo = fileMap.get(a.getFileId());
        if (fo != null) {
          r.setOriginalName(fo.getOriginalName());
          r.setContentType(fo.getContentType());
          r.setFileSize(fo.getFileSize());
        }
      }

      rows.add(r);
    }
    return rows;
  }

  private List<TaskStepAttachmentRow> toAttachmentRowsForStudent(List<TaskStepAttachment> list,
                                                                 Map<Long, TaskStepAttachmentProgress> progressMap) {
    if (list == null || list.isEmpty()) {
      return List.of();
    }

    Set<Long> fileIds = list.stream()
        .map(TaskStepAttachment::getFileId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    Map<Long, FileObject> fileMap = new HashMap<>();
    if (!fileIds.isEmpty()) {
      List<FileObject> fos = fileObjectMapper.selectList(new LambdaQueryWrapper<FileObject>()
          .in(FileObject::getId, fileIds)
          .eq(FileObject::getDeleted, 0));
      for (FileObject fo : fos) {
        fileMap.put(fo.getId(), fo);
      }
    }

    List<TaskStepAttachmentRow> rows = new ArrayList<>();
    for (TaskStepAttachment a : list) {
      TaskStepAttachmentRow r = new TaskStepAttachmentRow();
      r.setId(a.getId());
      r.setStepId(a.getStepId());
      r.setKind(a.getKind());
      r.setTitle(a.getTitle());
      r.setFileId(a.getFileId());
      r.setUrl(a.getUrl());
      r.setSortNo(a.getSortNo());

      TaskStepAttachmentProgress p = progressMap == null ? null : progressMap.get(a.getId());
      if (p != null && p.getStatus() != null && p.getStatus() == 2) {
        r.setDone(1);
      } else {
        r.setDone(0);
      }
      if (p != null) {
        r.setProgressPercent(p.getProgressPercent() == null ? 0 : p.getProgressPercent());
        r.setPositionSeconds(p.getPositionSeconds());
        r.setDurationSeconds(p.getDurationSeconds());
      }

      if (a.getFileId() != null) {
        FileObject fo = fileMap.get(a.getFileId());
        if (fo != null) {
          r.setOriginalName(fo.getOriginalName());
          r.setContentType(fo.getContentType());
          r.setFileSize(fo.getFileSize());
        }
      }

      rows.add(r);
    }
    return rows;
  }

  /**
   * 老师端：更新步骤（章节）。支持更新标题/内容/排序号。
   */
  public void updateStep(Long teacherId, Long stepId, String title, String content, Integer sortNo) {
    updateStep(teacherId, stepId, title, content, sortNo, null, null);
  }

  /**
   * 老师端：更新步骤（章节）。支持更新标题/内容/排序号/类型/父节点。
   */
  public void updateStep(Long teacherId,
                         Long stepId,
                         String title,
                         String content,
                         Integer sortNo,
                         Integer stepType,
                         Long parentId) {
    TaskStep step = taskStepMapper.selectById(stepId);
    if (step == null || step.getDeleted() == null || step.getDeleted() != 0) {
      throw new BusinessException(40422, "步骤不存在");
    }

    requireTeacherOwnsTask(teacherId, step.getTaskId());

    boolean changed = false;

    Integer finalType = stepType == null ? (step.getStepType() == null ? STEP_TYPE_CONTENT : step.getStepType()) : normalizeStepType(stepType);
    Long finalParentId = parentId == null ? step.getParentId() : parentId;
    finalParentId = normalizeAndValidateParentId(step.getTaskId(), finalType, finalParentId, step.getId());

    if (stepType != null) {
      if (!Objects.equals(step.getStepType(), finalType)) {
        step.setStepType(finalType);
        changed = true;
      }
    }

    if (parentId != null) {
      if (!Objects.equals(step.getParentId(), finalParentId)) {
        step.setParentId(finalParentId);
        changed = true;

        // parent 变化时，默认把排序号放到同级末尾（除非显式传 sortNo）
        if (sortNo == null) {
          step.setSortNo(nextStepSortNo(step.getTaskId(), finalParentId));
        }
      }
    }

    if (title != null) {
      if (title.trim().isEmpty()) {
        throw new BusinessException(40001, "title 不能为空");
      }
      if (!Objects.equals(step.getTitle(), title)) {
        step.setTitle(title);
        changed = true;
      }
    }

    if (content != null) {
      if (!Objects.equals(step.getContent(), content)) {
        step.setContent(content);
        changed = true;
      }
    }

    if (sortNo != null) {
      if (!Objects.equals(step.getSortNo(), sortNo)) {
        step.setSortNo(sortNo);
        changed = true;
      }
    }

    if (!changed) {
      return;
    }

    step.setUpdatedAt(LocalDateTime.now());
    taskStepMapper.updateById(step);
  }

  public TaskLink createLink(Long teacherId, Long taskId, Long stepId, Integer linkType, Long refId, Integer sortNo) {
    Task task = requireTeacherOwnsTask(teacherId, taskId);

    if (stepId != null) {
      TaskStep step = taskStepMapper.selectById(stepId);
      if (step == null || step.getDeleted() == null || step.getDeleted() != 0 || !Objects.equals(step.getTaskId(), task.getId())) {
        throw new BusinessException(40031, "stepId 无效");
      }

      if (isNodeStep(step.getStepType())) {
        throw new BusinessException(40031, "stepId 必须为内容章节");
      }
    }

    if (linkType == null || (linkType != 1 && linkType != 2 && linkType != 3)) {
      throw new BusinessException(40032, "linkType 无效");
    }

    if (refId == null || refId <= 0) {
      throw new BusinessException(40033, "refId 无效");
    }

    if (linkType == 1) {
      Assignment a = assignmentMapper.selectById(refId);
      if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
        throw new BusinessException(40431, "作业不存在");
      }
      if (!Objects.equals(a.getCourseId(), task.getCourseId())) {
        throw new BusinessException(40034, "作业不属于该课程");
      }
      requireTeacherOwnsCourse(teacherId, a.getCourseId());
    } else if (linkType == 2) {
      Exam e = examMapper.selectById(refId);
      if (e == null || e.getDeleted() == null || e.getDeleted() != 0) {
        throw new BusinessException(40432, "考试不存在");
      }
      if (!Objects.equals(e.getCourseId(), task.getCourseId())) {
        throw new BusinessException(40035, "考试不属于该课程");
      }
      requireTeacherOwnsCourse(teacherId, e.getCourseId());
    }

    TaskLink link = new TaskLink();
    link.setTaskId(task.getId());
    link.setStepId(stepId);
    link.setLinkType(linkType);
    link.setRefId(refId);
    link.setSortNo(sortNo == null ? 0 : sortNo);
    link.setCreatedAt(LocalDateTime.now());
    link.setDeleted(0);

    taskLinkMapper.insert(link);
    return link;
  }

  public List<TaskLinkRow> listLinksForTeacher(Long teacherId, Long taskId) {
    Task task = requireTeacherOwnsTask(teacherId, taskId);
    return listLinksInternal(task.getId(), task.getCourseId());
  }

  public List<TaskLinkRow> listLinksForStudent(Long studentId, Long taskId) {
    Task task = requireStudentCanAccessTask(studentId, taskId);
    if (task.getStatus() == null || task.getStatus() == 0) {
      throw new BusinessException(40421, "任务不存在或已停用");
    }
    return listLinksInternal(task.getId(), task.getCourseId());
  }

  public void deleteLink(Long teacherId, Long linkId) {
    TaskLink link = taskLinkMapper.selectById(linkId);
    if (link == null || link.getDeleted() == null || link.getDeleted() != 0) {
      return;
    }

    Task task = taskMapper.selectById(link.getTaskId());
    if (task == null || task.getDeleted() == null || task.getDeleted() != 0) {
      throw new BusinessException(40421, "任务不存在");
    }

    requireTeacherOwnsCourse(teacherId, task.getCourseId());
    taskLinkMapper.deleteById(linkId);
  }

  private List<TaskLinkRow> listLinksInternal(Long taskId, Long courseId) {
    List<TaskLink> links = taskLinkMapper.selectList(new LambdaQueryWrapper<TaskLink>()
        .eq(TaskLink::getTaskId, taskId)
        .eq(TaskLink::getDeleted, 0)
        .orderByAsc(TaskLink::getSortNo)
        .orderByAsc(TaskLink::getId));

    if (links.isEmpty()) {
      return List.of();
    }

    Set<Long> assignmentIds = links.stream().filter(l -> l.getLinkType() != null && l.getLinkType() == 1).map(TaskLink::getRefId).collect(Collectors.toSet());
    Set<Long> examIds = links.stream().filter(l -> l.getLinkType() != null && l.getLinkType() == 2).map(TaskLink::getRefId).collect(Collectors.toSet());

    Map<Long, String> assignmentTitleMap = new HashMap<>();
    if (!assignmentIds.isEmpty()) {
      List<Assignment> as = assignmentMapper.selectList(new LambdaQueryWrapper<Assignment>()
          .in(Assignment::getId, assignmentIds)
          .eq(Assignment::getCourseId, courseId)
          .eq(Assignment::getDeleted, 0));
      for (Assignment a : as) {
        assignmentTitleMap.put(a.getId(), a.getTitle());
      }
    }

    Map<Long, String> examTitleMap = new HashMap<>();
    if (!examIds.isEmpty()) {
      List<Exam> es = examMapper.selectList(new LambdaQueryWrapper<Exam>()
          .in(Exam::getId, examIds)
          .eq(Exam::getCourseId, courseId)
          .eq(Exam::getDeleted, 0));
      for (Exam e : es) {
        examTitleMap.put(e.getId(), e.getTitle());
      }
    }

    List<TaskLinkRow> rows = new ArrayList<>();
    for (TaskLink l : links) {
      TaskLinkRow r = new TaskLinkRow();
      r.setId(l.getId());
      r.setTaskId(l.getTaskId());
      r.setStepId(l.getStepId());
      r.setLinkType(l.getLinkType());
      r.setRefId(l.getRefId());
      r.setSortNo(l.getSortNo());

      if (l.getLinkType() != null && l.getLinkType() == 1) {
        r.setRefTitle(assignmentTitleMap.getOrDefault(l.getRefId(), "作业#" + l.getRefId()));
      } else if (l.getLinkType() != null && l.getLinkType() == 2) {
        r.setRefTitle(examTitleMap.getOrDefault(l.getRefId(), "考试#" + l.getRefId()));
      } else {
        r.setRefTitle("对象#" + l.getRefId());
      }

      rows.add(r);
    }

    return rows;
  }

  private TaskRow toTaskRow(Task t) {
    TaskRow r = new TaskRow();
    r.setId(t.getId());
    r.setCourseId(t.getCourseId());
    r.setTitle(t.getTitle());
    r.setDescription(t.getDescription());
    r.setStatus(t.getStatus());
    r.setSortNo(t.getSortNo());
    return r;
  }

  private TaskStepRow toStepRow(TaskStep s) {
    TaskStepRow r = new TaskStepRow();
    r.setId(s.getId());
    r.setTaskId(s.getTaskId());
    r.setStepType(s.getStepType() == null ? STEP_TYPE_CONTENT : s.getStepType());
    r.setParentId(s.getParentId());
    r.setTitle(s.getTitle());
    r.setContent(s.getContent());
    r.setSortNo(s.getSortNo());
    return r;
  }

  private Task requireTeacherOwnsTask(Long teacherId, Long taskId) {
    Task task = taskMapper.selectById(taskId);
    if (task == null || task.getDeleted() == null || task.getDeleted() != 0) {
      throw new BusinessException(40421, "任务不存在");
    }
    requireTeacherOwnsCourse(teacherId, task.getCourseId());
    return task;
  }

  private Task requireStudentCanAccessTask(Long studentId, Long taskId) {
    Task task = taskMapper.selectById(taskId);
    if (task == null || task.getDeleted() == null || task.getDeleted() != 0) {
      throw new BusinessException(40421, "任务不存在");
    }
    requireStudentInCourse(studentId, task.getCourseId());
    return task;
  }

  private Course requireTeacherOwnsCourse(Long teacherId, Long courseId) {
    if (teacherId == null) {
      throw new BusinessException(40100, "未登录");
    }

    Course course = courseMapper.selectOne(new LambdaQueryWrapper<Course>()
        .eq(Course::getId, courseId)
        .eq(Course::getTeacherId, teacherId)
        .eq(Course::getDeleted, 0)
        .last("LIMIT 1"));

    if (course == null) {
      throw new BusinessException(40301, "无权限或课程不存在");
    }

    return course;
  }

  private void requireStudentInCourse(Long studentId, Long courseId) {
    if (studentId == null) {
      throw new BusinessException(40100, "未登录");
    }

    CourseMember m = courseMemberMapper.selectOne(new LambdaQueryWrapper<CourseMember>()
        .eq(CourseMember::getCourseId, courseId)
        .eq(CourseMember::getUserId, studentId)
        .eq(CourseMember::getDeleted, 0)
        .last("LIMIT 1"));

    if (m == null) {
      throw new BusinessException(40302, "未加入课程");
    }
  }

  private int nextStepSortNo(Long taskId, Long parentId) {
    List<TaskStep> list = taskStepMapper.selectList(new LambdaQueryWrapper<TaskStep>()
        .eq(TaskStep::getTaskId, taskId)
        .eq(TaskStep::getParentId, parentId)
        .eq(TaskStep::getDeleted, 0)
        .orderByDesc(TaskStep::getSortNo)
        .orderByDesc(TaskStep::getId)
        .last("LIMIT 1"));

    if (list.isEmpty() || list.get(0).getSortNo() == null) {
      return 1;
    }

    return list.get(0).getSortNo() + 1;
  }

  private boolean isNodeStep(Integer stepType) {
    return stepType != null && stepType == STEP_TYPE_NODE;
  }

  private int normalizeStepType(Integer stepType) {
    if (stepType == null) {
      return STEP_TYPE_CONTENT;
    }
    if (stepType != STEP_TYPE_NODE && stepType != STEP_TYPE_CONTENT) {
      throw new BusinessException(40001, "stepType 无效");
    }
    return stepType;
  }

  private Long normalizeAndValidateParentId(Long taskId, int stepType, Long parentId, Long selfId) {
    if (stepType == STEP_TYPE_NODE) {
      if (parentId != null) {
        throw new BusinessException(40001, "节点不支持 parentId");
      }
      return null;
    }

    if (parentId == null) {
      return null;
    }
    if (parentId <= 0) {
      throw new BusinessException(40001, "parentId 无效");
    }
    if (selfId != null && Objects.equals(parentId, selfId)) {
      throw new BusinessException(40001, "parentId 不能指向自身");
    }

    TaskStep parent = taskStepMapper.selectById(parentId);
    if (parent == null || parent.getDeleted() == null || parent.getDeleted() != 0) {
      throw new BusinessException(40001, "parentId 不存在");
    }
    if (!Objects.equals(parent.getTaskId(), taskId)) {
      throw new BusinessException(40001, "parentId 不属于该任务");
    }
    if (!isNodeStep(parent.getStepType())) {
      throw new BusinessException(40001, "parentId 必须是节点类型");
    }
    return parentId;
  }

  private int calcPercent(int done, int total) {
    if (total <= 0) {
      return 0;
    }
    double p = (done * 100.0) / total;
    return (int) Math.round(p);
  }
}
