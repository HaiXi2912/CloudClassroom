package com.cloudclassroom.modules.material.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件存储配置项。
 */
@Component
@ConfigurationProperties(prefix = "cloudclassroom.file")
public class FileStorageProperties {

  /**
   * 存储基目录。
   */
  private String baseDir;

  /**
   * 最大上传大小（字节）。
   */
  private Long maxSizeBytes;

  /**
   * 允许的扩展名列表（逗号分隔）。
   */
  private String allowedExt;

  public String getBaseDir() {
    return baseDir;
  }

  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  public Long getMaxSizeBytes() {
    return maxSizeBytes;
  }

  public void setMaxSizeBytes(Long maxSizeBytes) {
    this.maxSizeBytes = maxSizeBytes;
  }

  public String getAllowedExt() {
    return allowedExt;
  }

  public void setAllowedExt(String allowedExt) {
    this.allowedExt = allowedExt;
  }
}
