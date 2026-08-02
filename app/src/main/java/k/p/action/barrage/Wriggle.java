package k.p.action.barrage;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Wriggle extends BaseEnemy {
    public Wriggle() {
        this.name = "莉格露 奈特巴格";
        this.strength = 13;
        this.speed = 15;
        this.magic = 12;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 10 , P点 x 10";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"~!@#$%^&*\"";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return true;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(10);
        pet.changePoint(10);
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        if (roundCount == 1) {
            damageTarget(77);
            sendMessage("虫子飞起一脚");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成77点伤害");
        }
    }
}
