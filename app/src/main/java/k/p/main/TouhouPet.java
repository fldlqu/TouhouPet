package k.p.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

/* JADX INFO: loaded from: classes.dex */
public class TouhouPet extends Activity {
    private MainView mainView;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(1);
            getWindow().setFlags(1024, 1024);
            File mainFilePath = new File(EnvironmentUtil.getMainPath());
            if (!mainFilePath.exists()) {
                try {
                    LogUtil.log("not found");
                    LogUtil.log(mainFilePath.getAbsolutePath());
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("SD卡根目录下没有找到TouhouPet文件夹,请确认安装位置正确");
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
        if (this.mainView.getStage() >= 6) {
            exit();
        }
    }
}
