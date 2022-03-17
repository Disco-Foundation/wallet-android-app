package disco.foundation.discowallet.utils.solana.models

import com.squareup.moshi.JsonClass
import java.math.BigInteger

data class CheckinTransactionInstruction(
    val wId: BigInteger,
    val eId: String,
    val PIN: String,
)

@JsonClass(generateAdapter = true)
data class CheckinTransaction(
    val inst: CheckinTransactionInstruction,
    val action: String,
    val ref: String,
)