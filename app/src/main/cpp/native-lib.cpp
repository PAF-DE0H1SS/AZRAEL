#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_xyz_azraellab_app_MainActivity_nativeString(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}