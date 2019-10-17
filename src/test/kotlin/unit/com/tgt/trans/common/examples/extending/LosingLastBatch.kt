package com.tgt.trans.common.examples.extending

import com.tgt.trans.common.konsumers.consumers.Consumer
import com.tgt.trans.common.konsumers.consumers.consume
import kotlin.test.Test

class LosingLastBatch {
    @Test
    fun `BatchSaverV1 does not implement stop and loses last batch`() {
        (1..5).asSequence().consume(BatchSaverV1(3))
    }

    /*
    Output:
Saving batch [1, 2, 3]
     */

    @Test
    fun `BatchSaverV2 implements stop and saves last batch`() {
        (1..5).asSequence().consume(BatchSaverV2(3))
    }

    /*
Output:
Saving batch [1, 2, 3]
Saving batch [4, 5]
 */

    private class BatchSaverV1(val batchSize: Int): Consumer<Int> {
        private val buffer = mutableListOf<Int>()
        private val database = FakeDatabase()

        override fun process(value: Int) {
            buffer.add(value)
            if(buffer.size == batchSize) {
                println("Saving buffer from process()")
                database.save(buffer)
                buffer.clear()
            }
        }

        override fun results(): Any = 42
    }

    private class BatchSaverV2(val batchSize: Int): Consumer<Int> {
        private val buffer = mutableListOf<Int>()
        private val database = FakeDatabase()

        override fun process(value: Int) {
            buffer.add(value)
            if(buffer.size == batchSize) {
                println("Saving buffer from process()")
                database.save(buffer)
                buffer.clear()
            }
        }

        override fun results(): Any = 42

        override fun stop() {
            println("Saving buffer from stop()")
            database.save(buffer)
        }
    }

    private class FakeDatabase {
        fun save(batch: List<Int>) {
            println("Saving batch $batch")
        }
    }
}
