package org.apache.jmeter.protocol.gametcp.test;

import com.synet.protobuf.TestOuterClass;
import com.synet.protocol.ProtocolHead;
import com.synet.protocol.ProtocolHeadDefine;
import com.synet.protocol.TcpNetProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.jmeter.protocol.gametcp.sampler.ReadException;
import org.apache.jmeter.protocol.gametcp.sampler.TCPClient;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.util.JOrphanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

public class GameTcpClientTest implements TCPClient {

    private static final Logger log = LoggerFactory.getLogger(GameTcpClientTest.class);

    ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 1024);

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
        TestOuterClass.Test test = TestOuterClass.Test.newBuilder().setName("input 1").setPassword("input 2").build();
        TcpNetProtocol protocol = TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) 1, 1, test.toByteArray());
        os.write(protocol.toArray());
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
            TcpNetProtocol protocol = null;
            while ((x = is.read(readBuffer.array())) > -1) {
                if (readBuffer.remaining() >= ProtocolHead.headSize) {

                    bodysize = readBuffer.getInt(TcpNetProtocol.length_index);

                    log.info("bodysize:" + bodysize);
                }
                int packsize = ProtocolHead.headSize + bodysize;
                if (readBuffer.remaining() >= packsize) {
                    ByteBuf buf = Unpooled.buffer(packsize);
                    buf.writeBytes(readBuffer.array(), 0, packsize);
                    int last = readBuffer.remaining() - packsize;
                    if (last > 0) {
                        readBuffer.position(packsize);
                        readBuffer.compact();
                    }
                    protocol = TcpNetProtocol.parse(buf);
                    buf.release();
                    break;
                }
            }
            final String hexString = JOrphanUtils.baToHexString(protocol.toArray());
            sampleResult.latencyEnd();
            return hexString;
        } catch (IOException e) {
            throw new ReadException("", e, "remaining:" + readBuffer.remaining());
        }
    }

    @Override
    public byte getEolByte() {
        return 0;
    }

    @Override
    public String getCharset() {
        return null;
    }

    @Override
    public void setEolByte(int eolInt) {

    }
}
