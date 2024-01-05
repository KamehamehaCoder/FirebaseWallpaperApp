# FirebaseWallpaperApp - Android

**FirebaseWallpaperApp** is a wallpaper app module that fetches wallpapers from Firebase and displays them in the app. The app allows users to set wallpapers as their home screen, lock screen, or both, and also includes sharing functionality. It's built using Java and utilizes Firebase for efficient and secure data storage. Fork the repository to contribute to the development of additional features.

## Installation

1. Clone this repository and sync Gradle.
2. Create an account in Firebase and obtain the `google-services.json`.
3. Paste the `google-services.json` inside the `src` directory in project view (remove the existing one).
4. Upload wallpapers to Firebase Storage.
5. Create a child class in the Realtime Database (named "images").
6. Click on the uploaded wallpaper and get the Access Token.
7. Copy the Access Token and upload it in the Realtime Database.
8. Put anything in the Key Section and paste the Access Token in the value.
9. Inside it, upload all the links of wallpapers that you have uploaded in Firebase Storage.
10. Run the app, but before that, make sure to change the app name as you desire.
