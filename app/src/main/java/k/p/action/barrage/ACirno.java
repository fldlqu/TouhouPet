package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class ACirno extends BaseEnemy {
    public ACirno() {
        this.name = "Advent琪露诺";
        this.strength = BasePet.MAX_LEVEL;
        this.speed = BasePet.MAX_LEVEL;
        this.magic = BasePet.MAX_LEVEL;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得经验999点";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"哦?又要来玩弹幕吗?\r\n这次可不会手下留情了\"";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return true;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.addExp(BasePet.MAX_LEVEL);
        if (pet.getPetSetting("WinACirno") == null) {
            pet.setAchievement(pet.getAchievement() + 5);
            pet.setPetSetting("WinACirno", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        Random random = new Random();
        int decreaseSpeed = (target.getSpeed() / 9) + 99;
        target.setSpeed(target.getSpeed() - decreaseSpeed);
        sendMessage(String.valueOf(target.getName()) + "受到寒气影响,速度降低了" + decreaseSpeed + "点");
        if (target.getSpeed() < 99) {
            damageTarget(BasePet.MAX_LEVEL);
            sendMessage("「冰华⑨咲」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成999点伤害");
        }
        if (random.nextInt(100) < 30) {
            target.setSpeed(target.getSpeed() - 999);
            sendMessage("冻符「负K」");
            sendMessage(String.valueOf(target.getName()) + "的速度降低了999点");
        }
    }
}
