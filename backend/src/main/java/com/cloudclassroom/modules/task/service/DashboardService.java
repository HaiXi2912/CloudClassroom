package com.cloudclassroom.modules.task.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.assignment.entity.Assignment;
import com.cloudclassroom.modules.assignment.entity.AssignmentSubmission;
import com.cloudclassroom.modules.assignment.mapper.AssignmentMapper;
import com.cloudclassroom.modules.assignment.mapper.AssignmentSubmissionMapper;
import com.cloudclassroom.modules.course.entity.CourseMember;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import com.cloudclassroom.modules.exam.entity.Exam;
import com.cloudclassroom.modules.exam.entity.ExamAttempt;
import com.cloudclassroom.modules.exam.mapper.ExamAttemptMapper;
import com.cloudclassroom.modules.exam.mapper.ExamMapper;
import com.cloudclassroom.modules.task.dto.DashboardRow;
import com.cloudclassroom.modules.task.entity.CourseGradeConfig;
import com.cloudclassroom.modules.task.entity.Task;
import com.cloudclassroom.modules.task.entity.TaskStep;
import com.cloudclassroom.modules.task.entity.TaskStepProgress;
import com.cloudclassroom.modules.task.mapper.CourseGradeConfigMapper;
import com.cloudclassroom.modules.task.mapper.TaskMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepMapper;
import com.cloudclassroom.modules.task.mapper.TaskStepProgressMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 学习看板服务：按课程汇总任务/作业/考试完成度与得分率。
 */
@Service
public class DashboardService {

  private final CourseMemberMapper courseMemberMapper;
  private final TaskMapper taskMapper;
  private final TaskStepMapper taskStepMapper;
  private final TaskStepProgressMapper taskStepProgressMapper;
  private final AssignmentMapper assignmentMapper;
  private final AssignmentSubmissionMapper assignmentSubmissionMapper;
  private final ExamMapper examMapper;
  private final ExamAttemptMapper examAttemptMapper;
  private final CourseGradeConfigMapper courseGradeConfigMapper;

  public DashboardService(CourseMemberMapper courseMemberMapper,
                          TaskMapper taskMapper,
                          TaskStepMapper taskStepMapper,
                          TaskStepProgressMapper taskStepProgressMapper,
                          AssignmentMapper assignmentMapper,
                          AssignmentSubmissionMapper assignmentSubmissionMapper,
                          ExamMapper examMapper,
                          ExamAttemptMapper examAttemptMapper,
                          CourseGradeConfigMapper courseGradeConfigMapper) {
    this.courseMemberMapper = courseMemberMapper;
    this.taskMapper = taskMapper;
    this.taskStepMapper = taskStepMapper;
    this.taskStepProgressMapper = taskStepProgressMapper;
    this.assignmentMapper = assignmentMapper;
    this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    this.examMapper = examMapper;
    this.examAttemptMapper = examAttemptMapper;
    this.courseGradeConfigMapper = courseGradeConfigMapper;
  }

  public DashboardRow getStudentDashboard(Long studentId, Long courseId) {
    requireStudentInCourse(studentId, courseId);

    // ===== 1) 任务 =====
    List<Task> tasks = taskMapper.selectList(new LambdaQueryWrapper<Task>()
        .eq(Task::getCourseId, courseId)
        .eq(Task::getDeleted, 0)
        .eq(Task::getStatus, 1));

    List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

    int taskTotalSteps = 0;
    int taskDoneSteps = 0;

    if (!taskIds.isEmpty()) {
      List<TaskStep> steps = taskStepMapper.selectList(new LambdaQueryWrapper<TaskStep>()
          .in(TaskStep::getTaskId, taskIds)
          .eq(TaskStep::getDeleted, 0));

        List<TaskStep> contentSteps = steps.stream()
          .filter(s -> s.getStepType() == null || s.getStepType() != 1)
          .collect(Collectors.toList());

        taskTotalSteps = contentSteps.size();

        Set<Long> stepIds = contentSteps.stream().map(TaskStep::getId).collect(Collectors.toSet());
      if (!stepIds.isEmpty()) {
        taskDoneSteps = taskStepProgressMapper.selectCount(new LambdaQueryWrapper<TaskStepProgress>()
            .in(TaskStepProgress::getStepId, stepIds)
            .eq(TaskStepProgress::getUserId, studentId)
            .eq(TaskStepProgress::getStatus, 2)
            .eq(TaskStepProgress::getDeleted, 0)).intValue();
      }
    }

    int taskPercent = percent(taskDoneSteps, taskTotalSteps);

    // ===== 2) 作业 =====
    List<Assignment> assignments = assignmentMapper.selectList(new LambdaQueryWrapper<Assignment>()
        .eq(Assignment::getCourseId, courseId)
        .eq(Assignment::getDeleted, 0));

    int assignmentTotal = assignments.size();

    Set<Long> assignmentIds = assignments.stream().map(Assignment::getId).collect(Collectors.toSet());
    int assignmentDone = 0;
    long assignmentMaxTotal = assignments.stream().mapToLong(a -> a.getTotalScore() == null ? 0 : a.getTotalScore()).sum();
    long assignmentEarnedTotal = 0;

    if (!assignmentIds.isEmpty()) {
      List<AssignmentSubmission> subs = assignmentSubmissionMapper.selectList(new LambdaQueryWrapper<AssignmentSubmission>()
          .in(AssignmentSubmission::getAssignmentId, assignmentIds)
          .eq(AssignmentSubmission::getStudentId, studentId)
          .ge(AssignmentSubmission::getStatus, 2)
          .eq(AssignmentSubmission::getDeleted, 0));

      assignmentDone = subs.size();
      assignmentEarnedTotal = subs.stream().mapToLong(s -> s.getTotalScore() == null ? 0 : s.getTotalScore()).sum();
    }

    int assignmentPercent = percent(assignmentDone, assignmentTotal);
    int assignmentScorePercent = assignmentMaxTotal <= 0 ? 0 : percentLong(assignmentEarnedTotal, assignmentMaxTotal);

    // ===== 3) 考试 =====
    List<Exam> exams = examMapper.selectList(new LambdaQueryWrapper<Exam>()
        .eq(Exam::getCourseId, courseId)
        .eq(Exam::getDeleted, 0));

    int examTotal = exams.size();

    Set<Long> examIds = exams.stream().map(Exam::getId).collect(Collectors.toSet());
    int examDone = 0;
    long examMaxTotal = exams.stream().mapToLong(e -> e.getTotalScore() == null ? 0 : e.getTotalScore()).sum();
    long examEarnedTotal = 0;

    if (!examIds.isEmpty()) {
      List<ExamAttempt> attempts = examAttemptMapper.selectList(new LambdaQueryWrapper<ExamAttempt>()
          .in(ExamAttempt::getExamId, examIds)
          .eq(ExamAttempt::getStudentId, studentId)
          .eq(ExamAttempt::getStatus, 2)
          .eq(ExamAttempt::getDeleted, 0));

      examDone = attempts.size();
      examEarnedTotal = attempts.stream().mapToLong(a -> a.getTotalScore() == null ? 0 : a.getTotalScore()).sum();
    }

    int examPercent = percent(examDone, examTotal);
    int examScorePercent = examMaxTotal <= 0 ? 0 : percentLong(examEarnedTotal, examMaxTotal);

    // ===== 4) 总评（基于得分率/任务完成度） =====
    int taskScorePercent = taskPercent;

    CourseGradeConfig cfg = courseGradeConfigMapper.selectOne(new LambdaQueryWrapper<CourseGradeConfig>()
        .eq(CourseGradeConfig::getCourseId, courseId)
        .eq(CourseGradeConfig::getDeleted, 0)
        .last("LIMIT 1"));

    int wt = cfg == null || cfg.getWeightTask() == null ? 30 : cfg.getWeightTask();
    int wa = cfg == null || cfg.getWeightAssignment() == null ? 30 : cfg.getWeightAssignment();
    int we = cfg == null || cfg.getWeightExam() == null ? 40 : cfg.getWeightExam();
    if (wt + wa + we != 100) {
      wt = 30;
      wa = 30;
      we = 40;
    }

    double finalScore = (taskScorePercent * wt + assignmentScorePercent * wa + examScorePercent * we) / 100.0;

    DashboardRow r = new DashboardRow();
    r.setCourseId(courseId);

    r.setTaskDone(taskDoneSteps);
    r.setTaskTotal(taskTotalSteps);
    r.setTaskPercent(taskPercent);
    r.setTaskScorePercent(taskScorePercent);

    r.setAssignmentDone(assignmentDone);
    r.setAssignmentTotal(assignmentTotal);
    r.setAssignmentPercent(assignmentPercent);
    r.setAssignmentScorePercent(assignmentScorePercent);

    r.setExamDone(examDone);
    r.setExamTotal(examTotal);
    r.setExamPercent(examPercent);
    r.setExamScorePercent(examScorePercent);

    r.setFinalScore(round2(finalScore));

    return r;
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
}
