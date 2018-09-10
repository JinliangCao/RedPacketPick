package com.example.cao_j.redpaketautopick;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityEventSource;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class RedPacketPickService extends AccessibilityService {
    public RedPacketPickService() {
    }

    /**
     * 有关AccessibilityEvent事件的回调函数，
     * 系统通过sendAccessibiliyEvent()不断的发送AccessibilityEvent到此处
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();
        String className = accessibilityEvent.getClassName().toString();

        Log.d("caojinliang","拦截事件 eventType = " + eventType);
//        Log.d("caojinliang","拦截事件 nodeInfo = " + nodeInfo.toString());
        Log.d("caojinliang","拦截事件 className = " + className);
        switch (eventType){
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = accessibilityEvent.getText();
                if(!texts.isEmpty()){
                    for(CharSequence text : texts){
                        String content = text.toString();
                        if(content.contains("[微信红包]")){
                            dealWithWXNotification(accessibilityEvent);
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if(className.equals("com.tencent.mm.ui.LauncherUI")){
                    Log.d("caojinliang","点击红包");
                    getLastPacket();
                }else if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")){
                    Log.d("caojinliang","开红包");
                    inputClick("com.tencent.mm:id/bg7");
                }else if(className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")){
                    Log.d("caojinliang","退出红包");
                    inputClick("com.tencent.mm:id/gd");
                }
                break;
        }
    }

    private void dealWithWXNotification(AccessibilityEvent event) {
        if(event.getParcelableData() != null &&
                event.getParcelableData() instanceof Notification){
            Notification notification = (Notification)event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try{
                pendingIntent.send();
                Log.d("caojinliang", "打开微信");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void inputClick(String clickId){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo != null){
            List<AccessibilityNodeInfo> list =
                    nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for(AccessibilityNodeInfo item : list){
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private List<AccessibilityNodeInfo> mParents = new ArrayList<>();
    private void getLastPacket(){
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        mParents.clear();
        recycle(rootNode);
        if(mParents.size()>0){
            mParents.get(mParents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    //回归函数遍历每一个节点，并将“领取红包”存进list中
    public void recycle(AccessibilityNodeInfo info){
        if(info.getChildCount() == 0){
            if("领取红包".equals(info.getText().toString())){
                if(info.isClickable()){
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                AccessibilityNodeInfo parent = info.getParent();
                while(parent != null){
                    if(parent.isClickable()){
                        mParents.add(parent);
                        break;
                    }
                    parent = parent.getParent();
                }
            }else{
                for(int i = 0; i < info.getChildCount(); i++){
                    if(info.getChild(i) != null){
                        recycle(info.getChild(i));
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "抢红包服务开启",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "抢红包服务关闭",
                Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

}
