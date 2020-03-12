package com.harmony.kotlin.data.datasource.network

import com.harmony.kotlin.library.oauth.Default
import com.harmony.kotlin.library.oauth.domain.interactor.GetApplicationTokenInteractor
import com.harmony.kotlin.library.oauth.domain.interactor.GetPasswordTokenInteractor
import io.ktor.client.request.HttpRequestBuilder

fun HttpRequestBuilder.headers(values: List<Pair<String, String>>) {
    values.forEach { headers.append(it.first, it.second) }
}

suspend fun HttpRequestBuilder.oauthPasswordHeader(
    tokenIdentifier: String = Default.TOKEN_IDENTIFIER,
    getPasswordTokenInteractor: GetPasswordTokenInteractor
) {
    try {
        val passwordTokenInteractor = getPasswordTokenInteractor(tokenIdentifier)
        headers.append("Authorization", "${passwordTokenInteractor.tokenType} ${passwordTokenInteractor.accessToken}")
    } catch (e: Exception) {
        //todo: @jose throw error if it's 401
        println(e.toString())
        throw e
    }
}

suspend fun HttpRequestBuilder.oauthApplicationCredentialHeader(getApplicationTokenInteractor: GetApplicationTokenInteractor) {
    try {
        val applicationToken = getApplicationTokenInteractor()

        headers.append("Authorization", "${applicationToken.tokenType} ${applicationToken.accessToken}")
    } catch (e: Exception) {
        //todo: @jose throw error if it's 401
        println(e.toString())
        throw e
    }
}


fun HttpRequestBuilder.paginationOffsetLimitParams(offset: Int, limit: Int) {
    url.parameters.append("offset", offset.toString())
    url.parameters.append("limit", limit.toString())
}
