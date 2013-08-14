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

import java.util.ArrayList;
import java.util.List;

public class Jockey {

    private static List<AuthenticationProvider> sAuthProviderRegistry = new ArrayList<AuthenticationProvider>();

    public static synchronized <T extends AuthenticationProvider> T to(T provider) {
        sAuthProviderRegistry.add(provider);
        return provider;
    }

    public static synchronized List<AuthenticationProvider> getAuthProviderRegistry() {
        return new ArrayList<AuthenticationProvider>(sAuthProviderRegistry);
    }

    public static synchronized void resetAuthProviderRegistry() {
        sAuthProviderRegistry.clear();
    }

}
