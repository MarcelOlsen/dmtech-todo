package com.example.todo

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.example.todo.ui.TodoApp
import com.example.todo.ui.theme.TodoAppTheme
import com.example.todo.viewmodel.TodoViewModel

fun main() = application {
    val viewModel = remember { TodoViewModel() }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "Todo App",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        TodoAppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                TodoApp(viewModel)
            }
        }
    }
} 