package k.p.animation

import k.p.exceptions.LoadXMLFailException
import k.p.services.AnimationService
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.FileInputStream

/**
 * 保真修正:v1.0.2 真机行为证明每个 XML 事件只能推进一次 next()。
 * 旧重建版在每个循环底部丢弃一个事件(底部的第二次 next()),
 * 会把 animationinfo(帧数据)全部吞掉 → 动画注册无帧 → 宠物空白。
 * 此怪癖修正为单 next 语义(逐事件处理)。
 */
class PetAnimationLoader(private val filePath: String) {

    @Throws(LoadXMLFailException::class)
    fun load() {
        var tagName: String? = null
        var animation: PetAnimation? = null
        var info: PetAnimationInfo? = null
        val parser = try {
            XmlPullParserFactory.newInstance().newPullParser().apply {
                setInput(FileInputStream(filePath), "UTF-8")
            }
        } catch (e: Exception) {
            throw LoadXMLFailException(0, "")
        }
        while (true) {
            val eventType = try {
                parser.next()
            } catch (e: Exception) {
                throw LoadXMLFailException(parser.lineNumber, "")
            }
            if (eventType == XmlPullParser.END_DOCUMENT) {
                return
            }
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    try {
                        tagName = parser.name
                        when (tagName) {
                            "animation" -> {
                                animation = PetAnimation()
                                try {
                                    if (parser.getAttributeValue(null, "loop") == null) {
                                        animation!!.loop = 0
                                    } else {
                                        animation!!.loop = Integer.parseInt(parser.getAttributeValue(null, "loop"))
                                    }
                                } catch (e: Exception) {
                                    throw LoadXMLFailException(parser.lineNumber, "")
                                }
                                animation!!.name = parser.getAttributeValue(null, "name")
                                // 注意:Kotlin split("\\|") 是字面匹配二字符"\\|",拆不开竖线(Java split 是正则)
                                // 必须用 split('|') 按字符拆,否则 type 永远单元素 → 动画类型匹配全部失败
                                animation!!.type = parser.getAttributeValue(null, "type")!!.split('|').toTypedArray()
                            }
                            "animationinfo" -> {
                                try {
                                    info = PetAnimationInfo()
                                    info!!.picPath = parser.getAttributeValue(null, "path")
                                    info!!.delay = Integer.parseInt(parser.getAttributeValue(null, "delay"))
                                    animation!!.addActionInfo(info!!)
                                } catch (e: Exception) {
                                    throw LoadXMLFailException(parser.lineNumber, "")
                                }
                            }
                            "settings" -> {
                                AnimationService.petWidth = Integer.parseInt(parser.getAttributeValue(null, "width"))
                                AnimationService.petHeight = Integer.parseInt(parser.getAttributeValue(null, "height"))
                            }
                        }
                    } catch (e: Exception) {
                        throw LoadXMLFailException(parser.lineNumber, "")
                    }
                }
                XmlPullParser.END_TAG -> {
                    try {
                        tagName = parser.name
                        if ("animation" == tagName) {
                            AnimationService.registerAnimation(animation!!)
                            animation = null
                        }
                    } catch (e: Exception) {
                        throw LoadXMLFailException(parser.lineNumber, "")
                    }
                }
                else -> {}
            }
        }
    }
}