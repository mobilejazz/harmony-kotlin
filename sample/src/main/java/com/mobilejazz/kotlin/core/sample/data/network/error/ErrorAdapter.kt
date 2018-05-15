package org.worldreader.classroom.dataprovider.network.error

import retrofit2.HttpException

interface ErrorAdapter<in T, out K : Throwable> {
  fun of(error: T): K
}

class RetrofitErrorAdapter : ErrorAdapter<Throwable, NetworkException> {
  override fun of(error: Throwable): NetworkException = when (error) {
    is HttpException -> NetworkException(NetworkException.Type.HTTP, error)
    else -> NetworkException(NetworkException.Type.UNEXPECTED, error)
  }
}


