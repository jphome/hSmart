#define JNI_VERSION_1_8 0x00010008

#include <stdlib.h>
#include <android/log.h>
#include <jni.h>
#include "util.h"

extern jstring getStream(JNIEnv *env, jobject this);
extern void FFMpegPlayerAndroid_play(JNIEnv *env, jobject obj, jstring fname, jint width, jint height);

static const char *classPathName = "jphome/hsmart/jni/FFMpegPlayer";

static JavaVM *sVm;

static JNINativeMethod methods[] = {
	{ "nativeGetStream", "()Ljava/lang/String;", (void *)getStream },
	{ "nativePlay", "(Ljava/lang/String;II)V", (void *)FFMpegPlayerAndroid_play },
};

static int jniRegisterNativeMethods(JNIEnv* env, const char* className, const JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    LOGI("JNI===> Registering %s natives \n", className);

    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return -1;
    }    
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return -1;
	}

    return 0;
}
int register_FFMpegPlayerAndroid(JNIEnv *env)
{
	return jniRegisterNativeMethods(env, classPathName, methods, sizeof(methods) / sizeof(methods[0]));
}

// Throw an exception with the specified class and an optional message.
int jniThrowException(JNIEnv* env, const char* className, const char* msg) 
{
    jclass exceptionClass = (*env)->FindClass(env, className);
    if (exceptionClass == NULL) 
        return -1;

    if ((*env)->ThrowNew(env, exceptionClass, msg) != JNI_OK) {
    }

    return 0;
}

JNIEnv* getJNIEnv() 
{
    JNIEnv* env = NULL;
    if ((*sVm)->GetEnv(sVm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
      LOGE("JNI===> Failed to obtain JNIEnv");
      return NULL;
    }

    return env;
}

// Dalvik虚拟机加载C库时，第一件事是调用JNI_OnLoad()函数
jint JNI_OnLoad(JavaVM* vm, void* reserved) 
{
    JNIEnv* env = NULL;
    jint result = JNI_ERR;
	sVm = vm;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
      LOGE("JNI===> GetEnv failed!");
      return result;
    }
	
    LOGI("JNI===> loading ...");
	
    if(register_FFMpegPlayerAndroid(env) != JNI_OK)
    	goto end;

    result = JNI_VERSION_1_4;

end:
    return result;
}
