package disco.foundation.discowallet.api.models

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class CreateRechargeDataBody (
    val amount: Double,
    val wearableId: BigInteger,
    val eventId: String,
    val payer: String
)

data class CreateRechargeDataResponse (
    @SerializedName("transaction") val transaction: RawTransaction
)
