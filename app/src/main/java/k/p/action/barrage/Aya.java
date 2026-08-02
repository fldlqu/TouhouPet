package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Aya extends BaseEnemy {
    public Aya() {
        this.name = "射命丸 文";
        this.strength = 285;
        this.speed = 775;
        this.magic = 274;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 80 , P点 x 80";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"来吧,我会放水的.认真的打过来吧!\"\r\n\r\n需要等级 : 30";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 30;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(80);
        pet.changePoint(80);
        if (pet.getPetSetting("WinAya") == null) {
            pet.setAchievement(pet.getAchievement() + 2);
            pet.setPetSetting("WinAya", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        Random random = new Random();
        if (random.nextInt(100) < 40) {
            sendMessage("「无双风神」");
            int k2 = (int) (((double) getSpeed()) * 0.3d);
            setSpeed(getSpeed() + k2);
            sendMessage(String.valueOf(getName()) + "的速度提升了" + k2 + "点");
        }
        if (random.nextInt(100) < 30) {
            sendMessage("「幻想风靡」");
            int damage = getSpeed();
            damageTarget(damage);
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + ",造成" + damage + "点伤害");
        }
    }
}
