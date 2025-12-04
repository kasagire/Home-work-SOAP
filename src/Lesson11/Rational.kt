import kotlin.math.abs

/**
 * Пример: класс "рациональное число"
 *
 * Объект класса представляет число M/N, где M - целое, N - целое положительное.
 * Дробь M/N удобно хранить в несократимом виде: GCD(M, N) = 1.
 * Рациональные числа можно складывать, вычитать, умножать, делить, сравнивать,
 * преобразовывать к целому или вещественному.
 */
class Rational private constructor() : Comparable<Rational> {
    val numerator: Int
    val denominator: Int

    // чтобы избежать конфликта конструкторов
    init {
        numerator = 0
        denominator = 1
    }

    // Приватный вспомогательный конструктор для инициализации полей
    private constructor(num: Int, den: Int, dummy: Boolean) : this() {

        val numeratorField = Rational::class.java.getDeclaredField("numerator")
        numeratorField.isAccessible = true
        numeratorField.setInt(this, num)

        val denominatorField = Rational::class.java.getDeclaredField("denominator")
        denominatorField.isAccessible = true
        denominatorField.setInt(this, den)
    }

    private tailrec fun gcd(a: Int, b: Int): Int =
        when {
            a == b || b == 0 -> a
            a == 0 -> b
            a > b -> gcd(a % b, b)
            else -> gcd(a, b % a)
        }

    companion object {
        /**
         * Создание рационального числа с нормализацией
         */
        private fun createNormalized(num: Int, den: Int): Rational {
            if (den == 0) throw ArithmeticException("Denominator cannot be zero")
            var gcdValue = gcd(num, den)
            if (den < 0) gcdValue = -gcdValue
            return Rational(num / gcdValue, den / gcdValue, true)
        }

        private fun gcd(a: Int, b: Int): Int {
            var x = abs(a)
            var y = abs(b)
            while (y != 0) {
                val temp = y
                y = x % y
                x = temp
            }
            return x
        }

        /**
         * Ноль как рациональное число
         */
        val ZERO = Rational(0, 1, true)

        /**
         * Один как рациональное число
         */
        val ONE = Rational(1, 1, true)

        /**
         * Создание рационального числа из строки
         */
        fun parse(s: String): Rational {
            val parts = s.trim().split("/")
            return when (parts.size) {
                1 -> {
                    val num = parts[0].toIntOrNull()
                        ?: throw NumberFormatException("Invalid rational number: $s")
                    createNormalized(num, 1)
                }
                2 -> {
                    val num = parts[0].toIntOrNull()
                        ?: throw NumberFormatException("Invalid numerator: ${parts[0]}")
                    val den = parts[1].toIntOrNull()
                        ?: throw NumberFormatException("Invalid denominator: ${parts[1]}")
                    createNormalized(num, den)
                }
                else -> throw NumberFormatException("Invalid rational number format: $s")
            }
        }
    }

    /**
     * Основной публичный конструктор
     */
    constructor(numerator: Int, denominator: Int) : this() {
        if (denominator == 0) throw ArithmeticException("Denominator cannot be zero")
        var gcdValue = gcd(abs(numerator), abs(denominator))
        if (denominator < 0) gcdValue = -gcdValue


        val numeratorField = Rational::class.java.getDeclaredField("numerator")
        numeratorField.isAccessible = true
        numeratorField.setInt(this, numerator / gcdValue)

        val denominatorField = Rational::class.java.getDeclaredField("denominator")
        denominatorField.isAccessible = true
        denominatorField.setInt(this, denominator / gcdValue)
    }

    /**
     * Конструктор из целого числа
     */
    constructor(i: Int) : this(i, 1)

    /**
     * Конструктор из строки вида "a/b" или "a"
     */
    constructor(s: String) : this() {
        val r = parse(s)

        // Используем рефлексию для установки final полей
        val numeratorField = Rational::class.java.getDeclaredField("numerator")
        numeratorField.isAccessible = true
        numeratorField.setInt(this, r.numerator)

        val denominatorField = Rational::class.java.getDeclaredField("denominator")
        denominatorField.isAccessible = true
        denominatorField.setInt(this, r.denominator)
    }

    /**
     * Абсолютное значение
     */
    fun abs(): Rational = createNormalized(abs(numerator), denominator)

    /**
     * Обратное значение
     */
    fun reciprocal(): Rational = createNormalized(denominator, numerator)

    /**
     * Возведение в целую степень
     */
    fun pow(exp: Int): Rational {
        return when {
            exp == 0 -> ONE
            exp > 0 -> {
                var num = 1
                var den = 1
                repeat(exp) {
                    num *= numerator
                    den *= denominator
                }
                createNormalized(num, den)
            }
            else -> reciprocal().pow(-exp)
        }
    }

    /**
     * Проверка, является ли число целым
     */
    fun isInteger(): Boolean = denominator == 1

    /**
     * Округление вниз
     */
    fun floor(): Int = numerator / denominator

    /**
     * Округление вверх
     */
    fun ceil(): Int = if (numerator % denominator == 0) {
        numerator / denominator
    } else {
        numerator / denominator + if (numerator > 0) 1 else 0
    }

    /**
     * Сложение
     */
    operator fun plus(other: Rational): Rational = createNormalized(
        numerator * other.denominator + denominator * other.numerator,
        denominator * other.denominator
    )

    /**
     * Сложение с целым числом
     */
    operator fun plus(other: Int): Rational = this + Rational(other)

    /**
     * Смена знака (Y = -X)
     */
    operator fun unaryMinus(): Rational = createNormalized(-numerator, denominator)

    /**
     * Вычитание
     */
    operator fun minus(other: Rational): Rational = this + (-other)

    /**
     * Вычитание целого числа
     */
    operator fun minus(other: Int): Rational = this - Rational(other)

    /**
     * Умножение
     */
    operator fun times(other: Rational): Rational =
        createNormalized(numerator * other.numerator, denominator * other.denominator)

    /**
     * Умножение на целое число
     */
    operator fun times(other: Int): Rational = this * Rational(other)

    /**
     * Деление
     */
    operator fun div(other: Rational): Rational =
        createNormalized(numerator * other.denominator, denominator * other.numerator)

    /**
     * Деление на целое число
     */
    operator fun div(other: Int): Rational = this / Rational(other)

    /**
     * Преобразование к целому
     */
    fun toInt(): Int = numerator / denominator

    /**
     * Преобразование к вещественному
     */
    fun toDouble(): Double = numerator.toDouble() / denominator

    /**
     * Преобразование к Float
     */
    fun toFloat(): Float = numerator.toFloat() / denominator

    /**
     * Сравнение на равенство (переопределяет Any.equals)
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Rational) return false
        return numerator == other.numerator && denominator == other.denominator
    }

    override fun hashCode(): Int {
        var result = numerator
        result = 31 * result + denominator
        return result
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String = if (denominator == 1) {
        numerator.toString()
    } else {
        "$numerator/$denominator"
    }

    /**
     * Сравнение на неравенство (переопределяет Comparable.compareTo)
     */
    override fun compareTo(other: Rational): Int {
        val diff = this - other
        return diff.numerator
    }
}

/**
 * Расширения для работы с рациональными числами
 */
operator fun Int.plus(r: Rational): Rational = Rational(this) + r
operator fun Int.minus(r: Rational): Rational = Rational(this) - r
operator fun Int.times(r: Rational): Rational = Rational(this) * r
operator fun Int.div(r: Rational): Rational = Rational(this) / r

/**
 * Функции сравнения Rational с Int
 */
fun Rational.equalsInt(other: Int): Boolean = this == Rational(other)
infix fun Rational.eq(other: Int): Boolean = this == Rational(other)
infix fun Rational.neq(other: Int): Boolean = this != Rational(other)
infix fun Rational.lt(other: Int): Boolean = this < Rational(other)
infix fun Rational.gt(other: Int): Boolean = this > Rational(other)
infix fun Rational.le(other: Int): Boolean = this <= Rational(other)
infix fun Rational.ge(other: Int): Boolean = this >= Rational(other)

/**
 * Примеры использования
 */
fun main() {
    // Создание рациональных чисел
    val r1 = Rational(1, 2)
    val r2 = Rational(3, 4)
    val r3 = Rational(2) // 2/1
    val r4 = Rational("3/5")
    val r5 = Rational("7")

    println("r1 = $r1") // 1/2
    println("r2 = $r2") // 3/4
    println("r3 = $r3") // 2
    println("r4 = $r4") // 3/5
    println("r5 = $r5") // 7

    println("r1.numerator = ${r1.numerator}, r1.denominator = ${r1.denominator}")

    // Арифметические операции
    println("r1 + r2 = ${r1 + r2}") // 5/4
    println("r1 - r2 = ${r1 - r2}") // -1/4
    println("r1 * r2 = ${r1 * r2}") // 3/8
    println("r1 / r2 = ${r1 / r2}") // 2/3

    // Операции с целыми числами
    println("r1 + 2 = ${r1 + 2}") // 5/2
    println("3 * r2 = ${3 * r2}") // 9/4
    println("2 + r1 = ${2 + r1}") // 5/2

    // Сравнение
    println("r1 < r2: ${r1 < r2}") // true
    println("r1 == Rational(2, 4): ${r1 == Rational(2, 4)}") // true
    println("r3 eq 2: ${r3 eq 2}") // true
    println("r3.equalsInt(2): ${r3.equalsInt(2)}") // true

    // Преобразования
    println("r2.toDouble() = ${r2.toDouble()}") // 0.75
    println("r2.toInt() = ${r2.toInt()}") // 0
    println("r3.toInt() = ${r3.toInt()}") // 2

    // Дополнительные функции
    println("r1.reciprocal() = ${r1.reciprocal()}") // 2
    println("r1.abs() = ${r1.abs()}") // 1/2
    println("Rational(-3, 4).abs() = ${Rational(-3, 4).abs()}") // 3/4
    println("r2.isInteger() = ${r2.isInteger()}") // false
    println("r3.isInteger() = ${r3.isInteger()}") // true

    // Округление
    println("Rational(7, 3).floor() = ${Rational(7, 3).floor()}") // 2
    println("Rational(7, 3).ceil() = ${Rational(7, 3).ceil()}") // 3
    println("Rational(-7, 3).floor() = ${Rational(-7, 3).floor()}") // -3
    println("Rational(-7, 3).ceil() = ${Rational(-7, 3).ceil()}") // -2

    // Возведение в степень
    println("Rational(2, 3).pow(2) = ${Rational(2, 3).pow(2)}") // 4/9
    println("Rational(2, 3).pow(-1) = ${Rational(2, 3).pow(-1)}") // 3/2
    println("Rational(2, 3).pow(0) = ${Rational(2, 3).pow(0)}") // 1

    // Константы
    println("Rational.ZERO = ${Rational.ZERO}") // 0
    println("Rational.ONE = ${Rational.ONE}") // 1

    // Использование parse
    println("Rational.parse(\"5/10\") = ${Rational.parse("5/10")}") // 1/2
    println("Rational.parse(\"8\") = ${Rational.parse("8")}") // 8
}