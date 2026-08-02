package k.p.modern

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 可控的周期轮询器(替代 while+Thread.sleep 轮询线程,如歌曲进度条)。
 * start/stop 幂等;stop 协程取消后立即退出。
 */
class Poller(private val ms: Long, private val tick: Runnable) {
    private val scope = AppScopes.newDefault()
    private var job: Job? = null

    fun start() {
        if (job == null) {
            job = scope.launch {
                while (isActive) {
                    delay(ms)
                    tick.run()
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }
}