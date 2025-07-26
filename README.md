# WeatherSnap 🌦️

A simple **Weather Forecast App** built with **Jetpack Compose** that displays the current weather based on the user's location and allows capturing weather-tagged photos.

---

## Features

- **Location Detection** – Automatically fetches the user’s location using gms Location Services.
- **Weather Display** – Shows current temperature (°C/°F), humidity, wind speed, and weather condition icon by weatherapi.com.
- **Unit Conversion** – Toggle between Celsius and Fahrenheit.
- **Weather Overlay on Photos** – Captures a photo with the weather data overlaid and saves it to local storage.
- **History Screen** – Displays a grid of previously captured weather photos.
- **Settings** – Toggle temperature units and view app version.
- **Responsive UI** – Optimized for both portrait and landscape orientations.

---

## Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose 
- **Architecture:** MVVM + Clean Architecture (Domain, Data, UI layers)  
- **Coroutines & Flow:** Asynchronous data fetching and state management  
- **CameraX:** For capturing weather-tagged photos  
- **DataStore:** Persist user preferences (e.g., temperature unit)  
- **Room + Paging3:** For local storage and paginated image history
- **File Management:** For save images to local storage   
- **Coil:** Image loading  
- **Hilt:** Dependency Injection  

---

## Project Structure

```
com.zerir.weathersnap
├── data/               # Data sources, repositories
├── domain/             # Domain models and repository interfaces
├── ui/
│   ├── components/     # Reusable Jetpack Compose components
│   ├── screens/        # App screens (Home, Camera, Settings)
│   └── theme/          
└── utils/              # Helpers (permissions, etc.)
```

---

## How It Works

1. **Home Screen**  
   - Requests **location permission** on launch.  
   - Fetches weather data via `WeatherRepository`.  
   - Displays weather info and a grid of captured photos.

2. **Camera Screen**  
   - Uses CameraX to preview and capture photos.  
   - Weather data is overlaid using a `Canvas` before saving.  
   - Saves metadata (weather, location, timestamp) in Room DB.

3. **Settings Screen**  
   - Allows toggling between Celsius and Fahrenheit (stored in DataStore).  
   - Displays app version.

---

## Clone the repository

```bash
   git clone git@github.com:omarzerir/WeatherSnap.git
```

---

## Testing

- **Unit Tests**:  
  - `HomeViewModelTest` – Tests HomeViewModel logic.  
  - `WeatherRepositoryImplTest` – Tests WeatherRepository implementation.

---
