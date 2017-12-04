# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,EnclosingMethod,Signature
-keep class android.support.v7.widget.ShareActionProvider { *; }
-keepclassmembers class ** {
  @com.fasterxml.jackson.annotation.** public *;
}

## Cxense SDK
-keepclassmembers class com.cxense.cxensesdk.model.* {
  *;
}

## Square Picasso specific rules ##
## https://square.github.io/picasso/ ##

-dontwarn com.squareup.okhttp.**

# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic signatures used for parsing the return types reflectively.
-keepattributes Signature

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)

-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keep @interface com.fasterxml.jackson.**
#-keepnames class com.fasterxml.jackson.** { *; }
-keep class com.fasterxml.jackson.annotation.** {
   *;
}
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn java.beans.**
#for 2.8.5 to work on versions of android prior to L
-keep class java.beans.Transient.** {*;}
-keep class java.beans.ConstructorProperties.** {*;}
-keep class java.nio.file.Path.** {*;}

