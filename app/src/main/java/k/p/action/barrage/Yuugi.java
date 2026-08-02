package k.p.action.barrage;

import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Yuugi extends BaseEnemy {
    public Yuugi() {
        this.name = "星熊 勇仪";
        this.strength = 60;
        this.speed = 22;
        this.magic = 46;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 20 , P点 x 40";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"是来喝酒的吗?\"\r\n\r\n需要等级 : 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 20;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(40);
        pet.changePoint(20);
        if (pet.getPetSetting("WinYuugi") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinYuugi", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        if (roundCount == 3) {
            damageTarget(333);
            sendMessage("四天王奥义「三步必杀」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成333点伤害");
        }
    }
}
