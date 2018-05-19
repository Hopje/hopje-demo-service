package com.hopje.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

  @Value("${kafka.host}")
  private String kafkaHost;
  @Value("${service.delay.ms}")
  private long serviceDelayMs;

  public String getKafkaHost() {
    return kafkaHost;
  }

  public long getServiceDelayMs() {
    return serviceDelayMs;
  }
}
