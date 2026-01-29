# 쿠맵 (KU Map) - 고려대학교 배리어프리 네비게이션 앱

> 고려대학교 캠퍼스 내 장애학생을 위한 배리어프리 정보 제공 및 길찾기 서비스

[![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Android](https://img.shields.io/badge/platform-Android-green.svg?logo=android)](https://www.android.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📱 쿠맵이란?

쿠맵(KU Map)은 고려대학교 캠퍼스 내 모든 학생들, 특히 장애학생들이 편리하게 캠퍼스를 이동할 수 있도록 돕는 배리어프리 네비게이션 애플리케이션입니다.

### 주요 기능

- 🗺️ **캠퍼스 지도 및 건물 검색**: Google Maps 기반의 직관적인 캠퍼스 지도와 건물 검색 기능
- 🚪 **출입구 정보 제공**: 각 건물의 배리어프리 출입구 위치 및 상세 정보 확인
- 🏢 **건물 상세 정보**: 층별 강의실, 편의시설 정보 및 배리어프리 시설 안내
- 🚶 **길찾기 서비스**: 현재 위치에서 목적지까지의 배리어프리 경로 안내
- 🔍 **강의실 검색**: 건물명, 강의실명으로 빠른 검색 및 위치 확인
- 💡 **장애학생지원센터 정보**: 공지사항, 취업정보, 바로가기 등 유용한 정보 제공

## 🛠 기술 스택

### Core Technologies
- **Kotlin 2.2.20**: Modern Android development language
- **Jetpack Compose**: Declarative UI toolkit
- **Kotlin Coroutines & Flow**: Asynchronous programming
- **Material Design 3**: Modern UI components

### Architecture & Libraries
- **MVVM + Clean Architecture**: Separation of concerns with core/feature structure
- **Koin 4.1.1**: Dependency injection
- **Ktor 3.0.3**: HTTP client for API communication
- **Google Maps Compose 4.4.1**: Map integration
- **Navigation Compose 2.8.9**: Type-safe navigation
- **Kotlinx Serialization**: JSON parsing
- **Coil 3**: Image loading

### Development Tools
- **Detekt 1.23.7**: Code quality and static analysis
- **Firebase Crashlytics**: Crash reporting
- **KSP**: Kotlin Symbol Processing

## 🏗 프로젝트 구조

프로젝트는 Google 권장 아키텍처를 따르는 core/feature 구조로 구성되어 있습니다:

```
app/src/main/java/com/ganaljigi/kubf/
├── core/                           # 공통 기능 레이어
│   ├── data/                       # 네트워크, 데이터 소스
│   ├── designsystem/               # 디자인 시스템 컴포넌트
│   ├── model/                      # 공통 도메인 모델
│   ├── navigation/                 # 앱 네비게이션
│   └── ui/                         # 공통 UI 유틸리티
│
└── feature/                        # 기능별 모듈
    ├── home/                       # 홈(지도) 화면
    ├── building/                   # 건물 정보 화면
    ├── room/                       # 강의실 정보 화면
    └── helper/                     # 장애학생지원센터 화면
```

자세한 아키텍처 마이그레이션 가이드는 [ARCHITECTURE_MIGRATION.md](ARCHITECTURE_MIGRATION.md)를 참고하세요.

## 🚀 시작하기

### 필수 요구사항

- Android Studio Ladybug | 2024.2.1 이상
- JDK 17 이상
- Android SDK 30 이상 (minSdk: 30, targetSdk: 35)
- Google Maps API Key

### 설치 및 실행

1. 레포지토리 클론
```bash
git clone https://github.com/KU-Barrier-Free/KU-Barrier-Free-KMP.git
cd KU-Barrier-Free-KMP
```

2. `local.properties` 파일 생성 및 Google Maps API Key 추가
```properties
GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
```

3. Android Studio에서 프로젝트 열기

4. Gradle Sync 후 실행

### 빌드 명령어

```bash
# Debug 빌드
./gradlew assembleDebug

# Release 빌드
./gradlew assembleRelease

# 유닛 테스트 실행
./gradlew test

# 코드 품질 검사 (Detekt)
./gradlew detekt
```

## 📖 개발 가이드

### 리팩토링 가이드
프로젝트 개선 사항 및 리팩토링 가이드는 [REFACTORING.md](REFACTORING.md)를 참고하세요.

### 코드 스타일
- Kotlin 코딩 컨벤션 준수
- Detekt를 통한 코드 품질 관리
- Material Design 3 가이드라인 준수

## 🤝 기여하기

프로젝트에 기여를 원하시는 분들은 다음 단계를 따라주세요:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이선스

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## 👥 팀

고려대학교 배리어프리 프로젝트 팀

- **개발**: 간알지기 팀
- **지원**: 고려대학교 장애학생지원센터

## 📞 문의

프로젝트에 대한 문의사항이나 제안은 이슈를 통해 남겨주세요.

---

**Made with ❤️ for KU Students**