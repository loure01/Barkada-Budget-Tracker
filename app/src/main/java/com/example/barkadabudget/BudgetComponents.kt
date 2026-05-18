package com.example.barkadabudget.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TransactionForm(
    payerName: TextFieldValue,
    onPayerNameChange: (TextFieldValue) -> Unit,
    description: TextFieldValue,
    onDescriptionChange: (TextFieldValue) -> Unit,
    amount: TextFieldValue,
    onAmountChange: (TextFieldValue) -> Unit,
    onAddClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = payerName, onValueChange = onPayerNameChange, label = { Text("Payer Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = onDescriptionChange, label = { Text("Item Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = amount, onValueChange = onAmountChange, label = { Text("Amount Paid (₱)") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = onAddClick, modifier = Modifier.align(Alignment.End)) { Text("Add Expense") }
        }
    }
}

@Composable
fun HistoryList(
    expenseList: List<BudgetExpenseItem>,
    onDeleteClick: (Int) -> Unit
) {
    Text(text = "Transaction Records", style = MaterialTheme.typography.titleLarge)
    LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(expenseList) { item ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.payer_name, style = MaterialTheme.typography.titleMedium)
                        Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "₱${item.amount}", style = MaterialTheme.typography.titleLarge)
                        IconButton(onClick = { item.id?.let { onDeleteClick(it) } }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }
        }
    }
}