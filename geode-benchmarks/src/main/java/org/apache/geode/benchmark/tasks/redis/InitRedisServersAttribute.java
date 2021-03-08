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

package org.apache.geode.benchmark.tasks.redis;


import java.net.InetSocketAddress;
import java.util.Collection;

import org.apache.geode.benchmark.tests.redis.RedisBenchmark;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;

public class InitRedisServersAttribute implements Task {
  final Collection<InetSocketAddress> servers;

  public InitRedisServersAttribute(final Collection<InetSocketAddress> servers) {
    this.servers = servers;
  }

  @Override
  public void run(final TestContext context) throws Exception {
    context.setAttribute(RedisBenchmark.REDIS_SERVERS_ATTRIBUTE, servers);
  }

}
