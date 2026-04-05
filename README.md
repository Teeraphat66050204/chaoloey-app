# Chaoloey — Car Rental Android App

A native Android application for car rental booking, built with **Kotlin + XML + ViewBinding + Material Design**.

---

## Features

- **Onboarding** — first-launch walkthrough with ViewPager2
- **Authentication** — register / login with JWT token stored in SharedPreferences
- **Car Listing** — browse available cars with search and filter (brand, transmission, seats)
- **Car Detail** — full specs view with booking entry point
- **Booking Flow** — select dates with availability check → payment method → confirmation
- **My Bookings** — view all past and upcoming bookings synced from backend
- **Profile** — view account info, change username, logout

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.2.10 (100%) |
| UI | XML Layouts + ViewBinding |
| Design | Material Design 3 |
| Networking | Retrofit2 + OkHttp3 + Gson |
| Image Loading | Glide |
| Auth Storage | SharedPreferences (`TokenManager`) |
| Build System | Gradle (Kotlin DSL) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

---

## Project Structure

```
app/src/main/java/com/example/chaoloey/
├── adapter/            # RecyclerView adapters (CarAdapter, BookingAdapter)
├── data/
│   ├── model/          # Kotlin data classes (Car, Booking, Auth models)
│   └── remote/         # Retrofit API service & client
├── ui/                 # Fragments, dialogs, onboarding screens
├── util/               # Helper classes (TokenManager)
└── *Activity.kt        # All screen activities
```

---

## Backend

REST API hosted at: `https://chaoloey.onrender.com/api/`

Endpoints used:
- `POST /auth/login`
- `POST /auth/register`
- `PUT /auth/profile`
- `GET /cars`
- `GET /cars/:id`
- `GET /cars/:id/availability`
- `POST /bookings`
- `GET /bookings`
- `GET /bookings/:id`

---

## Setup

1. Clone the repository
2. Open in **Android Studio Meerkat** or later
3. Sync Gradle
4. Run on device / emulator (API 24+)

> No API keys or local configuration required — the backend is publicly hosted.


