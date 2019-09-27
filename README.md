# konsumers

Advanced work with Kotlin sequences. Developed to improve performance in cases when iterating the sequence and/or transforming its items is slow.

* Practical library designed to solve real-world problems.
* Pure Kotlin.
* Improves performance by doing less sequence iterations and less transformations.
* A rich set of filtering, mapping, and other transformations.
* Easy to use and etend.

## Basics

### Computing multiple results while iterating a sequence once.

The following example iterates over daily weather data once, and computes the following:

* the coldest days
* the warmest sunny days
* number of days

```kotlin
        val highestTemperatureOnSunnyDay = filterOn<DailyWeather> { it.weatherType == WeatherType.Sunny }
            .mapTo { it -> it.high }
            .max()

        val lowestTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val dayCount = counter<DailyWeather>()

        val allResults = dailyWeather.consume(highestTemperatureOnSunnyDay, lowestTemperature, dayCount)

        //one day with high=75
        println(highestTemperatureOnSunnyDay.results())
        //two days with low=20
        println(lowestTemperature.results())
        //twelve days
        println(dayCount.results())
        println(allResults)

Optional[75]
Optional[20]
12
[Optional[75], Optional[20], 12]

```

For a complete working example, refer to `examples/basics/MultipleResultsAtOnce.kt`.

### Reusing one filtering or mapping in multiple consumers

The following example shows how to apply a slow filtering condition to three consumers, `lowestLowTemperature`, `lowestHighTemperature`, and `rainyDaysCount`:

```kotlin
        val lowestLowTemperature = mapTo<DailyWeather, Int> { it -> it.low }
            .min()

        val lowestHighTemperature = mapTo<DailyWeather, Int> { it -> it.high }
            .min()

        val rainyDaysCount = counter<DailyWeather>()

        val dayCount = counter<DailyWeather>()

        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }

        val allResults = dailyWeather.consume(
            verySlowFilter.allOf(lowestLowTemperature, lowestHighTemperature, rainyDaysCount),
            dayCount)
```

For a complete working example, refer to `examples/basics/BranchingAfterTransformation.kt`.

### Process filtered out items

When we process a sequence and filter out items, we may also need to process rejected items by another consumer.
To improve performance, we can use `branchOn` to compute a filter condition only once, and process both accepted and rejected items by two different consumers.
In the following example , we process a sequence of temperature readings, compute min and max when the temperature is in acceptable range, and alert when the temperature is too high:

```kotlin
        val alertOnVeryHighTemperature = asList<Int>()
        val minimumTemperature = min<Int>()
        val maximumTemperature = max<Int>()
        val sut = branchOn(condition = { a: Int -> a < 200 },
            consumerForRejected = alertOnVeryHighTemperature).allOf(minimumTemperature, maximumTemperature)
        val veryHighTemperature = 201
        val actual = listOf(75, 82, 55, veryHighTemperature, 74).consume(sut)
        println("minimumTemperature: ${minimumTemperature.results()}")
        println("maximumTemperature: ${maximumTemperature.results()}")
        println("Rejected items: ${alertOnVeryHighTemperature.results()}")

minimumTemperature: Optional[55]
maximumTemperature: Optional[82]
Rejected items: [201]
```

For a complete working example, refer to `examples/basics/IfOrElse.kt`.

### Advanced transformations

The following advantages will be discussed below, one example at a time.

* Filters can have a state, which allows for easy solution to many common problems.
* Mappings also can have a state, with same benefits.
* Mappings do not have to produce an outgoing value for every incoming one, allowing us to solve problems without creating many short-lived objects.
* In general, transformations process an incoming value, and provide one outgoing value, or several, or none at all.

#### Stateful filters.
In the following example we are processing a sequence of bank account deposits and withdrawals, and our filter makes sure that the account balance is never negative. The filter has a state which stores the current account balance, which allows for an easy solution.

```kotlin
fun nonNegativeBalance(): Condition<BigDecimal> =
    ConditionOnState(state = sumOfBigDecimal()) { currentSum: BigDecimal, change: BigDecimal -> (currentSum + change) >= BigDecimal.ZERO }

(snip)

        val changeToReject = BigDecimal("-2")
        val changes = listOf(BigDecimal.ONE, BigDecimal("-1"), changeToReject, BigDecimal.ONE)
        val condition = nonNegativeBalance()
        val acceptedChanges = changes.consume(filterOn(condition).asList())[0]
        assertEquals(listOf(BigDecimal.ONE, BigDecimal("-1"), BigDecimal.ONE), acceptedChanges)
```

For a complete working example, refer to `examples/basics/StatefulFilter.kt`.

#### Stateful mappings.

In the following example we are converting a sequence of bank account deposits and withdrawals into a sequence of current balances. The mapping has a state which stores the current account balance, which allows for an easy solution.

```kotlin
class CurrentBalanceConsumerBuilder(): ConsumerBuilder<BigDecimal, BigDecimal> {
    override fun build(innerConsumer: Consumer<BigDecimal>): Consumer<BigDecimal> = TransformationWithState(state = sumOfBigDecimal(),
        condition = { currentSum: BigDecimal, change: BigDecimal -> (currentSum + change) >= BigDecimal.ZERO},
        transformation = {stateValue: BigDecimal, incomingValue: BigDecimal -> sequenceOf(stateValue)},
        innerConsumer = innerConsumer
    )
}

fun toCurrentBalance() = CurrentBalanceConsumerBuilder()

(snip)

        val changes = listOf(BigDecimal.TEN, BigDecimal("-1"), BigDecimal("-1"), BigDecimal.ONE)
        val currentBalance = changes.consume(toCurrentBalance().asList())[0]
        assertEquals(listOf(BigDecimal.TEN, BigDecimal("9"), BigDecimal("8"), BigDecimal("9")), currentBalance)
```

For a complete working example, refer to `examples/basics/StatefulMapping.kt`.

#### Combining mapping and filtering in one transformation.

Typically a `filter` will accept or reject items without transforming them, and a `map` must produce a transformed item for every incoming one.

Sometimes this approach means that we have to produce a lot of short-lived objects. For example, suppose that whenever more than a half of the amount on a bank account is withdrawn at once, we need to do something, such as trigger an alert. Traditionally, we would:
* map an incoming transaction amount into an instance of another class with two fields, `(previousBalance, transactionAmount)`
* filter these instances
* alert

This is demonstrated in the following example:

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

We can both filter and transform in the same transformation, eliminating the need to create short-lived-objects, as follows:

```kotlin
        val amounts = listOf(BigDecimal(100), BigDecimal(-10), BigDecimal(-1), BigDecimal(-50))
        val largeWithdrawals = amounts.consume(toLargeWithdrawal()
            .peek { println("After mapping and filtering: $it") }
            .asList())

After mapping and filtering: TransactionWithCurrentBalance(currentBalance=39, amount=-50)
```
For a complete working example, refer to `examples/basics/LargeWithdrawals.kt`.

### Grouping and Resetting

#### Basic Grouping

We can group items by any key, which is equivalent to the standard function `associateBy``. Unlike in previous examples, we do not provide a consumer Instead, we provide a lambda that creates consumers as needed. Here is a basic example:

```kotlin
        val things = listOf(Thing("Amber", "Circle"),
            Thing("Amber", "Square"),
            Thing("Red", "Oval"))

        val actual = things
                .consume(groupBy(keyFactory =  { it: Thing -> it.color },
                    innerConsumerFactory = { counter() }))
        assertEquals(mapOf("Amber" to 2L, "Red" to 1L), actual[0])
```

For a complete working example, refer to `examples/basics/BasicGroups.kt`.

#### Grouping with multiple consumers

After grouping by a key, we can submit values to more than one consumer:

```kotlin
        val actual = things
            .consume(groupBy(keyFactory =  { it: Thing -> it.color },
                innerConsumerFactory = { allOf(counter(), mapTo { it: Thing -> it.shape }.asList()) }))
        assertEquals(mapOf("Amber" to listOf(2L, listOf("Circle", "Square")), "Red" to listOf(1L, listOf("Oval"))), actual[0])
```

For a complete working example, refer to `examples/basics/BasicGroups.kt`.

#### Nested groups

Groups can be nested In the following example we group things by color, then group by shape:

```kotlin
        val actual = things.consume(groupBy(keyFactory = { a: Thing -> a.color },
            innerConsumerFactory = {
                allOf(counter(), groupBy(keyFactory = { a: Thing -> a.shape },
                    innerConsumerFactory = { allOf(counter()) }))
            })
        )
```

For a complete working example, refer to `examples/basics/BasicGroups.kt`.

#### Why resetting?

Suppose that we are consuming a time series of weather readings, as follows:

#### Resetting
[Complete list of consumers](#consumers)

[Complete list of transformations](#transformations)

[Implementing your own consumer](#implementing-your-own-consumer)

