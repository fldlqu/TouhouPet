package k.p.action.barrage;

import k.p.domain.BasePet;
import k.p.services.DialogService;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseEnemy extends BaseShoujo {
    public abstract boolean canDone(BasePet basePet);

    public abstract String getLoseMessage();

    public abstract String getStartMessage();

    public abstract String getWinMessage();

    public void onWin(BasePet pet) {
        DialogService.alert("胜利", "成功击败了" + getName() + "\r\n\r\n" + getWinMessage());
    }

    public void onLose(BasePet pet) {
        DialogService.alert("失败", "满身苍夷...");
    }
}
