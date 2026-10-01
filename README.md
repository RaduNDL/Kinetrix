# Kinetix – proiect de disertație

## Pornire pentru telefon prin Wi-Fi sau USB

1. Pornește Docker Desktop.
2. În Android Studio, conectează telefonul prin **Wireless debugging / Pair devices using Wi-Fi** sau prin USB.
3. Din rădăcina proiectului rulează:

   ```powershell
   powershell -ExecutionPolicy Bypass -File .\Start-Kinetix.ps1
   ```

4. Rulează aplicația **debug** din Android Studio. Backendul este accesat la `http://127.0.0.1:5068/`, prin redirecționarea portului făcută de script. Funcționează și pentru emulatorul conectat prin adb.

Rulează scriptul din nou după reconectarea telefonului, oprirea depanării Wi-Fi sau repornirea calculatorului. Dacă scriptul spune că nu există dispozitive, reconectează telefonul în Android Studio. Pentru a porni doar backendul folosește `-BackendOnly`.

### Wi-Fi fără depanare Android

Pentru acces direct în rețeaua locală, adaugă în `KinetixApp/local.properties`:

```properties
kinetix.baseUrl=http://IP-UL-CALCULATORULUI:5068/
```

Înlocuiește valoarea cu adresa IPv4 reală a calculatorului, reconstruiește aplicația și permite accesul la portul 5068 în firewall pentru rețeaua privată. Telefonul și calculatorul trebuie să fie în aceeași rețea. `10.0.2.2` funcționează pentru emulatorul Android Studio, nu pentru telefonul fizic. Configurația prin script evită dependența de adresa LAN și firewall.

Adresa poate fi configurată și cu `-Pkinetix.baseUrl=...` sau variabila `KINETIX_BASE_URL`. Pentru emulator fără redirecționare folosește `http://10.0.2.2:5068/`. Un build release cere explicit `kinetix.releaseBaseUrl` sau `KINETIX_RELEASE_BASE_URL`, cu un backend HTTPS real.

## Backend și email

Configurația locală este în `C#/FitnessApp/.env`, exclusă din Git și din imaginea Docker. Dacă lipsește, copiază `.env.example` și completează SQL, cheia JWT și contul SMTP. Valorile din exemplu sunt pentru dezvoltare; folosește secrete proprii pentru publicare. Pentru Gmail, folosește o parolă de aplicație, cu verificarea în doi pași activată.

În `C#/FitnessApp`:

```powershell
docker compose ps
docker compose logs --tail 100 api db
Invoke-RestMethod http://127.0.0.1:5068/api/health
```

`api` și `database` trebuie să fie `ok`, iar containerele să fie `healthy`. Scriptul reconstruiește API-ul când codul se schimbă. Nu folosi `docker compose down -v` pentru depanare: șterge volumul cu datele SQL.

## Register / verificare / login

1. `POST /api/auth/register` creează contul **neverificat** și trimite codul prin email.
2. Aplicația afișează ecranul de verificare; codul are șase cifre și expiră în 15 minute.
3. `POST /api/auth/verify-email` confirmă accesul la adresă, activează contul, trimite emailul de bun venit și întoarce sesiunea autentificată.
4. `POST /api/auth/login` refuză conturile noi neverificate și trimite utilizatorul la ecranul pentru cod.
5. `POST /api/auth/resend-verification` retrimite codul; pauză de 60 de secunde între trimiteri reușite. După cinci coduri greșite trebuie cerut un cod nou.

Se acceptă adrese de la orice furnizor. Validarea formatului nu dovedește că o adresă există; introducerea codului dovedește accesul la acea adresă. Acceptarea mesajului de către SMTP nu garantează că apare în Inbox; verifică și Spam.

Dacă SMTP nu funcționează la înregistrare, API-ul returnează o eroare explicită, contul rămâne neverificat și se poate cere un cod nou. Emailul de bun venit este trimis după verificare; dacă acea trimitere eșuează, contul rămâne activ, iar eroarea este în loguri. Nu există încă o coadă persistentă de retrimitere a mesajelor de bun venit.

Conturile istorice deja marcate verificate rămân în starea existentă. Migrations sunt păstrate; nu șterge istoricul deja aplicat bazei de date.

## Verificări automate

```powershell
dotnet test .\C#\FitnessApp\FitnessApp.slnx
cd KinetixApp
.\gradlew.bat :app:assembleDebug
```

Testele backendului folosesc o bază SQLite temporară în memorie și un expeditor de email capturat; nu trimit emailuri reale și nu ating baza existentă.

## Starea componentelor

- `C#/FitnessApp`: API, autentificare, SQL Server, email. ProfileController, BiometricController, BiometricService și BiometricLogDto sunt încă schelete.
- `KinetixApp`: aplicația Android. Camera/MediaPipe, sumarul antrenamentelor, panoul biometric și preferințele Health Connect nu sunt încă implementate complet.
- `Python`: structură rezervată pentru viitoare componente ML; în prezent nu conține un serviciu backend funcțional.

Documentație: [rețeaua emulatorului](https://developer.android.com/studio/run/emulator-networking-address), [depanare Android prin Wi-Fi](https://developer.android.com/tools/adb), [starea și ordinea serviciilor Docker](https://docs.docker.com/compose/how-tos/startup-order), [parole de aplicație Gmail](https://support.google.com/mail/answer/185833?hl=en).
