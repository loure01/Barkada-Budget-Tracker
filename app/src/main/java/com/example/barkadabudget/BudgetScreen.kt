package com.example.barkadabudget.budget

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
    var loggedInUser by remember { mutableStateOf("") }

    var authUsername by remember { mutableStateOf(TextFieldValue("")) }
    var authPassword by remember { mutableStateOf(TextFieldValue("")) }
    var authMessage by remember { mutableStateOf("") }

    var expenseList by remember { mutableStateOf(listOf<BudgetExpenseItem>()) }
    var payerName by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var amount by remember { mutableStateOf(TextFieldValue("")) }

    fun refreshExpenses() {
        coroutineScope.launch {
            try { expenseList = RetrofitClient.instance.getExpenses() } catch (e: Exception) { e.printStackTrace() }
        }
    }

    val totalExpense = expenseList.sumOf { it.amount }
    val distinctPayerNames = expenseList.map { it.payer_name.trim().lowercase().capitalize() }.distinct()
    val totalMembers = if (distinctPayerNames.isEmpty()) 1 else distinctPayerNames.size
    val sharePerPerson = totalExpense / totalMembers

    val individualBalances = distinctPayerNames.map { name ->
        val totalPaidByHim = expenseList.filter { it.payer_name.trim().lowercase() == name.lowercase() }.sumOf { it.amount }
        PayerBalance(name = name, amountSpent = totalPaidByHim, netBalance = totalPaidByHim - sharePerPerson)
    }

    val debtors = individualBalances.filter { it.netBalance < -0.01 }.sortedBy { it.netBalance }.toMutableList()
    val lenders = individualBalances.filter { it.netBalance > 0.01 }.sortedByDescending { it.netBalance }.toMutableList()
    val calculatedSettlements = remember(expenseList) { mutableStateListOf<String>() }
    calculatedSettlements.clear()

    var dIdx = 0
    var lIdx = 0
    while (dIdx < debtors.size && lIdx < lenders.size) {
        val debtor = debtors[dIdx]; val lender = lenders[lIdx]
        val oweAmount = minOf(-debtor.netBalance, lender.netBalance)
        if (oweAmount > 0.01) { calculatedSettlements.add("${debtor.name} owes ${lender.name} ₱${String.format("%.2f", oweAmount)}") }
        debtor.netBalance += oweAmount; lender.netBalance -= oweAmount
        if (Math.abs(debtor.netBalance) < 0.01) dIdx++
        if (Math.abs(lender.netBalance) < 0.01) lIdx++
    }

    fun exportExpensesToCSV(context: Context, items: List<BudgetExpenseItem>) {
        if (items.isEmpty()) { Toast.makeText(context, "No records to export!", Toast.LENGTH_SHORT).show(); return }
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val csvFile = File(downloadsDir, "Barkada_Budget_Report.csv")
            val writer = FileWriter(csvFile)
            writer.append("ID,Payer Name,Description,Amount (PHP)\n")
            items.forEach { item -> writer.append("${item.id ?: 0},${item.payer_name},${item.description},${item.amount}\n") }
            writer.append("\n,Total Expenses Pool,₱${totalExpense}\n,Fair Share Per Person,₱${sharePerPerson}\n")
            writer.flush(); writer.close()
            Toast.makeText(context, "Saved to Downloads: ${csvFile.name}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) { e.printStackTrace() }
    }

    when (currentScreen) {
        AppScreen.LOGIN -> {
            AuthScreen(
                isRegister = false, usernameValue = authUsername, onUsernameChange = { authUsername = it },
                passwordValue = authPassword, onPasswordChange = { authPassword = it }, message = authMessage,
                onPrimaryButtonClick = {
                    if (authUsername.text.isNotBlank() && authPassword.text.isNotBlank()) {
                        loggedInUser = authUsername.text; authMessage = ""; refreshExpenses(); currentScreen = AppScreen.MAIN_DASHBOARD
                    } else { authMessage = "Please fill in all fields" }
                },
                onToggleScreenClick = { authMessage = ""; currentScreen = AppScreen.REGISTER }
            )
        }
        AppScreen.REGISTER -> {
            AuthScreen(
                isRegister = true, usernameValue = authUsername, onUsernameChange = { authUsername = it },
                passwordValue = authPassword, onPasswordChange = { authPassword = it }, message = authMessage,
                onPrimaryButtonClick = { if (authUsername.text.isNotBlank() && authPassword.text.isNotBlank()) { authMessage = "Registration successful!"; currentScreen = AppScreen.LOGIN } },
                onToggleScreenClick = { currentScreen = AppScreen.LOGIN }
            )
        }
        AppScreen.MAIN_DASHBOARD -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Barkada Tracker (${loggedInUser})") },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        actions = {
                            TextButton(onClick = { exportExpensesToCSV(context, expenseList) }) { Text("Export CSV", color = MaterialTheme.colorScheme.primary) }
                            TextButton(onClick = { currentScreen = AppScreen.LOGIN }) { Text("Logout", color = MaterialTheme.colorScheme.error) }
                        }
                    )
                }
            ) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BudgetMetrics(totalExpense = totalExpense, sharePerPerson = sharePerPerson, settlements = calculatedSettlements)
                    TransactionForm(
                        payerName = payerName, onPayerNameChange = { payerName = it },
                        description = description, onDescriptionChange = { description = it },
                        amount = amount, onAmountChange = { amount = it },
                        onAddClick = {
                            if (payerName.text.isNotBlank() && description.text.isNotBlank() && amount.text.isNotBlank()) {
                                coroutineScope.launch {
                                    try {
                                        val newItem = BudgetExpenseItem(payer_name = payerName.text, description = description.text, amount = amount.text.toDoubleOrNull() ?: 0.0)
                                        RetrofitClient.instance.addExpense(newItem)
                                        payerName = TextFieldValue(""); description = TextFieldValue(""); amount = TextFieldValue(""); refreshExpenses()
                                    } catch (e: Exception) { e.printStackTrace() }
                                }
                            }
                        }
                    )
                    HistoryList(expenseList = expenseList, onDeleteClick = { itemId ->
                        coroutineScope.launch {
                            try { RetrofitClient.instance.deleteExpense(mapOf("id" to itemId)); refreshExpenses() } catch (e: Exception) { e.printStackTrace() }
                        }
                    })
                }
            }
        }
    }
}