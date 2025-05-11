# TaskFlow - Todo App

A modern desktop Todo application built with Kotlin and Jetpack Compose.

## Features

- ✅ Create, edit and manage tasks with customizable details
- 🔄 Track task status (New, In Progress, Done)
- 🗑️ Soft-delete management with restore capability
- 📱 Pleasant and responsive user interface

## Technologies Used

- **Kotlin** - Modern JVM language with coroutines
- **Jetpack Compose for Desktop** - Declarative UI framework
- **MVVM Architecture** - Clean separation of concerns
- **Kotlin Flow** - Reactive state management

## Getting Started

### Prerequisites

- JDK 11 or higher
- Gradle 7.0+ (wrapper included)

### Running the Application

Clone the repository and navigate to the project directory:

```bash
git clone https://github.com/MarcelOlsen/dmtech-todo.git
cd dmtech-todo
```

Run the application with Gradle:

```bash
./gradlew run
```

## Project Structure

The application follows the MVVM architecture pattern:

- **Model**: Data structures and business logic
- **View**: Compose UI elements and styling
- **ViewModel**: State management and UI logic

Key components:

```
src/main/kotlin/com/example/todo/
├── model/          # Data models
├── repository/     # Data handling
├── viewmodel/      # UI state and logic
├── ui/             # UI components
│   ├── components/ # Reusable UI elements
│   └── theme/      # Styling and theming
└── Main.kt         # Application entry point
```
