package com.worldreader.core.domain.deprecated.executor;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.deprecated.Interactor;

import javax.inject.Inject;
import java.io.*;
import java.util.concurrent.*;

public class SingleThreadExecutor implements InteractorExecutor {
  public static final String TAG = ThreadExecutor.class.getSimpleName();

  private static final int CORE_POOL_SIZE = 1;
  private static final int MAX_POOL_SIZE = 1;
  private static final int KEEP_ALIVE_TIME = 0;
  private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
  private static final BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue<>();

  private final ThreadPoolExecutor threadPoolExecutor;

  @Inject public SingleThreadExecutor(final Logger logger) {
    threadPoolExecutor =
        new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT,
            WORK_QUEUE, new InteractorThreadFactory(logger)) {
          @Override protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t == null && r instanceof Future<?>) {
              try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                  future.get();
                }
              } catch (CancellationException | ExecutionException ce) {
                t = ce.getCause();
              } catch (InterruptedException ie) {
                t = ie.getCause();
                Thread.currentThread().interrupt();
              } finally {
                if (t != null) {
                  StringWriter writer = new StringWriter();
                  PrintWriter printWriter = new PrintWriter(writer);
                  t.printStackTrace(printWriter);
                  printWriter.flush();
                  logger.e(TAG, writer.toString());
                }
              }
            } else if (t != null) {
              logger.e(TAG, t.getMessage());
            }
          }
        };
  }

  @Override public void run(final Interactor interactor) {
    if (interactor == null) {
      throw new IllegalArgumentException("Interactor must not be null");
    }

    threadPoolExecutor.submit(new Runnable() {
      @Override public void run() {
        interactor.run();
      }
    });
  }
}
