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

import android.accounts.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.clarionmedia.jockey.authentication.Authenticator;
import com.clarionmedia.jockey.authentication.CookieStoreProvider;
import com.clarionmedia.jockey.authentication.OnAuthenticationListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class AppEngineAuthTokenCallback implements AccountManagerCallback<Bundle> {

    private static final String AUTH_COOKIE = "ACSID";

    private Authenticator mAuthenticator;
    private Context mContext;
    private CookieStoreProvider mCookieStoreProvider;

    public AppEngineAuthTokenCallback(Context context, CookieStoreProvider cookieStoreProvider,
                                      Authenticator authenticator) {
        mAuthenticator = authenticator;
        mContext = context.getApplicationContext();
        mCookieStoreProvider = cookieStoreProvider;
    }

    public void run(AccountManagerFuture result) {
        try {
            Bundle bundle = (Bundle) result.getResult();
            Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
            if (intent != null) {
                // User input required
                mContext.startActivity(intent);
            } else {
                onGetAuthToken(bundle, mCookieStoreProvider);
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

    protected void onGetAuthToken(Bundle bundle, CookieStoreProvider cookieStoreProvider) {
        String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
        new GetCookieTask(cookieStoreProvider).execute(auth_token);
    }

    private class GetCookieTask extends AsyncTask<String, String, Cookie> {

        private CookieStoreProvider mCookieStoreProvider;

        public GetCookieTask(CookieStoreProvider cookieStoreProvider) {
            mCookieStoreProvider = cookieStoreProvider;
        }

        protected Cookie doInBackground(String... tokens) {
            // Create a local instance of cookie store
            CookieStore cookieStore = mCookieStoreProvider.getNewCookieStore();

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpGet get = new HttpGet(mAuthenticator.getAuthUrl(tokens[0]));
            HttpClient httpClient = mAuthenticator.getHttpClient();

            // Don't follow redirects
            httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

            try {
                HttpResponse response = httpClient.execute(get, localContext);
                if (response.getStatusLine().getStatusCode() != 302)
                    // Response should be a redirect
                    return null;

                for (Cookie cookie : cookieStore.getCookies()) {
                    if (cookie.getName().equals(AUTH_COOKIE)) {
                        return cookie;
                    }
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                mAuthenticator.getHttpClient().getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
            }
            return null;
        }

        protected void onPostExecute(Cookie cookie) {
            if (cookie == null) {
                notifyListenersFailed();
            } else {
                notifyListenersSuccess(cookie);
            }
        }

        private void notifyListenersSuccess(Cookie cookie) {
            for (OnAuthenticationListener listener : mAuthenticator.getOnAuthenticationListeners()) {
                listener.onAuthenticationSuccess(cookie);
            }
        }

        private void notifyListenersFailed() {
            for (OnAuthenticationListener listener : mAuthenticator.getOnAuthenticationListeners()) {
                listener.onAuthenticationFailed();
            }
        }

    }

}
