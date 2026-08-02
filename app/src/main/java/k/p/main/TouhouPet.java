package k.p.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

/* 数据目录 = 公共 SD 卡 /sdcard/TouhouPet(与原版一致)。
 * 权限引导:Android 11+ 需"所有文件访问";Android 10 及以下需 WRITE_EXTERNAL_STORAGE。 */
public class TouhouPet extends Activity {
    private static final int REQUEST_NOTIFICATION = 1;
    private static final int REQUEST_STORAGE = 2;
    private MainView mainView;
    private boolean initialized = false;
    private boolean overlayDialogShown = false;
    private boolean storageDialogShown = false;

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
                overlayDialogShown = true; /* 避免 onCreate 后紧随的 onResume 重复弹窗 */
                showOverlayPermissionDialog();
                return;
            }
            proceed();
        } catch (Exception ae) {
            LogUtil.log(ae);
        }
    }

    /* 从系统设置/权限页返回后复查 */
    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.initialized) {
            return;
        }
        if (Settings.canDrawOverlays(this) && hasStoragePermission()) {
            this.initialized = true;
            proceed();
        } else if (!Settings.canDrawOverlays(this) && !this.overlayDialogShown) {
            this.overlayDialogShown = true;
            showOverlayPermissionDialog();
        } else if (Settings.canDrawOverlays(this) && !hasStoragePermission()
                && !this.storageDialogShown) {
            this.storageDialogShown = true;
            showStoragePermissionDialog();
        }
    }

    /* Android 11+(API 30):"所有文件访问";API 26-29:WRITE_EXTERNAL_STORAGE 运行时权限 */
    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= 30) {
            return Environment.isExternalStorageManager();
        }
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void showStoragePermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("需要存储权限");
            builder.setMessage("TouhouPet 数据(存档/动画/音乐)存放在 SD 卡根目录 TouhouPet 文件夹。\n\n"
                    + "点击\\\"去授权\\\"后在系统设置中允许\\\"所有文件访问\\\",返回后继续。");
            builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Build.VERSION.SDK_INT >= 30) {
                        try {
                            Intent intent = new Intent(
                                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                                    Uri.parse("package:" + TouhouPet.this.getPackageName()));
                            TouhouPet.this.startActivity(intent);
                        } catch (Exception e) {
                            LogUtil.log(e);
                        }
                    } else {
                        requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_STORAGE);
                    }
                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TouhouPet.this.finish();
                }
            });
            builder.show();
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

    private void showOverlayPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("需要悬浮窗权限");
            builder.setMessage("TouhouPet 需要\\\"显示在其他应用上层\\\"权限才能把宠物悬浮在桌面上。\n\n"
                    + "点击\\\"去授权\\\"后将跳转到系统设置,开启后返回本应用即可。");
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
