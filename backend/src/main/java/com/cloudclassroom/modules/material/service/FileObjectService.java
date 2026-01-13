package com.cloudclassroom.modules.material.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloudclassroom.common.BusinessException;
import com.cloudclassroom.modules.material.config.FileStorageProperties;
import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.mapper.FileObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 文件对象服务：上传/读取文件（本地存储）。
 */
@Service
public class FileObjectService {

  private final FileObjectMapper fileObjectMapper;
  private final FileStorageProperties props;

  public FileObjectService(FileObjectMapper fileObjectMapper, FileStorageProperties props) {
    this.fileObjectMapper = fileObjectMapper;
    this.props = props;
  }

  /**
   * 上传文件并落库。
   */
  public FileObject upload(Long uploaderId, MultipartFile file) {
    if (uploaderId == null) {
      throw new BusinessException(40100, "未登录");
    }
    if (file == null || file.isEmpty()) {
      throw new BusinessException(40050, "文件不能为空");
    }

    long size = file.getSize();
    long max = props.getMaxSizeBytes() == null ? 20L * 1024 * 1024 : props.getMaxSizeBytes();
    if (size <= 0) {
      throw new BusinessException(40050, "文件不能为空");
    }
    if (size > max) {
      throw new BusinessException(40051, "文件过大");
    }

    String originalName = safeOriginalName(file.getOriginalFilename());
    String ext = getLowerExt(originalName);
    if (!isAllowedExt(ext)) {
      throw new BusinessException(40052, "不支持的文件类型");
    }

    // 生成存储相对路径：yyyy/MM/{uuid}.{ext}
    LocalDate today = LocalDate.now();
    String rel = String.format("%04d/%02d/%s.%s", today.getYear(), today.getMonthValue(), UUID.randomUUID(), ext);

    Path base = Paths.get(props.getBaseDir() == null ? "./data/uploads" : props.getBaseDir()).normalize();
    Path target = base.resolve(rel).normalize();
    if (!target.startsWith(base)) {
      throw new BusinessException(40053, "文件路径非法");
    }

    try {
      Files.createDirectories(target.getParent());
    } catch (IOException e) {
      throw new BusinessException(50001, "创建目录失败");
    }

    String sha256;
    try (InputStream in = file.getInputStream()) {
      sha256 = sha256Hex(in);
    } catch (IOException e) {
      throw new BusinessException(50002, "读取文件失败");
    }

    try (InputStream in2 = file.getInputStream()) {
      Files.copy(in2, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new BusinessException(50003, "保存文件失败");
    }

    FileObject fo = new FileObject();
    fo.setOriginalName(originalName);
    fo.setStoredPath(rel.replace("\\", "/"));
    fo.setContentType(file.getContentType());
    fo.setFileExt(ext);
    fo.setFileSize(size);
    fo.setSha256(sha256);
    fo.setCreatedBy(uploaderId);
    fo.setCreatedAt(LocalDateTime.now());
    fo.setDeleted(0);
    fileObjectMapper.insert(fo);
    return fo;
  }

  /**
   * 读取文件字节（用于下载）。
   */
  public byte[] readBytesById(Long fileId) {
    FileObject fo = fileObjectMapper.selectOne(new LambdaQueryWrapper<FileObject>()
        .eq(FileObject::getId, fileId)
        .eq(FileObject::getDeleted, 0)
        .last("LIMIT 1"));
    if (fo == null) {
      throw new BusinessException(40420, "文件不存在");
    }
    return readBytes(fo);
  }

  public byte[] readBytes(FileObject fo) {
    if (fo == null || fo.getId() == null || fo.getDeleted() == null || fo.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }
    String rel = fo.getStoredPath();
    if (rel == null || rel.trim().isEmpty()) {
      throw new BusinessException(50004, "文件路径缺失");
    }

    Path base = Paths.get(props.getBaseDir() == null ? "./data/uploads" : props.getBaseDir()).normalize();
    Path target = base.resolve(rel).normalize();
    if (!target.startsWith(base)) {
      throw new BusinessException(40053, "文件路径非法");
    }

    try {
      return Files.readAllBytes(target);
    } catch (NoSuchFileException e) {
      throw new BusinessException(40420, "文件不存在");
    } catch (IOException e) {
      throw new BusinessException(50005, "读取文件失败");
    }
  }

  private boolean isAllowedExt(String ext) {
    if (ext == null || ext.isEmpty()) {
      return false;
    }
    String allowed = props.getAllowedExt() == null ? "" : props.getAllowedExt();
    Set<String> set = Arrays.stream(allowed.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(String::toLowerCase)
        .collect(java.util.stream.Collectors.toSet());
    return set.contains(ext.toLowerCase(Locale.ROOT));
  }

  private String safeOriginalName(String originalFilename) {
    String name = originalFilename == null ? "" : originalFilename.trim();
    if (name.isEmpty()) {
      return "file";
    }
    // 仅取文件名部分，避免路径穿越。
    name = name.replace("\\", "/");
    int idx = name.lastIndexOf('/');
    if (idx >= 0) {
      name = name.substring(idx + 1);
    }
    // 限制长度，避免 header/日志问题。
    if (name.length() > 200) {
      name = name.substring(name.length() - 200);
    }
    return name;
  }

  private String getLowerExt(String filename) {
    if (filename == null) {
      return "";
    }
    int idx = filename.lastIndexOf('.');
    if (idx < 0 || idx == filename.length() - 1) {
      return "";
    }
    return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
  }

  private String sha256Hex(InputStream in) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new BusinessException(50006, "SHA-256 不可用");
    }

    byte[] buf = new byte[8192];
    int n;
    try {
      while ((n = in.read(buf)) > 0) {
        md.update(buf, 0, n);
      }
    } catch (IOException e) {
      throw new BusinessException(50002, "读取文件失败");
    }

    byte[] digest = md.digest();
    StringBuilder sb = new StringBuilder();
    for (byte b : digest) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
