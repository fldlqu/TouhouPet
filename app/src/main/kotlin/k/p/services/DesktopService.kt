package k.p.services

import android.content.Context
import android.view.WindowManager
import k.p.view.BaseDesktopView
import k.p.modern.Diag
import local.kcn.utils.LogUtil
import java.util.ArrayList

class DesktopService private constructor() {
    private var allViewList: MutableList<BaseDesktopView> = ArrayList()
    private var viewList: MutableList<BaseDesktopView> = ArrayList()
    private var windowManager: WindowManager? = null

    companion object {
        private const val TAG = "DesktopService"
        private var instance: DesktopService? = null

        @JvmStatic
        fun init(context: Context) {
            instance = DesktopService()
            instance!!.windowManager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            instance!!.viewList = ArrayList()
            instance!!.allViewList = ArrayList()
        }

        @JvmStatic
        fun addDesktopView(view: BaseDesktopView, params: WindowManager.LayoutParams) {
            val svc = instance
            if (svc == null) {
                Diag.log("addDesktopView SKIP: instance null")
                return
            }
            if (!svc.viewList.contains(view)) {
                try {
                    svc.windowManager!!.addView(view, params)
                    Diag.log("addView OK: " + view.javaClass.simpleName + " w=" + params.width + " h=" + params.height + " x=" + params.x + " y=" + params.y)
                } catch (e: Exception) {
                    Diag.log("addView THROW: " + view.javaClass.simpleName + " -> " + e)
                }
                svc.viewList.add(view)
                view.startDraw()
            }
            if (!svc.allViewList.contains(view)) {
                svc.allViewList.add(view)
            }
        }

        @JvmStatic
        fun removeDesktopView(view: BaseDesktopView) {
            removeDesktopView(view, false)
        }

        @JvmStatic
        fun removeDesktopView(view: BaseDesktopView, destroy: Boolean) {
            val svc = instance ?: return
            if (svc.viewList.contains(view)) {
                try {
                    if (destroy) {
                        view.requestStop()
                    } else {
                        view.requestPause()
                    }
                    svc.windowManager!!.removeView(view)
                    svc.viewList.remove(view)
                } catch (e: Exception) {
                    LogUtil.log(TAG, "removeDesktopView Fail")
                }
            }
        }

        @JvmStatic
        fun clear() {
            val svc = instance
            if (svc == null || svc.allViewList.isEmpty()) {
                return /* 幂等:release 后再次调用不崩溃 */
            }
            for (view in svc.allViewList) {
                try {
                    view.requestStop()
                    svc.windowManager!!.removeView(view)
                } catch (e: Exception) {
                    LogUtil.log(TAG, "removeDesktopView Fail")
                    e.printStackTrace()
                }
            }
            svc.viewList.clear()
        }

        @JvmStatic
        fun refreshDesktopView(view: BaseDesktopView) {
            try {
                instance!!.windowManager!!.updateViewLayout(view, view.getParams())
            } catch (e: Exception) {
            }
        }

        @JvmStatic
        fun judgeViewShow(view: BaseDesktopView): Boolean {
            return instance!!.viewList.contains(view)
        }

        @JvmStatic
        fun release() {
            clear()
            instance = null
        }
    }
}