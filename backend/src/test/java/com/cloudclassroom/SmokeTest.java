package com.cloudclassroom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 冒烟测试：保证 Spring 上下文能启动。
 */
@SpringBootTest
public class SmokeTest {

  @Test
  void contextLoads() {
    // 只要能启动就算通过
  }
}
