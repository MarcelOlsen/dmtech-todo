package com.example.todo.repository

import com.example.todo.model.TodoItem
import com.example.todo.model.TodoState
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoItem>>

    fun getTodosByState(state: TodoState): Flow<List<TodoItem>>

    suspend fun getTodoById(id: UUID): TodoItem?

    suspend fun addTodo(todo: TodoItem)

    suspend fun updateTodo(todo: TodoItem)

    suspend fun deleteTodoPermanently(id: UUID)

    suspend fun markAsDeleted(id: UUID)

    suspend fun restoreTodo(id: UUID)
} 