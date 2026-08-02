package k.p.services;

import android.content.Context;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.List;
import k.p.view.BaseDesktopView;
import local.kcn.utils.LogUtil;

/* JADX INFO: loaded from: classes.dex */
public class DesktopService {
    private static final String TAG = "DesktopService";
    private static List<BaseDesktopView> allViewList;
    private static List<BaseDesktopView> viewList;
    private static WindowManager windowManager;

    public static void init(Context context) {
        windowManager = (WindowManager) context.getApplicationContext().getSystemService("window");
        viewList = new ArrayList();
        allViewList = new ArrayList();
    }

    public static void addDesktopView(BaseDesktopView view, WindowManager.LayoutParams params) {
        if (!viewList.contains(view)) {
            windowManager.addView(view, params);
            viewList.add(view);
            view.startDraw();
        }
        if (!allViewList.contains(view)) {
            allViewList.add(view);
        }
    }

    public static void removeDesktopView(BaseDesktopView view) {
        removeDesktopView(view, false);
    }

    public static void removeDesktopView(BaseDesktopView view, boolean destroy) {
        if (viewList.contains(view)) {
            try {
                if (destroy) {
                    view.requestStop();
                } else {
                    view.requestPause();
                }
                windowManager.removeView(view);
                viewList.remove(view);
            } catch (Exception e) {
                LogUtil.log(TAG, "removeDesktopView Fail");
            }
        }
    }

    public static void clear() {
        for (BaseDesktopView view : allViewList) {
            try {
                view.requestStop();
                windowManager.removeView(view);
            } catch (Exception e) {
                LogUtil.log(TAG, "removeDesktopView Fail");
                e.printStackTrace();
            }
        }
        viewList.clear();
    }

    public static void refreshDesktopView(BaseDesktopView view) {
        try {
            windowManager.updateViewLayout(view, view.getParams());
        } catch (Exception e) {
        }
    }

    public static boolean judgeViewShow(BaseDesktopView view) {
        return viewList.contains(view);
    }

    public static void release() {
        clear();
        windowManager = null;
        viewList = null;
        allViewList = null;
    }
}
