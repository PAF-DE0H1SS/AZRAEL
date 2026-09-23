#pragma once

#include <chrono>
#include <cstdint>
#include <ctime>
#include <string>
#include <string_view>

namespace az {

// Утилиты времени (C++17). Proxmox API отдаёт время в виде epoch-секунд
// (status/uptime/running) и ISO8601 в логах и задачах.

namespace timex {

inline int64_t now_ms() {
    return std::chrono::duration_cast<std::chrono::milliseconds>(
               std::chrono::system_clock::now().time_since_epoch())
        .count();
}

inline int64_t now_sec() {
    return std::chrono::duration_cast<std::chrono::seconds>(
               std::chrono::system_clock::now().time_since_epoch())
        .count();
}

inline auto now_steady() {
    return std::chrono::steady_clock::now();
}

inline double elapsed_ms(std::chrono::steady_clock::time_point from) {
    return std::chrono::duration<double, std::milli>(
               std::chrono::steady_clock::now() - from)
        .count();
}

// epoch_sec -> "2026-09-23T17:45:12+05:00" (по zoned-смещению системы).
inline std::string iso8601(int64_t epoch_sec, const std::string& tz_offset = "") {
    std::time_t t = static_cast<std::time_t>(epoch_sec);
    std::tm tm{};
    gmtime_r(&t, &tm);
    char buf[40];
    std::snprintf(buf, sizeof(buf), "%04d-%02d-%02dT%02d:%02d:%02d",
                  tm.tm_year + 1900, tm.tm_mon + 1, tm.tm_mday, tm.tm_hour,
                  tm.tm_min, tm.tm_sec);
    std::string s(buf);
    std::string off = tz_offset;
    if (off.empty()) {
        // offset текущей локальной зоны
        std::tm ltm{};
        std::time_t lt = std::time(nullptr);
#if defined(_WIN32)
        localtime_s(&ltm, &lt);
#else
        localtime_r(&lt, &ltm);
#endif
        std::time_t t2 = std::mktime(&ltm);
        long diff = static_cast<long>(t2 - lt);
        char ob[16];
        std::snprintf(ob, sizeof(ob), "%+03ld:%02ld", diff / 3600, (diff % 3600) / 60);
        off = ob;
    }
    return s + off;
}

inline std::string iso8601() { return iso8601(now_sec()); }

// Обратно: "2026-09-23T17:45:12+05:00" | "2026-09-23 17:45:12" | "epoch" -> sec.
inline int64_t parse_iso8601(std::string_view s) {
    std::tm tm{};
    int year, mon, day, hh, mm, ss, off_hh = 0, off_mm = 0;
    if (std::sscanf(std::string(s.substr(0, 19)).c_str(), "%d-%d-%dT%d:%d:%d",
                    &year, &mon, &day, &hh, &mm, &ss) < 6 &&
        std::sscanf(std::string(s.substr(0, 19)).c_str(), "%d-%d-%d %d:%d:%d",
                    &year, &mon, &day, &hh, &mm, &ss) < 6) {
        return 0;
    }
    tm.tm_year = year - 1900;
    tm.tm_mon = mon - 1;
    tm.tm_mday = day;
    tm.tm_hour = hh;
    tm.tm_min = mm;
    tm.tm_sec = ss;
    // сдвиг времени
    if (s.size() >= 22 && (s[19] == '+' || s[19] == '-') && s[20] >= '0' && s[20] <= '9') {
        std::sscanf(std::string(s.substr(19, 6)).c_str(), "%d:%d", &off_hh, &off_mm);
        if (s[19] == '-') { off_hh = -off_hh; off_mm = -off_mm; }
    }
    std::time_t t = std::timegm(&tm);
    return static_cast<int64_t>(t) - (off_hh * 3600 + off_mm * 60);
}

// "uptime: 12345 сек" -> "3ч 25м" (как в Proxmox).
inline std::string human_uptime(int64_t sec) {
    if (sec < 60) return std::to_string(sec) + "s";
    int64_t m = sec / 60;
    if (m < 60) return std::to_string(m) + "м";
    int64_t h = m / 60;
    m %= 60;
    if (h < 24) return std::to_string(h) + "ч " + std::to_string(m) + "м";
    int64_t d = h / 24;
    h %= 24;
    return std::to_string(d) + "д " + std::to_string(h) + "ч";
}

// "sz: 2147483648" -> "2.0G" (human-размер как в pve).
inline std::string human_size(int64_t bytes) {
    const char* units[] = {"B", "K", "M", "G", "T", "P"};
    double v = static_cast<double>(bytes);
    int u = 0;
    while (v >= 1024.0 && u < 5) {
        v /= 1024.0;
        ++u;
    }
    char buf[32];
    if (u == 0) std::snprintf(buf, sizeof(buf), "%lld", static_cast<long long>(bytes));
    else std::snprintf(buf, sizeof(buf), "%.1f%s", v, units[u]);
    return buf;
}

// Формат скорости: Б/с, КБ/с, МБ/с.
inline std::string human_rate(double bytes_per_sec) {
    if (bytes_per_sec < 1024)
        return [&]() { char b[32]; std::snprintf(b, sizeof(b), "%.0f Б/с", bytes_per_sec); return std::string(b); }();
    if (bytes_per_sec < 1024 * 1024) {
        char b[32];
        std::snprintf(b, sizeof(b), "%.1f КБ/с", bytes_per_sec / 1024.0);
        return b;
    }
    char b[32];
    std::snprintf(b, sizeof(b), "%.1f МБ/с", bytes_per_sec / (1024.0 * 1024.0));
    return b;
}

}  // namespace timex
}  // namespace az