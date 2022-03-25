package disco.foundation.discowallet.utils.solana

import com.solana.core.Account

fun createNewWallet(): Account {
    return Account()
}

fun generateWallet(secret: ByteArray): Account {
    return Account(secretKey = secret)
}