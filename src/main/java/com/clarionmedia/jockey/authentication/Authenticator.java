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

package com.clarionmedia.jockey.authentication;


import android.os.Bundle;
import org.apache.http.client.HttpClient;
import org.apache.http.cookie.Cookie;

import java.util.concurrent.Future;

/**
 * {@code Authenticator} is responsible for performing authentication with the server. This is essentially a strategy
 * pattern which facilitates authentication with a wide range of HTTP-compliant services.
 */
public interface Authenticator {

    /**
     * Asynchronously authenticates with the server.
     *
     * @return {@link Future} which contains a handle to the authentication results
     */
    Future<Bundle> authenticateAsync();

    /**
     * Authenticates with the server in a blocking fashion.
     *
     * @return the authentication {@link Cookie} returned by the server if there is one
     */
    Cookie authenticate();

    /**
     * Indicates if this {@code Authenticator} is currently authenticated with the server. When this returns {@code
     * true}, this {@code Authenticator} should be able to successfully make requests to the server. If this returns
     * {@code false}, subsequent requests may first make calls to {@link Authenticator#authenticate()} to perform the
     * handshake.
     *
     * @return {@code true} if authenticated, {@code false} if not
     */
    boolean isAuthenticated();

    /**
     * Sets the {@link OnAuthenticationListener} for this {@code Authenticator}. The {@code OnAuthenticationListener}
     * hooks will be invoked when calls to {@link Authenticator#authenticate()} and {@link
     * Authenticator#authenticateAsync()} complete.
     *
     * @param onAuthenticationListener the {@code OnAuthenticationListener} to use
     */
    void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener);

    /**
     * Returns the proxied {@link HttpClient} that is used to perform authenticated HTTP requests.
     *
     * @return {@link HttpClient}
     */
    HttpClient getHttpClient();

}
