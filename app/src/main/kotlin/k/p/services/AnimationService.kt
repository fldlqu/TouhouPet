package k.p.services

import k.p.animation.PetAnimation
import k.p.animation.PetAnimationLoader
import k.p.exceptions.LoadXMLFailException
import k.p.main.MainService
import k.p.modern.Diag
import k.p.utils.EnvironmentUtil
import local.kcn.utils.LogUtil
import java.util.ArrayList
import java.util.Random

class AnimationService private constructor() {
    private var animationList: MutableList<PetAnimation> = ArrayList()

    companion object {
        @JvmField
        var petHeight = 0
        @JvmField
        var petWidth = 0
        private var instance: AnimationService? = null

        @JvmStatic
        fun init(mainService: MainService) {
            instance = AnimationService()
            instance!!.animationList = ArrayList()
            loadAction()
        }

        private fun loadAction() {
            try {
                PetAnimationLoader(EnvironmentUtil.getMainPath() + "/pet/animations/satori.xml").load()
            } catch (e: LoadXMLFailException) {
                LogUtil.log("XML加载失败")
                LogUtil.log("Line:" + e.lineNumber)
                LogUtil.log("Caused:" + e.message)
            }
        }

        @JvmStatic
        fun registerAnimation(animation: PetAnimation) {
            instance!!.animationList.add(animation)
            Diag.log("registerAnimation: " + animation.name + " frames=" + animation.getList().size + " type=" + (animation.type?.joinToString("|") ?: "null"))
        }

        @JvmStatic
        fun getAnimationByName(animationName: String): PetAnimation? {
            for (animation in instance!!.animationList) {
                if (animationName == animation.name) {
                    return animation
                }
            }
            return null
        }

        @JvmStatic
        fun getRandomAnimationByType(typeName: String): PetAnimation? {
            Diag.log("getRandomAnimationByType: type=" + typeName + " list=" + (instance?.animationList?.size ?: -1))
            for ((i, anim) in instance!!.animationList.withIndex()) {
                Diag.log("   anim[$i]: " + anim.name + " typeArr=" + anim.type!!.contentDeepToString() + " type=" + (anim.type?.joinToString(",") ?: "null"))
            }
            val tmpList = ArrayList<PetAnimation>()
            for (animation in instance!!.animationList) {
                var hasType = false
                val type = animation.type!!
                for (type2 in type) {
                    if (typeName.equals(type2)) {
                        hasType = true
                        break
                    }
                }
                if (hasType) {
                    tmpList.add(animation)
                }
            }
            val sz = tmpList.size
            if (sz > 0) {
                val picked = tmpList[Random().nextInt(sz)]
                Diag.log("pick animation: " + picked.name + " (type=" + typeName + ")")
                return picked
            }
            return if ("ACTIVE".equals(typeName)) {
                null
            } else {
                getRandomAnimationByType("ACTIVE")
            }
        }

        @JvmStatic
        fun requestChangeAnimation(animation: PetAnimation?) {
            if (animation != null) {
                animation.reset()
                ViewService.petView?.changeAnimation(animation)
            }
        }

        @JvmStatic
        fun release() {
            instance = null
        }
    }
}