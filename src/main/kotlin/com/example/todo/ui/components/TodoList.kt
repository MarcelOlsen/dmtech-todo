package com.example.todo.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todo.model.TodoItem
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.util.UUID

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TodoList(
    title: String,
    todos: List<TodoItem>,
    emptyText: String = "No items",
    onStartClick: (UUID) -> Unit = {},
    onCompleteClick: (UUID) -> Unit = {},
    onDeleteClick: (UUID) -> Unit = {},
    onRestoreClick: (UUID) -> Unit = {},
    onEditClick: (UUID) -> Unit = {},
    maxVisibleItems: Int = Int.MAX_VALUE,
    isInDeletedSection: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (todos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            OptimizedTodoList(
                todos = todos,
                onStartClick = onStartClick,
                onCompleteClick = onCompleteClick,
                onDeleteClick = onDeleteClick,
                onRestoreClick = onRestoreClick,
                onEditClick = onEditClick,
                maxVisibleItems = maxVisibleItems,
                isInDeletedSection = isInDeletedSection
            )
        }
    }
}

@Composable
private fun OptimizedTodoList(
    todos: List<TodoItem>,
    onStartClick: (UUID) -> Unit,
    onCompleteClick: (UUID) -> Unit,
    onDeleteClick: (UUID) -> Unit,
    onRestoreClick: (UUID) -> Unit,
    onEditClick: (UUID) -> Unit,
    maxVisibleItems: Int,
    isInDeletedSection: Boolean
) {
    val listState = rememberLazyListState()
    
    // Ensure the list is scrollable when there are more items than maxVisibleItems
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = todos,
            key = { it.id }
        ) { todo ->
            // Using key with item helps prevent recomposition of items that haven't changed
            key(todo.id, todo.state, todo.updatedAt) {
                TodoItemCard(
                    todo = todo,
                    onStartClick = onStartClick,
                    onCompleteClick = onCompleteClick,
                    onDeleteClick = onDeleteClick,
                    onRestoreClick = onRestoreClick,
                    onEditClick = onEditClick,
                    isInDeletedSection = isInDeletedSection
                )
            }
        }
    }
} 