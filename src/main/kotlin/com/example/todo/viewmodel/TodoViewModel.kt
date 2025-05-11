package com.example.todo.viewmodel

import com.example.todo.model.TodoItem
import com.example.todo.model.TodoState
import com.example.todo.repository.InMemoryTodoRepository
import com.example.todo.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

// Define a set of filter options
enum class TodoFilter {
    ALL,
    NEW,
    IN_PROGRESS,
    DONE,
    DELETED
}

class TodoViewModel(
    private val repository: TodoRepository = InMemoryTodoRepository(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    // Current filter setting
    private val _currentFilter = MutableStateFlow(TodoFilter.ALL)
    val currentFilter: StateFlow<TodoFilter> = _currentFilter

    // Expose flows for each type of todo based on state
    val newTodos: StateFlow<List<TodoItem>> = repository.getTodosByState(TodoState.NEW)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    
    val inProgressTodos: StateFlow<List<TodoItem>> = repository.getTodosByState(TodoState.IN_PROGRESS)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    
    val doneTodos: StateFlow<List<TodoItem>> = repository.getTodosByState(TodoState.DONE)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    
    val deletedTodos: StateFlow<List<TodoItem>> = repository.getTodosByState(TodoState.DELETED)
        .stateIn(scope, SharingStarted.Eagerly, emptyList())
    
    // Combine all non-deleted todos for main display
    val activeTodos: StateFlow<List<TodoItem>> = combine(
        newTodos,
        inProgressTodos,
        doneTodos
    ) { new, inProgress, done ->
        new + inProgress + done
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())
    
    // Filtered todos based on current filter
    val filteredTodos: StateFlow<List<TodoItem>> = combine(
        currentFilter,
        newTodos,
        inProgressTodos,
        doneTodos,
        deletedTodos
    ) { filter, new, inProgress, done, deleted ->
        when (filter) {
            TodoFilter.ALL -> new + inProgress + done
            TodoFilter.NEW -> new
            TodoFilter.IN_PROGRESS -> inProgress
            TodoFilter.DONE -> done
            TodoFilter.DELETED -> deleted
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    // Set the current filter
    fun setFilter(filter: TodoFilter) {
        _currentFilter.value = filter
    }

    fun addTodo(title: String, description: String = "") {
        if (title.isBlank()) return
        
        val newTodo = TodoItem(
            title = title,
            description = description
        )
        
        scope.launch {
            repository.addTodo(newTodo)
        }
    }

    fun updateTodo(id: UUID, title: String, description: String = "") {
        if (title.isBlank()) return
        
        scope.launch {
            val todo = repository.getTodoById(id) ?: return@launch
            val updatedTodo = todo.copy(
                title = title,
                description = description,
                updatedAt = java.time.LocalDateTime.now()
            )
            repository.updateTodo(updatedTodo)
        }
    }

    fun updateTodoState(id: UUID, newState: TodoState) {
        scope.launch {
            val todo = repository.getTodoById(id) ?: return@launch
            val updatedTodo = todo.updateState(newState)
            repository.updateTodo(updatedTodo)
        }
    }

    fun startTodo(id: UUID) {
        updateTodoState(id, TodoState.IN_PROGRESS)
    }

    fun completeTodo(id: UUID) {
        updateTodoState(id, TodoState.DONE)
    }

    fun deleteTodo(id: UUID) {
        scope.launch {
            repository.markAsDeleted(id)
        }
    }

    fun restoreTodo(id: UUID) {
        scope.launch {
            repository.restoreTodo(id)
        }
    }

    fun permanentlyDeleteTodo(id: UUID) {
        scope.launch {
            repository.deleteTodoPermanently(id)
        }
    }

    fun clearDeleted() {
        scope.launch {
            deletedTodos.value.forEach { todo ->
                repository.deleteTodoPermanently(todo.id)
            }
        }
    }

    fun updateTodoTitle(id: UUID, newTitle: String) {
        if (newTitle.isBlank()) return
        
        scope.launch {
            val todo = repository.getTodoById(id) ?: return@launch
            val updatedTodo = todo.updateTitle(newTitle)
            repository.updateTodo(updatedTodo)
        }
    }

    fun updateTodoDescription(id: UUID, newDescription: String) {
        scope.launch {
            val todo = repository.getTodoById(id) ?: return@launch
            val updatedTodo = todo.updateDescription(newDescription)
            repository.updateTodo(updatedTodo)
        }
    }
} 