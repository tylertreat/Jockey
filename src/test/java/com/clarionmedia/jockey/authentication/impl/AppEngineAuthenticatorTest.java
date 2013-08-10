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
import android.app.Activity;
import android.content.Context;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.apache.http.client.HttpClient;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class AppEngineAuthenticatorTest {

    @Mock
    private Context mMockContext;

    @Mock
    private AccountManager mMockAccountManager;

    @Mock
    private Account mMockAccount;

    @Mock
    private HttpClient mMockHttpClient;

    @Mock
    private Activity mMockActivity;

    @Mock
    private AppEngineAuthTokenCallback mMockAuthTokenCallback;

    @Mock
    private HttpParams mMockHttpParams;

    private String mUrl = "foo";

    private AppEngineAuthenticator mAppEngineAuthenticator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mMockContext.getApplicationContext()).thenReturn(mMockContext);
        when(mMockHttpClient.getParams()).thenReturn(mMockHttpParams);
        mAppEngineAuthenticator = new AppEngineAuthenticator(mMockContext, mMockAccountManager, mMockAccount,
                mMockHttpClient, mUrl, mMockActivity);
        mAppEngineAuthenticator.setAuthCallback(mMockAuthTokenCallback);
    }

    @Test
    public void testAuthenticateWithPromptActivity() {
        mAppEngineAuthenticator.authenticate();

        verify(mMockAccountManager)
                .getAuthToken(mMockAccount, "ah", null, mMockActivity, mMockAuthTokenCallback, null);
    }

    @Test
    public void testAuthenticateNoPromptActivity() {
        mAppEngineAuthenticator.setPromptActivity(null);

        mAppEngineAuthenticator.authenticate();

        verify(mMockAccountManager).getAuthToken(mMockAccount, "ah", false, mMockAuthTokenCallback, null);
    }

    @Test
    public void testGetAuthUrl() {
        String token = "bar";
        String expected = mUrl + "/_ah/login?continue=http://localhost/&auth=" + token;

        String actual = mAppEngineAuthenticator.getAuthUrl(token);

        assertEquals("Auth URL should equal expected value", expected, actual);
    }

}
