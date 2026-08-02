package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Mokou extends BaseEnemy {
    public Mokou() {
        this.name = "藤原 妹红";
        this.strength = 455;
        this.speed = 435;
        this.magic = 442;
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
        return "\"无聊啊无聊啊...\"\r\n\r\n需要等级 : 40";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 40;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(80);
        pet.changePoint(80);
        if (pet.getPetSetting("WinMokou") == null) {
            pet.setAchievement(pet.getAchievement() + 2);
            pet.setPetSetting("WinMokou", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onDamaged(Shoujo src, int damage) {
        if (getCurrentHP() <= 0 && new Random().nextInt(100) < 70) {
            sendMessage("「不死鸟重生」");
            setCurrentHP(getMaxHP());
        }
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        Random random = new Random();
        if (random.nextInt(100) < 30) {
            int damage = random.nextInt(BasePet.MAX_LEVEL) + 1;
            damageTarget(damage);
            sendMessage("「不朽的弹幕」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成" + damage + "点伤害");
        }
    }
}
