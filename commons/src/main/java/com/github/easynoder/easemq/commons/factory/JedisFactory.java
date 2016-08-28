package com.github.easynoder.easemq.commons.factory;

import redis.clients.jedis.Jedis;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class JedisFactory {

    private static Jedis jedis = new Jedis("localhost", 6379);

    public static Jedis getJedis() {
        return jedis;
    }
}
