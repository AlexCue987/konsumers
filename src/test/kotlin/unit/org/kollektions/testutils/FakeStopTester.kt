package org.kollektions.testutils

import org.kollektions.consumers.Consumer
import org.kollektions.consumers.ConsumerBuilder

class FakeStopTester<T>: Consumer<T> {
    private var stopped = false

    override fun process(value: T) {}

    override fun results() = stopped

    override fun stop() {
        if(stopped) {
            throw RuntimeException("Already stopped")
        }
        stopped = true
    }

    fun isStopped() = stopped
}


fun<T, V> ConsumerBuilder<T, V>.fakeStopTester() = this.build(FakeStopTester())
