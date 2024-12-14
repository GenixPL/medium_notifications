## Required steps

### 1

Swap the `google-services.json` [file](app/google-services.json) for yours (you get it from Firebase).

### 2

Android package name has to match the one registered in Firebase, change this `applicationId` inside the app-level `build.gradle.kts` [file](app/build.gradle.kts).

```
applicationId = "com.genix.notifications_example"
```

(Do NOT touch the `namespace` unless you know what you do.)

### 3 (optional)

You might have to change the address of your service; current one points to your (computer's) `localhost:8080/token` (I'm assuming you are running this on an emulator).

Look for this line inside the main [file](app/src/main/java/com/genix/notifications_example/MainActivity.kt).

```
  // This is your localhost:8080,
  // see: https://stackoverflow.com/a/5495789
  val url = URL("http://10.0.2.2:8080/token")
```

### 4

Once you run the app, make sure that you give it notifications permission! You can use system settings, or the button I've added.

## Disclaimer

THE app doesn't (visually) support foreground notifications (I haven't set it up), so if you want the system thingies to appear, you have to move it to background.
