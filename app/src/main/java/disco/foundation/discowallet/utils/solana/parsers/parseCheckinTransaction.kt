package disco.foundation.discowallet.utils.solana.parsers

import disco.foundation.discowallet.utils.solana.models.CheckinTransaction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private val moshi: Moshi = Moshi.Builder().add(BigIntegerAdapter).add(KotlinJsonAdapterFactory()).build()

fun parseCheckinTransaction(rechargeData: String): CheckinTransaction? {
    val jsonAdapter: JsonAdapter<CheckinTransaction> = moshi.adapter(CheckinTransaction::class.java)
    return jsonAdapter.fromJson(rechargeData)
}

