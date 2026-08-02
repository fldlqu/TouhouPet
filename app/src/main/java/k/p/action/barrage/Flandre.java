package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Flandre extends BaseEnemy {
    public Flandre() {
        this.name = "芙兰朵露 斯卡雷特";
        this.strength = 495;
        this.speed = 367;
        this.magic = 386;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 122 , P点 x 122";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"玩坏你哦!\"\r\n\r\n需要等级 : 40";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 40;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(122);
        pet.changePoint(122);
        if (pet.getPetSetting("WinFlandre") == null) {
            pet.setAchievement(pet.getAchievement() + 2);
            pet.setPetSetting("WinFlandre", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onCauseDamage(Shoujo target, int damage) {
        int maxHP = getMaxHP();
        int rHP = damage / 3;
        setCurrentHP(getCurrentHP() + rHP);
        if (getCurrentHP() > maxHP) {
            setCurrentHP(maxHP);
        }
        sendMessage(String.valueOf(getName()) + " 吸取生命值 " + rHP + " 点");
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        Random random = new Random();
        if (roundCount >= 20) {
            int damage = random.nextInt(5555) + 1;
            damageTarget(damage);
            sendMessage("秘弹「接下来谁都不剩了喔?」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成" + damage + "点伤害");
            return;
        }
        int r = random.nextInt(100);
        if (r < 20) {
            int damage2 = random.nextInt(1999) + 1;
            damageTarget(damage2);
            sendMessage("禁忌「莱瓦汀」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成" + damage2 + "点伤害");
            return;
        }
        if (r < 40) {
            int increaseSpeed = random.nextInt(500);
            sendMessage("禁忌「四重存在」");
            setSpeed(getSpeed() + increaseSpeed);
            sendMessage(String.valueOf(getName()) + " 的速度提升了 " + increaseSpeed + " 点");
        }
    }
}
