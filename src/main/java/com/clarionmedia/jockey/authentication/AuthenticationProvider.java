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

package com.clarionmedia.jockey.authentication;

import android.app.Activity;
import android.content.Context;
import com.clarionmedia.jockey.HttpClientProxy;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;

import java.lang.reflect.Proxy;

public abstract class AuthenticationProvider {

    protected String mUrl;
    protected AbstractHttpClient mHttpClient;
    protected Authenticator mAuthenticator;

    public AuthenticationProvider(String url) {
        mUrl = url;
    }

    public abstract AuthenticationProvider promptConfirmationFrom(Activity activity);

    protected abstract Authenticator buildAuthenticator(Context context);

    public AuthenticationProvider using(AbstractHttpClient httpClient) {
        mHttpClient = httpClient;
        return this;
    }

    public HttpClient now(Context context, OnAuthenticationListener onAuthenticationListener) {
        checkPreconditions(context);
        mAuthenticator = buildAuthenticator(context);
        mAuthenticator.setOnAuthenticationListener(onAuthenticationListener);

        mAuthenticator.authenticateAsync();

        return (HttpClient) Proxy.newProxyInstance(HttpClient.class.getClassLoader(),
                new Class<?>[]{HttpClient.class}, new HttpClientProxy(mAuthenticator));
    }

    public HttpClient now(Context context) {
        return now(context, null);
    }

    public String getUrl() {
        return mUrl;
    }

    protected void checkPreconditions(Context context) {
        if (context == null) {
            throw new RuntimeException("Context must not be null");
        }
        if (mHttpClient == null) {
            throw new RuntimeException("HttpClient must not be null");
        }
        if (mUrl == null) {
            throw new RuntimeException("URL must not be null");
        }
    }

}
