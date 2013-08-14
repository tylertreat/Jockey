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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        String url = "foo";
        when(mMockAuthenticationProvider.getUrl()).thenReturn(url);

        AuthenticationProvider actual = Jockey.to(mMockAuthenticationProvider);

        assertEquals("AuthenticationProvider returned by to() should equal the provider passed in",
                mMockAuthenticationProvider, actual);
        assertEquals("AuthProviderRegistry should contain the expected AuthenticationProvider", 1,
                Jockey.getAuthProviderRegistrySize());
        assertEquals("AuthProviderRegistry should contain the expected AuthenticationProvider",
                mMockAuthenticationProvider, Jockey.getAuthProvider(url));
    }

    @Test
    public void testGetAuthProvider() {
        String url = "foo";
        when(mMockAuthenticationProvider.getUrl()).thenReturn(url);
        Jockey.to(mMockAuthenticationProvider);

        AuthenticationProvider actual = Jockey.getAuthProvider(url);

        assertEquals("AuthenticationProvider returned by getAuthProvider() should equal the expected provider",
                mMockAuthenticationProvider, actual);
    }

    @Test
    public void testHasAuthProvider_contains() {
        String url = "foo";
        when(mMockAuthenticationProvider.getUrl()).thenReturn(url);
        Jockey.to(mMockAuthenticationProvider);

        boolean actual = Jockey.hasAuthProvider(url);

        assertTrue("hasAuthProvider() should have returned true", actual);
    }

    @Test
    public void testHasAuthProvider_doesNotContain() {
        String url = "foo";

        boolean actual = Jockey.hasAuthProvider(url);

        assertFalse("hasAuthProvider() should have returned false", actual);
    }

    @Test
    public void testGetAuthProviderRegistrySize_empty() {
        int actual = Jockey.getAuthProviderRegistrySize();

        assertEquals("getAuthProviderRegistrySize should have returned 0", 0, actual);
    }

    @Test
    public void testGetAuthProviderRegistrySize() {
        when(mMockAuthenticationProvider.getUrl()).thenReturn("foo");
        Jockey.to(mMockAuthenticationProvider);
        AuthenticationProvider mockAuthProvider = mock(AuthenticationProvider.class);
        when(mockAuthProvider.getUrl()).thenReturn("bar");
        Jockey.to(mockAuthProvider);

        int actual = Jockey.getAuthProviderRegistrySize();

        assertEquals("getAuthProviderRegistrySize should have returned 2", 2, actual);
    }

    @Test
    public void testResetAuthProviderRegistry() {
        Jockey.to(mMockAuthenticationProvider);
        Jockey.to(mMockAuthenticationProvider);
        Jockey.to(mMockAuthenticationProvider);

        Jockey.resetAuthProviderRegistry();

        assertEquals("AuthProviderRegistry should be empty", 0, Jockey.getAuthProviderRegistrySize());
    }

}
