package com.yexc.common;

import com.yexc.controller.SsoServerController;
import com.yexc.controller.ssoCilentController;
import com.yexc.model.registerInfo;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/21.
 */
public class TimeLimit implements Runnable{
    Logger log= LoggerFactory.getLogger(TimeLimit.class);

    private String TGTId;
    private String globalId;
    public TimeLimit(String TGTId, String  globalId){
        this.TGTId=TGTId;
        this.globalId=globalId;
    }
    @Override
    public void run() {
        //TGT 1分钟后失效
        try {
            log.info("TGT开始计时");
            log.info("globalId--"+globalId);
            Thread.sleep(1000*90);
            collect.TGTMap.remove(TGTId);
            //通知客户端登出
            ArrayList<registerInfo> localInfo=collect.localAndGobalMap.get(globalId);
            if(localInfo !=null && localInfo.size()>0)
                new SsoServerController().loginOutAll(localInfo);
            log.info("TGT失效");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
