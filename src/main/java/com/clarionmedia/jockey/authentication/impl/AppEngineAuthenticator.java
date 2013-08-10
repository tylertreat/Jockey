/*
 * Copyright (C) 2013 Clarion Media, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clarionmedia.jockey.authentication.impl;

import android.accounts.Account;
import android.accounts.AccountManager;
import com.clarionmedia.jockey.authentication.Authenticator;
import com.clarionmedia.jockey.authentication.OnAuthenticationListener;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link com.clarionmedia.jockey.authentication.Authenticator} for Google App Engine.
 */
public class AppEngineAuthenticator implements Authenticator {

    private static final String TOKEN_TYPE = "ah";
    private static final String AUTH_ENDPOINT = "/_ah/login?continue=http://localhost/&auth=";

    private Account mAccount;
    private AccountManager mAccountManager;
    private String mUrl;
    private List<OnAuthenticationListener> mAuthenticationListeners;
    private HttpClient mHttpClient;

    public AppEngineAuthenticator(AccountManager accountManager, Account account, HttpClient httpClient, String url) {
        mAccount = account;
        mAccountManager = accountManager;
        mUrl = url;
        mAuthenticationListeners = new ArrayList<OnAuthenticationListener>();
        mHttpClient = httpClient;

        // Don't follow redirects
        mHttpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
    }

    @Override
    public void authenticate() {
        mAccountManager.getAuthToken(mAccount, TOKEN_TYPE, false, new GetAuthTokenCallback(this), null);
    }

    @Override
    public synchronized List<OnAuthenticationListener> getOnAuthenticationListeners() {
        return mAuthenticationListeners;
    }

    @Override
    public HttpClient getHttpClient() {
        return mHttpClient;
    }

    @Override
    public String getAuthUrl(String token) {
        return mUrl + AUTH_ENDPOINT + token;
    }

    @Override
    public synchronized void registerOnAuthenticationListener(OnAuthenticationListener authenticationListener) {
        mAuthenticationListeners.add(authenticationListener);
    }

}
