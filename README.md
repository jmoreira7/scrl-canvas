# SCRL Android Technical Assignment

## Features

### Core
- Fetch overlays from a remote API
- Scrollable canvas for overlay placement
- “+” button to open a sheet with overlays
- Add overlays to the canvas from the sheet
- Select and deselect overlays by tapping
- Move overlays by dragging
- Supports snapping to canvas edges and other overlays
- Visual yellow guidelines appear when snapping occurs

### Nice-to-Have
- Category section in the overlays sheet

## Technologies
- Kotlin
- Jetpack Compose
- Koin (Dependency Injection)
- Ktor (Networking)
- Coil (Image Loading)

## Setup
1. Clone the repository
2. Configure API URL
   
   - Add the following line to your local.properties file: `API_URL=https://your-api-url.com/endpoint`
   - Replace the URL with your actual API endpoint.
3. Open in Android Studio and build the project

## Demo
<img src="scrl-demo.gif" alt="Demo" width="300"/>