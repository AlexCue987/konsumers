package com.tgt.trans.common.testutils

import com.tgt.trans.common.konsumers.consumers.Consumer

class FakeStopTester<T>: Consumer<T>{
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
