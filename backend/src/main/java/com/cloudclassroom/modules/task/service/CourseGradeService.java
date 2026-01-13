package com.cloudclassroom.modules.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.assignment.entity.Assignment;
import com.cloudclassroom.modules.assignment.entity.AssignmentSubmission;
import com.cloudclassroom.modules.assignment.mapper.AssignmentMapper;
import com.cloudclassroom.modules.assignment.mapper.AssignmentSubmissionMapper;
import com.cloudclassroom.modules.course.dto.CourseMemberRow;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import com.cloudclassroom.modules.exam.entity.Exam;
import com.cloudclassroom.modules.exam.entity.ExamAttempt;
import com.cloudclassroom.modules.exam.mapper.ExamAttemptMapper;
import com.cloudclassroom.modules.exam.mapper.ExamMapper;
import com.cloudclassroom.modules.task.dto.GradeConfigRow;
import com.cloudclassroom.modules.task.dto.StudentGradeRow;
import com.cloudclassroom.modules.task.entity.CourseGradeConfig;
import com.cloudclassroom.modules.task.entity.Task;
import com.cloudclassroom.modules.task.entity.TaskStep;
import com.cloudclassroom.modules.task.entity.TaskStepProgress;
import com.cloudclassroom.modules.task.mapper.CourseGradeConfigMapper;
import com.cloudclassroom.modules.task.mapper.TaskMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepProgressMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程成绩服务：权重配置 + 成绩汇总。
 */
@Service
public class CourseGradeService {

  private final CourseGradeConfigMapper courseGradeConfigMapper;
  private final CourseMapper courseMapper;
  private final CourseMemberMapper courseMemberMapper;
  private final TaskMapper taskMapper;
  private final TaskStepMapper taskStepMapper;
  private final TaskStepProgressMapper taskStepProgressMapper;
  private final AssignmentMapper assignmentMapper;
  private final AssignmentSubmissionMapper assignmentSubmissionMapper;
  private final ExamMapper examMapper;
  private final ExamAttemptMapper examAttemptMapper;

  public CourseGradeService(CourseGradeConfigMapper courseGradeConfigMapper,
                            CourseMapper courseMapper,
                            CourseMemberMapper courseMemberMapper,
                            TaskMapper taskMapper,
                            TaskStepMapper taskStepMapper,
                            TaskStepProgressMapper taskStepProgressMapper,
                            AssignmentMapper assignmentMapper,
                            AssignmentSubmissionMapper assignmentSubmissionMapper,
                            ExamMapper examMapper,
                            ExamAttemptMapper examAttemptMapper) {
    this.courseGradeConfigMapper = courseGradeConfigMapper;
    this.courseMapper = courseMapper;
    this.courseMemberMapper = courseMemberMapper;
    this.taskMapper = taskMapper;
    this.taskStepMapper = taskStepMapper;
    this.taskStepProgressMapper = taskStepProgressMapper;
    this.assignmentMapper = assignmentMapper;
    this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    this.examMapper = examMapper;
    this.examAttemptMapper = examAttemptMapper;
  }

  public GradeConfigRow getConfig(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);
    CourseGradeConfig cfg = getOrDefaultConfig(courseId);

    GradeConfigRow r = new GradeConfigRow();
    r.setCourseId(courseId);
    r.setWeightTask(cfg.getWeightTask());
    r.setWeightAssignment(cfg.getWeightAssignment());
    r.setWeightExam(cfg.getWeightExam());
    return r;
  }

  public void upsertConfig(Long teacherId, Long courseId, Integer weightTask, Integer weightAssignment, Integer weightExam) {
    requireTeacherOwnsCourse(teacherId, courseId);

    int wt = clamp(weightTask);
    int wa = clamp(weightAssignment);
    int we = clamp(weightExam);

    if (wt + wa + we != 100) {
      throw new BusinessException(40061, "权重之和必须为 100");
    }

    CourseGradeConfig existed = courseGradeConfigMapper.selectOne(new LambdaQueryWrapper<CourseGradeConfig>()
        .eq(CourseGradeConfig::getCourseId, courseId)
        .eq(CourseGradeConfig::getDeleted, 0)
        .last("LIMIT 1"));

    if (existed == null) {
      CourseGradeConfig cfg = new CourseGradeConfig();
      cfg.setCourseId(courseId);
      cfg.setWeightTask(wt);
      cfg.setWeightAssignment(wa);
      cfg.setWeightExam(we);
      cfg.setCreatedBy(teacherId);
      cfg.setCreatedAt(LocalDateTime.now());
      cfg.setUpdatedAt(LocalDateTime.now());
      cfg.setDeleted(0);
      courseGradeConfigMapper.insert(cfg);
      return;
    }

    existed.setWeightTask(wt);
    existed.setWeightAssignment(wa);
    existed.setWeightExam(we);
    existed.setUpdatedAt(LocalDateTime.now());
    courseGradeConfigMapper.updateById(existed);
  }

  public List<StudentGradeRow> listStudentGrades(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);

    List<CourseMemberRow> members = courseMemberMapper.selectCourseMembers(courseId);
    List<CourseMemberRow> students = members.stream()
        .filter(m -> m.getMemberRole() != null && m.getMemberRole() == 1)
        .collect(Collectors.toList());

    if (students.isEmpty()) {
      return List.of();
    }

    // ===== 1) 任务：提前拉取步骤与完成进度 =====
    List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
        .eq(Task::getCourseId, courseId)
        .eq(Task::getDeleted, 0)
        .eq(Task::getStatus, 1));

    List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

    Map<Long, Long> stepIdToTaskId = new HashMap<>();
    int totalSteps = 0;

    if (!taskIds.isEmpty()) {
      List<TaskStep> steps = taskStepMapper.selectList(new LambdaQueryWrapper<TaskStep>()
          .in(TaskStep::getTaskId, taskIds)
          .eq(TaskStep::getDeleted, 0));

      totalSteps = steps.size();
      for (TaskStep s : steps) {
        stepIdToTaskId.put(s.getId(), s.getTaskId());
      }
    }

    // ===== 2) 作业/考试：提前拉取所有对象 =====
    List<Assignment> assignments = assignmentMapper.selectList(new LambdaQueryWrapper<Assignment>()
        .eq(Assignment::getCourseId, courseId)
        .eq(Assignment::getDeleted, 0));

    List<Exam> exams = examMapper.selectList(new LambdaQueryWrapper<Exam>()
        .eq(Exam::getCourseId, courseId)
        .eq(Exam::getDeleted, 0));

    int assignmentTotal = assignments.size();
    int examTotal = exams.size();

    long assignmentMaxTotal = assignments.stream().mapToLong(a -> a.getTotalScore() == null ? 0 : a.getTotalScore()).sum();
    long examMaxTotal = exams.stream().mapToLong(e -> e.getTotalScore() == null ? 0 : e.getTotalScore()).sum();

    Set<Long> allAssignmentIds = assignments.stream().map(Assignment::getId).collect(Collectors.toSet());
    Set<Long> allExamIds = exams.stream().map(Exam::getId).collect(Collectors.toSet());

    CourseGradeConfig cfg = getOrDefaultConfig(courseId);

    List<StudentGradeRow> rows = new ArrayList<>();

    for (CourseMemberRow stu : students) {
      Long studentId = stu.getUserId();

      TaskMetrics tm = calcTaskMetrics(studentId, stepIdToTaskId, totalSteps);
      AssignmentMetrics am = calcAssignmentMetrics(studentId, allAssignmentIds, assignmentTotal, assignmentMaxTotal);
      ExamMetrics em = calcExamMetrics(studentId, allExamIds, examTotal, examMaxTotal);

      int taskScorePercent = tm.scorePercent;
      int assignmentScorePercent = am.scorePercent;
      int examScorePercent = em.scorePercent;

      double finalScore = (taskScorePercent * cfg.getWeightTask() + assignmentScorePercent * cfg.getWeightAssignment() + examScorePercent * cfg.getWeightExam()) / 100.0;

      StudentGradeRow r = new StudentGradeRow();
      r.setStudentId(studentId);
      r.setNickname(stu.getNickname());

      r.setTaskPercent(tm.completionPercent);
      r.setAssignmentPercent(am.completionPercent);
      r.setExamPercent(em.completionPercent);

      r.setTaskScorePercent(taskScorePercent);
      r.setAssignmentScorePercent(assignmentScorePercent);
      r.setExamScorePercent(examScorePercent);

      r.setFinalScore(round2(finalScore));

      rows.add(r);
    }

    return rows;
  }

  private CourseGradeConfig getOrDefaultConfig(Long courseId) {
    CourseGradeConfig existed = courseGradeConfigMapper.selectOne(new LambdaQueryWrapper<CourseGradeConfig>()
        .eq(CourseGradeConfig::getCourseId, courseId)
        .eq(CourseGradeConfig::getDeleted, 0)
        .last("LIMIT 1"));

    if (existed != null) {
      return existed;
    }

    CourseGradeConfig cfg = new CourseGradeConfig();
    cfg.setCourseId(courseId);
    cfg.setWeightTask(30);
    cfg.setWeightAssignment(30);
    cfg.setWeightExam(40);
    return cfg;
  }

  private TaskMetrics calcTaskMetrics(Long studentId, Map<Long, Long> stepIdToTaskId, int totalSteps) {
    if (totalSteps <= 0 || stepIdToTaskId.isEmpty()) {
      return new TaskMetrics(0, 0, 0, 0);
    }

    List<TaskStepProgress> progresses = taskStepProgressMapper.selectList(new LambdaQueryWrapper<TaskStepProgress>()
        .in(TaskStepProgress::getStepId, stepIdToTaskId.keySet())
        .eq(TaskStepProgress::getUserId, studentId)
        .eq(TaskStepProgress::getStatus, 2)
        .eq(TaskStepProgress::getDeleted, 0));

    int doneSteps = progresses.size();
    int completionPercent = percent(doneSteps, totalSteps);

    return new TaskMetrics(doneSteps, totalSteps, completionPercent, completionPercent);
  }

  private AssignmentMetrics calcAssignmentMetrics(Long studentId, Set<Long> assignmentIds, int total, long maxTotal) {
    if (total <= 0 || assignmentIds.isEmpty()) {
      return new AssignmentMetrics(0, 0, 0, 0);
    }

    List<AssignmentSubmission> subs = assignmentSubmissionMapper.selectList(new LambdaQueryWrapper<AssignmentSubmission>()
        .in(AssignmentSubmission::getAssignmentId, assignmentIds)
        .eq(AssignmentSubmission::getStudentId, studentId)
        .ge(AssignmentSubmission::getStatus, 2)
        .eq(AssignmentSubmission::getDeleted, 0));

    int done = subs.size();
    int completionPercent = percent(done, total);

    long earned = subs.stream().mapToLong(s -> s.getTotalScore() == null ? 0 : s.getTotalScore()).sum();
    int scorePercent = maxTotal <= 0 ? 0 : percentLong(earned, maxTotal);

    return new AssignmentMetrics(done, total, completionPercent, scorePercent);
  }

  private ExamMetrics calcExamMetrics(Long studentId, Set<Long> examIds, int total, long maxTotal) {
    if (total <= 0 || examIds.isEmpty()) {
      return new ExamMetrics(0, 0, 0, 0);
    }

    List<ExamAttempt> attempts = examAttemptMapper.selectList(new LambdaQueryWrapper<ExamAttempt>()
        .in(ExamAttempt::getExamId, examIds)
        .eq(ExamAttempt::getStudentId, studentId)
        .eq(ExamAttempt::getStatus, 2)
        .eq(ExamAttempt::getDeleted, 0));

    int done = attempts.size();
    int completionPercent = percent(done, total);

    long earned = attempts.stream().mapToLong(a -> a.getTotalScore() == null ? 0 : a.getTotalScore()).sum();
    int scorePercent = maxTotal <= 0 ? 0 : percentLong(earned, maxTotal);

    return new ExamMetrics(done, total, completionPercent, scorePercent);
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

  private int clamp(Integer v) {
    if (v == null) {
      return 0;
    }
    if (v < 0) {
      return 0;
    }
    if (v > 100) {
      return 100;
    }
    return v;
  }

  private int percent(int done, int total) {
    if (total <= 0) {
      return 0;
    }
    return (int) Math.round((done * 100.0) / total);
  }

  private int percentLong(long part, long total) {
    if (total <= 0) {
      return 0;
    }
    return (int) Math.round((part * 100.0) / total);
  }

  private double round2(double v) {
    return Math.round(v * 100.0) / 100.0;
  }

  private static class TaskMetrics {
    final int doneSteps;
    final int totalSteps;
    final int completionPercent;
    final int scorePercent;

    TaskMetrics(int doneSteps, int totalSteps, int completionPercent, int scorePercent) {
      this.doneSteps = doneSteps;
      this.totalSteps = totalSteps;
      this.completionPercent = completionPercent;
      this.scorePercent = scorePercent;
    }
  }

  private static class AssignmentMetrics {
    final int done;
    final int total;
    final int completionPercent;
    final int scorePercent;

    AssignmentMetrics(int done, int total, int completionPercent, int scorePercent) {
      this.done = done;
      this.total = total;
      this.completionPercent = completionPercent;
      this.scorePercent = scorePercent;
    }
  }

  private static class ExamMetrics {
    final int done;
    final int total;
    final int completionPercent;
    final int scorePercent;

    ExamMetrics(int done, int total, int completionPercent, int scorePercent) {
      this.done = done;
      this.total = total;
      this.completionPercent = completionPercent;
      this.scorePercent = scorePercent;
    }
  }
}
