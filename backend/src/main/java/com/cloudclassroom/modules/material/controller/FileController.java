package com.cloudclassroom.modules.material.controller;

import com.cloudclassroom.modules.material.entity.FileObject;
import com.cloudclassroom.modules.material.mapper.FileObjectMapper;
import com.cloudclassroom.modules.material.service.FileObjectService;
import com.cloudclassroom.common.BusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 通用：文件读取（需要登录，不限制角色）。
 *
 * <p>用于富文本内嵌图片/资源：前端可使用 /api/files/{fileId}/raw。
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

  private final FileObjectMapper fileObjectMapper;
  private final FileObjectService fileObjectService;

  public FileController(FileObjectMapper fileObjectMapper, FileObjectService fileObjectService) {
    this.fileObjectMapper = fileObjectMapper;
    this.fileObjectService = fileObjectService;
  }

  @GetMapping("/{fileId}/raw")
  public ResponseEntity<byte[]> raw(@PathVariable Long fileId) {
    FileObject fo = fileObjectMapper.selectById(fileId);
    if (fo == null || fo.getDeleted() == null || fo.getDeleted() != 0) {
      throw new BusinessException(40420, "文件不存在");
    }

    byte[] bytes = fileObjectService.readBytes(fo);

    String filename = fo.getOriginalName() == null ? "file" : fo.getOriginalName();
    String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

    MediaType ct = MediaType.APPLICATION_OCTET_STREAM;
    if (fo.getContentType() != null && !fo.getContentType().trim().isEmpty()) {
      try {
        ct = MediaType.parseMediaType(fo.getContentType());
      } catch (Exception ignored) {
        ct = MediaType.APPLICATION_OCTET_STREAM;
      }
    }

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(ct);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encoded);
    return ResponseEntity.ok().headers(headers).body(bytes);
  }
}
