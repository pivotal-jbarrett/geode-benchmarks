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
import org.apache.commons.lang3.RandomStringUtils;
import org.yardstickframework.BenchmarkConfiguration;
import org.yardstickframework.BenchmarkDriver;
import org.yardstickframework.BenchmarkDriverAdapter;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;

@SuppressWarnings("UnstableApiUsage")
public class RateLimittedTask implements BenchmarkDriver, Serializable {
  private final BenchmarkDriver delegate;
  private double rateLimit;

  transient private RateLimiter rateLimiter;

  public RateLimittedTask(final BenchmarkDriver delegate, final double rateLimit) {
    this.delegate = delegate;
    this.rateLimit = rateLimit;
  }

  @Override
  public void setUp(BenchmarkConfiguration cfg) throws Exception {
    rateLimiter = RateLimiter.create(rateLimit);
    delegate.setUp(cfg);
  }

  @Override
  public boolean test(Map<Object, Object> ctx) throws Exception {
    rateLimiter.acquire();
    return delegate.test(ctx);
  }

  @Override
  public void tearDown() throws Exception {
    delegate.tearDown();
  }

  @Override
  public String description() {
    return delegate.description();
  }

  @Override
  public String usage() {
    return delegate.usage();
  }

  @Override
  public void onWarmupFinished() {
    delegate.onWarmupFinished();
  }

  @Override
  public void onException(final Throwable e) {
    delegate.onException(e);
  }
}
