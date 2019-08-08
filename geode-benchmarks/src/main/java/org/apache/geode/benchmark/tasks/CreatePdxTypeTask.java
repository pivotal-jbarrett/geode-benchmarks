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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.benchmark.LongRange;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.perftest.TestContext;
import org.apache.geode.perftest.jvms.rmi.Worker;

public class CreatePdxTypeTask extends BenchmarkDriverAdapter implements Serializable {
  private final LongRange range;
  private final AtomicLong counter = new AtomicLong();

  private LongRange serverRange;

  public CreatePdxTypeTask(final LongRange range) {
    this.range = range;
  }

  @Override
  public void setUp(final BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);

    TestContext context = Worker.getCurrentTestContext();
    setUp(context);
  }

  private void setUp(final TestContext context) {
    final int numLocators = context.getHostsIDsForRole(LOCATOR).size();
    final int numServers = context.getHostsIDsForRole(SERVER).size();
    final int jvmID = context.getJvmID();
    final int serverIndex = jvmID - numLocators;
    serverRange = range.sliceFor(numServers, serverIndex);
    counter.set(serverRange.getMin());
  }

  @Override
  public boolean test(final Map<Object, Object> ctx) {
    final long id = counter.getAndIncrement();
    if (id > serverRange.getMax() || id < serverRange.getMin()) {
      return false;
    }
    createPdxType(id);
    return true;
  }

  static void createPdxType(final long id) {
    final String jsonString = "{\"field-" + id + "\": 0}";
    JSONFormatter.fromJSON(jsonString);
  }
}
