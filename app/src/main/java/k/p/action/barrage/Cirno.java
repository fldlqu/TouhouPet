package k.p.action.barrage;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Cirno extends BaseEnemy {
    public Cirno() {
        this.name = "琪露诺";
        this.strength = 9;
        this.speed = 9;
        this.magic = 9;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得经验9点";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"俺最强!\"";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return true;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        Integer count;
        super.onWin(pet);
        pet.addExp(9);
        if (pet.getPetSetting("BeatCirno") == null) {
            count = 0;
        } else {
            count = (Integer) pet.getPetSetting("BeatCirno");
        }
        pet.setPetSetting("BeatCirno", Integer.valueOf(count.intValue() + 1));
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }
}
