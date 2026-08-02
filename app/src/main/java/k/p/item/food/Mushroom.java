package k.p.item.food;

import java.util.Random;
import k.p.domain.BasePet;

/* JADX INFO: loaded from: classes.dex */
public class Mushroom extends Food {
    private static final long serialVersionUID = -5267456244095647868L;

    @Override // k.p.item.Item
    public String getName() {
        return "蘑菇";
    }

    @Override // k.p.item.Item
    public String getItemDescription() {
        return "魔法森林里采到的蘑菇,说不定会有毒.\r\n\r\n+10饱食度";
    }

    @Override // k.p.item.BaseEatableItem
    protected void onEat(BasePet pet) {
        Random r = new Random();
        pet.changeRepletionDegree(10);
        if (r.nextInt(100) < 15) {
            pet.changeEnergy(-5);
        }
    }
}
