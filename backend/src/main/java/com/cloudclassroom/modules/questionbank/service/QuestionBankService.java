package com.cloudclassroom.modules.questionbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.questionbank.dto.QuestionBankQuestionRow;
import com.cloudclassroom.modules.questionbank.entity.QuestionBankQuestion;
import com.cloudclassroom.modules.questionbank.mapper.QuestionBankQuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionBankService {

  private final CourseMapper courseMapper;
  private final QuestionBankQuestionMapper questionMapper;

  public QuestionBankService(CourseMapper courseMapper, QuestionBankQuestionMapper questionMapper) {
    this.courseMapper = courseMapper;
    this.questionMapper = questionMapper;
  }

  public List<QuestionBankQuestionRow> listForTeacher(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);
    List<QuestionBankQuestion> list = questionMapper.selectList(new LambdaQueryWrapper<QuestionBankQuestion>()
        .eq(QuestionBankQuestion::getCourseId, courseId)
        .eq(QuestionBankQuestion::getDeleted, 0)
        .orderByDesc(QuestionBankQuestion::getId));
    return list.stream().map(this::toRow).collect(Collectors.toList());
  }

  public Long create(Long teacherId, Long courseId, QuestionBankQuestion q) {
    requireTeacherOwnsCourse(teacherId, courseId);
    validateQuestion(q);

    LocalDateTime now = LocalDateTime.now();
    q.setId(null);
    q.setCourseId(courseId);
    q.setCreatedBy(teacherId);
    q.setCreatedAt(now);
    q.setUpdatedAt(now);
    q.setDeleted(0);
    if (q.getScore() == null) {
      q.setScore(5);
    }
    if (q.getQuestionType() == null) {
      q.setQuestionType(1);
    }
    questionMapper.insert(q);
    return q.getId();
  }

  public void update(Long teacherId, Long id, QuestionBankQuestion patch) {
    QuestionBankQuestion existed = questionMapper.selectById(id);
    if (existed == null || existed.getDeleted() != null && existed.getDeleted() != 0) {
      throw new BusinessException(40401, "题目不存在");
    }
    requireTeacherOwnsCourse(teacherId, existed.getCourseId());

    if (patch.getQuestionType() != null) existed.setQuestionType(patch.getQuestionType());
    if (patch.getQuestionText() != null) existed.setQuestionText(patch.getQuestionText());
    if (patch.getOptionA() != null) existed.setOptionA(patch.getOptionA());
    if (patch.getOptionB() != null) existed.setOptionB(patch.getOptionB());
    if (patch.getOptionC() != null) existed.setOptionC(patch.getOptionC());
    if (patch.getOptionD() != null) existed.setOptionD(patch.getOptionD());
    if (patch.getCorrectAnswer() != null) existed.setCorrectAnswer(patch.getCorrectAnswer());
    if (patch.getScore() != null) existed.setScore(patch.getScore());

    validateQuestion(existed);
    existed.setUpdatedAt(LocalDateTime.now());
    questionMapper.updateById(existed);
  }

  public void delete(Long teacherId, Long id) {
    QuestionBankQuestion existed = questionMapper.selectById(id);
    if (existed == null || existed.getDeleted() != null && existed.getDeleted() != 0) {
      return;
    }
    requireTeacherOwnsCourse(teacherId, existed.getCourseId());
    questionMapper.deleteById(id);
  }

  public int importFromCourse(Long teacherId, Long targetCourseId, Long sourceCourseId, List<Long> questionIds) {
    if (Objects.equals(targetCourseId, sourceCourseId)) {
      throw new BusinessException(40041, "源课程与目标课程不能相同");
    }

    requireTeacherOwnsCourse(teacherId, targetCourseId);
    requireTeacherOwnsCourse(teacherId, sourceCourseId);

    LambdaQueryWrapper<QuestionBankQuestion> qw = new LambdaQueryWrapper<QuestionBankQuestion>()
        .eq(QuestionBankQuestion::getCourseId, sourceCourseId)
        .eq(QuestionBankQuestion::getDeleted, 0)
        .orderByDesc(QuestionBankQuestion::getId);

    if (questionIds != null && !questionIds.isEmpty()) {
      qw.in(QuestionBankQuestion::getId, questionIds);
    }

    List<QuestionBankQuestion> src = questionMapper.selectList(qw);
    if (src.isEmpty()) {
      return 0;
    }

    LocalDateTime now = LocalDateTime.now();
    int count = 0;
    for (QuestionBankQuestion s : src) {
      QuestionBankQuestion c = new QuestionBankQuestion();
      c.setCourseId(targetCourseId);
      c.setQuestionType(s.getQuestionType());
      c.setQuestionText(s.getQuestionText());
      c.setOptionA(s.getOptionA());
      c.setOptionB(s.getOptionB());
      c.setOptionC(s.getOptionC());
      c.setOptionD(s.getOptionD());
      c.setCorrectAnswer(s.getCorrectAnswer());
      c.setScore(s.getScore());
      c.setCreatedBy(teacherId);
      c.setCreatedAt(now);
      c.setUpdatedAt(now);
      c.setDeleted(0);
      questionMapper.insert(c);
      count++;
    }
    return count;
  }

  public List<QuestionBankQuestionRow> randomPick(Long teacherId, Long courseId, int count) {
    requireTeacherOwnsCourse(teacherId, courseId);
    if (count <= 0) {
      throw new BusinessException(40042, "count 必须 >= 1");
    }
    int limit = Math.min(count, 50);
    List<QuestionBankQuestion> list = questionMapper.selectList(new LambdaQueryWrapper<QuestionBankQuestion>()
        .eq(QuestionBankQuestion::getCourseId, courseId)
        .eq(QuestionBankQuestion::getDeleted, 0)
        .last("ORDER BY RAND() LIMIT " + limit));
    return list.stream().map(this::toRow).collect(Collectors.toList());
  }

  private void validateQuestion(QuestionBankQuestion q) {
    if (q == null) {
      throw new BusinessException(40040, "题目不能为空");
    }
    if (!StringUtils.hasText(q.getQuestionText())) {
      throw new BusinessException(40040, "题干不能为空");
    }

    Integer type = q.getQuestionType() == null ? 1 : q.getQuestionType();
    // 最小实现：只强校验单选
    if (type == 1) {
      if (!StringUtils.hasText(q.getOptionA()) || !StringUtils.hasText(q.getOptionB()) || !StringUtils.hasText(q.getOptionC()) || !StringUtils.hasText(q.getOptionD())) {
        throw new BusinessException(40040, "选项 A-D 不能为空");
      }
      if (!StringUtils.hasText(q.getCorrectAnswer())) {
        throw new BusinessException(40040, "正确答案不能为空");
      }
    }

    if (q.getScore() != null && q.getScore() <= 0) {
      throw new BusinessException(40040, "分值必须 > 0");
    }
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

  private QuestionBankQuestionRow toRow(QuestionBankQuestion q) {
    QuestionBankQuestionRow r = new QuestionBankQuestionRow();
    r.setId(q.getId());
    r.setCourseId(q.getCourseId());
    r.setQuestionType(q.getQuestionType());
    r.setQuestionText(q.getQuestionText());
    r.setOptionA(q.getOptionA());
    r.setOptionB(q.getOptionB());
    r.setOptionC(q.getOptionC());
    r.setOptionD(q.getOptionD());
    r.setCorrectAnswer(q.getCorrectAnswer());
    r.setScore(q.getScore());
    return r;
  }
}
