#ifndef __DRIVERS_MAP_H__
#define __DRIVERS_MAP_H__

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// methods for access to surface on OS
typedef int (*videoDriver_register_t) (JNIEnv*, jobject);
typedef int (*videoDriver_getPixels_t) (int width, int height, void** pixels);
typedef int (*videoDriver_updateSurface_t) (void);
typedef int (*videoDriver_unregister_t) (void);

// create pointer on system methods for accessing audio and video system

#include "surface.h"

videoDriver_register_t VideoDriver_register = AndroidSurface_register;
videoDriver_getPixels_t VideoDriver_getPixels = AndroidSurface_getPixels;
videoDriver_updateSurface_t VideoDriver_updateSurface = AndroidSurface_updateSurface;
videoDriver_unregister_t VideoDriver_unregister = AndroidSurface_unregister;

#ifdef __cplusplus
} // end of cpluplus
#endif

#endif
