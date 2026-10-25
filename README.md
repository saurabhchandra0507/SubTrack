# SubTrack – Subscription Tracker

An Android app to track all your subscriptions (Netflix, Spotify, etc.) in one place: see how much you spend every month and get reminded before a payment renews.

Built with **Kotlin** and **Jetpack Compose** using **MVVM architecture**.

## Screenshots

| <img width="305" height="646" alt="Screenshot 2026-10-25 at 12 32 47 PM" src="https://github.com/user-attachments/assets/2c4ee1ac-58c5-4184-abb7-b42e29545169" />
 | <img width="301" height="649" alt="Screenshot 2026-10-25 at 12 32 59 PM" src="https://github.com/user-attachments/assets/7932789b-fb79-4ed5-808b-f6ef65f9094c" />
<img width="310" height="654" alt="Screenshot 2026-10-25 at 12 33 18 PM" src="https://github.com/user-attachments/assets/fb32dd34-0eac-44af-8605-be3195e56c27" />

## Features

- **Login & Sign up** – local account with salted SHA-256 password hashing
- **Dashboard** – monthly and yearly spend, payments due this week, all subscriptions sorted by next payment
- **Add / Edit / Delete** subscriptions with weekly, monthly, quarterly or yearly billing
- **Insights** – spending breakdown by category with a chart
- **Daily reminders** – notification when a subscription renews today or tomorrow
- **Profile & Settings** – light/dark/system theme, multiple currencies (INR, USD, EUR, GBP, JPY)
- Material 3 design with the Plus Jakarta Sans font

## Tech Stack

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM + Repository pattern |
| State | StateFlow, Coroutines |
| Navigation | Navigation Compose |
| Database | Room |
| Settings storage | Preferences DataStore |
| Background work | WorkManager |
| Dependency injection | Manual DI (AppContainer) |
| Testing | JUnit |

## Architecture

```
UI (Compose Screen)  →  ViewModel (StateFlow UiState)  →  Repository  →  Room / DataStore
```

- **Screens** only draw the UI and send user actions to the ViewModel
- **ViewModels** hold the screen state and logic, and survive screen rotation
- **Repositories** are the single source of data for the app
- Room returns a `Flow`, so screens update automatically when data changes

## Project Structure

```
app/src/main/java/com/example/subscriptiontracker/
├── data/          # Room entities, DAO, database, repositories, DataStore
├── navigation/    # Routes, NavGraph, bottom bar
├── reminders/     # WorkManager worker, scheduler, notifications
└── ui/            # Screens + ViewModels (auth, dashboard, addedit, insights, profile)
    ├── components/  # Reusable UI components
    └── theme/       # Colors, typography, theme
```

## Getting Started

1. Clone the repo
   ```bash
   git clone https://github.com/saurabhchandra0507/SubTrack.git
   ```
2. Open it in **Android Studio**
3. Let Gradle sync, then run the app on an emulator or device (Android 8.0+, API 26)

## Future Improvements

- Hilt for dependency injection
- Cloud sync with Firebase
- UI tests

## Author

**Saurabh Chandra** – [GitHub](https://github.com/saurabhchandra0507)
