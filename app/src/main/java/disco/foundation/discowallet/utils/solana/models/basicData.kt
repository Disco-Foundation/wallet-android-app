package disco.foundation.discowallet.utils.solana.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BasicData(
    val action: String,
)