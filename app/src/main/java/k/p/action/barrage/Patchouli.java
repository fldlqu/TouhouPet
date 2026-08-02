package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Patchouli extends BaseEnemy {
    public Patchouli() {
        this.name = "帕秋莉 诺蕾姬";
        this.strength = 79;
        this.speed = 157;
        this.magic = 267;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 30 , P点 x 40";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"咳...咳.!\"\r\n\r\n需要等级 : 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 20;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(40);
        pet.changePoint(30);
        if (pet.getPetSetting("WinPatchouli") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinPatchouli", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        int r = new Random().nextInt(100);
        if (r < 20) {
            int restoreHP = (getMaxHP() - getCurrentHP()) / 3;
            sendMessage("水符「水精公主」");
            sendMessage(String.valueOf(getName()) + " 回复了 " + restoreHP + " 点生命");
            setCurrentHP(getCurrentHP() + restoreHP);
            return;
        }
        if (r < 40) {
            int damage = r * 20;
            damageTarget(damage);
            sendMessage("火符「火神闪光」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成" + damage + "点伤害");
            return;
        }
        if (r < 60) {
            target.setMagic(target.getMagic() - 50);
            sendMessage("金符「金属疲劳」");
            sendMessage(String.valueOf(target.getName()) + "的灵力下降了50点");
        }
    }
}
