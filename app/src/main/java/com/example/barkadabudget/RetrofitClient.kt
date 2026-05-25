package com.example.barkadabudget.budget

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Points directly to your running Flask server
    private const val BASE_URL = "http://192.168.254.101:5000/"

    val instance: BudgetApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BudgetApi::class.java)
    }
}