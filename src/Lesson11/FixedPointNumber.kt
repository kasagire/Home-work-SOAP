
/**
 * Класс "вещественное число с фиксированной точкой"
 *
 * Общая сложность задания - сложная, общая ценность в баллах -- 20.
 * Объект класса - вещественное число с заданным числом десятичных цифр после запятой (precision, точность).
 * Например, для ограничения в три знака это может быть число 1.234 или -987654.321.
 * Числа можно складывать, вычитать, умножать, делить
 * (при этом точность результата выбирается как наибольшая точность аргументов),
 * а также сравнить на равенство и больше/меньше, преобразовывать в строку и тип Double.
 *
 * Вы можете сами выбрать, как хранить число в памяти
 * (в виде строки, целого числа, двух целых чисел и т.д.).
 * Представление числа должно позволять хранить числа с общим числом десятичных цифр не менее 9.
*/
class FixedPointNumber : Comparable<FixedPointNumber> {
    /**
     * Храним число как целое, умноженное на 10^precision
     */
    private val scaledValue: Long

    /**
     * Точность - число десятичных цифр после запятой.
     */
    val precision: Int

    /**
     * Множитель для текущей точности
     */
    private val scaleFactor: Long

    private constructor(scaledValue: Long, precision: Int) {
        this.scaledValue = scaledValue
        this.precision = precision
        this.scaleFactor = pow10(precision)
    }

    /**
     * Конструктор из строки
     */
    constructor(s: String) {
        val trimmed = s.trim()

        if (!trimmed.matches(Regex("-?\\d+(\\.\\d+)?"))) {
            throw NumberFormatException("Некорректный формат числа: $s")
        }

        val hasDecimal = trimmed.contains('.')
        if (hasDecimal) {
            val parts = trimmed.split('.')
            val integerPart = parts[0]
            val decimalPart = parts[1]

            if (decimalPart.length > 9 || integerPart.replace("-", "").length + decimalPart.length > 9) {
                throw NumberFormatException("Слишком много цифр: $s")
            }

            this.precision = decimalPart.length
            this.scaleFactor = pow10(precision)

            val integer = integerPart.toLong()
            val decimal = decimalPart.toLong()

            this.scaledValue = if (integer >= 0) {
                integer * scaleFactor + decimal
            } else {
                integer * scaleFactor - decimal
            }
        } else {
            if (trimmed.replace("-", "").length > 9) {
                throw NumberFormatException("Слишком много цифр: $s")
            }

            this.precision = 0
            this.scaleFactor = 1L
            this.scaledValue = trimmed.toLong()
        }
    }

    /**
     * Конструктор из вещественного числа с заданной точностью
     */
    constructor(d: Double, p: Int) {
        if (p < 0 || p > 9) {
            throw IllegalArgumentException("Точность должна быть от 0 до 9: $p")
        }

        this.precision = p
        this.scaleFactor = pow10(p)
        // Используем математическое округление
        this.scaledValue = if (d >= 0) {
            (d * scaleFactor + 0.5).toLong()
        } else {
            (d * scaleFactor - 0.5).toLong()
        }
    }

    /**
     * Конструктор из целого числа
     */
    constructor(i: Int) {
        this.precision = 0
        this.scaleFactor = 1L
        this.scaledValue = i.toLong()
    }

    /**
     * Возведение 10 в степень (до 9)
     */
    private fun pow10(power: Int): Long {
        return when (power) {
            0 -> 1L
            1 -> 10L
            2 -> 100L
            3 -> 1000L
            4 -> 10000L
            5 -> 100000L
            6 -> 1000000L
            7 -> 10000000L
            8 -> 100000000L
            9 -> 1000000000L
            else -> throw IllegalArgumentException("Точность не может быть больше 9")
        }
    }

    /**
     * Приведение к общей точности
     */
    private fun rescaleTo(newPrecision: Int): Long {
        return when {
            newPrecision > precision -> {
                scaledValue * pow10(newPrecision - precision)
            }
            newPrecision < precision -> {
                // Округляем при уменьшении точности
                val factor = pow10(precision - newPrecision)
                val half = if (scaledValue >= 0) factor / 2 else -factor / 2
                (scaledValue + half) / factor
            }
            else -> scaledValue
        }
    }

    /**
     * Сложение
     *  Здесь и в других бинарных операциях
     * точность результата выбирается как наибольшая точность аргументов.
     * Лишние знаки отрбрасываются, число округляется по правилам арифметики.
     */
    operator fun plus(other: FixedPointNumber): FixedPointNumber {
        val resultPrecision = maxOf(this.precision, other.precision)
        val thisScaled = this.rescaleTo(resultPrecision)
        val otherScaled = other.rescaleTo(resultPrecision)

        return FixedPointNumber(thisScaled + otherScaled, resultPrecision)
    }

    /**
     * Смена знака
     */
    operator fun unaryMinus(): FixedPointNumber {
        return FixedPointNumber(-this.scaledValue, this.precision)
    }

    /**
     * Вычитание
     */
    operator fun minus(other: FixedPointNumber): FixedPointNumber {
        val resultPrecision = maxOf(this.precision, other.precision)
        val thisScaled = this.rescaleTo(resultPrecision)
        val otherScaled = other.rescaleTo(resultPrecision)

        return FixedPointNumber(thisScaled - otherScaled, resultPrecision)
    }

    /**
     * Умножение
     */
    operator fun times(other: FixedPointNumber): FixedPointNumber {
        val resultPrecision = maxOf(this.precision, other.precision)

        val tempPrecision = this.precision + other.precision

        if (tempPrecision > 18) {
            // Используем Double для очень больших чисел
            val result = (this.scaledValue.toDouble() * other.scaledValue) /
                    pow10(this.precision + other.precision - resultPrecision).toDouble()
            return FixedPointNumber(result.toLong(), resultPrecision)
        }

        // Для чисел в пределах Long
        val multiplication = this.scaledValue.toBigInteger() * other.scaledValue.toBigInteger()
        val divisor = pow10(this.precision + other.precision - resultPrecision).toBigInteger()
        val resultScaled = (multiplication / divisor).toLong()

        return FixedPointNumber(resultScaled, resultPrecision)
    }

    /**
     * Деление
     */
    operator fun div(other: FixedPointNumber): FixedPointNumber {
        if (other.scaledValue == 0L) {
            throw ArithmeticException("Деление на ноль")
        }

        val resultPrecision = maxOf(this.precision, other.precision)
        // Увеличиваем точность для деления, чтобы избежать потери точности
        val extraPrecision = 4
        val totalPrecision = resultPrecision + extraPrecision

        // Приводим к увеличенной точности
        val thisScaled = this.scaledValue.toDouble() * pow10(totalPrecision - this.precision).toDouble()
        val otherScaled = other.scaledValue.toDouble() * pow10(totalPrecision - other.precision).toDouble()

        val resultDouble = thisScaled / otherScaled
        val resultLong = if (resultDouble >= 0) {
            (resultDouble + 0.5).toLong()
        } else {
            (resultDouble - 0.5).toLong()
        }

        return FixedPointNumber(resultLong, totalPrecision)
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FixedPointNumber) return false

        val maxPrecision = maxOf(this.precision, other.precision)
        return this.rescaleTo(maxPrecision) == other.rescaleTo(maxPrecision)
    }

    /**
     * Сравнение на больше/меньше
     */
    override fun compareTo(other: FixedPointNumber): Int {
        val maxPrecision = maxOf(this.precision, other.precision)
        val thisScaled = this.rescaleTo(maxPrecision)
        val otherScaled = other.rescaleTo(maxPrecision)

        return thisScaled.compareTo(otherScaled)
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String {
        if (precision == 0) {
            return scaledValue.toString()
        }

        val sign = if (scaledValue < 0) "-" else ""
        val absValue = Math.abs(scaledValue)
        val integerPart = absValue / scaleFactor
        val decimalPart = absValue % scaleFactor

        val decimalStr = decimalPart.toString().padStart(precision, '0')
        // Убираем незначащие нули в конце
        val trimmedDecimal = decimalStr.trimEnd('0')
        return if (trimmedDecimal.isEmpty()) {
            "$sign$integerPart"
        } else {
            "$sign$integerPart.$trimmedDecimal"
        }
    }

    /**
     * Преобразование к вещественному числу
     */
    fun toDouble(): Double {
        return scaledValue.toDouble() / scaleFactor.toDouble()
    }

    override fun hashCode(): Int {
        // Приводим к стандартной точности для хэширования
        val standardPrecision = minOf(this.precision, 6)
        return rescaleTo(standardPrecision).hashCode()
    }
}
fun main() {
    // Создание чисел
    val num1 = FixedPointNumber("123.456")
    val num2 = FixedPointNumber(78.901, 3)
    val num3 = FixedPointNumber(100)

    println("num1 = $num1, precision = ${num1.precision}") // 123.456, precision = 3
    println("num2 = $num2, precision = ${num2.precision}") // 78.901, precision = 3
    println("num3 = $num3, precision = ${num3.precision}") // 100, precision = 0

    // Арифметические операции
    println("num1 + num2 = ${num1 + num2}") // 202.357
    println("num1 - num2 = ${num1 - num2}") // 44.555
    println("num1 * num2 = ${num1 * num2}") // 9740.553
    println("num1 / num2 = ${num1 / num2}") // 1.565

    // Сравнение
    println("num1 > num2: ${num1 > num2}") // true
    println("num1 == FixedPointNumber(\"123.456\"): ${num1 == FixedPointNumber("123.456")}") // true

    // Преобразование
    println("num1.toDouble() = ${num1.toDouble()}") // 123.456
}