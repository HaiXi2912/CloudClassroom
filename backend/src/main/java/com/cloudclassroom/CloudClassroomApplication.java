package com.cloudclassroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * 云课堂（CloudClassroom）后端启动类。
 */
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class CloudClassroomApplication {
  public static void main(String[] args) {
    SpringApplication.run(CloudClassroomApplication.class, args);
  }
}
