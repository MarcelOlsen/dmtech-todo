package com.example.todo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todo.model.TodoItem
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun EditTodoDialog(
    todo: TodoItem?,
    onSave: (title: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isNewTodo = todo == null
    val dialogTitle = if (isNewTodo) "Add Task" else "Edit Task"
    val buttonText = if (isNewTodo) "ADD TASK" else "SAVE CHANGES"
    
    var title by remember(todo) { mutableStateOf(todo?.title ?: "") }
    var description by remember(todo) { mutableStateOf(todo?.description ?: "") }
    val focusRequester = remember { FocusRequester() }
    
    // Auto-focus the title field when dialog opens
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
    
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colors.surface,
            elevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dialogTitle,
                        style = MaterialTheme.typography.h5.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Title input with better focus handling
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { 
                        Text(
                            "Enter task title",
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary,
                        unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        cursorColor = MaterialTheme.colors.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description input with better styling
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { 
                        Text(
                            "Add details about your task",
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colors.primary,
                        unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        cursorColor = MaterialTheme.colors.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("CANCEL")
                    }
                    
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(title.trim(), description.trim())
                            }
                        },
                        enabled = title.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            disabledBackgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buttonText,
                            style = MaterialTheme.typography.button
                        )
                    }
                }
            }
        }
    }
} 