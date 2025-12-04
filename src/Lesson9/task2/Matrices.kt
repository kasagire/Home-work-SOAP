import java.util.*
import kotlin.math.abs
/**
 * Сложная (5 баллов)
 *
 * Заполнить матрицу заданной высоты height и ширины width
 * натуральными числами от 1 до m*n по спирали,
 * начинающейся в левом верхнем углу и закрученной по часовой стрелке.
 *
 * Пример для height = 3, width = 4:
 *  1  2  3  4
 * 10 11 12  5
 *  9  8  7  6
 */
fun generateSpiral(height: Int, width: Int): Matrix<Int> {
    if (height <= 0 || width <= 0) {
        throw IllegalArgumentException("Height and width must be positive")
    }

    val matrix = MatrixImpl<Int>(height, width, 0)
    var top = 0
    var bottom = height - 1
    var left = 0
    var right = width - 1
    var num = 1

    while (top <= bottom && left <= right) {
        // Верхняя строка слева направо
        for (i in left..right) {
            matrix[top, i] = num++
        }
        top++

        // Правый столбец сверху вниз
        for (i in top..bottom) {
            matrix[i, right] = num++
        }
        right--

        // Нижняя строка справа налево
        if (top <= bottom) {
            for (i in right downTo left) {
                matrix[bottom, i] = num++
            }
            bottom--
        }

        // Левый столбец снизу вверх
        if (left <= right) {
            for (i in bottom downTo top) {
                matrix[i, left] = num++
            }
            left++
        }
    }

    return matrix
}

/**
 * Сложная (5 баллов)
 *
 * Заполнить матрицу заданной высоты height и ширины width следующим образом.
 * Элементам, находящимся на периферии (по периметру матрицы), присвоить значение 1;
 * периметру оставшейся подматрицы – значение 2 и так далее до заполнения всей матрицы.
 *
 * Пример для height = 5, width = 6:
 *  1  1  1  1  1  1
 *  1  2  2  2  2  1
 *  1  2  3  3  2  1
 *  1  2  2  2  2  1
 *  1  1  1  1  1  1
 */
fun generateRectangles(height: Int, width: Int): Matrix<Int> {
    if (height <= 0 || width <= 0) {
        throw IllegalArgumentException("Height and width must be positive")
    }

    val matrix = MatrixImpl<Int>(height, width, 0)

    for (i in 0 until height) {
        for (j in 0 until width) {
            // Вычисляем расстояние до ближайшего края
            val distanceToTop = i
            val distanceToBottom = height - 1 - i
            val distanceToLeft = j
            val distanceToRight = width - 1 - j

            // Минимальное расстояние до края + 1
            val minDistance = minOf(distanceToTop, distanceToBottom, distanceToLeft, distanceToRight)
            matrix[i, j] = minDistance + 1
        }
    }

    return matrix
}

/**
 * Сложная (5 баллов)
 *
 * Заполнить матрицу заданной высоты height и ширины width диагональной змейкой:
 * в левый верхний угол 1, во вторую от угла диагональ 2 и 3 сверху вниз, в третью 4-6 сверху вниз и так далее.
 *
 * Пример для height = 5, width = 4:
 *  1  2  4  7
 *  3  5  8 11
 *  6  9 12 15
 * 10 13 16 18
 * 14 17 19 20
 */
fun generateSnake(height: Int, width: Int): Matrix<Int> {
    if (height <= 0 || width <= 0) {
        throw IllegalArgumentException("Height and width must be positive")
    }

    val matrix = MatrixImpl<Int>(height, width, 0)
    var num = 1

    // Всего диагоналей: height + width - 1
    for (d in 0 until height + width - 1) {
        // Начальная строка для диагонали
        val startRow = maxOf(0, d - width + 1)
        // Конечная строка для диагонали
        val endRow = minOf(d, height - 1)

        // Заполняем диагональ сверху вниз
        for (i in startRow..endRow) {
            val j = d - i
            if (j in 0 until width) {
                matrix[i, j] = num++
            }
        }
    }

    return matrix
}

/**
 * Средняя (3 балла)
 *
 * Содержимое квадратной матрицы matrix (с произвольным содержимым) повернуть на 90 градусов по часовой стрелке.
 * Если height != width, бросить IllegalArgumentException.
 *
 * Пример:    Станет:
 * 1 2 3      7 4 1
 * 4 5 6      8 5 2
 * 7 8 9      9 6 3
 */
fun <E> rotate(matrix: Matrix<E>): Matrix<E> {
    if (matrix.height != matrix.width) {
        throw IllegalArgumentException("Matrix must be square to rotate")
    }

    val size = matrix.height
    val result = MatrixImpl<E>(size, size)

    for (i in 0 until size) {
        for (j in 0 until size) {
            result[i, j] = matrix[size - 1 - j, i]
        }
    }

    return result
}

/**
 * Сложная (5 баллов)
 *
 * Проверить, является ли квадратная целочисленная матрица matrix латинским квадратом.
 * Латинским квадратом называется матрица размером n x n,
 * каждая строка и каждый столбец которой содержат все числа от 1 до n.
 * Если height != width, вернуть false.
 *
 * Пример латинского квадрата 3х3:
 * 2 3 1
 * 1 2 3
 * 3 1 2
 */
fun isLatinSquare(matrix: Matrix<Int>): Boolean {
    if (matrix.height != matrix.width) return false

    val n = matrix.height
    val expectedSet = (1..n).toSet()

    // Проверяем строки
    for (i in 0 until n) {
        val rowSet = mutableSetOf<Int>()
        for (j in 0 until n) {
            val value = matrix[i, j]
            if (value !in 1..n) return false
            rowSet.add(value)
        }
        if (rowSet != expectedSet) return false
    }

    // Проверяем столбцы
    for (j in 0 until n) {
        val colSet = mutableSetOf<Int>()
        for (i in 0 until n) {
            colSet.add(matrix[i, j])
        }
        if (colSet != expectedSet) return false
    }

    return true
}

/**
 * Средняя (3 балла)
 *
 * В матрице matrix каждый элемент заменить суммой непосредственно примыкающих к нему
 * элементов по вертикали, горизонтали и диагоналям.
 *
 * Пример для матрицы 4 x 3: (11=2+4+5, 19=1+3+4+5+6, ...)
 * 1 2 3       11 19 13
 * 4 5 6  ===> 19 31 19
 * 6 5 4       19 31 19
 * 3 2 1       13 19 11
 *
 * Поскольку в матрице 1 х 1 примыкающих элементов отсутствует,
 * для неё следует вернуть как результат нулевую матрицу:
 *
 * 42 ===> 0
 */
fun sumNeighbours(matrix: Matrix<Int>): Matrix<Int> {
    val result = MatrixImpl<Int>(matrix.height, matrix.width, 0)

    if (matrix.height == 1 && matrix.width == 1) {
        return result
    }

    // Соседние направления: все 8 направлений вокруг клетки
    val directions = listOf(
        Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
        Pair(0, -1),              Pair(0, 1),
        Pair(1, -1),  Pair(1, 0),  Pair(1, 1)
    )

    for (i in 0 until matrix.height) {
        for (j in 0 until matrix.width) {
            var sum = 0
            for ((di, dj) in directions) {
                val ni = i + di
                val nj = j + dj
                if (ni in 0 until matrix.height && nj in 0 until matrix.width) {
                    sum += matrix[ni, nj]
                }
            }
            result[i, j] = sum
        }
    }

    return result
}

/**
 * Средняя (4 балла)
 *
 * Целочисленная матрица matrix состоит из "дырок" (на их месте стоит 0) и "кирпичей" (на их месте стоит 1).
 * Найти в этой матрице все ряды и колонки, целиком состоящие из "дырок".
 * Результат вернуть в виде Holes(rows = список дырчатых рядов, columns = список дырчатых колонок).
 * Ряды и колонки нумеруются с нуля. Любой из спискоов rows / columns может оказаться пустым.
 *
 * Пример для матрицы 5 х 4:
 * 1 0 1 0
 * 0 0 1 0
 * 1 0 0 0 ==> результат: Holes(rows = listOf(4), columns = listOf(1, 3)): 4-й ряд, 1-я и 3-я колонки
 * 0 0 1 0
 * 0 0 0 0
 */
fun findHoles(matrix: Matrix<Int>): Holes {
    val emptyRows = mutableListOf<Int>()
    val emptyColumns = mutableListOf<Int>()

    // Проверяем строки
    for (i in 0 until matrix.height) {
        var allZeros = true
        for (j in 0 until matrix.width) {
            if (matrix[i, j] != 0) {
                allZeros = false
                break
            }
        }
        if (allZeros) {
            emptyRows.add(i)
        }
    }

    // Проверяем столбцы
    for (j in 0 until matrix.width) {
        var allZeros = true
        for (i in 0 until matrix.height) {
            if (matrix[i, j] != 0) {
                allZeros = false
                break
            }
        }
        if (allZeros) {
            emptyColumns.add(j)
        }
    }

    return Holes(emptyRows, emptyColumns)
}

/**
 * Класс для описания местонахождения "дырок" в матрице
 */
data class Holes(val rows: List<Int>, val columns: List<Int>)

/**
 * Средняя (3 балла)
 *
 * В целочисленной матрице matrix каждый элемент заменить суммой элементов подматрицы,
 * расположенной в левом верхнем углу матрицы matrix и ограниченной справа-снизу данным элементом.
 *
 * Пример для матрицы 3 х 3:
 *
 * 1  2  3      1  3  6
 * 4  5  6  =>  5 12 21
 * 7  8  9     12 27 45
 *
 * К примеру, центральный элемент 12 = 1 + 2 + 4 + 5, элемент в левом нижнем углу 12 = 1 + 4 + 7 и так далее.
 */
fun sumSubMatrix(matrix: Matrix<Int>): Matrix<Int> {
    val result = MatrixImpl<Int>(matrix.height, matrix.width, 0)

    for (i in 0 until matrix.height) {
        for (j in 0 until matrix.width) {
            var sum = 0
            for (x in 0..i) {
                for (y in 0..j) {
                    sum += matrix[x, y]
                }
            }
            result[i, j] = sum
        }
    }

    return result
}

/**
 * Простая (2 балла)
 *
 * Инвертировать заданную матрицу.
 * При инвертировании знак каждого элемента матрицы следует заменить на обратный
 */
operator fun Matrix<Int>.unaryMinus(): Matrix<Int> {
    val result = MatrixImpl<Int>(this.height, this.width, 0)

    for (i in 0 until this.height) {
        for (j in 0 until this.width) {
            result[i, j] = -this[i, j]
        }
    }

    return result
}

/**
 * Средняя (4 балла)
 *
 * Перемножить две заданные матрицы друг с другом.
 * Матрицы можно умножать, только если ширина первой матрицы совпадает с высотой второй матрицы.
 * В противном случае бросить IllegalArgumentException.
 * Подробно про порядок умножения см. статью Википедии "Умножение матриц".
 */
operator fun Matrix<Int>.times(other: Matrix<Int>): Matrix<Int> {
    if (this.width != other.height) {
        throw IllegalArgumentException(
            "Cannot multiply matrices: first.width=${this.width} != second.height=${other.height}"
        )
    }

    val result = MatrixImpl<Int>(this.height, other.width, 0)

    for (i in 0 until this.height) {
        for (j in 0 until other.width) {
            var sum = 0
            for (k in 0 until this.width) {
                sum += this[i, k] * other[k, j]
            }
            result[i, j] = sum
        }
    }

    return result
}

/**
 * Сложная (7 баллов)
 *
 * Даны мозаичные изображения замочной скважины и ключа. Пройдет ли ключ в скважину?
 * То есть даны две матрицы key и lock, key.height <= lock.height, key.width <= lock.width, состоящие из нулей и единиц.
 *
 * Проверить, можно ли наложить матрицу key на матрицу lock (без поворота, разрешается только сдвиг) так,
 * чтобы каждой единице в матрице lock (штырь) соответствовал ноль в матрице key (прорезь),
 * а каждому нулю в матрице lock (дырка) соответствовала, наоборот, единица в матрице key (штырь).
 * Ключ при сдвиге не может выходить за пределы замка.
 *
 * Пример: ключ подойдёт, если его сдвинуть на 1 по ширине
 * lock    key
 * 1 0 1   1 0
 * 0 1 0   0 1
 * 1 1 1
 *
 * Вернуть тройку (Triple) -- (да/нет, требуемый сдвиг по высоте, требуемый сдвиг по ширине).
 * Если наложение невозможно, то первый элемент тройки "нет" и сдвиги могут быть любыми.
 */
fun canOpenLock(key: Matrix<Int>, lock: Matrix<Int>): Triple<Boolean, Int, Int> {
    // Возможные сдвиги ключа относительно замка
    for (shiftRow in 0..(lock.height - key.height)) {
        for (shiftCol in 0..(lock.width - key.width)) {
            var fits = true

            // Проверяем все ячейки ключа
            for (i in 0 until key.height) {
                for (j in 0 until key.width) {
                    val lockValue = lock[i + shiftRow, j + shiftCol]
                    val keyValue = key[i, j]

                    // Проверяем условие: lock=1 -> key=0 и lock=0 -> key=1
                    if (!((lockValue == 1 && keyValue == 0) || (lockValue == 0 && keyValue == 1))) {
                        fits = false
                        break
                    }
                }
                if (!fits) break
            }

            if (fits) {
                return Triple(true, shiftRow, shiftCol)
            }
        }
    }

    return Triple(false, -1, -1)
}

/**
 * Сложная (8 баллов)
 *
 * В матрице matrix размером 4х4 дана исходная позиция для игры в 15, например
 *  5  7  9  1
 *  2 12 14 15
 *  3  4  6  8
 * 10 11 13  0
 *
 * Здесь 0 обозначает пустую клетку, а 1-15 – фишки с соответствующими номерами.
 * Напомним, что "игра в 15" имеет квадратное поле 4х4, по которому двигается 15 фишек,
 * одна клетка всегда остаётся пустой. Цель игры -- упорядочить фишки на игровом поле.
 *
 * В списке moves задана последовательность ходов, например [8, 6, 13, 11, 10, 3].
 * Ход задаётся номером фишки, которая передвигается на пустое место (то есть, меняется местами с нулём).
 * Фишка должна примыкать к пустому месту по горизонтали или вертикали, иначе ход не будет возможным.
 * Все номера должны быть в пределах от 1 до 15.
 * Определить финальную позицию после выполнения всех ходов и вернуть её.
 * Если какой-либо ход является невозможным или список содержит неверные номера,
 * бросить IllegalStateException.
 *
 * В данном случае должно получиться
 * 5  7  9  1
 * 2 12 14 15
 * 0  4 13  6
 * 3 10 11  8
 */
fun fifteenGameMoves(matrix: Matrix<Int>, moves: List<Int>): Matrix<Int> {
    if (matrix.height != 4 || matrix.width != 4) {
        throw IllegalArgumentException("Matrix must be 4x4 for 15 game")
    }

    // Копируем матрицу
    val result = MatrixImpl<Int>(4, 4)
    for (i in 0 until 4) {
        for (j in 0 until 4) {
            result[i, j] = matrix[i, j]
        }
    }

    // Находим позицию пустой клетки
    var emptyRow = -1
    var emptyCol = -1

    for (i in 0 until 4) {
        for (j in 0 until 4) {
            if (result[i, j] == 0) {
                emptyRow = i
                emptyCol = j
                break
            }
        }
        if (emptyRow != -1) break
    }

    // Выполняем ходы
    for (move in moves) {
        if (move !in 1..15) {
            throw IllegalStateException("Invalid move number: $move")
        }

        // Находим позицию фишки
        var pieceRow = -1
        var pieceCol = -1

        for (i in 0 until 4) {
            for (j in 0 until 4) {
                if (result[i, j] == move) {
                    pieceRow = i
                    pieceCol = j
                    break
                }
            }
            if (pieceRow != -1) break
        }

        // Проверяем, что фишка найдена
        if (pieceRow == -1) {
            throw IllegalStateException("Piece $move not found")
        }

        // Проверяем, что фишка примыкает к пустой клетке
        val isAdjacent = (abs(pieceRow - emptyRow) == 1 && pieceCol == emptyCol) ||
                (abs(pieceCol - emptyCol) == 1 && pieceRow == emptyRow)

        if (!isAdjacent) {
            throw IllegalStateException("Move $move is not adjacent to empty cell")
        }

        // Меняем фишку и пустую клетку местами
        result[pieceRow, pieceCol] = 0
        result[emptyRow, emptyCol] = move

        // Обновляем позицию пустой клетки
        emptyRow = pieceRow
        emptyCol = pieceCol
    }

    return result
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== ТЕСТИРОВАНИЕ МАТРИЧНЫХ ФУНКЦИЙ ===\n")

    // 1. Тестирование generateSpiral
    println("1. ГЕНЕРАЦИЯ СПИРАЛИ:")
    val spiral = generateSpiral(3, 4)
    println("   Спираль 3x4:")
    println("   $spiral")

    val spiral2 = generateSpiral(4, 4)
    println("\n   Спираль 4x4:")
    println("   $spiral2")

    // 2. Тестирование generateRectangles
    println("\n2. ГЕНЕРАЦИЯ ВЛОЖЕННЫХ ПРЯМОУГОЛЬНИКОВ:")
    val rectangles = generateRectangles(5, 6)
    println("   Прямоугольники 5x6:")
    println("   $rectangles")

    // 3. Тестирование generateSnake
    println("\n3. ГЕНЕРАЦИЯ ДИАГОНАЛЬНОЙ ЗМЕЙКИ:")
    val snake = generateSnake(5, 4)
    println("   Змейка 5x4:")
    println("   $snake")

    // 4. Тестирование rotate
    println("\n4. ПОВОРОТ МАТРИЦЫ:")
    val matrix3x3 = MatrixImpl<Int>(3, 3)
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            matrix3x3[i, j] = i * 3 + j + 1
        }
    }
    println("   Исходная матрица 3x3:")
    println("   $matrix3x3")

    val rotated = rotate(matrix3x3)
    println("   Повернутая на 90° по часовой стрелке:")
    println("   $rotated")

    // 5. Тестирование isLatinSquare
    println("\n5. ПРОВЕРКА ЛАТИНСКОГО КВАДРАТА:")
    val latinSquare = MatrixImpl<Int>(3, 3)
    latinSquare[0, 0] = 2; latinSquare[0, 1] = 3; latinSquare[0, 2] = 1
    latinSquare[1, 0] = 1; latinSquare[1, 1] = 2; latinSquare[1, 2] = 3
    latinSquare[2, 0] = 3; latinSquare[2, 1] = 1; latinSquare[2, 2] = 2

    val notLatinSquare = MatrixImpl<Int>(3, 3)
    notLatinSquare[0, 0] = 1; notLatinSquare[0, 1] = 2; notLatinSquare[0, 2] = 3
    notLatinSquare[1, 0] = 4; notLatinSquare[1, 1] = 5; notLatinSquare[1, 2] = 6
    notLatinSquare[2, 0] = 7; notLatinSquare[2, 1] = 8; notLatinSquare[2, 2] = 9

    println("   Матрица 1 - латинский квадрат: ${isLatinSquare(latinSquare)}")
    println("   Матрица 2 - латинский квадрат: ${isLatinSquare(notLatinSquare)}")

    // 6. Тестирование sumNeighbours
    println("\n6. СУММА СОСЕДЕЙ:")
    val matrix4x3 = MatrixImpl<Int>(4, 3)
    matrix4x3[0, 0] = 1; matrix4x3[0, 1] = 2; matrix4x3[0, 2] = 3
    matrix4x3[1, 0] = 4; matrix4x3[1, 1] = 5; matrix4x3[1, 2] = 6
    matrix4x3[2, 0] = 6; matrix4x3[2, 1] = 5; matrix4x3[2, 2] = 4
    matrix4x3[3, 0] = 3; matrix4x3[3, 1] = 2; matrix4x3[3, 2] = 1

    println("   Исходная матрица 4x3:")
    println("   $matrix4x3")

    val neighboursSum = sumNeighbours(matrix4x3)
    println("   Сумма соседей:")
    println("   $neighboursSum")

    // 7. Тестирование findHoles
    println("\n7. ПОИСК ДЫРОК:")
    val holesMatrix = MatrixImpl<Int>(5, 4)
    holesMatrix[0, 0] = 1; holesMatrix[0, 1] = 0; holesMatrix[0, 2] = 1; holesMatrix[0, 3] = 0
    holesMatrix[1, 0] = 0; holesMatrix[1, 1] = 0; holesMatrix[1, 2] = 1; holesMatrix[1, 3] = 0
    holesMatrix[2, 0] = 1; holesMatrix[2, 1] = 0; holesMatrix[2, 2] = 0; holesMatrix[2, 3] = 0
    holesMatrix[3, 0] = 0; holesMatrix[3, 1] = 0; holesMatrix[3, 2] = 1; holesMatrix[3, 3] = 0
    holesMatrix[4, 0] = 0; holesMatrix[4, 1] = 0; holesMatrix[4, 2] = 0; holesMatrix[4, 3] = 0

    println("   Матрица 5x4 для поиска дырок:")
    println("   $holesMatrix")

    val holes = findHoles(holesMatrix)
    println("   Результат: rows=${holes.rows}, columns=${holes.columns}")

    // 8. Тестирование sumSubMatrix
    println("\n8. СУММА ПОДМАТРИЦ:")
    val subMatrix = MatrixImpl<Int>(3, 3)
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            subMatrix[i, j] = i * 3 + j + 1
        }
    }
    println("   Исходная матрица 3x3:")
    println("   $subMatrix")

    val summed = sumSubMatrix(subMatrix)
    println("   Суммы подматриц:")
    println("   $summed")

    // 9. Тестирование унарного минуса
    println("\n9. ИНВЕРТИРОВАНИЕ МАТРИЦЫ:")
    println("   Исходная: $subMatrix")
    println("   Инвертированная: ${-subMatrix}")

    // 10. Тестирование умножения матриц
    println("\n10. УМНОЖЕНИЕ МАТРИЦ:")
    val a = MatrixImpl<Int>(2, 3)
    a[0, 0] = 1; a[0, 1] = 2; a[0, 2] = 3
    a[1, 0] = 4; a[1, 1] = 5; a[1, 2] = 6

    val b = MatrixImpl<Int>(3, 2)
    b[0, 0] = 7; b[0, 1] = 8
    b[1, 0] = 9; b[1, 1] = 10
    b[2, 0] = 11; b[2, 1] = 12

    println("   Матрица A (2x3):")
    println("   $a")
    println("   Матрица B (3x2):")
    println("   $b")

    val product = a * b
    println("   Результат A × B (2x2):")
    println("   $product")

    // 11. Тестирование canOpenLock
    println("\n11. ПРОВЕРКА КЛЮЧА И ЗАМКА:")
    val lock = MatrixImpl<Int>(3, 3)
    lock[0, 0] = 1; lock[0, 1] = 0; lock[0, 2] = 1
    lock[1, 0] = 0; lock[1, 1] = 1; lock[1, 2] = 0
    lock[2, 0] = 1; lock[2, 1] = 1; lock[2, 2] = 1

    val key = MatrixImpl<Int>(2, 2)
    key[0, 0] = 1; key[0, 1] = 0
    key[1, 0] = 0; key[1, 1] = 1

    println("   Замок 3x3:")
    println("   $lock")
    println("   Ключ 2x2:")
    println("   $key")

    val lockResult = canOpenLock(key, lock)
    println("   Результат: fits=${lockResult.first}, rowShift=${lockResult.second}, colShift=${lockResult.third}")

    // 12. Тестирование fifteenGameMoves
    println("\n12. ИГРА В 15 (ПРОСТЫЕ ХОДЫ):")
    val gameMatrix = MatrixImpl<Int>(4, 4)
    gameMatrix[0, 0] = 5; gameMatrix[0, 1] = 7; gameMatrix[0, 2] = 9; gameMatrix[0, 3] = 1
    gameMatrix[1, 0] = 2; gameMatrix[1, 1] = 12; gameMatrix[1, 2] = 14; gameMatrix[1, 3] = 15
    gameMatrix[2, 0] = 3; gameMatrix[2, 1] = 4; gameMatrix[2, 2] = 6; gameMatrix[2, 3] = 8
    gameMatrix[3, 0] = 10; gameMatrix[3, 1] = 11; gameMatrix[3, 2] = 13; gameMatrix[3, 3] = 0

    val moves = listOf(8, 6, 13, 11, 10, 3)

    println("   Начальная позиция:")
    println("   $gameMatrix")
    println("   Ходы: $moves")

    try {
        val finalPos = fifteenGameMoves(gameMatrix, moves)
        println("   Финальная позиция:")
        println("   $finalPos")
    } catch (e: IllegalStateException) {
        println("   Ошибка: ${e.message}")
    }

    println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")
}