package com.cloudclassroom.modules.user.dto;

import java.util.List;

/**
 * 分页返回（管理端通用）。
 */
public class PageResult<T> {
  private long total;
  private long pageNo;
  private long pageSize;
  private List<T> records;

  public PageResult() {
  }

  public PageResult(long total, long pageNo, long pageSize, List<T> records) {
    this.total = total;
    this.pageNo = pageNo;
    this.pageSize = pageSize;
    this.records = records;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public long getPageNo() {
    return pageNo;
  }

  public void setPageNo(long pageNo) {
    this.pageNo = pageNo;
  }

  public long getPageSize() {
    return pageSize;
  }

  public void setPageSize(long pageSize) {
    this.pageSize = pageSize;
  }

  public List<T> getRecords() {
    return records;
  }

  public void setRecords(List<T> records) {
    this.records = records;
  }
}
