import kotlin.math.abs
/**
 * Клетка шахматной доски. Шахматная доска квадратная и имеет 8 х 8 клеток.
 * Поэтому, обе координаты клетки (горизонталь row, вертикаль column) могут находиться в пределах от 1 до 8.
 * Горизонтали нумеруются снизу вверх, вертикали слева направо.
 */
data class Square(val column: Int, val row: Int) {
    /**
     * Пример
     *
     * Возвращает true, если клетка находится в пределах доски
     */
    fun inside(): Boolean = column in 1..8 && row in 1..8

    /**
     * Простая (2 балла)
     *
     * Возвращает строковую нотацию для клетки.
     * В нотации, колонки обозначаются латинскими буквами от a до h, а ряды -- цифрами от 1 до 8.
     * Для клетки не в пределах доски вернуть пустую строку
     */
    fun notation(): String {
        if (!inside()) return ""
        val columnChar = 'a' + (column - 1)
        return "$columnChar$row"
    }
}

/**
 * Простая (2 балла)
 *
 * Создаёт клетку по строковой нотации.
 * В нотации, колонки обозначаются латинскими буквами от a до h, а ряды -- цифрами от 1 до 8.
 * Если нотация некорректна, бросить IllegalArgumentException
 */
fun square(notation: String): Square {
    if (notation.length != 2) throw IllegalArgumentException("Invalid notation: $notation")

    val columnChar = notation[0]
    val rowChar = notation[1]

    if (columnChar !in 'a'..'h' || rowChar !in '1'..'8') {
        throw IllegalArgumentException("Invalid notation: $notation")
    }

    val column = columnChar - 'a' + 1
    val row = rowChar.digitToInt()

    return Square(column, row)
}

/**
 * Простая (2 балла)
 *
 * Определить число ходов, за которое шахматная ладья пройдёт из клетки start в клетку end.
 * Шахматная ладья может за один ход переместиться на любую другую клетку
 * по вертикали или горизонтали.
 * Ниже точками выделены возможные ходы ладьи, а крестиками -- невозможные:
 *
 * xx.xxххх
 * xх.хxххх
 * ..Л.....
 * xх.хxххх
 * xx.xxххх
 * xx.xxххх
 * xx.xxххх
 * xx.xxххх
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: rookMoveNumber(Square(3, 1), Square(6, 3)) = 2
 * Ладья может пройти через клетку (3, 3) или через клетку (6, 1) к клетке (6, 3).
 */
fun rookMoveNumber(start: Square, end: Square): Int {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    return when {
        start == end -> 0
        start.column == end.column || start.row == end.row -> 1
        else -> 2
    }
}

/**
 * Средняя (3 балла)
 *
 * Вернуть список из клеток, по которым шахматная ладья может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов ладьи см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры: rookTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          (здесь возможен ещё один вариант)
 *          rookTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(3, 3), Square(6, 3))
 *          (здесь возможен единственный вариант)
 *          rookTrajectory(Square(3, 5), Square(8, 5)) = listOf(Square(3, 5), Square(8, 5))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun rookTrajectory(start: Square, end: Square): List<Square> {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    val result = mutableListOf(start)

    if (start == end) return result

    when {
        start.column == end.column -> {
            // Двигаемся по вертикали
            val step = if (end.row > start.row) 1 else -1
            var currentRow = start.row + step
            while (currentRow != end.row) {
                result.add(Square(start.column, currentRow))
                currentRow += step
            }
        }

        start.row == end.row -> {
            // Двигаемся по горизонтали
            val step = if (end.column > start.column) 1 else -1
            var currentColumn = start.column + step
            while (currentColumn != end.column) {
                result.add(Square(currentColumn, start.row))
                currentColumn += step
            }
        }

        else -> {
            // Сначала двигаемся по вертикали к промежуточной точке, потом по горизонтали
            val intermediate = Square(start.column, end.row)
            result.add(intermediate)
        }
    }

    result.add(end)
    return result
}

/**
 * Простая (2 балла)
 *
 * Определить число ходов, за которое шахматный слон пройдёт из клетки start в клетку end.
 * Шахматный слон может за один ход переместиться на любую другую клетку по диагонали.
 * Ниже точками выделены возможные ходы слона, а крестиками -- невозможные:
 *
 * .xxx.ххх
 * x.x.xххх
 * xxСxxxxx
 * x.x.xххх
 * .xxx.ххх
 * xxxxx.хх
 * xxxxxх.х
 * xxxxxхх.
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если клетка end недостижима для слона, вернуть -1.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Примеры: bishopMoveNumber(Square(3, 1), Square(6, 3)) = -1; bishopMoveNumber(Square(3, 1), Square(3, 7)) = 2.
 * Слон может пройти через клетку (6, 4) к клетке (3, 7).
 */
fun bishopMoveNumber(start: Square, end: Square): Int {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    if (start == end) return 0

    // Проверяем, находятся ли клетки на одном цвете
    val sameColor = (start.column + start.row) % 2 == (end.column + end.row) % 2

    if (!sameColor) return -1

    // Если клетки на одной диагонали, слон может дойти за 1 ход
    if (abs(start.column - end.column) == abs(start.row - end.row)) {
        return 1
    }

    return 2
}

/**
 * Сложная (5 баллов)
 *
 * Вернуть список из клеток, по которым шахматный слон может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов слона см. предыдущую задачу.
 *
 * Если клетка end недостижима для слона, вернуть пустой список.
 *
 * Если клетка достижима:
 * - список всегда включает в себя клетку start
 * - клетка end включается, если она не совпадает со start.
 * - между ними должны находиться промежуточные клетки, по порядку от start до end.
 *
 * Примеры: bishopTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          bishopTrajectory(Square(3, 1), Square(3, 7)) = listOf(Square(3, 1), Square(6, 4), Square(3, 7))
 *          bishopTrajectory(Square(1, 3), Square(6, 8)) = listOf(Square(1, 3), Square(6, 8))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun bishopTrajectory(start: Square, end: Square): List<Square> {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    val result = mutableListOf<Square>()

    if (start == end) {
        result.add(start)
        return result
    }

    // Проверяем, находятся ли клетки на одном цвете
    val sameColor = (start.column + start.row) % 2 == (end.column + end.row) % 2

    if (!sameColor) return result // Пустой список

    result.add(start)

    // Если клетки на одной диагонали
    if (abs(start.column - end.column) == abs(start.row - end.row)) {
        result.add(end)
        return result
    }

    // Решаем систему уравнений для точки пересечения диагоналей
    val (intermediate1, intermediate2) = findBishopIntermediatePoints(start, end)

    if (intermediate1.inside()) {
        result.add(intermediate1)
        result.add(end)
        return result
    } else if (intermediate2.inside()) {
        result.add(intermediate2)
        result.add(end)
        return result
    }

    return result
}

/**
 * Вспомогательная функция для нахождения промежуточных точек для слона
 */
private fun findBishopIntermediatePoints(start: Square, end: Square): Pair<Square, Square> {
    // Система уравнений для точки на обеих диагоналях:
    // x + y = start.column + start.row
    // x - y = start.column - start.row

    // Находим точку пересечения диагоналей start и end
    val x1 = (start.column + start.row + end.column - end.row) / 2
    val y1 = (start.column + start.row - end.column + end.row) / 2

    val x2 = (start.column - start.row + end.column + end.row) / 2
    val y2 = (-start.column + start.row + end.column + end.row) / 2

    return Pair(Square(x1, y1), Square(x2, y2))
}

/**
 * Средняя (3 балла)
 *
 * Определить число ходов, за которое шахматный король пройдёт из клетки start в клетку end.
 * Шахматный король одним ходом может переместиться из клетки, в которой стоит,
 * на любую соседнюю по вертикали, горизонтали или диагонали.
 * Ниже точками выделены возможные ходы короля, а крестиками -- невозможные:
 *
 * xxxxx
 * x...x
 * x.K.x
 * x...x
 * xxxxx
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: kingMoveNumber(Square(3, 1), Square(6, 3)) = 3.
 * Король может последовательно пройти через клетки (4, 2) и (5, 2) к клетке (6, 3).
 */
fun kingMoveNumber(start: Square, end: Square): Int {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    val columnDiff = abs(start.column - end.column)
    val rowDiff = abs(start.row - end.row)

    return maxOf(columnDiff, rowDiff)
}

/**
 * Сложная (5 баллов)
 *
 * Вернуть список из клеток, по которым шахматный король может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов короля см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры: kingTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          (здесь возможны другие варианты)
 *          kingTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(4, 2), Square(5, 2), Square(6, 3))
 *          (здесь возможен единственный вариант)
 *          kingTrajectory(Square(3, 5), Square(6, 2)) = listOf(Square(3, 5), Square(4, 4), Square(5, 3), Square(6, 2))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun kingTrajectory(start: Square, end: Square): List<Square> {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    val result = mutableListOf(start)

    if (start == end) return result

    var current = start

    while (current != end) {
        val nextColumn = when {
            current.column < end.column -> current.column + 1
            current.column > end.column -> current.column - 1
            else -> current.column
        }

        val nextRow = when {
            current.row < end.row -> current.row + 1
            current.row > end.row -> current.row - 1
            else -> current.row
        }

        current = Square(nextColumn, nextRow)
        result.add(current)
    }

    return result
}

/**
 * Сложная (6 баллов)
 *
 * Определить число ходов, за которое шахматный конь пройдёт из клетки start в клетку end.
 * Шахматный конь одним ходом вначале передвигается ровно на 2 клетки по горизонтали или вертикали,
 * а затем ещё на 1 клетку под прямым углом, образуя букву "Г".
 * Ниже точками выделены возможные ходы коня, а крестиками -- невозможные:
 *
 * .xxx.xxx
 * xxKxxxxx
 * .xxx.xxx
 * x.x.xxxx
 * xxxxxxxx
 * xxxxxxxx
 * xxxxxxxx
 * xxxxxxxx
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: knightMoveNumber(Square(3, 1), Square(6, 3)) = 3.
 * Конь может последовательно пройти через клетки (5, 2) и (4, 4) к клетке (6, 3).
 */
fun knightMoveNumber(start: Square, end: Square): Int {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    if (start == end) return 0

    // Используем BFS для поиска кратчайшего пути
    val visited = mutableSetOf<Square>()
    val queue = mutableListOf<Square>()
    val distance = mutableMapOf<Square, Int>()

    queue.add(start)
    visited.add(start)
    distance[start] = 0

    // Возможные ходы коня
    val knightMoves = listOf(
        Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1),
        Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2)
    )

    while (queue.isNotEmpty()) {
        val current = queue.removeAt(0)

        if (current == end) {
            return distance[current] ?: 0
        }

        for ((dx, dy) in knightMoves) {
            val newColumn = current.column + dx
            val newRow = current.row + dy

            if (newColumn in 1..8 && newRow in 1..8) {
                val nextSquare = Square(newColumn, newRow)

                if (nextSquare !in visited) {
                    visited.add(nextSquare)
                    queue.add(nextSquare)
                    distance[nextSquare] = distance[current]!! + 1
                }
            }
        }
    }

    return -1
}

/**
 * Очень сложная (10 баллов)
 *
 * Вернуть список из клеток, по которым шахматный конь может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов коня см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры:
 *
 * knightTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 * здесь возможны другие варианты)
 * knightTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(5, 2), Square(4, 4), Square(6, 3))
 * (здесь возможен единственный вариант)
 * knightTrajectory(Square(3, 5), Square(5, 6)) = listOf(Square(3, 5), Square(5, 6))
 * (здесь опять возможны другие варианты)
 * knightTrajectory(Square(7, 7), Square(8, 8)) =
 *     listOf(Square(7, 7), Square(5, 8), Square(4, 6), Square(6, 7), Square(8, 8))
 *
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun knightTrajectory(start: Square, end: Square): List<Square> {
    if (!start.inside() || !end.inside()) {
        throw IllegalArgumentException("Invalid square")
    }

    if (start == end) return listOf(start)

    // Используем BFS для поиска пути и восстановления траектории
    val visited = mutableSetOf<Square>()
    val queue = mutableListOf<Square>()
    val parent = mutableMapOf<Square, Square>()

    queue.add(start)
    visited.add(start)

    // Возможные ходы коня
    val knightMoves = listOf(
        Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1),
        Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2)
    )

    while (queue.isNotEmpty()) {
        val current = queue.removeAt(0)

        if (current == end) {
            // Восстанавливаем путь от end к start
            val path = mutableListOf<Square>()
            var node: Square? = end

            while (node != null) {
                path.add(node)
                node = parent[node]
            }

            return path.reversed()
        }

        for ((dx, dy) in knightMoves) {
            val newColumn = current.column + dx
            val newRow = current.row + dy

            if (newColumn in 1..8 && newRow in 1..8) {
                val nextSquare = Square(newColumn, newRow)

                if (nextSquare !in visited) {
                    visited.add(nextSquare)
                    queue.add(nextSquare)
                    parent[nextSquare] = current
                }
            }
        }
    }

    return emptyList() // Должно быть недостижимо для шахматной доски
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== Тестирование шахматных функций ===\n")

    // 1. Тестирование Square и нотации
    println("1. Тестирование Square и нотации:")
    val square1 = Square(1, 1)
    val square2 = Square(4, 5)
    val square3 = Square(8, 8)

    println("   Клетка (1,1): нотация = ${square1.notation()}")
    println("   Клетка (4,5): нотация = ${square2.notation()}")
    println("   Клетка (8,8): нотация = ${square3.notation()}")

    // 2. Тестирование функции square()
    println("\n2. Тестирование функции square():")
    println("   'a1' -> ${square("a1")}")
    println("   'd5' -> ${square("d5")}")
    println("   'h8' -> ${square("h8")}")

    // 3. Тестирование rookMoveNumber и rookTrajectory
    println("\n3. Тестирование ладьи:")
    val rookStart1 = Square(3, 1)
    val rookEnd1 = Square(6, 3)
    val rookStart2 = Square(3, 5)
    val rookEnd2 = Square(8, 5)

    println("   Ходов от ${rookStart1.notation()} до ${rookEnd1.notation()}: ${rookMoveNumber(rookStart1, rookEnd1)}")
    println("   Траектория: ${rookTrajectory(rookStart1, rookEnd1).joinToString { it.notation() }}")
    println("   Ходов от ${rookStart2.notation()} до ${rookEnd2.notation()}: ${rookMoveNumber(rookStart2, rookEnd2)}")
    println("   Траектория: ${rookTrajectory(rookStart2, rookEnd2).joinToString { it.notation() }}")

    // 4. Тестирование bishopMoveNumber и bishopTrajectory
    println("\n4. Тестирование слона:")
    val bishopStart1 = Square(3, 1)
    val bishopEnd1 = Square(6, 3)
    val bishopStart2 = Square(3, 1)
    val bishopEnd2 = Square(3, 7)
    val bishopStart3 = Square(1, 3)
    val bishopEnd3 = Square(6, 8)

    println("   Ходов от ${bishopStart1.notation()} до ${bishopEnd1.notation()}: ${bishopMoveNumber(bishopStart1, bishopEnd1)}")
    println("   Ходов от ${bishopStart2.notation()} до ${bishopEnd2.notation()}: ${bishopMoveNumber(bishopStart2, bishopEnd2)}")
    println("   Траектория: ${bishopTrajectory(bishopStart2, bishopEnd2).joinToString { it.notation() }}")
    println("   Ходов от ${bishopStart3.notation()} до ${bishopEnd3.notation()}: ${bishopMoveNumber(bishopStart3, bishopEnd3)}")
    println("   Траектория: ${bishopTrajectory(bishopStart3, bishopEnd3).joinToString { it.notation() }}")

    // 5. Тестирование kingMoveNumber и kingTrajectory
    println("\n5. Тестирование короля:")
    val kingStart = Square(3, 1)
    val kingEnd = Square(6, 3)

    println("   Ходов от ${kingStart.notation()} до ${kingEnd.notation()}: ${kingMoveNumber(kingStart, kingEnd)}")
    println("   Траектория: ${kingTrajectory(kingStart, kingEnd).joinToString { it.notation() }}")

    // 6. Тестирование knightMoveNumber и knightTrajectory
    println("\n6. Тестирование коня:")
    val knightStart1 = Square(3, 1)
    val knightEnd1 = Square(6, 3)
    val knightStart2 = Square(7, 7)
    val knightEnd2 = Square(8, 8)

    println("   Ходов от ${knightStart1.notation()} до ${knightEnd1.notation()}: ${knightMoveNumber(knightStart1, knightEnd1)}")
    println("   Траектория: ${knightTrajectory(knightStart1, knightEnd1).joinToString { it.notation() }}")
    println("   Ходов от ${knightStart2.notation()} до ${knightEnd2.notation()}: ${knightMoveNumber(knightStart2, knightEnd2)}")
    println("   Траектория: ${knightTrajectory(knightStart2, knightEnd2).joinToString { it.notation() }}")

    // 7. Дополнительные тесты
    println("\n7. Дополнительные тесты:")

    // Тест для слона с невозможным ходом
    val bishopStart4 = Square(1, 1)
    val bishopEnd4 = Square(2, 2)
    println("   Слон от ${bishopStart4.notation()} до ${bishopEnd4.notation()}: ${bishopMoveNumber(bishopStart4, bishopEnd4)} ход")
    println("   Траектория: ${bishopTrajectory(bishopStart4, bishopEnd4).joinToString { it.notation() }}")

    // Тест для коня короткий путь
    val knightStart3 = Square(1, 1)
    val knightEnd3 = Square(2, 3)
    println("   Конь от ${knightStart3.notation()} до ${knightEnd3.notation()}: ${knightMoveNumber(knightStart3, knightEnd3)} ход")
    println("   Траектория: ${knightTrajectory(knightStart3, knightEnd3).joinToString { it.notation() }}")

    println("\n=== Тестирование завершено ===")
}