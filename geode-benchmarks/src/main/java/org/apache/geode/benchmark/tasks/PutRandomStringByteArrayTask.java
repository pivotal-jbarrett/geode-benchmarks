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
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.util.concurrent.RateLimiter;
import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;

@SuppressWarnings("UnstableApiUsage")
public class PutRandomStringByteArrayTask extends BenchmarkDriverAdapter implements Serializable {
  private final byte[] value;

  transient private Region<String, byte[]> region;
  transient private RateLimiter rateLimiter;

  public PutRandomStringByteArrayTask() {
    value = new byte[1000];
  }

  @Override
  public void setUp(BenchmarkConfiguration cfg) throws Exception {
    super.setUp(cfg);

    final Cache cache = CacheFactory.getAnyInstance();
    region = cache.getRegion("region");

    rateLimiter = RateLimiter.create(4000.0);
  }

  @Override
  public boolean test(Map<Object, Object> ctx) throws Exception {
    rateLimiter.acquire();
    final String key = getRandomString(64);
    region.put(key, value);
    return true;
  }

  public static String getRandomString(final int count) {
    return RandomStringUtils.random(count, 0, 0, true, true, null,
        ThreadLocalRandom.current());
  }
}
