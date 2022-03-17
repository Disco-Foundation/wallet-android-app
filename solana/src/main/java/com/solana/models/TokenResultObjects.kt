package com.solana.models

import com.squareup.moshi.Json

class TokenResultObjects {
    open class TokenAmountInfo(
        @Json(name = "amount") var amount: String?,
        @Json(name = "decimals") var decimals: Int,
        @Json(name = "uiAmount") var uiAmount: Double,
        @Json(name = "uiAmountString") var uiAmountString: String
    )

    class TokenAccount (
        @Json(name = "amount") val amount: String?,
        @Json(name = "decimals") val decimals: Int,
        @Json(name = "uiAmount") val uiAmount: Double?,
        @Json(name = "uiAmountString") val uiAmountString: String?,
        @Json(name = "address") val address: String?
    )

    class TokenInfo (
        @Json(name = "isNative")
        private val isNative: Boolean?,

        @Json(name = "mint")
        private val mint: String?,

        @Json(name = "owner")
        private val owner: String?,

        @Json(name = "state")
        private val state: String?,

        @Json(name = "tokenAmount")
        private val tokenAmount: TokenAmountInfo?
    )

    class ParsedData (
        @Json(name = "info")
        private val info: TokenInfo,

        @Json(name = "type")
        private val type: String
    )

    class Data (
        @Json(name = "parsed")
        private val parsed: ParsedData,

        @Json(name = "program")
        private val program: String,

        @Json(name = "space")
        private val space: Int? = null
    )
}