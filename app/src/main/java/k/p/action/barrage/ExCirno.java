package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class ExCirno extends BaseEnemy {
    public ExCirno() {
        this.name = "Ex琪露诺";
        this.strength = 99;
        this.speed = 99;
        this.magic = 99;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得经验99点";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"干得不错嘛!\"";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return true;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        Integer count;
        super.onWin(pet);
        pet.addExp(99);
        if (pet.getPetSetting("BeatEXCirno") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            count = 0;
        } else {
            count = (Integer) pet.getPetSetting("BeatEXCirno");
        }
        pet.setPetSetting("BeatEXCirno", Integer.valueOf(count.intValue() + 1));
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
        Random random = new Random();
        int decreaseSpeed = (target.getSpeed() / 99) + 9;
        if (decreaseSpeed > 0) {
            target.setSpeed(target.getSpeed() - decreaseSpeed);
            sendMessage(String.valueOf(target.getName()) + "受到寒气影响,速度降低了" + decreaseSpeed + "点");
        }
        if (target.getSpeed() < 99) {
            damageTarget(99);
            sendMessage("「冰华⑨咲」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成99点伤害");
        }
        int r = random.nextInt(100);
        if (r < 30) {
            target.setSpeed(target.getSpeed() - 99);
            sendMessage("冻符「负K」");
            sendMessage(String.valueOf(target.getName()) + "的速度降低了99点");
        }
    }
}
