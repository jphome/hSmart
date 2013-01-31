#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <android/log.h>
#include <jni.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "util.h"
#include "drivers_map.h"

int net_connect()
{
  int sockfd;
  struct sockaddr_in servaddr;

  if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    LOGE("JNI===> socket()");

  bzero(&servaddr, sizeof(servaddr));
  servaddr.sin_family = AF_INET;
  servaddr.sin_port = htons(8080);

  if (inet_pton(AF_INET, "192.168.1.1", &servaddr.sin_addr) <= 0) {
    LOGE("JNI===> inet_pton");
  }

  if (connect(sockfd, (struct sockaddr_in *)&servaddr, sizeof(servaddr)) < 0) {
    LOGE("JNI===> connect");
  }  
}

void
FFMpegPlayerAndroid_play(JNIEnv *env, jobject this, jstring fname, jint width, jint height)
{
  jboolean isCopy;
  const char *szfname = (*env)->GetStringUTFChars(env, fname, &isCopy);
  FILE *fp=fopen("/mnt/sdcard/pic/light_on.png", "rb"); // "/sdcard/512x400_565.bmp", "rb");
  char *buf=(char*)malloc(width*height*2+70);
  fread(buf, 1, width*height*2+70, fp);
  fclose(fp);

  while(1) {
    LOGI(szfname);

    void *pbits;
    VideoDriver_getPixels(width, height, &pbits);
    
    memcpy(pbits, buf+70, width*height*2);      
    VideoDriver_updateSurface();
    
    sleep(1);
  }
  (*env)->ReleaseStringUTFChars(env, fname, szfname);

  return;
}

/*
// 通过这个得到Surface指针，而且java代码调用时把surface对象传递过来了
static Surface* getNativeSurface(JNIEnv *env, jobject jsurface) {
  jclass clazz = (*env)->FindClass(env, "android/view/Surface");
  jfieldID field_surface = (*env)->GetFieldID(env, clazz, "mSurface", "I");
  if(field_surface == NULL) {
    return NULL;
  }
  return (Surface *) env->GetIntField(jsurface, field_surface);
}
*/

jstring
getStream(JNIEnv *env, jobject this)
{
  LOGI("JNI===> getStream()");

  return (*env)->NewStringUTF(env, "Hello from JNI !");
}
