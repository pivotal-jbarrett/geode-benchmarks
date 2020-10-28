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

import static java.net.Proxy.Type.SOCKS;
import static org.apache.geode.benchmark.topology.Roles.PROXY;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.Properties;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.SocketFactory;
import org.apache.geode.perftest.TestContext;

public class StartClientSocks5 extends StartClient {

  private final int proxyPort;

  public StartClientSocks5(final int locatorPort, final int proxyPort) {
    super(locatorPort);
    this.proxyPort = proxyPort;
  }

  @Override
  protected ClientCacheFactory createClientCacheFactory(final InetAddress locator,
      final String statsFile,
      final Properties properties,
      final TestContext context)
      throws NoSuchMethodException, InvocationTargetException,
      IllegalAccessException, ClassNotFoundException {

    final ClientCacheFactory cacheFactory =
        super.createClientCacheFactory(locator, statsFile, properties, context);

    final InetAddress proxyInetAddress =
        context.getHostsForRole(PROXY.name()).stream().findFirst().get();

    cacheFactory.setPoolSocketFactory(new Socks5SocketFactory(proxyInetAddress.getHostName(), proxyPort));

    return cacheFactory;
  }

  public static class Socks5SocketFactory implements SocketFactory {
    private final String hostname;
    private final int port;

    public Socks5SocketFactory(final String hostname, final int port) {
      this.hostname = hostname;
      this.port = port;
    }

    @Override
    public Socket createSocket() {
      return new Socket(new Proxy(SOCKS, new InetSocketAddress(hostname, port)));
    }
  }
}
