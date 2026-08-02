package k.p.view.sliderview;

import java.util.ArrayList;
import java.util.List;
import k.p.action.barrage.ACirno;
import k.p.action.barrage.Aya;
import k.p.action.barrage.BaseEnemy;
import k.p.action.barrage.Cirno;
import k.p.action.barrage.ExCirno;
import k.p.action.barrage.Flandre;
import k.p.action.barrage.Marisa;
import k.p.action.barrage.Mokou;
import k.p.action.barrage.Patchouli;
import k.p.action.barrage.Reisen;
import k.p.action.barrage.Remilia;
import k.p.action.barrage.Sakuya;
import k.p.action.barrage.Wriggle;
import k.p.action.barrage.Youmu;
import k.p.action.barrage.Yuugi;
import k.p.services.PetService;

/* JADX INFO: loaded from: classes.dex */
public class BarrageSliderItemList extends SliderItemList {
    private List<BarrageInfo> barrageList;

    public BarrageSliderItemList(SliderView sliderView, SliderCanvas sc) {
        super(sliderView, sc);
    }

    @Override // k.p.view.sliderview.SliderItemList
    public void init() {
        super.init();
        this.barrageList = new ArrayList();
        Integer count = 0;
        try {
            count = (Integer) PetService.pet.getPetSetting("BeatEXCirno");
        } catch (Exception e) {
        }
        if (count == null || count.intValue() < 10) {
            try {
                count = (Integer) PetService.pet.getPetSetting("BeatCirno");
            } catch (Exception e2) {
            }
            if (count == null || count.intValue() < 10) {
                this.barrageList.add(new BarrageInfo(Cirno.class, "⑨"));
            } else {
                this.barrageList.add(new BarrageInfo(ExCirno.class, "EX⑨"));
            }
        } else {
            this.barrageList.add(new BarrageInfo(ACirno.class, "A⑨"));
        }
        this.barrageList.add(new BarrageInfo(Wriggle.class, "虫子"));
        this.barrageList.add(new BarrageInfo(Marisa.class, "魔理沙"));
        this.barrageList.add(new BarrageInfo(Yuugi.class, "红有三"));
        this.barrageList.add(new BarrageInfo(Youmu.class, "妖梦"));
        this.barrageList.add(new BarrageInfo(Sakuya.class, "十六"));
        this.barrageList.add(new BarrageInfo(Patchouli.class, "图书"));
        this.barrageList.add(new BarrageInfo(Remilia.class, "蕾米"));
        if (PetService.pet.getPetSetting("CanBeatFlandre") != null) {
            this.barrageList.add(new BarrageInfo(Flandre.class, "芙兰"));
        }
        if (PetService.pet.getPetSetting("CanBeatReisen") != null) {
            this.barrageList.add(new BarrageInfo(Reisen.class, "铃仙"));
        }
        if (PetService.pet.getPetSetting("CanBeatMokou") != null) {
            this.barrageList.add(new BarrageInfo(Mokou.class, "妹红"));
        }
        this.barrageList.add(new BarrageInfo(Aya.class, "文文"));
        refreshEnemy();
    }

    public void refreshEnemy() {
        clearSliderItemView();
        for (BarrageInfo info : this.barrageList) {
            addSliderItemView(new BarrageButton(info.getEnemyClazz(), this.sliderView, info.getName()));
        }
        ReturnButton returnButton = new ReturnButton(this.sliderView);
        returnButton.init();
        addSliderItemView(returnButton);
    }

    public void addEnemy(BarrageInfo info) {
        this.barrageList.add(info);
        refreshEnemy();
    }

    public static class BarrageInfo {
        private Class<? extends BaseEnemy> enemyClazz;
        private String name;

        public BarrageInfo(Class<? extends BaseEnemy> enemyClazz, String name) {
            this.enemyClazz = enemyClazz;
            this.name = name;
        }

        public Class<? extends BaseEnemy> getEnemyClazz() {
            return this.enemyClazz;
        }

        public String getName() {
            return this.name;
        }
    }
}
