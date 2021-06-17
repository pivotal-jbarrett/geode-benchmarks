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

import static org.apache.geode.benchmark.Config.before;
import static org.apache.geode.benchmark.Config.workload;
import static org.apache.geode.benchmark.parameters.Utils.configureGeodeProductJvms;
import static org.apache.geode.benchmark.topology.Ports.EPHEMERAL_PORT;
import static org.apache.geode.benchmark.topology.Ports.LOCATOR_PORT;
import static org.apache.geode.benchmark.topology.Roles.CLIENT;
import static org.apache.geode.benchmark.topology.Roles.SERVER;

import org.junit.jupiter.api.Test;

import org.apache.geode.benchmark.LongRange;
import org.apache.geode.benchmark.tasks.CreateClientProxyRegion;
import org.apache.geode.benchmark.tasks.CreatePartitionedExpirationRegion;
import org.apache.geode.benchmark.tasks.PutRandomStringByteArrayTask;
import org.apache.geode.benchmark.tasks.PutStringTask;
import org.apache.geode.benchmark.tasks.RateLimittedTask;
import org.apache.geode.benchmark.tasks.RebalanceTask;
import org.apache.geode.benchmark.tasks.RestartServerTask;
import org.apache.geode.benchmark.tasks.StartServer;
import org.apache.geode.benchmark.tasks.StopServer;
import org.apache.geode.perftest.TestConfig;
import org.apache.geode.perftest.TestRunners;

/**
 * Benchmark of puts on a partitioned region.
 */
public class PartitionedPutWithExpirationBenchmark extends AbstractPerformanceTest {

  public PartitionedPutWithExpirationBenchmark() {
  }

  @Test
  public void run() throws Exception {
    TestRunners.defaultRunner().runTest(this);
  }

  @Override
  public TestConfig configure() {
    TestConfig config = ClientServerBenchmark.createConfig();

    // matching some properties at customer site.
    config.jvmArgs(SERVER.name(), "-Dp2p.HANDSHAKE_POOL_SIZE=120",
        "-DBridgeServer.HANDSHAKE_POOL_SIZE=80",
        "-Dp2p.backlog=1024",
        "-DDistributionManager.MAX_FE_THREADS=2048",
        "-XX:NewSize=2048m",
        "-XX:MaxNewSize=2048m",
        "-Dgemfire.tombstone-gc-threshold=1000",
        "-Dgemfire.tombstone-timeout=1000"
        );

    before(config, new CreatePartitionedExpirationRegion(), SERVER);
    before(config, new CreateClientProxyRegion(), CLIENT);

    workload(config, new RateLimittedTask(new PutStringTask(new LongRange(0, 1000000)), 10000), CLIENT);

    return config;
  }
}
