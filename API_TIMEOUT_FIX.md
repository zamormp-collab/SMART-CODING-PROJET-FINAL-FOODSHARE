# 🔧 API Timeout Fix — Rapport de Correction

## Problème Identifié

L'API était en attente ("pending") après login et register. Les appels API ne retournaient jamais ou prenaient un temps très long.

### Causes Principales

1. **Aucun timeout configuré** — `RetrofitClient` n'avait pas d'`OkHttpClient` configuré sur l'instance Retrofit principale
   - Les timeouts par défaut OkHttp sont infinis ou très longs
   - Résultat : appels API bloqués indéfiniment si le serveur n'est pas accessible

2. **Pas de logging** — Impossible de debugger les appels réseau
   - Pas de visibilité sur ce qui se passe lors des requêtes

3. **Pas de gestion des erreurs réseau** — Les erreurs de timeout ou connexion n'étaient pas capturées clairement
   - Messages d'erreur génériques non utiles pour le diagnostic

4. **URL ngrok inaccessible** —  `https://naturist-gab-discharge.ngrok-free.dev` peut être down ou expirée
   - ngrok-free URLs expirent après 2 heures sans accès

---

## Solutions Implémentées

### 1️⃣ RetrofitClient.kt — Timeouts et Logging

**Avant :**
```kotlin
private val retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()  // ❌ Aucun OkHttpClient, pas de timeouts
}
```

**Après :**
```kotlin
private val baseHttpClient: OkHttpClient by lazy {
    val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d(TAG, message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY  // ✅ Log les requêtes/réponses
    }

    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)     // ✅ Timeout de connexion
        .readTimeout(30, TimeUnit.SECONDS)        // ✅ Timeout de lecture
        .writeTimeout(30, TimeUnit.SECONDS)       // ✅ Timeout d'écriture
        .callTimeout(60, TimeUnit.SECONDS)        // ✅ Timeout global
        .build()
}
```

**Impact :**
- Les appels API timeout après 30-60 secondes au lieu de rester en attente indéfiniment
- Chaque requête/réponse est loggée pour le debugging (visible dans Android Studio Logcat)

---

### 2️⃣ AuthRepository.kt — Gestion Avancée des Erreurs

**Ajouté :**
- Détection spécifique des erreurs de timeout (`SocketTimeoutException`)
- Détection des erreurs de connexion (`ConnectException`)
- Messages d'erreur clairs et actionables
- Logging complet pour le debugging

**Exemple :**
```kotlin
catch (e: SocketTimeoutException) {
    val msg = "Connection timeout: API is not responding. Please check the backend server."
    Log.e(TAG, msg, e)
    Result.failure(Exception(msg))
}
```

**Résultat :**
- L'utilisateur reçoit des messages clairs (ex : "Connection timeout")
- Les logs permettent d'identifier rapidement le problème

---

### 3️⃣ AuthViewModel.kt — Logging et Gestion d'Erreurs HTTP

**Ajouté :**
- Logging de chaque étape du login
- Handling spécifique des codes HTTP (401 = unauthorized, 404 = user not found)
- Messages d'erreur clairs affichés à l'utilisateur

**Exemple :**
```kotlin
uiState = when {
    response.isSuccessful -> LoginState.Success
    response.code() == 401 -> LoginState.Error("Invalid email or password")
    response.code() == 404 -> LoginState.Error("User not found")
    else -> LoginState.Error(response.message())
}
```

---

### 4️⃣ build.gradle.kts — Dépendances

**Ajouté :**
```kotlin
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

---

## 📋 How to Debug (avec les corrections)

### 1. Ouvrir Android Studio Logcat

Dans Android Studio : **Logcat** tab → Filter par `RetrofitClient` ou `AuthViewModel`

### 2. Lancer un Login

Les logs afficheront maintenant :
```
D/RetrofitClient: --> POST https://naturist-gab-discharge.ngrok-free.dev/api/auth/connexion
D/RetrofitClient: Content-Type: application/json; charset=utf-8
D/RetrofitClient: {"email":"test@test.com","password":"password"}
D/RetrofitClient: --> END POST (45 bytes)
D/AuthViewModel: login() called with email=test@test.com
D/RetrofitClient: <-- 200 OK https://... (120ms)
D/RetrofitClient: {"token":"abc123..."}
D/AuthViewModel: API response code: 200
D/AuthViewModel: Login successful
```

### 3. Si Timeout

```
E/AuthViewModel: SocketTimeoutException: Connection timeout: API is not responding...
E/RetrofitClient: Read timed out after 30000 ms
```

---

## 🚨 Actions à Prendre Maintenant

### 1. **Vérifier l'URL de l'API**

L'URL ngrok-free actuelle `https://naturist-gab-discharge.ngrok-free.dev` peut être expirée.

**Options :**
- A) Vérifier que le serveur backend est démarré
- B) Générer une nouvelle URL ngrok :
  ```bash
  ngrok http 8080
  # Copier l'URL https://... et la mettre dans RetrofitClient.kt (ligne 17)
  ```
- C) Si développement local sur émulateur, utiliser `http://10.0.2.2:8080` (émulateur Android voir l'hôte via cette IP)
- D) Si device réel, utiliser l'IP locale du PC (ex : `http://192.168.1.50:8080`)

### 2. **Démarrer le Backend API**

S'assurer que le serveur backend est en cours d'exécution sur le port 8080 (ou le port configuré).

### 3. **Tester en Local**

```powershell
# Build et deployer sur émulateur/device
./gradlew.bat clean :app:assembleDebug

# Ouvrir Android Studio et run l'app
```

---

## 📊 Branch et Commits

- **Branch :** `integration/android-restore`
- **Commit :** `78920fd` — fix(api): add timeouts, logging interceptor, and improved error handling

**Pour tester :**
```powershell
git checkout integration/android-restore
./gradlew.bat clean :app:assembleDebug
# Ouvrir dans Android Studio et tester login/register
```

---

## ✅ Checklist de Validation

- [ ] Observer les logs Logcat lors du login
- [ ] Vérifier que les timeouts s'appliquent (pas d'attente infinie)
- [ ] Tester avec API inaccessible → vérifier message "Connection timeout" après 30s
- [ ] Tester avec API accessible → vérifier login réussit
- [ ] Vérifier les messages d'erreur affichés à l'utilisateur sont clairs
- [ ] Mettre en production la version fixée (push vers develop + PR)

---

## 📚 Ressources

- [OkHttp Timeouts configuring](https://square.github.io/okhttp/features/timeouts/)
- [Retrofit + OkHttp Integration](https://square.github.io/retrofit/)
- [ngrok Documentation](https://ngrok.com/docs)


