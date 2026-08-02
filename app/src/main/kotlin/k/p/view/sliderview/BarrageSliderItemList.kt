package k.p.view.sliderview

import k.p.action.barrage.ACirno
import k.p.action.barrage.Aya
import k.p.action.barrage.BaseEnemy
import k.p.action.barrage.Cirno
import k.p.action.barrage.ExCirno
import k.p.action.barrage.Flandre
import k.p.action.barrage.Marisa
import k.p.action.barrage.Mokou
import k.p.action.barrage.Patchouli
import k.p.action.barrage.Reisen
import k.p.action.barrage.Remilia
import k.p.action.barrage.Sakuya
import k.p.action.barrage.Wriggle
import k.p.action.barrage.Youmu
import k.p.action.barrage.Yuugi
import k.p.services.PetService

class BarrageSliderItemList(sv: SliderView, sc: SliderCanvas) : SliderItemList(sv, sc) {
    private var barrageList: MutableList<BarrageInfo> = ArrayList()

    override fun init() {
        super.init()
        barrageList = ArrayList()
        var count: Int? = 0
        try {
            count = PetService.pet!!.getPetSetting("BeatEXCirno") as? Int
        } catch (e: Exception) {
        }
        if (count == null || count < 10) {
            try {
                count = PetService.pet!!.getPetSetting("BeatCirno") as? Int
            } catch (e: Exception) {
            }
            if (count == null || count < 10) {
                barrageList.add(BarrageInfo(Cirno::class.java, "⑨"))
            } else {
                barrageList.add(BarrageInfo(ExCirno::class.java, "EX⑨"))
            }
        } else {
            barrageList.add(BarrageInfo(ACirno::class.java, "A⑨"))
        }
        barrageList.add(BarrageInfo(Wriggle::class.java, "虫子"))
        barrageList.add(BarrageInfo(Marisa::class.java, "魔理沙"))
        barrageList.add(BarrageInfo(Yuugi::class.java, "红有三"))
        barrageList.add(BarrageInfo(Youmu::class.java, "妖梦"))
        barrageList.add(BarrageInfo(Sakuya::class.java, "十六"))
        barrageList.add(BarrageInfo(Patchouli::class.java, "图书"))
        barrageList.add(BarrageInfo(Remilia::class.java, "蕾米"))
        if (PetService.pet!!.getPetSetting("CanBeatFlandre") != null) {
            barrageList.add(BarrageInfo(Flandre::class.java, "芙兰"))
        }
        if (PetService.pet!!.getPetSetting("CanBeatReisen") != null) {
            barrageList.add(BarrageInfo(Reisen::class.java, "铃仙"))
        }
        if (PetService.pet!!.getPetSetting("CanBeatMokou") != null) {
            barrageList.add(BarrageInfo(Mokou::class.java, "妹红"))
        }
        barrageList.add(BarrageInfo(Aya::class.java, "文文"))
        refreshEnemy()
    }

    fun refreshEnemy() {
        clearSliderItemView()
        for (info in barrageList) {
            addSliderItemView(BarrageButton(info.enemyClazz, sliderView, info.name))
        }
        val returnButton = ReturnButton(sliderView)
        returnButton.init()
        addSliderItemView(returnButton)
    }

    fun addEnemy(info: BarrageInfo) {
        barrageList.add(info)
        refreshEnemy()
    }

    class BarrageInfo(
        var enemyClazz: Class<out BaseEnemy>,
        var name: String
    )
}