package disco.foundation.discowallet.utils.solana

import com.solana.core.Account

fun createNewWallet(): Account {
    val wallet = Account(secretKey=WALLET_SEED) // 3cq7a3wFecykY9C9Qt5E23HEwHfGVZTzhczgg11XEdoa
    println("WALLET DATA -> ${wallet.publicKey.toBase58()}")

    return wallet
}