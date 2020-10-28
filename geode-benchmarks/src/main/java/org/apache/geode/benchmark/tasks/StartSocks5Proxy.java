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

package org.apache.geode.benchmark.tasks;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static org.apache.geode.benchmark.topology.Roles.LOCATOR;
import static org.apache.geode.benchmark.topology.Roles.SERVER;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;

/**
 * Task to start the SNI proxy
 */
public class StartSocks5Proxy implements Task {
  public static final String START_DOCKER_DAEMON_COMMAND = "sudo service docker start";
  public static final String START_PROXY_COMMAND = "docker run --rm -d -p %d:1080 --name dante wernight/dante";

  private final int proxyPort;

  public StartSocks5Proxy(final int proxyPort) {
    this.proxyPort = proxyPort;
  }

  @Override
  public void run(final TestContext context) throws Exception {
    final ProcessControl processControl = new ProcessControl();
    processControl.runCommand(START_DOCKER_DAEMON_COMMAND);
    processControl.runCommand(format(START_PROXY_COMMAND, proxyPort));
  }

}
