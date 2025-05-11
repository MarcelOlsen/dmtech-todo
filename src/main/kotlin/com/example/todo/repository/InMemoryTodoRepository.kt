package com.example.todo.repository

import com.example.todo.model.TodoItem
import com.example.todo.model.TodoState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID

class InMemoryTodoRepository : TodoRepository {
    // Using MutableStateFlow to store the list of todos and emit updates
    private val _todos = MutableStateFlow<MutableMap<UUID, TodoItem>>(mutableMapOf())
    
    override fun getAllTodos(): Flow<List<TodoItem>> {
        return _todos.map { it.values.toList() }
    }
    
    override fun getTodosByState(state: TodoState): Flow<List<TodoItem>> {
        return _todos.map { map -> 
            map.values.filter { it.state == state }
        }
    }
    
    override suspend fun getTodoById(id: UUID): TodoItem? {
        return _todos.value[id]
    }
    
    override suspend fun addTodo(todo: TodoItem) {
        val updatedMap = _todos.value.toMutableMap()
        updatedMap[todo.id] = todo
        _todos.value = updatedMap
    }
    
    override suspend fun updateTodo(todo: TodoItem) {
        val updatedMap = _todos.value.toMutableMap()
        updatedMap[todo.id] = todo
        _todos.value = updatedMap
    }
    
    override suspend fun deleteTodoPermanently(id: UUID) {
        val updatedMap = _todos.value.toMutableMap()
        updatedMap.remove(id)
        _todos.value = updatedMap
    }
    
    override suspend fun markAsDeleted(id: UUID) {
        val todo = _todos.value[id] ?: return
        val updatedTodo = todo.updateState(TodoState.DELETED)
        updateTodo(updatedTodo)
    }
    
    override suspend fun restoreTodo(id: UUID) {
        val todo = _todos.value[id] ?: return
        if (todo.state == TodoState.DELETED) {
            val updatedTodo = todo.copy(
                state = TodoState.IN_PROGRESS,
                updatedAt = LocalDateTime.now(),
                deletedAt = null
            )
            updateTodo(updatedTodo)
        }
    }
} 