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
import android.app.Activity;
import android.content.Context;
import com.clarionmedia.jockey.authentication.AuthenticationProvider;
import com.clarionmedia.jockey.authentication.Authenticator;
import com.clarionmedia.jockey.authentication.OnAuthenticationListener;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class AppEngine extends AuthenticationProvider {

    private String mUrl;
    private Account mAccount;
    private Activity mActivity;

    public AppEngine() {
        mHttpClient = new DefaultHttpClient();
    }

    @Override
    public AppEngine at(String url) {
        mUrl = url;
        return this;
    }

    @Override
    public AppEngine promptConfirmationFrom(Activity activity) {
        mActivity = activity;
        return this;
    }

    @Override
    protected Authenticator buildAuthenticator(Context context) {
        checkPreconditions(context);
        return new AppEngineAuthenticator(context.getApplicationContext(), mAccount, mHttpClient, mUrl, mActivity);
    }

    public AppEngine withCredentials(Account account) {
        mAccount = account;
        return this;
    }

    public AppEngine using(AbstractHttpClient httpClient) {
        mHttpClient = httpClient;
        return this;
    }

    private void checkPreconditions(Context context) {
        if (context == null) {
            throw new RuntimeException("Context must not be null");
        }
        if (mAccount == null) {
            throw new RuntimeException("Account must not be null");
        }
        if (mHttpClient == null) {
            throw new RuntimeException("HttpClient must not be null");
        }
        if (mUrl == null) {
            throw new RuntimeException("URL must not be null");
        }
    }

}
