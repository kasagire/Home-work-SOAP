import java.util.*

/**
 * Ячейка матрицы: row = ряд, column = колонка
 */
data class Cell(val row: Int, val column: Int)

/**
 * Интерфейс, описывающий возможности матрицы. E = тип элемента матрицы
 */
interface Matrix<E> {
    /** Высота */
    val height: Int

    /** Ширина */
    val width: Int

    /**
     * Доступ к ячейке.
     * Методы могут бросить исключение, если ячейка не существует или пуста
     */
    operator fun get(row: Int, column: Int): E

    operator fun get(cell: Cell): E

    /**
     * Запись в ячейку.
     * Методы могут бросить исключение, если ячейка не существует
     */
    operator fun set(row: Int, column: Int, value: E)

    operator fun set(cell: Cell, value: E)
}

/**
 * Простая (2 балла)
 *
 * Метод для создания матрицы, должен вернуть РЕАЛИЗАЦИЮ Matrix<E>.
 * height = высота, width = ширина, e = чем заполнить элементы.
 * Бросить исключение IllegalArgumentException, если height или width <= 0.
 */
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> {
    if (height <= 0 || width <= 0) {
        throw IllegalArgumentException("Height and width must be positive. Got height=$height, width=$width")
    }
    return MatrixImpl(height, width, e)
}

/**
 * Средняя сложность (считается двумя задачами в 3 балла каждая)
 *
 * Реализация интерфейса "матрица"
 */
class MatrixImpl<E> : Matrix<E> {

    private val data: MutableList<MutableList<E?>>

    override val height: Int
    override val width: Int

    /**
     * Конструктор матрицы с заполнением всех ячеек одним значением
     */
    constructor(height: Int, width: Int, initialValue: E) {
        if (height <= 0 || width <= 0) {
            throw IllegalArgumentException("Height and width must be positive. Got height=$height, width=$width")
        }
        this.height = height
        this.width = width
        // Инициализируем список списков с начальным значением
        this.data = MutableList(height) { MutableList(width) { initialValue } }
    }

    /**
     * Конструктор пустой матрицы
     */
    constructor(height: Int, width: Int) {
        if (height <= 0 || width <= 0) {
            throw IllegalArgumentException("Height and width must be positive. Got height=$height, width=$width")
        }
        this.height = height
        this.width = width
        // Инициализируем список списков с null значениями
        this.data = MutableList(height) { MutableList<E?>(width) { null } }
    }

    /**
     * Проверка индексов на корректность
     */
    private fun checkIndices(row: Int, column: Int) {
        if (row < 0 || row >= height || column < 0 || column >= width) {
            throw IndexOutOfBoundsException(
                "Index ($row, $column) is out of bounds. Matrix size: $height x $width"
            )
        }
    }

    override fun get(row: Int, column: Int): E {
        checkIndices(row, column)
        return data[row][column] ?: throw NoSuchElementException("Cell ($row, $column) is empty")
    }

    override fun get(cell: Cell): E {
        return get(cell.row, cell.column)
    }

    override fun set(row: Int, column: Int, value: E) {
        checkIndices(row, column)
        data[row][column] = value
    }

    override fun set(cell: Cell, value: E) {
        set(cell.row, cell.column, value)
    }

    /**
     * Преобразование матрицы в строку для отладки
     */
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Matrix($height x $width):\n")
        for (row in 0 until height) {
            builder.append("[")
            for (col in 0 until width) {
                val value = data[row][col]
                builder.append(value?.toString() ?: "null")
                if (col < width - 1) builder.append(", ")
            }
            builder.append("]")
            if (row < height - 1) builder.append("\n")
        }
        return builder.toString()
    }

    /**
     * Сравнение матриц по содержимому
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MatrixImpl<*>) return false
        if (height != other.height || width != other.width) return false

        for (row in 0 until height) {
            for (col in 0 until width) {
                if (data[row][col] != other.data[row][col]) return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + data.hashCode()
        return result
    }

    /**
     * Проверяет, является ли матрица квадратной
     */
    fun isSquare(): Boolean = height == width

    /**
     * Получает строку матрицы
     */
    fun getRow(row: Int): List<E> {
        checkIndices(row, 0)
        return (0 until width).map { col ->
            data[row][col] ?: throw NoSuchElementException("Cell ($row, $col) is empty")
        }
    }

    /**
     * Получает столбец матрицы
     */
    fun getColumn(column: Int): List<E> {
        checkIndices(0, column)
        return (0 until height).map { row ->
            data[row][column] ?: throw NoSuchElementException("Cell ($row, $column) is empty")
        }
    }

    /**
     * Заполняет всю матрицу одним значением
     */
    fun fill(value: E) {
        for (row in 0 until height) {
            for (col in 0 until width) {
                data[row][col] = value
            }
        }
    }

    /**
     * Транспонирование матрицы
     */
    fun transpose() {
        if (!isSquare()) {
            throw IllegalStateException("Transpose is only available for square matrices")
        }

        for (i in 0 until height) {
            for (j in i + 1 until width) {
                val temp = data[i][j]
                data[i][j] = data[j][i]
                data[j][i] = temp
            }
        }
    }

    /**
     * Копирует матрицу
     */
    fun copy(): MatrixImpl<E> {
        val copy = MatrixImpl<E>(height, width)
        for (row in 0 until height) {
            for (col in 0 until width) {
                copy.data[row][col] = data[row][col]
            }
        }
        return copy
    }
}

/**
 * Создает единичную матрицу заданного размера
 */
fun createIdentityMatrix(size: Int): MatrixImpl<Int> {
    val matrix = MatrixImpl<Int>(size, size, 0)
    for (i in 0 until size) {
        matrix[i, i] = 1
    }
    return matrix
}

/**
 * Создает матрицу с последовательными числами
 */
fun createSequenceMatrix(height: Int, width: Int, start: Int = 1): MatrixImpl<Int> {
    val matrix = MatrixImpl<Int>(height, width)
    var counter = start
    for (row in 0 until height) {
        for (col in 0 until width) {
            matrix[row, col] = counter++
        }
    }
    return matrix
}

/**
 * Умножение матриц (только для совместимых размеров)
 */
fun multiplyMatrices(a: MatrixImpl<Int>, b: MatrixImpl<Int>): MatrixImpl<Int> {
    if (a.width != b.height) {
        throw IllegalArgumentException(
            "Cannot multiply matrices: a.width=${a.width} != b.height=${b.height}"
        )
    }

    val result = MatrixImpl<Int>(a.height, b.width, 0)

    for (i in 0 until a.height) {
        for (j in 0 until b.width) {
            var sum = 0
            for (k in 0 until a.width) {
                sum += a[i, k] * b[k, j]
            }
            result[i, j] = sum
        }
    }

    return result
}

/**
 * Сложение матриц
 */
fun addMatrices(a: MatrixImpl<Int>, b: MatrixImpl<Int>): MatrixImpl<Int> {
    if (a.height != b.height || a.width != b.width) {
        throw IllegalArgumentException("Matrices must have the same dimensions for addition")
    }

    val result = MatrixImpl<Int>(a.height, a.width, 0)

    for (row in 0 until a.height) {
        for (col in 0 until a.width) {
            result[row, col] = a[row, col] + b[row, col]
        }
    }

    return result
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== ТЕСТИРОВАНИЕ РАБОТЫ С МАТРИЦАМИ ===\n")

    // 1. Тестирование создания матриц
    println("1. СОЗДАНИЕ МАТРИЦ:")
    try {
        val matrix1 = createMatrix(3, 4, 0)
        println("   Создана матрица 3x4, заполненная нулями:")
        println("   $matrix1")

        val matrix2 = MatrixImpl<String>(2, 3, "empty")
        println("\n   Создана матрица 2x3 строк, заполненная 'empty':")
        println("   $matrix2")

        // Тест с неверными размерами
        try {
            createMatrix(0, 5, 1)
        } catch (e: IllegalArgumentException) {
            println("\n   Ошибка при создании матрицы 0x5: ${e.message}")
        }
    } catch (e: Exception) {
        println("   Ошибка: ${e.message}")
    }

    // 2. Тестирование доступа к элементам
    println("\n2. ДОСТУП И ИЗМЕНЕНИЕ ЭЛЕМЕНТОВ:")
    val matrix = MatrixImpl<Int>(3, 3)

    // Заполняем матрицу значениями
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            matrix[i, j] = i * 3 + j + 1
        }
    }

    println("   Матрица 3x3 с числами от 1 до 9:")
    println("   $matrix")

    println("   Элемент [1,2] = ${matrix[1, 2]}")
    println("   Элемент Cell(0,1) = ${matrix[Cell(0, 1)]}")

    // Изменяем элемент
    matrix[1, 1] = 99
    println("   После изменения matrix[1,1] = 99:")
    println("   $matrix")

    // 3. Тестирование проверки границ
    println("\n3. ПРОВЕРКА ГРАНИЦ:")
    try {
        matrix[5, 5]
    } catch (e: IndexOutOfBoundsException) {
        println("   Ошибка при доступе к несуществующему индексу: ${e.message}")
    }

    try {
        matrix[-1, 0]
    } catch (e: IndexOutOfBoundsException) {
        println("   Ошибка при доступе к отрицательному индексу: ${e.message}")
    }

    // Тест пустой ячейки
    val emptyMatrix = MatrixImpl<Int>(2, 2)
    try {
        emptyMatrix[0, 0]
    } catch (e: NoSuchElementException) {
        println("   Ошибка при доступе к пустой ячейке: ${e.message}")
    }

    // 4. Тестирование сравнения матриц
    println("\n4. СРАВНЕНИЕ МАТРИЦ:")
    val matrixA = MatrixImpl<Int>(2, 2)
    matrixA[0, 0] = 1; matrixA[0, 1] = 2
    matrixA[1, 0] = 3; matrixA[1, 1] = 4

    val matrixB = MatrixImpl<Int>(2, 2)
    matrixB[0, 0] = 1; matrixB[0, 1] = 2
    matrixB[1, 0] = 3; matrixB[1, 1] = 4

    val matrixC = MatrixImpl<Int>(2, 2)
    matrixC[0, 0] = 1; matrixC[0, 1] = 2
    matrixC[1, 0] = 3; matrixC[1, 1] = 5

    println("   matrixA == matrixB: ${matrixA == matrixB}")
    println("   matrixA == matrixC: ${matrixA == matrixC}")
    println("   matrixA.hashCode(): ${matrixA.hashCode()}")
    println("   matrixB.hashCode(): ${matrixB.hashCode()}")

    // 5. Тестирование дополнительных методов
    println("\n5. ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ:")

    println("   Матрица является квадратной: ${matrix.isSquare()}")
    println("   Первая строка матрицы: ${matrix.getRow(0)}")
    println("   Второй столбец матрицы: ${matrix.getColumn(1)}")

    // Копирование матрицы
    val original = MatrixImpl<Int>(2, 2)
    original[0, 0] = 1; original[0, 1] = 2
    original[1, 0] = 3; original[1, 1] = 4

    val copied = original.copy()
    println("\n   Оригинальная матрица: $original")
    println("   Скопированная матрица: $copied")
    println("   Копия равна оригиналу: ${original == copied}")

    // Изменяем копию
    copied[0, 0] = 100
    println("   После изменения копии:")
    println("   Оригинал: $original")
    println("   Копия: $copied")
    println("   Копия равна оригиналу: ${original == copied}")

    // Транспонирование
    println("\n   Транспонирование матрицы:")
    val squareMatrix = MatrixImpl<Int>(3, 3)
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            squareMatrix[i, j] = i * 3 + j + 1
        }
    }
    println("   Исходная матрица:")
    println("   $squareMatrix")

    squareMatrix.transpose()
    println("   Транспонированная матрица:")
    println("   $squareMatrix")

    // Тест транспонирования неквадратной матрицы
    val nonSquareMatrix = MatrixImpl<Int>(2, 3)
    try {
        nonSquareMatrix.transpose()
    } catch (e: IllegalStateException) {
        println("   Ошибка транспонирования неквадратной матрицы: ${e.message}")
    }

    // 6. Тестирование функций работы с матрицами
    println("\n6. МАТРИЧНЫЕ ОПЕРАЦИИ:")

    // Единичная матрица
    val identity = createIdentityMatrix(4)
    println("   Единичная матрица 4x4:")
    println("   $identity")

    // Последовательная матрица
    val seqMatrix = createSequenceMatrix(2, 3, 10)
    println("\n   Последовательная матрица 2x3 начиная с 10:")
    println("   $seqMatrix")

    // Умножение матриц
    val mA = MatrixImpl<Int>(2, 3)
    mA[0, 0] = 1; mA[0, 1] = 2; mA[0, 2] = 3
    mA[1, 0] = 4; mA[1, 1] = 5; mA[1, 2] = 6

    val mB = MatrixImpl<Int>(3, 2)
    mB[0, 0] = 7; mB[0, 1] = 8
    mB[1, 0] = 9; mB[1, 1] = 10
    mB[2, 0] = 11; mB[2, 1] = 12

    try {
        val multiplied = multiplyMatrices(mA, mB)
        println("\n   Умножение матриц:")
        println("   Матрица A (2x3):")
        println("   $mA")
        println("   Матрица B (3x2):")
        println("   $mB")
        println("   Результат A × B (2x2):")
        println("   $multiplied")
    } catch (e: Exception) {
        println("   Ошибка при умножении: ${e.message}")
    }

    // Сложение матриц
    val mC = MatrixImpl<Int>(2, 2)
    mC[0, 0] = 1; mC[0, 1] = 2
    mC[1, 0] = 3; mC[1, 1] = 4

    val mD = MatrixImpl<Int>(2, 2)
    mD[0, 0] = 5; mD[0, 1] = 6
    mD[1, 0] = 7; mD[1, 1] = 8

    try {
        val sum = addMatrices(mC, mD)
        println("\n   Сложение матриц:")
        println("   Матрица C:")
        println("   $mC")
        println("   Матрица D:")
        println("   $mD")
        println("   Результат C + D:")
        println("   $sum")
    } catch (e: Exception) {
        println("   Ошибка при сложении: ${e.message}")
    }

    // 7. Тестирование с различными типами данных
    println("\n7. МАТРИЦЫ РАЗЛИЧНЫХ ТИПОВ:")

    val doubleMatrix = MatrixImpl<Double>(2, 2, 0.0)
    doubleMatrix[0, 0] = 1.5
    doubleMatrix[0, 1] = 2.7
    doubleMatrix[1, 0] = 3.1
    doubleMatrix[1, 1] = 4.9

    println("   Матрица Double:")
    println("   $doubleMatrix")

    val charMatrix = MatrixImpl<Char>(3, 3, ' ')
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            charMatrix[i, j] = ('A' + i * 3 + j)
        }
    }

    println("\n   Матрица Char:")
    println("   $charMatrix")

    val booleanMatrix = MatrixImpl<Boolean>(2, 2, false)
    booleanMatrix[0, 0] = true
    booleanMatrix[1, 1] = true

    println("\n   Матрица Boolean:")
    println("   $booleanMatrix")

    val stringMatrix = MatrixImpl<String>(2, 2, "")
    stringMatrix[0, 0] = "Hello"
    stringMatrix[0, 1] = "World"
    stringMatrix[1, 0] = "Kotlin"
    stringMatrix[1, 1] = "Matrix"

    println("\n   Матрица String:")
    println("   $stringMatrix")

    println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")
}