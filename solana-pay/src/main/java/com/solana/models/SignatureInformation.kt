package com.solana.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignatureInformation(
    val blockTime: Any?,
    val confirmationStatus: Any?,
    var err: Any?,
    val memo: Any?,
    val signature: String,
    val slot: Any?
)