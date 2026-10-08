# Aplikasi Katalog dan Eksplorasi Video Game

Responsi Pemrograman Mobile  
Nama : Faqih Ardiansyah
NIM  : H1D024047

## 🎥 Video Penjelasan Kode

🔗 **Link Video YouTube:**
[VIDEO_PENJELASAN_KODE_PROGRAM](https://youtu.be/3xEjKE7_Ji0)

## Deskripsi

Aplikasi Katalog Video Game berbasis Android yang dibangun menggunakan **Kotlin**, **Jetpack Compose**, dan **Material Design 3**.
Aplikasi ini mengambil dan menampilkan data video game secara dinamis dari
**RAWG API** (https://api.rawg.io/docs/) dengan menerapkan arsitektur **MVVM**
(Model - View - ViewModel) serta pola **Repository**.

Aplikasi ini memiliki dua screen utama:
1. **Home Screen** — menampilkan daftar game (judul, rating, tanggal rilis, metacritic)
   beserta search bar untuk mencari game.
2. **Game Detail Screen** — menampilkan judul, rating, tanggal rilis, genre,
   platform, dan deskripsi dari game yang dipilih.

---

## Screenshot Aplikasi

| Home Screen | Hasil Pencarian | Detail Game |
|:---:|:---:|:---:|
| ![Home Screen](screenshots/home.png) | ![Hasil Search](screenshots/search.png) | ![Detail Screen](screenshots/detail.png) |
| Daftar game beserta search bar | Contoh hasil pencarian game | Detail game beserta deskripsi |

---

## Fitur

- Menampilkan daftar video game dari RAWG API (`GET /games`).
- Mencari game berdasarkan nama menggunakan search bar (`GET /games?search=...`).
- Menampilkan detail game termasuk deskripsi (`GET /games/{id}`).
- Menerapkan state-driven UI: **Loading**, **Success**, dan **Error**.
- Navigasi antar screen menggunakan Navigation Compose.
- Custom theme dan custom typography dengan Material Design 3.
- Tanpa Hilt (dependency injection dilakukan manual dengan singleton).

---

## Penjelasan Teknis

### 1. Struktur Project

```text
app/src/main/java/com/example/gameexplorer/
├── data/
│   └── model/
│       ├── Game.kt            # Data class model & response API
│       └── GameRepository.kt  # Repository (perantara ViewModel & API)
├── network/
│   └── ApiInterface.kt        # Kontrak Retrofit + singleton ApiClient
├── ui/
│   ├── screen/
│   │   ├── HomeScreen.kt      # Daftar game + search bar
│   │   └── GameDetailScreen.kt# Detail game + deskripsi
│   └── theme/
│       ├── Color.kt           # Custom color scheme
│       ├── Type.kt            # Custom typography
│       └── Theme.kt           # Material 3 theme
├── util/
│   └── RawgConstants.kt       # BASE_URL dan API_KEY
├── viewmodel/
│   └── GameViewModel.kt       # ViewModel + UI State
└── MainActivity.kt            # Entry point + Navigation host
```

### 2. Permission dan Dependency

- Permission `android.permission.INTERNET` ditambahkan pada `AndroidManifest.xml`
  agar aplikasi diizinkan mengakses jaringan.
- Dependency yang digunakan:
    - **Retrofit** + **Gson Converter** untuk request HTTP dan parsing JSON.
    - **Navigation Compose** untuk navigasi antar screen.
    - **Lifecycle ViewModel Compose** untuk menggunakan ViewModel di Compose.
    - **Material Icons Extended** untuk icon (search, close, arrow back).

### 3. Konstanta (util)

File `RawgConstants.kt` menyimpan:
- `BASE_URL = "https://api.rawg.io/api/"` (wajib diakhiri `/` untuk Retrofit).
- `API_KEY` yang diperoleh dari https://rawg.io/apidocs dan dikirim sebagai
  query parameter `key` pada setiap request.

### 4. Model dan Pemetaan JSON

Berdasarkan dokumentasi RAWG API, respons endpoint `/games` berbentuk object
yang membungkus daftar game di dalam field `results`, sehingga dibuat:

- `GamesResponse` → menampung `count`, `next`, `previous`, dan `results`.
- `Game` → merepresentasikan satu game dengan field:
    - `id` (Int)
    - `name` (String?) — nama game
    - `rating` (Double?) — rating berbentuk angka
    - `released` (String?) — tanggal rilis format ISO 8601 (YYYY-MM-DD)
    - `metacritic` (Int?)
    - `description_raw` (String?) — deskripsi, hanya tersedia di endpoint detail
    - `genres` (List<Genre>?) dan `platforms` (List<PlatformInfo>?)

Field yang tidak selalu ada dibuat **nullable** untuk memanfaatkan fitur
**null safety** Kotlin. Nama properti disesuaikan dengan nama field JSON
sehingga tidak memerlukan `@SerializedName`.

### 5. Networking (Retrofit)

`ApiInterface` berisi dua endpoint:

- `GET games` → mengambil daftar game, mendukung query parameter:
    - `key` (API key)
    - `search` (kata kunci pencarian, nullable)
    - `page_size` (jumlah data per halaman)
- `GET games/{id}` → mengambil detail game berdasarkan ID untuk mendapatkan
  deskripsi (`description_raw`).

Semua fungsi dideklarasikan sebagai `suspend function` agar berjalan
asynchronous menggunakan Kotlin Coroutines tanpa memblokir UI thread.

`ApiClient` dibuat sebagai **singleton** dengan `by lazy` sehingga objek
Retrofit hanya diinisialisasi satu kali saat pertama digunakan.

### 6. Repository

`GameRepository` berperan sebagai lapisan perantara antara ViewModel dan API:
- `getGames(search)` → memanggil endpoint list lalu mengembalikan `results`.
- `getGameDetail(id)` → memanggil endpoint detail untuk data lengkap + deskripsi.

Karena tidak menggunakan Hilt, repository diakses melalui
`GameRepository.instance` (singleton via companion object).

### 7. ViewModel dan State (StateFlow)

`GameViewModel` menerapkan **UI State Pattern** menggunakan `sealed interface`:

- `GamesUiState` → `Loading`, `Success(games)`, `Error(message)` untuk halaman list.
- `GameDetailUiState` → `Loading`, `Success(game)`, `Error(message)` untuk halaman detail.

State internal disimpan pada `MutableStateFlow` (private) dan diekspos ke UI
sebagai `StateFlow` (read-only) melalui `asStateFlow()`, sehingga UI hanya dapat
mengamati perubahan state tanpa mengubahnya langsung.

Pemanggilan API dilakukan di dalam `viewModelScope.launch` dengan blok
`try-catch`: berhasil → state `Success`, gagal → state `Error`.

### 8. State-Driven UI, Recomposition, dan Search

- UI mengamati state dengan `collectAsState()`. Setiap state berubah
  (Loading → Success/Error), Compose otomatis melakukan **recomposition**
  hanya pada bagian UI yang mengonsumsi state tersebut.
- Search bar terhubung dengan state `searchQuery` di ViewModel.
  Saat pengguna mengetik, state berubah dan UI ikut ter-update.
  Saat tombol cari ditekan, ViewModel memanggil ulang API dengan parameter
  `search`, sehingga daftar game berubah sesuai kata kunci.
- Tombol reset mengosongkan query dan memuat ulang daftar game default.

### 9. UI (Jetpack Compose + Material 3)

- **Home Screen**: `Scaffold` + `TopAppBar`, `OutlinedTextField` sebagai search bar
  (dengan icon search & close), tombol Cari/Reset, dan `LazyColumn` untuk
  menampilkan daftar game dalam bentuk `Card`.
- **Detail Screen**: `Scaffold` + `TopAppBar` dengan navigation icon kembali,
  konten dapat di-scroll (`verticalScroll`), menampilkan judul, rating,
  tanggal rilis, metacritic, genre, platform, dan deskripsi.
  Data detail dimuat dengan `LaunchedEffect(gameId)` saat screen dibuka.
- **Custom Theme & Typography**: warna light/dark didefinisikan di `Color.kt`,
  gaya teks di `Type.kt`, dan digabungkan melalui `MaterialTheme` di `Theme.kt`.

### 10. Navigation

Navigasi menggunakan **Navigation Compose** dengan dua route:
- `home` → HomeScreen
- `detail/{gameId}` → GameDetailScreen (argumen bertipe `NavType.IntType`)

ViewModel dibuat satu kali di level navigation agar state konsisten
saat berpindah screen.

### 11. Fitur Kotlin yang Dimanfaatkan

- **Data class** → `Game`, `GamesResponse`, `Genre`, `PlatformInfo`.
- **Null safety** → field nullable (`String?`, `Double?`) dan operator `?:`, `?.let`.
- **Lambda** → callback `onGameClick`, `onBack`, `onClick`.
- **Collection** → `mapNotNull`, `joinToString`, `items` pada LazyColumn.
- **Coroutines & Flow** → `viewModelScope.launch`, `StateFlow`, `collectAsState`.

---

## Cara Menjalankan Aplikasi

1. Clone repository ini:
   ```bash
   git clone https://github.com/username-kamu/nama-repo.git
   ```
2. Dapatkan API key gratis di https://rawg.io/apidocs
3. Masukkan API key ke file:
   ```text
   app/src/main/java/com/example/gameexplorer/util/RawgConstants.kt
   ```
   pada konstanta `API_KEY`.
4. Buka project di Android Studio, lalu **Sync Project with Gradle Files**.
5. Jalankan aplikasi pada emulator atau perangkat fisik.

---

## Dependencies Utama

| Library | Kegunaan |
|---|---|
| Retrofit | HTTP client untuk mengakses RAWG API |
| Gson Converter | Parsing JSON menjadi data class Kotlin |
| Navigation Compose | Navigasi antar screen |
| Lifecycle ViewModel Compose | ViewModel pada Jetpack Compose |
| Material Icons Extended | Icon Material (search, close, arrow back) |

---

## Catatan

- Aplikasi tidak menggunakan database, login, maupun XML layout.
- Seluruh UI dibuat dengan Jetpack Compose.
- Dependency injection dilakukan manual (singleton) tanpa Hilt.