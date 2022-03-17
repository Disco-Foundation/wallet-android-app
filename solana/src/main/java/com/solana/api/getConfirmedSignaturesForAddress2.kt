package com.solana.api

import com.solana.core.PublicKey
import com.solana.models.ConfirmedSignFAddr2
import com.solana.models.SignatureInformation

fun Api.getSignaturesForAddress(
    account: PublicKey,
    limit: Int? = null,
    before: String?  = null,
    until: String? = null,
    onComplete: (Result<List<SignatureInformation>>) -> Unit
) {
    val params: MutableList<Any> = ArrayList()
    params.add(account.toString())
    params.add( ConfirmedSignFAddr2(limit = limit?.toLong(), before = before, until = until) )

    router.request<List<SignatureInformation>>(
        "getSignaturesForAddress", params,
        List::class.java
    ) { result ->
        result.onSuccess {
            onComplete(Result.success(it))
        }.onFailure {
            onComplete(Result.failure(it))
        }
    }
}
