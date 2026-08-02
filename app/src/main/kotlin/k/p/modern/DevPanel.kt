package k.p.modern

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import k.p.domain.BasePet
import k.p.domain.states.BasePetState
import k.p.main.MainService
import k.p.services.AnimationService
import k.p.services.PetService
import k.p.services.StateService
import k.p.services.ViewService
import k.p.utils.EnvironmentUtil
import local.kcn.utils.LogUtil
import java.io.File

/**
 * 开发者测试面板(仅 debug 构建入口,主界面"开发者"按钮打开)。
 * 用途:测试状态机/属性/动画/崩溃日志,无需操作宠物。
 */
object DevPanel {
    private var panel: View? = null
    private var panelContext: Context? = null

    @JvmStatic
    fun show(context: Context) {
        hide()
        panelContext = context.applicationContext
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.argb(230, 40, 40, 45))
            setPadding(24, 24, 24, 24)
        }

        fun addButton(label: String, action: () -> Unit) {
            container.addView(Button(context).apply {
                text = label
                setOnClickListener { action() }
            })
        }

        addButton("状态:活跃") { requestState(StateService.ACTIVE!!) }
        addButton("状态:睡觉") { requestState(StateService.SLEEP!!) }
        addButton("状态:学习") { requestState(StateService.STUDY!!) }
        addButton("状态:工作") { requestState(StateService.WORK!!) }
        addButton("状态:觅食") { requestState(StateService.FORAGE!!) }
        addButton("状态:搜索") { requestState(StateService.SEARCH!!) }
        addButton("状态:死亡") { requestState(StateService.DEAD!!) }
        addButton("时间:正常 ×1") { setMultiplier(1) }
        addButton("时间:×60(1秒=1分)") { setMultiplier(60) }
        addButton("时间:×600(1秒=10分)") { setMultiplier(600) }
        addButton("属性:+50 饱食") { pet().changeRepletionDegree(50) }
        addButton("属性:+50 饮水") { pet().changeDrinkDegree(50) }
        addButton("属性:+50 精力") { pet().changeEnergy(50) }
        addButton("属性:-50 饱食") { pet().changeRepletionDegree(-50) }
        addButton("属性:-50 饮水") { pet().changeDrinkDegree(-50) }
        addButton("属性:-50 精力") { pet().changeEnergy(-50) }
        addButton("模拟升级(属性+1)") { simulateLevelUp() }
        addButton("动画:随机活跃") { randomAnimation() }
        addButton("宠物信息") { showPetInfo(context) }
        addButton("崩溃测试(验证 crash.log)") {
            throw RuntimeException("DevPanel 手动崩溃测试")
        }
        addButton("查看崩溃日志") { showCrashLog(context) }
        addButton("关闭") { hide() }

        val scroll = ScrollView(context).apply {
            addView(container)
        }
        val params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = 520
            format = PixelFormat.TRANSLUCENT
            gravity = Gravity.CENTER
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        wm.addView(scroll, params)
        panel = scroll
    }

    @JvmStatic
    fun hide() {
        val v = panel ?: return
        panel = null
        try {
            val wm = v.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.removeView(v)
        } catch (e: Exception) {
            LogUtil.log(e)
        }
    }

    private fun pet(): BasePet = PetService.pet!!

    private fun requestState(state: BasePetState) {
        pet().requestChangeState(state, BasePet.MAX_LEVEL)
    }

    private fun setMultiplier(m: Int) {
        MainService.timeMultiplier = m
        toast("时间倍率 = ×$m")
    }

    private fun simulateLevelUp() {
        pet().setStrength(pet().getStrength() + 1)
        pet().setSpeed(pet().getSpeed() + 1)
        pet().setMagic(pet().getMagic() + 1)
        toast("属性+1:力量/速度/魔力")
    }

    private fun showPetInfo(context: Context) {
        val p = pet()
        val info = "状态:" + p.currentState.getStateDoingDescription() +
            "\nLv." + p.level +
            " 饱食:" + p.repletionDegree +
            " 饮水:" + p.drinkDegree +
            " 精力:" + p.energy +
            "\n力/速/魔:" + p.strength + "/" + p.speed + "/" + p.magic +
            "\n寿命:" + (p.lifeTime / 60000) + "分" +
            "\n时间倍率:×" + MainService.timeMultiplier
        toast(info)
    }

    private fun toast(msg: String) {
        val ctx = panelContext ?: return
        val t = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT)
        // 面板居中占屏,toast 默认底部会被盖住 → 顶部显示
        t.setGravity(Gravity.TOP, 0, 160)
        t.show()
    }

    private fun randomAnimation() {
        val anim = AnimationService.getRandomAnimationByType("ACTIVE") ?: return
        ViewService.petView?.changeAnimation(anim)
    }

    private fun showCrashLog(context: Context) {
        val f = File(EnvironmentUtil.getMainPath() + "/system/log/crash.log")
        val text = if (f.exists()) {
            val content = f.readText()
            if (content.isEmpty()) "(crash.log 为空)" else content.takeLast(600)
        } else {
            "(无 crash.log)"
        }
        Toast.makeText(context.applicationContext, text, Toast.LENGTH_LONG).show()
    }
}