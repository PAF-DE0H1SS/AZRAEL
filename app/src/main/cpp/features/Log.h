#pragma once

#include <cstdio>
#include <cstring>
#include <string>

#include "Str.h"

#if defined(__ANDROID__)
#include <android/log.h>
#endif

// Логирование в logcat (Android) / stderr (десктоп).
// Уровни: az::log::d/i/w/e. Формат: az::log::d("tag", "fmt", ...);

namespace az {
namespace log {

enum class Level { Verbose, Debug, Info, Warn, Error, Fatal };

inline const char* level_str(Level l) {
    switch (l) {
        case Level::Verbose: return "V";
        case Level::Debug: return "D";
        case Level::Info: return "I";
        case Level::Warn: return "W";
        case Level::Error: return "E";
        case Level::Fatal: return "F";
    }
    return "?";
}

inline void write(Level level, const char* tag, const std::string& msg) {
#if defined(__ANDROID__)
    static const int map[] = {ANDROID_LOG_VERBOSE, ANDROID_LOG_DEBUG, ANDROID_LOG_INFO,
                              ANDROID_LOG_WARN, ANDROID_LOG_ERROR, ANDROID_LOG_FATAL};
    __android_log_write(map[static_cast<int>(level)], tag ? tag : "az", msg.c_str());
#else
    std::fprintf(stderr, "[%s] %s: %s\n", level_str(level), tag ? tag : "az", msg.c_str());
#endif
}

#define AZ_LOG(level, tag, ...) \
    ::az::log::write(::az::log::level, tag, ::az::str::format(__VA_ARGS__))

}  // namespace log
}  // namespace az

// Краткие макросы.
#define AZLOGD(tag, ...) ::az::log::write(::az::log::Level::Debug, tag, ::az::str::format(__VA_ARGS__))
#define AZLOGI(tag, ...) ::az::log::write(::az::log::Level::Info, tag, ::az::str::format(__VA_ARGS__))
#define AZLOGW(tag, ...) ::az::log::write(::az::log::Level::Warn, tag, ::az::str::format(__VA_ARGS__))
#define AZLOGE(tag, ...) ::az::log::write(::az::log::Level::Error, tag, ::az::str::format(__VA_ARGS__))