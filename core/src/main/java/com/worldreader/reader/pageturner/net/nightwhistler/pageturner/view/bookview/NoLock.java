package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.support.annotation.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class NoLock implements Lock {

  @Override public void lock() {

  }

  @Override public void lockInterruptibly() throws InterruptedException {

  }

  @Override public boolean tryLock() {
    return false;
  }

  @Override public boolean tryLock(final long time, @NonNull final TimeUnit unit)
      throws InterruptedException {
    return false;
  }

  @Override public void unlock() {

  }

  @NonNull @Override public Condition newCondition() {
    return null;
  }
}
