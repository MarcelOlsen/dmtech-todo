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
import androidx.compose.material.icons.filled.*
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
import com.example.todo.viewmodel.TodoFilter
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
    var showFilterDialog by remember { mutableStateOf(false) }
    val currentFilter by viewModel.currentFilter.collectAsState()
    
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
    val filteredTodos by viewModel.filteredTodos.collectAsState()
    
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

    // Filter dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter Tasks") },
            text = {
                Column {
                    FilterOption(
                        text = "All Tasks",
                        selected = currentFilter == TodoFilter.ALL,
                        onClick = { 
                            viewModel.setFilter(TodoFilter.ALL)
                            showFilterDialog = false
                        }
                    )
                    FilterOption(
                        text = "New Tasks",
                        selected = currentFilter == TodoFilter.NEW,
                        onClick = { 
                            viewModel.setFilter(TodoFilter.NEW)
                            showFilterDialog = false
                        }
                    )
                    FilterOption(
                        text = "In Progress",
                        selected = currentFilter == TodoFilter.IN_PROGRESS,
                        onClick = { 
                            viewModel.setFilter(TodoFilter.IN_PROGRESS)
                            showFilterDialog = false
                        }
                    )
                    FilterOption(
                        text = "Completed",
                        selected = currentFilter == TodoFilter.DONE,
                        onClick = { 
                            viewModel.setFilter(TodoFilter.DONE)
                            showFilterDialog = false
                        }
                    )
                    FilterOption(
                        text = "Deleted",
                        selected = currentFilter == TodoFilter.DELETED,
                        onClick = { 
                            viewModel.setFilter(TodoFilter.DELETED)
                            showFilterDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Close")
                }
            }
        )
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
                    // Filter button with badge showing current filter
                    IconButton(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Tasks",
                                tint = MaterialTheme.colors.primary
                            )
                            // Show a badge dot indicating active filter
                            if (currentFilter != TodoFilter.ALL) {
                                Badge(
                                    backgroundColor = MaterialTheme.colors.primary,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 4.dp, end = 4.dp)
                                        .size(8.dp)
                                ) {}
                            }
                        }
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
            if (filteredTodos.isEmpty() && currentFilter != TodoFilter.DELETED) {
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
                        text = getEmptyMessageForFilter(currentFilter),
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (currentFilter == TodoFilter.ALL) "Tap + to add your first task" else "",
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
                        // Show current filter chip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Showing: ",
                                style = MaterialTheme.typography.subtitle2,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.clickable { showFilterDialog = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = getFilterName(currentFilter),
                                        style = MaterialTheme.typography.body2,
                                        color = MaterialTheme.colors.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Change Filter",
                                        tint = MaterialTheme.colors.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        
                        // Task counters with improved visuals
                        TaskCounters(
                            newCount = newTodos.size,
                            inProgressCount = inProgressTodos.size,
                            completedCount = doneTodos.size,
                            deletedCount = deletedTodos.size,
                            currentFilter = currentFilter,
                            onFilterChange = { viewModel.setFilter(it) }
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
                            // Display filtered todos in the list
                            TodoList(
                                title = "",
                                todos = filteredTodos,
                                emptyText = "",
                                onStartClick = { viewModel.startTodo(it) },
                                onCompleteClick = { viewModel.completeTodo(it) },
                                onDeleteClick = { viewModel.deleteTodo(it) },
                                onRestoreClick = { viewModel.restoreTodo(it) },
                                onEditClick = { id ->
                                    editingTodoItem = filteredTodos.find { it.id == id }
                                },
                                isInDeletedSection = currentFilter == TodoFilter.DELETED
                            )
                        }
                        
                        // Trash section at the bottom - only show when not already filtering by deleted
                        if (deletedTodos.isNotEmpty() && currentFilter != TodoFilter.DELETED) {
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

@Composable
private fun FilterOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colors.primary
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
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
    deletedCount: Int,
    currentFilter: TodoFilter,
    onFilterChange: (TodoFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // New tasks counter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onFilterChange(TodoFilter.NEW) }
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (currentFilter == TodoFilter.NEW) MaterialTheme.colors.primary else MaterialTheme.colors.primary.copy(alpha = 0.6f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "New ($newCount)",
                style = MaterialTheme.typography.caption,
                fontWeight = if (currentFilter == TodoFilter.NEW) FontWeight.Bold else FontWeight.Medium,
                color = if (currentFilter == TodoFilter.NEW) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // In progress counter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onFilterChange(TodoFilter.IN_PROGRESS) }
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (currentFilter == TodoFilter.IN_PROGRESS) MaterialTheme.colors.secondary else MaterialTheme.colors.secondary.copy(alpha = 0.6f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "In Progress ($inProgressCount)",
                style = MaterialTheme.typography.caption,
                fontWeight = if (currentFilter == TodoFilter.IN_PROGRESS) FontWeight.Bold else FontWeight.Medium,
                color = if (currentFilter == TodoFilter.IN_PROGRESS) MaterialTheme.colors.secondary else MaterialTheme.colors.onSurface
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Completed counter
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onFilterChange(TodoFilter.DONE) }
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (currentFilter == TodoFilter.DONE) Color(0xFF757575) else Color(0xFF757575).copy(alpha = 0.6f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Completed ($completedCount)",
                style = MaterialTheme.typography.caption,
                fontWeight = if (currentFilter == TodoFilter.DONE) FontWeight.Bold else FontWeight.Medium,
                color = if (currentFilter == TodoFilter.DONE) Color(0xFF757575) else MaterialTheme.colors.onSurface
            )
        }
    }
}

// Helper function to get filter display name
private fun getFilterName(filter: TodoFilter): String {
    return when (filter) {
        TodoFilter.ALL -> "All Tasks"
        TodoFilter.NEW -> "New Tasks"
        TodoFilter.IN_PROGRESS -> "In Progress"
        TodoFilter.DONE -> "Completed"
        TodoFilter.DELETED -> "Deleted"
    }
}

// Helper function to get empty state message based on current filter
private fun getEmptyMessageForFilter(filter: TodoFilter): String {
    return when (filter) {
        TodoFilter.ALL -> "No tasks yet"
        TodoFilter.NEW -> "No new tasks"
        TodoFilter.IN_PROGRESS -> "No tasks in progress"
        TodoFilter.DONE -> "No completed tasks"
        TodoFilter.DELETED -> "No deleted tasks"
    }
} 