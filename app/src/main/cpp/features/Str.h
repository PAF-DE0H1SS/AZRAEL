#pragma once

#include <cstdarg>
#include <cstdint>
#include <sstream>
#include <string>
#include <string_view>
#include <vector>

namespace az {

// Базовая библиотека строковых утилит (C++17, без зависимостей).
// Все функции работают с UTF-8 как с байтами (Android/си-ASCII-совместимо).

namespace str {

inline std::string trim(std::string_view s) {
    size_t b = 0, e = s.size();
    while (b < e && (s[b] <= ' ' || s[b] == '\t' || s[b] == '\r' || s[b] == '\n')) ++b;
    while (e > b && (s[e - 1] <= ' ' || s[e - 1] == '\t' || s[e - 1] == '\r' || s[e - 1] == '\n')) --e;
    return std::string(s.substr(b, e - b));
}

inline std::string to_lower(std::string_view s) {
    std::string r(s);
    for (char& c : r) if (c >= 'A' && c <= 'Z') c = char(c - 'A' + 'a');
    return r;
}

inline std::string to_upper(std::string_view s) {
    std::string r(s);
    for (char& c : r) if (c >= 'a' && c <= 'z') c = char(c - 'a' + 'A');
    return r;
}

inline bool starts_with(std::string_view s, std::string_view prefix) {
    return s.size() >= prefix.size() && s.compare(0, prefix.size(), prefix) == 0;
}

inline bool ends_with(std::string_view s, std::string_view suffix) {
    return s.size() >= suffix.size() &&
           s.compare(s.size() - suffix.size(), suffix.size(), suffix) == 0;
}

inline bool contains(std::string_view s, std::string_view needle) {
    return s.find(needle) != std::string_view::npos;
}

inline std::vector<std::string> split(std::string_view s, char delim,
                                      bool keep_empty = true) {
    std::vector<std::string> out;
    size_t start = 0;
    for (size_t i = 0; i <= s.size(); ++i) {
        if (i == s.size() || s[i] == delim) {
            if (keep_empty || i > start) out.emplace_back(s.substr(start, i - start));
            start = i + 1;
        }
    }
    return out;
}

// Разбиение с учётом кавычек и скобок (полезно для разбора CLI-команд,
// конфигов типа vpn:// ссылок). Пример: parse_args("a 'b c' \"d\" (e f)").
inline std::vector<std::string> parse_args(std::string_view s) {
    std::vector<std::string> out;
    std::string cur;
    char quote = 0;
    int depth = 0;
    for (char c : s) {
        if (quote) {
            if (c == quote) quote = 0;
            else cur += c;
        } else if (c == '\'' || c == '"') {
            quote = c;
        } else if (c == '(' || c == '[' || c == '{') {
            depth++;
            cur += c;
        } else if (c == ')' || c == ']' || c == '}') {
            if (depth > 0) depth--;
            cur += c;
        } else if (std::isspace(static_cast<unsigned char>(c)) && depth == 0) {
            if (!cur.empty()) { out.push_back(cur); cur.clear(); }
        } else {
            cur += c;
        }
    }
    if (!cur.empty()) out.push_back(cur);
    return out;
}

// printf-подобное форматирование с безопасным буфером.
inline std::string format(const char* fmt, ...) __attribute__((format(printf, 1, 2)));

inline std::string format(const char* fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    va_list ap2;
    va_copy(ap2, ap);
    int n = std::vsnprintf(nullptr, 0, fmt, ap);
    va_end(ap);
    if (n < 0) { va_end(ap2); return {}; }
    std::string buf(static_cast<size_t>(n), '\0');
    std::vsnprintf(buf.data(), buf.size() + 1, fmt, ap2);
    va_end(ap2);
    return buf;
}

// Замена всех вхождений (без regex, для простых случаев).
inline std::string replace_all(std::string s, std::string_view from, std::string_view to) {
    if (from.empty()) return s;
    size_t pos = 0;
    while ((pos = s.find(from, pos)) != std::string::npos) {
        s.replace(pos, from.size(), to);
        pos += to.size();
    }
    return s;
}

// s[n] > 'ф'  ->  q-encoding (RFC 2047) для MIME/заголовков, если понадобится.
inline bool is_printable_ascii(std::string_view s) {
    for (unsigned char c : s) if (c < 0x20 || c > 0x7e) return false;
    return true;
}

}  // namespace str

}  // namespace az