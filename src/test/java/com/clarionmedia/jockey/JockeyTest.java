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

package com.clarionmedia.jockey;

import com.clarionmedia.jockey.authentication.AuthenticationProvider;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class JockeyTest {

    @Mock
    private AuthenticationProvider mMockAuthenticationProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        Jockey.resetAuthProviderRegistry();
    }

    @Test
    public void testTo() {
        AuthenticationProvider actual = Jockey.to(mMockAuthenticationProvider);

        assertEquals("AuthenticationProvider returned by to() should equal the provider passed in",
                mMockAuthenticationProvider, actual);
        assertEquals("AuthProviderRegistry should contain the expected AuthenticationProvider", 1,
                Jockey.getAuthProviderRegistry().size());
        assertEquals("AuthProviderRegistry should contain the expected AuthenticationProvider",
                mMockAuthenticationProvider, Jockey.getAuthProviderRegistry().get(0));
    }

    @Test
    public void testResetAuthProviderRegistry() {
        Jockey.to(mMockAuthenticationProvider);
        Jockey.to(mMockAuthenticationProvider);
        Jockey.to(mMockAuthenticationProvider);

        Jockey.resetAuthProviderRegistry();

        assertEquals("AuthProviderRegistry should be empty", 0, Jockey.getAuthProviderRegistry().size());
    }

}
