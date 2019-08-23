# konsumers

Advanced work with Kotlin sequences. Developed to improve performance in cases when iterating the sequence and/or transforming its items is slow.

* Pure Kotlin.
* Easy to use and extend.
* Powerful control-of-flow abilities.
* A rich set of filtering, mapping, and other transformations.
* Compute multiple results while iterating a sequence once, boosting performance.

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

For a complete working example, refer to `examples/basics/BranchingAfterTransformation.kt`, test named `reuses filtering`.

Likewise, we can apply one slow mapping to several consumers:

```kotlin
        val minTemperature = min<Int>()

        val maxTemperature = max<Int>()

        val dayCount = counter<DailyWeather>()

        val verySlowMapping = mapTo<DailyWeather, Int> { it -> it.low }

        val allResults = dailyWeather.consume(
            verySlowMapping.allOf(minTemperature, maxTemperature),
            dayCount)
```

For a complete working example, refer to `examples/basics/BranchingAfterTransformation.kt`, test named `reuses mapping`.

Calls to `allOf` can be nested, as shown in the following example:

```kotlin
        val minTemperature = min<Int>()

        val maxTemperature = max<Int>()

        val rainyDaysCount = counter<DailyWeather>()

        val allDaysCount = counter<DailyWeather>()

        val verySlowFilter = filterOn<DailyWeather> { it -> it.rainAmount > BigDecimal.ZERO }
        val verySlowMapping = mapTo<DailyWeather, Int> { it -> it.low }

        val allResults = dailyWeather.consume(
            verySlowFilter.allOf(
                verySlowMapping.allOf(minTemperature, maxTemperature),
                rainyDaysCount),
            allDaysCount)
```

For a complete working example, refer to `examples/basics/BranchingAfterTransformation.kt`, test named `nested calls`.

[Complete list of consumers](#consumers)

[Complete list of transformations](#transformations)

[Implementing your own consumer](#implementing-your-own-consumer)

