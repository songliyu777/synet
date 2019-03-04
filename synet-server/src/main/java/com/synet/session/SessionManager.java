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

    public long GenerateId() {
        return id_gen.incrementAndGet();
    }

    public ISession AddSession(ISession session) throws RuntimeException {
        if (sessions.containsKey(session.GetId())) {
            throw new SessionException("exist session key");
        }
        sessions.put(session.GetId(), session);
        return session;
    }

    public ISession RemoveSession(long id) throws RuntimeException {
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

    public TcpSession GetTcpSession(long id) throws RuntimeException {
        if (!sessions.containsKey(id)) {
            throw new SessionException("no tcp session key");
        }
        ISession session = sessions.get(id);
        if(session instanceof TcpSession){
            return (TcpSession) sessions.get(id);
        }

        throw new SessionException("session is not TcpSession" + session);
    }
}
