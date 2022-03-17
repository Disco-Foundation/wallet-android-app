package disco.foundation.discowallet.utils.solana.parsers

import disco.foundation.discowallet.utils.solana.models.CheckinTransaction
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import disco.foundation.discowallet.utils.solana.models.BasicData

private val moshi: Moshi = Moshi.Builder().add(BigIntegerAdapter).add(KotlinJsonAdapterFactory()).build()

fun parseBasicData(rechargeData: String): BasicData? {
    val jsonAdapter: JsonAdapter<BasicData> = moshi.adapter(BasicData::class.java)
    return jsonAdapter.fromJson(rechargeData)
}

