package com.solana.api

fun Api.getSnapshotSlot(onComplete: (Result<Long>) -> Unit) {
    router.request("getSnapshotSlot", ArrayList(), Long::class.javaObjectType, onComplete)
}