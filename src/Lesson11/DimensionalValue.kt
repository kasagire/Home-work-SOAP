/**
 * Класс "Величина с размерностью".
 *
 * Предназначен для представления величин вроде "6 метров" или "3 килограммов"
 * Общая сложность задания - средняя, общая ценность в баллах -- 18
 * Величины с размерностью можно складывать, вычитать, делить, менять им знак.
 * Их также можно умножать и делить на число.
 *
 * В конструктор передаётся вещественное значение и строковая размерность.
 * Строковая размерность может:
 * - либо строго соответствовать одной из abbreviation класса Dimension (m, g)
 * - либо соответствовать одной из приставок, к которой приписана сама размерность (Km, Kg, mm, mg)
 * - во всех остальных случаях следует бросить IllegalArgumentException
 */
class DimensionalValue(
    /**
     * Величина с БАЗОВОЙ размерностью (например для 1.0Kg следует вернуть результат в граммах -- 1000.0)
     */
    val value: Double,
    /**
     * БАЗОВАЯ размерность (опять-таки для 1.0Kg следует вернуть GRAM)
     */
    val dimension: Dimension
) : Comparable<DimensionalValue> {

    /**
     * Основной публичный конструктор из значения и строки размерности
     */
    constructor(value: Double, dimensionStr: String) : this(
        calculateBaseValue(value, dimensionStr),
        parseDimension(dimensionStr)
    )

    /**
     * Конструктор из строки. Формат строки: значение пробел размерность (1 Kg, 3 mm, 100 g и так далее).
     */
    constructor(s: String) : this(0.0, Dimension.METER) {
        val parts = s.trim().split("\\s+".toRegex())
        if (parts.size != 2) {
            throw IllegalArgumentException("Некорректный формат строки: $s. Ожидается: 'значение размерность'")
        }

        val value = parts[0].toDoubleOrNull()
            ?: throw IllegalArgumentException("Некорректное значение: ${parts[0]}")
        val dimensionStr = parts[1]

        // Используем приватный setter для инициализации свойств
        val (multiplier, baseDimension) = parseDimensionString(dimensionStr)
        @Suppress("UNCHECKED_CAST")
        val valueField = DimensionalValue::class.java.getDeclaredField("value")
        valueField.isAccessible = true
        valueField.setDouble(this, value * multiplier)

        val dimensionField = DimensionalValue::class.java.getDeclaredField("dimension")
        dimensionField.isAccessible = true
        dimensionField.set(this, baseDimension)
    }

    /**
     * Вспомогательная функция для разбора строки размерности
     */
    private fun parseDimensionString(dimStr: String): Pair<Double, Dimension> {
        // Проверяем, соответствует ли строка базовой размерности
        Dimension.values().forEach { dim ->
            if (dim.abbreviation == dimStr) {
                return Pair(1.0, dim)
            }
        }

        // Проверяем, соответствует ли строка размерности с приставкой
        if (dimStr.startsWith(DimensionPrefix.KILO.abbreviation)) {
            val baseDimStr = dimStr.substring(DimensionPrefix.KILO.abbreviation.length)
            Dimension.values().forEach { dim ->
                if (dim.abbreviation == baseDimStr) {
                    return Pair(DimensionPrefix.KILO.multiplier, dim)
                }
            }
        }

        // Проверяем приставку MILLI
        if (dimStr.startsWith(DimensionPrefix.MILLI.abbreviation)) {
            val baseDimStr = dimStr.substring(DimensionPrefix.MILLI.abbreviation.length)
            Dimension.values().forEach { dim ->
                if (dim.abbreviation == baseDimStr) {
                    return Pair(DimensionPrefix.MILLI.multiplier, dim)
                }
            }
        }

        throw IllegalArgumentException("Некорректная размерность: $dimStr")
    }

    companion object {
        /**
         * Вычисление базового значения
         */
        private fun calculateBaseValue(value: Double, dimensionStr: String): Double {
            val (multiplier, _) = parseDimensionStringStatic(dimensionStr)
            return value * multiplier
        }

        /**
         * Парсинг размерности
         */
        private fun parseDimension(dimensionStr: String): Dimension {
            val (_, dimension) = parseDimensionStringStatic(dimensionStr)
            return dimension
        }

        private fun parseDimensionStringStatic(dimStr: String): Pair<Double, Dimension> {
            // Проверяем, соответствует ли строка базовой размерности
            Dimension.values().forEach { dim ->
                if (dim.abbreviation == dimStr) {
                    return Pair(1.0, dim)
                }
            }

            // Проверяем, соответствует ли строка размерности с приставкой
            if (dimStr.startsWith(DimensionPrefix.KILO.abbreviation)) {
                val baseDimStr = dimStr.substring(DimensionPrefix.KILO.abbreviation.length)
                Dimension.values().forEach { dim ->
                    if (dim.abbreviation == baseDimStr) {
                        return Pair(DimensionPrefix.KILO.multiplier, dim)
                    }
                }
            }

            if (dimStr.startsWith(DimensionPrefix.MILLI.abbreviation)) {
                val baseDimStr = dimStr.substring(DimensionPrefix.MILLI.abbreviation.length)
                Dimension.values().forEach { dim ->
                    if (dim.abbreviation == baseDimStr) {
                        return Pair(DimensionPrefix.MILLI.multiplier, dim)
                    }
                }
            }

            throw IllegalArgumentException("Некорректная размерность: $dimStr")
        }
    }

    /**
     * Сложение с другой величиной. Если базовая размерность разная, бросить IllegalArgumentException
     * (нельзя складывать метры и килограммы)
     */
    operator fun plus(other: DimensionalValue): DimensionalValue {
        if (this.dimension != other.dimension) {
            throw IllegalArgumentException("Нельзя складывать величины разных размерностей: ${this.dimension} и ${other.dimension}")
        }
        return DimensionalValue(this.value + other.value, this.dimension)
    }

    /**
     * Смена знака величины
     */
    operator fun unaryMinus(): DimensionalValue =
        DimensionalValue(-this.value, this.dimension)

    /**
     * Вычитание другой величины. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun minus(other: DimensionalValue): DimensionalValue =
        this + (-other)

    /**
     * Умножение на число
     */
    operator fun times(other: Double): DimensionalValue =
        DimensionalValue(this.value * other, this.dimension)

    /**
     * Деление на число
     */
    operator fun div(other: Double): DimensionalValue =
        DimensionalValue(this.value / other, this.dimension)

    /**
     * Деление на другую величину. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun div(other: DimensionalValue): Double {
        if (this.dimension != other.dimension) {
            throw IllegalArgumentException("Нельзя делить величины разных размерностей: ${this.dimension} и ${other.dimension}")
        }
        return this.value / other.value
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DimensionalValue) return false

        // Сравниваем с заданной точностью из-за погрешностей Double
        val epsilon = 1e-10
        return this.dimension == other.dimension &&
                Math.abs(this.value - other.value) < epsilon
    }

    /**
     * Сравнение на больше/меньше. Если базовая размерность разная, бросить IllegalArgumentException
     */
    override fun compareTo(other: DimensionalValue): Int {
        if (this.dimension != other.dimension) {
            throw IllegalArgumentException("Нельзя сравнивать величины разных размерностей: ${this.dimension} и ${other.dimension}")
        }
        return this.value.compareTo(other.value)
    }

    /**
     * Преобразование в строку
     */
    override fun toString(): String {

        val absValue = Math.abs(this.value)

        // Проверяем KILO
        if (absValue >= DimensionPrefix.KILO.multiplier &&
            Math.abs(absValue % DimensionPrefix.KILO.multiplier) < 1e-10) {
            val displayValue = this.value / DimensionPrefix.KILO.multiplier
            val displayDim = DimensionPrefix.KILO.abbreviation + this.dimension.abbreviation
            return "${formatValue(displayValue)} $displayDim"
        }

        // Проверяем MILLI
        if (absValue > 0 && absValue <= 0.1 &&
            Math.abs(absValue % DimensionPrefix.MILLI.multiplier) < 1e-10) {
            val displayValue = this.value / DimensionPrefix.MILLI.multiplier
            val displayDim = DimensionPrefix.MILLI.abbreviation + this.dimension.abbreviation
            return "${formatValue(displayValue)} $displayDim"
        }

        // Выводим в базовых единицах
        return "${formatValue(this.value)} ${this.dimension.abbreviation}"
    }

    /**
     * Форматирование значения
     */
    private fun formatValue(value: Double): String {
        val roundedValue = if (Math.abs(value % 1.0) < 1e-10) {
            value.toLong().toDouble()
        } else {
            value
        }

        return if (Math.abs(roundedValue % 1.0) < 1e-10) {
            roundedValue.toLong().toString()
        } else {
            "%.3f".format(roundedValue).trimEnd('0').trimEnd('.')
        }
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + dimension.hashCode()
        return result
    }
}

/**
 * Размерность.
 */
enum class Dimension(val abbreviation: String) {
    METER("m"),
    GRAM("g");
}

/**
 * Приставка размерности.
 */
enum class DimensionPrefix(val abbreviation: String, val multiplier: Double) {
    KILO("K", 1000.0),
    MILLI("m", 0.001);
}
fun main() {
    // Создание величин
    val length1 = DimensionalValue(1.5, "m")
    val length2 = DimensionalValue("2.0 m")
    val mass1 = DimensionalValue(1.0, "Kg")
    val mass2 = DimensionalValue("500 g")

    println("length1 = $length1")
    println("length2 = $length2")
    println("mass1 = $mass1")
    println("mass2 = $mass2")

    // Арифметические операции
    println("length1 + length2 = ${length1 + length2}")
    println("mass1 - mass2 = ${mass1 - mass2}")
}