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

import static java.util.concurrent.TimeUnit.SECONDS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.control.RebalanceOperation;
import org.apache.geode.cache.control.RebalanceResults;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;

public class RestartServerTask implements Task {
  private static final Logger logger = LoggerFactory.getLogger(RestartServerTask.class);

  private final Task startServer;
  private final Task stopServer;
  private final Task createRegion;

  public RestartServerTask(final Task startServer, final Task stopServer, final Task createRegion) {
    this.startServer = startServer;
    this.stopServer = stopServer;
    this.createRegion = createRegion;
  }

  @Override
  public void run(TestContext context) throws Exception {
    if (context.getJvmID() == 1) {
      logger.info("RestartServerTask: I am the sacrificial server.");
      final Thread thread = new Thread(() -> {
        while (true) {
          try {
            logger.info("RestartServerTask: waiting before stop.");
            Thread.sleep(SECONDS.toMillis(30));
            logger.info("RestartServerTask: stopping server.");
            stopServer.run(context);
            logger.info("RestartServerTask: server stopped.");
          } catch (Exception e) {
            logger.warn("RestartServerTask: failed to stop server.", e);
          }
          try {
            logger.info("RestartServerTask: waiting before start.");
            Thread.sleep(SECONDS.toMillis(1));
            logger.info("RestartServerTask: starting server.");
            startServer.run(context);
            createRegion.run(context);
            logger.info("RestartServerTask: server started.");
          } catch (Exception e) {
            logger.warn("RestartServerTask: failed to start server.", e);
          }
        }
      });
      thread.setDaemon(true);
      thread.start();
    }
  }
}
