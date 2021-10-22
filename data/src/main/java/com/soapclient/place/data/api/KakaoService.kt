package com.soapclient.place.data.api

import com.soapclient.place.data.BuildConfig.KAKAO_REST_API_KEY
import com.soapclient.place.data.remote.model.PlaceSearchResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoService {
    @GET("/v2/local/search/category.json")
    suspend fun searchPlace(
        @Header("Authorization") key: String = KAKAO_REST_API_KEY,
        @Query("category_group_code") category_group_code: String,
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("radius") radius: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): PlaceSearchResponse

    @GET("/v2/local/search/keyword.json")
    suspend fun searchLocationByKeyword(
        @Header("Authorization") key: String = KAKAO_REST_API_KEY,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("query") query: String,
        @Query("x") x: String,
        @Query("y") y: String
    ): PlaceSearchResponse

    companion object {
        private const val SERVICE_URL = "https://dapi.kakao.com/"

        fun create(): KakaoService {
            val client = OkHttpClient.Builder()
                .build()

            return Retrofit.Builder()
                .baseUrl(SERVICE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KakaoService::class.java)
        }
    }
}