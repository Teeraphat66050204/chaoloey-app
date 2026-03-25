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

## Screenshots

Add your app screenshots here after capturing them from the emulator or device.

Suggested screenshots:

- Login screen
- Car list screen
- Car detail screen
- Booking screen
- My bookings screen

Example structure:

```text
screenshots/
  login.png
  car-list.png
  car-detail.png
  booking.png
  my-bookings.png
```

Example markdown:

```md
![Login](screenshots/login.png)
![Car List](screenshots/car-list.png)
![Car Detail](screenshots/car-detail.png)
![Booking](screenshots/booking.png)
![My Bookings](screenshots/my-bookings.png)
```

## API Summary

This Android app uses the existing backend APIs only.

### Auth

- `POST /api/auth/login`

### Cars

- `GET /api/cars`
- `GET /api/cars/{id}`

### Bookings

- `POST /api/bookings`
- `GET /api/bookings`

### Authorization

Authenticated endpoints require:

```http
Authorization: Bearer <token>
```

The token is stored locally using `SharedPreferences`.

## Real Device Installation

If you want to test on a real Android phone instead of the emulator:

### 1. Connect phone and computer to the same Wi-Fi

Your backend and your Android phone must be on the same local network.

### 2. Find your computer's local IP address

Example:

```text
192.168.1.10
```

### 3. Change the base URL in `RetrofitClient.kt`

From:

```kotlin
private const val BASE_URL = "http://10.0.2.2:5000/api/"
```

To:

```kotlin
private const val BASE_URL = "http://192.168.1.10:5000/api/"
```

Replace `192.168.1.10` with your actual computer IP.

### 4. Make sure the backend is accessible

Run the backend and make sure port `5000` is open to devices on your network.

### 5. Allow cleartext HTTP traffic

This project already enables cleartext traffic for local development in `AndroidManifest.xml`.

### 6. Build and install the app

You can run the app directly from Android Studio or build an APK and install it on your phone.

## Notes

- This project does not use Jetpack Compose.
- The app uses `ViewBinding` for all XML screens.
- HTTP cleartext traffic is enabled for local development because the backend runs on `http://10.0.2.2:5000`.
