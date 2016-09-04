package com.github.easynoder.easemq.commons;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/3
 * E-mail:easynoder@outlook.com
 */
public class ZkClientTest {


    private ZkClient client;

    @Before
    public void init() {
        client = new ZkClient("localhost:2181");
        client.start();
    }

/*    @Test
    public void testCreateNode() {
        Assert.assertTrue("create node fail!", client.createNode("/servers/localhost:2770", "1".getBytes(Charset.forName("utf-8"))));
    }*/


/*
    @Test
    public void testCheckExists() {
        Assert.assertTrue("check exists fail!", client.checkExists("/servers"));
    }
*/

    @Test
    public void testGetChildren() {
        System.out.println(client.getChildren("/servers"));
    }

    @After
    public void teardown() {
        client.close();
    }

}
