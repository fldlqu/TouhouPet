package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Sakuya extends BaseEnemy {
    public Sakuya() {
        this.name = "十六夜 咲夜";
        this.strength = 76;
        this.speed = 105;
        this.magic = 85;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 30 , P点 x 30";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"经常来偷红茶和蛋糕的小鬼!\"\r\n\r\n需要等级 : 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 20;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(30);
        pet.changePoint(30);
        if (pet.getPetSetting("WinSakuya") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinSakuya", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onCauseDamage(Shoujo target, int damage) {
        setSpeed(getSpeed() + 10);
        target.setSpeed(target.getSpeed() - 10);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundEnd(Shoujo target, int roundCount) {
        int d = (getSpeed() - target.getSpeed()) / 20;
        int damage = getMagic() + new Random().nextInt(getSpeed());
        for (int i = 0; i < d; i++) {
            damageTarget(damage, false);
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + ",造成" + getMagic() + "点伤害");
        }
    }
}
