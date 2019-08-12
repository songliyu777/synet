package com.synet.net.session;

import com.synet.net.tcp.TcpSession;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import reactor.netty.Connection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SessionManager {

    protected ConcurrentHashMap<Long, ISession> sessions = new ConcurrentHashMap<Long, ISession>();

    protected AtomicInteger id_gen = new AtomicInteger(0);

    public static final AttributeKey<Long> channel_session_id = AttributeKey.valueOf("channel_session_id");

    protected ISession emptySession = new EmptySession();

    @Value("${eureka.instance.instance-id}")
    private String instanceId;

    private long id_hashcode = 0;

    public long generateId() {

        if (id_hashcode == 0) {
            id_hashcode = instanceId.hashCode();
            id_hashcode = id_hashcode << 32;
        }

        return id_hashcode + id_gen.incrementAndGet();
    }

    public static int getInstanceId(long session) {
        return (int) (session >> 32);
    }

    public ISession addSession(ISession session) throws RuntimeException {
        log.debug("addSession id[" + session.getId() + "]");
        if (sessions.containsKey(session.getId())) {
            throw new SessionException("exist session key");
        }
        sessions.put(session.getId(), session);
        return session;
    }

    public ISession removeSession(long id) throws RuntimeException {
        log.debug("removeSession id[" + id + "]");
        ISession session = sessions.remove(id);
        if (session != null) {
            return session;
        }
        throw new SessionException("no session key");
    }

    public ISession newTcpSession(Connection c) throws RuntimeException {
        return new TcpSession(generateId(), c);
    }

    public ISession getTcpSession(long id) throws RuntimeException {
        ISession session = sessions.get(id);
        if (session == null) {
            return emptySession;
        }
        if (session instanceof TcpSession) {
            return session;
        }
        throw new SessionException("session is not TcpSession" + session);
    }
}
