package com.djembe.android.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Use 10.0.2.2 for Android emulator -> localhost on dev machine.
    // Swap for your deployed backend URL for a real device / production build.
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: DjembeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DjembeApi::class.java)
    }
}
