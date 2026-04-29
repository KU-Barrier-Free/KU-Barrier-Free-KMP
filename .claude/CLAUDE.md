# KU-Barrier-Free KMP (쿠맵)

건국대학교 장애학생을 위한 배리어프리 캠퍼스 지도 앱 (Kotlin Multiplatform)

## 기술 스택

| 분류 | 기술 |
|------|------|
| **Language** | Kotlin 2.2.20 |
| **UI** | Compose Multiplatform 1.7.3 |
| **Architecture** | MVVM + Repository Pattern |
| **DI** | Koin 4.1.1 (Manual DI for commonMain) |
| **Network** | Ktor 3.0.3 + Kotlinx Serialization |
| **Navigation** | JetBrains Navigation Compose 2.8.0-alpha12 |
| **Maps** | Google Maps Compose (Android) / Google Maps SDK via CocoaPods (iOS) |
| **Image** | Coil 3.1.0 |
| **Logging** | Napier 2.7.1 |
| **Analytics** | Firebase Crashlytics (Android) |

## 프로젝트 구조

```
KU-Barrier-Free-KMP/
├── composeApp/                      # KMP 공유 모듈
│   └── src/
│       ├── commonMain/kotlin/.../   # 공통 코드
│       │   ├── core/
│       │   │   ├── data/
│       │   │   │   ├── di/          # Koin 모듈 (Manual)
│       │   │   │   ├── repository/  # Repository 인터페이스
│       │   │   │   └── repositoryimpl/
│       │   │   ├── network/
│       │   │   │   ├── service/     # Ktor API Service
│       │   │   │   └── response/    # DTO
│       │   │   ├── mapper/
│       │   │   ├── model/           # 공통 모델 (LatLng 등)
│       │   │   ├── navigation/      # Type-safe Routes
│       │   │   ├── designsystem/    # 테마, 공통 컴포넌트
│       │   │   └── ui/util/         # PlatformContext (expect)
│       │   └── feature/
│       │       ├── home/            # 홈(지도) - expect/actual
│       │       ├── building/        # 건물 정보
│       │       ├── room/            # 방 정보
│       │       └── helper/          # 도움말
│       ├── androidMain/kotlin/.../  # Android actual 구현
│       └── iosMain/kotlin/.../      # iOS actual 구현
├── androidApp/                      # Android 앱 진입점
└── iosApp/                          # iOS 앱 (Xcode)
```

## expect/actual 패턴

플랫폼별 구현이 필요한 기능:

| expect | Android actual | iOS actual |
|--------|---------------|------------|
| `PlatformContext` | Context wrapper | UIViewController wrapper |
| `HomeRoute` | Google Maps Compose | Google Maps SDK (UIKitView) |
| `MapComponent` | Google Maps Compose | Google Maps SDK (UIKitView) |
| `createHttpClient()` | Android engine | Darwin engine |

### PlatformContext 사용법
```kotlin
// commonMain (expect)
expect class PlatformContext
@Composable expect fun getPlatformContext(): PlatformContext
expect fun PlatformContext.showToast(message: String)
expect fun PlatformContext.copyToClipboard(text: String)
expect fun PlatformContext.openPhoneDialer(phoneNumber: String)

// Screen에서 사용
@Composable
fun SomeScreen() {
    val context = getPlatformContext()
    Button(onClick = { context.showToast("완료") }) { ... }
}
```

### iOS UIKitView 패턴 (중요)
iOS에서 `UIKitView`로 네이티브 뷰(GMSMapView 등) 사용 시 주의사항:

```kotlin
@Composable
fun MapComponent(..., onMapClick: () -> Unit) {
    // 1. Delegate를 remember로 관리 (가비지 컬렉션 방지)
    val delegate = remember {
        object : NSObject(), GMSMapViewDelegateProtocol {
            override fun mapView(...) {
                // currentOnMapClick.value() 사용
            }
        }
    }

    // 2. 콜백은 rememberUpdatedState로 관리 (최신 콜백 접근)
    val currentOnMapClick = rememberUpdatedState(onMapClick)

    // 3. 상태도 remember로 관리 (delegate에서 최신 상태 접근)
    val markerInfoState = remember { mutableStateOf<MarkerInfo?>(null) }

    UIKitView(
        factory = {
            val mapView = GMSMapView(...)
            mapView.delegate = delegate  // remember된 delegate 사용
            mapView
        },
        update = { mapView ->
            markerInfoState.value = selectedMarkerInfo  // 상태 업데이트
            // ... 마커 렌더링
        }
    )
}
```

**핵심 포인트:**
- `factory` 블록은 한 번만 실행됨 → 내부에서 캡처한 값은 갱신 안 됨
- Delegate, 콜백을 `remember`/`rememberUpdatedState`로 관리 필수
- Kotlin Native에서 delegate가 가비지 컬렉션될 수 있음 주의

## 주요 화면

| Route | 화면 | 설명 |
|-------|------|------|
| `Home` | 홈 | 캠퍼스 지도, 건물 마커, 검색 |
| `BuildingInfo(number)` | 건물 정보 | 층별 정보, 편의시설 |
| `RoomInfo(buildingId, spaceId)` | 방 정보 | 상세 공간 정보 |
| `Helper` | 도움말 | 장애학생 지원 정보 |

## 코딩 컨벤션

### 네이밍
- ViewModel: `*ViewModel`
- Repository: `*Repository` / `*RepositoryImpl`
- Screen: `*Screen`
- Component: `*Component` 또는 기능명
- DTO: `*ResponseDto`
- UI State: `*UiState`
- expect 파일: `*.kt` (commonMain)
- actual 파일: `*.android.kt`, `*.ios.kt`

### State 관리
```kotlin
// ViewModel
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

// Update
_uiState.update { it.copy(isLoading = true) }
```

### Repository 패턴 (Manual Koin)
```kotlin
// 인터페이스
interface HomeRepository {
    suspend fun getHomeData(): Result<HomeData>
}

// 구현체
class HomeRepositoryImpl(
    private val service: HomeService
) : HomeRepository { ... }

// DI 모듈 (commonMain)
val repositoryModule = module {
    single<HomeRepository> { HomeRepositoryImpl(get()) }
}
```

### ViewModel (Manual Koin)
```kotlin
// ViewModel 정의
class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() { ... }

// DI 모듈
val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    // SavedStateHandle이 필요한 경우
    viewModel { (savedStateHandle: SavedStateHandle) ->
        BuildingViewModel(savedStateHandle, get())
    }
}

// Screen에서 사용
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
)
```

### 에러 핸들링
```kotlin
repository.getData().fold(
    onSuccess = { data -> /* 처리 */ },
    onFailure = { error -> Napier.e("Error", error) }
)
```

## 빌드 & 실행

```bash
# Android 디버그 빌드
./gradlew :composeApp:assembleDebug

# Android 릴리즈 빌드
./gradlew :composeApp:assembleRelease

# iOS (Xcode에서 빌드)
# CocoaPods 사용 → 반드시 .xcworkspace로 열어야 함 (.xcodeproj 아님)
open iosApp/iosApp.xcworkspace

# 테스트
./gradlew :composeApp:test
```

## 주의사항

1. **Google Maps API 키** 필요 (`local.properties`)
2. **Firebase 설정** 필요 (`androidApp/google-services.json`)
3. **iOS 빌드** Xcode 15+ 필요
4. **expect/actual** commonMain에서 Android/iOS 특정 코드 사용 금지

---

## 이슈 컨벤션

### 이슈 제목
```
[타입] 간단한 설명
```

### 타입
| 타입 | 설명 |
|------|------|
| `Feature` | 새로운 기능 |
| `Bug` | 버그 리포트 |
| `Refactor` | 리팩토링 |
| `Chore` | 설정, 빌드, 의존성 |
| `Docs` | 문서 |

### 이슈 본문 템플릿
```markdown
## 설명
<!-- 무엇을 해야 하는지 -->

## 작업 내용
- [ ] 작업 1
- [ ] 작업 2

## 참고
<!-- 스크린샷, 링크 등 -->
```

---

## 커밋 컨벤션

### 커밋 메시지 형식
```
<type>: <subject> #<issue-number>
```

### 타입
| 타입 | 설명 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 | `feat: 건물 검색 기능 구현 #12` |
| `fix` | 버그 수정 | `fix: 마커 클릭 시 크래시 수정 #15` |
| `refactor` | 코드 리팩토링 | `refactor: HomeViewModel 분리 #20` |
| `chore` | 설정, 빌드, 의존성 | `chore: Koin 버전 업데이트` |
| `docs` | 문서 수정 | `docs: README 업데이트` |
| `style` | 코드 포맷팅 | `style: ktlint 적용` |
| `test` | 테스트 코드 | `test: HomeViewModel 테스트 추가` |

### 규칙
- 제목은 50자 이내
- 제목 끝에 마침표 없음
- 본문과 제목 사이 빈 줄
- 이슈 번호 연결 권장

---

## PR 컨벤션

### PR 제목
```
[Type] #<issue-number> : <간단한 설명>
```

| Type | 설명 |
|------|------|
| `Feat` | 새로운 기능 |
| `Fix` | 버그 수정 |
| `Refactor` | 리팩토링 |
| `Chore` | 설정, 빌드 |
| `Docs` | 문서 |

### PR 본문 템플릿
```markdown
## 🚀 이슈번호
- closed #<issue-number>

## ✏️ 변경사항
- 변경 내용 1
- 변경 내용 2

## 📷 스크린샷
<img src="" width="360"/>

## ✍️ 사용법
<!-- 테스트 방법 또는 사용법 -->

## 🎸 기타
<!-- 리뷰어에게 전달할 내용 -->
```

### 브랜치 네이밍
```
<type>/#<issue-number>-<간단한-설명>
```

| 타입 | 예시 |
|------|------|
| `feat` | `feat/#12-building-search` |
| `fix` | `fix/#15-marker-crash` |
| `refactor` | `refactor/#20-viewmodel-split` |
| `chore` | `chore/#24-compose-multiplatform-migration` |

---

## 중요 규칙

- **PR 베이스 브랜치는 항상 `develop`**
- **커밋, 이슈, PR에 Claude/AI 관련 문구 절대 금지**
  - `Co-Authored-By: Claude` 넣지 말 것
  - `Generated with Claude Code` 넣지 말 것
  - AI가 작성했다는 어떤 언급도 하지 말 것
