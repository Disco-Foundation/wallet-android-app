package disco.foundation.discowallet.utils.solana.parsers

import disco.foundation.discowallet.utils.solana.models.RechargeTransaction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

private val moshi: Moshi = Moshi.Builder().add(BigIntegerAdapter).add(KotlinJsonAdapterFactory()).build()

fun parseRechargeTransaction(rechargeData: String): RechargeTransaction? {
    val jsonAdapter: JsonAdapter<RechargeTransaction> = moshi.adapter(RechargeTransaction::class.java)
    return jsonAdapter.fromJson(rechargeData)
}

