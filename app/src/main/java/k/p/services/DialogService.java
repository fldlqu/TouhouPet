package k.p.services;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import k.p.main.MainService;
import k.p.main.R;

/* JADX INFO: loaded from: classes.dex */
public class DialogService {
    public static View currentDialogView;
    private static MainService mainService;
    private static WindowManager windowManager;

    public interface CallBack {
        void onReturn(boolean z);
    }

    public static void init(MainService mainService2) {
        mainService = mainService2;
        windowManager = (WindowManager) mainService2.getApplicationContext().getSystemService("window");
    }

    public static void confirm(String title, String message) {
        confirm(title, message, null);
    }

    public static void confirm(String title, String message, CallBack callback) {
        confirm(title, message, "确定", "取消", callback);
    }

    /* JADX WARN: Type inference failed for: r1v23, types: [k.p.services.DialogService$3] */
    public static void confirm(String title, String message, String sureStr, String cancelStr, final CallBack callback) {
        clearDialog();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = 2003;
        params.flags = 520;
        params.gravity = 17;
        params.width = -2;
        params.height = -2;
        currentDialogView = View.inflate(mainService, R.layout.confirm, null);
        ((TextView) currentDialogView.findViewById(R.id.confirm_title)).setText(title);
        ((TextView) currentDialogView.findViewById(R.id.confirm_message)).setText(message);
        ((TextView) currentDialogView.findViewById(R.id.confirm_sure_button)).setText(sureStr);
        ((TextView) currentDialogView.findViewById(R.id.confirm_cancel_button)).setText(cancelStr);
        currentDialogView.findViewById(R.id.confirm_sure_button).setOnClickListener(new View.OnClickListener() { // from class: k.p.services.DialogService.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DialogService.windowManager.removeView(DialogService.currentDialogView);
                DialogService.currentDialogView = null;
                if (callback != null) {
                    callback.onReturn(true);
                }
            }
        });
        currentDialogView.findViewById(R.id.confirm_cancel_button).setOnClickListener(new View.OnClickListener() { // from class: k.p.services.DialogService.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DialogService.windowManager.removeView(DialogService.currentDialogView);
                DialogService.currentDialogView = null;
                if (callback != null) {
                    callback.onReturn(false);
                }
            }
        });
        currentDialogView.setLayoutParams(params);
        mainService.requestNewDialog();
        new Thread() { // from class: k.p.services.DialogService.3
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                View view = DialogService.currentDialogView;
                try {
                    Thread.sleep(15000L);
                } catch (InterruptedException e) {
                }
                if (view == DialogService.currentDialogView) {
                    DialogService.clearDialog();
                }
            }
        }.start();
    }

    public static void alert(String title, String message) {
        alert(title, message, true);
    }

    public static void alert(String title, String message, CallBack callback) {
        alert(title, message, callback, true);
    }

    public static void alert(String title, String message, String sureStr, CallBack callback) {
        alert(title, message, sureStr, callback, true);
    }

    public static void alert(String title, String message, boolean autoHide) {
        alert(title, message, (CallBack) null, autoHide);
    }

    public static void alert(String title, String message, CallBack callback, boolean autoHide) {
        alert(title, message, "确定", callback, autoHide);
    }

    /* JADX WARN: Type inference failed for: r1v18, types: [k.p.services.DialogService$5] */
    public static void alert(String title, String message, String sureStr, final CallBack callback, boolean autoHide) {
        clearDialog();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = 2003;
        params.flags = 520;
        params.gravity = 17;
        params.width = -2;
        params.height = -2;
        currentDialogView = View.inflate(mainService, R.layout.alert, null);
        ((TextView) currentDialogView.findViewById(R.id.alert_title)).setText(title);
        ((TextView) currentDialogView.findViewById(R.id.alert_message)).setText(message);
        ((TextView) currentDialogView.findViewById(R.id.alert_button)).setText(sureStr);
        currentDialogView.findViewById(R.id.alert_button).setOnClickListener(new View.OnClickListener() { // from class: k.p.services.DialogService.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DialogService.windowManager.removeView(DialogService.currentDialogView);
                DialogService.currentDialogView = null;
                if (callback != null) {
                    callback.onReturn(true);
                }
            }
        });
        currentDialogView.setLayoutParams(params);
        mainService.requestNewDialog();
        if (autoHide) {
            new Thread() { // from class: k.p.services.DialogService.5
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    View view = DialogService.currentDialogView;
                    try {
                        Thread.sleep(15000L);
                    } catch (InterruptedException e) {
                    }
                    if (view == DialogService.currentDialogView) {
                        DialogService.clearDialog();
                    }
                }
            }.start();
        }
    }

    public static void changeNameDialog() {
        clearDialog();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = 2003;
        params.flags = 512;
        params.gravity = 17;
        params.width = -2;
        params.height = -2;
        currentDialogView = View.inflate(mainService, R.layout.changename, null);
        final EditText editText = (EditText) currentDialogView.findViewById(R.id.changename_edittext);
        editText.setText(PetService.pet.getName());
        editText.setSelection(editText.getText().length());
        currentDialogView.findViewById(R.id.changename_sure_button).setOnClickListener(new View.OnClickListener() { // from class: k.p.services.DialogService.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DialogService.windowManager.removeView(DialogService.currentDialogView);
                DialogService.currentDialogView = null;
                PetService.pet.setName(editText.getText().toString());
            }
        });
        currentDialogView.findViewById(R.id.changename_cancel_button).setOnClickListener(new View.OnClickListener() { // from class: k.p.services.DialogService.7
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                DialogService.windowManager.removeView(DialogService.currentDialogView);
                DialogService.currentDialogView = null;
            }
        });
        currentDialogView.setLayoutParams(params);
        mainService.requestNewDialog();
    }

    public static void clearDialog() {
        if (currentDialogView != null) {
            try {
                windowManager.removeView(currentDialogView);
            } catch (Exception e) {
            }
            currentDialogView = null;
        }
    }

    public static void release() {
        windowManager = null;
        currentDialogView = null;
    }
}
