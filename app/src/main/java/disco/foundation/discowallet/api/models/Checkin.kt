package disco.foundation.discowallet.api.models

import com.google.gson.annotations.SerializedName
import java.math.BigInteger

data class CreateCheckinDataBody (
    val PIN: String,
    val wearableId: BigInteger,
    val eventId: String,
    val payer: String
)

data class CreateCheckinDataResponse (
    @SerializedName("transaction") val transaction: RawTransaction
)
