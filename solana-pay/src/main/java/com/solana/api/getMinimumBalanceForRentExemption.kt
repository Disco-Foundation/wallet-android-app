package com.solana.api

fun Api.getMinimumBalanceForRentExemption(dataLength: Long,  onComplete: ((Result<Long>) -> Unit)) {
    val params: MutableList<Any> = ArrayList()
    params.add(dataLength)
    router.request("getMinimumBalanceForRentExemption", params, Long::class.javaObjectType, onComplete)
}