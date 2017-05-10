# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)



#
include_directories(src/main/cpp/3rdParty/SoundTouch/include)



# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             soundtouch

             # Sets the library as a shared library.
             STATIC

             # Provides a relative path to your source file(s).
             src/main/cpp/3rdParty/SoundTouch/AAFilter.cpp
             src/main/cpp/3rdParty/SoundTouch/FIFOSampleBuffer.cpp
             src/main/cpp/3rdParty/SoundTouch/FIRFilter.cpp
             src/main/cpp/3rdParty/SoundTouch/cpu_detect_x86.cpp
             src/main/cpp/3rdParty/SoundTouch/sse_optimized.cpp
             src/main/cpp/3rdParty/SoundTouch/RateTransposer.cpp
             src/main/cpp/3rdParty/SoundTouch/SoundTouch.cpp
             src/main/cpp/3rdParty/SoundTouch/InterpolateCubic.cpp
             src/main/cpp/3rdParty/SoundTouch/InterpolateLinear.cpp
             src/main/cpp/3rdParty/SoundTouch/InterpolateShannon.cpp
             src/main/cpp/3rdParty/SoundTouch/TDStretch.cpp
             src/main/cpp/3rdParty/SoundTouch/BPMDetect.cpp
             src/main/cpp/3rdParty/SoundTouch/PeakFinder.cpp)


add_library( medialink
             SHARED
             src/main/cpp/MediaLink/SoundTouch-jni.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       medialink

                       soundtouch

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )