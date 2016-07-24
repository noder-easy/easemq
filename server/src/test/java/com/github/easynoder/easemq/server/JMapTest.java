package com.github.easynoder.easemq.server;

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/21
 * E-mail:easynoder@outlook.com
 */
public class JMapTest {

    public static void main(String[] args) {
        int i = 0;
        while (true) {
            System.out.println("test:" + i++);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
