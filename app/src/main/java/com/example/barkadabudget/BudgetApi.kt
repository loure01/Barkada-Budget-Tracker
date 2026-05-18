package com.example.barkadabudget.budget

import retrofit2.http.*

data class BudgetExpenseItem(
    val id: Int? = null,
    val payer_name: String,
    val description: String,
    val amount: Double

)
data class UserItem(
    val username: String,
    val password: String
)

interface BudgetApi {
    @GET("manage")
    suspend fun getExpenses(): List<BudgetExpenseItem>

    @POST("manage")
    suspend fun addExpense(@Body expense: BudgetExpenseItem): Void

    @HTTP(method = "DELETE", path = "manage", hasBody = true)
    suspend fun deleteExpense(@Body body: Map<String, Int>): Void


    @POST("register")
    suspend fun registerUser(@Body body: Map<String, String>): retrofit2.Response<Void>
}
