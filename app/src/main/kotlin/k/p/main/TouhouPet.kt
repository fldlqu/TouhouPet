package k.p.main

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.Window
import k.p.domain.BasePet
import k.p.domain.Satori
import k.p.services.ItemService
import k.p.services.ListenerService
import k.p.services.LocationService
import k.p.services.PetService
import k.p.services.StateService
import k.p.utils.EnvironmentUtil
import k.p.utils.SaveLoadUtil
import local.kcn.utils.LogUtil
import java.io.File

/* 数据目录 = 公共 SD 卡 /sdcard/TouhouPet(与原版一致)。
 * 权限引导:Android 11+ 需"所有文件访问";Android 10 及以下需 WRITE_EXTERNAL_STORAGE。 */
class TouhouPet : Activity() {
    private var mainView: MainView? = null
    private var initialized = false
    private var overlayDialogShown = false
    private var storageDialogShown = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(1024, 1024)
            if (Build.VERSION.SDK_INT >= 33) {
                requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), REQUEST_NOTIFICATION)
            }
            if (!Settings.canDrawOverlays(this)) {
                overlayDialogShown = true /* 避免 onCreate 后紧随的 onResume 重复弹窗 */
                showOverlayPermissionDialog()
                return
            }
            proceed()
        } catch (ae: Exception) {
            LogUtil.log(ae)
        }
    }

    /* 从系统设置/权限页返回后复查 */
    public override fun onResume() {
        super.onResume()
        if (initialized) {
            return
        }
        /* 已引导过一次(Oppo/部分系统上已授权但 API 仍返回 false, 否则每次启动都弹) */
        if (permissionGuideDone()) {
            initialized = true
            proceed()
            return
        }
        if (Settings.canDrawOverlays(this) && hasStoragePermission()) {
            initialized = true
            markGuideDone()
            proceed()
        } else if (!Settings.canDrawOverlays(this) && !overlayDialogShown) {
            overlayDialogShown = true
            showOverlayPermissionDialog()
        } else if (Settings.canDrawOverlays(this) && !hasStoragePermission() && !storageDialogShown) {
            storageDialogShown = true
            showStoragePermissionDialog()
        }
    }

    /* 引导只做一次:点过"去授权"或成功进入后不再检查 */
    private fun permissionGuideDone(): Boolean {
        return getSharedPreferences("thp_prefs", MODE_PRIVATE)
            .getBoolean("permission_guide_done", false)
    }

    private fun markGuideDone() {
        getSharedPreferences("thp_prefs", MODE_PRIVATE)
            .edit().putBoolean("permission_guide_done", true).apply()
    }

    /* Android 11+(API 30):"所有文件访问";API 26-29:WRITE_EXTERNAL_STORAGE 运行时权限 */
    private fun hasStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= 30) {
            return Environment.isExternalStorageManager()
        }
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun showStoragePermissionDialog() {
        try {
            AlertDialog.Builder(this)
                .setTitle("需要存储权限")
                .setMessage(
                    "TouhouPet 数据(存档/动画/音乐)存放在 SD 卡根目录 TouhouPet 文件夹。\n\n" +
                        "点击\\\"去授权\\\"后在系统设置中允许\\\"所有文件访问\\\",返回后继续。"
                )
                .setPositiveButton("去授权") { _, _ ->
                    markGuideDone() /* 点过即不再弹, 避免系统误报反复引导 */
                    if (Build.VERSION.SDK_INT >= 30) {
                        try {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                Uri.parse("package:" + packageName)
                            )
                            startActivity(intent)
                        } catch (e: Exception) {
                            LogUtil.log(e)
                        }
                    } else {
                        requestPermissions(
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_STORAGE
                        )
                    }
                }
                .setNegativeButton("退出") { _, _ ->
                    finish()
                }
                .show()
        } catch (e: Exception) {
            LogUtil.log(e)
        }
    }

    private fun showOverlayPermissionDialog() {
        try {
            AlertDialog.Builder(this)
                .setTitle("需要悬浮窗权限")
                .setMessage(
                    "TouhouPet 需要\\\"显示在其他应用上层\\\"权限才能把宠物悬浮在桌面上。\n\n" +
                        "点击\\\"去授权\\\"后将跳转到系统设置,开启后返回本应用即可。"
                )
                .setPositiveButton("去授权") { _, _ ->
                    markGuideDone() /* 点过即不再弹, 避免系统误报反复引导 */
                    try {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + packageName)
                        )
                        startActivity(intent)
                    } catch (e: Exception) {
                        LogUtil.log(e)
                    }
                }
                .setNegativeButton("退出") { _, _ ->
                    finish()
                }
                .show()
        } catch (e: Exception) {
            LogUtil.log(e)
        }
    }

    private fun proceed() {
        try {
            val mainFilePath = File(EnvironmentUtil.getMainPath())
            if (!mainFilePath.exists()) {
                try {
                    LogUtil.log("not found")
                    LogUtil.log(mainFilePath.absolutePath)
                    AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage(
                            "没有找到 TouhouPet 数据目录:\n" +
                                mainFilePath.absolutePath +
                                "\n\n请将原版 TouhouPet 文件夹(含 pet/、system/ 等子目录)放入该位置" +
                                "后重新打开应用。"
                        )
                        .setPositiveButton("关闭") { _, _ ->
                            finish()
                        }
                        .show()
                    return
                } catch (e: Exception) {
                    LogUtil.log(e)
                    return
                }
            }
            LogUtil.log("exist")
            try {
                ListenerService.init()
                LocationService.init()
                ItemService.init()
                StateService.init()
            } catch (e: Exception) {
                LogUtil.log(e)
            }
            load()
            if (PetService.pet == null) {
                LogUtil.log("load fail")
                try {
                    PetService.pet = Satori()
                    mainView = MainView(this, null)
                    setContentView(mainView)
                    return
                } catch (e: Exception) {
                    LogUtil.log(e)
                    return
                }
            }
            LogUtil.log("load success")
            PetService.pet!!.getCurrentState().onResume()
            start()
            return
        } catch (ae: Exception) {
            LogUtil.log(ae)
        }
    }

    fun start() {
        try {
            val i = Intent(this, MainService::class.java)
            startService(i)
            if (mainView != null) {
                mainView!!.requestStop()
            }
            finish()
        } catch (e: Exception) {
            LogUtil.log(e)
        }
    }

    fun exit() {
        if (mainView != null) {
            mainView!!.requestStop()
        }
        finish()
    }

    private fun load() {
        try {
            PetService.pet = SaveLoadUtil.load(BasePet::class.java, "pet.thp") as BasePet?
        } catch (e: Exception) {
            Log.e("LOG", "load pet fail")
            LogUtil.log(e, false)
        }
        if (PetService.pet != null) {
            PetService.pet?.onLoad()
        }
    }

    public override fun onBackPressed() {
        if (mainView!!.getStage() >= 6) {
            exit()
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATION = 1
        private const val REQUEST_STORAGE = 2
    }
}