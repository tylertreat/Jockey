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

import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.clarionmedia.jockey.authentication.Authenticator;
import com.clarionmedia.jockey.authentication.CookieStoreProvider;
import com.clarionmedia.jockey.authentication.OnAuthenticationListener;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AppEngineAuthTokenCallbackTest {

    @Mock
    private Context mMockContext;

    @Mock
    private Authenticator mMockAuthenticator;

    @Mock
    private AccountManagerFuture<Bundle> mMockResult;

    @Mock
    private Intent mMockIntent;

    @Mock
    private HttpClient mMockHttpClient;

    @Mock
    private HttpResponse mMockHttpResponse;

    @Mock
    private CookieStoreProvider mMockCookieStoreProvider;

    @Mock
    private CookieStore mMockCookieStore;

    @Mock
    private OnAuthenticationListener mMockOnAuthenticationListener;

    @Mock
    private HttpParams mMockHttpParams;

    @Mock
    private StatusLine mMockStatus;

    private Bundle mBundle;

    private AppEngineAuthTokenCallback mAuthTokenCallback;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mMockContext.getApplicationContext()).thenReturn(mMockContext);
        when(mMockCookieStoreProvider.getNewCookieStore()).thenReturn(mMockCookieStore);
        List<OnAuthenticationListener> listeners = new ArrayList<OnAuthenticationListener>();
        listeners.add(mMockOnAuthenticationListener);
        when(mMockAuthenticator.getOnAuthenticationListeners()).thenReturn(listeners);
        when(mMockHttpClient.getParams()).thenReturn(mMockHttpParams);
        when(mMockHttpResponse.getStatusLine()).thenReturn(mMockStatus);
        mAuthTokenCallback = new AppEngineAuthTokenCallback(mMockContext, mMockCookieStoreProvider,
                mMockAuthenticator);
        mBundle = new Bundle();
    }

    @Test
    public void testRun_userInputRequired() throws AuthenticatorException, OperationCanceledException, IOException {
        mBundle.putParcelable(AccountManager.KEY_INTENT, mMockIntent);
        when(mMockResult.getResult()).thenReturn(mBundle);

        mAuthTokenCallback.run(mMockResult);

        verify(mMockContext).startActivity(mMockIntent);
    }

    @Test
    public void testRun_noUserInputRequired_success() throws AuthenticatorException, OperationCanceledException,
            IOException {
        String authToken = "foo";
        String authUrl = "bar";
        mBundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        when(mMockResult.getResult()).thenReturn(mBundle);
        when(mMockAuthenticator.getHttpClient()).thenReturn(mMockHttpClient);
        when(mMockHttpClient.execute(any(HttpGet.class), any(HttpContext.class))).thenReturn(mMockHttpResponse);
        List<Cookie> cookies = new ArrayList<Cookie>();
        cookies.add(mAppEngineAuthCookie);
        when(mMockCookieStore.getCookies()).thenReturn(cookies);
        when(mMockAuthenticator.getAuthUrl(authToken)).thenReturn(authUrl);
        when(mMockStatus.getStatusCode()).thenReturn(302);

        mAuthTokenCallback.run(mMockResult);

        verify(mMockAuthenticator).getAuthUrl(authToken);
        verify(mMockOnAuthenticationListener).onAuthenticationSuccess(mAppEngineAuthCookie);
    }

    @Test
    public void testRun_noUserInputRequired_failNoCookie() throws AuthenticatorException, OperationCanceledException,
            IOException {
        String authToken = "foo";
        String authUrl = "bar";
        mBundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        when(mMockResult.getResult()).thenReturn(mBundle);
        when(mMockAuthenticator.getHttpClient()).thenReturn(mMockHttpClient);
        when(mMockHttpClient.execute(any(HttpGet.class), any(HttpContext.class))).thenReturn(mMockHttpResponse);
        List<Cookie> cookies = new ArrayList<Cookie>();
        when(mMockCookieStore.getCookies()).thenReturn(cookies);
        when(mMockAuthenticator.getAuthUrl(authToken)).thenReturn(authUrl);
        when(mMockStatus.getStatusCode()).thenReturn(302);

        mAuthTokenCallback.run(mMockResult);

        verify(mMockAuthenticator).getAuthUrl(authToken);
        verify(mMockOnAuthenticationListener).onAuthenticationFailed();
    }

    @Test
    public void testRun_noUserInputRequired_failRequestNoRedirect() throws AuthenticatorException,
            OperationCanceledException, IOException {
        String authToken = "foo";
        String authUrl = "bar";
        mBundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
        when(mMockResult.getResult()).thenReturn(mBundle);
        when(mMockAuthenticator.getHttpClient()).thenReturn(mMockHttpClient);
        when(mMockHttpClient.execute(any(HttpGet.class), any(HttpContext.class))).thenReturn(mMockHttpResponse);
        List<Cookie> cookies = new ArrayList<Cookie>();
        when(mMockCookieStore.getCookies()).thenReturn(cookies);
        when(mMockAuthenticator.getAuthUrl(authToken)).thenReturn(authUrl);
        when(mMockStatus.getStatusCode()).thenReturn(404);

        mAuthTokenCallback.run(mMockResult);

        verify(mMockAuthenticator).getAuthUrl(authToken);
        verify(mMockOnAuthenticationListener).onAuthenticationFailed();
    }

    private Cookie mAppEngineAuthCookie = new Cookie() {
        @Override
        public String getName() {
            return "ACSID";
        }

        @Override
        public String getValue() {
            return "value";
        }

        @Override
        public String getComment() {
            return null;
        }

        @Override
        public String getCommentURL() {
            return null;
        }

        @Override
        public Date getExpiryDate() {
            return null;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public String getDomain() {
            return null;
        }

        @Override
        public String getPath() {
            return null;
        }

        @Override
        public int[] getPorts() {
            return new int[0];
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public boolean isExpired(Date date) {
            return false;
        }
    };

}
