@file:Suppress("FunctionName", "unused")

package com.mobilejazz.kotlin.core.threading.extensions

import com.google.common.base.Function
import com.google.common.util.concurrent.*
import com.mobilejazz.kotlin.core.threading.AppUiExecutor
import com.mobilejazz.kotlin.core.threading.DirectExecutor
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

typealias Future<K> = ListenableFuture<K>

fun <T> T.toFuture(): Future<T> = this.toListenableFuture()

fun <A> Throwable.toFuture(): Future<A> = this.toListenableFuture()

fun emptyFuture(): Future<Unit> = emptyListenableFuture()

// Creation
inline fun <A> Future(
    executor: ExecutorService = DirectExecutor,
    crossinline block: () -> A
): Future<A> {
  val service: ListeningExecutorService = MoreExecutors.listeningDecorator(executor)
  return service.submit(Callable<A> { block() })
}

inline fun <A> ImmediateFuture(crossinline block: () -> A): Future<A> = Future(DirectExecutor, block)

fun <A> A.toListenableFuture(): Future<A> = Futures.immediateFuture(this)

fun emptyListenableFuture(): Future<Unit> = Futures.immediateFuture(null)

fun <A> Throwable.toListenableFuture(): Future<A> = Futures.immediateFailedFuture(this)

// Monadic Operations
inline fun <A, B> Future<A>.map(
    executor: Executor = DirectExecutor,
    crossinline f: (A) -> B
): Future<B> =
    Futures.transform(this, com.google.common.base.Function { f(it!!) }, executor)

inline fun <A, B> Future<A>.flatMap(
    executor: Executor = DirectExecutor,
    crossinline f: (A) -> Future<B>
): Future<B> =
    Futures.transformAsync(this, AsyncFunction { f(it!!) }, executor)

fun <A> Future<Future<A>>.flatten(): Future<A> = flatMap { it }

inline fun <A> Future<A>.filter(
    executor: Executor = DirectExecutor,
    crossinline predicate: (A) -> Boolean
): Future<A> =
    map(executor) {
      if (predicate(it)) it else throw NoSuchElementException("Future.filter predicate is not satisfied")
    }

fun <A, B> Future<A>.zip(
    other: Future<B>,
    executor: Executor = DirectExecutor
): Future<Pair<A, B>> =
    zip(other, executor) { a, b -> a to b }

inline fun <A, B, C> Future<A>.zip(
    other: Future<B>,
    executor: Executor = DirectExecutor,
    crossinline f: (A, B) -> C
): Future<C> =
    flatMap(executor) { a -> other.map(executor) { b -> f(a, b) } }

// Error handling / Recovery
inline fun <A> Future<A>.recover(crossinline f: (Throwable) -> A): Future<A> =
    Futures.catching(this, Throwable::class.java, Function { f(it!!.cause ?: it) }, DirectExecutor)

inline fun <A> Future<A>.recoverWith(
    executor: Executor = DirectExecutor,
    crossinline f: (Throwable) -> Future<A>
): Future<A> {
  return Futures.catchingAsync(this, Throwable::class.java, AsyncFunction { f(it!!.finalCause()) }, executor)
}

fun Throwable.finalCause(): Throwable {
  return if (cause != null) {
    cause!!.finalCause()
  } else {
    this
  }
}

inline fun <A, reified E : Throwable> Future<A>.mapError(crossinline f: (E) -> Throwable): Future<A> {
  return Futures.catching(this, E::class.java, Function { throw f(it!!) }, DirectExecutor)
}

inline fun <A> Future<A>.fallbackTo(
    executor: Executor = DirectExecutor,
    crossinline f: () -> Future<A>
): Future<A> =
    recoverWith(executor) { f() }

/**
 * This function blocks a Exception thrown by a Future.
 *
 * This function should be used when neither result nor the error is needed.
 * An example of this could be a data storage operation that is not required to success and the error is not important for the caller.
 *
 * @return
 *
 */
inline fun <reified E : Throwable> Future<*>.blockError(): Future<Unit> {
  return Futures.catching(this, E::class.java, Function { null }, DirectExecutor).map { kotlin.Unit }
}

// Callbacks
inline fun <A> Future<A>.onFailure(
    executor: Executor = DirectExecutor,
    crossinline f: (Throwable) -> Unit
): Future<A> {
  Futures.addCallback(this, object : FutureCallback<A> {
    override fun onSuccess(result: A?) {
    }

    override fun onFailure(t: Throwable) {
      f(t)
    }
  }, executor)
  return this
}

inline fun <A> Future<A>.onSuccess(
    executor: Executor = DirectExecutor,
    crossinline f: (A) -> Unit
): Future<A> {
  Futures.addCallback(this, object : FutureCallback<A> {
    override fun onSuccess(result: A?) {
      f(result!!)
    }

    override fun onFailure(t: Throwable) {
    }
  }, executor)
  return this
}

inline fun <A> Future<A>.onComplete(
    executor: Executor = DirectExecutor,
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A) -> Unit
): Future<A> {
  Futures.addCallback(this, object : FutureCallback<A> {
    override fun onSuccess(result: A?) {
      onSuccess(result!!)
    }

    override fun onFailure(t: Throwable) {
      onFailure(t)
    }
  }, executor)
  return this
}

inline fun <A> Future<A>.onCompleteNullable(
    executor: Executor = DirectExecutor,
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A?) ->
    Unit
): Future<A> {
  Futures.addCallback(this, object : FutureCallback<A> {
    override fun onSuccess(result: A?) {
      onSuccess(result)
    }

    override fun onFailure(t: Throwable) {
      onFailure(t)
    }
  }, executor)
  return this
}

inline fun <A> Future<A>.onCompleteUi(
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A) -> Unit
): Future<A> =
    onComplete(executor = AppUiExecutor, onFailure = onFailure, onSuccess = onSuccess)

inline fun <A> Future<A>.onCompleteDirect(
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A) -> Unit
): Future<A> =
    onComplete(executor = DirectExecutor, onFailure = onFailure, onSuccess = onSuccess)

inline fun <A> Future<A>.onCompleteNullableUi(
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (A?) -> Unit
): Future<A> =
    onCompleteNullable(executor = AppUiExecutor, onFailure = onFailure, onSuccess = onSuccess)

object FutureObject {

  fun <A> allAsList(
      futures: Iterable<Future<A>>,
      executor: Executor = DirectExecutor
  ): Future<List<A>> =
      futures.fold(mutableListOf<A>().toListenableFuture()) { fr, fa ->
        fr.zip(fa, executor) { r, a -> r.add(a); r }
      }.map(executor) { it.toList() }

  fun <A> successfulList(
      futures: Iterable<Future<A>>,
      executor: Executor = DirectExecutor
  ): Future<List<A>> =
      futures.fold(mutableListOf<A>().toListenableFuture()) { fr, fa ->
        fr.flatMap(executor) { r ->
          fa
              .map(executor) {
                if (it != null) {
                  r.add(it)
                }
                r
              }
        }
      }.map(executor) { it.toList() }

  fun <A, R> fold(
      futures: Iterable<Future<A>>,
      initial: R,
      executor: Executor = DirectExecutor,
      op: (R, A) -> R
  ): Future<R> =
      fold(futures.iterator(), initial, executor, op)

  fun <A, R> fold(
      iterator: Iterator<Future<A>>,
      initial: R,
      executor: Executor = DirectExecutor,
      op: (R, A) -> R
  ): Future<R> =
      if (!iterator.hasNext()) initial.toListenableFuture()
      else iterator.next().flatMap(executor) { fold(iterator, op(initial, it), executor, op) }

  fun <A> reduce(
      iterable: Iterable<Future<A>>,
      executor: Executor = DirectExecutor,
      op: (A, A) -> A
  ): Future<A> {
    val iterator = iterable.iterator()
    return if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    else iterator.next().flatMap { fold(iterator, it, executor, op) }
  }

  fun <A, B> transform(
      iterable: Iterable<Future<A>>,
      executor: Executor = DirectExecutor,
      f: (A) -> B
  ): Future<List<B>> =
      iterable.fold(mutableListOf<B>().toListenableFuture()) { fr, fa ->
        fr.zip(fa, executor) { r, a -> r.add(f(a)); r }
      }.map(executor) { it.toList() }
}