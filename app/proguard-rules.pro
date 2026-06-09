# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# JAudioTagger
-keep class org.jaudiotagger.** { *; }
-dontwarn java.awt.**
-dontwarn java.awt.image.**
-dontwarn java.awt.geom.**
-dontwarn javax.swing.**
-dontwarn javax.imageio.**
-dontwarn javax.imageio.stream.**

# Coil
-keep class coil.** { *; }
