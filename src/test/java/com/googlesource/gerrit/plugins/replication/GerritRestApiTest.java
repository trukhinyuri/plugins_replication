// Copyright (C) 2026 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.replication;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.gerrit.entities.Project;
import com.googlesource.gerrit.plugins.replication.HttpResponseHandler.HttpResult;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.Before;
import org.junit.Test;

public class GerritRestApiTest {
  private CredentialsFactory credentialsFactory;
  private CloseableHttpClient httpClient;
  private RemoteConfig remoteConfig;
  private GerritRestApi restApi;

  @Before
  public void setUp() throws Exception {
    credentialsFactory = mock(CredentialsFactory.class);
    CredentialsProvider cp = mock(CredentialsProvider.class);
    when(credentialsFactory.create(any())).thenReturn(cp);
    httpClient = mock(CloseableHttpClient.class);
    remoteConfig = mock(RemoteConfig.class);
    when(remoteConfig.getName()).thenReturn("remote");
    restApi =
        new GerritRestApi(
            credentialsFactory,
            httpClient,
            remoteConfig,
            new URIish("gerrit+http://example.com:8080/"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void deleteProjectReturnsFalseWhenHttpResponseIsNotSuccessful() throws Exception {
    when(httpClient.execute(
            any(HttpDelete.class), any(ResponseHandler.class), any(HttpContext.class)))
        .thenReturn(new HttpResult(false, "HTTP 500"));

    assertThat(restApi.deleteProject(Project.nameKey("test-project"))).isFalse();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void updateHeadReturnsFalseWhenHttpResponseIsNotSuccessful() throws Exception {
    when(httpClient.execute(
            any(HttpPut.class), any(ResponseHandler.class), any(HttpContext.class)))
        .thenReturn(new HttpResult(false, "HTTP 500"));

    assertThat(restApi.updateHead(Project.nameKey("test-project"), "refs/heads/main")).isFalse();
  }
}
