package com.ganaljigi.kubf.feature.home.component.map

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.get
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import platform.CoreGraphics.*
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.*
import platform.darwin.NSObject

/**
 * InfoWindow용 이미지 캐시 및 비동기 다운로더
 * Google Maps iOS SDK의 InfoWindow는 정적 스냅샷이므로,
 * 이미지 다운로드 완료 후 InfoWindow를 갱신해야 함
 */
object InfoWindowImageCache {
    private val cache = mutableMapOf<String, UIImage>()
    private val attemptedUrls = mutableSetOf<String>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun getImage(url: String): UIImage? = cache[url]

    fun needsDownload(urls: List<String>): Boolean =
        urls.any { it !in cache && it !in attemptedUrls }

    fun downloadImages(urls: List<String>, onComplete: () -> Unit) {
        val urlsToDownload = urls.filter { it !in cache && it !in attemptedUrls }
        if (urlsToDownload.isEmpty()) return

        urlsToDownload.forEach { attemptedUrls.add(it) }

        @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
        scope.launch {
            urlsToDownload.forEach { url ->
                try {
                    val client = HttpClient(Darwin)
                    val bytes: ByteArray = client.get(url).body()
                    client.close()
                    val nsData = bytes.usePinned { pinned ->
                        NSData.create(
                            bytes = pinned.addressOf(0),
                            length = bytes.size.toULong()
                        )
                    }
                    val image = UIImage.imageWithData(nsData)
                    if (image != null) {
                        cache[url] = image
                    }
                } catch (_: Exception) { }
            }
            onComplete()
        }
    }

}

/**
 * UIView를 UIImage로 렌더링
 */
@OptIn(ExperimentalForeignApi::class)
fun UIView.toUIImage(): UIImage? {
    return memScoped {
        val width = frame.useContents { size.width }
        val height = frame.useContents { size.height }
        val size = CGSizeMake(width, height)

        UIGraphicsBeginImageContextWithOptions(size, false, UIScreen.mainScreen.scale)
        val context = UIGraphicsGetCurrentContext()

        if (context != null) {
            layer.renderInContext(context)
            val image = UIGraphicsGetImageFromCurrentImageContext()
            UIGraphicsEndImageContext()
            return@memScoped image
        }

        UIGraphicsEndImageContext()
        return@memScoped null
    }
}

/**
 * BuildingMarker용 커스텀 뷰 생성
 */
@OptIn(ExperimentalForeignApi::class)
fun createBuildingMarkerView(
    buildingName: String,
    isSelected: Boolean,
    scale: Float = 1.0f,
): UIView {
    // Android와 동일: 선택 시 32dp, 미선택 시 20dp
    val iconSize = if (isSelected) 50.0 * scale else 20.0 * scale
    val fontSize = (14.0 * scale).coerceAtLeast(10.0)
    val totalHeight = iconSize + fontSize + 6.0

    // 컨테이너 뷰 (최종 크기로 생성)
    val containerView = UIView(frame = CGRectMake(0.0, 0.0, 100.0, totalHeight))
    containerView.backgroundColor = UIColor.clearColor

    // Assets.xcassets에서 SVG 이미지 로드
    val iconName = if (isSelected) "ic_building_selected" else "ic_building"
    val iconImage = UIImage.imageNamed(iconName)

    if (iconImage != null) {
        val imageView = UIImageView(frame = CGRectMake(
            (100.0 - iconSize) / 2.0,
            0.0,
            iconSize,
            iconSize
        ))
        imageView.image = iconImage
        imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFit

        // 그림자 효과
        imageView.layer.shadowColor = UIColor.blackColor.CGColor
        imageView.layer.shadowOffset = CGSizeMake(0.0, 2.0)
        imageView.layer.shadowOpacity = 0.3f
        imageView.layer.shadowRadius = 3.0

        containerView.addSubview(imageView)
    }

    // 텍스트 레이블
    val label = UILabel(frame = CGRectMake(0.0, iconSize + 2.0, 100.0, fontSize + 4.0))
    label.text = buildingName
    label.font = UIFont.boldSystemFontOfSize(fontSize)
    label.textColor = if (isSelected) {
        UIColor.colorWithRed(0.0, green = 199.0/255.0, blue = 60.0/255.0, alpha = 1.0)
    } else {
        UIColor.colorWithRed(0.35, green = 0.41, blue = 0.38, alpha = 1.0)
    }
    label.textAlignment = NSTextAlignmentCenter
    label.numberOfLines = 1
    label.adjustsFontSizeToFitWidth = true
    label.minimumScaleFactor = 0.7

    // 텍스트 외곽선 (Android처럼)
    label.layer.shadowColor = UIColor.whiteColor.CGColor
    label.layer.shadowOffset = CGSizeMake(0.0, 0.0)
    label.layer.shadowOpacity = 1.0f
    label.layer.shadowRadius = 2.0

    containerView.addSubview(label)

    return containerView
}

/**
 * DoorMarker용 커스텀 뷰 생성 (Android DoorMarkerComposable과 동일한 스타일)
 * 원형 배경에 라벨 텍스트 표시
 */
@OptIn(ExperimentalForeignApi::class)
fun createDoorMarkerView(
    labelText: String,
    isWheelChairAccessible: Boolean,
    scale: Float = 1.0f,
): UIView {
    // Android: 16dp min size, 14sp fontSize
    val fontSize = (14.0 * scale).coerceAtLeast(10.0)
    val minSize = 16.0 * scale
    val paddingH = 4.0 * scale
    val paddingV = 2.0 * scale

    // 텍스트 크기 계산
    val textWidth = labelText.length * fontSize * 0.6 + (paddingH * 2)
    val viewWidth = maxOf(textWidth, minSize + (paddingH * 2))
    val viewHeight = minSize + (paddingV * 2)

    // 컨테이너 뷰 (원형 배경)
    val containerView = UIView(frame = CGRectMake(0.0, 0.0, viewWidth, viewHeight))

    // 배경색: 휠체어 접근 가능시 MainGreen, 아니면 Gray4
    containerView.backgroundColor = if (isWheelChairAccessible) {
        // MainGreen: 0xFF00C73C
        UIColor.colorWithRed(0.0, green = 199.0/255.0, blue = 60.0/255.0, alpha = 1.0)
    } else {
        // Gray4: 0xFF9E9E9E 정도
        UIColor.colorWithRed(0.62, green = 0.62, blue = 0.62, alpha = 1.0)
    }

    // 원형 모서리
    containerView.layer.cornerRadius = viewHeight / 2.0
    containerView.layer.masksToBounds = true

    // 라벨
    val labelView = UILabel(frame = CGRectMake(0.0, 0.0, viewWidth, viewHeight))
    labelView.text = labelText
    labelView.font = UIFont.systemFontOfSize(fontSize, weight = UIFontWeightMedium)
    labelView.textColor = UIColor.whiteColor
    labelView.textAlignment = NSTextAlignmentCenter

    containerView.addSubview(labelView)

    return containerView
}

/**
 * GateMarker용 InfoWindow 뷰 생성 (Android MapGateInfo와 동일한 스타일)
 */
@OptIn(ExperimentalForeignApi::class)
fun createGateInfoWindowView(
    gateName: String,
    description: String,
    imageUrls: List<String>,
): UIView {
    // Android: widthIn(max = 320dp), cornerRadius 20dp
    val maxWidth = 320.0
    val horizontalPadding = 24.0
    val verticalPadding = 12.0
    val cornerRadius = 20.0
    val imageSize = 80.0
    val imageSpacing = 8.0

    // 콘텐츠 너비 계산 (Android widthIn 동작: 콘텐츠에 맞게 줄어들고 max 제한)
    val titleLabel = UILabel()
    titleLabel.text = gateName
    titleLabel.font = UIFont.boldSystemFontOfSize(16.0)
    titleLabel.sizeToFit()
    val titleWidth = titleLabel.frame.useContents { size.width }

    val imageCount = imageUrls.size.coerceAtMost(2)
    val imageAreaWidth = if (imageCount > 0) {
        (imageSize * imageCount) + (imageSpacing * (imageCount - 1))
    } else 0.0

    val descLabel = UILabel()
    descLabel.text = description
    descLabel.font = UIFont.systemFontOfSize(14.0, weight = UIFontWeightSemibold)
    descLabel.numberOfLines = 0
    descLabel.sizeToFit()
    val descIntrinsicWidth = descLabel.frame.useContents { size.width }

    val maxContentWidth = maxWidth - (horizontalPadding * 2)
    val contentWidth = maxOf(titleWidth, imageAreaWidth, descIntrinsicWidth).coerceAtMost(maxContentWidth)
    val containerWidth = contentWidth + (horizontalPadding * 2)

    // 설명 텍스트를 실제 contentWidth에 맞게 다시 계산 (줄바꿈 반영)
    descLabel.setFrame(CGRectMake(0.0, 0.0, contentWidth, 0.0))
    descLabel.sizeToFit()
    descLabel.setFrame(CGRectMake(horizontalPadding, 0.0, contentWidth, descLabel.frame.useContents { size.height }))

    val estimatedHeight = if (imageUrls.isNotEmpty()) 220.0 else 100.0
    val containerView = UIView(frame = CGRectMake(0.0, 0.0, containerWidth, estimatedHeight))
    containerView.backgroundColor = UIColor.whiteColor
    containerView.layer.cornerRadius = cornerRadius

    // 테두리 (Android: Gray2.copy(alpha = 0.5f))
    containerView.layer.borderWidth = 1.0
    containerView.layer.borderColor = UIColor.colorWithRed(0.9, green = 0.9, blue = 0.9, alpha = 0.5).CGColor

    // 그림자
    containerView.layer.shadowColor = UIColor.blackColor.CGColor
    containerView.layer.shadowOffset = CGSizeMake(0.0, 2.0)
    containerView.layer.shadowOpacity = 0.3f
    containerView.layer.shadowRadius = 4.0

    var currentY = verticalPadding

    // 제목 (gateName)
    titleLabel.setFrame(CGRectMake(horizontalPadding, currentY, contentWidth, 24.0))
    titleLabel.textAlignment = NSTextAlignmentCenter
    titleLabel.textColor = UIColor.blackColor
    containerView.addSubview(titleLabel)
    currentY += 32.0

    // 이미지 영역
    if (imageCount > 0) {
        val totalImageWidth = (imageSize * imageCount) + (imageSpacing * (imageCount - 1))
        val imageStartX = (containerWidth - totalImageWidth) / 2.0

        for (i in 0 until imageCount) {
            val frameRect = CGRectMake(
                imageStartX + (i * (imageSize + imageSpacing)),
                currentY,
                imageSize,
                imageSize
            )
            val cachedImage = InfoWindowImageCache.getImage(imageUrls[i])
            if (cachedImage != null) {
                val imageView = UIImageView(frame = frameRect)
                imageView.image = cachedImage
                imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                imageView.layer.cornerRadius = 10.0
                imageView.layer.masksToBounds = true
                containerView.addSubview(imageView)
            } else {
                val imageView = UIView(frame = frameRect)
                imageView.backgroundColor = UIColor.colorWithRed(0.95, green = 0.95, blue = 0.95, alpha = 1.0)
                imageView.layer.cornerRadius = 10.0
                imageView.layer.masksToBounds = true
                containerView.addSubview(imageView)
            }
        }
        currentY += imageSize + 16.0
    }

    // 설명 텍스트
    descLabel.setFrame(CGRectMake(horizontalPadding, currentY, contentWidth, descLabel.frame.useContents { size.height }))
    descLabel.textAlignment = NSTextAlignmentCenter
    descLabel.textColor = UIColor.blackColor
    containerView.addSubview(descLabel)
    val descHeight = descLabel.frame.useContents { size.height }
    currentY += descHeight + verticalPadding

    // 컨테이너 최종 높이 조정
    containerView.setFrame(CGRectMake(0.0, 0.0, containerWidth, currentY))

    return containerView
}

/**
 * SpecialMarker용 InfoWindow 뷰 생성 (Android MapSpecialInfo와 동일한 스타일)
 */
@OptIn(ExperimentalForeignApi::class)
fun createSpecialInfoWindowView(
    description: String,
    imageUrls: List<String>,
): UIView {
    // Android: widthIn(max = 214dp), padding 12dp, cornerRadius 20dp
    val maxWidth = 214.0
    val padding = 12.0
    val cornerRadius = 20.0
    val imageSize = 60.0
    val imageSpacing = 8.0

    // 콘텐츠 너비 계산
    val titleLabel = UILabel()
    titleLabel.text = "특이사항"
    titleLabel.font = UIFont.boldSystemFontOfSize(14.0)
    titleLabel.sizeToFit()
    val titleWidth = titleLabel.frame.useContents { size.width }

    val imageCount = imageUrls.size.coerceAtMost(2)
    val imageAreaWidth = if (imageCount > 0) {
        (imageSize * imageCount) + (imageSpacing * (imageCount - 1))
    } else 0.0

    val descLabel = UILabel()
    descLabel.text = description
    descLabel.font = UIFont.systemFontOfSize(14.0, weight = UIFontWeightSemibold)
    descLabel.numberOfLines = 0
    descLabel.sizeToFit()
    val descIntrinsicWidth = descLabel.frame.useContents { size.width }

    val maxContentWidth = maxWidth - (padding * 2)
    val contentWidth = maxOf(titleWidth, imageAreaWidth, descIntrinsicWidth).coerceAtMost(maxContentWidth)
    val containerWidth = contentWidth + (padding * 2)

    // 설명 텍스트를 실제 contentWidth에 맞게 다시 계산
    descLabel.setFrame(CGRectMake(0.0, 0.0, contentWidth, 0.0))
    descLabel.sizeToFit()
    descLabel.setFrame(CGRectMake(padding, 0.0, contentWidth, descLabel.frame.useContents { size.height }))

    val estimatedHeight = if (imageUrls.isNotEmpty()) 200.0 else 90.0
    val containerView = UIView(frame = CGRectMake(0.0, 0.0, containerWidth, estimatedHeight))
    containerView.backgroundColor = UIColor.whiteColor
    containerView.layer.cornerRadius = cornerRadius

    containerView.layer.borderWidth = 1.0
    containerView.layer.borderColor = UIColor.colorWithRed(0.9, green = 0.9, blue = 0.9, alpha = 0.5).CGColor

    containerView.layer.shadowColor = UIColor.blackColor.CGColor
    containerView.layer.shadowOffset = CGSizeMake(0.0, 2.0)
    containerView.layer.shadowOpacity = 0.3f
    containerView.layer.shadowRadius = 4.0

    var currentY = padding

    // 제목
    titleLabel.setFrame(CGRectMake(padding, currentY, contentWidth, 20.0))
    titleLabel.textAlignment = NSTextAlignmentCenter
    titleLabel.textColor = UIColor.blackColor
    containerView.addSubview(titleLabel)
    currentY += 28.0

    // 이미지 영역
    if (imageCount > 0) {
        val totalImageWidth = (imageSize * imageCount) + (imageSpacing * (imageCount - 1))
        val imageStartX = (containerWidth - totalImageWidth) / 2.0

        for (i in 0 until imageCount) {
            val frameRect = CGRectMake(
                imageStartX + (i * (imageSize + imageSpacing)),
                currentY,
                imageSize,
                imageSize
            )
            val cachedImage = InfoWindowImageCache.getImage(imageUrls[i])
            if (cachedImage != null) {
                val imageView = UIImageView(frame = frameRect)
                imageView.image = cachedImage
                imageView.contentMode = UIViewContentMode.UIViewContentModeScaleAspectFill
                imageView.layer.cornerRadius = 10.0
                imageView.layer.masksToBounds = true
                containerView.addSubview(imageView)
            } else {
                val imageView = UIView(frame = frameRect)
                imageView.backgroundColor = UIColor.colorWithRed(0.95, green = 0.95, blue = 0.95, alpha = 1.0)
                imageView.layer.cornerRadius = 10.0
                imageView.layer.masksToBounds = true
                containerView.addSubview(imageView)
            }
        }
        currentY += imageSize + 16.0
    }

    // 설명 텍스트
    descLabel.setFrame(CGRectMake(padding, currentY, contentWidth, descLabel.frame.useContents { size.height }))
    descLabel.textAlignment = NSTextAlignmentCenter
    descLabel.textColor = UIColor.blackColor
    containerView.addSubview(descLabel)
    val descHeight = descLabel.frame.useContents { size.height }
    currentY += descHeight + padding

    // 컨테이너 최종 높이 조정
    containerView.setFrame(CGRectMake(0.0, 0.0, containerWidth, currentY))

    return containerView
}
