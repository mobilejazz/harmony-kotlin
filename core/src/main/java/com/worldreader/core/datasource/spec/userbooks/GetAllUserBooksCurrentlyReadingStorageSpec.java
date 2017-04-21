package com.worldreader.core.datasource.spec.userbooks;

import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UserBooksTable;

public class GetAllUserBooksCurrentlyReadingStorageSpec extends UserBookStorageSpecification {

  private Integer limit;
  private boolean strictFinished = true;
  private boolean finishedIncluded;
  private boolean orderByLastOpened;

  private GetAllUserBooksCurrentlyReadingStorageSpec(final Builder builder) {
    setLimit(builder.limit);
    this.finishedIncluded = builder.finishedIncluded;
    this.strictFinished = builder.strictFinished;
    this.orderByLastOpened = builder.orderByLastOpened;
  }

  public GetAllUserBooksCurrentlyReadingStorageSpec() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public void setLimit(final Integer limit) {
    this.limit = limit;
  }

  @Override public Query toQuery() {
    final Query.CompleteBuilder builder = Query.builder()
        .table(UserBooksTable.TABLE)
        .where(UserBooksTable.COLUMN_MARK_IN_MY_BOOKS + " = ? " + (strictFinished ? " AND " : " OR ") + UserBooksTable.COLUMN_FINISHED + " = ?")
        .whereArgs(1, finishedIncluded ? 1 : 0)
        .orderBy("datetime(" + (orderByLastOpened ? UserBooksTable.COLUMN_OPENED_AT : UserBooksTable.COLUMN_UPDATED_AT) + ") DESC");

    if (limit != null) {
      builder.limit(limit);
    }

    return builder.build();
  }

  @Override public DeleteQuery toDeleteQuery() {
    return null;
  }

  public static final class Builder {

    private Integer limit;
    private boolean finishedIncluded;
    private boolean strictFinished;
    private boolean orderByLastOpened;

    private Builder() {
    }

    public Builder withLimit(final Integer limit) {
      this.limit = limit;
      return this;
    }

    public Builder withFinishedIncluded(final boolean finishedIncluded) {
      this.finishedIncluded = finishedIncluded;
      return this;
    }

    public Builder withStrictFinished(final boolean strictFinished) {
      this.strictFinished = strictFinished;
      return this;
    }

    public Builder withOrderByLastOpened(final boolean orderByLastOpened) {
      this.orderByLastOpened = orderByLastOpened;
      return this;
    }

    public GetAllUserBooksCurrentlyReadingStorageSpec build() {
      return new GetAllUserBooksCurrentlyReadingStorageSpec(this);
    }
  }

}