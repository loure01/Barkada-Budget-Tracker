package com.example.barkadabudget.budget

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    isRegister: Boolean,
    usernameValue: TextFieldValue,
    onUsernameChange: (TextFieldValue) -> Unit,
    passwordValue: TextFieldValue,
    onPasswordChange: (TextFieldValue) -> Unit,
    message: String,
    onPrimaryButtonClick: () -> Unit,
    onToggleScreenClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(32.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(if (isRegister) "Create Account" else "Barkada Budget Tracker", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = usernameValue, onValueChange = onUsernameChange, label = { Text(if (isRegister) "Username" else "Username") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = passwordValue, onValueChange = onPasswordChange, label = { Text(if (isRegister) "Password" else "Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            if (message.isNotBlank()) { Text(message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 12.dp)) }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onPrimaryButtonClick, modifier = Modifier.fillMaxWidth()) { Text(if (isRegister) "Register Account" else "Login") }
            TextButton(onClick = onToggleScreenClick) { Text(if (isRegister) "Back to Login" else "Don't have an account? Register") }
        }
    }
}