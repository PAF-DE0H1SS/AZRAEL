#include "Net.h"

#include <cstring>
#include <fcntl.h>
#include <netdb.h>
#include <sys/poll.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <unistd.h>

#include <cerrno>

namespace az {
namespace net {

bool TcpSocket::connect(const std::string& host, uint16_t port, int timeout_ms) {
    close();
    struct addrinfo hints{};
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    char port_str[16];
    std::snprintf(port_str, sizeof(port_str), "%u", static_cast<unsigned>(port));
    struct addrinfo* res = nullptr;
    if (::getaddrinfo(host.c_str(), port_str, &hints, &res) != 0) return false;

    int last_errno = EINVAL;
    for (struct addrinfo* ai = res; ai; ai = ai->ai_next) {
        int s = ::socket(ai->ai_family, ai->ai_socktype | SOCK_CLOEXEC, ai->ai_protocol);
        if (s < 0) continue;

        if (timeout_ms > 0) {
            int flags = ::fcntl(s, F_GETFL, 0);
            ::fcntl(s, F_SETFL, flags | O_NONBLOCK);
            int rc = ::connect(s, ai->ai_addr, ai->ai_addrlen);
            if (rc != 0 && errno == EINPROGRESS) {
                struct pollfd pfd{s, POLLOUT, 0};
                rc = ::poll(&pfd, 1, timeout_ms);
                if (rc == 1) {
                    int soerr = 0;
                    socklen_t len = sizeof(soerr);
                    ::getsockopt(s, SOL_SOCKET, SO_ERROR, &soerr, &len);
                    rc = soerr == 0 ? 0 : -1;
                } else {
                    rc = -1;
                }
            }
            ::fcntl(s, F_SETFL, flags);
            if (rc == 0) {
                fd_ = s;
                break;
            }
            last_errno = errno;
            ::close(s);
        } else {
            if (::connect(s, ai->ai_addr, ai->ai_addrlen) == 0) {
                fd_ = s;
                break;
            }
            last_errno = errno;
            ::close(s);
        }
    }
    ::freeaddrinfo(res);
    return fd_ >= 0;
}

void TcpSocket::close() {
    if (fd_ >= 0) {
        ::close(fd_);
        fd_ = -1;
    }
}

void TcpSocket::set_timeout(int timeout_ms) {
    if (fd_ < 0) return;
    struct timeval tv{};
    tv.tv_sec = timeout_ms / 1000;
    tv.tv_usec = (timeout_ms % 1000) * 1000;
    ::setsockopt(fd_, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
    ::setsockopt(fd_, SOL_SOCKET, SO_SNDTIMEO, &tv, sizeof(tv));
}

ssize_t TcpSocket::send_bytes(const void* data, size_t len) {
    return ::send(fd_, data, len, MSG_NOSIGNAL);
}

ssize_t TcpSocket::recv_bytes(void* buf, size_t len) {
    return ::recv(fd_, buf, len, 0);
}

bool TcpSocket::send_all(const void* data, size_t len) {
    const auto* p = static_cast<const uint8_t*>(data);
    size_t sent = 0;
    while (sent < len) {
        ssize_t n = ::send(fd_, p + sent, len - sent, MSG_NOSIGNAL);
        if (n < 0) {
            if (errno == EINTR) continue;
            if (errno == EAGAIN || errno == EWOULDBLOCK) continue;
            return false;
        }
        if (n == 0) return false;
        sent += static_cast<size_t>(n);
    }
    return true;
}

bool read_crlf_line(TcpSocket& s, std::string& line, size_t max_len, int32_t timeout_ms) {
    line.clear();
    if (timeout_ms > 0) s.set_timeout(timeout_ms);
    char c;
    while (line.size() < max_len) {
        ssize_t n = s.recv_bytes(&c, 1);
        if (n < 0) return false;
        if (n == 0) return false;
        if (c == '\n') {
            if (!line.empty() && line.back() == '\r') line.pop_back();
            return true;
        }
        line += c;
    }
    return false;
}

bool parse_host_port(const std::string& s, std::string& host, uint16_t& port,
                     uint16_t default_port) {
    host.clear();
    port = default_port;
    if (s.empty()) return false;
    if (s.front() == '[') {  // [::1]:443
        size_t close = s.find(']');
        if (close == std::string::npos) return false;
        host = s.substr(1, close - 1);
        if (close + 1 < s.size() && s[close + 1] == ':') {
            port = static_cast<uint16_t>(std::stoul(s.substr(close + 2)));
        }
        return true;
    }
    size_t colon = s.rfind(':');
    if (colon != std::string::npos && s.find(':') == colon) {
        if (colon + 1 < s.size()) {
            host = s.substr(0, colon);
            port = static_cast<uint16_t>(std::stoul(s.substr(colon + 1)));
            return true;
        }
        return false;
    }
    host = s;
    return true;
}

}  // namespace net
}  // namespace az