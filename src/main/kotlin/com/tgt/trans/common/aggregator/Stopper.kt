package com.tgt.trans.common.aggregator

class Stopper<T>(override val aggregator: Aggregator<T>,
        private val circuitBreaker: (a: T) ->Boolean,
                 private val keepBreakingPoint: Boolean): DecoratedAggregator<T> {
    private var stopped = false

    override fun process(value: T) {
        if (stopped) {
            return
        }
        val circuitBroken = circuitBreaker(value)
        if (circuitBroken) {
            stop()
            if (!keepBreakingPoint) {
                return
            }
        }
        aggregator.process(value)
    }

    override fun emptyCopy() = Stopper(aggregator.emptyCopy(), circuitBreaker, keepBreakingPoint)

    override fun stop() {
        if (!stopped) {
            stopped = true
            aggregator.stop()
        }
    }
}

fun<T> Aggregator<T>.stopWhen(circuitBreaker: (a: T) ->Boolean) =
        Stopper(this, circuitBreaker, false)

fun<T> Aggregator<T>.stopAfter(circuitBreaker: (a: T) ->Boolean) =
        Stopper(this, circuitBreaker, true)
