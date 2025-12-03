import kotlin.math.abs

/**
 * Точка на гексагональной сетке.
 * Координаты соответствуют примеру из задания:
 * y - вертикальная координата, x - горизонтальная
 */
data class HexPoint(val x: Int, val y: Int) {
    /**
     * Средняя (3 балла)
     *
     * Найти целочисленное расстояние между двумя гексами сетки.
     * Расстояние вычисляется как число единичных отрезков в пути между двумя гексами.
     * Например, путь межу гексами 16 и 41 (см. выше) может проходить через 25, 34, 43 и 42 и имеет длину 5.
     */
    fun distance(other: HexPoint): Int {
        // Преобразуем гексагональные координаты в кубические
        val z1 = -x - y
        val z2 = -other.x - other.y

        // В кубических координатах расстояние - это максимум из разностей по осям
        return maxOf(abs(x - other.x), abs(y - other.y), abs(z1 - z2))
    }

    override fun toString(): String = "$y.$x"
}

/**
 * Правильный шестиугольник на гексагональной сетке.
 * Как окружность на плоскости, задаётся центральным гексом и радиусом.
 * Например, шестиугольник с центром в 33 и радиусом 1 состоит из гексов 42, 43, 34, 24, 23, 32.
 */
data class Hexagon(val center: HexPoint, val radius: Int) {

    /**
     * Средняя (3 балла)
     *
     * Рассчитать расстояние между двумя шестиугольниками.
     * Оно равно расстоянию между ближайшими точками этих шестиугольников,
     * или 0, если шестиугольники имеют общую точку.
     *
     * Например, расстояние между шестиугольником A с центром в 31 и радиусом 1
     * и другим шестиугольником B с центром в 26 и радиуоом 2 равно 2
     * (расстояние между точками 32 и 24)
     */
    fun distance(other: Hexagon): Int {
        val centerDistance = center.distance(other.center)
        val sumOfRadii = radius + other.radius

        return if (centerDistance > sumOfRadii) {
            centerDistance - sumOfRadii
        } else {
            0
        }
    }

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если заданная точка находится внутри или на границе шестиугольника
     */
    fun contains(point: HexPoint): Boolean {
        return center.distance(point) <= radius
    }
}

/**
 * Прямолинейный отрезок между двумя гексами
 */
class HexSegment(val begin: HexPoint, val end: HexPoint) {
    /**
     * Простая (2 балла)
     *
     * Определить "правильность" отрезка.
     * "Правильным" считается только отрезок, проходящий параллельно одной из трёх осей шестиугольника.
     * Такими являются, например, отрезок 30-34 (горизонталь), 13-63 (прямая диагональ) или 51-24 (косая диагональ).
     * А, например, 13-26 не является "правильным" отрезком.
     */
    fun isValid(): Boolean {
        val dx = end.x - begin.x
        val dy = end.y - begin.y

        // В гексагональной системе отрезок правильный, если:
        // 1. dx = 0 (вертикаль) OR dy = 0 (горизонталь) OR dx + dy = 0 (диагональ)
        // 2. dx и dy имеют одинаковый знак для диагонали
        return when {
            dx == 0 -> dy != 0 // Вертикаль
            dy == 0 -> dx != 0 // Горизонталь
            dx == -dy -> true   // Диагональ
            else -> false       // Неправильный отрезок
        }
    }

    /**
     * Средняя (3 балла)
     *
     * Вернуть направление отрезка (см. описание класса Direction ниже).
     * Для "правильного" отрезка выбирается одно из первых шести направлений,
     * для "неправильного" -- INCORRECT.
     */
    fun direction(): Direction {
        if (!isValid()) return Direction.INCORRECT

        val dx = end.x - begin.x
        val dy = end.y - begin.y

        return when {
            dx > 0 && dy == 0 -> Direction.RIGHT
            dx > 0 && dy > 0 -> Direction.UP_RIGHT
            dx == 0 && dy > 0 -> Direction.UP_LEFT
            dx < 0 && dy == 0 -> Direction.LEFT
            dx < 0 && dy < 0 -> Direction.DOWN_LEFT
            dx == 0 && dy < 0 -> Direction.DOWN_RIGHT
            dx > 0 && dy < 0 -> Direction.DOWN_RIGHT  // Для диагонали
            dx < 0 && dy > 0 -> Direction.UP_LEFT     // Для диагонали
            else -> Direction.INCORRECT
        }
    }

    override fun equals(other: Any?) =
        other is HexSegment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Направление отрезка на гексагональной сетке.
 * Если отрезок "правильный", то он проходит вдоль одной из трёх осей шестугольника.
 * Если нет, его направление считается INCORRECT
 */
enum class Direction {
    RIGHT,      // слева направо
    UP_RIGHT,   // вверх-вправо
    UP_LEFT,    // вверх-влево
    LEFT,       // справа налево
    DOWN_LEFT,  // вниз-влево
    DOWN_RIGHT, // вниз-вправо
    INCORRECT;  // отрезок имеет изгиб

    /**
     * Простая (2 балла)
     *
     * Вернуть направление, противоположное данному.
     * Для INCORRECT вернуть INCORRECT
     */
    fun opposite(): Direction = when (this) {
        RIGHT -> LEFT
        UP_RIGHT -> DOWN_LEFT
        UP_LEFT -> DOWN_RIGHT
        LEFT -> RIGHT
        DOWN_LEFT -> UP_RIGHT
        DOWN_RIGHT -> UP_LEFT
        INCORRECT -> INCORRECT
    }

    /**
     * Средняя (3 балла)
     *
     * Вернуть направление, повёрнутое относительно
     * заданного на 60 градусов против часовой стрелки.
     *
     * Например, для RIGHT это UP_RIGHT, для UP_LEFT это LEFT, для LEFT это DOWN_LEFT.
     * Для направления INCORRECT бросить исключение IllegalArgumentException.
     * При решении этой задачи попробуйте обойтись без перечисления всех семи вариантов.
     */
    fun next(): Direction {
        if (this == INCORRECT) throw IllegalArgumentException("Cannot rotate INCORRECT direction")

        val values = Direction.values()
        val index = ordinal
        // Поворот на 60° против часовой стрелки = переход к следующему направлению
        return values[(index + 1) % 6]
    }

    /**
     * Простая (2 балла)
     *
     * Вернуть true, если данное направление совпадает с other или противоположно ему.
     * INCORRECT не параллельно никакому направлению, в том числе другому INCORRECT.
     */
    fun isParallel(other: Direction): Boolean {
        if (this == INCORRECT || other == INCORRECT) return false
        return this == other || this.opposite() == other
    }
}

/**
 * Средняя (3 балла)
 *
 * Сдвинуть точку в направлении direction на расстояние distance.
 * Бросить IllegalArgumentException(), если задано направление INCORRECT.
 * Для расстояния 0 и направления не INCORRECT вернуть ту же точку.
 * Для отрицательного расстояния сдвинуть точку в противоположном направлении на -distance.
 *
 * Примеры:
 * 30, direction = RIGHT, distance = 3 --> 33
 * 35, direction = UP_LEFT, distance = 2 --> 53
 * 45, direction = DOWN_LEFT, distance = 4 --> 05
 */
fun HexPoint.move(direction: Direction, distance: Int): HexPoint {
    if (direction == Direction.INCORRECT) {
        throw IllegalArgumentException("INCORRECT direction cannot be used for movement")
    }

    val effectiveDistance = if (distance < 0) -distance else distance
    val effectiveDirection = if (distance < 0) direction.opposite() else direction

    return when (effectiveDirection) {
        Direction.RIGHT -> HexPoint(x + effectiveDistance, y)
        Direction.UP_RIGHT -> HexPoint(x + effectiveDistance, y + effectiveDistance)
        Direction.UP_LEFT -> HexPoint(x, y + effectiveDistance)
        Direction.LEFT -> HexPoint(x - effectiveDistance, y)
        Direction.DOWN_LEFT -> HexPoint(x - effectiveDistance, y - effectiveDistance)
        Direction.DOWN_RIGHT -> HexPoint(x, y - effectiveDistance)
        Direction.INCORRECT -> throw IllegalStateException("Should not reach here")
    }
}

/**
 * Сложная (5 баллов)
 *
 * Найти кратчайший путь между двумя заданными гексами, представленный в виде списка всех гексов,
 * которые входят в этот путь.
 * Начальный и конечный гекс также входят в данный список.
 * Если кратчайших путей существует несколько, вернуть любой из них.
 *
 * Пример (для координатной сетки из примера в начале файла):
 *   pathBetweenHexes(HexPoint(y = 2, x = 2), HexPoint(y = 5, x = 3)) ->
 *     listOf(
 *       HexPoint(y = 2, x = 2),
 *       HexPoint(y = 2, x = 3),
 *       HexPoint(y = 3, x = 3),
 *       HexPoint(y = 4, x = 3),
 *       HexPoint(y = 5, x = 3)
 *     )
 */
fun pathBetweenHexes(from: HexPoint, to: HexPoint): List<HexPoint> {
    val path = mutableListOf<HexPoint>()

    // Преобразуем в кубические координаты
    val fromCube = arrayOf(from.x, from.y, -from.x - from.y)
    val toCube = arrayOf(to.x, to.y, -to.x - to.y)

    val distance = from.distance(to)

    for (i in 0..distance) {
        val t = i.toDouble() / distance
        // Линейная интерполяция
        val x = ((fromCube[0] * (1 - t) + toCube[0] * t) + 0.5).toInt()
        val y = ((fromCube[1] * (1 - t) + toCube[1] * t) + 0.5).toInt()
        val z = ((fromCube[2] * (1 - t) + toCube[2] * t) + 0.5).toInt()

        // Конвертируем обратно в осевые координаты
        // Проверяем, какая пара координат дает правильное преобразование
        path.add(HexPoint(x, y))
    }

    return path.distinct() // Убираем возможные дубликаты
}

/**
 * Очень сложная (20 баллов)
 *
 * Дано три точки (гекса). Построить правильный шестиугольник, проходящий через них
 * (все три точки должны лежать НА ГРАНИЦЕ, а не ВНУТРИ, шестиугольника).
 * Все стороны шестиугольника должны являться "правильными" отрезками.
 * Вернуть null, если такой шестиугольник построить невозможно.
 * Если шестиугольников существует более одного, выбрать имеющий минимальный радиус.
 *
 * Пример: через точки 13, 32 и 44 проходит правильный шестиугольник с центром в 24 и радиусом 2.
 * Для точек 13, 32 и 45 такого шестиугольника не существует.
 * Для точек 32, 33 и 35 следует вернуть шестиугольник радиусом 3 (с центром в 62 или 05).
 *
 * Если все три точки совпадают, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 */
fun hexagonByThreePoints(a: HexPoint, b: HexPoint, c: HexPoint): Hexagon? {
    // Если все три точки совпадают
    if (a == b && b == c) {
        return Hexagon(a, 0)
    }

    // Перебираем возможные радиусы
    var minRadius = Int.MAX_VALUE
    var result: Hexagon? = null

    // Максимальный радиус для перебора - максимальное расстояние между точками
    val maxPossibleRadius = maxOf(a.distance(b), b.distance(c), a.distance(c)) * 2

    for (radius in 0..maxPossibleRadius) {
        // Для каждого радиуса ищем возможные центры
        // Точки должны быть на расстоянии radius от центра
        val possibleCenters = mutableSetOf<HexPoint>()

        // Для каждой точки находим все гексы на расстоянии radius от нее
        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                val candidate = HexPoint(a.x + dx, a.y + dy)
                if (a.distance(candidate) == radius) {
                    possibleCenters.add(candidate)
                }
            }
        }

        // Проверяем каждый возможный центр
        for (center in possibleCenters) {
            if (b.distance(center) == radius && c.distance(center) == radius) {
                // Проверяем, что точки лежат на границе
                val hexagon = Hexagon(center, radius)
                if (hexagon.contains(a) && hexagon.contains(b) && hexagon.contains(c)) {
                    // Проверяем, что точки именно на границе, а не внутри
                    val isOnBorder = listOf(a, b, c).all { point ->
                        center.distance(point) == radius
                    }

                    if (isOnBorder && radius < minRadius) {
                        minRadius = radius
                        result = hexagon
                    }
                }
            }
        }
    }

    return result
}

/**
 * Очень сложная (20 баллов)
 *
 * Дано множество точек (гексов). Найти правильный шестиугольник минимального радиуса,
 * содержащий все эти точки (безразлично, внутри или на границе).
 * Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит один гекс, вернуть шестиугольник нулевого радиуса с центром в данной точке.
 *
 * Пример: 13, 32, 45, 18 -- шестиугольник радиусом 3 (с центром, например, в 15)
 */
fun minContainingHexagon(vararg points: HexPoint): Hexagon {
    if (points.isEmpty()) {
        throw IllegalArgumentException("Points set is empty")
    }

    if (points.size == 1) {
        return Hexagon(points[0], 0)
    }

    // Находим ограничивающий прямоугольник для точек
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    var minY = Int.MAX_VALUE
    var maxY = Int.MIN_VALUE

    for (point in points) {
        if (point.x < minX) minX = point.x
        if (point.x > maxX) maxX = point.x
        if (point.y < minY) minY = point.y
        if (point.y > maxY) maxY = point.y
    }

    // Перебираем возможные радиусы
    var bestHexagon: Hexagon? = null

    // Максимальный радиус для поиска
    val maxRadius = maxOf(maxX - minX, maxY - minY) * 2

    for (radius in 0..maxRadius) {
        // Перебираем возможные центры в окрестности точек
        val possibleCenters = mutableSetOf<HexPoint>()

        // Центр должен быть в пределах radius от каждой точки
        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                val centerCandidate = HexPoint(minX + dx, minY + dy)
                possibleCenters.add(centerCandidate)
            }
        }

        // Проверяем каждый возможный центр
        for (center in possibleCenters) {
            val candidate = Hexagon(center, radius)

            // Проверяем, содержит ли шестиугольник все точки
            val containsAll = points.all { candidate.contains(it) }

            if (containsAll) {
                // Если нашли шестиугольник меньшего радиуса, обновляем
                if (bestHexagon == null || radius < bestHexagon.radius) {
                    bestHexagon = candidate
                }
            }
        }

        // Если нашли шестиугольник и радиус уже достаточно большой, можно остановиться
        if (bestHexagon != null && radius > bestHexagon.radius) {
            break
        }
    }

    return bestHexagon ?: throw IllegalStateException("Could not find containing hexagon")
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== Тестирование гексагональных функций ===\n")

    // 1. Тестирование HexPoint и distance
    println("1. Тестирование HexPoint.distance():")
    val point1 = HexPoint(3, 2)  // Гекс 2.3
    val point2 = HexPoint(4, 1)  // Гекс 1.4
    val point3 = HexPoint(1, 5)  // Гекс 5.1

    println("   Точка 1: $point1")
    println("   Точка 2: $point2")
    println("   Точка 3: $point3")
    println("   Расстояние между 1 и 2: ${point1.distance(point2)}")
    println("   Расстояние между 2 и 3: ${point2.distance(point3)}")
    println("   Расстояние между 1 и 3: ${point1.distance(point3)}")

    // 2. Тестирование Hexagon
    println("\n2. Тестирование Hexagon:")
    val hexagon1 = Hexagon(HexPoint(3, 3), 1)
    val hexagon2 = Hexagon(HexPoint(2, 6), 2)

    println("   Шестиугольник 1: центр=${hexagon1.center}, радиус=${hexagon1.radius}")
    println("   Шестиугольник 2: центр=${hexagon2.center}, радиус=${hexagon2.radius}")
    println("   Расстояние между шестиугольниками: ${hexagon1.distance(hexagon2)}")
    println("   Точка $point1 в шестиугольнике 1: ${hexagon1.contains(point1)}")
    println("   Точка $point2 в шестиугольнике 1: ${hexagon1.contains(point2)}")

    // 3. Тестирование HexSegment и Direction
    println("\n3. Тестирование HexSegment и Direction:")
    val segment1 = HexSegment(HexPoint(3, 0), HexPoint(4, 0))  // RIGHT
    val segment2 = HexSegment(HexPoint(3, 2), HexPoint(6, 2))  // RIGHT (длинный)
    val segment3 = HexSegment(HexPoint(3, 2), HexPoint(2, 3))  // UP_LEFT
    val segment4 = HexSegment(HexPoint(3, 2), HexPoint(2, 1))  // DOWN_LEFT
    val segment5 = HexSegment(HexPoint(3, 2), HexPoint(5, 4))  // UP_RIGHT
    val segment6 = HexSegment(HexPoint(3, 2), HexPoint(4, 1))  // INCORRECT? (проверим)

    val segments = listOf(segment1, segment2, segment3, segment4, segment5, segment6)

    for ((index, segment) in segments.withIndex()) {
        println("   Отрезок ${index + 1}: ${segment.begin} -> ${segment.end}")
        println("     Правильный: ${segment.isValid()}")
        println("     Направление: ${segment.direction()}")
    }

    // 4. Тестирование Direction методов
    println("\n4. Тестирование Direction:")
    val direction = Direction.RIGHT
    println("   Направление: $direction")
    println("   Противоположное: ${direction.opposite()}")
    println("   Поворот на 60°: ${direction.next()}")
    println("   Параллельно UP_RIGHT: ${direction.isParallel(Direction.UP_RIGHT)}")
    println("   Параллельно LEFT: ${direction.isParallel(Direction.LEFT)}")

    // 5. Тестирование move
    println("\n5. Тестирование HexPoint.move():")
    val startPoint = HexPoint(3, 0)
    println("   Начальная точка: $startPoint")
    println("   Движение RIGHT на 3: ${startPoint.move(Direction.RIGHT, 3)}")
    println("   Движение UP_LEFT на 2: ${startPoint.move(Direction.UP_LEFT, 2)}")
    println("   Движение DOWN_LEFT на 2: ${startPoint.move(Direction.DOWN_LEFT, 2)}")
    println("   Движение RIGHT на -2 (LEFT на 2): ${startPoint.move(Direction.RIGHT, -2)}")

    // 6. Тестирование pathBetweenHexes
    println("\n6. Тестирование pathBetweenHexes():")
    val from = HexPoint(2, 2)  // 2.2
    val to = HexPoint(3, 5)    // 5.3
    val path = pathBetweenHexes(from, to)
    println("   Путь от $from до $to:")
    println("   ${path.joinToString(" -> ")}")

    // 7. Тестирование hexagonByThreePoints
    println("\n7. Тестирование hexagonByThreePoints():")
    val hexPointA = HexPoint(1, 3)  // 3.1
    val hexPointB = HexPoint(3, 2)  // 2.3
    val hexPointC = HexPoint(4, 4)  // 4.4

    val hexagonResult = hexagonByThreePoints(hexPointA, hexPointB, hexPointC)
    println("   Точки: $hexPointA, $hexPointB, $hexPointC")
    println("   Результат: $hexagonResult")

    // 8. Тестирование minContainingHexagon
    println("\n8. Тестирование minContainingHexagon():")
    val testPoints = arrayOf(
        HexPoint(1, 3),  // 3.1
        HexPoint(3, 2),  // 2.3
        HexPoint(4, 5),  // 5.4
        HexPoint(1, 8)   // 8.1
    )

    try {
        val containingHexagon = minContainingHexagon(*testPoints)
        println("   Точки: ${testPoints.joinToString()}")
        println("   Минимальный содержащий шестиугольник: $containingHexagon")

        // Проверяем, что все точки содержатся
        val allContained = testPoints.all { containingHexagon.contains(it) }
        println("   Все точки содержатся: $allContained")
    } catch (e: IllegalArgumentException) {
        println("   Ошибка: ${e.message}")
    }

    println("\n=== Тестирование завершено ===")
}