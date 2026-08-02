package k.p.view.sliderview

import k.p.action.BarrageAction
import k.p.action.barrage.BaseEnemy
import k.p.services.PetService

class BarrageButton(
    private var enemyClazz: Class<out BaseEnemy>,
    sv: SliderView,
    hint: String
) : BaseSliderTextButton(sv, hint) {

    override fun onClick() {
        try {
            BarrageAction<BaseEnemy>(enemyClazz.newInstance()).start(PetService.pet)
        } catch (e: Exception) {
        }
    }
}