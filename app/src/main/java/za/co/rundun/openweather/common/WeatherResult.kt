package za.co.rundun.openweather.common

sealed class WeatherResult<out E, out V> {

    data class Value<out V>(val value: V) : WeatherResult<Nothing, V>()
    data class Error<out E>(val error: E) : WeatherResult<E, Nothing>()

    companion object Factory {
        inline fun <V> build(function: () -> V): WeatherResult<Exception, V> =
            try {
                Value(function.invoke())
            } catch (e: java.lang.Exception) {
                Error(e)
            }
    }

}