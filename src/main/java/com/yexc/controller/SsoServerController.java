package com.yexc.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.yexc.common.TimeLimit;
import com.yexc.common.connection;
import com.yexc.common.ssoToken;
import com.yexc.model.TokenInfo;
import com.yexc.common.collect;
import com.yexc.model.registerInfo;
import com.yexc.model.verifyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


@Controller
@RequestMapping({"/sso"})
public class SsoServerController {

    Logger log = LoggerFactory.getLogger(SsoServerController.class);

    @RequestMapping({"/page/login"})
    public String pageLogin(HttpServletRequest request) {
        String returnUrl = request.getParameter("returnUrl");
        //从Cookie当中获取TGTId
        String TGTId = getTGTId(request);
        if (TGTId == null) {
            log.info("未登录，跳转到登录界面");
            //未登录重定向到登录界面
            return "redirect:/views/login.jsp?returnUrl=" + returnUrl;
        } else {
            //获取TGT
            String TGT = collect.TGTMap.get(TGTId);
            if (TGT == null) {
                //令牌无效
                log.info("令牌无效");
                return "redirect:/views/login.jsp?returnUrl=" + returnUrl;
            } else {
                //重定向到客户端
                log.info("登录成功");
                return "redirect:" + returnUrl + "?tempToken=" + getTempToken(TGT);
            }
        }
    }

    @RequestMapping({"/user/login"})
    public String userLogin(HttpServletRequest request, HttpServletResponse response) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String returnUrl = request.getParameter("returnUrl");
        if ("ye".equals(userName) && "123".equals(password)) {
            //登录成功
            log.info("登录成功");
            //生成一个TGT
            String TGT = getTGT(response, request);
            log.info("生成一个TGT" + TGT);
            //生成TGT对应的用户信息
            TokenInfo tokenInfo = new TokenInfo(0, userName, null, request.getSession().getId());
            collect.InfoMap.put(TGT, tokenInfo);
            log.info("InfoMap---" + collect.InfoMap);
            return "redirect:" + returnUrl + "?tempToken="
                    + getTempToken(TGT);
        } else {
            return "redirect:redirect:/views/login.jsp?flag=false";
        }
    }


    @RequestMapping({"/token/verify"})
    @ResponseBody
    public verifyResponse tokenVerify(HttpServletRequest request, HttpServletResponse response) {
        String localSessionId = request.getParameter("localSessionId");
        String tempToken = request.getParameter("tempToken");
        String loginOutURl = request.getParameter("loginOutUrl");
        //获得临时令牌对应的TGT
        String TGT = collect.tempTokenMap.get(tempToken);
        //删除临时令牌
        collect.tempTokenMap.remove(tempToken);
        verifyResponse verifyResponse = new verifyResponse();
        if (TGT == null) {
//            //令牌校验为空
            verifyResponse.setReturnCode("500");
        } else {
            //全局Id
            TokenInfo tokenInfo = collect.InfoMap.get(TGT);
            String globalId = tokenInfo.getGlobalId();
            //注册应用
            register(globalId, localSessionId, loginOutURl);
            //将基本信息返回
            verifyResponse.setReturnCode("200");
            verifyResponse.setGlobalId(globalId);
            verifyResponse.setUserName(tokenInfo.getUsername());
        }
        return verifyResponse;
    }

    @RequestMapping({"/user/loginOut"})
    public verifyResponse userloginOut(HttpServletRequest request) throws Exception {
        String globalId = request.getParameter("globalId");
        verifyResponse verifyResponse = new verifyResponse();
        request.getSession().invalidate();
        if (globalId != null) {
            //通知所有的系统登出
            try {
                log.info(globalId);
                loginOutAll(collect.localAndGobalMap.get(globalId));
                collect.localAndGobalMap.remove(globalId);
                verifyResponse.setReturnCode("200");
            } catch (Exception e) {
                verifyResponse.setReturnCode("404");
                e.printStackTrace();
            }
        }
        return verifyResponse;
    }


    @RequestMapping({"/user/cookie"})
    public verifyResponse cookisTest(HttpServletRequest request) {
        getTGTId(request);
        return null;
    }


    private String getTempToken(String TGT) {
        //令牌有效
        TokenInfo tokenInfo = collect.InfoMap.get(TGT);
        //生成临时令牌
        String username = tokenInfo.getUsername();
        String tempToken = ssoToken.EncoderByMd5(username + System.currentTimeMillis());
        //将临时令牌和TGT对应
        collect.tempTokenMap.put(tempToken, TGT);
        return tempToken;
    }


    //从cookie当中获取TGTId
    private String getTGTId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                log.info(c.getName());
                log.info(c.getValue());
                String name = c.getName();
                if ("TGTId".equals(name))
                    return c.getValue();
            }
            return null;
        } else
            return null;
    }

    private String getTGT(HttpServletResponse response, HttpServletRequest request) {
            String TGT = ssoToken.getUUID();
            String TGTId = ssoToken.getUUID();
            //将TGTId放入cookie
            Cookie cookie = new Cookie("TGTId", TGTId);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            cookie.setPath("/");
            response.addCookie(cookie);
            //将TGTId和TGT对应
            collect.TGTMap.put(TGTId, TGT);
            //开启子线程
            TimeLimit timeLimit = new TimeLimit(TGTId, request.getSession().getId());
            Thread t = new Thread(timeLimit);
            t.start();
            return TGT;
        }

        //注册应用

    private void register(String globalId, String localSessionId, String loginOutURl) {
        ArrayList<registerInfo> list = collect.localAndGobalMap.get(globalId);
        registerInfo registerInfo = new registerInfo(loginOutURl, localSessionId);
        if (list == null || list.size() == 0) {
            ArrayList<registerInfo> newList = new ArrayList<>();
            newList.add(registerInfo);
            collect.localAndGobalMap.put(globalId, newList);
        } else
            list.add(registerInfo);

    }

        //通知所有用户登出

    public void loginOutAll(ArrayList<registerInfo> localInfo) throws Exception {
        log.info("通知其他系统退出");
        if (localInfo.isEmpty() == true || localInfo.size() == 0)
                throw new Exception("没有其他系统存在");
        localInfo.stream().forEach(
                    e -> {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("localSessionId", e.getLocalSessionId());
                        connection.doPost(e.getLoginOutUrl(), map, "UTF-8");
                    }
            );
        }

}