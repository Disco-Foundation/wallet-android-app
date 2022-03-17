package com.solana.api

import com.solana.core.Account
import com.solana.core.Transaction
import com.solana.models.RpcSendTransactionConfig
import java.lang.RuntimeException
import java.util.*

fun Api.sendTransaction(
    transaction: Transaction,
    signers: List<Account>,
    recentBlockHash: String? = null,
    onComplete: ((Result<String>) -> Unit)
) {
    if (recentBlockHash == null) {
        getRecentBlockhash { result ->
            result.map { recentBlockHash ->
                transaction.setRecentBlockHash(recentBlockHash)
                transaction.sign(signers)
                val serializedTransaction: ByteArray = transaction.serialize()
                val base64Trx: String = Base64.getEncoder().encodeToString(serializedTransaction)
                listOf(base64Trx, RpcSendTransactionConfig())
            }.onSuccess {
                router.request("sendTransaction", it, String::class.java, onComplete)
            }.onFailure {
                onComplete(Result.failure(RuntimeException(it)))
            }
        }
    } else {
        transaction.setRecentBlockHash(recentBlockHash)
        transaction.sign(signers)
        val serializedTransaction: ByteArray = transaction.serialize()
        val base64Trx: String = Base64.getEncoder().encodeToString(serializedTransaction)
        val params = listOf(base64Trx, RpcSendTransactionConfig())
        router.request("sendTransaction", params, String::class.java, onComplete)
    }
}