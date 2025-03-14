# MapDemo

A modern Android application that displays locations on both a map and list view, built with Kotlin and Material Design.

## Features

- Interactive map view showing locations
- List view of all locations
- Material Design UI components
- Bottom navigation for easy view switching
- Clean MVVM architecture
- Location-based functionality

## Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Components**: Material Design
- **Build System**: Gradle
- **View Binding**: AndroidX ViewBinding
- **Navigation**: Fragment-based navigation

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/example/mapdemo/
│       │   ├── api/         # Network and API related code
│       │   ├── data/        # Data models and repositories
│       │   ├── ui/          # UI components and fragments
│       │   └── util/        # Utility classes
│       └── res/             # Resources (layouts, strings, etc.)
```

## Setup Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the application on an emulator or physical device

## Requirements

- Android Studio Arctic Fox or newer
- Minimum SDK: 21 (Android 5.0)
- Target SDK: 35 (Android 15)

## Dependencies

The project uses the following main dependencies:

- AndroidX Core
- Material Design Components
- Google Maps SDK for Android
- AndroidX Navigation Components
- ViewBinding


## Security Notes
- The API key is stored in `gradle.properties` which is typically not committed to version control
- The key is injected at build time rather than being hardcoded in the source code
- Different keys can be used for different environments (development, staging, production)

  
## API Key Setup

The application uses Google Maps API and requires an API key for functionality. Here's how to set it up:

1. Get a Google Maps API key from the Google Cloud Console
2. Add the API key to `gradle.properties`:
   ```
   MAPS_API_KEY=your_api_key_here
   ```
3. The API key is automatically injected into the app during build time
4. The key is used for:
   - Google Maps initialization
   - Places API functionality
   - Location services


## Usage

1. Launch the application
2. Use the bottom navigation to switch between:
   - Map View: Displays locations on an interactive map
   - List View: Shows locations in a scrollable list

Thanks

