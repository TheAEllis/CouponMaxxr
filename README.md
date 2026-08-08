# 🛒 CouponMaxxr

CouponMaxxr is a lightweight, automated digital coupon clipper for Android. It uses a custom-built headless WebView architecture to intelligently navigate grocery store interfaces, detect active sessions, and automatically clip every available digital coupon directly to your account.

**Currently Supported Stores:**
* Publix
* Food Lion

## ✨ Features
* **Zero-Friction UI:** App-like storefront launchers that seamlessly manage your web sessions.
* **Smart Session Management:** Detects valid authentication tokens (like Peapod's `ppdtk` and Publix's `.pblx.auth`) and automatically routes you to the login screen when sessions expire.
* **Stealth Clipping:** Utilizes jitter delays and randomized human-click simulation to reliably clip coupons without triggering anti-bot protections.
* **Stale Cookie Protection:** Reads direct DOM data-layers to verify authentication state, preventing misfires on guest-view pages.

## 📱 How to Install (Sideloading)

Because this app automates web browser interactions, it is not distributed on the Google Play Store. You can easily install it directly from the Releases page.

1. On your Android device, open your web browser and go to this repository's [Releases](../../releases) page.
2. Under the latest release (e.g., `v1.0.0`), tap on `app-debug.apk` to download the file.
3. Open your device's **Files** or **Downloads** app and tap the downloaded `.apk` file.
4. **Grant Permissions:** If prompted with "For your security, your phone is not allowed to install unknown apps from this source," tap **Settings** and toggle on **Allow from this source**.
5. Tap **Install**, then **Open**.

## 🛠️ Building from Source
If you prefer to compile the app yourself:
1. Clone this repository.
2. Open the project in Android Studio.
3. Sync Gradle and click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## ⚠️ Disclaimer
This is an unofficial, open-source tool. It is not affiliated with, endorsed by, or connected to Publix, Food Lion, or Ahold Delhaize.
