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
    suspend fun addExpense(@Body item: BudgetExpenseItem): Map<String, String>

    @PUT("manage")
    suspend fun updateExpense(@Body item: BudgetExpenseItem): Map<String, String>

    @HTTP(method = "DELETE", path = "manage", hasBody = true)
    suspend fun deleteExpense(@Body body: Map<String, Int>): Map<String, String>

    @HTTP(method = "DELETE", path = "manage", hasBody = true)
    suspend fun clearAllExpenses(@Body body: Map<String, Boolean>): Map<String, String>
}