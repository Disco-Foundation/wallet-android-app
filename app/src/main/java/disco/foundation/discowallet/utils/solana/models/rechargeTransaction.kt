package disco.foundation.discowallet.utils.solana.models

import com.squareup.moshi.JsonClass
import java.math.BigInteger

data class RechargeTransactionInstruction(
    val wId: BigInteger,
    val eId: String,
    val amount: Double,
)

@JsonClass(generateAdapter = true)
data class RechargeTransaction(
    val inst: RechargeTransactionInstruction,
    val action: String,
    val ref: String,
)