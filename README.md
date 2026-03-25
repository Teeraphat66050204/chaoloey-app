# Chaoloey Android App

แอป Android สำหรับระบบเช่ารถ Chaoloey

โปรเจกต์นี้พัฒนาด้วย:

- Kotlin
- XML Layouts
- ViewBinding
- Retrofit + Gson
- OkHttp Logging Interceptor
- Glide
- RecyclerView

## ฟีเจอร์ที่มี

- เข้าสู่ระบบ
- รายการรถ
- รายละเอียดรถ
- จองรถ
- รายการจองของฉัน

## Backend

แอป Android นี้ใช้งานร่วมกับ backend เดิมจาก:

- `https://github.com/Teeraphat66050204/chaoloey`

Base URL ที่ใช้ในแอป:

- `http://10.0.2.2:5000/api/`

หมายเหตุ:

- `10.0.2.2` ใช้สำหรับ Android Emulator
- ถ้าทดสอบบนมือถือจริง ต้องเปลี่ยนเป็น IP ของเครื่องคอมพิวเตอร์ในวง LAN เดียวกัน

## สิ่งที่ต้องมี

- Android Studio
- Android SDK
- JDK 11
- Android Emulator หรือมือถือ Android
- Backend ที่รันอยู่บนพอร์ต `5000`

## วิธีรันโปรเจกต์

### 1. รัน Backend ก่อน

Clone และเริ่ม backend:

```bash
git clone https://github.com/Teeraphat66050204/chaoloey.git
cd chaoloey/backend
npm install
npx prisma generate
npx prisma migrate dev --name init
npm run prisma:seed
npm run dev
```

Backend จะรันที่:

```text
http://localhost:5000
```

### 2. เปิดโปรเจกต์ใน Android Studio

เปิด repository นี้ด้วย Android Studio

### 3. Sync Gradle

รอให้ Android Studio sync โปรเจกต์และติดตั้ง dependencies ให้ครบ

### 4. รันแอป

เปิด Android Emulator แล้วกด Run ได้เลย

## บัญชีสำหรับทดสอบ

บัญชีที่ backend seed มาให้:

- Admin: `admin@chaoloey.com` / `admin123`
- User: `member@chaoloey.com` / `member123`

## โครงสร้างโปรเจกต์

```text
app/
  src/main/java/com/example/chaoloey/
    adapter/
    data/model/
    data/remote/
    util/
  src/main/res/layout/
```

## หน้าหลักในแอป

- `LoginActivity`
- `CarListActivity`
- `CarDetailActivity`
- `BookingActivity`
- `MyBookingsActivity`

## ส่วนภาพหน้าจอ

คุณสามารถเพิ่มภาพหน้าจอของแอปไว้ในส่วนนี้ได้ภายหลัง

ภาพที่แนะนำ:

- หน้า Login
- หน้า Car List
- หน้า Car Detail
- หน้า Booking
- หน้า My Bookings

โครงสร้างไฟล์ตัวอย่าง:

```text
screenshots/
  login.png
  car-list.png
  car-detail.png
  booking.png
  my-bookings.png
```

ตัวอย่าง markdown:

```md
![Login](screenshots/login.png)
![Car List](screenshots/car-list.png)
![Car Detail](screenshots/car-detail.png)
![Booking](screenshots/booking.png)
![My Bookings](screenshots/my-bookings.png)
```

## สรุป API ที่ใช้

แอป Android นี้ใช้งานเฉพาะ API ที่มีอยู่แล้วใน backend

### Auth

- `POST /api/auth/login`

### Cars

- `GET /api/cars`
- `GET /api/cars/{id}`

### Bookings

- `POST /api/bookings`
- `GET /api/bookings`

### Authorization

API ที่ต้องล็อกอินจะต้องส่ง header แบบนี้:

```http
Authorization: Bearer <token>
```

Token จะถูกเก็บไว้ในเครื่องด้วย `SharedPreferences`

## การติดตั้งสำหรับทดสอบบนมือถือจริง

ถ้าต้องการทดสอบบนมือถือ Android จริงแทน Emulator:

### 1. ให้มือถือกับคอมต่อ Wi-Fi วงเดียวกัน

มือถือและ backend ต้องอยู่ใน network เดียวกัน

### 2. หา IP ของเครื่องคอมพิวเตอร์

ตัวอย่าง:

```text
192.168.1.10
```

### 3. เปลี่ยน Base URL ใน `RetrofitClient.kt`

จาก:

```kotlin
private const val BASE_URL = "http://10.0.2.2:5000/api/"
```

เป็น:

```kotlin
private const val BASE_URL = "http://192.168.1.10:5000/api/"
```

เปลี่ยน `192.168.1.10` ให้เป็น IP จริงของเครื่องคุณ

### 4. ตรวจสอบว่า backend เข้าถึงได้จากมือถือ

รัน backend และตรวจสอบว่าพอร์ต `5000` ไม่ถูกบล็อกในวง network เดียวกัน

### 5. อนุญาต cleartext HTTP

โปรเจกต์นี้เปิดใช้ cleartext traffic สำหรับ local development ไว้แล้วใน `AndroidManifest.xml`

### 6. ติดตั้งแอปลงมือถือ

คุณสามารถรันจาก Android Studio โดยตรง หรือ build APK แล้วติดตั้งลงเครื่องได้

## หมายเหตุ

- โปรเจกต์นี้ไม่ได้ใช้ Jetpack Compose
- ทุกหน้าจอใช้ `ViewBinding` ร่วมกับ XML
- เปิดใช้ HTTP cleartext สำหรับ local development เพราะ backend รันบน `http://10.0.2.2:5000`

