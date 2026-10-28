# Chess Dual (Chess Master) ♟️

A sleek, professional two-player chess application engineered specifically for local pass-and-play matches on a single mobile device. Built with native Android, Kotlin, Jetpack Compose, and Material Design 3.

---

## Features

- **Pass & Play System:** Across-table (face-to-face) dual HUD mode with 180° inversion for flat-table play.
- **Auto-Rotate Board:** Optional board spin on each turn to keep the active player on the bottom.
- **Realistic UI & Themes:** Royal Walnut & Maple, Tournament Green, Midnight Slate, and Glass & Frost.
- **Dual Digital Clocks:** Presets for Casual, Bullet (1 min), Blitz (3m, 3|2, 5m), and Rapid (10m, 15|10).
- **Full Engine & Move Assistance:** En passant, castling, promotion, live SAN notation ribbon, check/checkmate detection, and undo.
- **Offline Match History:** Built with local Room Database to save and review past match records and copy PGNs.

---

## Automated APK Builds & GitHub Releases

This repository includes a continuous integration workflow (`.github/workflows/build-apk.yml`) that automatically builds an installable Android APK (`Chess-Nova.apk`) on every push to `main`, on manual trigger, and whenever a version tag is published.

### 1. How to Open the Repository's Actions Tab
1. Open your repository on GitHub.
2. In the top navigation bar beneath the repository name, click on the **Actions** tab (located between *Pull requests* and *Projects*).

### 2. How to Run the APK Build Manually
1. Go to the **Actions** tab.
2. In the left sidebar, click on **Build & Release Android APK**.
3. On the right side, click the **Run workflow** dropdown button.
4. Select the branch (default: `main`).
5. *(Optional)* Check **Create a GitHub Release with this build** and enter a version tag (e.g., `v1.0.0`) if you want to publish a release directly.
6. Click the green **Run workflow** button. The build will start immediately.

### 3. How to Download the "Chess-Nova-APK" Artifact
1. Go to the **Actions** tab.
2. Click on the most recent workflow run (e.g., *Build & Release Android APK* with a green checkmark).
3. Scroll down to the **Artifacts** section at the bottom of the summary page.
4. Click on **Chess-Nova-APK**. GitHub will download a ZIP archive containing the ready-to-install `Chess-Nova.apk`.
5. Unzip the downloaded file and transfer or install `Chess-Nova.apk` onto your Android phone or emulator.

### 4. How to Download the APK from GitHub Releases
1. On the main page of your repository, look at the right sidebar under the **Releases** section.
2. Click on the latest release (e.g., `v1.0.0`).
3. Under the **Assets** section of the release notes, click on **Chess-Nova.apk** to download the standalone APK directly (no unzipping needed).

### 5. How to Create a New Versioned Release
You can create a versioned release automatically using either Git tags or GitHub web:

#### Method A: Using Git CLI (Recommended)
```bash
# Tag the current commit with your version
git tag v1.0.0

# Push the tag to GitHub
git push origin v1.0.0
```
Pushing any tag starting with `v` automatically triggers the build workflow, compiles `Chess-Nova.apk`, creates the GitHub Release, and attaches the APK file.

#### Method B: Using GitHub Web Interface
1. Go to your repository's main page.
2. In the right sidebar, click **Releases** → **Draft a new release**.
3. Click **Choose a tag**, type `v1.0.0` (or your version number), and select **Create new tag on publish**.
4. Set the Release Title (e.g., `Release v1.0.0`).
5. Click **Publish release**. The workflow will build and attach the APK asset automatically.

---

## Local Development & Build

To build the APK locally from the terminal:
```bash
# Ensure keystore and environment files are ready
base64 -d debug.keystore.base64 > debug.keystore
cp .env.example .env

# Assemble debug APK
./gradlew :app:assembleDebug
```
The output APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```
