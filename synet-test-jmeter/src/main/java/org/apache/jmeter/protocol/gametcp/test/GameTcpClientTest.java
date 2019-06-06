package org.apache.jmeter.protocol.gametcp.test;

import com.synet.net.protocol.ProtocolHead;
import com.synet.net.protocol.NetProtocol;
import org.apache.jmeter.protocol.gametcp.sampler.ReadException;
import org.apache.jmeter.protocol.gametcp.sampler.TCPClient;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class GameTcpClientTest implements TCPClient {

    private static final Logger log = LoggerFactory.getLogger(GameTcpClientTest.class);

    ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 1024);

    byte[] readTmp = new byte[0xFFFF];

    MessageHandler messageHandler = new MessageHandler();

    Queue<String> sendQueue = new LinkedList();

    @Override
    public void setupTest() {

    }

    @Override
    public void teardownTest() {

    }

    @Override
    public void write(OutputStream os, InputStream is) throws IOException {
        throw new UnsupportedOperationException(
                "Method not supported for Length-Prefixed data.");
    }

    @Override
    public void write(OutputStream os, String s) throws IOException {
        if (s.startsWith(ProcessDefine.NEXT)) {
            String command = sendQueue.poll();
            String[] param = s.split(" ");
            for (int i = 1; i < param.length; i++) {
                command += " ";
                command += param[i];
            }
            if (command != null) {
                messageHandler.send(os, command);
            }
        } else {
            String[] commands = s.split("\n");
            for (String command : commands) {
                sendQueue.add(command);
            }
        }
    }

    @Deprecated
    public String read(InputStream is) throws ReadException {
        throw new UnsupportedOperationException("Method not supported for Deprecated.");
    }

    @Override
    public String read(InputStream is, SampleResult sampleResult) throws ReadException {
        try {
            int x = 0;
            int bodysize = 0;
            String result = "";
            if (sendQueue.size() == 0) {
                return ProcessDefine.END;
            }
            if ((x = is.read(readTmp)) > -1) {
                readBuffer.put(readTmp, 0, x);
                if (readBuffer.position() >= ProtocolHead.headSize) {
                    bodysize = readBuffer.getInt(NetProtocol.length_index);
                }
                int packsize = ProtocolHead.headSize + bodysize;
                if (readBuffer.position() >= packsize) {
                    ByteBuffer buf = ByteBuffer.allocate(packsize);
                    buf.put(readBuffer.array(), 0, packsize);
                    int last = readBuffer.position() - packsize;
                    if (last >= 0) {
                        readBuffer.position(packsize);
                        readBuffer.compact();
                        readBuffer.position(last);
                    }
                    result = messageHandler.handle(buf);
                }
            }
            return result;
        } catch (IOException e) {
            throw new ReadException("", e, "position:" + readBuffer.position());
        }
    }

    @Override
    public byte getEolByte() {
        return (byte) messageHandler.getEndOfCommand();
    }

    @Override
    public String getCharset() {
        return null;
    }

    @Override
    public void setEolByte(int eolInt) {
        messageHandler.setEndOfCommand((short) eolInt);
        log.info("end of command:" + (short) eolInt);
    }
}
