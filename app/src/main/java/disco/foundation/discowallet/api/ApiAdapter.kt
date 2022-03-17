package disco.foundation.discowallet.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiAdapter {

    //private var BASE_URL = "http://localhost:3333/api/"
    private var BASE_URL = "https://api.disco.foundation/api/"

    val apiClient: ApiClient = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiClient::class.java)

}