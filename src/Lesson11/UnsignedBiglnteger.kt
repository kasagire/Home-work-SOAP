import java.math.BigInteger
/**
 * Класс "беззнаковое большое целое число".
 *
 * Общая сложность задания -- очень сложная, общая ценность в баллах -- 32.
 * Объект класса содержит целое число без знака произвольного размера
 * и поддерживает основные операции над такими числами, а именно:
 * сложение, вычитание (при вычитании большего числа из меньшего бросается исключение),
 * умножение, деление, остаток от деления,
 * преобразование в строку/из строки, преобразование в целое/из целого,
 * сравнение на равенство и неравенство
 */
class UnsignedBigInteger(private val value: BigInteger) : Comparable<UnsignedBigInteger> {

    init {
        if (value < BigInteger.ZERO) {
            throw IllegalArgumentException("UnsignedBigInteger cannot be negative")
        }
    }

    /**
     * Конструктор из строки
     */
    constructor(s: String) : this(BigInteger(s).also {
        if (it < BigInteger.ZERO) throw NumberFormatException("Negative number: $s")
    })

    /**
     * Конструктор из целого
     */
    constructor(i: Int) : this(BigInteger.valueOf(i.toLong()).also {
        if (i < 0) throw IllegalArgumentException("Negative number: $i")
    })

    /**
     * Конструктор из Long
     */
    constructor(l: Long) : this(BigInteger.valueOf(l).also {
        if (l < 0) throw IllegalArgumentException("Negative number: $l")
    })
    /**
     * Сложение
     */
    operator fun plus(other: UnsignedBigInteger): UnsignedBigInteger =
        UnsignedBigInteger(this.value + other.value)
    /**
     * Вычитание (бросить ArithmeticException, если this < other)
     */
    operator fun minus(other: UnsignedBigInteger): UnsignedBigInteger {
        if (this < other) {
            throw ArithmeticException("Cannot subtract larger number from smaller")
        }
        return UnsignedBigInteger(this.value - other.value)
    }
    /**
     * Умножение
     */
    operator fun times(other: UnsignedBigInteger): UnsignedBigInteger =
        UnsignedBigInteger(this.value * other.value)
    /**
     * Деление
     */
    operator fun div(other: UnsignedBigInteger): UnsignedBigInteger {
        if (other.value == BigInteger.ZERO) {
            throw ArithmeticException("Division by zero")
        }
        return UnsignedBigInteger(this.value / other.value)
    }
    /**
     * Взятие остатка
     */
    operator fun rem(other: UnsignedBigInteger): UnsignedBigInteger {
        if (other.value == BigInteger.ZERO) {
            throw ArithmeticException("Division by zero")
        }
        return UnsignedBigInteger(this.value % other.value)
    }
    /**
     * Сравнение на равенство (по контракту Any.equals)
     */
    override fun equals(other: Any?): Boolean =
        this === other || (other is UnsignedBigInteger && this.value == other.value)
    /**
     * Сравнение на больше/меньше (по контракту Comparable.compareTo)
     */
    override fun compareTo(other: UnsignedBigInteger): Int =
        this.value.compareTo(other.value)
    /**
     * Преобразование в строку
     */
    override fun toString(): String = value.toString()
    /**
     * Преобразование в целое
     * Если число не влезает в диапазон Int, бросить ArithmeticException
     */
    fun toInt(): Int {
        val maxInt = BigInteger.valueOf(Int.MAX_VALUE.toLong())
        if (value > maxInt) {
            throw ArithmeticException("Number too large for Int")
        }
        return value.toInt()
    }

    override fun hashCode(): Int = value.hashCode()

    companion object {
        val ZERO = UnsignedBigInteger(0)
        val ONE = UnsignedBigInteger(1)
    }
}

fun main() {
    // Создание чисел
    val num1 = UnsignedBigInteger("12345678901234567890")
    val num2 = UnsignedBigInteger("9876543210")
    val num3 = UnsignedBigInteger(12345)

    println("num1 = $num1")
    println("num2 = $num2")
    println("num3 = $num3")

    // Арифметические операции
    println("num1 + num2 = ${num1 + num2}")
    println("num1 - num2 = ${num1 - num2}")
    println("num1 * num2 = ${num1 * num2}")
    println("num1 / num2 = ${num1 / num2}")
    println("num1 % num2 = ${num1 % num2}")

    // Сравнение
    println("num1 > num2: ${num1 > num2}")
    println("num1 == num1: ${num1 == UnsignedBigInteger("12345678901234567890")}")

    // Преобразование
    println("num3.toInt() = ${num3.toInt()}")

    try {
        println("num1.toInt() = ${num1.toInt()}")
    } catch (e: ArithmeticException) {
        println("Ошибка: ${e.message}")
    }

    try {
        val small = UnsignedBigInteger("10")
        val large = UnsignedBigInteger("20")
        println("small - large = ${small - large}")
    } catch (e: ArithmeticException) {
        println("Ошибка вычитания: ${e.message}")
    }
}