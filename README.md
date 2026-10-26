# 🎧 WavePlayer

**A modern offline music player for Android.**

WavePlayer is a native Android music player project focused on bringing together beautiful visuals, smooth animations, and a feature-rich offline listening experience. Built with Kotlin and Jetpack Compose, the goal is to make music playback feel as good as it sounds.

> 🚧 **Status:** Under active development. Features and UI are evolving, and some functionality may not yet be fully implemented or tested.

## ✨ Vision & Features

### 🎵 Music Playback

* Offline playback of locally stored music.
* Background playback and media notification controls.
* Queue management, seeking, shuffle, and repeat.
* Album artwork and track information.
* Gapless playback support where applicable.

### 🎨 Modern Interface

* **Liquid Glass:** Translucent surfaces, depth, highlights, and floating navigation.
* **Material Expressive:** A distinct Material-inspired visual experience.
* Dynamic Colour support.
* Light, dark, and AMOLED appearance options.
* Smooth transitions between the mini-player and full player.
* Animated controls and responsive layouts.

### 🎚️ Sound Lab

* Equalizer and audio enhancement controls.
* Bass boost and audio presets where supported.
* Preamp controls where supported.
* Audio visualizer designed to respond to playback data when available.

*Audio effects depend on Android's audio-effect APIs and device compatibility.*

### 📚 Music Library

* Browse locally available music.
* Search and organize your music collection.
* Browse by supported categories such as songs, albums, artists, and folders.
* Like or favourite tracks.
* Create and manage playlists.

### 🛠️ Additional Features

* Lyrics support.
* Sleep timer.
* Playlist backup and restore.
* Persistent settings and playback preferences.

*The list describes the project's intended feature set. Individual features may still be under development.*

## 🧰 Tech Stack

| Technology               | Purpose                              |
| ------------------------ | ------------------------------------ |
| Kotlin                   | Primary programming language         |
| Jetpack Compose          | Declarative UI                       |
| Material 3               | UI components and design system      |
| Media3 ExoPlayer         | Audio playback                       |
| Room                     | Local structured data, where needed  |
| DataStore                | Persistent preferences               |
| MediaStore               | Access to locally stored music       |
| Kotlin Coroutines & Flow | Asynchronous work and reactive state |

## 📱 Platform

* **Platform:** Android
* **Language:** Kotlin
* **UI:** Jetpack Compose
* **Playback:** AndroidX Media3
* **Development environment:** Android Studio

The minimum Android version and SDK requirements will be documented once finalized in the project configuration.

## 🚀 Getting Started

### Prerequisites

* Android Studio compatible with the project's Gradle and Android Gradle Plugin versions.
* A compatible JDK.
* Android SDK components required by the project.
* An Android device or emulator for testing.

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/YOUR_USERNAME/WavePlayer.git
   ```

2. Open the cloned project in Android Studio.

3. Allow Gradle to sync and download the required dependencies.

4. Connect an Android device or start an emulator.

5. Run the `app` configuration from Android Studio.

Alternatively, build the debug APK from the project root:

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

The APK is normally generated under:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 🏗️ Project Structure

The project follows a native Android application structure. The exact packages and modules may evolve during development.

```text
WavePlayer/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/       # Kotlin source code
│   │       ├── res/        # Android resources
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
└── README.md
```

## 🔒 Privacy & Permissions

WavePlayer is designed primarily for local music playback.

* Access to local audio may require Android media permissions.
* Audio effects and background playback use Android platform capabilities.
* Network access should only be required for features that explicitly need it, such as optional online lyrics or metadata services.

Review the app's actual permissions and implementation as development progresses.

## 🧪 Testing

The project should be tested across supported Android versions and device configurations.

Important areas include:

* Library scanning and media permissions.
* Playback, seeking, queue, shuffle, and repeat.
* Background playback and notification controls.
* Mini-player and full-player transitions.
* Theme switching and settings persistence.
* Playlists, favourites, and backup/restore.
* Sound Lab, lyrics, sleep timer, and visualizer.
* Performance, lifecycle handling, and error states.

A feature should be considered complete only after its behaviour has been verified.

## 🗺️ Roadmap

* [ ] Finalize the music library experience.
* [ ] Perfect full-player and mini-player shared-element transitions.
* [ ] Add polished, responsive spring animations.
* [ ] Complete Liquid Glass and Material Expressive themes.
* [ ] Refine Sound Lab and playback visualization.
* [ ] Verify playlists, favourites, and backup/restore.
* [ ] Test all implemented features and resolve regressions.
* [ ] Improve accessibility, performance, and edge-case handling.

## 🤝 Contributing

WavePlayer is a work in progress. Contributions, bug reports, and suggestions are welcome.

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Test your changes.
5. Submit a pull request describing the implementation and test results.

## 📄 License

No license has been specified yet. Until a license is added to the repository, reuse and redistribution permissions are not explicitly granted.

## 💜 About

WavePlayer is an independent Android music player project exploring the intersection of offline music, expressive design, and smooth native interactions.

**Built for the love of music and the joy of building.** 🎶
