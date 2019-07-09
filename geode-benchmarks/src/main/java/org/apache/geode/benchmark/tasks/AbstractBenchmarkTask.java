/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.geode.benchmark.tasks;

import java.io.Serializable;

import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.distributed.DistributedSystem;

public abstract class AbstractBenchmarkTask extends BenchmarkDriverAdapter implements Serializable {

  private static final long BEGIN = 1;
  private static final long END = 0;

  private Statistics benchmarkMakerStatistics;
  private StatisticDescriptor warmup;
  private StatisticDescriptor measurement;

  @Override
  public void setUp(BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);

    final ClientCache cache = ClientCacheFactory.getAnyInstance();
    final DistributedSystem distributedSystem = cache.getDistributedSystem();

    benchmarkMakerStatistics = distributedSystem.createAtomicStatistics(
        distributedSystem.createType("BenchmarkMarkers", "Markers for benchmark events.",
            new StatisticDescriptor[] {
                warmup = distributedSystem.createLongGauge("warmup",
                    "Marks the warmup stage of benchmark.", "marks"),
                measurement = distributedSystem.createLongGauge("measurement",
                    "Marks the measurement of benchmark.", "marks")
            }), "BenchmarkMarkers");

    benchmarkMakerStatistics.setLong(warmup, BEGIN);
  }

  @Override
  public void onWarmupFinished() {
    benchmarkMakerStatistics.setLong(warmup, END);

    super.onWarmupFinished();

    benchmarkMakerStatistics.setLong(measurement, BEGIN);
  }

  @Override
  public void tearDown() throws Exception {
    benchmarkMakerStatistics.setLong(measurement, END);

    super.tearDown();
  }
}
