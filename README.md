# QR Barcode Generator & Scanner

A clean, minimal Android app for generating and scanning QR codes and barcodes. No ads, no accounts, no unnecessary permissions.

[![Get it on Google Play](https://img.shields.io/badge/Google_Play-Available-green?logo=google-play&logoColor=white)](https://play.google.com/store/apps/details?id=com.x_creations.qrandbarcode_generatorandscanner)
![Downloads](https://img.shields.io/badge/Downloads-500%2B-blue)
![Platform](https://img.shields.io/badge/Platform-Android-lightgrey?logo=android)
![Language](https://img.shields.io/badge/Language-Java-orange?logo=java)

---

## What It Does

Two tabs, two functions:

**Generator** — Type any text and instantly generate a QR code and a barcode side by side. Both can be saved to your device or shared directly to any app.

**Scanner** — Scan a QR code or barcode using your camera in real time, or decode one from an image already on your device. Results are displayed immediately with one-tap options to copy to clipboard or open URLs directly in a browser.

---

## Key Features

- **Dual-format generation** — Produces both QR code and barcode from a single text input simultaneously
- **Image-based scanning** — Import any image from your gallery and decode the barcode or QR code it contains, without needing to point a camera at it
- **Direct URL handling** — Scanned results that are URLs can be opened in the browser with a single tap
- **Clipboard integration** — One tap copies any scanned result to the clipboard
- **Haptic feedback** — Vibration on successful scan and on launch from a scanned QR code
- **Share and save** — Generated codes can be exported as image files or shared via Android's share sheet
- **No ads, no accounts, no bloat** — Installs, opens, and works immediately

---

## Technical Implementation

Built using the Android Navigation Component for the two-fragment tab layout. QR code and barcode generation uses the **ZXing** (Zebra Crossing) library via `MultiFormatWriter` and `BarcodeEncoder`. Camera scanning uses `DecoratedBarcodeView` for the live scanner. Image-based decoding reads the selected bitmap, converts it to a `LuminanceSource`, and passes it through `MultiFormatReader` for format-agnostic detection. Results are handled with `AlertDialog` for URL disambiguation.

**Stack:** Java · Android SDK · ZXing · Android Navigation Component · FileProvider · MediaStore

---

## Screenshots

*Add screenshots here showing the generator tab, scanner tab, and a decoded result.*

---

## Installation

Available on the [Google Play Store](https://play.google.com/store/apps/details?id=com.x_creations.qrandbarcode_generatorandscanner). Requires Android 5.0 (Lollipop) or higher.

To build from source:
```bash
git clone https://github.com/thedemogorgon3/qr-barcode-generator-scanner
```
Open in Android Studio and run on a device or emulator.
