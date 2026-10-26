package br.com.V1nic1us.callblocker

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimedContactLookupTest {
    @Test
    fun completedLookupReturnsKnownContactOnce() {
        val worker = Executors.newSingleThreadExecutor()
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        try {
            val result = AtomicReference<Boolean>()
            val calls = AtomicInteger()
            val responded = CountDownLatch(1)
            TimedContactLookup(worker, scheduler, 250).start({ true }) {
                result.set(it)
                calls.incrementAndGet()
                responded.countDown()
            }

            assertTrue(responded.await(1, TimeUnit.SECONDS))
            Thread.sleep(300)
            assertEquals(true, result.get())
            assertEquals(1, calls.get())
        } finally {
            worker.shutdownNow()
            scheduler.shutdownNow()
        }
    }

    @Test
    fun stalledLookupTimesOutAndLateSuccessCannotRespondAgain() {
        val worker = Executors.newSingleThreadExecutor()
        val scheduler = Executors.newSingleThreadScheduledExecutor()
        val releaseLookup = CountDownLatch(1)
        try {
            val result = AtomicReference<Boolean>()
            val calls = AtomicInteger()
            val responded = CountDownLatch(1)
            val lookupFinished = CountDownLatch(1)
            val start = System.nanoTime()
            TimedContactLookup(worker, scheduler, 50).start({
                releaseLookup.await(1, TimeUnit.SECONDS)
                lookupFinished.countDown()
                true
            }) {
                result.set(it)
                calls.incrementAndGet()
                responded.countDown()
            }

            assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 500)
            assertTrue(responded.await(1, TimeUnit.SECONDS))
            assertFalse(result.get())
            releaseLookup.countDown()
            assertTrue(lookupFinished.await(1, TimeUnit.SECONDS))
            assertEquals(1, calls.get())
        } finally {
            releaseLookup.countDown()
            worker.shutdownNow()
            scheduler.shutdownNow()
        }
    }
}
