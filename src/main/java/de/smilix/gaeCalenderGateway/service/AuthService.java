/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package de.smilix.gaeCalenderGateway.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.appengine.api.users.UserServiceFactory;

import de.smilix.gaeCalenderGateway.service.data.ConfigurationService;

public class AuthService {

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static final AppEngineDataStoreFactory DATA_STORE_FACTORY =
          AppEngineDataStoreFactory.getDefaultInstance();

  /** Global instance of the HTTP transport. */
  static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  /** Global instance of the JSON factory. */
  static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static AuthService instance;

  public static AuthService get() {
    if (instance == null) {
      instance = new AuthService();
    }

    return instance;
  }


  private GoogleClientSecrets clientSecrets = null;

  private AuthService() {
  }

  private GoogleClientSecrets getClientCredential() throws IOException {
    if (this.clientSecrets == null) {
      this.clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
              new InputStreamReader(AuthService.class.getResourceAsStream("/client_secrets.json")));

      Preconditions.checkArgument(!this.clientSecrets.getDetails().getClientId().startsWith("Enter ")
              && !this.clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
              "Download client_secrets.json file from https://code.google.com/apis/console/"
                      + "?api=calendar into calendar-appengine-sample/src/main/resources/client_secrets.json");
    }
    return this.clientSecrets;
  }

  public String getRedirectUri(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
    url.setRawPath("/oauth2callback");
    return url.build();
  }

  public GoogleAuthorizationCodeFlow newFlow() throws IOException {
    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
            getClientCredential(), Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(
            DATA_STORE_FACTORY).setAccessType("offline").build();
  }

  public Calendar loadCalendarClient() throws IOException {
    String userId = ConfigurationService.getConfig().getUserId();
    if (userId == null) {
      userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    }
    Credential credential = newFlow().loadCredential(userId);
    return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
  }
}
