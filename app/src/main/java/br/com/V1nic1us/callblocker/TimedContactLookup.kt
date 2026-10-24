package br.com.V1nic1us.callblocker

import java.util.concurrent.Executor
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/** Completes once, using false if contact lookup stalls or cannot be started. */
class TimedContactLookup(
    private val worker: Executor,
    private val scheduler: ScheduledExecutorService,
    private val timeoutMillis: Long,
) {
    fun start(lookup: () -> Boolean, complete: (Boolean) -> Unit) {
        val completed = AtomicBoolean(false)
        fun finish(isKnownContact: Boolean) {
            if (completed.compareAndSet(false, true)) complete(isKnownContact)
        }

        try {
            scheduler.schedule({ finish(false) }, timeoutMillis, TimeUnit.MILLISECONDS)
            worker.execute {
                val isKnownContact = try {
                    lookup()
                } catch (_: Exception) {
                    false
                }
                finish(isKnownContact)
            }
        } catch (_: RejectedExecutionException) {
            finish(false)
        }
    }
}
