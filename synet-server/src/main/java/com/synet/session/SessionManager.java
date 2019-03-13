package com.synet.session;

import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.Connection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SessionManager {

    public static SessionManager instance = new SessionManager();

    public static SessionManager GetInstance() {
        return instance;
    }

    protected ConcurrentHashMap<Long, ISession> sessions = new ConcurrentHashMap<Long, ISession>();

    protected AtomicLong id_gen = new AtomicLong(0);

    public static final AttributeKey<Long> channel_session_id = AttributeKey.valueOf("channel_session_id");

    protected ISession emptySession = new EmptySession();

    public long GenerateId() {
        return id_gen.incrementAndGet();
    }

    public ISession AddSession(ISession session) throws RuntimeException {
        log.debug("AddSession id[" + session.getId() + "]");
        if (sessions.containsKey(session.getId())) {
            throw new SessionException("exist session key");
        }
        sessions.put(session.getId(), session);
        return session;
    }

    public ISession RemoveSession(long id) throws RuntimeException {
        log.debug("RemoveSession id[" + id + "]");
        if (sessions.containsKey(id)) {
            ISession session = sessions.get(id);
            sessions.remove(id);
            return session;
        }
        throw new SessionException("no session key");
    }

    public ISession NewTcpSession(Connection c) throws RuntimeException {
        return new TcpSession(GenerateId(), c);
    }

    public ISession GetTcpSession(long id) throws RuntimeException {
        if (!sessions.containsKey(id)) {
            return emptySession;
        }
        ISession session = sessions.get(id);
        if (session instanceof TcpSession) {
            return sessions.get(id);
        }

        throw new SessionException("session is not TcpSession" + session);
    }
}
