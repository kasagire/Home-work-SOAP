import kotlin.math.sqrt

/**
 * Простая (2 балла)
 *
 * Найти модуль заданного вектора, представленного в виде списка v,
 * по формуле abs = sqrt(a1^2 + a2^2 + ... + aN^2).
 * Модуль пустого вектора считать равным 0.0.
 */
fun abs(v: List<Double>): Double {
    var sum = 0.0
    for (element in v) {
        sum += element * element
    }
    return sqrt(sum)
}

/**
 * Простая (2 балла)
 *
 * Рассчитать среднее арифметическое элементов списка list. Вернуть 0.0, если список пуст
 */
fun mean(list: List<Double>): Double {
    if (list.isEmpty()) return 0.0
    return list.sum() / list.size
}

/**
 * Средняя (3 балла)
 *
 * Центрировать заданный список list, уменьшив каждый элемент на среднее арифметическое всех элементов.
 * Если список пуст, не делать ничего. Вернуть изменённый список.
 *
 * Обратите внимание, что данная функция должна изменять содержание списка list, а не его копии.
 */
fun center(list: MutableList<Double>): MutableList<Double> {
    if (list.isNotEmpty()) {
        val average = mean(list)
        for (i in list.indices) {
            list[i] -= average
        }
    }
    return list
}

/**
 * Средняя (3 балла)
 *
 * Найти скалярное произведение двух векторов равной размерности,
 * представленные в виде списков a и b. Скалярное произведение считать по формуле:
 * C = a1b1 + a2b2 + ... + aNbN. Произведение пустых векторов считать равным 0.
 */
fun times(a: List<Int>, b: List<Int>): Int {
    var result = 0
    for (i in a.indices) {
        result += a[i] * b[i]
    }
    return result
}

/**
 * Средняя (3 балла)
 *
 * Рассчитать значение многочлена при заданном x:
 * p(x) = p0 + p1*x + p2*x^2 + p3*x^3 + ... + pN*x^N.
 * Коэффициенты многочлена заданы списком p: (p0, p1, p2, p3, ..., pN).
 * Значение пустого многочлена равно 0 при любом x.
 */
fun polynom(p: List<Int>, x: Int): Int {
    var result = 0
    var power = 1
    for (coefficient in p) {
        result += coefficient * power
        power *= x
    }
    return result
}

/**
 * Средняя (3 балла)
 *
 * В заданном списке list каждый элемент, кроме первого, заменить
 * суммой данного элемента и всех предыдущих.
 * Например: 1, 2, 3, 4 -> 1, 3, 6, 10.
 * Пустой список не следует изменять. Вернуть изменённый список.
 *
 * Обратите внимание, что данная функция должна изменять содержание списка list, а не его копии.
 */
fun accumulate(list: MutableList<Int>): MutableList<Int> {
    if (list.isNotEmpty()) {
        var sum = list[0]
        for (i in 1 until list.size) {
            sum += list[i]
            list[i] = sum
        }
    }
    return list
}

/**
 * Средняя (3 балла)
 *
 * Разложить заданное натуральное число n > 1 на простые множители.
 * Результат разложения вернуть в виде списка множителей, например 75 -> (3, 5, 5).
 * Множители в списке должны располагаться по возрастанию.
 */
fun factorize(n: Int): List<Int> {
    val result = mutableListOf<Int>()
    var number = n
    var divisor = 2

    while (number > 1) {
        while (number % divisor == 0) {
            result.add(divisor)
            number /= divisor
        }
        divisor++
    }

    return result
}

/**
 * Сложная (4 балла)
 *
 * Разложить заданное натуральное число n > 1 на простые множители.
 * Результат разложения вернуть в виде строки, например 75 -> 3*5*5
 * Множители в результирующей строке должны располагаться по возрастанию.
 */
fun factorizeToString(n: Int): String {
    return factorize(n).joinToString("*")
}

/**
 * Средняя (3 балла)
 *
 * Перевести заданное целое число n >= 0 в систему счисления с основанием base > 1.
 * Результат перевода вернуть в виде списка цифр в base-ичной системе от старшей к младшей,
 * например: n = 100, base = 4 -> (1, 2, 1, 0) или n = 250, base = 14 -> (1, 3, 12)
 */
fun convert(n: Int, base: Int): List<Int> {
    if (n == 0) return listOf(0)

    val result = mutableListOf<Int>()
    var number = n

    while (number > 0) {
        result.add(number % base)
        number /= base
    }

    return result.reversed()
}

/**
 * Сложная (4 балла)
 *
 * Перевести заданное целое число n >= 0 в систему счисления с основанием 1 < base < 37.
 * Результат перевода вернуть в виде строки, цифры более 9 представлять латинскими
 * строчными буквами: 10 -> a, 11 -> b, 12 -> c и так далее.
 * Например: n = 100, base = 4 -> 1210, n = 250, base = 14 -> 13c
 *
 * Использовать функции стандартной библиотеки, напрямую и полностью решающие данную задачу
 * (например, n.toString(base) и подобные), запрещается.
 */
fun convertToString(n: Int, base: Int): String {
    if (n == 0) return "0"

    val digits = "0123456789abcdefghijklmnopqrstuvwxyz"
    val result = StringBuilder()
    var number = n

    while (number > 0) {
        result.append(digits[number % base])
        number /= base
    }

    return result.reverse().toString()
}

/**
 * Средняя (3 балла)
 *
 * Перевести число, представленное списком цифр digits от старшей к младшей,
 * из системы счисления с основанием base в десятичную.
 * Например: digits = (1, 3, 12), base = 14 -> 250
 */
fun decimal(digits: List<Int>, base: Int): Int {
    var result = 0
    for (digit in digits) {
        result = result * base + digit
    }
    return result
}

/**
 * Сложная (4 балла)
 *
 * Перевести число, представленное цифровой строкой str,
 * из системы счисления с основанием base в десятичную.
 * Цифры более 9 представляются латинскими строчными буквами:
 * 10 -> a, 11 -> b, 12 -> c и так далее.
 * Например: str = "13c", base = 14 -> 250
 *
 * Использовать функции стандартной библиотеки, напрямую и полностью решающие данную задачу
 * (например, str.toInt(base)), запрещается.
 */
fun decimalFromString(str: String, base: Int): Int {
    var result = 0
    val digits = "0123456789abcdefghijklmnopqrstuvwxyz"

    for (char in str) {
        val digit = digits.indexOf(char)
        result = result * base + digit
    }

    return result
}

/**
 * Сложная (5 баллов)
 *
 * Перевести натуральное число n > 0 в римскую систему.
 * Римские цифры: 1 = I, 4 = IV, 5 = V, 9 = IX, 10 = X, 40 = XL, 50 = L,
 * 90 = XC, 100 = C, 400 = CD, 500 = D, 900 = CM, 1000 = M.
 * Например: 23 = XXIII, 44 = XLIV, 100 = C
 */
fun roman(n: Int): String {
    val values = listOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
    val symbols = listOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")

    var number = n
    val result = StringBuilder()

    for (i in values.indices) {
        while (number >= values[i]) {
            result.append(symbols[i])
            number -= values[i]
        }
    }

    return result.toString()
}

/**
 * Очень сложная (7 баллов)
 *
 * Записать заданное натуральное число 1..999999 прописью по-русски.
 * Например, 375 = "триста семьдесят пять",
 * 23964 = "двадцать три тысячи девятьсот шестьдесят четыре"
 */
fun russian(n: Int): String {
    val units = arrayOf("", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
    val teens = arrayOf("десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать",
        "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать")
    val tens = arrayOf("", "", "двадцать", "тридцать", "сорок", "пятьдесят",
        "шестьдесят", "семьдесят", "восемьдесят", "девяносто")
    val hundreds = arrayOf("", "сто", "двести", "триста", "четыреста", "пятьсот",
        "шестьсот", "семьсот", "восемьсот", "девятьсот")

    val thousandsUnits = arrayOf("", "одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")

    fun convertThreeDigits(number: Int, isThousands: Boolean = false): String {
        var result = mutableListOf<String>()
        val num = number

        // Сотни
        if (num / 100 > 0) {
            result.add(hundreds[num / 100])
        }

        // Десятки и единицы
        val lastTwo = num % 100
        when {
            lastTwo == 0 -> {}
            lastTwo < 10 -> result.add(if (isThousands) thousandsUnits[lastTwo] else units[lastTwo])
            lastTwo < 20 -> result.add(teens[lastTwo - 10])
            else -> {
                result.add(tens[lastTwo / 10])
                if (lastTwo % 10 > 0) {
                    result.add(if (isThousands) thousandsUnits[lastTwo % 10] else units[lastTwo % 10])
                }
            }
        }

        return result.filter { it.isNotEmpty() }.joinToString(" ")
    }

    val result = mutableListOf<String>()

    // Тысячи
    val thousands = n / 1000
    if (thousands > 0) {
        val thousandsText = convertThreeDigits(thousands, true)
        if (thousandsText.isNotEmpty()) {
            result.add(thousandsText)

            // Определяем правильное окончание для тысяч
            val lastDigit = thousands % 10
            val lastTwoDigits = thousands % 100

            val thousandWord = when {
                lastTwoDigits in 11..14 -> "тысяч"
                lastDigit == 1 -> "тысяча"
                lastDigit in 2..4 -> "тысячи"
                else -> "тысяч"
            }

            result.add(thousandWord)
        }
    }

    // Единицы
    val unitsPart = n % 1000
    if (unitsPart > 0 || n == 0) {
        result.add(convertThreeDigits(unitsPart))
    }

    return result.filter { it.isNotEmpty() }.joinToString(" ").trim()
}

/**
 * Тестирующая функция для проверки всех решений
 */
fun main() {
    println("Тестирование всех функций:\n")

    // Тест 1: abs
    println("1. abs:")
    println("   abs(listOf(3.0, 4.0)) = ${abs(listOf(3.0, 4.0))} ")
    println("   abs(listOf(1.0, 1.0, 1.0)) = ${String.format("%.2f", abs(listOf(1.0, 1.0, 1.0)))} ")
    println("   abs(emptyList()) = ${abs(emptyList())} ")

    // Тест 2: mean
    println("\n2. mean:")
    println("   mean(listOf(1.0, 2.0, 3.0, 4.0)) = ${mean(listOf(1.0, 2.0, 3.0, 4.0))} ")
    println("   mean(listOf(5.0, 5.0, 5.0)) = ${mean(listOf(5.0, 5.0, 5.0))} ")
    println("   mean(emptyList()) = ${mean(emptyList())} ")

    // Тест 3: center
    println("\n3. center:")
    val list1 = mutableListOf(1.0, 2.0, 3.0, 4.0)
    println("   center($list1) = ${center(list1)} ")
    val list2 = mutableListOf<Double>()
    println("   center(emptyList) = ${center(list2)}")

    // Тест 4: times
    println("\n4. times:")
    println("   times(listOf(1, 2, 3), listOf(4, 5, 6)) = ${times(listOf(1, 2, 3), listOf(4, 5, 6))} )")
    println("   times(emptyList(), emptyList()) = ${times(emptyList(), emptyList())} ")

    // Тест 5: polynom
    println("\n5. polynom:")
    println("   polynom(listOf(1, 2, 3), 2) = ${polynom(listOf(1, 2, 3), 2)} ")
    println("   polynom(listOf(5, 0, -1), 3) = ${polynom(listOf(5, 0, -1), 3)} ")

    // Тест 6: accumulate
    println("\n6. accumulate:")
    val list6 = mutableListOf(1, 2, 3, 4)
    println("   accumulate($list6) = ${accumulate(list6)} ")
    val emptyList6 = mutableListOf<Int>()
    println("   accumulate(emptyList) = ${accumulate(emptyList6)}")

    // Тест 7: factorize
    println("\n7. factorize:")
    println("   factorize(75) = ${factorize(75)} ")
    println("   factorize(17) = ${factorize(17)} ")
    println("   factorize(100) = ${factorize(100)} ")

    // Тест 8: factorizeToString
    println("\n8. factorizeToString:")
    println("   factorizeToString(75) = ${factorizeToString(75)} ")
    println("   factorizeToString(100) = ${factorizeToString(100)} ")

    // Тест 9: convert
    println("\n9. convert:")
    println("   convert(100, 4) = ${convert(100, 4)} ")
    println("   convert(250, 14) = ${convert(250, 14)} ")
    println("   convert(0, 2) = ${convert(0, 2)} ")

    // Тест 10: convertToString
    println("\n10. convertToString:")
    println("   convertToString(100, 4) = ${convertToString(100, 4)} ")
    println("   convertToString(250, 14) = ${convertToString(250, 14)} ")
    println("   convertToString(255, 16) = ${convertToString(255, 16)} ")

    // Тест 11: decimal
    println("\n11. decimal:")
    println("   decimal(listOf(1, 2, 1, 0), 4) = ${decimal(listOf(1, 2, 1, 0), 4)} ")
    println("   decimal(listOf(1, 3, 12), 14) = ${decimal(listOf(1, 3, 12), 14)} ")

    // Тест 12: decimalFromString
    println("\n12. decimalFromString:")
    println("   decimalFromString(\"1210\", 4) = ${decimalFromString("1210", 4)} ")
    println("   decimalFromString(\"13c\", 14) = ${decimalFromString("13c", 14)} ")
    println("   decimalFromString(\"ff\", 16) = ${decimalFromString("ff", 16)} ")

    // Тест 13: roman
    println("\n13. roman:")
    println("   roman(23) = ${roman(23)} ")
    println("   roman(44) = ${roman(44)} ")
    println("   roman(100) = ${roman(100)} ")
    println("   roman(1994) = ${roman(1994)} ")

    // Тест 14: russian
    println("\n14. russian:")
    println("   russian(375) = \"${russian(375)}\" ")
    println("   russian(23964) = \"${russian(23964)}\"")
    println("   russian(1) = \"${russian(1)}\" ")
    println("   russian(21) = \"${russian(21)}\" ")
    println("   russian(1000) = \"${russian(1000)}\" ")
    println("   russian(2000) = \"${russian(2000)}\" ")
    println("   russian(5000) = \"${russian(5000)}\" ")

}