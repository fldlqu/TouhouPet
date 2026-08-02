package k.p.item.drink;

import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class ThermalSpring extends Drink {
    private static final long serialVersionUID = 3897527197972640143L;

    @Override // k.p.item.Item
    public String getName() {
        return "温泉水";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "温泉水,富含各种矿物质和硫磺\r\n喝了绝对强身健体(咳..\r\n\r\n+8饮水度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        pet.changeDrinkDegree(8);
    }
}
