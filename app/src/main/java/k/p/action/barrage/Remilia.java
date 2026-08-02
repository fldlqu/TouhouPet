package k.p.action.barrage;

import java.util.Random;
import k.p.domain.BasePet;
import k.p.domain.Flag;

/* JADX INFO: loaded from: classes.dex */
public class Remilia extends BaseEnemy {
    public Remilia() {
        this.name = "蕾米莉亚 斯卡雷特";
        this.strength = 227;
        this.speed = 213;
        this.magic = 239;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getWinMessage() {
        return "获得:\r\n 点数 x 60 , P点 x 60";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getLoseMessage() {
        return "";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public String getStartMessage() {
        return "\"阿类,地灵殿之主?\"\r\n\r\n需要等级 : 30";
    }

    @Override // k.p.action.barrage.BaseEnemy
    public boolean canDone(BasePet pet) {
        return pet.getLevel() >= 30;
    }

    @Override // k.p.action.barrage.BaseEnemy
    public void onWin(BasePet pet) {
        super.onWin(pet);
        pet.changePower(60);
        pet.changePoint(60);
        if (pet.getPetSetting("WinRemilia") == null) {
            pet.setAchievement(pet.getAchievement() + 1);
            pet.setPetSetting("WinRemilia", new Flag());
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
        int r = random.nextInt(100);
        if (r < 20) {
            int damage = random.nextInt(BasePet.MAX_LEVEL) + 1;
            damageTarget(damage);
            sendMessage("神枪「冈格尼尔」");
            sendMessage(String.valueOf(getName()) + " 对 " + target.getName() + "造成" + damage + "点伤害");
        }
    }

    @Override // k.p.action.barrage.BaseShoujo, k.p.action.barrage.Shoujo
    public void onDamaged(Shoujo src, int damage) {
        Random random = new Random();
        int r = random.nextInt(100);
        if (r < 30) {
            sendMessage("「抱头蹲防」");
            setCurrentHP(getCurrentHP() + damage);
            if (getCurrentHP() > getMaxHP()) {
                setCurrentHP(getMaxHP());
            }
        }
    }
}
