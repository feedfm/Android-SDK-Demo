# Feed.fm Android SDK Demo

This repository contains a demo application that illustrates how to use the
[Feed.fm Android SDK](https://github.com/feedfm/Android-SDK) to build a simple
app that streams music. It is meant as a reference integration: a small, readable Jetpack
Compose project you can read top-to-bottom to see how the SDK is configured,
how playback is driven, and how the standard Android "now playing" experience
(notification + lock-screen controls, artwork, and background audio) is wired
up.

## What's in here

| Path | Description |
| --- | --- |
| `settings.gradle.kts`, `build.gradle.kts` | Gradle project setup. Open the repo root in Android Studio. |
| `simple/` | The **Simple** demo app — a Compose "Feed Radio" client. |
| `simple/src/main/` | App source. |
| `simple/src/test/` | Unit tests for the app's plain-Kotlin helpers. |

The Feed.fm SDK is pulled in from Maven Central as
`fm.feed.android:player-sdk:7.1.0` (see `simple/build.gradle.kts`). There is
nothing to install by hand — Gradle resolves it on first sync.

## The Simple app

**Simple** is a one-screen Compose music app that:

- Lists the stations available to the 'demo' Feed.fm account.
- Plays, pauses, and skips tracks, and likes / dislikes the current song.
- Shows a mini player bar that expands into a full-screen player.
- Renders per-station artwork (a remote image when the station provides one,
  otherwise a generated brand-gradient + waveform fallback).
- Keeps playing in the background and drives the lock-screen / notification
  "Now Playing" interface — metadata, artwork, and remote play / pause / skip
  controls.

### How it's structured

The SDK does the heavy lifting (streaming, the audio focus / `MediaSession`,
the foreground service, notification metadata, and remote commands); the app is
mostly thin UI plus a single `ViewModel` that bridges SDK listener callbacks to
Compose state.

| File | Role |
| --- | --- |
| `SimpleApplication.kt` | App entry point. Builds the player with the demo credentials and a fresh client id, then hands it to `FeedPlayerService.initialize(...)` so playback and the media notification survive backgrounding. |
| `MainActivity.kt` | Hosts the Compose UI. Gates the app on player availability, requests the `POST_NOTIFICATIONS` permission on Android 13+, and composes the station list with the mini bar and the expanding full player. |
| `player/SimplePlayerViewModel.kt` | Wrapper around `FeedPlayerService.getInstance()`. Subscribes to the SDK's listeners, exposes display state + playback intents to the UI, and maps SDK `Station`s to the display model. |
| `ui/StationListScreen.kt`, `ui/StationRow.kt` | Station browsing UI. |
| `ui/MiniPlayerBar.kt`, `ui/FullPlayerSheet.kt` | Playback UI. |
| `model/RadioStation.kt` | A display model decoupled from the SDK's `Station`, built from a plain options map so it's easy to test. |
| `ui/ArtworkView.kt`, `ui/WaveformBars.kt`, `audio/Waveform.kt`, `ui/theme/FrTheme.kt` | Artwork rendering and theming. |
| `audio/TimeFormat.kt` | Elapsed / remaining time formatting. |

### Key SDK touch points

If you're integrating the SDK into your own app, these are the parts to read first:

1. **Credentials & initialization** — `FeedAudioPlayer.Builder(context, token, secret)`
   plus `FeedPlayerService.initialize(builder)` in `SimpleApplication.kt`. Doing
   this in `Application.onCreate()` lets the SDK own a foreground service so audio
   and the media notification continue when the app is backgrounded.
2. **Availability** — `FeedPlayerService.getInstance(AvailabilityListener)` in
   `MainActivity.kt` gates the UI until the player (and station list) is ready, or
   reports that no music is available.
3. **Playback** — `play(station)`, `play()`, `pause()`, `skip(...)`, `like()`,
   `dislike()`, and `unlike()` in `SimplePlayerViewModel.kt`.
4. **State** — the app registers `StateListener`, `PlayListener`,
   `StationChangedListener`, and `LikeStatusChangeListener` (and removes them in
   `onCleared()`) to react to playback state, the current item, elapsed time, and
   skip / like status rather than polling.
5. **Notification, lock screen & background audio** — `FeedPlayerService` runs the
   foreground media-playback service and posts the notification with remote
   controls. The SDK's manifest merges in the `FOREGROUND_SERVICE*` permissions and
   the service declaration; the host app only needs to declare and request the
   runtime `POST_NOTIFICATIONS` permission on Android 13+ (see
   `AndroidManifest.xml` and `MainActivity.kt`).

## Demo Requirements

- Android Studio (Giraffe or later) with the Android Gradle Plugin 8.x
- JDK 17
- Android SDK Platform 34 (`compileSdk`/`targetSdk = 34`, `minSdk = 23`)
- A Feed.fm client token and secret

## Running

1. Open the repository root in Android Studio and let Gradle sync.
2. Select the **simple** run configuration and a device or emulator (API 23+).
3. Press Run (▶).

Or from the command line:

```sh
./gradlew :simple:installDebug
```

The demo ships with the placeholder credentials `"demo"` / `"demo"` (see
`SimpleApplication.kt`). To stream your own catalog, replace them with the
client token and secret from your [Feed.fm](https://feed.fm) account.

### Running the tests

```sh
./gradlew :simple:testDebugUnitTest
```

## Learn more

- [docs.feed.fm](https://docs.feed.fm)
