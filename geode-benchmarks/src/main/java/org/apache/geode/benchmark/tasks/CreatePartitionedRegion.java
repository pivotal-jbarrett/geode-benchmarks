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


import static org.apache.geode.benchmark.tasks.StartServer.SERVER_CACHE;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.PartitionAttributesFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.perftest.Task;
import org.apache.geode.perftest.TestContext;

/**
 * Task to create a Partitioned region on the server
 */
public class CreatePartitionedRegion implements Task {

  @Override
  public void run(TestContext context) throws Exception {
    final Cache cache = (Cache) context.getAttribute(SERVER_CACHE);
    final PartitionAttributes<?, ?> partitionAttributes = new PartitionAttributesFactory<>()
        .setTotalNumBuckets(1 << 8)
        .setRedundantCopies(1)
        .create();
    final Region<?, ?> region = cache.createRegionFactory(RegionShortcut.PARTITION_REDUNDANT)
        .setPartitionAttributes(partitionAttributes)
        .create("region");
    PartitionRegionHelper.assignBucketsToPartitions(region);
  }
}
