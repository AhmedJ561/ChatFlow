# ChatFlow

A modern Java-based chat application designed for seamless communication and real-time messaging.

## 📋 Overview

ChatFlow is a Java-based chat application built with a focus on performance and scalability. This project provides a foundation for building interactive messaging systems.

## 🎯 Features

- **Real-time Messaging** - Instant message delivery and synchronization
- **User Management** - Secure user authentication and profile management
- **Conversation Threading** - Organized chat threads and message history
- **Scalable Architecture** - Built to handle concurrent users and high message throughput
- **Cross-platform Support** - Compatible with multiple client platforms

## 💻 Tech Stack

- **Language**: Java
- **Build Tool**: Gradle (Kotlin DSL)
- **Project Type**: Android Application

## 📹 Demo

Check out a quick demo of ChatFlow in action:

https://www.youtube.com/shorts/0nbugsEB-4E?si=b7573FBIj0zbb0xb

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Gradle 7.0 or higher
- Android SDK (if building for Android)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/AhmedJ561/ChatFlow.git
   cd ChatFlow
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew run
   ```

## 📁 Project Structure

```
ChatFlow/
├── app/                    # Main application code
├── gradle/                 # Gradle wrapper and configuration
├── build.gradle.kts        # Build configuration
├── settings.gradle.kts     # Settings configuration
└── gradle.properties       # Gradle properties
```

## 🔧 Configuration

Configuration details are managed through `gradle.properties`. Update these files to customize your deployment:

- **build.gradle.kts** - Build dependencies and tasks
- **settings.gradle.kts** - Project structure and module definitions

### Firebase Setup

To configure Firebase for this application, you need to set up the `google_services.json` file:

1. **Get the google_services.json file**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Select your project (or create a new one)
   - Navigate to **Project Settings** (gear icon)
   - Download the `google_services.json` file

2. **Place the file in the correct location**
   ```
   app/google_services.json
   ```
   
   The file must be placed in the `app` directory of your Android project (at the same level as `build.gradle`).

3. **Verify the Configuration**
   - Ensure the `google-services` plugin is added to your `build.gradle.kts`:
     ```kotlin
     plugins {
         id("com.android.application")
         id("com.google.gms.google-services")
     }
     ```
   - The `com.google.gms.google-services` plugin will automatically read and configure the `google_services.json` file during the build process.

4. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew run
   ```

**Note**: The `google_services.json` file contains sensitive configuration data. Never commit this file to version control. Add it to your `.gitignore`:
```
google_services.json
```

---

**Repository**: https://github.com/AhmedJ561/ChatFlow  
**Status**: Active Development
