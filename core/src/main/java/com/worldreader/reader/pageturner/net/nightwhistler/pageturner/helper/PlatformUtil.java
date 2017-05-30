/*
 * Copyright (C) 2012 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.helper;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PlatformUtil {

  private static final Executor SINGLE_THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

  @SafeVarargs @TargetApi(Build.VERSION_CODES.HONEYCOMB) public static <A, B, C> void executeTask(AsyncTask<A, B, C> task, A... params) {
    task.executeOnExecutor(SINGLE_THREAD_EXECUTOR, params);
  }

}
