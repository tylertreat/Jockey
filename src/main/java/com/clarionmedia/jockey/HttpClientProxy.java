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

import com.clarionmedia.jockey.authentication.Authenticator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class HttpClientProxy implements InvocationHandler {

    private Authenticator mAuthenticator;

    public HttpClientProxy(Authenticator authenticator) {
        mAuthenticator = authenticator;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!mAuthenticator.isAuthenticated()) {
            mAuthenticator.authenticate();
        }

        return method.invoke(mAuthenticator.getHttpClient(), args);
    }

}
