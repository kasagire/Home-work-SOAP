import kotlin.math.abs
import kotlin.math.PI

/**
 * Простая (2 балла)
 *
 * Найти количество цифр в заданном числе n.
 * Например, число 1 содержит 1 цифру, 456 -- 3 цифры, 65536 -- 5 цифр.
 *
 * Использовать операции со строками в этой задаче запрещается.
 */
fun digitNumber(n: Int): Int {
    var count = 0
    var number = abs(n)

    if (number == 0) return 1

    while (number > 0) {
        count++
        number /= 10
    }

    return count
}

/**
 * Простая (2 балла)
 *
 * Найти число Фибоначчи из ряда 1, 1, 2, 3, 5, 8, 13, 21, ... с номером n.
 * Ряд Фибоначчи определён следующим образом: fib(1) = 1, fib(2) = 1, fib(n+2) = fib(n) + fib(n+1)
 */
fun fib(n: Int): Int {
    if (n <= 2) return 1

    var prev1 = 1
    var prev2 = 1
    var current = 0

    for (i in 3..n) {
        current = prev1 + prev2
        prev2 = prev1
        prev1 = current
    }

    return current
}

/**
 * Простая (2 балла)
 *
 * Для заданного числа n > 1 найти минимальный делитель, превышающий 1
 */
fun minDivisor(n: Int): Int {
    for (i in 2..n) {
        if (n % i == 0) {
            return i
        }
    }
    return n
}

/**
 * Простая (2 балла)
 *
 * Для заданного числа n > 1 найти максимальный делитель, меньший n
 */
fun maxDivisor(n: Int): Int {
    for (i in n-1 downTo 1) {
        if (n % i == 0) {
            return i
        }
    }
    return 1
}

/**
 * Простая (2 балла)
 *
 * Гипотеза Коллатца. Рекуррентная последовательность чисел задана следующим образом:
 *
 *   ЕСЛИ (X четное)
 *     Xслед = X /2
 *   ИНАЧЕ
 *     Xслед = 3 * X + 1
 *
 * например
 *   15 46 23 70 35 106 53 160 80 40 20 10 5 16 8 4 2 1 4 2 1 4 2 1 ...
 * Данная последовательность рано или поздно встречает X == 1.
 * Написать функцию, которая находит, сколько шагов требуется для
 * этого для какого-либо начального X > 0.
 */
fun collatzSteps(x: Int): Int {
    var steps = 0
    var current = x

    while (current != 1) {
        if (current % 2 == 0) {
            current /= 2
        } else {
            current = 3 * current + 1
        }
        steps++
    }

    return steps
}

/**
 * Средняя (3 балла)
 *
 * Для заданных чисел m и n найти наименьшее общее кратное, то есть,
 * минимальное число k, которое делится и на m и на n без остатка
 */
fun lcm(m: Int, n: Int): Int {
    // Используем формулу: LCM = |m * n| / GCD(m, n)
    return abs(m * n) / gcd(m, n)
}

/**
 * Вспомогательная функция для нахождения наибольшего общего делителя
 */
fun gcd(a: Int, b: Int): Int {
    var x = abs(a)
    var y = abs(b)

    while (y != 0) {
        val temp = y
        y = x % y
        x = temp
    }

    return x
}

/**
 * Средняя (3 балла)
 *
 * Определить, являются ли два заданных числа m и n взаимно простыми.
 * Взаимно простые числа не имеют общих делителей, кроме 1.
 * Например, 25 и 49 взаимно простые, а 6 и 8 -- нет.
 */
fun isCoPrime(m: Int, n: Int): Boolean {
    return gcd(m, n) == 1
}

/**
 * Средняя (3 балла)
 *
 * Поменять порядок цифр заданного числа n на обратный: 13478 -> 87431.
 *
 * Использовать операции со строками в этой задаче запрещается.
 */
fun revert(n: Int): Int {
    var reversed = 0
    var number = abs(n)

    while (number > 0) {
        val digit = number % 10
        reversed = reversed * 10 + digit
        number /= 10
    }

    return if (n < 0) -reversed else reversed
}

/**
 * Средняя (3 балла)
 *
 * Проверить, является ли заданное число n палиндромом:
 * первая цифра равна последней, вторая -- предпоследней и так далее.
 * 15751 -- палиндром, 3653 -- нет.
 *
 * Использовать операции со строками в этой задаче запрещается.
 */
fun isPalindrome(n: Int): Boolean {
    return n == revert(n)
}

/**
 * Средняя (3 балла)
 *
 * Для заданного числа n определить, содержит ли оно различающиеся цифры.
 * Например, 54 и 323 состоят из разных цифр, а 111 и 0 из одинаковых.
 *
 * Использовать операции со строками в этой задаче запрещается.
 */
fun hasDifferentDigits(n: Int): Boolean {
    if (n == 0) return false

    var number = abs(n)
    val firstDigit = number % 10

    while (number > 0) {
        if (number % 10 != firstDigit) {
            return true
        }
        number /= 10
    }

    return false
}

/**
 * Средняя (4 балла)
 *
 * Для заданного x рассчитать с заданной точностью eps
 * sin(x) = x - x^3 / 3! + x^5 / 5! - x^7 / 7! + ...
 * Нужную точность считать достигнутой, если очередной член ряда меньше eps по модулю.
 * Подумайте, как добиться более быстрой сходимости ряда при больших значениях x.
 * Использовать kotlin.math.sin и другие стандартные реализации функции синуса в этой задаче запрещается.
 */
fun sin(x: Double, eps: Double): Double {
    // Приводим x к диапазону [-π, π] для лучшей сходимости
    var angle = x % (2 * PI)
    if (angle > PI) angle -= 2 * PI
    if (angle < -PI) angle += 2 * PI

    var result = 0.0
    var term = angle
    var n = 1

    while (abs(term) >= eps) {
        result += term
        n += 2
        term = -term * angle * angle / ((n - 1) * n)
    }

    return result
}

/**
 * Средняя (4 балла)
 *
 * Для заданного x рассчитать с заданной точностью eps
 * cos(x) = 1 - x^2 / 2! + x^4 / 4! - x^6 / 6! + ...
 * Нужную точность считать достигнутой, если очередной член ряда меньше eps по модулю
 * Подумайте, как добиться более быстрой сходимости ряда при больших значениях x.
 * Использовать kotlin.math.cos и другие стандартные реализации функции косинуса в этой задаче запрещается.
 */
fun cos(x: Double, eps: Double): Double {
    // Приводим x к диапазону [-π, π] для лучшей сходимости
    var angle = x % (2 * PI)
    if (angle > PI) angle -= 2 * PI
    if (angle < -PI) angle += 2 * PI

    var result = 0.0
    var term = 1.0
    var n = 0

    while (abs(term) >= eps) {
        result += term
        n += 2
        term = -term * angle * angle / ((n - 1) * n)
    }

    return result
}

/**
 * Сложная (4 балла)
 *
 * Найти n-ю цифру последовательности из квадратов целых чисел:
 * 149162536496481100121144...
 * Например, 2-я цифра равна 4, 7-я 5, 12-я 6.
 *
 * Использовать операции со строками в этой задаче запрещается.
 */
fun squareSequenceDigit(n: Int): Int {
    var position = 0
    var number = 1

    while (true) {
        var square = number * number
        var temp = square
        var digitsCount = 0

        // Считаем количество цифр в квадрате
        if (temp == 0) {
            digitsCount = 1
        } else {
            while (temp > 0) {
                digitsCount++
                temp /= 10
            }
        }

        // Проверяем, не попала ли нужная позиция в этот квадрат
        if (position + digitsCount >= n) {
            // Нужная цифра в этом числе
            var targetPos = n - position - 1
            // Пропускаем лишние цифры справа
            for (i in 0 until digitsCount - targetPos - 1) {
                square /= 10
            }
            return square % 10
        }

        position += digitsCount
        number++
    }
}

/**
 * Сложная (5 баллов)
 *
 * Найти n-ю цифру последовательности из чисел Фибоначчи (см. функцию fib выше):
 * 1123581321345589144...
 * Например, 2-я цифра равна 1, 9-я 2, 14-я 5.
 *
 * Использовать операции со строками в этой задаче запрещается.
 */
fun fibSequenceDigit(n: Int): Int {
    var position = 0
    var fibIndex = 1

    while (true) {
        val fibNumber = fib(fibIndex)
        var temp = fibNumber
        var digitsCount = 0

        // Считаем количество цифр в числе Фибоначчи
        if (temp == 0) {
            digitsCount = 1
        } else {
            while (temp > 0) {
                digitsCount++
                temp /= 10
            }
        }

        // Проверяем, не попала ли нужная позиция в это число
        if (position + digitsCount >= n) {
            // Нужная цифра в этом числе
            var targetPos = n - position - 1
            // Пропускаем лишние цифры справа
            for (i in 0 until digitsCount - targetPos - 1) {
                temp = fibNumber
                repeat(i) { temp /= 10 }
            }
            temp = fibNumber
            for (i in 0 until targetPos) {
                temp /= 10
            }
            return temp % 10
        }

        position += digitsCount
        fibIndex++
    }
}

/**
 * Тестирующая функция для проверки всех решений
 */
fun main() {
    println("Тестирование всех функций:\n")

    // Тест 1: digitNumber
    println("1. digitNumber:")
    println("   digitNumber(0) = ${digitNumber(0)} ")
    println("   digitNumber(1) = ${digitNumber(1)} ")
    println("   digitNumber(456) = ${digitNumber(456)} )")
    println("   digitNumber(65536) = ${digitNumber(65536)} ")
    println("   digitNumber(-123) = ${digitNumber(-123)} ")

    // Тест 2: fib
    println("\n2. fib:")
    println("   fib(1) = ${fib(1)} ")
    println("   fib(2) = ${fib(2)} ")
    println("   fib(3) = ${fib(3)} ")
    println("   fib(6) = ${fib(6)} ")
    println("   fib(10) = ${fib(10)} ")

    // Тест 3: minDivisor
    println("\n3. minDivisor:")
    println("   minDivisor(2) = ${minDivisor(2)} ")
    println("   minDivisor(9) = ${minDivisor(9)} ")
    println("   minDivisor(49) = ${minDivisor(49)} ")
    println("   minDivisor(17) = ${minDivisor(17)} ")

    // Тест 4: maxDivisor
    println("\n4. maxDivisor:")
    println("   maxDivisor(2) = ${maxDivisor(2)} ")
    println("   maxDivisor(9) = ${maxDivisor(9)} ")
    println("   maxDivisor(49) = ${maxDivisor(49)} ")
    println("   maxDivisor(100) = ${maxDivisor(100)} ")

    // Тест 5: collatzSteps
    println("\n5. collatzSteps:")
    println("   collatzSteps(1) = ${collatzSteps(1)} ")
    println("   collatzSteps(2) = ${collatzSteps(2)} ")
    println("   collatzSteps(15) = ${collatzSteps(15)}")
    println("   collatzSteps(27) = ${collatzSteps(27)}")

    // Тест 6: lcm
    println("\n6. lcm:")
    println("   lcm(3, 5) = ${lcm(3, 5)} ")
    println("   lcm(12, 18) = ${lcm(12, 18)} ")
    println("   lcm(25, 49) = ${lcm(25, 49)} ")

    // Тест 7: isCoPrime
    println("\n7. isCoPrime:")
    println("   isCoPrime(25, 49) = ${isCoPrime(25, 49)} ")
    println("   isCoPrime(6, 8) = ${isCoPrime(6, 8)} ")
    println("   isCoPrime(1, 100) = ${isCoPrime(1, 100)} ")
    println("   isCoPrime(15, 25) = ${isCoPrime(15, 25)} ")

    // Тест 8: revert
    println("\n8. revert:")
    println("   revert(13478) = ${revert(13478)} ")
    println("   revert(12345) = ${revert(12345)} ")
    println("   revert(100) = ${revert(100)} ")
    println("   revert(-123) = ${revert(-123)} ")

    // Тест 9: isPalindrome
    println("\n9. isPalindrome:")
    println("   isPalindrome(15751) = ${isPalindrome(15751)} ")
    println("   isPalindrome(3653) = ${isPalindrome(3653)} ")
    println("   isPalindrome(12321) = ${isPalindrome(12321)} ")
    println("   isPalindrome(123321) = ${isPalindrome(123321)} ")

    // Тест 10: hasDifferentDigits
    println("\n10. hasDifferentDigits:")
    println("   hasDifferentDigits(54) = ${hasDifferentDigits(54)} ")
    println("   hasDifferentDigits(323) = ${hasDifferentDigits(323)}")
    println("   hasDifferentDigits(111) = ${hasDifferentDigits(111)} ")
    println("   hasDifferentDigits(0) = ${hasDifferentDigits(0)} ")
    println("   hasDifferentDigits(777) = ${hasDifferentDigits(777)} ")

    // Тест 11: sin
    println("\n11. sin (приближенные значения):")
    println("   sin(0.0, 0.0001) = ${String.format("%.6f", sin(0.0, 0.0001))} ")
    println("   sin(PI/6, 0.0001) = ${String.format("%.6f", sin(PI/6, 0.0001))} ")
    println("   sin(PI/2, 0.0001) = ${String.format("%.6f", sin(PI/2, 0.0001))} ")

    // Тест 12: cos
    println("\n12. cos (приближенные значения):")
    println("   cos(0.0, 0.0001) = ${String.format("%.6f", cos(0.0, 0.0001))} ")
    println("   cos(PI/3, 0.0001) = ${String.format("%.6f", cos(PI/3, 0.0001))} ")
    println("   cos(PI, 0.0001) = ${String.format("%.6f", cos(PI, 0.0001))} ")

    // Тест 13: squareSequenceDigit
    println("\n13. squareSequenceDigit:")
    println("   squareSequenceDigit(1) = ${squareSequenceDigit(1)} ")
    println("   squareSequenceDigit(2) = ${squareSequenceDigit(2)} ")
    println("   squareSequenceDigit(7) = ${squareSequenceDigit(7)} ")
    println("   squareSequenceDigit(12) = ${squareSequenceDigit(12)} ")

    // Тест 14: fibSequenceDigit
    println("\n14. fibSequenceDigit:")
    println("   fibSequenceDigit(1) = ${fibSequenceDigit(1)} ")
    println("   fibSequenceDigit(2) = ${fibSequenceDigit(2)} ")
    println("   fibSequenceDigit(9) = ${fibSequenceDigit(9)} ")
    println("   fibSequenceDigit(14) = ${fibSequenceDigit(14)} ")

    println("\n=== Все тесты завершены ===")
}