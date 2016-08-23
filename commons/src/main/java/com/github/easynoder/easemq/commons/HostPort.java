package com.github.easynoder.easemq.commons;

import java.util.Objects;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/23
 * E-mail:easynoder@outlook.com
 */
public class HostPort {

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = 2770;

    private String host;

    private int port;

    public HostPort() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public HostPort(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostPort hostPort = (HostPort) o;
        return port == hostPort.port &&
                Objects.equals(host, hostPort.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HostPort{");
        sb.append("host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append('}');
        return sb.toString();
    }
}
