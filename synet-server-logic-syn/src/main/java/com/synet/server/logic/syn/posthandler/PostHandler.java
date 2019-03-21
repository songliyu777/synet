package com.synet.server.logic.syn.posthandler;

import com.synet.server.logic.syn.database.TestRepository;
import com.synet.server.logic.syn.database.bean.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class PostHandler {

    @Autowired
    TestRepository testRepository;
    AtomicLong l = new AtomicLong(10000000);

    @PostMapping(value = "/test")
    public byte[] getbytes(HttpServletRequest request) throws IOException
    {
        int len = request.getContentLength();
        ServletInputStream is = request.getInputStream();
        byte[] buffer = new byte[len];
        is.read(buffer, 0, len);

        Test t =new Test();
        t.setId(l.getAndIncrement());
        t.setName("test123");
        t.setPassword("test123");
        testRepository.saveAndFlush(t);

        return buffer;
    }
}
