package com.microsoft.gbb.reddog.makelineservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.dapr.client.DaprClientBuilder;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprPreviewClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DaprConfig {

  private static final DaprClientBuilder BUILDER = new DaprClientBuilder();

  @Bean
  @Qualifier("daprClient")
  public DaprClient buildDaprClient() {
    log.info("Creating a new Dapr Client");
    return BUILDER.build();
  }

  @Bean
  @Qualifier("daprPreviewClient")
  public DaprPreviewClient buildDaprPreviewClient() {
    log.info("Creating a new Dapr preview Client");
    return BUILDER.buildPreviewClient();
  }
}