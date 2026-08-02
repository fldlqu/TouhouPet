package k.p.view.sliderview;

import k.p.action.BarrageAction;
import k.p.action.barrage.BaseEnemy;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class BarrageButton extends BaseSliderTextButton {
    private Class<? extends BaseEnemy> enemyClazz;

    public BarrageButton(Class<? extends BaseEnemy> enemyClazz, SliderView sliderView, String hint) {
        super(sliderView, hint);
        this.enemyClazz = enemyClazz;
    }

    @Override // k.p.view.sliderview.BaseSliderTextButton, k.p.view.sliderview.SliderItemView
    public void onClick() {
        try {
            new BarrageAction(this.enemyClazz.newInstance()).start(PetService.pet);
        } catch (Exception e) {
        }
    }
}
