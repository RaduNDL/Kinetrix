# Kinetix – Dissertation Project

## Running on a Phone over Wi-Fi or USB

1. Start Docker Desktop.
2. In Android Studio, connect your phone using **Wireless debugging / Pair devices using Wi-Fi** or USB.
3. From the project root, run:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\Start-Kinetix.ps1
   ```

4. Run the **debug** app from Android Studio. The backend is accessed at `http://127.0.0.1:5068/` through the port forwarding configured by the script. This also works for an emulator connected through adb.

Run the script again after reconnecting your phone, turning off wireless debugging, or restarting your computer. If the script reports that no devices are connected, reconnect your phone in Android Studio. To start only the backend, use `-BackendOnly`.

### Wi-Fi without Android Debugging

For direct access over the local network, add the following to `KinetixApp/local.properties`:

```properties
kinetix.baseUrl=http://YOUR-COMPUTER-IP:5068/
```

Replace the placeholder with your computer's actual IPv4 address, rebuild the app, and allow access to port 5068 through the firewall on the private network. Your phone and computer must be on the same network. `10.0.2.2` works for the Android Studio emulator, but not for a physical phone. The script-based configuration avoids reliance on the LAN address and firewall configuration.

The address can also be configured using `-Pkinetix.baseUrl=...` or the `KINETIX_BASE_URL` environment variable. For an emulator without port forwarding, use `http://10.0.2.2:5068/`. A release build explicitly requires `kinetix.releaseBaseUrl` or `KINETIX_RELEASE_BASE_URL`, pointing to a real HTTPS backend.

## Backend and Email

Local configuration is stored in `C#/FitnessApp/.env`, which is excluded from Git and the Docker image. If it is missing, copy `.env.example` to `.env` and configure SQL, the JWT key, and the SMTP account. The example values are intended for development; use your own secrets for deployment. For Gmail, use an app password with two-step verification enabled.

From `C#/FitnessApp`, run:

```powershell
docker compose ps
docker compose logs --tail 100 api db
Invoke-RestMethod http://127.0.0.1:5068/api/health
```

The `api` and `database` health checks should both report `ok`, and the containers should be `healthy`. The script rebuilds the API when the code changes. Do not use `docker compose down -v` for troubleshooting: it deletes the volume containing the SQL data.

## Registration / Verification / Login

1. `POST /api/auth/register` creates an **unverified** account and sends a verification code by email.
2. The app displays the verification screen; the code has six digits and expires after 15 minutes.
3. `POST /api/auth/verify-email` confirms access to the email address, activates the account, sends the welcome email, and returns an authenticated session.
4. `POST /api/auth/login` rejects newly registered, unverified accounts and directs the user to the verification code screen.
5. `POST /api/auth/resend-verification` resends the code, with a 60-second cooldown between successful sends. After five incorrect code attempts, a new code must be requested.

Email addresses from any provider are accepted. Format validation does not prove that an address exists; entering the verification code proves access to that address. SMTP acceptance does not guarantee delivery to the Inbox; check Spam as well.

If SMTP fails during registration, the API returns an explicit error, the account remains unverified, and a new code can be requested. The welcome email is sent after verification; if that send fails, the account remains active and the error is logged. There is currently no persistent retry queue for welcome emails.

Existing accounts already marked as verified retain their current status. Migrations are preserved; do not delete migration history that has already been applied to the database.

## Automated Checks

```powershell
dotnet test .\C#\FitnessApp\FitnessApp.slnx
cd KinetixApp
.\gradlew.bat :app:assembleDebug
```

Backend tests use a temporary in-memory SQLite database and a test email sender that captures outgoing messages. They do not send real emails or access the existing database.

## Component Status

- `C#/FitnessApp`: API, authentication, SQL Server, and email. ProfileController, BiometricController, BiometricService, and BiometricLogDto are still skeleton implementations.
- `KinetixApp`: Android app. Camera/MediaPipe, workout summaries, the biometric dashboard, and Health Connect preferences are not yet fully implemented.
- `Python`: Structure reserved for future ML components; it currently does not contain a functional backend service.

Documentation: [emulator networking](https://developer.android.com/studio/run/emulator-networking-address), [Android debugging over Wi-Fi](https://developer.android.com/tools/adb), [Docker service health and startup order](https://docs.docker.com/compose/how-tos/startup-order), [Gmail app passwords](https://support.google.com/mail/answer/185833?hl=en).
