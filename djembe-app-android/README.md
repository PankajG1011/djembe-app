# Djembe App — Android

Native Android (Kotlin) frontend for the playable virtual djembe, pairing with
the [Spring Boot backend](../djembe-app-backend.zip).

## Stack
- Kotlin, minSdk 24 / targetSdk 34
- `SoundPool` for low-latency audio triggering (not `MediaPlayer` — too much
  trigger latency for a playable instrument)
- Retrofit + coroutines for API calls to the backend

## Project structure
```
app/src/main/java/com/djembe/android/
├── ui/DjembeView.kt          The playable drum - custom View, touch zones -> tones
├── audio/DjembeSoundEngine.kt  SoundPool wrapper, loads & triggers samples
├── network/                  Retrofit API + client
├── model/Models.kt           Data classes matching backend DTOs
└── MainActivity.kt           Hosts the DjembeView
```

## How the playable djembe works
`DjembeView` draws a circular drum head and splits it into three concentric
touch zones:
- **Center** → BASS (deep, open-hand-on-center sound)
- **Middle ring** → TONE (open-hand-on-edge sound)
- **Outer ring** → SLAP (sharp, fingers-on-edge sound)

Multi-touch is supported (`ACTION_POINTER_DOWN`), so two fingers can trigger
independent hits — closer to real two-handed djembe technique than a
single-touch instrument would be.

`DjembeSoundEngine` wraps `SoundPool` with `AudioAttributes.USAGE_GAME`, which
Android treats as latency-sensitive (same category games use for sound
effects) — this is what keeps tap-to-sound feel responsive instead of laggy.

## Before this runs
1. **Add real audio samples.** Drop three short WAV files into
   `app/src/main/res/raw/`:
   - `djembe_bass.wav`
   - `djembe_tone.wav`
   - `djembe_slap.wav`

   Keep them short and dry-mixed (no reverb tail) — reverb makes rapid
   retriggering sound muddy. WAV over MP3 for lowest load/trigger latency.
2. **Point Retrofit at your backend.** `RetrofitClient.kt` defaults to
   `10.0.2.2:8080`, which is the Android emulator's alias for your dev
   machine's `localhost`. Change this to your deployed URL for a real device.
3. Open in Android Studio (this was scaffolded by hand, not by Android
   Studio's wizard, so let it sync Gradle on first open — it may prompt to
   regenerate the Gradle wrapper, which is fine to accept).

## Next steps
- Wire up login/register screens using `DjembeApi.register`/`login`, store the
  returned JWT (e.g. in `EncryptedSharedPreferences`), and pass it as the
  `Authorization: Bearer <token>` header on progress calls.
- Build lesson list + rhythm library screens (RecyclerView) pulling from
  `GET /api/lessons` and `GET /api/rhythms`.
- Add a metronome (a simple `Handler`-based click loop synced to a rhythm's
  `defaultBpm` is enough to start).
- Consider velocity from touch *position within* a zone, not just distance
  from center, for more expressive dynamics later.
