package com.cloudclassroom.modules.exam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.entity.CourseMember;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import com.cloudclassroom.modules.exam.dto.*;
import com.cloudclassroom.modules.exam.entity.*;
import com.cloudclassroom.modules.exam.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考试服务：组卷、答题、自动判分。
 */
@Service
public class ExamService {

  private final CourseMapper courseMapper;
  private final CourseMemberMapper courseMemberMapper;
  private final ExamMapper examMapper;
  private final ExamQuestionMapper examQuestionMapper;
  private final ExamAttemptMapper examAttemptMapper;
  private final ExamAttemptAnswerMapper examAttemptAnswerMapper;

  public ExamService(CourseMapper courseMapper,
                     CourseMemberMapper courseMemberMapper,
                     ExamMapper examMapper,
                     ExamQuestionMapper examQuestionMapper,
                     ExamAttemptMapper examAttemptMapper,
                     ExamAttemptAnswerMapper examAttemptAnswerMapper) {
    this.courseMapper = courseMapper;
    this.courseMemberMapper = courseMemberMapper;
    this.examMapper = examMapper;
    this.examQuestionMapper = examQuestionMapper;
    this.examAttemptMapper = examAttemptMapper;
    this.examAttemptAnswerMapper = examAttemptAnswerMapper;
  }

  public Exam createExam(Long teacherId,
                         Long courseId,
                         String title,
                         String description,
                         LocalDateTime startAt,
                         LocalDateTime endAt,
                         Integer durationMinutes,
                         List<ExamQuestion> questions) {

    requireTeacherOwnsCourse(teacherId, courseId);

    if (questions == null || questions.isEmpty()) {
      throw new BusinessException(40030, "题目不能为空");
    }

    if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
      throw new BusinessException(40031, "开始/结束时间不合法");
    }

    int totalScore = questions.stream().map(q -> q.getScore() == null ? 0 : q.getScore()).reduce(0, Integer::sum);

    Exam e = new Exam();
    e.setCourseId(courseId);
    e.setTitle(title);
    e.setDescription(description);
    e.setStartAt(startAt);
    e.setEndAt(endAt);
    e.setDurationMinutes(durationMinutes == null ? 60 : durationMinutes);
    e.setTotalScore(totalScore);
    e.setCreatedBy(teacherId);
    e.setCreatedAt(LocalDateTime.now());
    e.setUpdatedAt(LocalDateTime.now());
    e.setDeleted(0);
    examMapper.insert(e);

    int sort = 1;
    for (ExamQuestion q : questions) {
      q.setExamId(e.getId());
      if (q.getSortNo() == null) {
        q.setSortNo(sort++);
      }
      if (q.getScore() == null) {
        q.setScore(5);
      }
      q.setCreatedAt(LocalDateTime.now());
      q.setDeleted(0);
      examQuestionMapper.insert(q);
    }

    return e;
  }

  public List<ExamRow> listTeacherExams(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);

    List<Exam> list = examMapper.selectList(new LambdaQueryWrapper<Exam>()
        .eq(Exam::getCourseId, courseId)
        .eq(Exam::getDeleted, 0)
        .orderByDesc(Exam::getId));

    return list.stream().map(this::toRow).collect(Collectors.toList());
  }

  public List<ExamRow> listStudentExams(Long studentId, Long courseId) {
    requireStudentInCourse(studentId, courseId);

    List<Exam> list = examMapper.selectList(new LambdaQueryWrapper<Exam>()
        .eq(Exam::getCourseId, courseId)
        .eq(Exam::getDeleted, 0)
        .orderByDesc(Exam::getId));

    return list.stream().map(this::toRow).collect(Collectors.toList());
  }

  public List<ExamQuestionRow> listQuestionsForTeacher(Long teacherId, Long examId) {
    requireTeacherOwnsExam(teacherId, examId);
    List<ExamQuestion> qs = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
        .eq(ExamQuestion::getExamId, examId)
        .eq(ExamQuestion::getDeleted, 0)
        .orderByAsc(ExamQuestion::getSortNo)
        .orderByAsc(ExamQuestion::getId));

    // 最小实现：老师端也不返回正确答案
    return qs.stream().map(this::toQuestionRow).collect(Collectors.toList());
  }

  public List<ExamQuestionRow> listQuestionsForStudent(Long studentId, Long examId) {
    Exam e = requireStudentCanAccessExam(studentId, examId);

    List<ExamQuestion> qs = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
        .eq(ExamQuestion::getExamId, e.getId())
        .eq(ExamQuestion::getDeleted, 0)
        .orderByAsc(ExamQuestion::getSortNo)
        .orderByAsc(ExamQuestion::getId));

    return qs.stream().map(this::toQuestionRow).collect(Collectors.toList());
  }

  public AttemptRow getMyAttempt(Long studentId, Long examId) {
    Exam e = requireStudentCanAccessExam(studentId, examId);

    ExamAttempt a = examAttemptMapper.selectOne(new LambdaQueryWrapper<ExamAttempt>()
        .eq(ExamAttempt::getExamId, e.getId())
        .eq(ExamAttempt::getStudentId, studentId)
        .eq(ExamAttempt::getDeleted, 0)
        .last("LIMIT 1"));

    if (a == null) {
      return null;
    }
    return toAttemptRow(a);
  }

  public void submit(Long studentId, Long examId, Map<Long, String> answers) {
    Exam e = requireStudentCanAccessExam(studentId, examId);

    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(e.getStartAt())) {
      throw new BusinessException(40032, "考试未开始");
    }
    if (now.isAfter(e.getEndAt())) {
      throw new BusinessException(40033, "考试已结束");
    }

    ExamAttempt existed = examAttemptMapper.selectOne(new LambdaQueryWrapper<ExamAttempt>()
        .eq(ExamAttempt::getExamId, examId)
        .eq(ExamAttempt::getStudentId, studentId)
        .eq(ExamAttempt::getDeleted, 0)
        .last("LIMIT 1"));

    if (existed != null && existed.getStatus() != null && existed.getStatus() >= 2) {
      throw new BusinessException(40034, "已提交，不能重复提交");
    }

    ExamAttempt attempt = existed;
    if (attempt == null) {
      attempt = new ExamAttempt();
      attempt.setExamId(examId);
      attempt.setStudentId(studentId);
      attempt.setStatus(1);
      attempt.setStartAt(now);
      attempt.setTotalScore(0);
      attempt.setCreatedAt(now);
      attempt.setUpdatedAt(now);
      attempt.setDeleted(0);
      examAttemptMapper.insert(attempt);
    }

    List<ExamQuestion> qs = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
        .eq(ExamQuestion::getExamId, examId)
        .eq(ExamQuestion::getDeleted, 0));

    Map<Long, ExamQuestion> qMap = qs.stream().collect(Collectors.toMap(ExamQuestion::getId, q -> q));

    if (answers == null) {
      answers = Map.of();
    }

    int totalScore = 0;

    for (Map.Entry<Long, String> entry : answers.entrySet()) {
      Long questionId = entry.getKey();
      String answerText = entry.getValue();

      ExamQuestion q = qMap.get(questionId);
      if (q == null) {
        continue;
      }

      boolean correct = equalsAnswer(q.getQuestionType(), q.getCorrectAnswer(), answerText);
      int score = correct ? (q.getScore() == null ? 0 : q.getScore()) : 0;

      ExamAttemptAnswer aa = examAttemptAnswerMapper.selectOne(new LambdaQueryWrapper<ExamAttemptAnswer>()
          .eq(ExamAttemptAnswer::getAttemptId, attempt.getId())
          .eq(ExamAttemptAnswer::getQuestionId, questionId)
          .eq(ExamAttemptAnswer::getDeleted, 0)
          .last("LIMIT 1"));

      if (aa == null) {
        aa = new ExamAttemptAnswer();
        aa.setAttemptId(attempt.getId());
        aa.setQuestionId(questionId);
        aa.setCreatedAt(now);
        aa.setDeleted(0);
      }

      aa.setAnswerText(answerText);
      aa.setIsCorrect(correct ? 1 : 0);
      aa.setScore(score);

      if (aa.getId() == null) {
        examAttemptAnswerMapper.insert(aa);
      } else {
        examAttemptAnswerMapper.updateById(aa);
      }

      totalScore += score;
    }

    attempt.setStatus(2);
    attempt.setSubmitAt(now);
    attempt.setTotalScore(totalScore);
    attempt.setUpdatedAt(now);
    examAttemptMapper.updateById(attempt);
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

  private Exam requireTeacherOwnsExam(Long teacherId, Long examId) {
    Exam e = examMapper.selectById(examId);
    if (e == null || e.getDeleted() == null || e.getDeleted() != 0) {
      throw new BusinessException(40430, "考试不存在");
    }
    requireTeacherOwnsCourse(teacherId, e.getCourseId());
    return e;
  }

  private Exam requireStudentCanAccessExam(Long studentId, Long examId) {
    Exam e = examMapper.selectById(examId);
    if (e == null || e.getDeleted() == null || e.getDeleted() != 0) {
      throw new BusinessException(40430, "考试不存在");
    }
    requireStudentInCourse(studentId, e.getCourseId());
    return e;
  }

  private ExamRow toRow(Exam e) {
    ExamRow r = new ExamRow();
    r.setId(e.getId());
    r.setCourseId(e.getCourseId());
    r.setTitle(e.getTitle());
    r.setStartAt(e.getStartAt());
    r.setEndAt(e.getEndAt());
    r.setDurationMinutes(e.getDurationMinutes());
    r.setTotalScore(e.getTotalScore());
    return r;
  }

  private ExamQuestionRow toQuestionRow(ExamQuestion q) {
    ExamQuestionRow r = new ExamQuestionRow();
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

  private AttemptRow toAttemptRow(ExamAttempt a) {
    AttemptRow r = new AttemptRow();
    r.setId(a.getId());
    r.setExamId(a.getExamId());
    r.setStudentId(a.getStudentId());
    r.setStatus(a.getStatus());
    r.setStartAt(a.getStartAt());
    r.setSubmitAt(a.getSubmitAt());
    r.setTotalScore(a.getTotalScore());
    return r;
  }

  private static boolean equalsAnswer(Integer questionType, String correctAnswer, String studentAnswer) {
    if (correctAnswer == null || studentAnswer == null) {
      return false;
    }

    if (questionType != null && questionType == 3) {
      // 判断题：TRUE/FALSE
      return correctAnswer.trim().equalsIgnoreCase(studentAnswer.trim());
    }

    // 单选：A/B/C/D
    return correctAnswer.trim().equalsIgnoreCase(studentAnswer.trim());
  }
}
