package k.p.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import java.io.File;
import k.p.domain.BasePet;
import k.p.domain.Satori;
import k.p.services.ItemService;
import k.p.services.ListenerService;
import k.p.services.LocationService;
import k.p.services.PetService;
import k.p.services.StateService;
import k.p.utils.EnvironmentUtil;
import k.p.utils.SaveLoadUtil;
import local.kcn.utils.LogUtil;

/* 现代化:悬浮窗权限引导(SYSTEM_ALERT_WINDOW 需用户到系统设置开启)、
 * 旧版 SD 卡数据自动迁移、Android 13+ 通知权限。宠物逻辑保持原版。 */
public class TouhouPet extends Activity {
    private static final int REQUEST_NOTIFICATION = 1;
    private MainView mainView;
    private boolean initialized = false;
    private boolean overlayDialogShown = false;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(1);
            getWindow().setFlags(1024, 1024);
            if (Build.VERSION.SDK_INT >= 33) {
                requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},
                        REQUEST_NOTIFICATION);
            }
            if (!Settings.canDrawOverlays(this)) {
                showOverlayPermissionDialog();
                return;
            }
            proceed();
        } catch (Exception ae) {
            LogUtil.log(ae);
        }
    }

    /* 从系统设置返回后复查悬浮窗权限 */
    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (!initialized && Settings.canDrawOverlays(this)) {
            initialized = true;
            proceed();
        } else if (!initialized && !overlayDialogShown && !Settings.canDrawOverlays(this)) {
            overlayDialogShown = true;
            showOverlayPermissionDialog();
        }
    }

    private void showOverlayPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("需要悬浮窗权限");
            builder.setMessage("TouhouPet 需要\"显示在其他应用上层\"权限才能把宠物悬浮在桌面上。\n\n"
                    + "点击\"去授权\"后将跳转到系统设置,开启后返回本应用即可。");
            builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() { // from class: k.p.main.TouhouPet.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + TouhouPet.this.getPackageName()));
                        TouhouPet.this.startActivity(intent);
                    } catch (Exception e) {
                        LogUtil.log(e);
                    }
                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() { // from class: k.p.main.TouhouPet.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    TouhouPet.this.finish();
                }
            });
            builder.show();
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

    private void proceed() {
        try {
            EnvironmentUtil.migrateFromLegacy(this);
            File mainFilePath = new File(EnvironmentUtil.getMainPath());
            if (!mainFilePath.exists()) {
                try {
                    LogUtil.log("not found");
                    LogUtil.log(mainFilePath.getAbsolutePath());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("没有找到 TouhouPet 数据目录:\n"
                            + mainFilePath.getAbsolutePath()
                            + "\n\n请将原版 TouhouPet 文件夹(含 pet/、system/ 等子目录)放入该位置"
                            + "后重新打开应用。");
                    builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() { // from class: k.p.main.TouhouPet.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            TouhouPet.this.finish();
                        }
                    });
                    builder.show();
                    return;
                } catch (Exception e) {
                    LogUtil.log(e);
                    return;
                }
            }
            LogUtil.log("exist");
            try {
                ListenerService.init();
                LocationService.init();
                ItemService.init();
                StateService.init();
            } catch (Exception e2) {
                LogUtil.log(e2);
            }
            load();
            if (PetService.pet == null) {
                LogUtil.log("load fail");
                try {
                    PetService.pet = new Satori();
                    this.mainView = new MainView(this, null);
                    setContentView(this.mainView);
                    return;
                } catch (Exception e3) {
                    LogUtil.log(e3);
                    return;
                }
            }
            LogUtil.log("load success");
            PetService.pet.getCurrentState().onResume();
            start();
            return;
        } catch (Exception ae) {
            LogUtil.log(ae);
        }
    }

    public void start() {
        try {
            Intent i = new Intent(this, (Class<?>) MainService.class);
            startService(i);
            if (this.mainView != null) {
                this.mainView.requestStop();
            }
            finish();
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

    public void exit() {
        if (this.mainView != null) {
            this.mainView.requestStop();
        }
        finish();
    }

    private void load() {
        try {
            PetService.pet = (BasePet) SaveLoadUtil.load(BasePet.class, "pet.thp");
        } catch (Exception e) {
            Log.e("LOG", "load pet fail");
            LogUtil.log(e, false);
        }
        if (PetService.pet != null) {
            PetService.pet.onLoad();
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.mainView != null && this.mainView.getStage() >= 6) {
            exit();
        }
    }
}
