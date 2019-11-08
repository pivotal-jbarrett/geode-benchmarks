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
package org.apache.geode.benchmark.topology;

import static org.apache.geode.benchmark.topology.PrimarySecondaryTopology.Roles.LOCATOR;
import static org.apache.geode.benchmark.topology.PrimarySecondaryTopology.Roles.PRIMARY;
import static org.apache.geode.benchmark.topology.PrimarySecondaryTopology.Roles.SECONDARY;

import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.geode.benchmark.parameters.GcLoggingParameters;
import org.apache.geode.benchmark.parameters.GcParameters;
import org.apache.geode.benchmark.parameters.HeapParameters;
import org.apache.geode.benchmark.parameters.JvmParameters;
import org.apache.geode.benchmark.parameters.ProfilerParameters;
import org.apache.geode.benchmark.tasks.StartClient;
import org.apache.geode.benchmark.tasks.StartLocator;
import org.apache.geode.benchmark.tasks.StartServer;
import org.apache.geode.perftest.TestConfig;

public class PrimarySecondaryTopology {
  private static final Logger logger = LoggerFactory.getLogger(PrimarySecondaryTopology.class);

  /**
   * All roles defined for the JVMs created for the benchmark
   */
  public static class Roles {
    public static final String LOCATOR = "locator";
    public static final String PRIMARY = "primary";
    public static final String SECONDARY = "secondary";
  }

  /**
   * The port used to create the locator for the tests
   */
  public static final int LOCATOR_PORT = 10334;

  static final int NUM_LOCATORS = 1;
  static final int NUM_PRIMARIES = 1;
  static final int NUM_SECONDARIES = 1;
  private static final String WITH_SSL_ARGUMENT = "-DwithSsl=true";
  private static final String WITH_SECURITY_MANAGER_ARGUMENT = "-DwithSecurityManager=true";

  public static void configure(TestConfig testConfig) {
    testConfig.role(LOCATOR, NUM_LOCATORS);
    testConfig.role(PRIMARY, NUM_PRIMARIES);
    testConfig.role(SECONDARY, NUM_SECONDARIES);

    JvmParameters.configure(testConfig);
    HeapParameters.configure(testConfig);
    GcLoggingParameters.configure(testConfig);
    GcParameters.configure(testConfig);
    ProfilerParameters.configure(testConfig);

    addToTestConfig(testConfig, "withSsl", WITH_SSL_ARGUMENT);
    addToTestConfig(testConfig, "withSecurityManager", WITH_SECURITY_MANAGER_ARGUMENT);

    testConfig.before(new StartLocator(LOCATOR_PORT), LOCATOR);
    testConfig.before(new StartServer(LOCATOR_PORT), PRIMARY);
    testConfig.before(new StartServer(LOCATOR_PORT), SECONDARY);
  }

  private static void addToTestConfig(TestConfig testConfig, String systemPropertyKey,
      String jvmArgument) {
    if (Boolean.getBoolean(systemPropertyKey)) {
      logger.info("Configuring JVMs to run with " + jvmArgument);
      testConfig.jvmArgs(LOCATOR, jvmArgument);
      testConfig.jvmArgs(PRIMARY, jvmArgument);
      testConfig.jvmArgs(SECONDARY, jvmArgument);
    }
  }

  private static String[] appendIfNotEmpty(String[] a, String b) {
    if (null == b || b.length() == 0) {
      return a;
    }

    return Arrays.append(a, b);
  }
}
