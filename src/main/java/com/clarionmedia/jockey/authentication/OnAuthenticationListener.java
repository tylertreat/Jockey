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

import org.apache.http.cookie.Cookie;

public interface OnAuthenticationListener {

    /**
     * Hook to be called when authentication with the server is successful.
     *
     * @param authenticationCookie the authentication {@link Cookie} returned by the server or {@code null} if there
     *                             isn't one
     */
    void onAuthenticationSuccess(Cookie authenticationCookie);

    /**
     * Hook to be called when authentication with the server fails.
     */
    void onAuthenticationFailed();

}
