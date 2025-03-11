# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn okio.**
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes Exceptions
-keep class androidx.work.** { *; }
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type





-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Uncomment for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

-dontwarn jp.co.cyberagent.android.gpuimage.**


-dontnote androidx.media2.exoplayer.external.ext.vp9.LibvpxVideoRenderer
-keepclassmembers class androidx.media2.exoplayer.external.ext.vp9.LibvpxVideoRenderer {
  <init>(boolean, long, android.os.Handler, androidx.media2.exoplayer.external.video.VideoRendererEventListener, int);
}
-dontnote androidx.media2.exoplayer.external.ext.opus.LibopusAudioRenderer
-keepclassmembers class androidx.media2.exoplayer.external.ext.opus.LibopusAudioRenderer {
  <init>(android.os.Handler, androidx.media2.exoplayer.external.audio.AudioRendererEventListener, androidx.media2.exoplayer.external.audio.AudioProcessor[]);
}
-dontnote androidx.media2.exoplayer.external.ext.flac.LibflacAudioRenderer
-keepclassmembers class androidx.media2.exoplayer.external.ext.flac.LibflacAudioRenderer {
  <init>(android.os.Handler, androidx.media2.exoplayer.external.audio.AudioRendererEventListener, androidx.media2.exoplayer.external.audio.AudioProcessor[]);
}
-dontnote androidx.media2.exoplayer.external.ext.ffmpeg.FfmpegAudioRenderer
-keepclassmembers class androidx.media2.exoplayer.external.ext.ffmpeg.FfmpegAudioRenderer {
  <init>(android.os.Handler, androidx.media2.exoplayer.external.audio.AudioRendererEventListener, androidx.media2.exoplayer.external.audio.AudioProcessor[]);
}
# Constructors accessed via reflection in DefaultExtractorsFactory
-dontnote androidx.media2.exoplayer.external.ext.flac.FlacExtractor
-keepclassmembers class androidx.media2.exoplayer.external.ext.flac.FlacExtractor {
  <init>();
}
# Constructors accessed via reflection in DefaultDataSource
-dontnote androidx.media2.exoplayer.external.ext.rtmp.RtmpDataSource
-keepclassmembers class androidx.media2.exoplayer.external.ext.rtmp.RtmpDataSource {
  <init>();
}
# Constructors accessed via reflection in DefaultDownloaderFactory
-dontnote androidx.media2.exoplayer.external.source.dash.offline.DashDownloader
-keepclassmembers class androidx.media2.exoplayer.external.source.dash.offline.DashDownloader {
  <init>(android.net.Uri, java.util.List, androidx.media2.exoplayer.external.offline.DownloaderConstructorHelper);
}
-dontnote androidx.media2.exoplayer.external.source.hls.offline.HlsDownloader
-keepclassmembers class androidx.media2.exoplayer.external.source.hls.offline.HlsDownloader {
  <init>(android.net.Uri, java.util.List, androidx.media2.exoplayer.external.offline.DownloaderConstructorHelper);
}
-dontnote androidx.media2.exoplayer.external.source.smoothstreaming.offline.SsDownloader
-keepclassmembers class androidx.media2.exoplayer.external.source.smoothstreaming.offline.SsDownloader {
  <init>(android.net.Uri, java.util.List, androidx.media2.exoplayer.external.offline.DownloaderConstructorHelper);
}
# Constructors accessed via reflection in DownloadHelper
-dontnote androidx.media2.exoplayer.external.source.dash.DashMediaSource$Factory
-keepclasseswithmembers class androidx.media2.exoplayer.external.source.dash.DashMediaSource$Factory {
  <init>(androidx.media2.exoplayer.external.upstream.DataSource$Factory);
}
-dontnote androidx.media2.exoplayer.external.source.hls.HlsMediaSource$Factory
-keepclasseswithmembers class androidx.media2.exoplayer.external.source.hls.HlsMediaSource$Factory {
  <init>(androidx.media2.exoplayer.external.upstream.DataSource$Factory);
}
-dontnote androidx.media2.exoplayer.external.source.smoothstreaming.SsMediaSource$Factory
-keepclasseswithmembers class androidx.media2.exoplayer.external.source.smoothstreaming.SsMediaSource$Factory {
  <init>(androidx.media2.exoplayer.external.upstream.DataSource$Factory);
}


# Gson

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# https://github.com/orhanobut/hawk/issues/143
-keepattributes EnclosingMethod

# Gson specific classes
-keep class sun.misc.Unsafe { *; }

# Facebook Conceal

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.crypto.proguard.annotations.KeepGettersAndSetters

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.crypto.proguard.annotations.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.crypto.proguard.annotations.DoNotStrip *;
}

-keepclassmembers @com.facebook.crypto.proguard.annotations.KeepGettersAndSetters class * {
  void set*(***);
  *** get*();
}


-keep public class pl.droidsonroids.gif.GifIOException{<init>(int, java.lang.String);}
#Prevents warnings for consumers not using AndroidX
-dontwarn androidx.annotation.**


# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

# For old ads classes
-keep public class com.google.ads.**{
   public *;
}

# For mediation
-keepattributes *Annotation*

# Other required classes for Google Play Services
# Read more at http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}








# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response




#-renamesourcefileattribute SourceFile
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-dontwarn com.huawei.**
-keep class com.huawei.** {*;}
-dontwarn org.slf4j.**
-keep class org.slf4j.** {*;}
-dontwarn org.springframework.**
-keep class org.springframework.** {*;}
-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.** {*;}





-keep class * implements java.util.Locale { *; }
-keepclassmembers class * {
    @android.support.annotation.* <fields>;
}
-keepclassmembers class * {
    @androidx.annotation.* <fields>;
}




-keep public class com.gallaryapp.privacyvault.photoeditor.Model.** {*;}
-keep public class com.gallaryapp.privacyvault.photoeditor.Interface.** {*;}
-keep public class com.gallaryapp.privacyvault.photoeditor.MyUtils.** {*;}
-keep public class com.gallaryapp.privacyvault.photoeditor.MyApp.** {*;}

