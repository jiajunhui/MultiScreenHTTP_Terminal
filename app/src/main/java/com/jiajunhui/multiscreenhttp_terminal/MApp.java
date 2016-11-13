package com.jiajunhui.multiscreenhttp_terminal;

import com.xapp.jjh.xui.application.XUIApplication;
import com.xapp.jjh.xui.config.XUIConfig;

/**
 * Created by Taurus on 16/11/13.
 */

public class MApp extends XUIApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        XUIConfig.setXUIRedStyle();
    }
}
