package com.solana.models

import com.solana.core.PublicKey
import com.solana.core.PublicKeyRule
import com.solana.models.buffer.*
import com.solana.models.buffer.moshi.AccountInfoJsonAdapter
import com.solana.models.buffer.moshi.MintJsonAdapter
import com.solana.models.buffer.moshi.TokenSwapInfoJsonAdapter
import com.solana.networking.models.RpcResponse
import com.solana.vendor.borshj.Borsh
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test
import org.junit.Assert.*
import java.util.*

class DecodingTests {

    fun borsh(): Borsh {
        val borsh = Borsh()
        borsh.setRules(listOf(PublicKeyRule(), AccountInfoRule(), MintRule(), TokenSwapInfoRule()))
        return borsh
    }

    @Test
    fun testDecodingMetaplexMeta() {
        val blobSrc = "BD8slOrgPZpL+5iD8MJBsTGY+esK0dZd8tlCX14LRmfREFsKKgyOBm9TbEDjhj2CcT+KPJFJ3acbYRLIqSsfsUkVAAAAU29sYW5pbWFsICMxOTYyIC0gQ2F0AAAAAD8AAABodHRwczovL2Fyd2VhdmUubmV0LzREVlNhUjU2V1RrV1Y4TnlvZ2U0LXRQWC1wOXJhSUJScHlZNEtxZVZlUDQAAAEBAAAAPyyU6uA9mkv7mIPwwkGxMZj56wrR1l3y2UJfXgtGZ9EBZAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=="

        val borsh = borsh()
        val byteArray = Base64.getDecoder().decode(blobSrc)

        val data = borsh.deserialize(byteArray, MetaplexMeta::class.java)
        assertEquals("5Fc7Zy7HgRatL8XhX5uqsUFEjGPop1uJXKrp3Ws7m1Tn", data.update_authority.toBase58())
        assertEquals("26r3GfgqbjMTjZahNgnwa9AtbDg8x2E5AGwzw17KWRC4", data.mint.toBase58())
        assertEquals("Solanimal #1962 - Cat", data.data.name)
        assertEquals("https://arweave.net/4DVSaR56WTkWV8Nyoge4-tPX-p9raIBRpyY4KqeVeP4", data.data.uri)
    }

    @Test
    fun testMoreDecodingMetaplexMeta() {
        val borsh = borsh()

        val plexData = MetaplexData("NFT", "BORSH", "www.solana.com")
        val plexMeta = MetaplexMeta(
            key = 1,
            update_authority = PublicKey("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3"),
            mint = PublicKey("BQWWFhzBdw2vKKBUX17NHeFbCoFQHfRARpdztPE2tDJ"),
            data = plexData
        )

        val serialized = borsh.serialize(plexMeta)
        val out = borsh.deserialize(serialized, MetaplexMeta::class.java)

        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", out.update_authority.toBase58())
        assertEquals("BQWWFhzBdw2vKKBUX17NHeFbCoFQHfRARpdztPE2tDJ", out.mint.toBase58())
        assertEquals("NFT", out.data.name)
        assertEquals("BORSH", out.data.symbol)
        assertEquals("www.solana.com", out.data.uri)
    }

    @Test
    fun testDecodingPublicKey() {
        val borsh = borsh()
        val bytes = byteArrayOf( 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val key = PublicKey(bytes)
        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", key.toBase58())
        val serialized = borsh.serialize(key)
        assertEquals(serialized.toTypedArray(), bytes.toTypedArray())
        val newPublickey = borsh.deserialize(serialized, PublicKey::class.java)
        assertEquals("CiDwVBFgWV9E5MvXWoLgnEgn2hK7rJikbvfWavzAQz3", newPublickey.toBase58())
    }


    @Test
    fun testDecodingMint() {
        val borsh = borsh()
        val rawData = listOf(
            "AQAAAAYa2dBThxVIU37ePiYYSaPft/0C+rx1siPI5GrbhT0MABCl1OgAAAAGAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
            "base64"
        )
        val buffer = Buffer.create(borsh, rawData, Mint::class.java)
        assertNotNull(buffer.value)
        val mintLayout = buffer.value!!

        assertEquals(
            "QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo",
            mintLayout.mintAuthority!!.toBase58()
        )
        assertEquals(1000000000000, mintLayout.supply)
        assertEquals(6, mintLayout.decimals)
        assertTrue(mintLayout.isInitialized == true)
        assertNull(mintLayout.freezeAuthority)

        val serialized = borsh.serialize(mintLayout)
        val deserialized = borsh.deserialize(serialized, Mint::class.java)
        assertEquals(deserialized.mintAuthority!!.toBase58(), mintLayout.mintAuthority!!.toBase58())
        assertEquals(deserialized.supply, mintLayout.supply)
        assertEquals(deserialized.decimals, mintLayout.decimals)
        assertTrue(deserialized.isInitialized == mintLayout.isInitialized == true)
        assertEquals(deserialized.freezeAuthority, mintLayout.freezeAuthority)
    }

    @Test
    fun testDecodingAccountInfo() {
        val borsh = borsh()
        val rawData: List<String> = listOf(
            "BhrZ0FOHFUhTft4+JhhJo9+3/QL6vHWyI8jkatuFPQwCqmOzhzy1ve5l2AqL0ottCChJZ1XSIW3k3C7TaBQn7aCGAQAAAAAAAQAAAOt6vNDYdevCbaGxgaMzmz7yoxaVu3q9vGeCc7ytzeWqAQAAAAAAAAAAAAAAAGQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "base64"
        )
        val buffer = Buffer.create(borsh, rawData, AccountInfo::class.java)
        assertNotNull(buffer.value)
        val accountInfo = buffer.value!!
        assertEquals("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo", accountInfo.mint.toBase58())
        assertEquals("BQWWFhzBdw2vKKBUX17NHeFbCoFQHfRARpdztPE2tDJ", accountInfo.owner.toBase58())
        assertEquals(
            "GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5",
            accountInfo.delegate?.toBase58()
        )
        assertEquals(100, accountInfo.delegatedAmount)
        assertEquals(false, accountInfo.isNative)
        assertEquals(true, accountInfo.isInitialized)
        assertEquals(false, accountInfo.isFrozen)
        assertNull(accountInfo.rentExemptReserve)
        assertNull(accountInfo.closeAuthority)

        val serialized = borsh.serialize(accountInfo)

        val deserialized = borsh.deserialize(serialized, AccountInfo::class.java)

        assertEquals(deserialized.mint.toBase58(), accountInfo.mint.toBase58())
        assertEquals(deserialized.owner.toBase58(), accountInfo.owner.toBase58())
        assertEquals(deserialized.delegate?.toBase58(), accountInfo.delegate?.toBase58())
        assertEquals(deserialized.state, accountInfo.state)
        assertEquals(deserialized.isNativeOption, accountInfo.isNativeOption)
        assertEquals(deserialized.rentExemptReserve, accountInfo.rentExemptReserve)
        assertEquals(deserialized.isNativeRaw, accountInfo.isNativeRaw)
        assertEquals(deserialized.isNative, accountInfo.isNative)
        assertEquals(deserialized.isInitialized, accountInfo.isInitialized)
        assertEquals(deserialized.delegatedAmount, accountInfo.delegatedAmount)
        assertEquals(deserialized.isInitialized, accountInfo.isInitialized)
        assertEquals(deserialized.isFrozen, accountInfo.isFrozen)
        assertEquals(deserialized.closeAuthority, accountInfo.closeAuthority)
    }

    @Test
    fun testDecodingAccountInfo2() {
        val borsh = borsh()
        val string = listOf(
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAOt6vNDYdevCbaGxgaMzmz7yoxaVu3q9vGeCc7ytzeWq",
            "base64"
        )
        val buffer = Buffer.create(borsh, string, AccountInfo::class.java)
        assertNotNull(buffer.value)
        val accountInfo = buffer.value!!

        assertNull(accountInfo.delegate)
        assertEquals(0, accountInfo.delegatedAmount)
        assertEquals(false, accountInfo.isInitialized)
        assertEquals(false, accountInfo.isNative)
        assertNull(accountInfo.rentExemptReserve)

        assertEquals(
            "GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5",
            accountInfo.closeAuthority?.toBase58()
        )

        val string2 = listOf(
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAOt6vNDYdevCbaGxgaMzmz7yoxaVu3q9vGeCc7ytzeWq",
            "base64"
        )
        val buffer2 = Buffer.create(borsh, string2, AccountInfo::class.java)
        assertNotNull(buffer.value)
        val accountInfo2 = buffer2.value!!
        assertEquals(true, accountInfo2.isFrozen)
    }

    @Test
    fun testDecodingTokenSwap() {
        val borsh = borsh()
        val string = listOf(
            "AQH/Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKnPPnmVdf8VefedpPOl3xy2V/o+YvTT+f/dj/1blp9D9lI+9w67aLlO5X6dSFPB7WkhvyP+71AxESXk7Qw9nyYEYH7t0UamkBlPrllRfjnQ9h+sx/GQHoBS4AbWPpi2+m5dBuymmuZeydiI91aVN//6kR8bk4czKnvSXu1WXNW4hwabiFf+q4GE+2h/Y0YYwDXaxDncGus7VZig8AAAAAAB1UBY8wcrypvzuco4dv7UUURt8t9MOpnq7YnffB1OovkZAAAAAAAAABAnAAAAAAAABQAAAAAAAAAQJwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUAAAAAAAAAGQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            "base64"
        )
        val tokenSwapInfoBorsh = Buffer.create(borsh, string, TokenSwapInfo::class.java)
        assertNotNull(tokenSwapInfoBorsh.value)
        val tokenSwapInfo = tokenSwapInfoBorsh.value!!
        assertEquals(1, tokenSwapInfo.version)
        assertEquals(
            "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
            tokenSwapInfo.tokenProgramId.toBase58()
        )
        assertEquals("7G93KAMR8bLq5TvgLHmpACLXCYwDcdtXVBKsN5Fx41iN", tokenSwapInfo.mintA.toBase58())
        assertEquals("So11111111111111111111111111111111111111112", tokenSwapInfo.mintB.toBase58())
        assertEquals(0, tokenSwapInfo.curveType)
        assertEquals(tokenSwapInfo.isInitialized, true)
        assertEquals("11111111111111111111111111111111", tokenSwapInfo.payer.toBase58())

        val serialized = borsh.serialize(tokenSwapInfo)
        val deserialized = borsh.deserialize(serialized, TokenSwapInfo::class.java)

        assertEquals(deserialized.version, tokenSwapInfo.version)
        assertEquals(
            deserialized.tokenProgramId.toBase58(),
            tokenSwapInfo.tokenProgramId.toBase58()
        )
        assertEquals(deserialized.mintA.toBase58(), tokenSwapInfo.mintA.toBase58())
        assertEquals(deserialized.mintB.toBase58(), tokenSwapInfo.mintB.toBase58())
        assertEquals(deserialized.curveType, tokenSwapInfo.curveType)
        assertEquals(deserialized.isInitialized, tokenSwapInfo.isInitialized)
        assertEquals(deserialized.payer.toBase58(), tokenSwapInfo.payer.toBase58())
    }

    @Test
    fun testDecodingRPCAccountInfo() {
        val moshi: Moshi = Moshi.Builder()
            .add(AccountInfoJsonAdapter(borsh()))
            .addLast(KotlinJsonAdapterFactory()).build()
        val response = """
            {
                "context":{
                    "slot":66581317
                },
                "value":{
                    "data":[
                        "AQH/Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKkQr0Ypa909a9ivbANnFa97v5sZX6SSw4RMjz25SJneJ7hVdfVvF2ulbUZ+sO8PdxmtEPwI3BDoM2BQJbwTgVECe7TnDMVGigX6Vwtmg8zNO42b0cOn5KCeZPFrz9b5cCo17xfXx1Y0fo+QMstbwrkKVfDprS2WS51UTI9eIZO+Ofo/EG/xTzvdcalkpCBEoNMWI8a2DFYSzHvAv3PVjJbtj+vrRV0pqYlEY7ZMtzrhD/hCWfFbBga4DFrJLlEeFTUZAAAAAAAAABAnAAAAAAAABQAAAAAAAAAQJwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUAAAAAAAAAGQAAAAAAAAAAQEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "base64"
                    ],
                    "executable":false,
                    "lamports":3145920,
                    "owner":"SwaPpA9LAaLfeLi3a68M4DjnLqgtticKg6CnyNwgAC8",
                    "rentEpoch":153
                }
            }"""
        val adapter: JsonAdapter<RPC<BufferInfo<AccountInfo>>> = moshi.adapter(
            Types.newParameterizedType(
                RPC::class.java,
                Types.newParameterizedType(
                    BufferInfo::class.java,
                    AccountInfo::class.java
                )
            )
        )
        val obj = adapter.fromJson(response)
        assertNotNull(obj)
    }

    @Test
    fun testDecodingRPCAccountInfo2() {
        val moshi: Moshi = Moshi.Builder()
            .add(AccountInfoJsonAdapter(borsh()))
            .addLast(KotlinJsonAdapterFactory()).build()
        val response = """
            {
                "context":{
                    "slot":66581317
                },
                "value":{
                    "data":[
                        "BhrZ0FOHFUhTft4+JhhJo9+3/QL6vHWyI8jkatuFPQwCqmOzhzy1ve5l2AqL0ottCChJZ1XSIW3k3C7TaBQn7aCGAQAAAAAAAQAAAOt6vNDYdevCbaGxgaMzmz7yoxaVu3q9vGeCc7ytzeWqAQAAAAAAAAAAAAAAAGQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "base64"
                    ],
                    "executable":false,
                    "lamports":3145920,
                    "owner":"SwaPpA9LAaLfeLi3a68M4DjnLqgtticKg6CnyNwgAC8",
                    "rentEpoch":153
                }
            }""".trimIndent()
        val adapter: JsonAdapter<RPC<BufferInfo<AccountInfo>>> = moshi.adapter(
            Types.newParameterizedType(
                RPC::class.java,
                Types.newParameterizedType(
                    BufferInfo::class.java,
                    AccountInfo::class.java
                )
            )
        )
        val obj = adapter.fromJson(response)!!
        assertNotNull(obj)
        val accountInfo = obj.value!!.data!!.value!!
        assertEquals("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo", accountInfo.mint.toBase58())
        assertEquals("BQWWFhzBdw2vKKBUX17NHeFbCoFQHfRARpdztPE2tDJ", accountInfo.owner.toBase58())
        assertEquals(
            "GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5",
            accountInfo.delegate?.toBase58()
        )
        assertEquals(100, accountInfo.delegatedAmount)
        assertEquals(false, accountInfo.isNative)
        assertEquals(true, accountInfo.isInitialized)
        assertEquals(false, accountInfo.isFrozen)
        assertNull(accountInfo.rentExemptReserve)
        assertNull(accountInfo.closeAuthority)
    }

    @Test
    fun testDecodingRPCMint() {
        val moshi: Moshi = Moshi.Builder()
            .add(MintJsonAdapter(borsh()))
            .addLast(KotlinJsonAdapterFactory()).build()
        val response = """
            {
                "context":{
                    "slot":66581317
                },
                "value":{
                    "data":[
                        "AQAAAAYa2dBThxVIU37ePiYYSaPft/0C+rx1siPI5GrbhT0MABCl1OgAAAAGAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
                        "base64"
                    ],
                    "executable":false,
                    "lamports":3145920,
                    "owner":"SwaPpA9LAaLfeLi3a68M4DjnLqgtticKg6CnyNwgAC8",
                    "rentEpoch":153
                }
            }""".trimIndent()

        val adapter: JsonAdapter<RPC<BufferInfo<Mint>>> = moshi.adapter(
            Types.newParameterizedType(
                RPC::class.java,
                Types.newParameterizedType(
                    BufferInfo::class.java,
                    Mint::class.java
                )
            )
        )
        val obj = adapter.fromJson(response)!!
        assertNotNull(obj)
        val mintLayout = obj.value!!.data!!.value!!
        assertEquals(
            "QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo",
            mintLayout.mintAuthority!!.toBase58()
        )
        assertEquals(1000000000000, mintLayout.supply)
        assertEquals(6, mintLayout.decimals)
        assertTrue(mintLayout.isInitialized == true)
        assertNull(mintLayout.freezeAuthority)
    }

    @Test
    fun testDecodingRPCTokenSwap() {
        val moshi: Moshi = Moshi.Builder()
            .add(TokenSwapInfoJsonAdapter(borsh()))
            .addLast(KotlinJsonAdapterFactory()).build()
        val response = """
            {
                "context":{
                    "slot":66581317
                },
                "value":{
                    "data":[
                        "AQH/Bt324ddloZPZy+FGzut5rBy0he1fWzeROoz1hX7/AKnPPnmVdf8VefedpPOl3xy2V/o+YvTT+f/dj/1blp9D9lI+9w67aLlO5X6dSFPB7WkhvyP+71AxESXk7Qw9nyYEYH7t0UamkBlPrllRfjnQ9h+sx/GQHoBS4AbWPpi2+m5dBuymmuZeydiI91aVN//6kR8bk4czKnvSXu1WXNW4hwabiFf+q4GE+2h/Y0YYwDXaxDncGus7VZig8AAAAAAB1UBY8wcrypvzuco4dv7UUURt8t9MOpnq7YnffB1OovkZAAAAAAAAABAnAAAAAAAABQAAAAAAAAAQJwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUAAAAAAAAAGQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                        "base64"
                    ],
                    "executable":false,
                    "lamports":3145920,
                    "owner":"SwaPpA9LAaLfeLi3a68M4DjnLqgtticKg6CnyNwgAC8",
                    "rentEpoch":153
                }
            }"""
        val adapter: JsonAdapter<RPC<BufferInfo<TokenSwapInfo>>> = moshi.adapter(
            Types.newParameterizedType(
                RPC::class.java,
                Types.newParameterizedType(
                    BufferInfo::class.java,
                    TokenSwapInfo::class.java
                )
            )
        )
        val obj = adapter.fromJson(response)!!
        assertNotNull(obj)

        val tokenSwapInfo = obj.value!!.data!!.value!!
        assertEquals(1, tokenSwapInfo.version)
        assertEquals(
            "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA",
            tokenSwapInfo.tokenProgramId.toBase58()
        )
        assertEquals("7G93KAMR8bLq5TvgLHmpACLXCYwDcdtXVBKsN5Fx41iN", tokenSwapInfo.mintA.toBase58())
        assertEquals("So11111111111111111111111111111111111111112", tokenSwapInfo.mintB.toBase58())
        assertEquals(0, tokenSwapInfo.curveType)
        assertEquals(tokenSwapInfo.isInitialized, true)
        assertEquals("11111111111111111111111111111111", tokenSwapInfo.payer.toBase58())
    }

    @Test
    fun testDecodingRPCResponse() {
        val moshi: Moshi = Moshi.Builder()
            .add(AccountInfoJsonAdapter(borsh()))
            .addLast(KotlinJsonAdapterFactory()).build()
        val response = """
            {
               "jsonrpc":"2.0",
               "result":{
                  "context":{
                     "slot":66669000
                  },
                  "value":{
                     "data":[
                        "",
                        "base64"
                     ],
                     "executable":false,
                     "lamports":161169886,
                     "owner":"11111111111111111111111111111111",
                     "rentEpoch":154
                  }
               },
               "id":"3618d086-1562-41b6-ad5e-f387b67d4bf9"
            }""".trimIndent()
        val adapter: JsonAdapter<RpcResponse<RPC<BufferInfo<AccountInfo>>>> = moshi.adapter(
            Types.newParameterizedType(
                RpcResponse::class.java,
                Types.newParameterizedType(
                    RPC::class.java,
                    Types.newParameterizedType(
                        BufferInfo::class.java,
                        AccountInfo::class.java
                    )
                )
            )
        )
        val obj = adapter.fromJson(response)!!
        assertNotNull(obj)
    }
}