package disco.foundation.discowallet.api

import disco.foundation.discowallet.api.models.CreateCheckinDataBody
import disco.foundation.discowallet.api.models.CreateCheckinDataResponse
import disco.foundation.discowallet.api.models.CreateRechargeDataBody
import disco.foundation.discowallet.api.models.CreateRechargeDataResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiClient {
    @POST("recharge")
    suspend fun createRechargeData(@Body requestBody: CreateRechargeDataBody): Response<CreateRechargeDataResponse>

    @POST("check-in")
    suspend fun createCheckinData(@Body requestBody: CreateCheckinDataBody): Response<CreateCheckinDataResponse>
}