# Project 3: Secure Offline Vault 🔐

An air-gapped, zero-internet offline password, API token, and developer key vault built with **100% Java**, **Android Keystore AES-256 GCM**, **BiometricPrompt**, and **Clean MVVM Architecture**.

## Features
- **Hardware-Backed AES-256 GCM Encryption**: Uses Android Keystore (KeyGenParameterSpec) with randomized IVs and 128-bit authentication tags.
- **Biometric Authentication**: Fingerprint and Face Unlock powered by ndroidx.biometric.BiometricPrompt.
- **Air-Gapped Security**: Zero internet permissions requested (INTERNET permission omitted).
- **Offline Persistence**: Encrypted local SQLite database storage.
