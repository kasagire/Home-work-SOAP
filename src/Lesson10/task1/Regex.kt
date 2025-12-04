import kotlin.math.pow

sealed class Expression {
    object Variable : Expression()
    data class Constant(val value: Int) : Expression()
    data class Binary(val left: Expression, val op: Operation, val right: Expression) : Expression()
    data class Negate(val expr: Expression) : Expression()
}

enum class Operation {
    PLUS, MINUS, TIMES, DIV, POW
}

val PLUS = Operation.PLUS
val MINUS = Operation.MINUS
val TIMES = Operation.TIMES
val DIV = Operation.DIV
val POW = Operation.POW

class Parser(private val groups: List<String>) {
    private var pos = 0

    fun parse(): Expression {
        val result = parseExpression()
        check(pos >= groups.size) { "Unexpected expression remainder: ${groups.subList(pos, groups.size)}" }
        return result
    }

    private fun parseExpression(): Expression {
        var left = parseItem()
        while (pos < groups.size) {
            when (val op = operationMap[groups[pos]]) {
                PLUS, MINUS -> {
                    pos++
                    val right = parseItem()
                    left = Expression.Binary(left, op, right)
                }
                else -> return left
            }
        }
        return left
    }

    private fun parseItem(): Expression {
        var left = parseFactor()
        while (pos < groups.size) {
            when (val op = operationMap[groups[pos]]) {
                TIMES, DIV -> {
                    pos++
                    val right = parseFactor()
                    left = Expression.Binary(left, op, right)
                }
                else -> return left
            }
        }
        return left
    }

    private fun parseFactor(): Expression {
        check(pos < groups.size) { "Unexpected expression end" }
        return when (val group = groups[pos++]) {
            "x" -> Expression.Variable
            "-" -> Expression.Negate(parseFactor())
            "(" -> {
                val arg = parseExpression()
                val next = groups[pos++]
                if (next == ")") arg
                else throw IllegalStateException(") expected instead of $next")
            }
            else -> {
                val number = group.toIntOrNull() ?: throw IllegalArgumentException("Expected number or variable, got: $group")

                // Проверяем, есть ли после числа операция возведения в степень
                if (pos < groups.size && groups[pos] == "^") {
                    pos++ // Пропускаем "^"
                    val exponent = parseFactor()
                    return Expression.Binary(Expression.Constant(number), POW, exponent)
                }

                Expression.Constant(number)
            }
        }
    }

    /**
     * Сложная (15 баллов)
     *
     * Поддержать операцию возведения в степень на базе того же парсера.
     * Операция обозначается символом ^, выполняется раньше, чем умножение и деление.
     * Кроме написания этой функции, вам придётся вызвать её в одной или двух
     * предыдущих функциях парсера, и поддержать операцию POW внутри функции calculate.
     */
    internal fun parseExponentiation(): Expression {
        var left = parsePrimary()
        while (pos < groups.size) {
            when (val op = operationMap[groups[pos]]) {
                POW -> {
                    pos++
                    val right = parsePrimary()
                    left = Expression.Binary(left, op, right)
                }
                else -> return left
            }
        }
        return left
    }

    /**
     * Парсит первичные выражения: числа, переменные, скобки
     */
    private fun parsePrimary(): Expression {
        check(pos < groups.size) { "Unexpected expression end" }
        return when (val group = groups[pos++]) {
            "x" -> Expression.Variable
            "-" -> Expression.Negate(parsePrimary())
            "(" -> {
                val arg = parseExpression()
                val next = groups[pos++]
                if (next == ")") arg
                else throw IllegalStateException(") expected instead of $next")
            }
            else -> Expression.Constant(group.toInt())
        }
    }

    private val operationMap = mapOf("+" to PLUS, "-" to MINUS, "*" to TIMES, "/" to DIV, "^" to POW)
}

/**
 * Функция для вычисления выражения
 */
fun calculate(expr: Expression, x: Int): Int {
    return when (expr) {
        is Expression.Variable -> x
        is Expression.Constant -> expr.value
        is Expression.Negate -> -calculate(expr.expr, x)
        is Expression.Binary -> when (expr.op) {
            PLUS -> calculate(expr.left, x) + calculate(expr.right, x)
            MINUS -> calculate(expr.left, x) - calculate(expr.right, x)
            TIMES -> calculate(expr.left, x) * calculate(expr.right, x)
            DIV -> {
                val denominator = calculate(expr.right, x)
                if (denominator == 0) throw ArithmeticException("Division by zero")
                calculate(expr.left, x) / denominator
            }
            POW -> {
                val base = calculate(expr.left, x)
                val exponent = calculate(expr.right, x)
                if (exponent < 0) throw ArithmeticException("Negative exponent not supported")
                base.toDouble().pow(exponent).toInt()
            }
        }
    }
}

/**
 * Функция для упрощенного парсинга строки
 */
fun parseExpression(expression: String): Expression {
    val tokens = tokenize(expression)
    val parser = Parser(tokens)
    return parser.parse()
}

/**
 * Токенизация строки выражения
 */
fun tokenize(expression: String): List<String> {
    val result = mutableListOf<String>()
    var i = 0

    while (i < expression.length) {
        when (val c = expression[i]) {
            ' ' -> {
                i++
                continue
            }
            in '0'..'9' -> {
                var number = ""
                while (i < expression.length && expression[i].isDigit()) {
                    number += expression[i]
                    i++
                }
                result.add(number)
                continue
            }
            'x', '+', '-', '*', '/', '^', '(', ')' -> {
                result.add(c.toString())
                i++
            }
            else -> throw IllegalArgumentException("Invalid character: $c")
        }
    }

    return result
}

/**
 * Вспомогательная функция для преобразования выражения в строку
 */
fun exprToString(expr: Expression): String {
    return when (expr) {
        is Expression.Variable -> "x"
        is Expression.Constant -> expr.value.toString()
        is Expression.Negate -> "-(${exprToString(expr.expr)})"
        is Expression.Binary -> {
            val opStr = when (expr.op) {
                PLUS -> "+"
                MINUS -> "-"
                TIMES -> "*"
                DIV -> "/"
                POW -> "^"
            }
            "(${exprToString(expr.left)} $opStr ${exprToString(expr.right)})"
        }
    }
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== ТЕСТИРОВАНИЕ ПАРСЕРА С ВОЗВЕДЕНИЕМ В СТЕПЕНЬ ===\n")

    // 1. Тестирование токенизации
    println("1. ТЕСТИРОВАНИЕ ТОКЕНИЗАЦИИ:")
    val testExpressions = listOf(
        "2 + 3",
        "x * 5",
        "2 ^ 3",
        "x ^ 2 + 3",
        "(x + 1) ^ 2",
        "2 * 3 ^ 2",
        "2 ^ 3 ^ 2",
        "x ^ 0"
    )

    for (expr in testExpressions) {
        val tokens = tokenize(expr)
        println("   '$expr' -> $tokens")
    }

    // 2. Тестирование парсинга и вычисления
    println("\n2. ТЕСТИРОВАНИЕ ПАРСИНГА И ВЫЧИСЛЕНИЯ:")

    val testCases = listOf(
        Triple("2 + 3", 0, 5),
        Triple("x * 5", 2, 10),
        Triple("2 ^ 3", 0, 8),
        Triple("x ^ 2 + 3", 4, 19),
        Triple("(x + 1) ^ 2", 3, 16),
        Triple("2 * 3 ^ 2", 0, 18),
        Triple("x ^ 0", 5, 1),
        Triple("2 ^ 3 ^ 2", 0, 512), // 2^(3^2) = 2^9 = 512
        Triple("x ^ 2 * x", 3, 27), // 3^2 * 3 = 9 * 3 = 27
        Triple("4 / 2 ^ 2", 0, 1) // 4 / (2^2) = 4 / 4 = 1
    )

    for ((exprStr, xValue, expected) in testCases) {
        try {
            val expr = parseExpression(exprStr)
            val result = calculate(expr, xValue)
            val isCorrect = result == expected
            println("   '$exprStr' при x=$xValue = $result (ожидалось: $expected) ${if (isCorrect) "✓" else "✗"}")

            if (!isCorrect) {
                println("     Выражение: ${exprToString(expr)}")
            }
        } catch (e: Exception) {
            println("   '$exprStr' при x=$xValue -> Ошибка: ${e.message}")
        }
    }

    // 3. Тестирование приоритета операций
    println("\n3. ТЕСТИРОВАНИЕ ПРИОРИТЕТА ОПЕРАЦИЙ:")
    val priorityTests = listOf(
        "2 + 3 * 4 ^ 2",      // 2 + (3 * (4^2)) = 2 + (3 * 16) = 2 + 48 = 50
        "2 ^ 3 * 4",          // (2^3) * 4 = 8 * 4 = 32
        "x ^ 2 + x * 2",      // при x=3: (3^2) + (3*2) = 9 + 6 = 15
        "(x + 2) ^ (x - 1)"   // при x=4: (4+2)^(4-1) = 6^3 = 216
    )

    for (exprStr in priorityTests) {
        try {
            val expr = parseExpression(exprStr)
            val xValue = 3
            val result = calculate(expr, xValue)
            println("   '$exprStr' при x=$xValue = $result")
        } catch (e: Exception) {
            println("   '$exprStr' -> Ошибка: ${e.message}")
        }
    }

    // 4. Тестирование ошибок
    println("\n4. ТЕСТИРОВАНИЕ ОШИБОК:")
    val errorTests = listOf(
        "x ^ -1",      // Отрицательная степень
        "5 / 0",       // Деление на ноль
        "2 ^",         // Неполное выражение
        "(2 + 3",      // Незакрытая скобка
        "abc",         // Неверный символ
        "2 ^ 1000"     // Очень большая степень
    )

    for (exprStr in errorTests) {
        try {
            val expr = parseExpression(exprStr)
            val result = calculate(expr, 0)
            println("   '$exprStr' = $result (неожиданный успех)")
        } catch (e: Exception) {
            println("   '$exprStr' -> Ошибка: ${e.message}")
        }
    }

    // 5. Тестирование цепочки возведений в степень
    println("\n5. ТЕСТИРОВАНИЕ ЦЕПОЧКИ ВОЗВЕДЕНИЙ В СТЕПЕНЬ:")
    val chainTests = listOf(
        "2 ^ 3 ^ 2",    // 2^(3^2) = 2^9 = 512
        "x ^ x ^ x",    // при x=2: 2^(2^2) = 2^4 = 16
        "3 ^ 2 ^ 1"     // 3^(2^1) = 3^2 = 9
    )

    for (exprStr in chainTests) {
        try {
            val expr = parseExpression(exprStr)
            val xValue = if (exprStr.contains("x")) 2 else 0
            val result = calculate(expr, xValue)
            println("   '$exprStr' при x=$xValue = $result")

            // Вывод дерева выражения для отладки
            println("     Дерево: ${exprToString(expr)}")
        } catch (e: Exception) {
            println("   '$exprStr' -> Ошибка: ${e.message}")
        }
    }

    // 6. Тестирование с отрицательными числами
    println("\n6. ТЕСТИРОВАНИЕ С ОТРИЦАТЕЛЬНЫМИ ЧИСЛАМИ:")
    val negativeTests = listOf(
        "-2 ^ 3",       // (-2)^3 = -8
        "(-2) ^ 3",     // (-2)^3 = -8
        "-x ^ 2",       // при x=3: -(3^2) = -9
        "(-x) ^ 2"      // при x=3: (-3)^2 = 9
    )

    for (exprStr in negativeTests) {
        try {
            val expr = parseExpression(exprStr)
            val xValue = 3
            val result = calculate(expr, xValue)
            println("   '$exprStr' при x=$xValue = $result")
        } catch (e: Exception) {
            println("   '$exprStr' -> Ошибка: ${e.message}")
        }
    }

    // 7. Тестирование метода parseExponentiation
    println("\n7. ТЕСТИРОВАНИЕ МЕТОДА parseExponentiation:")
    val exponentiationTests = listOf("2 ^ 3", "x ^ 2", "2 ^ 3 ^ 2")

    for (exprStr in exponentiationTests) {
        try {
            val tokens = tokenize(exprStr)
            val parser = Parser(tokens)
            val expr = parser.parseExponentiation()
            println("   '$exprStr' -> ${exprToString(expr)}")
        } catch (e: Exception) {
            println("   '$exprStr' -> Ошибка: ${e.message}")
        }
    }

    println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")
}
