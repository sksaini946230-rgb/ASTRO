# AstroVeda ProGuard / R8 Rules

# Keep Line Numbers and Attributes for Crash Reporting
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*

# Data Models and Entities (Preserve reflection/serialization for Firestore & Room)
-keep class com.example.data.model.** { *; }

# Firebase (Auth, Firestore, Cloud Messaging, Analytics)
-keep class com.google.firebase.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName *;
    @com.google.firebase.database.PropertyName *;
}
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers class * {
    @androidx.room.* *;
}
-dontwarn androidx.room.paging.**

# WorkManager Workers
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.example.worker.** { *; }

# Google Play Billing
-keep class com.android.billingclient.api.** { *; }

# Google Mobile Ads (AdMob)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }

# Kotlin Serialization / Coroutines
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-dontwarn kotlinx.coroutines.**
