/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geode.benchmark.tests;

import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import org.apache.geode.benchmark.tasks.StartSniProxy;
import org.apache.geode.benchmark.tasks.StartSocks5Proxy;
import org.apache.geode.perftest.TestConfig;

class GeodeBenchmarkTest {

  private TestConfig config;

  @AfterAll
  public static void afterAll() {
    System.clearProperty("withSniProxy");
    System.clearProperty("withSocks5Proxy");
  }

  @Test
  public void withoutSniProxy() {
    System.clearProperty("withSniProxy");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).noneMatch(s -> s.getTask() instanceof StartSniProxy);
  }

  @Test
  public void withSniProxyFalse() {
    System.setProperty("withSniProxy", "false");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).noneMatch(s -> s.getTask() instanceof StartSniProxy);
  }

  @Test
  public void withSniProxyTrue() {
    System.setProperty("withSniProxy", "true");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).anyMatch(s -> s.getTask() instanceof StartSniProxy);
  }

  @Test
  public void withSniProxyNotLowercaseFalse() {
    System.setProperty("withSniProxy", "AnythING");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).noneMatch(s -> s.getTask() instanceof StartSniProxy);
  }

  @Test
  public void withoutSocks5Proxy() {
    System.clearProperty("withSocks5Proxy");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).noneMatch(s -> s.getTask() instanceof StartSocks5Proxy);
  }

  @Test
  public void withSocks5ProxyFalse() {
    System.setProperty("withSocks5Proxy", "false");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).noneMatch(s -> s.getTask() instanceof StartSocks5Proxy);
  }

  @Test
  public void withSocks5ProxyTrue() {
    System.setProperty("withSocks5Proxy", "true");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).anyMatch(s -> s.getTask() instanceof StartSocks5Proxy);
  }

  @Test
  public void withSocks5ProxyNotLowercaseFalse() {
    System.setProperty("withSocks5Proxy", "AnythING");
    config = GeodeBenchmark.createConfig();
    assertThat(config.getBefore()).noneMatch(s -> s.getTask() instanceof StartSocks5Proxy);
  }

}
