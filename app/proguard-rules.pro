# =============================================================================
# AudioBook — ProGuard / R8 rules
# =============================================================================
# Nguyên tắc chung:
#   1. Ưu tiên giữ rules TỐI THIỂU — chỉ thêm khi thực sự cần.
#   2. R8 đã có rules sẵn cho hầu hết AndroidX (qua consumer-rules của AAR).
#   3. Bật `-dontwarn` cho các thư viện Java-only (PDFBox, POI) vì chúng
#      reference một số class Android không có (java.awt, javax.imageio…).
# =============================================================================

# -----------------------------------------------------------------------------
# 1. Giữ metadata cần thiết (annotation, generic signature, inner class, enum)
# -----------------------------------------------------------------------------
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations

# Ẩn tên file gốc trong stacktrace nhưng vẫn giữ line number để debug
-renamesourcefileattribute SourceFile

# -----------------------------------------------------------------------------
# 2. Kotlin — giữ metadata cho reflection (Hilt, Gson, Room dùng)
# -----------------------------------------------------------------------------
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Giữ default constructor của data class (Gson cần)
-keepclassmembers class **$Companion { *; }

# -----------------------------------------------------------------------------
# 3. Coroutines
# -----------------------------------------------------------------------------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# -----------------------------------------------------------------------------
# 4. Dagger / Hilt
# -----------------------------------------------------------------------------
# Hilt sinh code tại compile-time, R8 tự xử lý. Chỉ cần giữ entry point.
-keep,allowobfuscation @interface dagger.hilt.android.HiltAndroidApp
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

-keep,allowobfuscation @interface dagger.hilt.android.lifecycle.HiltViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }

-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# -----------------------------------------------------------------------------
# 5. Room
# -----------------------------------------------------------------------------
# Room sinh impl tại compile-time. Giữ:
#   - @Entity, @Dao interfaces
#   - constructor của entity (Room dùng reflection)
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Giữ constructor của entity để Room instantiate
-keepclassmembers @androidx.room.Entity class * {
    <init>(...);
}

# -----------------------------------------------------------------------------
# 6. Retrofit
# -----------------------------------------------------------------------------
# Retrofit dùng reflection cho @GET, @POST… nên giữ annotation + signature
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Giữ ApiService interface — Retrofit parse @GET/@POST tại runtime
-keep interface com.example.app.data.remote.ApiService { *; }

# Giữ method return type của suspend function (Retrofit cần biết)
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Retrofit + OkHttp
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# -----------------------------------------------------------------------------
# 7. Gson — giữ DTO để serialize/deserialize đúng tên field
# -----------------------------------------------------------------------------
-keep class com.example.app.data.remote.dto.** { *; }

# Giữ field name khi Gson dùng reflection
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Generic type của Gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Gson internals
-dontwarn sun.misc.**
-keep class sun.misc.Unsafe { *; }

# -----------------------------------------------------------------------------
# 8. PDFBox-Android (com.tom-roush)
# -----------------------------------------------------------------------------
# PDFBox reference nhiều class Java SE không có trên Android → phải -dontwarn
-dontwarn com.tom_roush.pdfbox.**
-dontwarn org.apache.**
-dontwarn javax.imageio.**
-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn java.beans.**
-dontwarn javax.xml.stream.**
-dontwarn org.w3c.dom.**

# Giữ PdfBoxResourceLoader (gọi trong Application init)
-keep class com.tom_roush.pdfbox.android.PDFBoxResourceLoader { *; }

# Giữ các parser cụ thể của mình — Hilt multibinding cần class không bị obfuscate
-keep class com.example.app.data.parser.** { *; }

# -----------------------------------------------------------------------------
# 9. TTS + MediaPlayer
# -----------------------------------------------------------------------------
# Android framework — không cần keep, R8 tự biết.
# Nhưng giữ service nếu sau này thêm foreground service.
# -keep class com.example.app.data.tts.** { *; }   # chỉ bật nếu có reflection

# -----------------------------------------------------------------------------
# 10. Compose
# -----------------------------------------------------------------------------
# Compose 1.x+ đi kèm consumer rules đầy đủ. Chỉ cần giữ @Composable
# khi dùng reflection (vd: @Preview). Không cần thêm gì cho production.
-dontwarn androidx.compose.**

# -----------------------------------------------------------------------------
# 11. Kotlin Serialization (nếu dùng)
# -----------------------------------------------------------------------------
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.app.**$$serializer { *; }
-keepclassmembers class com.example.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# -----------------------------------------------------------------------------
# 12. Entry point của app — không obfuscate để stacktrace đọc được
# -----------------------------------------------------------------------------
-keep class com.example.app.MainActivity { *; }
-keep class com.example.app.AudioBookApplication { *; }

# Giữ domain model nếu cần debug (khuyến nghị chỉ bật ở internal build)
# -keep class com.example.app.domain.model.** { *; }

# -----------------------------------------------------------------------------
# 13. Debug info — giữ line number để crash report có ích
# -----------------------------------------------------------------------------
-keepattributes SourceFile,LineNumberTable