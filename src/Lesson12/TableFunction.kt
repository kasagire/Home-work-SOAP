import kotlin.math.abs

/**
 * Класс "табличная функция".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса хранит таблицу значений функции (y) от одного аргумента (x).
 * В таблицу можно добавлять и удалять пары (x, y),
 * найти в ней ближайшую пару (x, y) по заданному x,
 * найти (интерполяцией или экстраполяцией) значение y по заданному x.
 *
 * Класс должен иметь конструктор по умолчанию (без параметров).
 */
class TableFunction {

    // Храним пары отсортированными по x для быстрого поиска
    private val pairs = mutableListOf<Pair<Double, Double>>()

    // Вспомогательная структура для быстрого поиска по x
    private val xSet = mutableSetOf<Double>()

    /**
     * Количество пар в таблице
     */
    val size: Int get() = pairs.size

    /**
     * Проверка на пустоту
     */
    fun isEmpty(): Boolean = pairs.isEmpty()

    /**
     * Проверка на наличие пары с заданным x
     */
    fun containsX(x: Double): Boolean = xSet.contains(x)

    /**
     * Бинарный поиск индекса для вставки/поиска x
     */
    private fun findIndexForX(x: Double): Int {
        if (pairs.isEmpty()) return 0

        var left = 0
        var right = pairs.size - 1

        while (left <= right) {
            val mid = (left + right) / 2
            val midX = pairs[mid].first

            when {
                midX == x -> return mid
                midX < x -> left = mid + 1
                else -> right = mid - 1
            }
        }

        return left
    }

    /**
     * Добавить новую пару.
     * Вернуть true, если пары с заданным x ещё нет,
     * или false, если она уже есть (в этом случае перезаписать значение y)
     */
    fun add(x: Double, y: Double): Boolean {
        val index = findIndexForX(x)

        return if (index < pairs.size && pairs[index].first == x) {
            // Пара уже существует
            pairs[index] = Pair(x, y)
            false
        } else {
            // Вставляем новую пару в отсортированном порядке
            pairs.add(index, Pair(x, y))
            xSet.add(x)
            true
        }
    }

    /**
     * Удалить пару с заданным значением x.
     * Вернуть true, если пара была удалена.
     */
    fun remove(x: Double): Boolean {
        val index = findIndexForX(x)

        return if (index < pairs.size && pairs[index].first == x) {
            pairs.removeAt(index)
            xSet.remove(x)
            true
        } else {
            false
        }
    }

    /**
     * Вернуть коллекцию из всех пар в таблице
     */
    fun getPairs(): Collection<Pair<Double, Double>> = pairs.toList()

    /**
     * Получить значение y для заданного x, если оно существует
     */
    fun getY(x: Double): Double? {
        val index = findIndexForX(x)
        return if (index < pairs.size && pairs[index].first == x) {
            pairs[index].second
        } else {
            null
        }
    }

    /**
     * Вернуть пару, ближайшую к заданному x.
     * Если существует две ближайшие пары, вернуть пару с меньшим значением x.
     * Если таблица пуста, бросить IllegalStateException.
     */
    fun findPair(x: Double): Pair<Double, Double> {
        if (pairs.isEmpty()) {
            throw IllegalStateException("Таблица пуста")
        }

        if (pairs.size == 1) {
            return pairs[0]
        }

        val index = findIndexForX(x)

        // Если x находится в таблице
        if (index < pairs.size && pairs[index].first == x) {
            return pairs[index]
        }

        // Определяем ближайшие пары
        val leftIndex = when (index) {
            0 -> 0
            pairs.size -> pairs.size - 1
            else -> index - 1
        }

        val rightIndex = when (index) {
            0 -> if (pairs.size > 1) 1 else 0
            pairs.size -> pairs.size - 1
            else -> index
        }

        // Выбираем ближайшую пару
        val leftPair = pairs[leftIndex]
        val rightPair = pairs[rightIndex]

        val leftDistance = abs(leftPair.first - x)
        val rightDistance = abs(rightPair.first - x)

        return when {
            leftDistance < rightDistance -> leftPair
            rightDistance < leftDistance -> rightPair
            else -> if (leftPair.first < rightPair.first) leftPair else rightPair
        }
    }

    /**
     * Вернуть значение y по заданному x.
     * Если в таблице есть пара с заданным x, взять значение y из неё.
     * Если в таблице есть всего одна пара, взять значение y из неё.
     * Если таблица пуста, бросить IllegalStateException.
     * Если существуют две пары, такие, что x1 < x < x2, использовать интерполяцию.
     * Если их нет, но существуют две пары, такие, что x1 < x2 < x или x > x2 > x1, использовать экстраполяцию.
     */
    fun getValue(x: Double): Double {
        if (pairs.isEmpty()) {
            throw IllegalStateException("Таблица пуста")
        }

        // Проверяем, есть ли точное значение
        val exactY = getY(x)
        if (exactY != null) {
            return exactY
        }

        if (pairs.size == 1) {
            return pairs[0].second
        }

        val index = findIndexForX(x)

        // Определяем две ближайшие пары для интерполяции/экстраполяции
        val (leftPair, rightPair) = when (index) {
            0 -> {
                // x меньше всех значений в таблице - экстраполяция
                Pair(pairs[0], pairs[1])
            }
            pairs.size -> {
                // x больше всех значений в таблице - экстраполяция
                Pair(pairs[pairs.size - 2], pairs[pairs.size - 1])
            }
            else -> {
                // x между двумя значениями - интерполяция
                Pair(pairs[index - 1], pairs[index])
            }
        }

        // Линейная интерполяция/экстраполяция
        val (x1, y1) = leftPair
        val (x2, y2) = rightPair

        // Избегаем деления на ноль
        if (x1 == x2) {
            return (y1 + y2) / 2
        }

        // Формула линейной интерполяции
        return y1 + (x - x1) * (y2 - y1) / (x2 - x1)
    }

    /**
     * Очистить таблицу
     */
    fun clear() {
        pairs.clear()
        xSet.clear()
    }

    /**
     * Получить минимальное значение x
     */
    fun minX(): Double? = pairs.firstOrNull()?.first

    /**
     * Получить максимальное значение x
     */
    fun maxX(): Double? = pairs.lastOrNull()?.first

    /**
     * Получить минимальное значение y
     */
    fun minY(): Double? = pairs.minOfOrNull { it.second }

    /**
     * Получить максимальное значение y
     */
    fun maxY(): Double? = pairs.maxOfOrNull { it.second }

    /**
     * Таблицы равны, если в них одинаковое количество пар,
     * и любая пара из второй таблицы входит также и в первую
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TableFunction) return false

        // Проверяем размеры
        if (this.size != other.size) return false

        // Проверяем, что все пары из other есть в this
        for ((x, y) in other.pairs) {
            val thisY = this.getY(x)
            if (thisY == null || thisY != y) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        for ((x, y) in pairs) {
            result = 31 * result + x.hashCode()
            result = 31 * result + y.hashCode()
        }
        return result
    }

    override fun toString(): String {
        return "TableFunction(size=$size, pairs=${pairs})"
    }
}

fun main() {
    println("=== Тестирование TableFunction ===")

    val table = TableFunction()

    println("1. Создание пустой таблицы:")
    println("size: ${table.size}")
    println("isEmpty: ${table.isEmpty()}")

    // Добавление пар
    println("\n2. Добавление пар:")
    println("add(1.0, 10.0): ${table.add(1.0, 10.0)}")
    println("add(3.0, 30.0): ${table.add(3.0, 30.0)}")
    println("add(2.0, 20.0): ${table.add(2.0, 20.0)}")
    println("add(2.0, 25.0): ${table.add(2.0, 25.0)}")
    println("size: ${table.size}")
    println("table: $table")

    // Получение пар
    println("\n3. Получение пар:")
    println("getPairs(): ${table.getPairs()}")
    println("getY(2.0): ${table.getY(2.0)}")
    println("getY(5.0): ${table.getY(5.0)}")
    println("containsX(3.0): ${table.containsX(3.0)}")
    println("containsX(5.0): ${table.containsX(5.0)}")

    // Поиск ближайшей пары
    println("\n4. Поиск ближайшей пары:")
    println("findPair(1.5): ${table.findPair(1.5)}")
    println("findPair(2.0): ${table.findPair(2.0)}")
    println("findPair(2.5): ${table.findPair(2.5)}")
    println("findPair(0.5): ${table.findPair(0.5)}")
    println("findPair(5.0): ${table.findPair(5.0)}")

    // Получение значения с интерполяцией/экстраполяцией
    println("\n5. Получение значения (интерполяция/экстраполяция):")
    println("getValue(1.0): ${table.getValue(1.0)}")
    println("getValue(2.0): ${table.getValue(2.0)}")
    println("getValue(1.5): ${table.getValue(1.5)}")
    println("getValue(2.5): ${table.getValue(2.5)}")
    println("getValue(0.0): ${table.getValue(0.0)}")
    println("getValue(4.0): ${table.getValue(4.0)}")

    // Удаление пар
    println("\n6. Удаление пар:")
    println("remove(2.0): ${table.remove(2.0)}")
    println("remove(5.0): ${table.remove(5.0)}")
    println("После удаления:")
    println("size: ${table.size}")
    println("table: $table")

    // Тестирование с одной парой
    println("\n7. Тестирование с одной парой:")
    val singleTable = TableFunction()
    singleTable.add(5.0, 50.0)
    println("singleTable: $singleTable")
    println("findPair(10.0): ${singleTable.findPair(10.0)}")
    println("getValue(10.0): ${singleTable.getValue(10.0)}")

    // Проверка равенства
    println("\n8. Проверка равенства:")
    val table1 = TableFunction()
    table1.add(1.0, 10.0)
    table1.add(2.0, 20.0)
    table1.add(3.0, 30.0)

    val table2 = TableFunction()
    table2.add(3.0, 30.0)
    table2.add(1.0, 10.0)
    table2.add(2.0, 20.0)

    val table3 = TableFunction()
    table3.add(1.0, 10.0)
    table3.add(2.0, 20.0)
    table3.add(3.0, 31.0)

    println("table1 == table2: ${table1 == table2}")
    println("table1 == table3: ${table1 == table3}")

    // Тестирование исключений
    println("\n9. Тестирование исключений:")
    val emptyTable = TableFunction()
    try {
        println("emptyTable.findPair(1.0): ${emptyTable.findPair(1.0)}")
    } catch (e: IllegalStateException) {
        println("Исключение при findPair: ${e.message}")
    }

    try {
        println("emptyTable.getValue(1.0): ${emptyTable.getValue(1.0)}")
    } catch (e: IllegalStateException) {
        println("Исключение при getValue: ${e.message}")
    }

    // Дополнительные методы
    println("\n10. Дополнительные методы:")
    println("table1.minX(): ${table1.minX()}")
    println("table1.maxX(): ${table1.maxX()}")
    println("table1.minY(): ${table1.minY()}")
    println("table1.maxY(): ${table1.maxY()}")

    // Очистка
    println("\n11. Очистка:")
    table1.clear()
    println("table1 после clear: $table1")
    println("size: ${table1.size}")
    println("isEmpty: ${table1.isEmpty()}")

    // Тестирование с большим количеством пар
    println("\n12. Тестирование с большим количеством пар:")
    val bigTable = TableFunction()
    for (i in 1..10) {
        bigTable.add(i.toDouble(), (i * i).toDouble())
    }
    println("bigTable: $bigTable")
    println("bigTable.getValue(5.5): ${bigTable.getValue(5.5)}")
    println("bigTable.getValue(0.5): ${bigTable.getValue(0.5)}")
    println("bigTable.getValue(10.5): ${bigTable.getValue(10.5)}")
}