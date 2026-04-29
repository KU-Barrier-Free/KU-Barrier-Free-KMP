package com.ganaljigi.kubf.feature.home.component.map

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.*
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetCurrentContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage

/**
 * Android XML Vector Drawable을 iOS UIImage로 렌더링
 */
@OptIn(ExperimentalForeignApi::class)
object SVGImageRenderer {

    /**
     * 건물 아이콘 생성 (ic_building.xml과 동일)
     */
    fun createBuildingIcon(size: Double, isSelected: Boolean = false): UIImage? {
        val scale = size / 20.0 // 원본 크기 20dp 기준

        val imageSize = CGSizeMake(size, size)
        UIGraphicsBeginImageContextWithOptions(imageSize, false, 0.0)
        val context = UIGraphicsGetCurrentContext() ?: run {
            UIGraphicsEndImageContext()
            return null
        }

        CGContextSaveGState(context)
        CGContextScaleCTM(context, scale, scale)

        // 배경 원
        val bgColor = if (isSelected) {
            UIColor.colorWithRed(0.0, green = 199.0/255.0, blue = 60.0/255.0, alpha = 1.0)
        } else {
            UIColor.colorWithRed(0.35, green = 0.41, blue = 0.38, alpha = 1.0)
        }
        bgColor.setFill()
        CGContextAddEllipseInRect(context, CGRectMake(0.5, 0.5, 19.0, 19.0))
        CGContextFillPath(context)

        // 원 테두리 (흰색)
        UIColor.whiteColor.setStroke()
        CGContextSetLineWidth(context, 1.0)
        CGContextAddEllipseInRect(context, CGRectMake(0.5, 0.5, 19.0, 19.0))
        CGContextStrokePath(context)

        // 건물 아이콘 (흰색)
        UIColor.colorWithRed(0.96, green = 0.96, blue = 0.96, alpha = 1.0).setFill()

        // 건물 외곽
        CGContextMoveToPoint(context, 12.5, 5.0)
        CGContextAddLineToPoint(context, 13.5, 5.0)
        CGContextAddLineToPoint(context, 13.5, 15.0)
        CGContextAddLineToPoint(context, 11.0, 15.0)
        CGContextAddLineToPoint(context, 11.0, 12.875)
        CGContextAddLineToPoint(context, 10.0, 11.875)
        CGContextAddLineToPoint(context, 9.5, 11.875)
        CGContextAddLineToPoint(context, 8.5, 12.875)
        CGContextAddLineToPoint(context, 8.5, 15.0)
        CGContextAddLineToPoint(context, 6.0, 15.0)
        CGContextAddLineToPoint(context, 6.0, 6.0)
        CGContextAddLineToPoint(context, 7.0, 5.0)
        CGContextAddLineToPoint(context, 12.5, 5.0)
        CGContextFillPath(context)

        // 창문들
        drawWindow(context, 8.375, 9.25)
        drawWindow(context, 11.125, 9.25)
        drawWindow(context, 8.375, 6.5)
        drawWindow(context, 11.125, 6.5)

        CGContextRestoreGState(context)

        val image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }

    private fun drawWindow(context: CGContextRef, x: Double, y: Double) {
        val windowSize = 0.625
        UIColor.colorWithRed(0.35, green = 0.41, blue = 0.38, alpha = 1.0).setFill()
        CGContextFillRect(context, CGRectMake(x, y, windowSize, windowSize))
    }
}
