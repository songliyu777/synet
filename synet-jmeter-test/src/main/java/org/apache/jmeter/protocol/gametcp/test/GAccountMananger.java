package org.apache.jmeter.protocol.gametcp.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GAccountMananger {

    private static final Logger log = LoggerFactory.getLogger(GAccountMananger.class);

    ConcurrentLinkedQueue<GAccount> queue = new ConcurrentLinkedQueue<GAccount>();

    static GAccountMananger instance = new GAccountMananger();

    private GAccountMananger() {

    }

    public static GAccountMananger getInstance() {
        return instance;
    }

    public void InitAccount(){
        log.info("InitAccount");
    }

    public GAccount getAccount()
    {
        GAccount a = queue.poll();
        queue.add(a);
        return a;
    }
}
