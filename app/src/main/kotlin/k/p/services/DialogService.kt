package k.p.services

import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.WindowManager
import android.widget.EditText
import k.p.main.MainService

/** 全局对话框:原生 AlertDialog + 悬浮窗类型。
 *  现代化:原版自绘 layout(alert/confirm/changename.xml)换为系统对话框,
 *  跟随系统深浅色主题, 长文本自动换行; 线程切换(可能被后台线程调用)留在内部主线程 Handler。 */
class DialogService private constructor() {
    private var mainService: MainService? = null

    fun interface CallBack {
        fun onReturn(retVal: Boolean)
    }

    companion object {
        @JvmField
        var currentDialog: AlertDialog? = null

        private var instance = DialogService()
        private val mainHandler = Handler(Looper.getMainLooper())

        @JvmStatic
        fun init(mainService2: MainService) {
            instance = DialogService()
            instance.mainService = mainService2
        }

        @JvmStatic
        fun confirm(title: String, message: String) {
            confirm(title, message, null)
        }

        @JvmStatic
        fun confirm(title: String, message: String, callback: CallBack?) {
            confirm(title, message, "确定", "取消", callback)
        }

        @JvmStatic
        fun confirm(title: String, message: String, sureStr: String, cancelStr: String, callback: CallBack?) {
            clearDialog()
            mainHandler.post {
                val dialog = AlertDialog.Builder(instance.mainService!!)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(sureStr) { _, _ ->
                        dismissCurrent()
                        callback?.onReturn(true)
                    }
                    .setNegativeButton(cancelStr) { _, _ ->
                        dismissCurrent()
                        callback?.onReturn(false)
                    }
                    .create()
                showAndAutoHide(dialog, true)
            }
        }

        @JvmStatic
        fun alert(title: String, message: String) {
            alert(title, message, true)
        }

        @JvmStatic
        fun alert(title: String, message: String, callback: CallBack?) {
            alert(title, message, callback, true)
        }

        @JvmStatic
        fun alert(title: String, message: String, sureStr: String, callback: CallBack?) {
            alert(title, message, sureStr, callback, true)
        }

        @JvmStatic
        fun alert(title: String, message: String, autoHide: Boolean) {
            alert(title, message, null, autoHide)
        }

        @JvmStatic
        fun alert(title: String, message: String, callback: CallBack?, autoHide: Boolean) {
            alert(title, message, "确定", callback, autoHide)
        }

        @JvmStatic
        fun alert(title: String, message: String, sureStr: String, callback: CallBack?, autoHide: Boolean) {
            clearDialog()
            mainHandler.post {
                val dialog = AlertDialog.Builder(instance.mainService!!)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(sureStr) { _, _ ->
                        dismissCurrent()
                        callback?.onReturn(true)
                    }
                    .create()
                showAndAutoHide(dialog, autoHide)
            }
        }

        @JvmStatic
        fun changeNameDialog() {
            clearDialog()
            mainHandler.post {
                val editText = EditText(instance.mainService!!).apply {
                    setText(PetService.pet!!.getName())
                    setSelection(text.length)
                    isSingleLine = true
                    filters = arrayOf(InputFilter.LengthFilter(20))
                }
                val dialog = AlertDialog.Builder(instance.mainService!!)
                    .setTitle("修改名字")
                    .setView(editText)
                    .setPositiveButton("确定") { _, _ ->
                        PetService.pet!!.setName(editText.text.toString())
                        dismissCurrent()
                    }
                    .setNegativeButton("取消") { _, _ ->
                        dismissCurrent()
                    }
                    .create()
                showAndAutoHide(dialog, false)
            }
        }

        /** 悬浮窗类型(Service 无 Activity 窗口, 必需) + 禁止 back/外点关闭(与原自绘窗口一致) */
        private fun showAndAutoHide(dialog: AlertDialog, autoHide: Boolean) {
            dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            currentDialog = dialog
            if (autoHide) {
                val captured = dialog
                mainHandler.postDelayed({
                    if (currentDialog === captured) {
                        dismissCurrent()
                    }
                }, 15000L)
            }
        }

        private fun dismissCurrent() {
            currentDialog?.dismiss()
            currentDialog = null
        }

        @JvmStatic
        fun clearDialog() {
            /* 可能在非主线程被调用(如 onHide), dismiss 要求主线程 */
            if (Looper.myLooper() == Looper.getMainLooper()) {
                dismissCurrent()
            } else {
                mainHandler.post { dismissCurrent() }
            }
        }

        @JvmStatic
        fun release() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                dismissCurrent()
            } else {
                mainHandler.post { dismissCurrent() }
            }
            instance = DialogService()
        }
    }
}