package com.yexc.interceptor;

import com.yexc.common.GlobalSessions;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by Administrator on 2017/8/17.
 */
public class sessionListener implements HttpSessionListener{

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        System.out.println("creat session----");
        GlobalSessions.addSession(
                httpSessionEvent.getSession().getId(),
                httpSessionEvent.getSession()
        );
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        GlobalSessions.delSession(httpSessionEvent.getSession().getId());
    }
}
