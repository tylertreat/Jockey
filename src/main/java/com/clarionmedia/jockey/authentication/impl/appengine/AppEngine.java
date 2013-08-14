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

package com.clarionmedia.jockey.authentication.impl.appengine;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import com.clarionmedia.jockey.authentication.AuthenticationProvider;
import com.clarionmedia.jockey.authentication.Authenticator;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class AppEngine extends AuthenticationProvider {

    private Account mAccount;
    private Activity mActivity;

    public AppEngine(String url) {
        super(url);
        mHttpClient = new DefaultHttpClient();
    }

    @Override
    public AppEngine promptConfirmationFrom(Activity activity) {
        mActivity = activity;
        return this;
    }

    @Override
    protected Authenticator buildAuthenticator(Context context) {
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

    @Override
    protected void checkPreconditions(Context context) {
        super.checkPreconditions(context);
        if (mAccount == null) {
            throw new RuntimeException("Account must not be null");
        }
    }

}
