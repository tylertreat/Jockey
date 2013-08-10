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

package com.clarionmedia.jockey.impl;

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.clarionmedia.jockey.Authenticator;
import com.clarionmedia.jockey.OnAuthenticationListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link com.clarionmedia.jockey.Authenticator} for Google App Engine.
 */
public class AppEngineAuthenticator implements Authenticator {

    private static final String TOKEN_TYPE = "ah";

    private Account mAccount;
    private AccountManager mAccountManager;
    private String mUrl;
    private List<OnAuthenticationListener> mAuthenticationListeners;
    private HttpClient mHttpClient;

    public AppEngineAuthenticator(Context context, Account account, String url) {
        mAccount = account;
        mAccountManager = AccountManager.get(context.getApplicationContext());
        mUrl = url;
        mAuthenticationListeners = new ArrayList<OnAuthenticationListener>();
        mHttpClient = new DefaultHttpClient();

        // Don't follow redirects
        mHttpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
    }

    @Override
    public void authenticate() {
        mAccountManager.getAuthToken(mAccount, TOKEN_TYPE, false, new GetAuthTokenCallback(), null);
    }

    public void registerAuthenticationListener(OnAuthenticationListener authenticationListener) {
        mAuthenticationListeners.add(authenticationListener);
    }

    private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {

        public void run(AccountManagerFuture result) {
            try {
                Bundle bundle = (Bundle) result.getResult();
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (intent != null) {
                    // TODO: User input required
                    return;
                } else {
                    onGetAuthToken(bundle);
                }
            } catch (OperationCanceledException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        protected void onGetAuthToken(Bundle bundle) {
            String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            new GetCookieTask().execute(auth_token);
        }

    }

    private class GetCookieTask extends AsyncTask<String, String, Cookie> {

        protected Cookie doInBackground(String... tokens) {
            // Create a local instance of cookie store
            CookieStore cookieStore = new BasicCookieStore();

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpGet get = new HttpGet(mUrl + "/_ah/login?continue=http://localhost/&auth=" + tokens[0]);

            try {
                HttpResponse response = mHttpClient.execute(get, localContext);
                if (response.getStatusLine().getStatusCode() != 302)
                    // Response should be a redirect
                    return null;

                for (Cookie cookie : cookieStore.getCookies()) {
                    if (cookie.getName().equals("ACSID"))
                        return cookie;
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                mHttpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            }
            return null;
        }

        protected void onPostExecute(Cookie cookie) {
            if (cookie == null) {
                return;
            }

            // Notify authentication listeners
            for (OnAuthenticationListener listener : mAuthenticationListeners) {
                listener.onAuthentication(cookie);
            }
        }

    }

}
