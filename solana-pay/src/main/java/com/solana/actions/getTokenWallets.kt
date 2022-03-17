package com.solana.actions

import com.solana.api.getProgramAccounts
import com.solana.core.PublicKey
import com.solana.models.*
import com.solana.models.buffer.AccountInfo
import com.solana.programs.TokenProgram
import com.solana.vendor.ContResult
import com.solana.vendor.Result


fun Action.getTokenWallets(
    account: PublicKey,
    onComplete: ((Result<List<Wallet>, Exception>) -> Unit)
) {
    val memcmp = listOf(
        Memcmp(32, account.toBase58())
    )
    ContResult<List<ProgramAccount<AccountInfo>>, Exception> { cb ->
        api.getProgramAccounts(TokenProgram.PROGRAM_ID, memcmp, 165, AccountInfo::class.java) { result ->
            result.onSuccess {
                cb(Result.success(it))
            }.onFailure {
                cb(Result.failure(Exception(it)))
            }
        }
    }.map { accounts ->

        val accountsValues = accounts.map { if(it.account.data != null) { it } else { null } }.filterNotNull()
        val pubkeyValue = accountsValues.map { Pair(it.pubkey, it.account) }
        val wallets = pubkeyValue.map {
            println("PROBANDO")
            println(it.second.data!!.value)
            val mintAddress = it.second.data!!.value!!.mint
            val token = this.supportedTokens.firstOrNull() { it.address == mintAddress.toBase58() } ?: Token.unsupported(mintAddress.toBase58())
            Wallet(it.first, it.second.data!!.value!!.lamports, token, true)
        }
       wallets
    }.run(onComplete)
}

