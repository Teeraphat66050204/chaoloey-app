# Chaoloey Android App

Android client for the Chaoloey car rental system.

This project is built with:

- Kotlin
- XML layouts
- ViewBinding
- Retrofit + Gson
- OkHttp logging interceptor
- Glide
- RecyclerView

## Features

- Login
- Car list
- Car detail
- Booking
- My bookings

## Backend

This Android app uses the existing backend from:

- `https://github.com/Teeraphat66050204/chaoloey`

Base URL used in the app:

- `http://10.0.2.2:5000/api/`

Note:

- `10.0.2.2` works for the Android Emulator.
- If you run on a real Android device, change the base URL to your computer's local IP address.

## Requirements

- Android Studio
- Android SDK installed
- JDK 11
- Android Emulator or physical device
- Backend server running on port `5000`

## How To Run

### 1. Run the backend

Clone and start the backend project first:

```bash
git clone https://github.com/Teeraphat66050204/chaoloey.git
cd chaoloey/backend
npm install
npx prisma generate
npx prisma migrate dev --name init
npm run prisma:seed
npm run dev
```

Backend should run at:

```text
http://localhost:5000
```

### 2. Open the Android project

Open this repository in Android Studio.

### 3. Sync Gradle

Let Android Studio sync the Gradle project and download dependencies.

### 4. Run the app

Start an Android Emulator and run the app.

## Test Accounts

The backend seed provides these accounts:

- Admin: `admin@chaoloey.com` / `admin123`
- User: `member@chaoloey.com` / `member123`

## Project Structure

```text
app/
  src/main/java/com/example/chaoloey/
    adapter/
    data/model/
    data/remote/
    util/
  src/main/res/layout/
```

## Main Screens

- `LoginActivity`
- `CarListActivity`
- `CarDetailActivity`
- `BookingActivity`
- `MyBookingsActivity`

## Notes

- This project does not use Jetpack Compose.
- The app uses `ViewBinding` for all XML screens.
- HTTP cleartext traffic is enabled for local development because the backend runs on `http://10.0.2.2:5000`.

