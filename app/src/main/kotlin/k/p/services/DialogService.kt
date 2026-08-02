package k.p.services

import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import k.p.main.MainService
import k.p.main.R
import k.p.modern.Tasks

class DialogService private constructor() {
    private var mainService: MainService? = null
    private var windowManager: WindowManager? = null

    fun interface CallBack {
        fun onReturn(retVal: Boolean)
    }

    companion object {
        @JvmField
        var currentDialogView: View? = null

        private var instance = DialogService()

        @JvmStatic
        fun init(mainService2: MainService) {
            instance = DialogService()
            instance.mainService = mainService2
            instance.windowManager = mainService2.applicationContext.getSystemService(WindowManager::class.java)
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
            val params = WindowManager.LayoutParams()
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            params.flags = 520
            params.gravity = 17
            params.width = -2
            params.height = -2
            currentDialogView = View.inflate(instance.mainService, R.layout.confirm, null)
            (currentDialogView!!.findViewById(R.id.confirm_title) as TextView).text = title
            (currentDialogView!!.findViewById(R.id.confirm_message) as TextView).text = message
            (currentDialogView!!.findViewById(R.id.confirm_sure_button) as TextView).text = sureStr
            (currentDialogView!!.findViewById(R.id.confirm_cancel_button) as TextView).text = cancelStr
            currentDialogView!!.findViewById<View>(R.id.confirm_sure_button).setOnClickListener {
                instance.windowManager!!.removeView(currentDialogView)
                currentDialogView = null
                if (callback != null) {
                    callback.onReturn(true)
                }
            }
            currentDialogView!!.findViewById<View>(R.id.confirm_cancel_button).setOnClickListener {
                instance.windowManager!!.removeView(currentDialogView)
                currentDialogView = null
                if (callback != null) {
                    callback.onReturn(false)
                }
            }
            currentDialogView!!.layoutParams = params
            instance.mainService!!.requestNewDialog()
            val capturedView = currentDialogView
            Tasks.after(15000L) {
                if (capturedView === currentDialogView) {
                    clearDialog()
                }
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
            val params = WindowManager.LayoutParams()
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            params.flags = 520
            params.gravity = 17
            params.width = -2
            params.height = -2
            currentDialogView = View.inflate(instance.mainService, R.layout.alert, null)
            (currentDialogView!!.findViewById(R.id.alert_title) as TextView).text = title
            (currentDialogView!!.findViewById(R.id.alert_message) as TextView).text = message
            (currentDialogView!!.findViewById(R.id.alert_button) as TextView).text = sureStr
            currentDialogView!!.findViewById<View>(R.id.alert_button).setOnClickListener {
                instance.windowManager!!.removeView(currentDialogView)
                currentDialogView = null
                if (callback != null) {
                    callback.onReturn(true)
                }
            }
            currentDialogView!!.layoutParams = params
            instance.mainService!!.requestNewDialog()
            if (autoHide) {
                val capturedView = currentDialogView
                Tasks.after(15000L) {
                    if (capturedView === currentDialogView) {
                        clearDialog()
                    }
                }
            }
        }

        @JvmStatic
        fun changeNameDialog() {
            clearDialog()
            val params = WindowManager.LayoutParams()
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            params.flags = 512
            params.gravity = 17
            params.width = -2
            params.height = -2
            currentDialogView = View.inflate(instance.mainService, R.layout.changename, null)
            val editText = currentDialogView!!.findViewById(R.id.changename_edittext) as EditText
            editText.setText(PetService.pet!!.getName())
            editText.setSelection(editText.text.length)
            currentDialogView!!.findViewById<View>(R.id.changename_sure_button).setOnClickListener {
                instance.windowManager!!.removeView(currentDialogView)
                currentDialogView = null
                PetService.pet!!.setName(editText.text.toString())
            }
            currentDialogView!!.findViewById<View>(R.id.changename_cancel_button).setOnClickListener {
                instance.windowManager!!.removeView(currentDialogView)
                currentDialogView = null
            }
            currentDialogView!!.layoutParams = params
            instance.mainService!!.requestNewDialog()
        }

        @JvmStatic
        fun clearDialog() {
            if (currentDialogView != null) {
                try {
                    instance.windowManager!!.removeView(currentDialogView)
                } catch (e: Exception) {
                }
                currentDialogView = null
            }
        }

        @JvmStatic
        fun release() {
            currentDialogView = null
            instance = DialogService()
        }
    }
}