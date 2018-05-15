package org.worldreader.classroom.dataprovider.network.error

class NetworkException(val type: Type, val throwable: Throwable?) : RuntimeException(throwable) {

  enum class Type {
    HTTP, NETWORK, CONVERSION, UNEXPECTED
  }

}