import kotlin.math.abs

/**
 * Класс "полином с вещественными коэффициентами".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса -- полином от одной переменной (x) вида 7x^4+3x^3-6x^2+x-8.
 * Количество слагаемых неограничено.
 *
 * Полиномы можно складывать -- (x^2+3x+2) + (x^3-2x^2-x+4) = x^3-x^2+2x+6,
 * вычитать -- (x^3-2x^2-x+4) - (x^2+3x+2) = x^3-3x^2-4x+2,
 * умножать -- (x^2+3x+2) * (x^3-2x^2-x+4) = x^5+x^4-5x^3-3x^2+10x+8,
 * делить с остатком -- (x^3-2x^2-x+4) / (x^2+3x+2) = x-5, остаток 12x+16
 * вычислять значение при заданном x: при x=5 (x^2+3x+2) = 42.
 *
 * В конструктор полинома передаются его коэффициенты, начиная со старшего.
 * Нули в середине и в конце пропускаться не должны, например: x^3+2x+1 --> Polynom(1.0, 2.0, 0.0, 1.0)
 * Старшие коэффициенты, равные нулю, игнорировать, например Polynom(0.0, 0.0, 5.0, 3.0) соответствует 5x+3
 */
class Polynom(vararg coeffs: Double) {

    // Коэффициенты полинома: coeffs[0] - коэффициент при старшей степени
    private val coefficients: DoubleArray

    init {
        // Убираем ведущие нули
        var firstNonZero = 0
        while (firstNonZero < coeffs.size && abs(coeffs[firstNonZero]) < EPS) {
            firstNonZero++
        }

        if (firstNonZero == coeffs.size) {
            // Все коэффициенты нулевые
            coefficients = doubleArrayOf(0.0)
        } else {
            coefficients = coeffs.copyOfRange(firstNonZero, coeffs.size)
        }
    }

    companion object {
        private const val EPS = 1e-10
    }

    /**
     * Геттер: вернуть значение коэффициента при x^i
     */
    fun coeff(i: Int): Double {
        if (i < 0) throw IllegalArgumentException("Степень не может быть отрицательной: $i")

        val degree = degree()
        return if (i > degree) {
            0.0
        } else {
            val index = degree - i
            if (index >= 0 && index < coefficients.size) coefficients[index] else 0.0
        }
    }

    /**
     * Расчёт значения при заданном x
     */
    fun getValue(x: Double): Double {
        // Используем схему Горнера
        var result = 0.0
        for (coeff in coefficients) {
            result = result * x + coeff
        }
        return result
    }

    /**
     * Степень (максимальная степень x при ненулевом слагаемом, например 2 для x^2+x+1).
     *
     * Степень полинома с нулевыми коэффициентами считать равной 0.
     * Слагаемые с нулевыми коэффициентами игнорировать, т.е.
     * степень 0x^2+0x+2 также равна 0.
     */
    fun degree(): Int {
        return coefficients.size - 1
    }

    /**
     * Сложение
     */
    operator fun plus(other: Polynom): Polynom {
        val maxDegree = maxOf(this.degree(), other.degree())
        val resultCoeffs = DoubleArray(maxDegree + 1) { 0.0 }

        for (i in 0..maxDegree) {
            resultCoeffs[maxDegree - i] = this.coeff(i) + other.coeff(i)
        }

        return Polynom(*resultCoeffs)
    }

    /**
     * Смена знака (при всех слагаемых)
     */
    operator fun unaryMinus(): Polynom {
        val negated = coefficients.map { -it }.toDoubleArray()
        return Polynom(*negated)
    }

    /**
     * Вычитание
     */
    operator fun minus(other: Polynom): Polynom {
        return this + (-other)
    }

    /**
     * Умножение
     */
    operator fun times(other: Polynom): Polynom {
        val thisDegree = this.degree()
        val otherDegree = other.degree()
        val resultDegree = thisDegree + otherDegree
        val resultCoeffs = DoubleArray(resultDegree + 1) { 0.0 }

        for (i in 0..thisDegree) {
            for (j in 0..otherDegree) {
                resultCoeffs[resultDegree - (i + j)] += this.coeff(i) * other.coeff(j)
            }
        }

        return Polynom(*resultCoeffs)
    }

    /**
     * Деление
     *
     * Про операции деления и взятия остатка см. статью Википедии
     * "Деление многочленов столбиком". Основные свойства:
     *
     * Если A / B = C и A % B = D, то A = B * C + D и степень D меньше степени B
     */
    operator fun div(other: Polynom): Polynom {
        if (other.degree() == 0 && abs(other.coeff(0)) < EPS) {
            throw ArithmeticException("Деление на нулевой полином")
        }

        val thisDegree = this.degree()
        val otherDegree = other.degree()

        if (thisDegree < otherDegree) {
            return Polynom(0.0) // нулевой полином
        }

        val quotientDegree = thisDegree - otherDegree
        val quotientCoeffs = DoubleArray(quotientDegree + 1) { 0.0 }
        val remainderCoeffs = this.coefficients.copyOf()

        // Алгоритм деления в столбик
        for (i in 0..quotientDegree) {
            if (remainderCoeffs.size <= i) break

            val factor = remainderCoeffs[i] / other.coefficients[0]
            if (abs(factor) < EPS) continue

            quotientCoeffs[i] = factor

            // Вычитаем factor * other из остатка
            for (j in 0..otherDegree) {
                if (i + j < remainderCoeffs.size) {
                    remainderCoeffs[i + j] -= factor * other.coefficients[j]
                }
            }
        }

        // Убираем мелкие погрешности
        for (i in remainderCoeffs.indices) {
            if (abs(remainderCoeffs[i]) < EPS) {
                remainderCoeffs[i] = 0.0
            }
        }

        return Polynom(*quotientCoeffs.reversedArray())
    }

    /**
     * Взятие остатка
     */
    operator fun rem(other: Polynom): Polynom {
        if (other.degree() == 0 && abs(other.coeff(0)) < EPS) {
            throw ArithmeticException("Деление на нулевой полином")
        }

        val thisDegree = this.degree()
        val otherDegree = other.degree()

        if (thisDegree < otherDegree) {
            return this
        }

        val quotientDegree = thisDegree - otherDegree
        val remainderCoeffs = this.coefficients.copyOf()

        // Алгоритм деления в столбик
        for (i in 0..quotientDegree) {
            if (remainderCoeffs.size <= i) break

            val factor = remainderCoeffs[i] / other.coefficients[0]
            if (abs(factor) < EPS) continue

            // Вычитаем factor * other из остатка
            for (j in 0..otherDegree) {
                if (i + j < remainderCoeffs.size) {
                    remainderCoeffs[i + j] -= factor * other.coefficients[j]
                }
            }
        }

        // Убираем мелкие погрешности
        for (i in remainderCoeffs.indices) {
            if (abs(remainderCoeffs[i]) < EPS) {
                remainderCoeffs[i] = 0.0
            }
        }

        // Находим первый ненулевой коэффициент
        var firstNonZero = 0
        while (firstNonZero < remainderCoeffs.size && abs(remainderCoeffs[firstNonZero]) < EPS) {
            firstNonZero++
        }

        return if (firstNonZero == remainderCoeffs.size) {
            Polynom(0.0) // нулевой полином
        } else {
            Polynom(*remainderCoeffs.copyOfRange(firstNonZero, remainderCoeffs.size))
        }
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Polynom) return false

        val thisDegree = this.degree()
        val otherDegree = other.degree()

        if (thisDegree != otherDegree) return false

        for (i in 0..thisDegree) {
            if (abs(this.coeff(i) - other.coeff(i)) >= EPS) {
                return false
            }
        }

        return true
    }

    /**
     * Получение хеш-кода
     */
    override fun hashCode(): Int {
        var result = degree()

        for (coeff in coefficients) {
            // Учитываем только значимые коэффициенты
            if (abs(coeff) >= EPS) {
                result = 31 * result + coeff.hashCode()
            }
        }

        return result
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String {
        val degree = degree()
        if (degree == 0 && abs(coeff(0)) < EPS) {
            return "0"
        }

        val terms = mutableListOf<String>()

        for (i in degree downTo 0) {
            val coeff = coeff(i)
            if (abs(coeff) < EPS) continue

            val sign = when {
                terms.isEmpty() && coeff < 0 -> "-"
                terms.isEmpty() -> ""
                coeff > 0 -> "+"
                else -> "-"
            }

            val absCoeff = abs(coeff)
            val coeffStr = when {
                absCoeff == 1.0 && i > 0 -> ""
                absCoeff % 1.0 == 0.0 -> absCoeff.toInt().toString()
                else -> String.format("%.2f", absCoeff).trimEnd('0').trimEnd('.')
            }

            val powerStr = when (i) {
                0 -> if (coeffStr.isEmpty()) "1" else ""
                1 -> "x"
                else -> "x^$i"
            }

            terms.add("$sign${coeffStr}$powerStr")
        }

        return if (terms.isEmpty()) "0" else terms.joinToString("")
    }
}
fun main() {
    // Создание полиномов
    val p1 = Polynom(1.0, 3.0, 2.0)
    val p2 = Polynom(1.0, -2.0, -1.0, 4.0)

    println("p1 = $p1")
    println("p2 = $p2")
    println("p1.degree() = ${p1.degree()}")
    println("p2.degree() = ${p2.degree()}")

    // Арифметические операции
    println("p1 + p2 = ${p1 + p2}")
    println("p2 - p1 = ${p2 - p1}")
    println("p1 * p2 = ${p1 * p2}")

    // Деление с остатком
    val quotient = p2 / p1
    val remainder = p2 % p1
    println("p2 / p1 = $quotient")
    println("p2 % p1 = $remainder")
    println("p1 * quotient + remainder = ${p1 * quotient + remainder}")

    // Вычисление значения
    println("p1.getValue(5) = ${p1.getValue(5.0)}")

    // Проверка коэффициентов
    println("p1.coeff(2) = ${p1.coeff(2)}")
    println("p1.coeff(1) = ${p1.coeff(1)}")
    println("p1.coeff(0) = ${p1.coeff(0)}")

    // Смена знака
    println("-p1 = ${-p1}")

    // Проверка равенства
    val p3 = Polynom(1.0, 3.0, 2.0)
    println("p1 == p3: ${p1 == p3}")

    // Полином с ведущими нулями
    val p4 = Polynom(0.0, 0.0, 5.0, 3.0)
    println("p4 = $p4")
    println("p4.degree() = ${p4.degree()}")
}