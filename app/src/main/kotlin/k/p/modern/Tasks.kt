package k.p.modern

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Java 侧可用的协程后台任务(替代 2012 年的"new Thread + sleep + 动作"匿名线程模式)。
 *
 * 注意:与客户端一次性线程的语义保持一致 —— 任务是不可取消的单次/循环执行
 * (原版线程同样泄漏且不可取消;阶段 3 服务实例化后再细化生命周期)。
 */
object Tasks {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** 延迟 ms 后执行一次(替代匿名 Thread + sleep + run).start());Runnable 便于 Java 调用 */
    @JvmStatic
    fun after(ms: Long, block: Runnable) {
        scope.launch {
            delay(ms)
            block.run()
        }
    }

    /** 每隔 intervalMs 执行 block 一次,共 rounds 次(替代 for + sleep);block 返回 false 提前终止 */
    @JvmStatic
    fun loop(intervalMs: Long, rounds: Int, block: (Int) -> Boolean, finish: Runnable?) {
        scope.launch {
            for (round in 1 until rounds) {
                delay(intervalMs)
                if (!block(round)) break
            }
            finish?.run()
        }
    }
}