# Ktor + Kotlin Serialization 마이그레이션 가이드

## 개요

현재 Retrofit + OkHttp 기반 네트워크 레이어를 Ktor Client + Kotlin Serialization으로 마이그레이션합니다.

### 마이그레이션 이유
- KMP (Kotlin Multiplatform) 지원
- 순수 Kotlin 기반 네트워크 클라이언트
- 코루틴 네이티브 지원
- 더 간결한 API

---

## 1. 의존성 변경

### 제거할 의존성 (libs.versions.toml)

```toml
# [versions]에서 제거
retrofit = "3.0.0"
okhttp = "4.12.0"

# [libraries]에서 제거
okhttp-bom = { group = "com.squareup.okhttp3", name = "okhttp-bom", version.ref = "okhttp" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlin-serialization-converter = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
```

### 추가할 의존성 (libs.versions.toml)

```toml
[versions]
ktor = "3.0.3"

[libraries]
# Ktor Client
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
ktor-client-okhttp = { group = "io.ktor", name = "ktor-client-okhttp", version.ref = "ktor" }
ktor-client-content-negotiation = { group = "io.ktor", name = "ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { group = "io.ktor", name = "ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { group = "io.ktor", name = "ktor-client-logging", version.ref = "ktor" }
```

### build.gradle.kts 변경

```kotlin
// 제거
implementation(platform(libs.okhttp.bom))
implementation(libs.okhttp)
implementation(libs.okhttp.logging.interceptor)
implementation(libs.retrofit)
implementation(libs.retrofit.kotlin.serialization.converter)

// 추가
implementation(libs.ktor.client.core)
implementation(libs.ktor.client.android)  // 또는 ktor-client-okhttp
implementation(libs.ktor.client.content.negotiation)
implementation(libs.ktor.serialization.kotlinx.json)
implementation(libs.ktor.client.logging)
```

---

## 2. NetworkModule 마이그레이션

### Before (Retrofit)

```kotlin
// core/data/di/NetworkModule.kt
@Module
class NetworkModule {
    @Single
    fun providesJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    @Single
    fun providesLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Single
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    @Single
    fun providesRetrofit(
        client: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
```

### After (Ktor)

```kotlin
// core/data/di/NetworkModule.kt
package com.ganaljigi.kubf.core.data.di

import com.ganaljigi.kubf.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class NetworkModule {

    @Single
    fun providesJson(): Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    @Single
    fun providesHttpClient(json: Json): HttpClient = HttpClient(Android) {
        // JSON 직렬화 설정
        install(ContentNegotiation) {
            json(json)
        }

        // 로깅 설정
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("KtorClient", message)
                }
            }
            level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
        }

        // 기본 요청 설정
        defaultRequest {
            url(BuildConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
}
```

---

## 3. Service → ApiClient 마이그레이션

### Before (Retrofit Service)

```kotlin
// core/network/service/HomeService.kt
interface HomeService {
    @GET("home")
    suspend fun getHomeData(): BaseResponse<HomeResponseDto>

    @GET("home/outside-significants/{outsideSignificantId}")
    suspend fun getSignificantInfo(
        @Path("outsideSignificantId") id: Long,
    ): BaseResponse<HomeSignificantResponseDto>

    @GET("places/search")
    suspend fun getHomeSearchResult(
        @Query("keyword") keyword: String,
    ): BaseResponse<HomeSearchResponseDto>

    @GET("home/gates/{gateId}")
    suspend fun getGateInfo(
        @Path("gateId") id: Long,
    ): BaseResponse<HomeGateResponseDto>
}
```

### After (Ktor ApiClient)

```kotlin
// core/network/api/HomeApi.kt
package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class HomeApi(private val client: HttpClient) {

    suspend fun getHomeData(): BaseResponse<HomeResponseDto> =
        client.get("home").body()

    suspend fun getSignificantInfo(id: Long): BaseResponse<HomeSignificantResponseDto> =
        client.get("home/outside-significants/$id").body()

    suspend fun getHomeSearchResult(keyword: String): BaseResponse<HomeSearchResponseDto> =
        client.get("places/search") {
            parameter("keyword", keyword)
        }.body()

    suspend fun getGateInfo(id: Long): BaseResponse<HomeGateResponseDto> =
        client.get("home/gates/$id").body()
}
```

---

## 4. 모든 Service 마이그레이션

### BuildingApi

```kotlin
// core/network/api/BuildingApi.kt
package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.building.BuildingDto
import com.ganaljigi.kubf.core.network.response.building.BuildingSummaryResponseDto
import com.ganaljigi.kubf.core.network.response.building.SearchResponseDto
import com.ganaljigi.kubf.core.network.response.building.SpacesDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class BuildingApi(private val client: HttpClient) {

    suspend fun getBuildingInfo(buildingId: Long): BaseResponse<BuildingSummaryResponseDto> =
        client.get("buildings/$buildingId").body()

    suspend fun getBuildingInfo2(id: Long): BaseResponse<BuildingDto> =
        client.get("buildings/$id").body()

    suspend fun getBuildingSpaces(id: Long): BaseResponse<SpacesDto> =
        client.get("buildings/$id/spaces").body()

    suspend fun searchSpaces(id: Long, keyword: String): BaseResponse<SearchResponseDto> =
        client.get("buildings/$id/spaces/search") {
            parameter("keyword", keyword)
        }.body()
}
```

### RouteApi

```kotlin
// core/network/api/RouteApi.kt
package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.BaseResponse
import com.ganaljigi.kubf.core.network.response.route.PathResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class RouteApi(private val client: HttpClient) {

    suspend fun getPath(
        srcId: Long,
        srcType: String,
        destId: Long,
        destType: String,
    ): BaseResponse<PathResponseDto> = client.get("path") {
        parameter("srcId", srcId)
        parameter("srcType", srcType)
        parameter("destId", destId)
        parameter("destType", destType)
    }.body()
}
```

### HelperApi

```kotlin
// core/network/api/HelperApi.kt
package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.core.network.response.helper.HelperNoticeResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.koin.core.annotation.Single

@Single
class HelperApi(private val client: HttpClient) {

    suspend fun getSupportCenterNotices(): HelperNoticeResponseDto =
        client.get("support-center").body()
}
```

### RoomInfoApi

```kotlin
// core/network/api/RoomInfoApi.kt
package com.ganaljigi.kubf.core.network.api

import com.ganaljigi.kubf.feature.room.response.RoomInfoResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.koin.core.annotation.Single

@Single
class RoomInfoApi(private val client: HttpClient) {

    suspend fun getRoomInfo(
        buildingId: Long,
        spaceId: Long,
        type: Int,
    ): RoomInfoResponseDto = client.get("buildings/$buildingId/spaces/$spaceId") {
        parameter("type", type)
    }.body()
}
```

---

## 5. Repository 업데이트

### HomeRepositoryImpl

```kotlin
// core/data/repositoryimpl/HomeRepositoryImpl.kt
package com.ganaljigi.kubf.core.data.repositoryimpl

import com.ganaljigi.kubf.core.data.repository.HomeRepository
import com.ganaljigi.kubf.core.network.api.HomeApi
import com.ganaljigi.kubf.core.network.response.handleBaseResponse
import com.ganaljigi.kubf.core.network.response.home.HomeGateResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSearchResponseDto
import com.ganaljigi.kubf.core.network.response.home.HomeSignificantResponseDto
import org.koin.core.annotation.Single

@Single
class HomeRepositoryImpl(
    private val homeApi: HomeApi,
) : HomeRepository {

    override suspend fun getHomeData(): Result<HomeResponseDto> = runCatching {
        homeApi.getHomeData().handleBaseResponse().getOrThrow()
    }

    override suspend fun getSpecialInfo(id: Long): Result<HomeSignificantResponseDto> =
        runCatching {
            homeApi.getSignificantInfo(id).handleBaseResponse().getOrThrow()
        }

    override suspend fun getHomeSearchResult(keyword: String): Result<HomeSearchResponseDto> =
        runCatching {
            homeApi.getHomeSearchResult(keyword).handleBaseResponse().getOrThrow()
        }

    override suspend fun getGateInfo(id: Long): Result<HomeGateResponseDto> =
        runCatching {
            homeApi.getGateInfo(id).handleBaseResponse().getOrThrow()
        }
}
```

---

## 6. ApiModule 제거 및 Koin 설정

### 제거할 파일
- `core/data/di/ApiModule.kt` (Retrofit Service Provider)

### Koin 모듈 설정 (자동 스캔)

```kotlin
// KubfApplication.kt 또는 di/AppModule.kt
@Module
@ComponentScan("com.ganaljigi.kubf")
class AppModule
```

Koin Annotations의 `@Single` 어노테이션이 붙은 클래스들은 자동으로 스캔되어 등록됩니다.

---

## 7. 에러 핸들링 개선 (선택사항)

### Ktor 전용 에러 핸들링

```kotlin
// core/network/util/KtorExtensions.kt
package com.ganaljigi.kubf.core.network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

suspend inline fun <reified T> HttpResponse.safeBody(): Result<T> = runCatching {
    if (status.isSuccess()) {
        body<T>()
    } else {
        throw Exception("HTTP Error: ${status.value} - ${status.description}")
    }
}
```

### HttpClient에 에러 핸들링 플러그인 추가

```kotlin
@Single
fun providesHttpClient(json: Json): HttpClient = HttpClient(Android) {
    // ... 기존 설정

    // 응답 검증
    install(HttpResponseValidator) {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                throw ResponseException(
                    response,
                    "HTTP Error: ${response.status.value}"
                )
            }
        }
    }

    // 타임아웃 설정
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 10_000
        socketTimeoutMillis = 30_000
    }
}
```

---

## 8. 마이그레이션 체크리스트

### Phase 1: 의존성 변경
- [ ] libs.versions.toml에 Ktor 의존성 추가
- [ ] libs.versions.toml에서 Retrofit/OkHttp 의존성 제거
- [ ] build.gradle.kts 의존성 업데이트
- [ ] Sync & Build 확인

### Phase 2: NetworkModule 마이그레이션
- [ ] NetworkModule을 Ktor HttpClient로 변경
- [ ] OkHttpClient, Retrofit 제거

### Phase 3: API 레이어 마이그레이션
- [ ] HomeService → HomeApi
- [ ] BuildingService → BuildingApi
- [ ] RouteService → RouteApi
- [ ] HelperService → HelperApi
- [ ] RoomInfoService → RoomInfoApi

### Phase 4: Repository 업데이트
- [ ] HomeRepositoryImpl - HomeApi 주입
- [ ] BuildingRepositoryImpl - BuildingApi 주입
- [ ] RouteRepositoryImpl - RouteApi 주입
- [ ] BuildingInfoRepositoryImpl - BuildingApi 주입

### Phase 5: 정리
- [ ] ApiModule.kt 제거
- [ ] 기존 Service 인터페이스 제거
- [ ] 불필요한 import 정리
- [ ] 테스트 실행

---

## 9. 파일 변경 요약

### 생성할 파일
```
core/network/api/
├── HomeApi.kt
├── BuildingApi.kt
├── RouteApi.kt
├── HelperApi.kt
└── RoomInfoApi.kt
```

### 수정할 파일
```
core/data/di/NetworkModule.kt
core/data/repositoryimpl/HomeRepositoryImpl.kt
core/data/repositoryimpl/BuildingRepositoryImpl.kt
core/data/repositoryimpl/BuildingInfoRepositoryImpl.kt
core/data/repositoryimpl/RouteRepositoryImpl.kt
```

### 삭제할 파일
```
core/data/di/ApiModule.kt
core/network/service/HomeService.kt
core/network/service/BuildingService.kt
core/network/service/RouteService.kt
core/network/service/HelperService.kt
feature/room/service/RoomInfoService.kt
```

---

## 10. 주의사항

1. **Coil 이미지 로딩**: Coil은 내부적으로 OkHttp를 사용하므로 별도 설정 필요 없음
2. **BaseResponse 유지**: 기존 `BaseResponse<T>` 및 `handleBaseResponse()` 그대로 사용 가능
3. **Kotlinx Serialization**: 기존 DTO의 `@Serializable`, `@SerialName` 어노테이션 그대로 유지
4. **테스트**: 마이그레이션 후 모든 API 호출 테스트 필수

---

## 부록: Ktor vs Retrofit 비교

| 항목 | Retrofit | Ktor |
|------|----------|------|
| KMP 지원 | X | O |
| 인터페이스 기반 | O | X (클래스 기반) |
| 코루틴 지원 | O (suspend) | O (네이티브) |
| 직렬화 | 컨버터 필요 | 플러그인 |
| 커스터마이징 | Interceptor | Plugin |
| 러닝 커브 | 낮음 | 중간 |
