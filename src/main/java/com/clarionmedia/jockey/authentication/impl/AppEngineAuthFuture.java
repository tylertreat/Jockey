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

import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AppEngineAuthFuture implements Future<Bundle> {

    private AccountManagerFuture<Bundle> mBundleAccountManagerFuture;

    public AppEngineAuthFuture(AccountManagerFuture<Bundle> accountManagerFuture) {
        mBundleAccountManagerFuture = accountManagerFuture;
    }

    @Override
    public boolean cancel(boolean b) {
        return mBundleAccountManagerFuture.cancel(b);
    }

    @Override
    public boolean isCancelled() {
        return mBundleAccountManagerFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return mBundleAccountManagerFuture.isDone();
    }

    @Override
    public Bundle get() throws InterruptedException, ExecutionException {
        try {
            return mBundleAccountManagerFuture.getResult();
        } catch (OperationCanceledException e) {
            throw new InterruptedException(e.getMessage());
        } catch (IOException e) {
            throw new ExecutionException(e);
        } catch (AuthenticatorException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public Bundle get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return mBundleAccountManagerFuture.getResult(l, timeUnit);
        } catch (OperationCanceledException e) {
            throw new InterruptedException(e.getMessage());
        } catch (IOException e) {
            throw new ExecutionException(e);
        } catch (AuthenticatorException e) {
            throw new ExecutionException(e);
        }
    }

}
