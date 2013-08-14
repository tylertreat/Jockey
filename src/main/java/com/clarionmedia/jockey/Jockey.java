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

import java.util.HashMap;
import java.util.Map;

public final class Jockey {

    private static Map<String, AuthenticationProvider> sAuthProviderRegistry = new HashMap<String,
            AuthenticationProvider>();

    public static synchronized <T extends AuthenticationProvider> T to(T provider) {
        sAuthProviderRegistry.put(provider.getUrl(), provider);
        return provider;
    }

    public static synchronized AuthenticationProvider getAuthProvider(String url) {
        return sAuthProviderRegistry.get(url);
    }

    public static synchronized boolean hasAuthProvider(String url) {
        return sAuthProviderRegistry.containsKey(url);
    }

    public static synchronized int getAuthProviderRegistrySize() {
        return sAuthProviderRegistry.size();
    }

    public static synchronized void resetAuthProviderRegistry() {
        sAuthProviderRegistry.clear();
    }

}
