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
  public static final String START_PROXY_COMMAND = "docker run --rm -d -v %s:/etc/sockd.conf -p %d:1080 --name dante wernight/dante";

  private final int proxyPort;

  public StartSocks5Proxy(final int proxyPort) {
    this.proxyPort = proxyPort;
  }

  @Override
  public void run(final TestContext context) throws Exception {
    final Path configFile = Paths.get(getProperty("user.home"), "sockd.conf");
    rewriteFile(generateConfig(context), configFile);

    final ProcessControl processControl = new ProcessControl();
    processControl.runCommand(START_DOCKER_DAEMON_COMMAND);
    processControl.runCommand(format(START_PROXY_COMMAND, configFile, proxyPort));
  }

  private void rewriteFile(final String content, final Path path) throws IOException {
    try (final BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), false))) {
      writer.write(content);
    }
  }

  String generateConfig(final TestContext context) {
    StringBuilder config = new StringBuilder("logoutput: stderr\n"
        + "internal: 0.0.0.0 port = 1080\n"
        + "external: eth0\n"
        + "external.rotation: route\n"
        + "socksmethod: none\n"
        + "clientmethod: none\n"
        + "user.privileged: root\n"
        + "\n"
        + "cpu.schedule.io:        fifo/15\n"
        + "\n"
        + "# Allow everyone to connect to this server.\n"
        + "client pass {\n"
        + "    from: 0.0.0.0/0 to: 0.0.0.0/0\n"
        + "    log: error connect disconnect\n"
        + "}\n"
        + "\n"
        + "# Allow all operations for connected clients on this server.\n"
        + "socks pass {\n"
        + "    from: 0.0.0.0/0 to: 0.0.0.0/0\n"
        + "    command: bind connect udpassociate\n"
        + "    log: error connect disconnect #iooperation\n"
        + "}\n"
        + "\n"
        + "# Allow all inbound packets.\n"
        + "socks pass {\n"
        + "    from: 0.0.0.0/0 to: 0.0.0.0/0\n"
        + "    command: bindreply udpreply\n"
        + "    log: error connect disconnect #iooperation\n"
        + "}\n");

    return config.toString();
  }

}
