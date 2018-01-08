package com.worldreader.core.domain.interactors.referrer;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.worldreader.core.datasource.ReferrerDataSource;
import com.worldreader.core.domain.model.Referrer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

@Singleton public class GetReferrerInteractor {

  private final ListeningExecutorService executorService;
  private final ReferrerDataSource referrerRepository;

  @Inject public GetReferrerInteractor(ListeningExecutorService executorService, ReferrerDataSource referrerRepository) {
    this.executorService = executorService;
    this.referrerRepository = referrerRepository;
  }

  /**
   * This method will always return a {@link Referrer} object but if no referrer data is available its fields will be null
   * @return
   */
  public ListenableFuture<Referrer> execute() {
    return this.execute(this.executorService);
  }

  /**
   * This method will always return a {@link Referrer} object but if no referrer data is available its fields will be null
   * @param executorService
   * @return
   */
  public ListenableFuture<Referrer> execute(ListeningExecutorService executorService) {
    return executorService.submit(new Callable<Referrer>() {
      @Override public Referrer call() throws Exception {
        return referrerRepository.get();
      }
    });

  }

}
