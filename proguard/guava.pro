# Source: https://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization/20935044
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**
-dontwarn com.google.errorprone.**
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.ClassValue