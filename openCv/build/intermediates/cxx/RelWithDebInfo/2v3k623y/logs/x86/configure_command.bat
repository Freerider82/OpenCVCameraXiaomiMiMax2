@echo off
"C:\\Users\\user\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HZ:\\Oleg\\Android\\OpenCVCameraXiaomiMiMax2\\openCv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\user\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\user\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\user\\AppData\\Local\\Android\\Sdk\\ndk\\26.1.10909125\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\user\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=Z:\\Oleg\\Android\\OpenCVCameraXiaomiMiMax2\\openCv\\build\\intermediates\\cxx\\RelWithDebInfo\\2v3k623y\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=Z:\\Oleg\\Android\\OpenCVCameraXiaomiMiMax2\\openCv\\build\\intermediates\\cxx\\RelWithDebInfo\\2v3k623y\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BZ:\\Oleg\\Android\\OpenCVCameraXiaomiMiMax2\\openCv\\.cxx\\RelWithDebInfo\\2v3k623y\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
