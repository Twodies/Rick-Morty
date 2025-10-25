# Rick and Morty Android App

An Android application that displays characters from the Rick and Morty series using the [Rick and Morty API](https://rickandmortyapi.com/). Built with modern Android development practices following Clean Architecture principles.

## Features

### Core Functionality
- **Character List**: Browse all characters from the Rick and Morty series in a 2-column grid layout
- **Character Details**: View detailed information about each character including:
  - Name, status, species, type, and gender
  - Origin and current location
  - Episode count
  - Character image

### Search & Filter
- **Real-time Search**: Search characters by name
- **Advanced Filters**: Filter characters by:
  - Status (Alive, Dead, Unknown)
  - Gender (Male, Female, Genderless, Unknown)
  - Species
  - Type

### Offline Support
- **Full Offline Capability**: All features work without internet connection
- **Smart Caching**: Data is cached locally using Room database
- **Seamless Experience**: Shows cached data while fetching updates

### User Experience
- **Pull-to-Refresh**: Swipe down to refresh the character list
- **Pagination**: Automatic loading of more characters as you scroll
- **Loading States**: Progress indicators during data loading
- **Error Handling**: Graceful error messages with retry options
- **Empty States**: Informative messages when no data is available
- **Back Navigation**: Proper back navigation on all screens

## Technologies Used

### Architecture & Design Patterns
- **Clean Architecture**: Separation of concerns with three layers:
  - **Presentation Layer**: ViewModels, Compose UI
  - **Domain Layer**: Use Cases, Business Models, Repository Interfaces
  - **Data Layer**: Repository Implementation, API, Database
- **MVVM Pattern**: ViewModel-driven UI updates
- **Single Source of Truth**: Room database as the single source of truth

### Android & Kotlin
- **Kotlin**: 100% Kotlin codebase
- **Jetpack Compose**: Modern declarative UI framework
- **Material Design 3**: Latest Material Design components
- **Coroutines & Flow**: Asynchronous programming and reactive streams
- **Navigation Component**: Type-safe navigation between screens

### Dependency Injection
- **Hilt**: Dagger-based dependency injection framework

### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP client with logging interceptor
- **Gson**: JSON serialization/deserialization

### Database
- **Room**: SQLite object mapping library for offline caching

### Image Loading
- **Coil**: Modern image loading library for Compose

### Additional Libraries
- **Accompanist SwipeRefresh**: Pull-to-refresh functionality
- **Lifecycle ViewModel Compose**: ViewModel integration with Compose

## Project Structure

```
com.twodies.rickmorty/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── database/         # Room Database
│   │   └── entity/           # Room Entities
│   ├── remote/
│   │   ├── api/              # Retrofit API interface
│   │   └── dto/              # Data Transfer Objects
│   └── repository/           # Repository implementations
├── domain/
│   ├── model/                # Domain models
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Use cases
├── presentation/
│   ├── characters/           # Character list screen
│   ├── detail/               # Character detail screen
│   ├── filter/               # Filter screen
│   ├── components/           # Reusable UI components
│   ├── navigation/           # Navigation setup
│   └── theme/                # Compose theme
├── di/                       # Dependency injection modules
└── util/                     # Utility classes
```

## Key Design Decisions

### 1. Clean Architecture
The app follows Clean Architecture principles to ensure:
- **Separation of Concerns**: Each layer has a specific responsibility
- **Testability**: Easy to write unit tests for business logic
- **Maintainability**: Changes in one layer don't affect others
- **Scalability**: Easy to add new features

### 2. Offline-First Approach
- **Room Database**: All data is cached locally
- **Repository Pattern**: Single source of truth pattern ensures data consistency
- **Network State Handling**: Shows cached data when network is unavailable
- **Smart Sync**: Updates cache with fresh data when available

### 3. Modern UI with Jetpack Compose
- **Declarative UI**: Easier to build and maintain UI
- **Material Design 3**: Modern, beautiful user interface
- **Reactive Updates**: UI automatically updates when data changes
- **Composable Components**: Reusable UI components

### 4. Reactive Programming
- **Kotlin Flow**: Reactive data streams from database to UI
- **State Management**: ViewModel manages UI state
- **Coroutines**: Asynchronous operations without callback hell

### 5. Type-Safe Navigation
- **Navigation Component**: Type-safe navigation between screens
- **Sealed Class Routes**: Compile-time safety for navigation routes

### 6. Pagination & Performance
- **Lazy Loading**: Load more data as user scrolls
- **Efficient Grid Layout**: 2-column grid for optimal space usage
- **Image Loading**: Coil handles image caching and loading efficiently

## API Endpoints Used

- **Get All Characters**: `GET /api/character?page={page}`
- **Get Single Character**: `GET /api/character/{id}`
- **Filter Characters**: `GET /api/character?name={name}&status={status}&species={species}&type={type}&gender={gender}`

## Requirements

- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Java Version**: 17
- **Kotlin Version**: 1.9.0

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio (Hedgehog or later)
3. Sync Gradle files
4. Run the app on an emulator or physical device

## Building the App

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## Testing

The app is designed with testability in mind:
- **Unit Tests**: Test use cases and ViewModels
- **Repository Tests**: Test repository implementations with mock data
- **UI Tests**: Test Compose UI components

## Future Enhancements

Possible improvements for future versions:
- Add support for Locations and Episodes
- Implement favorites feature
- Add character comparison
- Support for multiple languages
- Dark mode toggle
- Advanced sorting options
- Export character list

## License

This is a sample project for educational purposes.

## Acknowledgments

- [Rick and Morty API](https://rickandmortyapi.com/) for providing the free API
- Rick and Morty creators for the amazing series

---

**Note**: This app is not affiliated with Rick and Morty or its creators. It's a fan-made application for educational purposes.
