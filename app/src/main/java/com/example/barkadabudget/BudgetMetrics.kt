package com.example.barkadabudget.budget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetMetrics(
    totalExpense: Double,
    sharePerPerson: Double,
    settlements: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Financial Breakdown", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Pool Spent:")
                    Text("₱${String.format("%.2f", totalExpense)}", style = MaterialTheme.typography.titleMedium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Fair Share Split:")
                    Text("₱${String.format("%.2f", sharePerPerson)} / person", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (settlements.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Settle-Up Ledger (Who Owes Who)", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(10.dp))
                    settlements.forEach { instruction ->
                        Text("• $instruction", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}