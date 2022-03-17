package com.solana.api

fun Api.getConfirmedBlocks(start: Int, end: Int? = null, onComplete: (Result<List<Double>>) -> Unit) {
    val params: List<Int>
    params = if (end == null) listOf(start) else listOf(start, end)
    router.request<List<Double>>("getConfirmedBlocks", params, List::class.java) { result ->
        result.onSuccess {
            onComplete(Result.success(it))
        }.onFailure {
            onComplete(Result.failure(it))
        }
    }
}