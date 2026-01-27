# KU-Barrier-Free KMP (쿠맵)

건국대학교 장애학생을 위한 배리어프리 캠퍼스 지도 앱

## 기술 스택

- **Language**: Kotlin 2.0.0
- **UI**: Jetpack Compose (BOM 2024.09.00)
- **Architecture**: MVVM + Repository Pattern
- **DI**: Koin Annotations 2.0.0
- **Network**: Retrofit 3.0.0 + OkHttp 4.12.0 + Kotlinx Serialization
- **Maps**: Google Maps Compose 4.4.1
- **Navigation**: Type-safe Navigation (Serializable routes)
- **Image**: Coil 3.1.0
- **Analytics**: Firebase Crashlytics

## 프로젝트 구조

```
app/src/main/java/com/ganaljigi/kubf/
├── core/
│   ├── data/
│   │   ├── di/              # Koin 모듈 (AppModule, NetworkModule, ApiModule)
│   │   ├── repository/      # Repository 인터페이스
│   │   └── repositoryimpl/  # Repository 구현체 (@Single)
│   ├── network/
│   │   ├── service/         # Retrofit 서비스
│   │   └── response/        # DTO 모델
│   ├── mapper/              # DTO → Domain 변환
│   ├── navigation/          # 네비게이션 라우트
│   └── designsystem/        # 공통 컴포넌트, 테마
└── feature/
    ├── home/                # 홈(지도) 화면
    ├── building/            # 건물 정보 화면
    ├── room/                # 방 정보 화면
    └── helper/              # 도움말 화면
```

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

### State 관리
```kotlin
// ViewModel
private val _uiState = MutableStateFlow(HomeUiState())
val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

// Update
_uiState.update { it.copy(isLoading = true) }
```

### Repository 패턴
```kotlin
// 인터페이스
interface HomeRepository {
    suspend fun getHomeData(): Result<HomeData>
}

// 구현체 (Koin Annotations)
@Single(binds = [HomeRepository::class])
class HomeRepositoryImpl(
    private val service: HomeService
) : HomeRepository
```

### ViewModel
```kotlin
@KoinViewModel
class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel()

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
    onFailure = { error -> Log.e(TAG, "Error", error) }
)
```

## 빌드 & 실행

```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# 테스트
./gradlew test
```

## 주의사항

1. **Google Maps API 키** 필요 (`local.properties`)
2. **Firebase 설정** 필요 (`google-services.json`)
3. **ProGuard** 릴리즈 빌드 시 난독화 적용됨

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

### 예시
```markdown
## 🚀 이슈번호
- closed #14

## ✏️ 변경사항
- 지도에 건물 마커 표시 기능 구현
- 마커 클릭 시 건물 정보 바텀시트 표시
- 건물 검색 기능 추가

## 📷 스크린샷
<img src="screenshot.png" width="360"/>

## ✍️ 사용법
1. 홈 화면에서 지도 확인
2. 건물 마커 클릭
3. 바텀시트에서 상세 정보 확인

## 🎸 기타
- API 연동 완료
- 오프라인 캐시는 다음 PR에서 진행
```

### 브랜치 네이밍
```
<type>/<간단한-설명>
```

| 타입 | 예시 |
|------|------|
| `feat` | `feat/building-search` |
| `fix` | `fix/marker-crash` |
| `refactor` | `refactor/viewmodel-split` |
| `chore` | `chore/gradle-update` |

---

## 중요 규칙

- **PR 베이스 브랜치는 항상 `develop`**
- **커밋, 이슈, PR에 Claude/AI 관련 문구 절대 금지**
  - `Co-Authored-By: Claude` 넣지 말 것
  - `Generated with Claude Code` 넣지 말 것
  - AI가 작성했다는 어떤 언급도 하지 말 것
