package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Reisen extends BaseEnemy {
    public Reisen() {
        this.name = "铃仙 优昙华院 因幡";
        this.strength = 165;
        this.speed = 157;
        this.magic = 172;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 40 , P点 x 40";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"台词待补充(懒)\"\r\n\r\n需要等级 : 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 20;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(40);
        pet.changePoint(40);
        if (pet.getPetSetting("WinReisen") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinReisen", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        if (new Random().nextInt(100) < 50) {
            int d = (int) (((double) target.getMagic()) * 0.1d);
            target.setMagic(target.getMagic() - d);
            sendMessage("狂符「幻视调律」");
            sendMessage(String.valueOf(target.getName()) + "的灵力下降了" + d + "点");
        }
    }
}
