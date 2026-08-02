package k.p.modern

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * 架构现代化:统一协程作用域工厂。
 * 每个组件持有自己的 scope,生命周期结束时 cancel(),不再有裸 Thread 泄漏。
 */
object AppScopes {
    /** 后台计算 / 游戏循环 */
    fun newDefault(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** UI 主线程 */
    fun newMain(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}
