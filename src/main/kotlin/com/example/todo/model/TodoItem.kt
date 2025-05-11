package com.example.todo.model

import java.time.LocalDateTime
import java.util.UUID

enum class TodoState {
    NEW,
    IN_PROGRESS,
    DONE,
    DELETED
}

data class TodoItem(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String = "",
    val state: TodoState = TodoState.NEW,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
) {
    fun updateState(newState: TodoState): TodoItem {
        return when (newState) {
            TodoState.DONE -> this.copy(
                state = newState,
                updatedAt = LocalDateTime.now(),
                completedAt = LocalDateTime.now()
            )
            TodoState.DELETED -> this.copy(
                state = newState,
                updatedAt = LocalDateTime.now(),
                deletedAt = LocalDateTime.now()
            )
            else -> this.copy(
                state = newState,
                updatedAt = LocalDateTime.now()
            )
        }
    }

    fun updateTitle(newTitle: String): TodoItem {
        return this.copy(
            title = newTitle,
            updatedAt = LocalDateTime.now()
        )
    }

    fun updateDescription(newDescription: String): TodoItem {
        return this.copy(
            description = newDescription,
            updatedAt = LocalDateTime.now()
        )
    }
} 