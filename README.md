# Seekho Anime Assignment (Android)

An Android app that shows **Top Anime** list and **Anime Details** using the **Jikan API** (MyAnimeList unofficial API).  
Built with a clean separation of layers, offline-first caching, and reactive UI updates.

---

## Features Implemented (End-to-End)

### 1) Top Anime List (Home)
- Fetches **Top Anime** from Jikan API (`/v4/top/anime?page=1`)
- Displays list with:
  - Poster image
  - Title
  - Episodes
  - Rating/Score
- Click an item → navigates to Detail screen

### 2) Anime Details Screen
- Fetches **anime detail** by id:
  - Title
  - Episodes
  - Rating
  - Genres
  - Synopsis
  - Poster
- Fetches **main cast**:
  - Shows horizontal list of main cast
  - Shows profile image (or fallback initial if missing)
  - Controlled by `SHOW_PROFILE_IMAGES` flag

### 3) Trailer Handling (Video if available else Poster)
- If trailer is available:
  - Shows trailer inside the app via WebView (YouTube embed)
  - Also supports “Watch Trailer” button to open YouTube app/browser
- If trailer is not available:
  - Shows poster image only
- **Offline behavior:**
  - Trailer WebView is not shown when offline (to avoid WebView error)
  - Poster is shown instead
  - Cached detail + cast still display (Room)

### 4) Offline-First Caching
- Uses **Room DB** to cache:
  - Top anime list
  - Anime detail
  - Main cast
- App works in offline mode by showing cached data
- Refresh happens when network is available

### 5) Error Handling (Mapped & User-Friendly)
Implemented a consistent error model:
- `AppResult<T>` (Success / Error)
- `AppError` types:
  - Network, RateLimited (429), Server (5xx), NotFound (404), Database, Unknown
- `ErrorMapper` converts exceptions → `AppError`
- UI shows:
  - Loading only when there is no cached data
  - Error screen + Retry only when cache is empty
  - Cached data shown even if refresh fails

### 6) Reactive UI (StateFlow)
- UI is reactive using **StateFlow**
- Fragments collect UI state with `repeatOnLifecycle(STARTED)`
- No manual “load()” calls once binding is active (bind triggers refresh logic)

### 7) Navigation (Fragments)
- `MainActivity` hosts a single fragment container
- `AnimeListFragment` → `AnimeDetailFragment` (backstack supported)

### 8) Splash Screen
- Simple splash screen showing app logo + title before landing on Home

---

## Architecture Overview

### Layers
- **UI layer**: Activity + Fragments + Adapters
- **ViewModel layer**: StateFlow-based viewmodels
- **Repository layer**: single source of truth
- **Data layer**:
  - Remote (Retrofit + OkHttp logging)
  - Local (Room)

### Data Flow (Offline-first)
1. UI observes Room via `Flow`
2. UI displays cached data immediately
3. If online → refresh from network → update Room
4. UI updates automatically via Flow

---

##  API Used (Jikan)
Base URL:
- `https://api.jikan.moe/v4/`

Endpoints used:
- Top Anime:
  - `/top/anime?page=1`
- Anime Detail:
  - `/anime/{id}/full` (or equivalent detail endpoint used in code)
- Characters:
  - `/anime/{id}/characters`

---

## What to Test (Quick Checklist)
- Launch app → should show Top Anime
- Tap anime → detail loads + cast loads
- Turn off internet:
  - Reopen app → cached list shows
  - Open detail → cached detail shows
  - Trailer WebView should not show (poster shows instead)
- Rate limit (429):
  - User message: “Too many requests. Try again in a few seconds.”

---

## Tech Stack
- Kotlin
- MVVM
- StateFlow + Coroutines
- Room
- Retrofit + OkHttp
- Coil (image loading)
- Material UI components

---

## How to Run
1. Clone repo
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator/device (minSdk 24)

---

##  Notes / Decisions
- Pagination: For assignment scope, showing **page=1** top anime is sufficient.
  (Easy to extend to paging if required.)
- Trailer: Jikan provides trailer fields like:
  - `trailer.youtube_id`
  - `trailer.embed_url`
  WebView uses embed URL when available.

### Pagination (Top Anime)
- Jikan Top Anime endpoint supports pagination via `page` query param.
- For assignment scope and simplicity, this app currently fetches **page=1 only**:
  - `/v4/top/anime?page=1`
- This still satisfies “Top Anime” requirement and can be extended later to load more pages (Paging 3 / endless scroll).

### Why WebView trailer + Watch Trailer button (No native video player)
- Jikan trailer data is typically provided as **YouTube metadata**:
  - `trailer.youtube_id`
  - `trailer.embed_url` (e.g. `https://www.youtube-nocookie.com/embed/...`)
- Since the source is **YouTube embed/watch URL**, a native player like **ExoPlayer** cannot directly play it reliably unless you have a direct MP4/HLS stream URL (which YouTube does not provide via this API).
- Therefore:
  - **Option A (in-app)**: show trailer via **WebView (YouTube embed)** when online
  - **Option B (external)**: show **Watch Trailer** button → opens YouTube app/browser (best UX + stable playback)
- Offline: YouTube embed will not work offline, so we fallback to poster.

---

## Trailer Behavior (Two Ways)

If trailer exists:
- When **ONLINE**:
  - Trailer can be played **inside the app** using WebView embed
  - A **Watch Trailer** button is also available (opens YouTube app/browser)
  - Once WebView trailer is showing, the button is hidden to avoid duplication
- When **OFFLINE**:
  - Trailer is not loaded (WebView hidden)
  - Poster is shown instead (prevents WebView "No Internet" page)

If trailer does NOT exist:
- Poster is shown always
- Watch Trailer button is hidden


## Author
Gopalakrishnvel R

