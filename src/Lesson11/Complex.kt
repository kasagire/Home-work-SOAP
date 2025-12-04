/**
 * Фабричный метод для создания комплексного числа из строки вида x+yi
 */
fun Complex(s: String): Complex {
    // Убираем пробелы и разбираем строку
    val str = s.replace(" ", "")

    // Определяем позицию знака мнимой части
    val lastPlus = str.lastIndexOf('+')
    val lastMinus = str.lastIndexOf('-')

    val signPos = if (lastPlus > lastMinus) lastPlus else lastMinus

    // Если знака нет
    return if (signPos <= 0) {
        // Проверяем, есть ли i в строке
        if (str.contains('i')) {
            // Только мнимая часть
            val imStr = str.replace("i", "")
            Complex(0.0, if (imStr.isEmpty() || imStr == "+") 1.0
            else if (imStr == "-") -1.0 else imStr.toDouble())
        } else {
            // Только вещественная часть
            Complex(str.toDouble(), 0.0)
        }
    } else {
        // Разделяем на вещественную и мнимую части
        val reStr = str.substring(0, signPos)
        val imStr = str.substring(signPos, str.length - 1) // без последнего символа 'i'

        val re = if (reStr.isEmpty()) 0.0 else reStr.toDouble()
        val im = if (imStr == "+") 1.0 else if (imStr == "-") -1.0 else imStr.toDouble()

        Complex(re, im)
    }
}

/**
 * Класс "комплексное число".
 *
 * Общая сложность задания -- лёгкая, общая ценность в баллах -- 8.
 * Объект класса -- комплексное число вида x+yi.
 * Про принципы работы с комплексными числами см. статью Википеды "Комплексное число".
 *
 * Аргументы конструктора -- вещественная и мнимая часть числа.
 */
class Complex(val re: Double, val im: Double) {

    /**
     * Конструктор из вещественного числа
     */
    constructor(x: Double) : this(x, 0.0)

    /**
     * Сложение.
     */
    operator fun plus(other: Complex): Complex =
        Complex(this.re + other.re, this.im + other.im)

    /**
     * Смена знака (у обеих частей числа)
     */
    operator fun unaryMinus(): Complex =
        Complex(-this.re, -this.im)

    /**
     * Вычитание
     */
    operator fun minus(other: Complex): Complex =
        this + (-other)

    /**
     * Умножение
     */
    operator fun times(other: Complex): Complex =
        Complex(
            this.re * other.re - this.im * other.im,
            this.re * other.im + this.im * other.re
        )

    /**
     * Деление
     */
    operator fun div(other: Complex): Complex {
        val denominator = other.re * other.re + other.im * other.im
        return Complex(
            (this.re * other.re + this.im * other.im) / denominator,
            (this.im * other.re - this.re * other.im) / denominator
        )
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Complex) return false

        // Сравниваем с заданной точностью из-за погрешностей Double
        val epsilon = 1e-10
        return Math.abs(this.re - other.re) < epsilon &&
                Math.abs(this.im - other.im) < epsilon
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String {
        return when {
            im == 0.0 -> "$re"
            re == 0.0 -> "${im}i"
            im < 0 -> "$re${im}i"
            else -> "$re+${im}i"
        }
    }
}
fun main() {
    // Создание комплексных чисел
    val c1 = Complex(2.0, 3.0)
    val c2 = Complex(1.0, -1.0)
    val c3 = Complex("2+3i")
    val c4 = Complex("4-2i")
    val c5 = Complex(5.0) // вещественное число

    println("c1 = $c1")
    println("c2 = $c2")
    println("c3 = $c3")
    println("c5 = $c5")

    // Арифметические операции
    println("c1 + c2 = ${c1 + c2}")
    println("c1 - c2 = ${c1 - c2}")
    println("c1 * c2 = ${c1 * c2}")
    println("c1 / c2 = ${c1 / c2}")
    println("-c1 = ${-c1}")

    // Сравнение
    println("c1 == c3: ${c1 == c3}")
}