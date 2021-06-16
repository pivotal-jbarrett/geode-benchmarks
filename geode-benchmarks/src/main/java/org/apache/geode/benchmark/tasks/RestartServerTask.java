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

  public RestartServerTask(final Task startServer, final Task stopServer) {
    this.startServer = startServer;
    this.stopServer = stopServer;
  }

  @Override
  public void run(TestContext context) throws Exception {
    if (context.getJvmID() == 1) {
      logger.info("RestartServerTask: I am the sacrificial server.");
      final Thread thread = new Thread(() -> {
        while (true) {
          try {
            logger.info("RestartServerTask: stopping server.");
            stopServer.run(context);
            logger.info("RestartServerTask: server stopped.");
          } catch (Exception e) {
            logger.warn("RestartServerTask: failed to stop server.", e);
          }
          logger.info("RestartServerTask: waiting after stop.");
          try {
            logger.info("RestartServerTask: starting server.");
            startServer.run(context);
            logger.info("RestartServerTask: server started.");
          } catch (Exception e) {
            logger.warn("RestartServerTask: failed to start server.", e);
          }
          try {
            logger.info("RestartServerTask: waiting after start.");
            Thread.sleep(SECONDS.toMillis(10));
            final Cache cache = CacheFactory.getAnyInstance();
            logger.info("RestartServerTask: starting rebalance.");
            final RebalanceOperation rebalanceOperation =
                cache.getResourceManager().createRebalanceFactory().start();
            final RebalanceResults results = rebalanceOperation.getResults();
            logger.info("RestartServerTask: ended rebalance. {}", results);
          } catch (InterruptedException e) {
            logger.warn("RestartServerTask: interrupted.");
          }
        }
      });
      thread.setDaemon(true);
      thread.start();
    }
  }
}
