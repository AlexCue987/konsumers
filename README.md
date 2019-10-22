<p align="center">
    <img width=200 alt="Kotlin" src="https://upload.wikimedia.org/wikipedia/commons/7/74/Kotlin-logo.svg">
</p>

# Konsumers

Advanced work with Kotlin sequences. Developed to make solving many common problems easier, and to improve performance in cases when iterating the sequence and/or transforming its items is slow.

* Advanced features to split and transform sequences, to make solving complex problems easier.
* Allows to iterate a sequence once and simultaneously compute multiple results, improving performance.
* Allows to use one computation, such as filtering or mapping, in multiple results, making code shorter and easier to understand, and improving performance.
* Uses stateful transformations, such as filters and mappings, which allows for easy solutions to many common problems.
* Easy to use and extend.
* Pure Kotlin.

## Basics

### Computing multiple results while iterating a sequence once, to improve performance.

In following example we are searching for a flight that meets one of the following two criteria:

* Preferably, we want the cheapest flight arriving on Saturday.
* If this is not possible, then the plan B is the earliest flight arriving after Saturday.

```kotlin
        val cheapestOnSaturdayPlanA = filterOn<Flight> { it.arrival.toLocalDate() == saturday }
            .bottomNBy(1) { it: Flight -> it.price }

        val earliestAfterSaturdayPlanB = filterOn<Flight> { it.arrival.toLocalDate() > saturday }
            .bottomNBy(1) { it: Flight -> it.arrival }

        val actual = flights.consume(cheapestOnSaturdayPlanA, earliestAfterSaturdayPlanB)
```

For a complete working example, refer to [`examples/basics/FlightsFinder.kt`].

### Reusing one filtering or mapping in multiple consumers.

In the following example we compute a condition once, and use it in two consumers, `lowestLowTemperature` and `rainyDaysCount`. This makes our code terser, easier to understand, and might perform better if computing the condition is slow:

```kotlin
        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }

        val lowestLowTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val rainyDaysCount = count<DailyWeather>()

        val allResults = dailyWeather.consumeByOne(
            verySlowFilter.allOf(lowestLowTemperature, rainyDaysCount))
```

For a complete working example, refer to [`examples/basics/ReusingFilteringAndMapping.kt`].

### Process both accepted and rejected items: branching instead of filtering.

In some case we want to make sure each item is processed exactly once, and we use a condition to determine how to process it. For instance, if passenger have arrived at an airport, we may want to make sure that every passenger does exactly one of the two following actions:

* Exit the airport, if arrived at their final destination.
* Transfer to another flight.

We can do it with two filters, but the code is repetitive, the intent is not clear, and the condition is computed twice, which can hurt performance:

```kotlin
        val spaceportName = "Tattoine"
        val actual = passengers.consume(
            filterOn{ it: Passenger -> it.destination == spaceportName }.asList(),
            filterOn{ it: Passenger -> it.destination != spaceportName }.asList()
            )

```

Instead, we can use a `Branch` to compute a filter condition only once, and process both accepted and rejected items by two different consumers. This makes our intent more clear, and may improve performance:

```kotlin
        val leavingSpaceport = asList<Passenger>()
        val transferringToAnotherFlight = asList<Passenger>()

        val spaceportName = "Tattoine"
        passengers.consume(Branch({ it: Passenger -> it.destination == spaceportName },
            consumerForAccepted = leavingSpaceport,
            consumerForRejected = transferringToAnotherFlight))

        println("Left spaceport: ${leavingSpaceport.results()}")
        println("Transferred: ${transferringToAnotherFlight.results()}")

Left spaceport: [Passenger(name=Yoda, destination=Tattoine), Passenger(name=Chewbacca, destination=Tattoine)]
Transferred: [Passenger(name=R2D2, destination=Alderaan), Passenger(name=Han Solo, destination=Alderaan)]
```

For a complete working example, refer to [`examples/basics/Passengers.kt`].


### Using states in transformations

As we are iterating items in our sequence, we can store any data in a state. This allows for easy solutions to many common problems.

For instance, in the following example we are using a state named `lastTwoItems` to transform a series of temperature reading into a series of temperature changes:

```kotlin
        val lastTwoItems = LastN<Temperature>(2)
        val changes = temperatures.consume(
            keepState(lastTwoItems)
                .peek { println("current item $it") }
                .skip(1)
                .peek { println("  last two items: ${lastTwoItems.results()}") }
                .mapTo { it ->
                    val previousTemperature = lastTwoItems.results().last().temperature
                    TemperatureChange(it.takenAt, it.temperature, it.temperature - previousTemperature)
                }
                .peek { println("  change: $it") }
                .asList())

current item Temperature(takenAt=2019-09-23T07:15, temperature=46)
current item Temperature(takenAt=2019-09-23T17:20, temperature=58)
  last two items: [Temperature(takenAt=2019-09-23T07:15, temperature=46)]
  change: TemperatureChange(takenAt=2019-09-23T17:20, temperature=58, change=12)
current item Temperature(takenAt=2019-09-24T07:15, temperature=44)
  last two items: [Temperature(takenAt=2019-09-23T07:15, temperature=46), Temperature(takenAt=2019-09-23T17:20, temperature=58)]
  change: TemperatureChange(takenAt=2019-09-24T07:15, temperature=44, change=-14)
```

For a complete working example, refer to [`examples/basics/TemperatureChanges.kt`].

Any implementation of `Consumer` can be used to store a state. Multiple states can be collected at the same time, or at different times. All this is demonstrated in `examples/advanced/RaceResults.kt`.

#### Using states with filters.

In the following example we are processing a sequence of bank account deposits and withdrawals, and our filter makes sure that the account balance is never negative. The filter uses a state which stores the current account balance.

```kotlin
        val currentBalance = sumOfBigDecimal()

        val changeToReject = BigDecimal("-2")
        val changes = listOf(BigDecimal("3"), BigDecimal("-2"), changeToReject, BigDecimal.ONE)

        val acceptedChanges = changes.consume(
            peek<BigDecimal> { println("Before filtering: $it, current balance : ${currentBalance.sum()}") }
                .filterOn { (currentBalance.sum() + it) >= BigDecimal.ZERO }
                .keepState(currentBalance)
                .peek { println("After filtering, change: $it, current balance: ${currentBalance.sum()}") }
                .asList()
        )[0]
        assertEquals(listOf(BigDecimal("3"), BigDecimal("-2"), BigDecimal.ONE), acceptedChanges)

Before filtering: 3, current balance : 0
After filtering, change: 3, current balance: 3
Before filtering: -2, current balance : 3
After filtering, change: -2, current balance: 1
Before filtering: -2, current balance : 1
Before filtering: 1, current balance : 1
After filtering, change: 1, current balance: 2
```

For a complete working example, refer to [`examples/basics/NonNegativeAccountBalance.kt`].

**Note:** Kotlin standard library does provide this ability in some special cases, such as `filterIndexed` which uses an item's index, a state. `konsumers` allows us to use any `Consumer` as a state in a filter.

#### Combining mapping and filtering in one transformation.

Typically a `filter` will accept or reject items without transforming them, and a `map` must produce a transformed item for every incoming one.

Sometimes this approach forces us to produce a lot of short-lived objects. For example, suppose that whenever more than a half of the amount on a bank account is withdrawn at once, we need to do something, such as trigger an alert. Traditionally, we would:
* map an incoming transaction amount into an instance of another class `TransactionWithCurrentBalance` with two fields, `(previousBalance, transactionAmount)`
* filter these instances
* alert

In the following example, three out of four instances of `TransactionWithCurrentBalance` are very short-lived, and only passes the filter condition:

```kotlin
        val amounts = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))
        val largeWithdrawals = amounts.consume(toTransactionWithCurrentBalance()
            .peek { println("Before filtering: $it") }
            .filterOn { -it.amount > it.currentBalance * BigDecimal("0.5") }
            .peek { println("After filtering: $it") }
            .asList())

Before filtering: TransactionWithCurrentBalance(currentBalance=100, amount=100)
Before filtering: TransactionWithCurrentBalance(currentBalance=90, amount=-10)
Before filtering: TransactionWithCurrentBalance(currentBalance=89, amount=-1)
Before filtering: TransactionWithCurrentBalance(currentBalance=39, amount=-50)
After filtering: TransactionWithCurrentBalance(currentBalance=39, amount=-50)
```

Using `konsumers`, we can both filter and transform in the same transformation, eliminating the need to create short-lived-objects, as follows:

```kotlin
        val currentBalance = sumOfBigDecimal()
        val amounts = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))
        val transformation =
            { value: BigDecimal ->
                when {
                    -value > (currentBalance.sum() * BigDecimal("0.5")) -> sequenceOf(TransactionWithCurrentBalance(currentBalance.sum(), value))
                    else -> sequenceOf()
                }
            }

        val largeWithdrawals = amounts.consume(
            keepState(currentBalance)
                .peek { println("Before transformation: item $it, currentBalance ${currentBalance.sum()}") }
                .transformTo(transformation)
                .peek { println("After transformation: $it") }
                .asList())

Before transformation: item 100, currentBalance 100
Before transformation: item -10, currentBalance 90
Before transformation: item -1, currentBalance 89
Before transformation: item -50, currentBalance 39
After transformation: TransactionWithCurrentBalance(currentBalance=39, amount=-50)
```

For a complete working example, refer to [`examples/basics/LargeWithdrawals.kt`].

Note that in this case we are returning either an empty `sequenceOf()` or a sequence of one element. In general, we can transform one incoming item into a sequence, which can contain more than one element. This is shown in `examples\advanced\UnpackItems`.


### Grouping and Resetting

#### Basic Grouping

We can group items by any key, which is equivalent to the standard function `associateBy``. Unlike in previous examples, we do not provide a consumer. Instead, we provide a lambda that creates consumers as needed. Here is a basic example:

```kotlin
        val things = listOf(Thing("Amber", "Circle"),
            Thing("Amber", "Square"),
            Thing("Red", "Oval"))

        val actual = things
                .consume(groupBy(keyFactory =  { it: Thing -> it.color },
                    innerConsumerFactory = { counter() }))

        assertEquals(mapOf("Amber" to 2L, "Red" to 1L), actual[0])
```

For a complete working example, refer to [`examples/basics/BasicGroups.kt`].

#### Grouping with multiple consumers

After grouping by a key, we can submit values to more than one consumer:

```kotlin
        val actual = things
            .consume(groupBy(keyFactory =  { it: Thing -> it.color },
                innerConsumerFactory = { allOf(counter(), mapTo { it: Thing -> it.shape }.asList()) }))

        assertEquals(
            mapOf("Amber" to listOf(2L, listOf("Circle", "Square")),
                "Red" to listOf(1L, listOf("Oval"))),
            actual[0])
```

For a complete working example, refer to [`examples/basics/BasicGroups.kt`].

#### Nested groups

Groups can be nested. In the following example we group things by color, then group by shape:

```kotlin
        val actual = things.consume(
            groupBy(
                keyFactory = { a: Thing -> a.color },
                innerConsumerFactory = {
                    allOf(count(), groupBy(keyFactory = { a: Thing -> a.shape },
                        innerConsumerFactory = { count() }))
                })
        )
```

For a complete working example, refer to [`examples/basics/BasicGroups.kt`].

#### Why resetting?

Results of grouping are only available after all the sequence has been consumed. In some cases we can do better: once we know that we are done with some bucket, we can produce the results off that bucket immediately - and in many cases this ability is important.

For example, suppose that we are consuming a time series of weather readings like this,

```kotlin
    data class Temperature(val takenAt: LocalDateTime, val temperature: Int)
```
and need to provide daily aggregates, high and low temperatures, as follows:

```kotlin
    data class DailyWeather(val date: LocalDate, val low: Int, val high: Int)
```

The following code accomplishes that via grouping:

```kotlin
        val rawDailyAggregates = temperatures.consume(
            groupBy(keyFactory = { it: Temperature -> it.getDate() },
                innerConsumerFactory = { mapTo { it: Temperature -> it.temperature }.allOf(min(), max()) }
            ))
```

For a complete working example, refer to [`examples/basics/HighAndLowTemperature.kt`].

This code works, but the daily aggregates are not available until we have consumed the whole sequence.

Yet we know that we are consuming a time series of data points ordered by time. So, for example, as soon as we get a data point for Tuesday, we know that we are done consuming Monday's data. As such, we should be able to produce Monday's aggregates immediately. Resetting was developed to allow that, and is explained in the next section.

#### Resetting

In the following example instances of `DailyWeather` will be available as soon as possible, using resetting. We shall accomplish that in several simple steps.

First, we need to define a consumer for the incoming data to compute high and low temperatures. The consumer is unaware that it is producing daily aggregates, it just computes high and low temperatures. We are not creating a consumer, we are defining a lambda that will create a new consumer for every day, because we shall need a new consumer for every day:

```kotlin
        val intermediateConsumer = {
            peek<Temperature> { println("Consuming $it") }
            .mapTo { it: Temperature -> it.temperature }
            .allOf(min(), max()) }
```

Another consumer will be used as a state, to store the date of the first data point. We shall use this state to determine when the date changes:

```kotlin
        val stateToStoreDay = { mapTo<Temperature, LocalDate> {it.getDate()}.first() }
```

Second, we need to specify when to stop consuming: whenever the date changes. We are extracting a stored date from the state and comparing it against the date of the incoming data point:

```kotlin
    private fun dateChange() = { intermediateConsumers: List<Consumer<Temperature>>, value: Temperature ->
        val optionalDay = intermediateConsumers[1].results() as Optional<LocalDate>
         optionalDay.isPresent && optionalDay.get() != value.getDate()
    }
```

Next, we need to transform the data collected by the consumer into the format that we need, which is similar to populating of `finalDailyAggregates` in the previous section.

```kotlin
    fun mapResultsToDailyWeather(intermediateConsumers: List<Consumer<Temperature>>): DailyWeather {
        val results = intermediateConsumers.map { it.results() }
        val highAndLow = (results[0] as List<Any>)
        val lowTemperature = highAndLow[0] as Optional<Int>
        val highTemperature = highAndLow[1] as Optional<Int>
        val day = (results[1] as Optional<LocalDate>).get()
        return DailyWeather(day, lowTemperature.get(), highTemperature.get())
    }
```

Finally, let us show how all these pieces work together:

```kotlin
        val dailyAggregates = temperatures.consume(
            consumeWithResetting(
                intermediateConsumersFactory = { listOf(intermediateConsumer(), stateToStoreDay()) },
                resetTrigger = dateChange(),
                intermediateResultsTransformer = intermediateResultsTransformer,
                finalConsumer = peek<DailyWeather> { println("Consuming $it") }.asList()))

Consuming Temperature(takenAt=2019-09-23T07:15, temperature=46)
Consuming Temperature(takenAt=2019-09-23T17:20, temperature=58)
Consuming DailyWeather(date=2019-09-23, low=46, high=58)
Consuming Temperature(takenAt=2019-09-24T07:15, temperature=44)
Consuming Temperature(takenAt=2019-09-24T17:20, temperature=61)
Consuming DailyWeather(date=2019-09-24, low=44, high=61)
```

As we have seen, a `DailyWeather` daily aggregate is available as soon as possible: when we know that we have consumed all the data for the day.

For a complete working example, refer to [`examples/basics/HighAndLowTemperature.kt`].

There are other examples when resetting makes solving complex problems easier:

* `examples/advanced/GroceriesToBags.kt`
* `examples/advanced/ValuesToRanges.kt`
* `examples/advanced/WarmingCooling.kt`

#### Resetting flags: `keepValueThatTriggeredReset` and `repeatLastValueInNewSeries`

These two flags are explained in the following example: [`examples/basic/ResetterFlags.kt`]

# Consumers

All consumers, in alphabetical order.

### Always

Example:

```kotlin
        val actual = listOf(1, -1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 }
        )
        print(actual)
        assertEquals(listOf(false, false, true), actual)
```

Complete example: [`examples/consumers/AlwaysSometimesNever.kt`]

### AsList

Example:

```kotlin
        val actual = listOf(1, 2, 3)
            .consume(filterOn<Int> { it > 1 }.asList())
        assertEquals(listOf(2, 3), actual[0])
```

Complete example: [`examples/consumers/AsList.kt`]

### Averages

Example:

```kotlin
        val actual = (1..10).asSequence()
            .consume(
                avgOfInt(),
                mapTo { it: Int -> it.toLong() }.avgOfLong(),
                mapTo { it: Int -> BigDecimal.valueOf(it.toLong()) }.avgOfBigDecimal()
                )

        print(actual)

[Optional[5.50], Optional[5.50], Optional[5.50]]
```

Complete example: [`examples/consumers/MinMaxCountAvg.kt`].

### BottomBy and BottomNBy

In the following example we provide a `Comparator`, and find bottom one and bottom two two items, with possible ties:

```kotlin
        val comparator = { a: Thing, b: Thing -> a.quantity.compareTo(b.quantity) }
        val actual = things.consume(bottomBy(comparator), bottomNBy(2, comparator))

```

Complete example: [`examples/consumers/BottomN.kt`].

We can also project items to `Comparable` values, and find bottom values by that projection. In that case all we need to do is to provide a projection to `Comparable`. A built-in `Comparator` for that projection will be used:


```kotlin
        val projection = { a: Thing -> a.quantity }
        val actual = things.consume(bottomBy(projection), bottomNBy(2, projection))
```

Complete example: `examples/consumers/BottomN`

### Count

Example:

```kotlin
        val actual = (1..10).asSequence()
            .consume(count())                )

        print(actual)

[10]
```

Complete example: `examples/consumers/MinMaxCountAvg`

### First and FirstN

Example:

```kotlin
        val actual = (1..10).asSequence()
            .consume(First(), Last(), FirstN(2), LastN(2))

        print(actual)

        assertEquals(listOf(Optional.of(1), Optional.of(10), listOf(1, 2), listOf(9, 10)), actual)

[Optional[1], Optional[10], [1, 2], [9, 10]]
```

Complete example: `examples/consumers/FirstAndLast`

### Last and LastN

Example:

```kotlin
        val actual = (1..10).asSequence()
            .consume(First(), Last(), FirstN(2), LastN(2))

        print(actual)

        assertEquals(listOf(Optional.of(1), Optional.of(10), listOf(1, 2), listOf(9, 10)), actual)

[Optional[1], Optional[10], [1, 2], [9, 10]]
```

Complete example: `examples/consumers/FirstAndLast`

### Max

Example:

```kotlin
        val actual = (1..10).asSequence()
            .consume(min(), max())

        print(actual)

[Optional[1], Optional[10]]
```

Complete example: `examples/consumers/MinMaxCountAvg`

### Min

Example:

```kotlin
        val actual = (1..10).asSequence()
            .consume(min(), max())

        print(actual)

[Optional[1], Optional[10]]
```

Complete example: `examples/consumers/MinMaxCountAvg`

### Never

Example:

```kotlin
        val actual = listOf(1, -1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 }
        )
        print(actual)
        assertEquals(listOf(false, false, true), actual)
```

Complete example: `examples/consumers/AlwaysSometimesNever`

### RatioOf

Example:

```kotlin
        val actual = listOf(1, 2, 3).consume(ratioOf { it%2 == 0 })
        print(actual)

[Ratio2(conditionMet=1, outOf=3)]
```

Complete example: `examples/consumers/RatioOf`

### Sometimes

Example:

```kotlin
        val actual = listOf(1, -1).consume(
            never { it > 0 },
            always { it > 0 },
            sometimes { it > 0 }
        )
        print(actual)
        assertEquals(listOf(false, false, true), actual)
```

Complete example: `examples/consumers/AlwaysSometimesNever`


### Sum

Example:

```kotlin
        val actual = listOf(1, 2).consume(
            sumOfInt(),
            mapTo { it:Int -> it.toLong() }.toSumOfLong(),
            mapTo { it:Int -> BigDecimal.valueOf(it.toLong()) }.toSumOfBigDecimal())

        assertEquals(listOf(3, 3L, BigDecimal.valueOf(3L)), actual)
```

Complete example: `examples/consumers/SumExample`

### TopBy and TopNBy

In the following example we provide a `Comparator`, and find top one and top two two items, with possible ties:

```kotlin
        val comparator = { a: Thing, b: Thing -> a.quantity.compareTo(b.quantity) }
        val actual = things.consume(topBy(comparator), topNBy(2, comparator))

```

Complete example: `examples/consumers/TopN`

We can also project items to `Comparable` values, and find top values by that projection. In that case all we need to do is to provide a projection to `Comparable`. A built-in `Comparator` for that projection will be used:


```kotlin
        val projection = { a: Thing -> a.quantity }
        val actual = things.consume(topBy(projection), topNBy(2, projection))
```

Complete example: `examples/consumers/TopN`


## Dispatchers

Dispatchers pass incoming items to one or more consumers.

### AllOf

Pass every item to every consumer in the list.

Example:

```kotlin
       val actual = (1..10).asSequence()
            .consume(
                filterOn<Int> { it > 2 }
                    .mapTo { it * 2 }
                    .allOf(min(), max()))

        assertEquals(
            listOf(
                listOf(Optional.of(6), Optional.of(20))),
            actual)
```

Complete example: `examples/transformations/AllOfExample`
Another example with nested uses of `allOf``: `examples/transformations/AllOfNestedExample`


### Branch

Evaluate an item against a condition, pass it to one of two consumers.

Example:

```kotlin
        val leavingSpaceport = asList<Passenger>()
        val transferringToAnotherFlight = asList<Passenger>()

        passengers.consume(Branch({ it: Passenger -> it.destination == "Tattoine" },
            consumerForAccepted = leavingSpaceport,
            consumerForRejected = transferringToAnotherFlight))
```

Complete example: `examples/basics/Passengers`

### Group

Example:

```kotlin
        val actual = things
            .consume(
                groupBy(
                    keyFactory = { it: Thing -> it.color },
                    innerConsumerFactory = { count() }
                )
            )

        assertEquals(mapOf("Amber" to 2L, "Red" to 1L), actual[0])
```

Complete example: `examples/transformations/GroupsExample`
Advanced examples: `examples/basics/BasicGroups`


## Transformations

All transformations, in alphabetical order.

### Batch

Example:

```kotlin
        val actual = listOf(1, 2, 3)
            .consume(
                batches<Int>(batchSize = 2).asList()
            )
        assertEquals(listOf(listOf(1, 2), listOf(3)), actual[0])
```

Complete example: `examples/transformations/Batches`

Note: each batch is accumulated in a list, which is passed downstream only when it is completed. Alternatively, we can use resetting and consume batches of data without the need to materialize batches in lists.

### Filter

This is basic filtering, not different from the one in standard Kotlin library.

Example:

```kotlin
        val actual = (1..5).asSequence().consume(
            filterOn<Int> { it%2 == 0 }.asList()
        )

        assertEquals(listOf(2, 4), actual[0])
```

Complete example: `examples/transformations/FilterExample`

### First

Example:

```kotlin
        val actual = (0..10).asSequence()
            .consume(
                first<Int>(2).asList()
            )

        assertEquals(listOf(
            listOf(0, 1),
            actual)
```

Complete example: `examples/transformations/FirstSkipLastStep`


### KeepState

Passes items to a consumer which stores a state, and passes these items, unchanged, downstream to the next consumer.

Example:

```kotlin
        val maximum = max<Int>()
        val numbers = listOf(1, 3, 2, 4)
        val actual = numbers.consume(
            keepState(maximum)
                .peek { println("Processing item $it, state: ${maximum.results()}") }
                .asList()
        )

        assertEquals(numbers, actual[0], "Items are passed through peek unchanged")

Processing item 1, state: Optional[1]
Processing item 3, state: Optional[3]
Processing item 2, state: Optional[3]
Processing item 4, state: Optional[4]
```

Complete example: `examples/transformations/KeepStateExample`

### KeepStates

Passes items to several consumers which store several states, and also passes these items, unchanged, downstream to the next consumer.

Example:

```kotlin
        val minimum = min<Int>()
        val maximum = max<Int>()
        val numbers = listOf(2, 3, 1, 4)
        val actual = numbers.consume(
            keepStates(minimum, maximum)
                .peek { println("Processing item $it, minimum: ${minimum.results()}, maximum: ${maximum.results()}") }
                .asList()
        )

        assertEquals(numbers, actual[0], "Items are passed through peek unchanged")

Processing item 2, minimum: Optional[2], maximum: Optional[2]
Processing item 3, minimum: Optional[2], maximum: Optional[3]
Processing item 1, minimum: Optional[1], maximum: Optional[3]
Processing item 4, minimum: Optional[1], maximum: Optional[4]
```

Complete example: `examples/transformations/KeepSeveralStatesExample`


### Last

Example:

```kotlin
        val actual = (0..10).asSequence()
            .consume(
                last<Int>(2).asList(),
                skip<Int>(3).step(2).first(3).asList()
            )

        assertEquals(listOf(
            listOf(9, 10),
            listOf(4, 6, 8)),
            actual)
```

Complete example: `examples/transformations/FirstSkipLastStep`


### MapTo

Transform an incoming item into exactly one item.

Example:

```kotlin
        val names = orderItems.consume(
            mapTo<OrderItem, String> { it.name }.asList()
        )
        assertEquals(listOf("Apple", "Orange"), names[0])
```

Complete example: `examples/transformations/MapToExample`


### Peek

Same as `peek` in the standard library. Performs an action and passes an unchanged item downstream.

Example:

```kotlin
        (0..3).asSequence().consume(
            peek<Int> { println("Processing item $it") }.asList()
        )

Processing item 0
Processing item 1
Processing item 2
Processing item 3
```

Complete example: `examples/transformations/PeekExample`

### Skip

Example:

```kotlin
        val actual = (0..10).asSequence()
            .consume(
                skip<Int>(8).asList(),
                skip<Int>(3).step(2).first(3).asList()
            )

        assertEquals(listOf(
            listOf(8, 9, 10),
            listOf(4, 6, 8)),
            actual)
```

Complete example: `examples/transformations/FirstSkipLastStep`


### Step

Takes a slice out of a sequence.

Example:

```kotlin
        val actual = (0..10).asSequence()
            .consume(
                step<Int>(4).asList(),
                skip<Int>(3).step(2).first(3).asList()
            )

        assertEquals(listOf(
            listOf(3, 7),
            listOf(4, 6, 8)),
            actual)
```

Complete example: `examples/transformations/FirstSkipLastStep`

### TransformTo

Combines filtering and mapping in one step. Transforms an incoming item into a `Sequence` of outgoing items: one item, or several, or none at all.

Example:

```kotlin
    data class ShoppingListItem(val name: String, val quantity: Int)

        val shoppingList = listOf(
            ShoppingListItem("Apple", 2),
            ShoppingListItem("Orange", 1)
        )

        val actual = shoppingList.consume(
            peek<ShoppingListItem> { println("Processing $it") }
                .transformTo { item: ShoppingListItem ->
                    (1..item.quantity).asSequence().map { item.name } }
                .peek { println("Unpacked to $it") }
                .asList()
        )

Processing ShoppingListItem(name=Apple, quantity=2)
Unpacked to Apple
Unpacked to Apple
Processing ShoppingListItem(name=Orange, quantity=1)
Unpacked to Orange
```

Complete example: `examples/transformations/TransformationExample`

More advanced example: `examples/advanced/UnpackItems`

# Transforming results after consuming

By default, `consume` returns a `List<Any>`, as shown in te following example:

```kotlin
        val actual = (0..10).asSequence().consume(min(), max(), count())

        assertEquals(listOf(
                Optional.of(0),
                Optional.of(10),
                11L),
            actual)
```

Instead, we can develop a function to transform these results into something more structured, like an instance of a data class:

```kotlin

    private data class BasicStats(val min: Optional<Int>, val max: Optional<Int>, val count: Long)

    private fun resultsMapper(consumers: List<Consumer<Int>>) =
        BasicStats(
            min = (consumers[0] as Min<Int>).results(),
            max = (consumers[1] as Max<Int>).results(),
            count= (consumers[2] as Counter<Int>).results()
            )
```

We can provide this function along with a list of consumers:

```kotlin
        val actual = (0..10).asSequence().consume(
            {consumersList: List<Consumer<Int>> -> resultsMapper(consumersList) },
            min(), max(), count())

        assertEquals(
            BasicStats(Optional.of(0), Optional.of(10), 11L),
            actual)
```

Complete example: `examples/basics/TransformingResults`

# Extending Consumers

### Developing a new consumer

To develop a new `Consumer`, we need to implement the following interface:

```kotlin
interface Consumer<T> {
    fun process(value: T)
    fun results(): Any
    fun stop() {}
}
```

#### Basic implementation

The following example implements bitwise and:

```kotlin
class BitwiseAnd: Consumer<Int> {
    private var aggregate = Int.MAX_VALUE
    private var count = 0

    override fun process(value: Int) {
        aggregate = aggregate and value
        count++
    }

    override fun results(): Any = if(count == 0) 0 else aggregate

    override fun stop() {}
}
```

`BitwiseAnd` can be used like this:

```kotlin
        val actual = listOf(1, 3).consume(BitwiseAnd())
        assertEquals(1, actual[0])
```

To use `BitwiseAnd` after a transformation, we also need to develop an extension method as follows:

```kotlin
fun<T> ConsumerBuilder<T, Int>.bitwiseAnd() = this.build(BitwiseAnd())
```

**Note:** for more on `ConsumerBuilder`, refer to the next chapter, transformations.

This method can be used like this:

```kotlin
        val actual = listOf(1, 3).consume(filterOn<Int> { it>0 }.bitwiseAnd())
        assertEquals(1, actual[0])
```

Complete example: `examples/extending/NewConsumer`

Note that `BitwiseAnd` provides `stop()` that does nothing. In this case, there is no need to do anything `stop()`. Let us discuss a case when we need to do something meaningful in `stop()`.

#### Using `stop()`.

Let us discuss an example when we do need to do something meaningful in `stop()`.

`BatchSaverV1` accumulates incoming items in a buffer, and whenever the buffer reaches batch size, it saves that buffer in the database, as follows:

```kotlin
        override fun process(value: Int) {
            buffer.add(value)
            if(buffer.size == batchSize) {
                println("Saving buffer from process()")
                database.save(buffer)
                buffer.clear()
            }
        }
```

Instead of a real database we are using a fake one which just prints out the batch:

```kotlin
    private class FakeDatabase {
        fun save(batch: List<Int>) {
            println("Saving batch $batch")
        }
    }
```

Let us consume a sequence, and we shall see that the last incomplete batch is lost:

```kotlin
        (1..5).asSequence().consume(BatchSaverV1(3))

Saving buffer from process()
Saving batch [1, 2, 3]
```

To make sure that the last incomplete batch is not lost, we need to save it in `stop()`:

```kotlin
        override fun stop() {
            println("Saving buffer from stop()")
            database.save(buffer)
        }
```
When `consume()` is done iterating through all the items, it calls `stop()` against all the consumers. This allows the consumers to complete whatever they are doing, in this case, save the last incomplete buffer. As a result, the last incomplete batch, `[4,5]` is not lost:

```kotlin
        (1..5).asSequence().consume(BatchSaverV2(3))

Saving buffer from process()
Saving batch [1, 2, 3]
Saving buffer from stop()
Saving batch [4, 5]

```

Complete example: `examples/extending/LosingLastBatch`


### Developing a new transformation

Transformations implement the same interface as consumers: `Consumer`. They always must provide a meaningful implementation of `stop()`.

#### Basic example

The following simple transformation prints the incoming value and passes it downstream:

```kotlin
    private class Printer<T>(private val innerConsumer: Consumer<T>): Consumer<T> {
        override fun process(value: T) {
            print("Processing item $value\n")
            innerConsumer.process(value)
        }

        override fun results() = innerConsumer.results()

        override fun stop() { innerConsumer.stop() }
    }

    private class PrinterBuilder<T>: ConsumerBuilder<T, T> {
        override fun build(innerConsumer: Consumer<T>): Consumer<T> = Printer(innerConsumer)
    }

    private fun<T> print() = PrinterBuilder<T>()
```

That done, our simple transformation is ready to be the first in a chain of transformations and a consumer at the end:

```kotlin
        (0..2).asSequence().consume(print<Int>().asList())

Processing item 0
Processing item 1
Processing item 2
```

To be able to plug our simple transformation in the middle of the chain, we need to do the following:

```kotlin

    private class ChainedPrinterBuilder<T, V>(val previousBuilder: ConsumerBuilder<T, V>): ConsumerBuilder<T, V> {
        override fun build(innerConsumer: Consumer<V>): Consumer<T> = previousBuilder.build(Printer(innerConsumer))
    }

    private fun<T, V> ConsumerBuilder<T, V>.print(): ConsumerBuilder<T, V> = ChainedPrinterBuilder(this)
```

Now we are ready to use our new transformation anywhere, in this example after filtering:

```kotlin
        (0..2).asSequence().consume(filterOn<Int> { it>0 }.print().asList())

Processing item 1
Processing item 2
```

Complete example: `examples/extending/NewTransformation`

#### We must always implement `stop()`

A transformation must always pass `stop()` call downstream. The following example explains why: `examples/extending/LosingLastBatch`

## Learning by example

### Converting finishers' times to complete race results

Using two states to compute overall and age group place for race finishers.

Complete example: `examples/advanced/RaceResults`

### Splitting time series of temperature into increasing and decreasing subseries

Using a `Resetter` to split.

Complete example: `examples/advanced/WarmingCooling`

Note that in this example data points at which the trend changes from warming to cooling or vice versa, is included in both increasing and decreasing subseries.

### Putting groceries in bags

Demonstrates branching and splitting into subseries.

Complete example: `examples/advanced/GroceriesToBags`

### Coalescing time series of prices to to time ranges

Yet another example of resetting.

Complete example: `examples/advanced/ValuesToRanges`

### Divide heavy items into smaller chunks

Demonstrates use of transformations, filtering and mapping in one step. Also shows how one incoming item can be transformed into several.

Complete example: `examples/advanced/UnpackItems`

### Consecutive rainy days.

Demonstrates advanced use of resetting. Finds series of consecutive rainy days that meet several criteria, all at once.

Complete example: `examples/advanced/RainyDays`
