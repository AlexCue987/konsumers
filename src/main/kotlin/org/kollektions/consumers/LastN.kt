package org.kollektions.consumers

class LastN<T>(val count: Int): Consumer<T> {
    var itemsProcessed = 0
    val buffer = mutableListOf<T>()

    init {
        require(count > 0) {"Count must be positive, is: $count"}
    }

    override  fun process(value: T) {
        if(itemsProcessed < count) {
            buffer.add(value)
        } else {
            buffer[itemsProcessed % count] = value
        }
        itemsProcessed++
    }

    override  fun results() = (maxOf(itemsProcessed - count, 0) until itemsProcessed).asSequence()
        .map{buffer[it % count]}
        .toList()

    override  fun stop() {}
}

 fun<T, V> ConsumerBuilder<T, V>.lastN(count: Int) = this.build(LastN<V>(count))
