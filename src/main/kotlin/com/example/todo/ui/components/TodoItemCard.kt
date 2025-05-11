package com.example.todo.ui.components

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
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todo.model.TodoItem
import com.example.todo.model.TodoState
import com.example.todo.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TodoItemCard(
    todo: TodoItem,
    onStartClick: (UUID) -> Unit,
    onCompleteClick: (UUID) -> Unit,
    onDeleteClick: (UUID) -> Unit,
    onRestoreClick: (UUID) -> Unit,
    onEditClick: (UUID) -> Unit,
    isInDeletedSection: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val backgroundColor = when (todo.state) {
        TodoState.NEW -> NewTaskColor
        TodoState.IN_PROGRESS -> InProgressTaskColor
        TodoState.DONE -> CompletedTaskColor
        TodoState.DELETED -> DeletedTaskColor
    }

    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
    )

    val scale by animateFloatAsState(
        targetValue = if (todo.state == TodoState.DELETED) 0.98f else 1f,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 100)
    )

    val textColor = when {
        isInDeletedSection -> MaterialTheme.colors.error.copy(alpha = 0.95f)
        todo.state == TodoState.NEW -> Color.White.copy(alpha = 0.95f)
        todo.state == TodoState.IN_PROGRESS -> Color.White.copy(alpha = 0.95f)
        todo.state == TodoState.DONE -> Color.White.copy(alpha = 0.75f)
        todo.state == TodoState.DELETED -> Color.White.copy(alpha = 0.9f)
        else -> Color.White
    }
    
    val textDecoration = when {
        todo.state == TodoState.DONE -> TextDecoration.LineThrough
        else -> null
    }
    
    // Additional styling for deleted section cards
    val cardModifier = if (isInDeletedSection) {
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
                )
            )
            .hoverable(interactionSource)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .shadow(elevation = elevation)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.error.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
    } else {
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = FastOutSlowInEasing
                )
            )
            .hoverable(interactionSource)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .shadow(elevation = elevation)
    }
    
    Card(
        modifier = cardModifier,
        elevation = 0.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = animatedColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isInDeletedSection -> MaterialTheme.colors.error
                                todo.state == TodoState.NEW -> MaterialTheme.colors.primary
                                todo.state == TodoState.IN_PROGRESS -> MaterialTheme.colors.secondary
                                todo.state == TodoState.DONE -> Color(0xFF757575)
                                todo.state == TodoState.DELETED -> MaterialTheme.colors.error
                                else -> MaterialTheme.colors.primary
                            }
                        )
                )
                
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.h6,
                    textDecoration = textDecoration,
                    color = textColor,
                    fontWeight = if (isInDeletedSection) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                AnimatedVisibility(
                    visible = isHovered || todo.state == TodoState.DELETED,
                    enter = fadeIn(animationSpec = tween(100)) + expandHorizontally(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(100)) + shrinkHorizontally(animationSpec = tween(150))
                ) {
                    Row {
                        when (todo.state) {
                            TodoState.NEW -> {
                                IconButton(
                                    onClick = { onStartClick(todo.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Start",
                                        tint = MaterialTheme.colors.primary
                                    )
                                }
                            }
                            TodoState.IN_PROGRESS -> {
                                IconButton(
                                    onClick = { onCompleteClick(todo.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Complete",
                                        tint = MaterialTheme.colors.secondary
                                    )
                                }
                            }
                            TodoState.DONE -> {
                                // No special action for DONE state
                            }
                            TodoState.DELETED -> {
                                IconButton(
                                    onClick = { onRestoreClick(todo.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restore,
                                        contentDescription = "Restore",
                                        tint = if (isInDeletedSection) MaterialTheme.colors.error else MaterialTheme.colors.primary
                                    )
                                }
                            }
                        }
                        
                        // Edit action for non-deleted todos
                        if (todo.state != TodoState.DELETED) {
                            IconButton(
                                onClick = { onEditClick(todo.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                        
                        // Delete action for all except DELETED states
                        if (todo.state != TodoState.DELETED) {
                            IconButton(
                                onClick = { onDeleteClick(todo.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colors.error
                                )
                            }
                        }
                    }
                }
            }

            if (todo.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = todo.description,
                    style = MaterialTheme.typography.body2,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    textDecoration = textDecoration,
                    color = textColor.copy(alpha = if (isInDeletedSection) 0.9f else 0.85f), // Higher contrast for dark mode
                    fontWeight = if (isInDeletedSection) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.padding(start = 22.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Divider(
                color = if (isInDeletedSection) 
                    MaterialTheme.colors.error.copy(alpha = 0.2f) 
                else 
                    MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val formatter = DateTimeFormatter.ofPattern("d MMM, HH:mm")

                Text(
                    text = "Created: ${todo.createdAt.format(formatter)}",
                    style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
                    color = if (isInDeletedSection) 
                        MaterialTheme.colors.error.copy(alpha = 0.9f) 
                    else 
                        MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                )
                
                when (todo.state) {
                    TodoState.DONE -> todo.completedAt?.let {
                        Text(
                            text = "Completed: ${it.format(formatter)}",
                            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                        )
                    }
                    TodoState.DELETED -> todo.deletedAt?.let {
                        Text(
                            text = "Deleted: ${it.format(formatter)}",
                            style = MaterialTheme.typography.caption.copy(
                                fontWeight = if (isInDeletedSection) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isInDeletedSection) 
                                MaterialTheme.colors.error
                            else 
                                MaterialTheme.colors.error.copy(alpha = 0.9f)
                        )
                    }
                    else -> { /* No additional dates to show */ }
                }
            }
        }
    }
} 