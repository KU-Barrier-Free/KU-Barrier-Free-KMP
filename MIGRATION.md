# Compose Multiplatform 마이그레이션 가이드

## 완료된 작업

### Phase 1: 프로젝트 구조 설정 ✅

#### 1. 프로젝트 구조 변경
```
KU-Barrier-Free-KMP/
├── app/                    # 기존 앱 (백업용)
├── composeApp/             # KMP 공유 앱
│   └── src/
│       ├── commonMain/     # 공통 코드
│       ├── androidMain/    # Android 전용
│       └── iosMain/        # iOS 전용
└── iosApp/                 # iOS 앱 진입점
```

#### 2. Gradle 설정 완료
- `settings.gradle.kts` - composeApp 모듈 포함
- `build.gradle.kts` (루트) - KMP 플러그인 설정
- `composeApp/build.gradle.kts` - KMP 설정
- `libs.versions.toml` - KMP 의존성 추가

### Phase 2: 수동 DI 설정 ✅

#### AppModule.kt 생성
```kotlin
// composeApp/src/commonMain/kotlin/com/ganaljigi/kubf/core/di/AppModule.kt
val networkModule = module {
    single { provideJson() }
    single { provideHttpClient(get()) }
}

val apiModule = module {
    single { HomeApi(get()) }
    single { BuildingApi(get()) }
    // ...
}

val repositoryModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    // ...
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    // ...
}

val appModules = listOf(networkModule, apiModule, repositoryModule, viewModelModule)
```

#### NetworkModule expect/actual
```kotlin
// commonMain
expect fun createPlatformHttpClient(): HttpClient
expect fun getBaseUrl(): String
expect fun isDebug(): Boolean

// androidMain
actual fun createPlatformHttpClient(): HttpClient = HttpClient(Android)
actual fun getBaseUrl(): String = BuildConfig.BASE_URL
actual fun isDebug(): Boolean = BuildConfig.DEBUG

// iosMain
actual fun createPlatformHttpClient(): HttpClient = HttpClient(Darwin)
actual fun getBaseUrl(): String = NSBundle.mainBundle.objectForInfoDictionaryKey("BASE_URL")
actual fun isDebug(): Boolean = Platform.isDebugBinary
```

#### KUBFApplication.kt (Android)
```kotlin
class KUBFApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@KUBFApplication)
            modules(appModules)
        }
    }
}
```

---

## 남은 작업

### 1. koinViewModel import 변경
모든 Screen 파일에서 import 변경 필요:
```kotlin
// 변경 전
import org.koin.androidx.compose.koinViewModel

// 변경 후
import org.koin.compose.viewmodel.koinViewModel
```

**해당 파일:**
- `HomeScreen.kt`
- `BuildingInfoScreen.kt`
- `HelperScreen.kt`
- `RoomInfoScreen.kt`
- `NavBackstackUtil.kt`
- `SearchComponent.kt`

### 2. expect/actual 구현 필요

#### MapComponent
```kotlin
// commonMain
@Composable
expect fun MapComponent(
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    // ... params
)
```

#### NoticeWebView (PlatformWebView)
```kotlin
// commonMain
@Composable
expect fun PlatformWebView(url: String, modifier: Modifier)
```

#### LocationUtil
```kotlin
// commonMain
expect suspend fun ensureLocationPermission(): Boolean
expect suspend fun getLocationWithPermission(): Pair<Double, Double>?
```

### 3. ViewModel SavedStateHandle 제거
KMP에서 SavedStateHandle 미지원으로 다른 방식 필요:

```kotlin
// 변경 전
class BuildingViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: BuildingInfoRepository,
) : BaseViewModel<BuildingUiEvent>() {
    private val buildingId: Long = savedStateHandle.toRoute<Routes.BuildingInfo>().number
}

// 변경 후 - 파라미터로 전달
class BuildingViewModel(
    private val buildingId: Long,
    private val repository: BuildingInfoRepository,
) : BaseViewModel<BuildingUiEvent>()
```

### 4. R.drawable/R.font → Compose Resources
```kotlin
// 리소스 이동
composeApp/src/commonMain/composeResources/drawable/
composeApp/src/commonMain/composeResources/font/

// 코드 변경
painterResource(Res.drawable.ic_building)
FontFamily(Font(Res.font.pretendard_bold))
```

---

## 현재 빌드 오류 목록

| 오류 | 해결 방법 |
|------|----------|
| koinViewModel import | `org.koin.compose.viewmodel.koinViewModel` |
| NoticeWebView | expect/actual 구현 |
| MapComponent | expect/actual 구현 |
| LocationUtil | expect/actual 구현 |
| SavedStateHandle | 파라미터 방식으로 변경 |
| R.drawable/R.font | Compose Resources 사용 |

---

## 빌드 명령어

```bash
# Android 빌드
./gradlew :composeApp:assembleDebug

# iOS Framework 빌드
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# 전체 빌드 확인
./gradlew :composeApp:build
```

---

## 파일 변경 요약

### 생성된 파일
- `composeApp/build.gradle.kts`
- `composeApp/src/commonMain/kotlin/.../core/di/AppModule.kt`
- `composeApp/src/commonMain/kotlin/.../core/di/NetworkModule.kt`
- `composeApp/src/androidMain/kotlin/.../core/di/NetworkModule.android.kt`
- `composeApp/src/iosMain/kotlin/.../core/di/NetworkModule.ios.kt`
- `composeApp/src/androidMain/kotlin/.../KUBFApplication.kt`
- `composeApp/src/iosMain/kotlin/.../MainViewController.kt`
- `iosApp/iosApp/iOSApp.swift`
- `iosApp/iosApp/ContentView.swift`

### 수정된 파일
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle/libs.versions.toml`
- `.gitignore`
- 모든 `*Api.kt` - @Single 제거
- 모든 `*RepositoryImpl.kt` - @Single 제거
- 모든 `*ViewModel.kt` - @KoinViewModel 제거

### 삭제된 파일
- `composeApp/src/androidMain/kotlin/.../App.kt` (KUBFApplication.kt로 대체)
- `composeApp/src/commonMain/kotlin/.../core/data/di/NetworkModule.kt` (core/di로 이동)

---

## 참고 프로젝트

- [philipplackner/CMPMemeCreator](https://github.com/philipplackner/CMPMemeCreator)
- [JetBrains/compose-multiplatform](https://github.com/JetBrains/compose-multiplatform)
