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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import com.clarionmedia.jockey.authentication.Authenticator;
import com.clarionmedia.jockey.authentication.OnAuthenticationListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;

/**
 * Implementation of {@link com.clarionmedia.jockey.authentication.Authenticator} for Google App Engine.
 */
public class AppEngineAuthenticator implements Authenticator {

    public static final String AUTH_COOKIE = "SACSID";

    private static final String TOKEN_TYPE = "ah";
    private static final String AUTH_ENDPOINT = "/_ah/login?continue=http://localhost/&auth=";
    private static final String SHARED_PREFS = "__JOCKEY__";
    private static final String KEY_NAME = "_name";
    private static final String KEY_VAL = "_value";

    private Account mAccount;
    private AccountManager mAccountManager;
    private String mUrl;
    private AbstractHttpClient mHttpClient;
    private Activity mPromptActivity;
    private Context mContext;
    private OnAuthenticationListener mOnAuthenticationListener;
    private AppEngineAuthTokenCallback mAuthTokenCallback;

    public AppEngineAuthenticator(Context context, Account account, AbstractHttpClient httpClient, String url,
                                  Activity promptActivity) {
        mAccount = account;
        mAccountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        mUrl = url;
        mHttpClient = httpClient;
        mPromptActivity = promptActivity;
        mContext = context.getApplicationContext();
        mAuthTokenCallback = new AppEngineAuthTokenCallback();
    }

    @Override
    public void authenticateAsync() {
        if (mPromptActivity != null) {
            mAccountManager.getAuthToken(mAccount, TOKEN_TYPE, null, mPromptActivity, mAuthTokenCallback, null);
        } else {
            mAccountManager.getAuthToken(mAccount, TOKEN_TYPE, false, mAuthTokenCallback, null);
        }
    }

    @Override
    public Cookie authenticate() {
        try {
            String authToken = mAccountManager.blockingGetAuthToken(mAccount, TOKEN_TYPE, false);
            Cookie cookie = exchangeTokenForCookie(authToken);
            if (cookie != null) {
                persistAuthCookie(cookie);
            }
            return cookie;
        } catch (OperationCanceledException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (AuthenticatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        // TODO consider cookie expiration
        return getPersistedAuthCookie() != null;
    }

    @Override
    public void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener) {
        mOnAuthenticationListener = onAuthenticationListener;
    }

    @Override
    public HttpClient getHttpClient() {
        return mHttpClient;
    }

    public void setPromptActivity(Activity promptActivity) {
        mPromptActivity = promptActivity;
    }

    /**
     * Persists the given App Engine authentication {@link Cookie} to SharedPreferences.
     *
     * @param cookie the {@code Cookie} to persist
     */
    private void persistAuthCookie(Cookie cookie) {
        String keyPrefix = getSharedPrefsKeyPrefix();
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(keyPrefix + KEY_NAME, cookie.getName())
                .putString(keyPrefix + KEY_VAL, cookie.getValue())
                .commit();
    }

    /**
     * Fetches the App Engine authentication {@link Cookie} for the user from SharedPreferences.
     *
     * @return authentication {@code Cookie} or {@code null} if there isn't one persisted
     */
    private Cookie getPersistedAuthCookie() {
        String keyPrefix = getSharedPrefsKeyPrefix();
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        String cookieName = prefs.getString(keyPrefix + KEY_NAME, null);
        String cookieVal = prefs.getString(keyPrefix + KEY_VAL, null);

        if (cookieName == null || cookieVal == null) {
            return null;
        }

        return new BasicClientCookie(cookieName, cookieVal);
    }

    private String getSharedPrefsKeyPrefix() {
        return "gae|" + mAccount.type + "|" + mAccount.name;
    }

    private Cookie exchangeTokenForCookie(String authToken) {
        HttpGet get = new HttpGet(getAuthUrl(authToken));

        // Don't follow redirects
        mHttpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

        try {
            HttpResponse response = mHttpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != 302)
                // Response should be a redirect
                return null;

            for (Cookie cookie : mHttpClient.getCookieStore().getCookies()) {
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
            mHttpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
        }
        return null;
    }

    private String getAuthUrl(String token) {
        return mUrl + AUTH_ENDPOINT + token;
    }

    public class AppEngineAuthTokenCallback implements AccountManagerCallback<Bundle> {

        public void run(AccountManagerFuture result) {
            try {
                Bundle bundle = (Bundle) result.getResult();
                Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (intent != null) {
                    // User input required
                    mContext.startActivity(intent);
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
            new GetCookieTask().execute(bundle.getString(AccountManager.KEY_AUTHTOKEN));
        }

        private class GetCookieTask extends AsyncTask<String, String, Cookie> {

            protected Cookie doInBackground(String... tokens) {
                return exchangeTokenForCookie(tokens[0]);
            }

            protected void onPostExecute(Cookie cookie) {
                if (cookie == null) {
                    if (mOnAuthenticationListener != null) {
                        mOnAuthenticationListener.onAuthFailed();
                    }
                } else {
                    persistAuthCookie(cookie);
                    if (mOnAuthenticationListener != null) {
                        mOnAuthenticationListener.onAuthSuccess(cookie);
                    }
                }
            }

        }

    }

}
