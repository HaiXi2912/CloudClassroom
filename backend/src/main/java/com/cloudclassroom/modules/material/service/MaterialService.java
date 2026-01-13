package com.cloudclassroom.modules.material.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.course.entity.Course;
import com.cloudclassroom.modules.course.entity.CourseMember;
import com.cloudclassroom.modules.course.mapper.CourseMapper;
import com.cloudclassroom.modules.course.mapper.CourseMemberMapper;
import com.cloudclassroom.modules.material.dto.MaterialProgressRow;
import com.cloudclassroom.modules.material.dto.MaterialRow;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.entity.Material;
import com.cloudclassroom.modules.material.entity.MaterialProgress;
import com.cloudclassroom.modules.material.mapper.FileObjectMapper;
import com.cloudclassroom.modules.material.mapper.MaterialMapper;
import com.cloudclassroom.modules.material.mapper.MaterialProgressMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资料与学习进度服务。
 */
@Service
public class MaterialService {

  private final CourseMapper courseMapper;
  private final CourseMemberMapper courseMemberMapper;
  private final FileObjectMapper fileObjectMapper;
  private final MaterialMapper materialMapper;
  private final MaterialProgressMapper materialProgressMapper;

  public MaterialService(CourseMapper courseMapper,
                         CourseMemberMapper courseMemberMapper,
                         FileObjectMapper fileObjectMapper,
                         MaterialMapper materialMapper,
                         MaterialProgressMapper materialProgressMapper) {
    this.courseMapper = courseMapper;
    this.courseMemberMapper = courseMemberMapper;
    this.fileObjectMapper = fileObjectMapper;
    this.materialMapper = materialMapper;
    this.materialProgressMapper = materialProgressMapper;
  }

  public Material createMaterial(Long teacherId, Long courseId, Long fileId, String title, Integer materialType, Integer sortNo) {
    requireTeacherOwnsCourse(teacherId, courseId);

    FileObject f = fileObjectMapper.selectById(fileId);
    if (f == null || f.getDeleted() == null || f.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }

    Material m = new Material();
    m.setCourseId(courseId);
    m.setFileId(fileId);
    m.setTitle(title);
    m.setMaterialType(materialType);
    m.setSortNo(sortNo == null ? 0 : sortNo);
    m.setCreatedBy(teacherId);
    m.setCreatedAt(LocalDateTime.now());
    m.setUpdatedAt(LocalDateTime.now());
    m.setDeleted(0);
    materialMapper.insert(m);
    return m;
  }

  public List<MaterialRow> listTeacherMaterials(Long teacherId, Long courseId) {
    requireTeacherOwnsCourse(teacherId, courseId);
    return listMaterialsInternal(courseId);
  }

  public List<MaterialRow> listStudentMaterials(Long studentId, Long courseId) {
    requireStudentInCourse(studentId, courseId);
    return listMaterialsInternal(courseId);
  }

  public MaterialProgressRow getMyProgress(Long studentId, Long materialId) {
    requireStudentCanAccessMaterial(studentId, materialId);

    MaterialProgress p = materialProgressMapper.selectOne(new LambdaQueryWrapper<MaterialProgress>()
        .eq(MaterialProgress::getMaterialId, materialId)
        .eq(MaterialProgress::getUserId, studentId)
        .eq(MaterialProgress::getDeleted, 0)
        .last("LIMIT 1"));

    MaterialProgressRow r = new MaterialProgressRow();
    r.setMaterialId(materialId);
    r.setUserId(studentId);

    if (p == null) {
      r.setProgressPercent(0);
      r.setLastPosition(0);
      r.setLastStudyAt(null);
      return r;
    }

    r.setProgressPercent(p.getProgressPercent());
    r.setLastPosition(p.getLastPosition());
    r.setLastStudyAt(p.getLastStudyAt());
    return r;
  }

  public void upsertMyProgress(Long studentId, Long materialId, Integer progressPercent, Integer lastPosition) {
    requireStudentCanAccessMaterial(studentId, materialId);

    int pp = progressPercent == null ? 0 : progressPercent;
    if (pp < 0) {
      pp = 0;
    }
    if (pp > 100) {
      pp = 100;
    }

    int lp = lastPosition == null ? 0 : lastPosition;
    if (lp < 0) {
      lp = 0;
    }

    MaterialProgress p = materialProgressMapper.selectOne(new LambdaQueryWrapper<MaterialProgress>()
        .eq(MaterialProgress::getMaterialId, materialId)
        .eq(MaterialProgress::getUserId, studentId)
        .eq(MaterialProgress::getDeleted, 0)
        .last("LIMIT 1"));

    if (p == null) {
      p = new MaterialProgress();
      p.setMaterialId(materialId);
      p.setUserId(studentId);
      p.setProgressPercent(pp);
      p.setLastPosition(lp);
      p.setLastStudyAt(LocalDateTime.now());
      p.setCreatedAt(LocalDateTime.now());
      p.setUpdatedAt(LocalDateTime.now());
      p.setDeleted(0);
      materialProgressMapper.insert(p);
      return;
    }

    p.setProgressPercent(pp);
    p.setLastPosition(lp);
    p.setLastStudyAt(LocalDateTime.now());
    p.setUpdatedAt(LocalDateTime.now());
    materialProgressMapper.updateById(p);
  }

  /**
   * 学生下载资料文件：校验“资料存在 + 已加入课程”，返回文件对象。
   */
  public FileObject getFileObjectForStudent(Long studentId, Long materialId) {
    Material m = requireStudentCanAccessMaterial(studentId, materialId);
    FileObject f = fileObjectMapper.selectById(m.getFileId());
    if (f == null || f.getDeleted() == null || f.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }
    return f;
  }

  /**
   * 老师下载资料文件：校验“资料存在 + 课程归属”，返回文件对象。
   */
  public FileObject getFileObjectForTeacher(Long teacherId, Long materialId) {
    Material m = materialMapper.selectById(materialId);
    if (m == null || m.getDeleted() == null || m.getDeleted() != 0) {
      throw new BusinessException(40421, "资料不存在");
    }
    requireTeacherOwnsCourse(teacherId, m.getCourseId());
    FileObject f = fileObjectMapper.selectById(m.getFileId());
    if (f == null || f.getDeleted() == null || f.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }
    return f;
  }

  private List<MaterialRow> listMaterialsInternal(Long courseId) {
    List<Material> list = materialMapper.selectList(new LambdaQueryWrapper<Material>()
        .eq(Material::getCourseId, courseId)
        .eq(Material::getDeleted, 0)
        .orderByAsc(Material::getSortNo)
        .orderByAsc(Material::getId));

    return list.stream().map(this::toRow).collect(Collectors.toList());
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

  private Material requireStudentCanAccessMaterial(Long studentId, Long materialId) {
    Material material = materialMapper.selectById(materialId);
    if (material == null || material.getDeleted() == null || material.getDeleted() != 0) {
      throw new BusinessException(40421, "资料不存在");
    }
    requireStudentInCourse(studentId, material.getCourseId());
    return material;
  }

  private MaterialRow toRow(Material m) {
    MaterialRow r = new MaterialRow();
    r.setId(m.getId());
    r.setCourseId(m.getCourseId());
    r.setFileId(m.getFileId());
    r.setTitle(m.getTitle());
    r.setMaterialType(m.getMaterialType());
    r.setSortNo(m.getSortNo());
    r.setCreatedAt(m.getCreatedAt());
    return r;
  }
}
