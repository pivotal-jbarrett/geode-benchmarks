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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.benchmark.LongRange;

public class HgetRedisTask extends BenchmarkDriverAdapter implements Serializable {
  private static final Logger logger = LoggerFactory.getLogger(HgetRedisTask.class);

  private final RedisClientManager redisClientManager;
  private final LongRange keyRange;
  private final boolean validate;

  private transient LongStringCache keyCache;
  private transient RedisClient redisClient;

  public HgetRedisTask(final RedisClientManager redisClientManager, final LongRange keyRange,
      final boolean validate) {
    logger.info("Initialized: keyRange={}, validate={}", keyRange, validate);
    this.redisClientManager = redisClientManager;
    this.keyRange = keyRange;
    this.validate = validate;
  }

  @Override
  public void setUp(final BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);

    keyCache = new LongStringCache(keyRange);
    redisClient = redisClientManager.get();
  }

  @Override
  public boolean test(final Map<Object, Object> ctx) throws Exception {
    final long k = keyRange.random();

    final String key = keyCache.valueOf(k / 1000);
    final String field = keyCache.valueOf(k % 1000);
    final String value = redisClient.hget(key, field);
    if (validate) {
      assertEquals(field, value);
    }
    return true;
  }

}