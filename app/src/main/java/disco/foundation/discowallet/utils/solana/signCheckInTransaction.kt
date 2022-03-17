package disco.foundation.discowallet.utils.solana

import disco.foundation.discowallet.api.ApiAdapter
import disco.foundation.discowallet.api.models.RawTransaction
import disco.foundation.discowallet.api.models.RequestResult
import com.solana.Solana
import com.solana.api.sendTransaction
import com.solana.core.*
import com.solana.networking.NetworkingRouter
import com.solana.networking.RPCEndpoint
import disco.foundation.discowallet.api.models.CreateCheckinDataBody
import disco.foundation.discowallet.utils.solana.models.CheckinTransaction
import kotlinx.coroutines.delay

private val solana = Solana(NetworkingRouter(RPCEndpoint.devnetSolana))


suspend fun createCheckinTransaction(body: CreateCheckinDataBody, payer: Account, ref: String): RequestResult<String> {
    var result: RequestResult<String> = RequestResult.Loading()
    val response = ApiAdapter.apiClient.createCheckinData(body).body()
    val data: RawTransaction? = response?.transaction
    var finished = false
    if (data != null) {
        val programId = data.instructions[0].programId
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
        val rechargeInstruction = TransactionInstruction(
            programId = PublicKey(programId),
            data = dataBuffer,
            keys = keys
        )
        tx.addInstruction(rechargeInstruction)
        solana.api.sendTransaction(transaction = tx, listOf(payer), null) { res ->
            println("El payer es ${payer.publicKey.toBase58()} - The result is --> $res")
            result = if (res.isSuccess) {
                RequestResult.Success(res.toString())
            } else {
                RequestResult.Error(res.toString())
            }
            finished = true
        }
        while (!finished) {
            delay(1000)
        }
        return result
    } else {
        return RequestResult.Error("Error creating recharge transaction")
    }
}

suspend fun signCheckinTransaction(
    payer: Account,
    qrData: CheckinTransaction
): RequestResult<String>  {

    println(payer)
    println(qrData)

    val body = CreateCheckinDataBody(
        PIN = qrData.inst.PIN,
        wearableId = qrData.inst.wId,
        eventId = qrData.inst.eId,
        payer = payer.publicKey.toBase58()
    )
    println("EL BODY $body")
    return createCheckinTransaction(body, payer, qrData.ref)
}


