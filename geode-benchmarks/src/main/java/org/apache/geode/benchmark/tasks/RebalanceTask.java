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

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.control.RebalanceOperation;
import org.apache.geode.cache.control.RebalanceResults;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;

public class RebalanceTask implements Task {
  private static final Logger logger = LoggerFactory.getLogger(RebalanceTask.class);

  @Override
  public void run(TestContext context) throws Exception {
    final Cache cache = CacheFactory.getAnyInstance();

    final Thread thread = new Thread(() -> {
      logger.info("RebalanceTask: starting rebalance.");
      final RebalanceOperation rebalanceOperation =
          cache.getResourceManager().createRebalanceFactory().start();
      try {
        final RebalanceResults results = rebalanceOperation.getResults();
        logger.info("RebalanceTask: ended rebalance. {}", results);
      } catch (InterruptedException e) {
        logger.warn("RebalanceTask: interrupted.");
      }
    });
    thread.setDaemon(true);
    thread.start();
  }
}
