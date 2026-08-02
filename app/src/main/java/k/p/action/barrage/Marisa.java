package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;
import k.p.item.food.Mushroom;
import k.p.services.ItemService;

/* JADX INFO: loaded from: classes.dex */
public class Marisa extends BaseEnemy {
    public Marisa() {
        this.name = "魔理沙";
        this.strength = 24;
        this.speed = 47;
        this.magic = 68;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 20 , P点 x 20 , 蘑菇 x 2";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "怎么有这么多台词要写ZE";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "怎么有这么多台词要写ZE\r\n\r\n需要等级 : 20";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 20;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(20);
        pet.changePoint(20);
        ItemService.addItem(new Mushroom());
        ItemService.addItem(new Mushroom());
        if (pet.getPetSetting("WinMarisa") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinMarisa", new Flag());
        }
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onLose(BasePet pet) {
        super.onLose(pet);
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onCauseDamage(Shoujo target, int damage) {
        if (new Random().nextInt(100) < 20) {
            damageTarget(120, false);
            sendMessage("魔理沙发动魔炮追击,造成120点伤害");
        }
    }
}
