#include "Json.h"

#include <cmath>
#include <cstring>
#include <sstream>
#include <stdexcept>

namespace az {
namespace {

// --- Рекурсивный парсер ---

struct Parser {
    std::string_view s;
    size_t pos = 0;

    [[noreturn]] void fail(const char* msg) const {
        throw std::runtime_error(std::string("json: ") + msg);
    }

    void ws() {
        while (pos < s.size() && (s[pos] == ' ' || s[pos] == '\t' || s[pos] == '\r' ||
                                  s[pos] == '\n'))
            ++pos;
    }

    bool eat(char c) {
        ws();
        if (pos < s.size() && s[pos] == c) {
            ++pos;
            return true;
        }
        return false;
    }

    void expect(char c) {
        if (!eat(c)) fail("unexpected character");
    }

    uint32_t hex4() {
        if (pos + 4 > s.size()) fail("bad \\u escape");
        uint32_t v = 0;
        for (int i = 0; i < 4; ++i) {
            char c = s[pos++];
            v <<= 4;
            if (c >= '0' && c <= '9') v |= uint32_t(c - '0');
            else if (c >= 'a' && c <= 'f') v |= uint32_t(c - 'a' + 10);
            else if (c >= 'A' && c <= 'F') v |= uint32_t(c - 'A' + 10);
            else fail("bad hex digit");
        }
        return v;
    }

    std::string utf8_enc(uint32_t cp) {
        std::string out;
        if (cp < 0x80) {
            out += char(cp);
        } else if (cp < 0x800) {
            out += char(0xc0 | (cp >> 6));
            out += char(0x80 | (cp & 0x3f));
        } else if (cp < 0x10000) {
            out += char(0xe0 | (cp >> 12));
            out += char(0x80 | ((cp >> 6) & 0x3f));
            out += char(0x80 | (cp & 0x3f));
        } else {
            out += char(0xf0 | (cp >> 18));
            out += char(0x80 | ((cp >> 12) & 0x3f));
            out += char(0x80 | ((cp >> 6) & 0x3f));
            out += char(0x80 | (cp & 0x3f));
        }
        return out;
    }

    Json parse_value() {
        ws();
        if (pos >= s.size()) fail("empty");
        char c = s[pos];
        if (c == '{') return parse_object();
        if (c == '[') return parse_array();
        if (c == '"') return Json(parse_string());
        if (c == 't') { expect_keyword("true"); return Json(true); }
        if (c == 'f') { expect_keyword("false"); return Json(false); }
        if (c == 'n') { expect_keyword("null"); return Json(nullptr); }
        if (c == '-' || (c >= '0' && c <= '9')) return Json(parse_number());
        fail("invalid value");
    }

    void expect_keyword(const char* kw) {
        size_t n = std::strlen(kw);
        if (pos + n > s.size() || s.substr(pos, n) != kw) fail("bad keyword");
        pos += n;
    }

    double parse_number() {
        size_t start = pos;
        if (pos < s.size() && s[pos] == '-') ++pos;
        while (pos < s.size() && (s[pos] >= '0' && s[pos] <= '9')) ++pos;
        if (pos < s.size() && s[pos] == '.') {
            ++pos;
            while (pos < s.size() && (s[pos] >= '0' && s[pos] <= '9')) ++pos;
        }
        if (pos < s.size() && (s[pos] == 'e' || s[pos] == 'E')) {
            ++pos;
            if (pos < s.size() && (s[pos] == '+' || s[pos] == '-')) ++pos;
            while (pos < s.size() && (s[pos] >= '0' && s[pos] <= '9')) ++pos;
        }
        return std::stod(std::string(s.substr(start, pos - start)));
    }

    std::string parse_string() {
        expect('"');
        std::string out;
        while (true) {
            if (pos >= s.size()) fail("unterminated string");
            char c = s[pos++];
            if (c == '"') break;
            if (c == '\\') {
                if (pos >= s.size()) fail("bad escape");
                char e = s[pos++];
                switch (e) {
                    case '"': out += '"'; break;
                    case '\\': out += '\\'; break;
                    case '/': out += '/'; break;
                    case 'b': out += '\b'; break;
                    case 'f': out += '\f'; break;
                    case 'n': out += '\n'; break;
                    case 'r': out += '\r'; break;
                    case 't': out += '\t'; break;
                    case 'u': {
                        uint32_t cp = hex4();
                        if (cp >= 0xd800 && cp <= 0xdbff) {  // суррогатная пара
                            if (pos + 1 < s.size() && s[pos] == '\\' && s[pos + 1] == 'u') {
                                pos += 2;
                                uint32_t lo = hex4();
                                cp = 0x10000 + ((cp - 0xd800) << 10) + (lo - 0xdc00);
                            }
                        }
                        out += utf8_enc(cp);
                        break;
                    }
                    default: fail("bad escape char");
                }
            } else {
                out += c;
            }
        }
        return out;
    }

    Json::Array parse_array() {
        expect('[');
        Json::Array arr;
        if (eat(']')) return arr;
        while (true) {
            arr.push_back(parse_value());
            if (eat(']')) return arr;
            expect(',');
        }
    }

    Json::Object parse_object() {
        expect('{');
        Json::Object obj;
        if (eat('}')) return obj;
        while (true) {
            ws();
            std::string key = parse_string();
            expect(':');
            obj.emplace(std::move(key), parse_value());
            if (eat('}')) return obj;
            expect(',');
        }
    }
};

// --- Сериализация ---

inline void write_string(std::string& out, std::string_view s) {
    out += '"';
    for (unsigned char c : s) {
        switch (c) {
            case '"': out += "\\\""; break;
            case '\\': out += "\\\\"; break;
            case '\b': out += "\\b"; break;
            case '\f': out += "\\f"; break;
            case '\n': out += "\\n"; break;
            case '\r': out += "\\r"; break;
            case '\t': out += "\\t"; break;
            default:
                if (c < 0x20) {
                    out += "\\u00";
                    out += "0123456789abcdef"[c >> 4];
                    out += "0123456789abcdef"[c & 0x0f];
                } else {
                    out += char(c);
                }
        }
    }
    out += '"';
}

inline void write_value(std::string& out, const Json& j, bool pretty, int depth) {
    const auto& v = j.value();
    if (std::holds_alternative<std::nullptr_t>(v)) {
        out += "null";
    } else if (std::holds_alternative<bool>(v)) {
        out += std::get<bool>(v) ? "true" : "false";
    } else if (std::holds_alternative<double>(v)) {
        double d = std::get<double>(v);
        if (std::isnan(d)) { out += "null"; return; }
        if (std::isinf(d)) { out += "null"; return; }
        if (std::floor(d) == d && std::fabs(d) < 1e15) {
            out += std::to_string(static_cast<long long>(d));
        } else {
            out += std::to_string(d);
        }
    } else if (std::holds_alternative<std::string>(v)) {
        write_string(out, std::get<std::string>(v));
    } else if (std::holds_alternative<Json::Array>(v)) {
        const auto& arr = std::get<Json::Array>(v);
        out += '[';
        for (size_t i = 0; i < arr.size(); ++i) {
            if (i) out += ',';
            if (pretty) { out += '\n'; for (int k = 0; k < depth + 1; ++k) out += "  "; }
            write_value(out, arr[i], pretty, depth + 1);
        }
        if (pretty && !arr.empty()) { out += '\n'; for (int k = 0; k < depth; ++k) out += "  "; }
        out += ']';
    } else if (std::holds_alternative<Json::Object>(v)) {
        const auto& obj = std::get<Json::Object>(v);
        out += '{';
        size_t i = 0;
        for (const auto& [k, val] : obj) {
            if (i++) out += ',';
            if (pretty) { out += '\n'; for (int k2 = 0; k2 < depth + 1; ++k2) out += "  "; }
            write_string(out, k);
            out += ':';
            if (pretty) out += ' ';
            write_value(out, val, pretty, depth + 1);
        }
        if (pretty && !obj.empty()) { out += '\n'; for (int k = 0; k < depth; ++k) out += "  "; }
        out += '}';
    }
}

}  // namespace

bool Json::parse(std::string_view text, Json& out) {
    try {
        Parser p{text, 0};
        Json v = p.parse_value();
        p.ws();
        if (p.pos != text.size()) return false;
        out = std::move(v);
        return true;
    } catch (...) {
        return false;
    }
}

std::string Json::dump(bool pretty) const {
    std::string out;
    write_value(out, *this, pretty, 0);
    return out;
}

}  // namespace az