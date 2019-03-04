package com.synet.session;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    public static SessionManager instance = new SessionManager();

    public static SessionManager getInstance(){
        return instance;
    }

    protected ConcurrentHashMap<Long, ISession> sessions = new ConcurrentHashMap<Long, ISession>();

    public void AddSesion(ISession session)
    {
        sessions.put(session.GetId(),session);
    }
}
