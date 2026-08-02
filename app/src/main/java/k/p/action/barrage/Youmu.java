package k.p.action.barrage;

import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Youmu extends BaseEnemy {
    public Youmu() {
        this.name = "魂魄 妖梦";
        this.strength = 37;
        this.speed = 87;
        this.magic = 34;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 40 , P点 x 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"幽幽子大人的食物,由我来守护!\"\r\n\r\n需要等级 : 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 20;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(20);
        pet.changePoint(40);
        if (pet.getPetSetting("WinYoumu") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinYoumu", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onCauseDamage(Shoujo target, int damage) {
        damageTarget(damage, false);
        sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + ",造成" + damage + "点伤害");
    }
}
