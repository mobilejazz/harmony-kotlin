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

// Creation
inline fun <A> FutureGuava(executor: ExecutorService = DirectExecutor, crossinline block: () -> A): ListenableFuture<A> {
  val service: ListeningExecutorService = MoreExecutors.listeningDecorator(executor)
  return service.submit(Callable<A> { block() })
}

inline fun <A> ImmediateFuture(crossinline block: () -> A): ListenableFuture<A> = FutureGuava(DirectExecutor, block)

fun <A> A.toListenableFuture(): ListenableFuture<A> = Futures.immediateFuture(this)


fun emptyListenableFuture(): ListenableFuture<Void> = Futures.immediateFuture(null)

fun <A> Throwable.toListenableFuture(): ListenableFuture<A> = Futures.immediateFailedFuture(this)

// Monadic Operations
inline fun <A, B> ListenableFuture<A>.map(executor: Executor = DirectExecutor, crossinline f: (A) -> B): ListenableFuture<B> =
    Futures.transform(this, com.google.common.base.Function { f(it!!) }, executor)

inline fun <A, B> ListenableFuture<A>.flatMap(executor: Executor = DirectExecutor, crossinline f: (A) -> ListenableFuture<B>): ListenableFuture<B> =
    Futures.transformAsync(this, AsyncFunction { f(it!!) }, executor)

fun <A> ListenableFuture<ListenableFuture<A>>.flatten(): ListenableFuture<A> = flatMap { it }

inline fun <A> ListenableFuture<A>.filter(executor: Executor = DirectExecutor, crossinline predicate: (A) -> Boolean): ListenableFuture<A> =
    map(executor) {
      if (predicate(it)) it else throw NoSuchElementException("ListenableFuture.filter predicate is not satisfied")
    }

fun <A, B> ListenableFuture<A>.zip(other: ListenableFuture<B>, executor: Executor = DirectExecutor): ListenableFuture<Pair<A, B>> =
    zip(other, executor) { a, b -> a to b }

inline fun <A, B, C> ListenableFuture<A>.zip(other: ListenableFuture<B>, executor: Executor = DirectExecutor, crossinline f: (A, B) -> C): ListenableFuture<C> =
    flatMap(executor) { a -> other.map(executor) { b -> f(a, b) } }

// Error handling / Recovery
inline fun <A> ListenableFuture<A>.recover(crossinline f: (Throwable) -> A): ListenableFuture<A> =
    Futures.catching(this, Throwable::class.java, Function { f(it!!.cause ?: it) }, DirectExecutor)

inline fun <A> ListenableFuture<A>.recoverWith(executor: Executor = DirectExecutor, crossinline f: (Throwable) -> ListenableFuture<A>): ListenableFuture<A> {
  return Futures.catchingAsync(this, Throwable::class.java, AsyncFunction { it -> f(it!!.cause ?: it) }, executor)
}

inline fun <A, reified E : Throwable> ListenableFuture<A>.mapError(crossinline f: (E) -> Throwable): ListenableFuture<A> {
  return Futures.catching(this, E::class.java, Function { throw f(it!!) }, DirectExecutor)
}

inline fun <A> ListenableFuture<A>.fallbackTo(executor: Executor = DirectExecutor, crossinline f: () -> ListenableFuture<A>): ListenableFuture<A> =
    recoverWith(executor, { f() })

// Callbacks
inline fun <A> ListenableFuture<A>.onFailure(executor: Executor = DirectExecutor, crossinline f: (Throwable) -> Unit): ListenableFuture<A> {
  Futures.addCallback(this, object : FutureCallback<A> {
    override fun onSuccess(result: A?) {
    }

    override fun onFailure(t: Throwable) {
      f(t)
    }
  }, executor)
  return this
}

inline fun <A> ListenableFuture<A>.onSuccess(executor: Executor = DirectExecutor, crossinline f: (A) -> Unit): ListenableFuture<A> {
  Futures.addCallback(this, object : FutureCallback<A> {
    override fun onSuccess(result: A?) {
      f(result!!)
    }

    override fun onFailure(t: Throwable) {
    }
  }, executor)
  return this
}

inline fun <A> ListenableFuture<A>.onComplete(executor: Executor = DirectExecutor, crossinline onFailure: (Throwable) -> Unit, crossinline onSuccess: (A) -> Unit): ListenableFuture<A> {
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

inline fun <A> ListenableFuture<A>.onCompleteUi(crossinline onFailure: (Throwable) -> Unit, crossinline onSuccess: (A) -> Unit): ListenableFuture<A> =
    onComplete(executor = AppUiExecutor, onFailure = onFailure, onSuccess = onSuccess)

inline fun <A> ListenableFuture<A>.onCompleteDirect(crossinline onFailure: (Throwable) -> Unit, crossinline onSuccess: (A) -> Unit): ListenableFuture<A> =
    onComplete(executor = DirectExecutor, onFailure = onFailure, onSuccess = onSuccess)

object Future {

  fun <A> allAsList(futures: Iterable<ListenableFuture<A>>, executor: Executor = DirectExecutor): ListenableFuture<List<A>> =
      futures.fold(mutableListOf<A>().toListenableFuture()) { fr, fa ->
        fr.zip(fa, executor) { r, a -> r.add(a); r }
      }.map(executor) { it.toList() }

  fun <A> successfulList(futures: Iterable<ListenableFuture<A>>, executor: Executor = DirectExecutor): ListenableFuture<List<A>> =
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

  fun <A, R> fold(futures: Iterable<ListenableFuture<A>>, initial: R, executor: Executor = DirectExecutor, op: (R, A) -> R): ListenableFuture<R> =
      fold(futures.iterator(), initial, executor, op)

  fun <A, R> fold(iterator: Iterator<ListenableFuture<A>>, initial: R, executor: Executor = DirectExecutor, op: (R, A) -> R): ListenableFuture<R> =
      if (!iterator.hasNext()) initial.toListenableFuture()
      else iterator.next().flatMap(executor) { fold(iterator, op(initial, it), executor, op) }

  fun <A> reduce(iterable: Iterable<ListenableFuture<A>>, executor: Executor = DirectExecutor, op: (A, A) -> A): ListenableFuture<A> {
    val iterator = iterable.iterator()
    return if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    else iterator.next().flatMap { fold(iterator, it, executor, op) }
  }

  fun <A, B> transform(iterable: Iterable<ListenableFuture<A>>, executor: Executor = DirectExecutor, f: (A) -> B): ListenableFuture<List<B>> =
      iterable.fold(mutableListOf<B>().toListenableFuture()) { fr, fa ->
        fr.zip(fa, executor) { r, a -> r.add(f(a)); r }
      }.map(executor) { it.toList() }
}