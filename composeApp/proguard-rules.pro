# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# ============================================
# Kotlin Serialization
# ============================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.ganaljigi.kubf.**$$serializer { *; }
-keepclassmembers class com.ganaljigi.kubf.** {
    *** Companion;
}
-keepclasseswithmembers class com.ganaljigi.kubf.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================
# Ktor
# ============================================
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }
-keep class io.ktor.client.engine.android.** { *; }
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.**

-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ============================================
# Koin
# ============================================
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.dsl.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
    @org.koin.core.annotation.* <fields>;
}
-keep class * extends org.koin.core.module.Module { *; }
-keep @org.koin.core.annotation.Module class * { *; }
-keep @org.koin.core.annotation.Single class * { *; }
-keep @org.koin.core.annotation.Factory class * { *; }
-keep @org.koin.android.annotation.KoinViewModel class * { *; }

# ============================================
# Coil
# ============================================
-dontwarn coil.**

# ============================================
# Google Maps
# ============================================
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# ============================================
# Firebase Crashlytics
# ============================================
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.google.firebase.crashlytics.** { *; }
-dontwarn com.google.firebase.crashlytics.**

# ============================================
# Data classes (DTO, Response, Model)
# ============================================
-keep class com.ganaljigi.kubf.data.remote.response.** { *; }
-keep class com.ganaljigi.kubf.ui.**.model.** { *; }

# ============================================
# Enum
# ============================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================
# Parcelable
# ============================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ============================================
# R8 Full Mode
# ============================================
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
