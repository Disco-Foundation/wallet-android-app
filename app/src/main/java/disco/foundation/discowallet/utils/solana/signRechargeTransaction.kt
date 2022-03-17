package disco.foundation.discowallet.utils.solana

import disco.foundation.discowallet.api.ApiAdapter
import disco.foundation.discowallet.api.models.CreateRechargeDataBody
import disco.foundation.discowallet.api.models.RawTransaction
import disco.foundation.discowallet.api.models.RequestResult
import disco.foundation.discowallet.utils.solana.models.RechargeTransaction
import com.solana.Solana
import com.solana.api.sendTransaction
import com.solana.core.*
import com.solana.networking.NetworkingRouter
import com.solana.networking.RPCEndpoint
import kotlinx.coroutines.delay

private val solana = Solana(NetworkingRouter(RPCEndpoint.devnetSolana))

fun dataToByteArray(data: Array<Int>) = ByteArray(data.size) { pos -> data[pos].toByte() }

suspend fun createRechargeTransaction(body: CreateRechargeDataBody, payer: Account, ref: String): RequestResult<String> {
    var result: RequestResult<String> = RequestResult.Loading()
    val response = ApiAdapter.apiClient.createRechargeData(body).body()
    val data: RawTransaction? = response?.transaction
    var finished = false
    if (data != null) {
        val keys = mutableListOf<AccountMeta>()
        val rawKeysArray = data.instructions[0].keys.iterator()
        rawKeysArray.forEach {
            val temporalKey = AccountMeta(PublicKey(it.pubkey), it.isSigner, it.isWritable)
            keys.add(temporalKey)
        }

        // add reference
        keys.add(AccountMeta(PublicKey(ref), false, false))

        val dataBuffer = dataToByteArray(data.instructions[0].data.data)
        val tx = Transaction()
        val rechargeInstruction = TransactionInstruction(programId = PublicKey(EVENT_PROGRAM_ID), data = dataBuffer, keys = keys)

        tx.addInstruction(rechargeInstruction)

        solana.api.sendTransaction(transaction = tx, listOf(payer), null) { res ->
            println("The result is --> $res")
            result = if(res.isSuccess){ RequestResult.Success(res.toString()) }
            else { RequestResult.Error(res.toString()) }
            finished = true
        }
        while(!finished){ delay(1000) }
        return result
    } else {
        return RequestResult.Error("Error creating recharge transaction")
    }
}

suspend fun signRechargeTransaction(
    payer: Account,
    qrData: RechargeTransaction
): RequestResult<String> {

    val amount: Double = qrData.inst.amount.toDouble()
    val body = CreateRechargeDataBody(
        amount = amount,
        wearableId = qrData.inst.wId,
        eventId = qrData.inst.eId,
        payer = payer.publicKey.toBase58()
    )
    return createRechargeTransaction(body, payer, qrData.ref)
}


