package com.solana.api

import com.solana.models.ConfirmedTransaction

fun Api.getConfirmedTransaction(signature: String,
                            onComplete: ((Result<ConfirmedTransaction>) -> Unit)
){
    val params: MutableList<Any> = ArrayList()
    params.add(signature)

    return router.request("getConfirmedTransaction", params, ConfirmedTransaction::class.java, onComplete)
}

fun Api.getConfirmedTransaction(signature: String,
                                additionalParams: Map<String, Any?>,
                                onComplete: ((Result<ConfirmedTransaction>) -> Unit)
){
    val params: MutableList<Any> = ArrayList()
    val parameterMap: MutableMap<String, Any?> = HashMap()
    params.add(signature)
    parameterMap["encoding"] = additionalParams.getOrDefault("encoding", "jsonParsed")
    params.add(parameterMap)

    return router.request("getConfirmedTransaction", params, ConfirmedTransaction::class.java, onComplete)
}

