package com.example.todo.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todo.model.TodoItem
import com.example.todo.model.TodoState
import com.example.todo.ui.components.*
import com.example.todo.ui.theme.DeletedTaskColor
import com.example.todo.viewmodel.TodoViewModel

/**
 * Main TodoApp composable with improved performance and modern UI
 */
@Composable
fun TodoApp(viewModel: TodoViewModel) {
    // Track dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodoItem by remember { mutableStateOf<TodoItem?>(null) }
    
    // Filter state
    var showCompletedTasks by remember { mutableStateOf(true) }
    
    // State for the recently deleted section
    var isDeletedSectionExpanded by remember { mutableStateOf(false) }
    val deleteInteractionSource = remember { MutableInteractionSource() }
    val isDeletedSectionHovered by deleteInteractionSource.collectIsHoveredAsState()
    
    // Auto-expand/collapse the deleted section based on hover state
    LaunchedEffect(isDeletedSectionHovered) {
        isDeletedSectionExpanded = isDeletedSectionHovered
    }
    
    // Collect flows from viewModel - use derivedStateOf for better performance
    val newTodos by viewModel.newTodos.collectAsState()
    val inProgressTodos by viewModel.inProgressTodos.collectAsState()
    val doneTodos by viewModel.doneTodos.collectAsState()
    val deletedTodos by viewModel.deletedTodos.collectAsState()
    
    // Filter visible todos based on user preference with derived state
    val allVisibleTodos by remember(newTodos, inProgressTodos, doneTodos, showCompletedTasks) {
        derivedStateOf {
            val activeTodos = (newTodos + inProgressTodos)
            val displayTodos = if (showCompletedTasks) {
                activeTodos + doneTodos
            } else {
                activeTodos
            }
            displayTodos.sortedByDescending { it.updatedAt }
        }
    }
    
    // Constants for drawer calculations
    val taskCardHeight = 95.dp // Increased task card height for better visibility
    val headerHeight = 56.dp // Height of the header
    val sectionVerticalPadding = 16.dp // Top and bottom padding for the section
    val cardVerticalPadding = 6.dp // Vertical padding per card
    val spacingBetweenFabAndDrawer = 24.dp // Space between FAB and drawer
    
    // Calculate the height of the deleted section when expanded
    // Always enough height for 3 tasks, or the actual number if less
    val deletedSectionHeight = if (deletedTodos.isEmpty()) {
        0.dp
    } else if (isDeletedSectionExpanded) {
        // Calculate height for 3 tasks or actual number if less
        val tasksToShow = minOf(3, deletedTodos.size)
        // Header + (task height + vertical padding per card) × number of tasks + section padding
        headerHeight + ((taskCardHeight + (cardVerticalPadding * 2)) * tasksToShow) + sectionVerticalPadding
    } else {
        // Just enough height for the header when collapsed
        headerHeight
    }
    
    // Calculate the FAB offset based on deleted section state - add extra spacing
    val fabOffset by animateDpAsState(
        targetValue = if (isDeletedSectionExpanded && deletedTodos.isNotEmpty()) 
            deletedSectionHeight + spacingBetweenFabAndDrawer else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )
    
    // Show edit dialog when needed
    LaunchedEffect(editingTodoItem) {
        if (editingTodoItem != null) {
            // The EditTodoDialog will be displayed when editingTodoItem is not null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "TaskFlow",
                        style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                actions = {
                    // Toggle filter for completed tasks with better visual feedback
                    IconButton(
                        onClick = { showCompletedTasks = !showCompletedTasks },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Badge(
                            backgroundColor = if (showCompletedTasks) MaterialTheme.colors.primary else Color.Gray,
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(8.dp)
                        ) { }
                        
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Toggle Completed Tasks",
                            tint = if (showCompletedTasks) 
                                MaterialTheme.colors.primary 
                            else 
                                MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Fixed FAB positioned with animated offset
            FloatingActionButton(
                onClick = { showAddDialog = true },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                ),
                modifier = Modifier
                    .offset(y = -fabOffset)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Todo"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        backgroundColor = MaterialTheme.colors.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (allVisibleTodos.isEmpty() && deletedTodos.isEmpty()) {
                // Empty state with nicer visuals
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "No tasks yet",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tap + to add your first task",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                // Use BoxWithConstraints for proper layout management
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        // Task counters with improved visuals
                        TaskCounters(
                            newCount = newTodos.size,
                            inProgressCount = inProgressTodos.size,
                            completedCount = doneTodos.size,
                            showCompleted = showCompletedTasks
                        )
                        
                        Divider(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.06f),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        
                        // Main content area with weight
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            // Display all visible todos in a single list
                            TodoList(
                                title = "",
                                todos = allVisibleTodos,
                                emptyText = "",
                                onStartClick = { viewModel.startTodo(it) },
                                onCompleteClick = { viewModel.completeTodo(it) },
                                onDeleteClick = { viewModel.deleteTodo(it) },
                                onEditClick = { id ->
                                    editingTodoItem = allVisibleTodos.find { it.id == id }
                                }
                            )
                        }
                        
                        // Trash section at the bottom
                        if (deletedTodos.isNotEmpty()) {
                            // Collapsible trash section with animation
                            Surface(
                                color = DeletedTaskColor,
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                                elevation = 4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .hoverable(deleteInteractionSource)
                                    .animateContentSize(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                    .height(deletedSectionHeight)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colors.error.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                    )
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    // Header with expand/collapse functionality
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                            .clickable { isDeletedSectionExpanded = !isDeletedSectionExpanded },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = MaterialTheme.colors.error,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Recently Deleted (${deletedTodos.size})",
                                                style = MaterialTheme.typography.subtitle1.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colors.error
                                            )
                                        }
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            // Clear all button - only visible when expanded
                                            AnimatedVisibility(
                                                visible = isDeletedSectionExpanded,
                                                enter = fadeIn() + expandHorizontally(),
                                                exit = fadeOut() + shrinkHorizontally()
                                            ) {
                                                TextButton(
                                                    onClick = { viewModel.clearDeleted() },
                                                    colors = ButtonDefaults.textButtonColors(
                                                        contentColor = MaterialTheme.colors.error
                                                    )
                                                ) {
                                                    Text(
                                                        text = "Clear All",
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            
                                            // Expand/collapse indicator
                                            Icon(
                                                imageVector = if (isDeletedSectionExpanded) 
                                                    Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = if (isDeletedSectionExpanded) 
                                                    "Collapse" else "Expand",
                                                tint = MaterialTheme.colors.error
                                            )
                                        }
                                    }
                                    
                                    // Only show deleted items when expanded
                                    AnimatedVisibility(
                                        visible = isDeletedSectionExpanded,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        // Expanded content with full height for 3 items
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp)
                                                .weight(1f) // Take all available space
                                        ) {
                                            TodoList(
                                                title = "",
                                                todos = deletedTodos,
                                                emptyText = "",
                                                onRestoreClick = { viewModel.restoreTodo(it) },
                                                maxVisibleItems = 3, // Limit visible items to 3
                                                isInDeletedSection = true // Flag for special styling
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Add dialog
            if (showAddDialog) {
                EditTodoDialog(
                    todo = null,
                    onSave = { title, description ->
                        viewModel.addTodo(title, description)
                        showAddDialog = false
                    },
                    onDismiss = { showAddDialog = false }
                )
            }
            
            // Edit dialog
            editingTodoItem?.let { todo ->
                EditTodoDialog(
                    todo = todo,
                    onSave = { title, description ->
                        viewModel.updateTodo(todo.id, title, description)
                        editingTodoItem = null
                    },
                    onDismiss = { editingTodoItem = null }
                )
            }
        }
    }
}

/**
 * Task counter component extracted for better readability
 */
@Composable
private fun TaskCounters(
    newCount: Int,
    inProgressCount: Int,
    completedCount: Int,
    showCompleted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // New tasks counter
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "New ($newCount)",
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // In progress counter
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "In Progress ($inProgressCount)",
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Only show completed counter if it's visible
        if (showCompleted) {
            Spacer(modifier = Modifier.width(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF757575))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Completed ($completedCount)",
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 