# Google 권장 아키텍처 마이그레이션 가이드

> 모듈화 없이 패키지 구조로 core/feature 아키텍처 적용

---

## 목표 구조

```
app/src/main/java/com/ganaljigi/kubf/
├── core/                          # 공통 기능
│   ├── common/                    # 공통 유틸리티
│   │   ├── result/
│   │   │   └── Result.kt          # Result wrapper
│   │   └── extension/
│   │       ├── FlowExt.kt
│   │       └── StringExt.kt
│   │
│   ├── data/                      # 데이터 레이어 공통
│   │   ├── di/
│   │   │   └── NetworkModule.kt
│   │   └── network/
│   │       ├── ApiClient.kt
│   │       ├── BaseResponse.kt
│   │       └── NetworkMonitor.kt
│   │
│   ├── domain/                    # 도메인 레이어 공통
│   │   └── model/
│   │       ├── Building.kt
│   │       ├── Room.kt
│   │       └── Route.kt
│   │
│   ├── designsystem/              # 디자인 시스템
│   │   ├── component/
│   │   │   ├── KubfButton.kt
│   │   │   ├── KubfCard.kt
│   │   │   ├── KubfSearchBar.kt
│   │   │   ├── KubfBottomSheet.kt
│   │   │   └── KubfDialog.kt
│   │   ├── icon/
│   │   │   └── KubfIcons.kt
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Type.kt
│   │       ├── Shape.kt
│   │       └── Theme.kt
│   │
│   └── ui/                        # UI 공통
│       ├── base/
│       │   └── BaseViewModel.kt
│       └── util/
│           ├── UiState.kt
│           └── SnackbarManager.kt
│
├── feature/                       # 기능별 화면
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   ├── HomeUiState.kt
│   │   ├── navigation/
│   │   │   └── HomeNavigation.kt
│   │   ├── component/
│   │   │   ├── MapComponent.kt
│   │   │   ├── BuildingMarker.kt
│   │   │   ├── HomeSearchBar.kt
│   │   │   └── HomeBottomSheet.kt
│   │   ├── data/
│   │   │   ├── repository/
│   │   │   │   ├── HomeRepository.kt
│   │   │   │   └── HomeRepositoryImpl.kt
│   │   │   ├── datasource/
│   │   │   │   └── HomeRemoteDataSource.kt
│   │   │   ├── service/
│   │   │   │   └── HomeService.kt
│   │   │   └── model/
│   │   │       ├── HomeResponseDto.kt
│   │   │       └── HomeMapper.kt
│   │   └── di/
│   │       └── HomeModule.kt
│   │
│   ├── building/
│   │   ├── BuildingInfoScreen.kt
│   │   ├── BuildingViewModel.kt
│   │   ├── BuildingUiState.kt
│   │   ├── navigation/
│   │   │   └── BuildingNavigation.kt
│   │   ├── component/
│   │   │   ├── FloorTabRow.kt
│   │   │   ├── RoomList.kt
│   │   │   └── DoorInfoCard.kt
│   │   ├── data/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── model/
│   │   └── di/
│   │       └── BuildingModule.kt
│   │
│   ├── room/
│   │   ├── RoomInfoScreen.kt
│   │   ├── RoomInfoViewModel.kt
│   │   ├── RoomInfoUiState.kt
│   │   ├── navigation/
│   │   ├── component/
│   │   ├── data/
│   │   └── di/
│   │
│   └── helper/
│       ├── HelperScreen.kt
│       ├── HelperViewModel.kt
│       ├── navigation/
│       ├── component/
│       ├── data/
│       └── di/
│
├── navigation/                    # 앱 네비게이션
│   ├── KubfNavHost.kt
│   └── Routes.kt
│
├── MainActivity.kt
└── App.kt
```

---

## 레이어별 역할

### Core Layer

| 패키지 | 역할 | 포함 내용 |
|--------|------|----------|
| `core/common` | 공통 유틸리티 | Result, Extension 함수 |
| `core/data` | 네트워크 공통 | Retrofit 설정, BaseResponse |
| `core/domain` | 도메인 모델 | 여러 feature에서 공유하는 모델 |
| `core/designsystem` | 디자인 시스템 | 공통 UI 컴포넌트, 테마 |
| `core/ui` | UI 공통 | BaseViewModel, UiState |

### Feature Layer

| 패키지 | 역할 |
|--------|------|
| `feature/*/Screen.kt` | Composable 화면 |
| `feature/*/ViewModel.kt` | 상태 관리 |
| `feature/*/UiState.kt` | UI 상태 클래스 |
| `feature/*/navigation/` | 해당 feature 네비게이션 |
| `feature/*/component/` | 해당 feature 전용 컴포넌트 |
| `feature/*/data/` | 해당 feature 전용 데이터 레이어 |
| `feature/*/di/` | 해당 feature DI 모듈 |

---

## 마이그레이션 단계

### Phase 1: Core 패키지 생성

#### 1.1 core/designsystem 이동
```
현재 위치                          → 이동 위치
ui/theme/Color.kt                 → core/designsystem/theme/Color.kt
ui/theme/Type.kt                  → core/designsystem/theme/Type.kt
ui/theme/Theme.kt                 → core/designsystem/theme/Theme.kt
ui/common/KUBFSearchBar.kt        → core/designsystem/component/KubfSearchBar.kt
ui/common/PermissionDialog.kt     → core/designsystem/component/KubfPermissionDialog.kt
ui/common/ImageViewerDialog.kt    → core/designsystem/component/KubfImageViewerDialog.kt
```

#### 1.2 core/data 이동
```
현재 위치                          → 이동 위치
data/di/NetworkModule.kt          → core/data/di/NetworkModule.kt
data/di/ApiModule.kt              → core/data/di/ApiModule.kt
data/remote/base/BaseResponse.kt  → core/data/network/BaseResponse.kt
```

#### 1.3 core/common 생성
```kotlin
// core/common/result/Result.kt
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val exception: Throwable) : AppResult<Nothing>
    object Loading : AppResult<Nothing>
}

// core/common/extension/FlowExt.kt
fun <T> Flow<T>.asResult(): Flow<AppResult<T>> = ...
```

#### 1.4 core/ui 생성
```kotlin
// core/ui/base/BaseViewModel.kt
abstract class BaseViewModel<S, E>(initialState: S) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    protected fun updateState(reducer: S.() -> S) {
        _uiState.update { it.reducer() }
    }
}

// core/ui/util/UiState.kt
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

---

### Phase 2: Feature 패키지 재구성

#### 2.1 feature/home 이동
```
현재 위치                                    → 이동 위치
ui/home/HomeScreen.kt                       → feature/home/HomeScreen.kt
ui/home/HomeViewModel.kt                    → feature/home/HomeViewModel.kt
ui/home/HomeUiState.kt                      → feature/home/HomeUiState.kt
ui/home/component/MapComponent.kt           → feature/home/component/MapComponent.kt
ui/home/component/search/*                  → feature/home/component/search/*
data/remote/service/HomeService.kt          → feature/home/data/service/HomeService.kt
data/remote/repository/HomeRepository.kt    → feature/home/data/repository/HomeRepository.kt
data/remote/repositoryimpl/HomeRepositoryImpl.kt → feature/home/data/repository/HomeRepositoryImpl.kt
data/remote/response/HomeResponseDto.kt     → feature/home/data/model/HomeResponseDto.kt
mapper/HomeMapper.kt                        → feature/home/data/model/HomeMapper.kt
data/di/RepositoryModule.kt (Home 부분)     → feature/home/di/HomeModule.kt
```

#### 2.2 feature/building 이동
```
현재 위치                                    → 이동 위치
ui/buildinginfo/BuildingInfoScreen.kt       → feature/building/BuildingInfoScreen.kt
ui/buildinginfo/BuildingViewModel.kt        → feature/building/BuildingViewModel.kt
ui/buildinginfo/component/*                 → feature/building/component/*
data/remote/service/BuildingService.kt      → feature/building/data/service/BuildingService.kt
data/remote/repository/BuildingRepository.kt → feature/building/data/repository/BuildingRepository.kt
...
```

#### 2.3 feature/room 이동
```
현재 위치                                    → 이동 위치
ui/roominfo/RoomInfoScreen.kt               → feature/room/RoomInfoScreen.kt
ui/roominfo/RoomInfoViewModel.kt            → feature/room/RoomInfoViewModel.kt
...
```

#### 2.4 feature/helper 이동
```
현재 위치                                    → 이동 위치
ui/helper/HelperScreen.kt                   → feature/helper/HelperScreen.kt
ui/helper/HelperViewModel.kt                → feature/helper/HelperViewModel.kt
...
```

---

### Phase 3: Navigation 통합

#### 3.1 Feature별 Navigation 정의
```kotlin
// feature/home/navigation/HomeNavigation.kt
fun NavGraphBuilder.homeScreen(
    onBuildingClick: (Long) -> Unit,
    onHelperClick: () -> Unit
) {
    composable<Routes.Home> {
        HomeRoute(
            onBuildingClick = onBuildingClick,
            onHelperClick = onHelperClick
        )
    }
}

fun NavController.navigateToHome() {
    navigate(Routes.Home)
}
```

#### 3.2 NavHost 통합
```kotlin
// navigation/KubfNavHost.kt
@Composable
fun KubfNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {
        homeScreen(
            onBuildingClick = { navController.navigateToBuilding(it) },
            onHelperClick = { navController.navigateToHelper() }
        )
        buildingScreen(
            onRoomClick = { buildingId, roomId ->
                navController.navigateToRoom(buildingId, roomId)
            },
            onBackClick = { navController.popBackStack() }
        )
        roomScreen(
            onBackClick = { navController.popBackStack() }
        )
        helperScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
```

---

### Phase 4: DI 모듈 재구성

#### 4.1 Core DI
```kotlin
// core/data/di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = ...

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = ...
}
```

#### 4.2 Feature DI
```kotlin
// feature/home/di/HomeModule.kt
@Module
@InstallIn(ViewModelComponent::class)
abstract class HomeModule {
    @Binds
    abstract fun bindHomeRepository(
        impl: HomeRepositoryImpl
    ): HomeRepository
}

@Module
@InstallIn(SingletonComponent::class)
object HomeServiceModule {
    @Provides
    @Singleton
    fun provideHomeService(retrofit: Retrofit): HomeService =
        retrofit.create(HomeService::class.java)
}
```

---

## 파일별 마이그레이션 체크리스트

### Core

- [ ] `core/common/result/Result.kt`
- [ ] `core/common/extension/FlowExt.kt`
- [ ] `core/common/extension/StringExt.kt`
- [ ] `core/data/di/NetworkModule.kt`
- [ ] `core/data/di/ApiModule.kt`
- [ ] `core/data/network/BaseResponse.kt`
- [ ] `core/data/network/ApiClient.kt`
- [ ] `core/designsystem/theme/Color.kt`
- [ ] `core/designsystem/theme/Type.kt`
- [ ] `core/designsystem/theme/Shape.kt`
- [ ] `core/designsystem/theme/Theme.kt`
- [ ] `core/designsystem/component/KubfSearchBar.kt`
- [ ] `core/designsystem/component/KubfButton.kt`
- [ ] `core/designsystem/component/KubfDialog.kt`
- [ ] `core/ui/base/BaseViewModel.kt`
- [ ] `core/ui/util/UiState.kt`

### Feature - Home

- [ ] `feature/home/HomeScreen.kt`
- [ ] `feature/home/HomeViewModel.kt`
- [ ] `feature/home/HomeUiState.kt`
- [ ] `feature/home/navigation/HomeNavigation.kt`
- [ ] `feature/home/component/MapComponent.kt`
- [ ] `feature/home/component/BuildingMarker.kt`
- [ ] `feature/home/component/HomeSearchBar.kt`
- [ ] `feature/home/component/HomeBottomSheet.kt`
- [ ] `feature/home/data/service/HomeService.kt`
- [ ] `feature/home/data/repository/HomeRepository.kt`
- [ ] `feature/home/data/repository/HomeRepositoryImpl.kt`
- [ ] `feature/home/data/model/HomeResponseDto.kt`
- [ ] `feature/home/data/model/HomeMapper.kt`
- [ ] `feature/home/di/HomeModule.kt`

### Feature - Building

- [ ] `feature/building/BuildingInfoScreen.kt`
- [ ] `feature/building/BuildingViewModel.kt`
- [ ] `feature/building/BuildingUiState.kt`
- [ ] `feature/building/navigation/BuildingNavigation.kt`
- [ ] `feature/building/component/*`
- [ ] `feature/building/data/service/BuildingService.kt`
- [ ] `feature/building/data/repository/*`
- [ ] `feature/building/data/model/*`
- [ ] `feature/building/di/BuildingModule.kt`

### Feature - Room

- [ ] `feature/room/RoomInfoScreen.kt`
- [ ] `feature/room/RoomInfoViewModel.kt`
- [ ] `feature/room/navigation/RoomNavigation.kt`
- [ ] `feature/room/data/*`
- [ ] `feature/room/di/RoomModule.kt`

### Feature - Helper

- [ ] `feature/helper/HelperScreen.kt`
- [ ] `feature/helper/HelperViewModel.kt`
- [ ] `feature/helper/navigation/HelperNavigation.kt`
- [ ] `feature/helper/data/*`
- [ ] `feature/helper/di/HelperModule.kt`

### Navigation

- [ ] `navigation/Routes.kt`
- [ ] `navigation/KubfNavHost.kt`

---

## Import 변경 가이드

마이그레이션 후 import 경로가 변경됩니다:

```kotlin
// Before
import com.ganaljigi.kubf.ui.theme.MainGreen
import com.ganaljigi.kubf.ui.common.KUBFSearchBar
import com.ganaljigi.kubf.data.remote.repository.HomeRepository

// After
import com.ganaljigi.kubf.core.designsystem.theme.MainGreen
import com.ganaljigi.kubf.core.designsystem.component.KubfSearchBar
import com.ganaljigi.kubf.feature.home.data.repository.HomeRepository
```

---

## 주의사항

1. **단계별 진행**: 한 번에 모든 파일을 옮기지 말고, feature 단위로 진행
2. **빌드 확인**: 각 단계 완료 후 빌드 성공 확인
3. **Git 이력**: `git mv` 사용하여 파일 이력 유지
4. **IDE 리팩토링**: Android Studio의 "Move" 기능 활용 (Refactor → Move)

---

## 예상 소요 작업

| Phase | 내용 | 예상 파일 수 |
|-------|------|-------------|
| Phase 1 | Core 패키지 생성 | ~15개 |
| Phase 2 | Feature 패키지 재구성 | ~50개 |
| Phase 3 | Navigation 통합 | ~5개 |
| Phase 4 | DI 모듈 재구성 | ~8개 |
| **Total** | | **~78개** |

---

*이 가이드를 따라 점진적으로 마이그레이션을 진행하세요.*
