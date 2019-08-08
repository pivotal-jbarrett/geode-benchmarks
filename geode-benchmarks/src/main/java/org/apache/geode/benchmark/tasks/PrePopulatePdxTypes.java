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

import static org.apache.geode.benchmark.topology.ClientServerTopology.Roles.LOCATOR;
import static org.apache.geode.benchmark.topology.ClientServerTopology.Roles.SERVER;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.geode.benchmark.LongRange;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;
import org.apache.geode.perftest.jvms.RemoteJVMFactory;

public class PrePopulatePdxTypes implements Task {

  private static final Logger logger = LoggerFactory.getLogger(RemoteJVMFactory.class);

  private final LongRange range;

  public PrePopulatePdxTypes(LongRange range) {
    this.range = range;
  }

  /**
   * This method prepopulates the region
   * before the actual benchmark starts.
   *
   */
  @Override
  public void run(final TestContext context) throws InterruptedException {
    final int numLocators = context.getHostsIDsForRole(LOCATOR).size();
    final int numServers = context.getHostsIDsForRole(SERVER).size();
    final int jvmID = context.getJvmID();
    final int serverIndex = jvmID - numLocators;
    run(range.sliceFor(numServers, serverIndex));
  }

  void run(final LongRange range)
      throws InterruptedException {

    logger.info("*******************************************");
    logger.info("      Prepopulating the pdx types ");
    logger.info("*******************************************");

    final Instant start = Instant.now();

    final int numThreads = Runtime.getRuntime().availableProcessors();
    final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
    final List<CompletableFuture<Void>> futures = new ArrayList<>();
    for (final LongRange slice : range.slice(numThreads)) {
      futures.add(CompletableFuture.runAsync(() -> doCreatePdxType(slice), threadPool));
    }

    futures.forEach(CompletableFuture::join);

    final Instant finish = Instant.now();

    logger.info("*******************************************");
    logger.info("    Prepopulating the pdx types");
    logger.info("    Duration = " + Duration.between(start, finish).toMillis() + "ms.");
    logger.info("*******************************************");

    threadPool.shutdownNow();
    threadPool.awaitTermination(5, TimeUnit.MINUTES);
  }

  private void doCreatePdxType(final LongRange range) {
    range.forEach(CreatePdxTypeTask::createPdxType);
  }


}
