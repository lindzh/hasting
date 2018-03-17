package com.lindzh.hasting.webui;

import org.junit.Test;

/**
 * Created by lin on 2016/12/17.
 */
public class SimpleTest extends AbstractTestCase {

    @Test
    public void startup() throws InterruptedException {
        System.out.println("started------");
        Thread.currentThread().sleep(1000000000);
    }
}
