package k.p.modern

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
 * 架构现代化:统一协程作用域工厂。
 * 每个组件持有自己的 scope,生命周期结束时 cancel(),不再有裸 Thread 泄漏。
 */
object AppScopes {
    /** 后台计算 / 游戏循环 */
    fun newDefault(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** UI 主线程 */
    fun newMain(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /** 单线程后台:lockCanvas/unlockCanvasAndPost 要求同线程配对(ReentrantLock),
     *  多线程池 + delay 挂起点会导致跨线程 unlock → IllegalMonitorStateException。
     *  limitedParallelism(1) 只保证互斥不保证恢复同线程(挂起点会换 worker),
     *  必须用 newSingleThreadContext 每次恢复都在同一线程执行。 */
    fun newSingle(): CoroutineScope {
        val dispatcher = Executors.newSingleThreadExecutor { r ->
            Thread(r, "pet-draw").apply { isDaemon = true }
        }.asCoroutineDispatcher()
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}
