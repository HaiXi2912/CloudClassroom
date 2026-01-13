package com.cloudclassroom.modules.assignment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.assignment.dto.AssignmentQuestionRow;
import com.cloudclassroom.modules.assignment.dto.AssignmentRow;
import com.cloudclassroom.modules.assignment.dto.SubmissionRow;
import com.cloudclassroom.modules.assignment.entity.*;
import com.cloudclassroom.modules.assignment.mapper.*;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.entity.CourseMember;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作业服务：布置、提交（客观题自动计分）、批改。
 */
@Service
public class AssignmentService {

  private final AssignmentMapper assignmentMapper;
  private final AssignmentQuestionMapper assignmentQuestionMapper;
  private final AssignmentSubmissionMapper assignmentSubmissionMapper;
  private final AssignmentSubmissionAnswerMapper assignmentSubmissionAnswerMapper;
  private final CourseMapper courseMapper;
  private final CourseMemberMapper courseMemberMapper;

  public AssignmentService(AssignmentMapper assignmentMapper,
                           AssignmentQuestionMapper assignmentQuestionMapper,
                           AssignmentSubmissionMapper assignmentSubmissionMapper,
                           AssignmentSubmissionAnswerMapper assignmentSubmissionAnswerMapper,
                           CourseMapper courseMapper,
                           CourseMemberMapper courseMemberMapper) {
    this.assignmentMapper = assignmentMapper;
    this.assignmentQuestionMapper = assignmentQuestionMapper;
    this.assignmentSubmissionMapper = assignmentSubmissionMapper;
    this.assignmentSubmissionAnswerMapper = assignmentSubmissionAnswerMapper;
    this.courseMapper = courseMapper;
    this.courseMemberMapper = courseMemberMapper;
  }

  public Assignment createAssignment(Long teacherId, Long courseId, String title, String content, LocalDateTime dueAt, List<AssignmentQuestion> questions) {
    Course course = requireTeacherOwnsCourse(teacherId, courseId);
    if (course.getStatus() == null || course.getStatus() == 0) {
      throw new BusinessException(40010, "课程已停用");
    }

    if (questions == null || questions.isEmpty()) {
      throw new BusinessException(40011, "题目不能为空");
    }

    int totalScore = questions.stream().map(q -> q.getScore() == null ? 0 : q.getScore()).reduce(0, Integer::sum);

    Assignment a = new Assignment();
    a.setCourseId(courseId);
    a.setTitle(title);
    a.setContent(content);
    a.setPublishAt(LocalDateTime.now());
    a.setDueAt(dueAt);
    a.setTotalScore(totalScore);
    a.setCreatedBy(teacherId);
    a.setCreatedAt(LocalDateTime.now());
    a.setUpdatedAt(LocalDateTime.now());
    a.setDeleted(0);
    assignmentMapper.insert(a);

    int sort = 1;
    for (AssignmentQuestion q : questions) {
      q.setAssignmentId(a.getId());
      if (q.getSortNo() == null) {
        q.setSortNo(sort++);
      }
      if (q.getScore() == null) {
        q.setScore(5);
      }
      q.setCreatedAt(LocalDateTime.now());
      q.setDeleted(0);
      assignmentQuestionMapper.insert(q);
    }

    return a;
  }

  public List<AssignmentRow> listTeacherAssignments(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);

    List<Assignment> list = assignmentMapper.selectList(new LambdaQueryWrapper<Assignment>()
        .eq(Assignment::getCourseId, courseId)
        .eq(Assignment::getDeleted, 0)
        .orderByDesc(Assignment::getId));

    return list.stream().map(this::toRow).collect(Collectors.toList());
  }

  public List<AssignmentRow> listStudentAssignments(Long studentId, Long courseId) {
    requireStudentInCourse(studentId, courseId);

    List<Assignment> list = assignmentMapper.selectList(new LambdaQueryWrapper<Assignment>()
        .eq(Assignment::getCourseId, courseId)
        .eq(Assignment::getDeleted, 0)
        .orderByDesc(Assignment::getId));

    return list.stream().map(this::toRow).collect(Collectors.toList());
  }

  public List<AssignmentQuestionRow> listQuestionsForTeacher(Long teacherId, Long assignmentId) {
    Assignment a = requireTeacherOwnsAssignment(teacherId, assignmentId);
    List<AssignmentQuestion> qs = assignmentQuestionMapper.selectList(new LambdaQueryWrapper<AssignmentQuestion>()
        .eq(AssignmentQuestion::getAssignmentId, a.getId())
        .eq(AssignmentQuestion::getDeleted, 0)
        .orderByAsc(AssignmentQuestion::getSortNo)
        .orderByAsc(AssignmentQuestion::getId));

    // 老师也不返回正确答案（前端如需展示可再加接口，这里保持最小）
    return qs.stream().map(this::toQuestionRow).collect(Collectors.toList());
  }

  public List<AssignmentQuestionRow> listQuestionsForStudent(Long studentId, Long assignmentId) {
    Assignment a = requireStudentCanAccessAssignment(studentId, assignmentId);
    List<AssignmentQuestion> qs = assignmentQuestionMapper.selectList(new LambdaQueryWrapper<AssignmentQuestion>()
        .eq(AssignmentQuestion::getAssignmentId, a.getId())
        .eq(AssignmentQuestion::getDeleted, 0)
        .orderByAsc(AssignmentQuestion::getSortNo)
        .orderByAsc(AssignmentQuestion::getId));

    return qs.stream().map(this::toQuestionRow).collect(Collectors.toList());
  }

  public void submit(Long studentId, Long assignmentId, Map<Long, String> answers) {
    Assignment a = requireStudentCanAccessAssignment(studentId, assignmentId);

    if (a.getDueAt() != null && LocalDateTime.now().isAfter(a.getDueAt())) {
      throw new BusinessException(40020, "已超过截止时间");
    }

    AssignmentSubmission existed = assignmentSubmissionMapper.selectOne(new LambdaQueryWrapper<AssignmentSubmission>()
        .eq(AssignmentSubmission::getAssignmentId, assignmentId)
        .eq(AssignmentSubmission::getStudentId, studentId)
        .eq(AssignmentSubmission::getDeleted, 0)
        .last("LIMIT 1"));

    if (existed != null && existed.getStatus() != null && existed.getStatus() >= 2) {
      throw new BusinessException(40021, "已提交，不能重复提交");
    }

    AssignmentSubmission sub = existed;
    if (sub == null) {
      sub = new AssignmentSubmission();
      sub.setAssignmentId(assignmentId);
      sub.setStudentId(studentId);
      sub.setStatus(1);
      sub.setAutoScore(0);
      sub.setManualScore(0);
      sub.setTotalScore(0);
      sub.setCreatedAt(LocalDateTime.now());
      sub.setUpdatedAt(LocalDateTime.now());
      sub.setDeleted(0);
      assignmentSubmissionMapper.insert(sub);
    }

    List<AssignmentQuestion> qs = assignmentQuestionMapper.selectList(new LambdaQueryWrapper<AssignmentQuestion>()
        .eq(AssignmentQuestion::getAssignmentId, assignmentId)
        .eq(AssignmentQuestion::getDeleted, 0));

    Map<Long, AssignmentQuestion> questionMap = qs.stream().collect(Collectors.toMap(AssignmentQuestion::getId, q -> q));

    int autoScore = 0;

    if (answers == null) {
      answers = Map.of();
    }

    for (Map.Entry<Long, String> e : answers.entrySet()) {
      Long questionId = e.getKey();
      String answerText = e.getValue();

      AssignmentQuestion q = questionMap.get(questionId);
      if (q == null) {
        continue;
      }

      // 仅对客观题自动判分：1/2/3
      boolean objective = q.getQuestionType() != null && (q.getQuestionType() == 1 || q.getQuestionType() == 2 || q.getQuestionType() == 3);
      boolean correct = objective && equalsAnswer(q.getQuestionType(), q.getCorrectAnswer(), answerText);
      int score = correct ? (q.getScore() == null ? 0 : q.getScore()) : 0;

      AssignmentSubmissionAnswer sa = assignmentSubmissionAnswerMapper.selectOne(new LambdaQueryWrapper<AssignmentSubmissionAnswer>()
          .eq(AssignmentSubmissionAnswer::getSubmissionId, sub.getId())
          .eq(AssignmentSubmissionAnswer::getQuestionId, questionId)
          .eq(AssignmentSubmissionAnswer::getDeleted, 0)
          .last("LIMIT 1"));

      if (sa == null) {
        sa = new AssignmentSubmissionAnswer();
        sa.setSubmissionId(sub.getId());
        sa.setQuestionId(questionId);
        sa.setCreatedAt(LocalDateTime.now());
        sa.setDeleted(0);
      }

      sa.setAnswerText(answerText);
      sa.setIsCorrect(correct ? 1 : 0);
      sa.setScore(score);

      if (sa.getId() == null) {
        assignmentSubmissionAnswerMapper.insert(sa);
      } else {
        assignmentSubmissionAnswerMapper.updateById(sa);
      }

      autoScore += score;
    }

    sub.setStatus(2);
    sub.setSubmitAt(LocalDateTime.now());
    sub.setAutoScore(autoScore);
    sub.setManualScore(0);
    sub.setTotalScore(autoScore);
    sub.setUpdatedAt(LocalDateTime.now());
    assignmentSubmissionMapper.updateById(sub);
  }

  public List<SubmissionRow> listSubmissionsForTeacher(Long teacherId, Long assignmentId) {
    requireTeacherOwnsAssignment(teacherId, assignmentId);

    List<AssignmentSubmission> list = assignmentSubmissionMapper.selectList(new LambdaQueryWrapper<AssignmentSubmission>()
        .eq(AssignmentSubmission::getAssignmentId, assignmentId)
        .eq(AssignmentSubmission::getDeleted, 0)
        .orderByDesc(AssignmentSubmission::getId));

    return list.stream().map(this::toSubmissionRow).collect(Collectors.toList());
  }

  public void gradeSubmission(Long teacherId, Long submissionId, Integer manualScore, String teacherComment) {
    AssignmentSubmission sub = assignmentSubmissionMapper.selectById(submissionId);
    if (sub == null || sub.getDeleted() == null || sub.getDeleted() != 0) {
      throw new BusinessException(40410, "提交不存在");
    }

    Assignment a = requireTeacherOwnsAssignment(teacherId, sub.getAssignmentId());

    if (sub.getStatus() == null || sub.getStatus() < 2) {
      throw new BusinessException(40022, "未提交，不能批改");
    }

    int ms = manualScore == null ? 0 : manualScore;
    if (ms < 0) {
      ms = 0;
    }

    int auto = sub.getAutoScore() == null ? 0 : sub.getAutoScore();
    int total = auto + ms;

    int max = a.getTotalScore() == null ? 0 : a.getTotalScore();
    if (max > 0 && total > max) {
      total = max;
    }

    sub.setManualScore(ms);
    sub.setTotalScore(total);
    sub.setTeacherComment(teacherComment);
    sub.setGradedBy(teacherId);
    sub.setGradedAt(LocalDateTime.now());
    sub.setStatus(3);
    sub.setUpdatedAt(LocalDateTime.now());
    assignmentSubmissionMapper.updateById(sub);
  }

  private Course requireTeacherOwnsCourse(Long teacherId, Long courseId) {
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
    CourseMember m = courseMemberMapper.selectOne(new LambdaQueryWrapper<CourseMember>()
        .eq(CourseMember::getCourseId, courseId)
        .eq(CourseMember::getUserId, studentId)
        .eq(CourseMember::getDeleted, 0)
        .last("LIMIT 1"));
    if (m == null) {
      throw new BusinessException(40302, "未加入课程");
    }
  }

  private Assignment requireTeacherOwnsAssignment(Long teacherId, Long assignmentId) {
    Assignment a = assignmentMapper.selectById(assignmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      throw new BusinessException(40411, "作业不存在");
    }
    requireTeacherOwnsCourse(teacherId, a.getCourseId());
    return a;
  }

  private Assignment requireStudentCanAccessAssignment(Long studentId, Long assignmentId) {
    Assignment a = assignmentMapper.selectById(assignmentId);
    if (a == null || a.getDeleted() == null || a.getDeleted() != 0) {
      throw new BusinessException(40411, "作业不存在");
    }
    requireStudentInCourse(studentId, a.getCourseId());
    return a;
  }

  private AssignmentRow toRow(Assignment a) {
    AssignmentRow r = new AssignmentRow();
    r.setId(a.getId());
    r.setCourseId(a.getCourseId());
    r.setTitle(a.getTitle());
    r.setPublishAt(a.getPublishAt());
    r.setDueAt(a.getDueAt());
    r.setTotalScore(a.getTotalScore());
    return r;
  }

  private AssignmentQuestionRow toQuestionRow(AssignmentQuestion q) {
    AssignmentQuestionRow r = new AssignmentQuestionRow();
    r.setId(q.getId());
    r.setQuestionType(q.getQuestionType());
    r.setQuestionText(q.getQuestionText());
    r.setOptionA(q.getOptionA());
    r.setOptionB(q.getOptionB());
    r.setOptionC(q.getOptionC());
    r.setOptionD(q.getOptionD());
    r.setScore(q.getScore());
    r.setSortNo(q.getSortNo());
    return r;
  }

  private SubmissionRow toSubmissionRow(AssignmentSubmission s) {
    SubmissionRow r = new SubmissionRow();
    r.setId(s.getId());
    r.setAssignmentId(s.getAssignmentId());
    r.setStudentId(s.getStudentId());
    r.setStatus(s.getStatus());
    r.setAutoScore(s.getAutoScore());
    r.setManualScore(s.getManualScore());
    r.setTotalScore(s.getTotalScore());
    r.setSubmitAt(s.getSubmitAt());
    r.setGradedAt(s.getGradedAt());
    return r;
  }

  private static boolean equalsAnswer(Integer questionType, String correctAnswer, String studentAnswer) {
    if (correctAnswer == null || studentAnswer == null) {
      return false;
    }

    if (questionType != null && questionType == 2) {
      // 多选：忽略顺序与空格，统一用逗号分隔
      return normalizeMulti(correctAnswer).equals(normalizeMulti(studentAnswer));
    }

    return correctAnswer.trim().equalsIgnoreCase(studentAnswer.trim());
  }

  private static String normalizeMulti(String s) {
    String t = s.trim().toUpperCase().replace(" ", "");
    String[] parts = t.split(",");
    List<String> list = Arrays.stream(parts)
        .filter(p -> p != null && !p.isBlank())
        .map(String::trim)
        .sorted()
        .collect(Collectors.toList());
    return String.join(",", list);
  }
}
