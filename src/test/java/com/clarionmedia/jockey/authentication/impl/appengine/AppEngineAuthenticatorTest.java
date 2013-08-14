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
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.clarionmedia.jockey.authentication.OnAuthenticationListener;
import com.clarionmedia.jockey.authentication.impl.appengine.AppEngineAuthenticator;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.HttpParams;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
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
    private AbstractHttpClient mMockHttpClient;

    @Mock
    private Activity mMockActivity;

    @Mock
    private AppEngineAuthenticator.AppEngineAuthTokenCallback mMockAuthTokenCallback;

    @Mock
    private HttpParams mMockHttpParams;

    @Mock
    private OnAuthenticationListener mMockOnAuthenticationListener;

    private String mUrl = "foo";

    private AppEngineAuthenticator mAppEngineAuthenticator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(mMockContext.getApplicationContext()).thenReturn(mMockContext);
        when(mMockContext.getSystemService(Context.ACCOUNT_SERVICE)).thenReturn(mMockAccountManager);
        when(mMockHttpClient.getParams()).thenReturn(mMockHttpParams);
        mAppEngineAuthenticator = new AppEngineAuthenticator(mMockContext, mMockAccount,
                mMockHttpClient, mUrl, mMockActivity);
    }

    @Test
    public void testAuthenticateAsyncWithPromptActivity() {
        mAppEngineAuthenticator.authenticateAsync();

        verify(mMockAccountManager).getAuthToken(any(Account.class), any(String.class), isNull(Bundle.class),
                any(Activity.class), any(AccountManagerCallback.class), isNull(Handler.class));
    }

    @Test
    public void testAuthenticateAsyncNoPromptActivity() {
        mAppEngineAuthenticator.setPromptActivity(null);

        mAppEngineAuthenticator.authenticateAsync();

        verify(mMockAccountManager).getAuthToken(any(Account.class), any(String.class), any(Boolean.class),
                any(AccountManagerCallback.class), isNull(Handler.class));
    }

}
