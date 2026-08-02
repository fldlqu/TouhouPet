package k.p.action.barrage;

import k.p.services.BarrageService;

/* JADX INFO: loaded from: classes.dex */
public class BaseShoujo implements Shoujo {
    protected int currentHP;
    protected int magic;
    protected int maxHP;
    protected String name;
    protected int speed;
    protected int strength;

    public BaseShoujo() {
    }

    public BaseShoujo(int strength, int speed, int magic) {
        this.strength = strength;
        this.speed = speed;
        this.magic = magic;
    }

    @Override // k.p.action.barrage.Shoujo
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override // k.p.action.barrage.Shoujo
    public void init() {
        this.maxHP = this.strength * 20;
        this.currentHP = this.maxHP;
    }

    @Override // k.p.action.barrage.Shoujo
    public void onCauseDamage(Shoujo target, int damage) {
    }

    @Override // k.p.action.barrage.Shoujo
    public void onDamaged(Shoujo src, int damage) {
    }

    @Override // k.p.action.barrage.Shoujo
    public void onRoundStart(Shoujo target, int roundCount) {
    }

    @Override // k.p.action.barrage.Shoujo
    public void onRoundEnd(Shoujo target, int roundCount) {
    }

    @Override // k.p.action.barrage.Shoujo
    public int getStrength() {
        return this.strength;
    }

    @Override // k.p.action.barrage.Shoujo
    public int getSpeed() {
        return this.speed;
    }

    @Override // k.p.action.barrage.Shoujo
    public int getMagic() {
        return this.magic;
    }

    @Override // k.p.action.barrage.Shoujo
    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override // k.p.action.barrage.Shoujo
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override // k.p.action.barrage.Shoujo
    public void setMagic(int magic) {
        this.magic = magic;
    }

    @Override // k.p.action.barrage.Shoujo
    public int getMaxHP() {
        return this.maxHP;
    }

    @Override // k.p.action.barrage.Shoujo
    public int getCurrentHP() {
        return this.currentHP;
    }

    @Override // k.p.action.barrage.Shoujo
    public void setCurrentHP(int hp) {
        this.currentHP = hp;
    }

    @Override // k.p.action.barrage.Shoujo
    public void sendMessage(String message) {
        BarrageService.putString(message);
    }

    public void damageTarget(int damage) {
        damageTarget(damage, true);
    }

    public void damageTarget(int damage, boolean triggerEvent) {
        if (this == BarrageService.player) {
            BarrageService.target.setCurrentHP(BarrageService.target.getCurrentHP() - damage);
            if (triggerEvent) {
                BarrageService.player.onCauseDamage(BarrageService.target, damage);
                if (BarrageService.target.getCurrentHP() > 0) {
                    BarrageService.target.onDamaged(BarrageService.player, damage);
                    return;
                }
                return;
            }
            return;
        }
        BarrageService.player.setCurrentHP(BarrageService.player.getCurrentHP() - damage);
        if (triggerEvent) {
            BarrageService.target.onCauseDamage(BarrageService.player, damage);
            if (BarrageService.player.getCurrentHP() > 0) {
                BarrageService.player.onDamaged(BarrageService.target, damage);
            }
        }
    }
}
