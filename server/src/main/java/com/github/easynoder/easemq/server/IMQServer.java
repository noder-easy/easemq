package com.github.easynoder.easemq.server;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public interface IMQServer {

    public void start() throws InterruptedException;

    public void close();
}
