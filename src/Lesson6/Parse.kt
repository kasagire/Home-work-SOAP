/**
 * Средняя (4 балла)
 *
 * Дата представлена строкой вида "15 июля 2016".
 * Перевести её в цифровой формат "15.07.2016".
 * День и месяц всегда представлять двумя цифрами, например: 03.04.2011.
 * При неверном формате входной строки вернуть пустую строку.
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30.02.2009) считается неверными
 * входными данными.
 */
fun dateStrToDigit(str: String): String {
    val months = mapOf(
        "января" to 1, "февраля" to 2, "марта" to 3,
        "апреля" to 4, "мая" to 5, "июня" to 6,
        "июля" to 7, "августа" to 8, "сентября" to 9,
        "октября" to 10, "ноября" to 11, "декабря" to 12
    )

    try {
        val parts = str.split(" ")
        if (parts.size != 3) return ""

        val day = parts[0].toInt()
        val monthName = parts[1]
        val year = parts[2].toInt()

        val month = months[monthName] ?: return ""

        // Проверка корректности даты
        if (!isValidDate(day, month, year)) return ""

        return String.format("%02d.%02d.%d", day, month, year)
    } catch (e: Exception) {
        return ""
    }
}

/**
 * Средняя (4 балла)
 *
 * Дата представлена строкой вида "15.07.2016".
 * Перевести её в строковый формат вида "15 июля 2016".
 * При неверном формате входной строки вернуть пустую строку
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30 февраля 2009) считается неверными
 * входными данными.
 */
fun dateDigitToStr(digital: String): String {
    val months = listOf(
        "января", "февраля", "марта", "апреля", "мая", "июня",
        "июля", "августа", "сентября", "октября", "ноября", "декабря"
    )

    try {
        val parts = digital.split(".")
        if (parts.size != 3) return ""

        // Проверяем, что все части имеют корректную длину
        if (parts[0].length !in 1..2 || parts[1].length !in 1..2 || parts[2].length != 4) return ""

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        // Проверка корректности даты
        if (!isValidDate(day, month, year)) return ""

        return "$day ${months[month - 1]} $year"
    } catch (e: Exception) {
        return ""
    }
}

/**
 * Вспомогательная функция для проверки корректности даты
 */
fun isValidDate(day: Int, month: Int, year: Int): Boolean {
    if (year < 1) return false
    if (month !in 1..12) return false
    if (day < 1) return false

    val daysInMonth = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYearCheck(year)) 29 else 28 // Используем переименованную функцию
        else -> return false
    }

    return day <= daysInMonth
}

/**
 * Вспомогательная функция для проверки високосного года
 * Переименована, чтобы избежать конфликта с другой функцией с тем же именем
 */
fun isLeapYearCheck(year: Int): Boolean {
    return (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)
}

/**
 * Средняя (4 балла)
 *
 * Номер телефона задан строкой вида "+7 (921) 123-45-67".
 * Префикс (+7) может отсутствовать, код города (в скобках) также может отсутствовать.
 * Может присутствовать неограниченное количество пробелов и чёрточек,
 * например, номер 12 --  34- 5 -- 67 -89 тоже следует считать легальным.
 * Перевести номер в формат без скобок, пробелов и чёрточек (но с +), например,
 * "+79211234567" или "123456789" для приведённых примеров.
 * Все символы в номере, кроме цифр, пробелов и +-(), считать недопустимыми.
 * При неверном формате вернуть пустую строку.
 *
 * PS: Дополнительные примеры работы функции можно посмотреть в соответствующих тестах.
 */
fun flattenPhoneNumber(phone: String): String {
    var result = StringBuilder()
    var hasPlusAtStart = false

    for (char in phone) {
        when {
            char == '+' -> {
                if (result.isEmpty()) {
                    result.append('+')
                    hasPlusAtStart = true
                } else {
                    return ""
                }
            }
            char.isDigit() -> result.append(char)
            char in " -()" -> continue // разрешенные символы
            else -> return "" // недопустимый символ
        }
    }

    // Не должно быть пустых скобок
    val temp = phone.replace(Regex("\\s+"), "")
    if ("()" in temp || "(+" in temp || "+)" in temp) {
        return ""
    }

    // Проверка парности скобок
    var bracketCount = 0
    for (char in phone) {
        when (char) {
            '(' -> bracketCount++
            ')' -> {
                bracketCount--
                if (bracketCount < 0) return ""
            }
        }
    }
    if (bracketCount != 0) return ""

    return result.toString()
}

/**
 * Средняя (5 баллов)
 *
 * Результаты спортсмена на соревнованиях в прыжках в длину представлены строкой вида
 * "706 - % 717 % 703".
 * В строке могут присутствовать числа, черточки - и знаки процента %, разделённые пробелами;
 * число соответствует удачному прыжку, - пропущенной попытке, % заступу.
 * Прочитать строку и вернуть максимальное присутствующее в ней число (717 в примере).
 * При нарушении формата входной строки или при отсутствии в ней чисел, вернуть -1.
 */
fun bestLongJump(jumps: String): Int {
    val parts = jumps.split(" ")
    var maxJump = -1

    for (part in parts) {
        when (part) {
            "-", "%" -> continue
            else -> {
                try {
                    val jump = part.toInt()
                    if (jump > maxJump) {
                        maxJump = jump
                    }
                } catch (e: NumberFormatException) {
                    return -1
                }
            }
        }
    }

    return maxJump
}

/**
 * Сложная (6 баллов)
 *
 * Результаты спортсмена на соревнованиях в прыжках в высоту представлены строкой вида
 * "220 + 224 %+ 228 %- 230 + 232 %%- 234 %".
 * Здесь + соответствует удачной попытке, % неудачной, - пропущенной.
 * Высота и соответствующие ей попытки разделяются пробелом.
 * Прочитать строку и вернуть максимальную взятую высоту (230 в примере).
 * При нарушении формата входной строки, а также в случае отсутствия удачных попыток,
 * вернуть -1.
 */
fun bestHighJump(jumps: String): Int {
    val parts = jumps.split(" ")
    if (parts.size % 2 != 0) return -1

    var maxHeight = -1

    for (i in parts.indices step 2) {
        try {
            val height = parts[i].toInt()
            val attempts = parts[i + 1]

            // Проверка формата попыток
            for (char in attempts) {
                if (char !in "+%-") return -1
            }

            // Если есть хотя бы один "+", высота взята
            if ('+' in attempts) {
                if (height > maxHeight) {
                    maxHeight = height
                }
            }
        } catch (e: NumberFormatException) {
            return -1
        }
    }

    return maxHeight
}

/**
 * Сложная (6 баллов)
 *
 * В строке представлено выражение вида "2 + 31 - 40 + 13",
 * использующее целые положительные числа, плюсы и минусы, разделённые пробелами.
 * Наличие двух знаков подряд "13 + + 10" или двух чисел подряд "1 2" не допускается.
 * Вернуть значение выражения (6 для примера).
 * Про нарушении формата входной строки бросить исключение IllegalArgumentException
 */
fun plusMinus(expression: String): Int {
    if (expression.isEmpty()) throw IllegalArgumentException()

    val parts = expression.split(" ")
    if (parts.isEmpty()) throw IllegalArgumentException()

    var result: Int
    try {
        result = parts[0].toInt()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException()
    }

    for (i in 1 until parts.size step 2) {
        if (i + 1 >= parts.size) throw IllegalArgumentException()

        val operator = parts[i]
        val numberStr = parts[i + 1]

        if (operator != "+" && operator != "-") throw IllegalArgumentException()

        try {
            val number = numberStr.toInt()
            when (operator) {
                "+" -> result += number
                "-" -> result -= number
            }
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException()
        }
    }

    return result
}

/**
 * Сложная (6 баллов)
 *
 * Строка состоит из набора слов, отделённых друг от друга одним пробелом.
 * Определить, имеются ли в строке повторяющиеся слова, идущие друг за другом.
 * Слова, отличающиеся только регистром, считать совпадающими.
 * Вернуть индекс начала первого повторяющегося слова, или -1, если повторов нет.
 * Пример: "Он пошёл в в школу" => результат 9 (индекс первого 'в')
 */
fun firstDuplicateIndex(str: String): Int {
    if (str.isEmpty()) return -1

    val words = str.split(" ")
    var currentIndex = 0

    for (i in 0 until words.size - 1) {
        if (words[i].equals(words[i + 1], ignoreCase = true)) {
            return currentIndex
        }
        currentIndex += words[i].length + 1 // +1 для пробела
    }

    return -1
}

/**
 * Сложная (6 баллов)
 *
 * Строка содержит названия товаров и цены на них в формате вида
 * "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9".
 * То есть, название товара отделено от цены пробелом,
 * а цена отделена от названия следующего товара точкой с запятой и пробелом.
 * Вернуть название самого дорогого товара в списке (в примере это Курица),
 * или пустую строку при нарушении формата строки.
 * Все цены должны быть больше нуля либо равны нулю.
 */
fun mostExpensive(description: String): String {
    if (description.isEmpty()) return ""

    val items = description.split("; ")
    var maxPrice = -1.0
    var mostExpensiveItem = ""

    for (item in items) {
        val parts = item.split(" ")
        if (parts.size != 2) return ""

        val name = parts[0]
        val priceStr = parts[1]

        try {
            val price = priceStr.toDouble()
            if (price < 0) return ""

            if (price > maxPrice) {
                maxPrice = price
                mostExpensiveItem = name
            }
        } catch (e: NumberFormatException) {
            return ""
        }
    }

    return mostExpensiveItem
}

/**
 * Сложная (6 баллов)
 *
 * Перевести число roman, заданное в римской системе счисления,
 * в десятичную систему и вернуть как результат.
 * Римские цифры: 1 = I, 4 = IV, 5 = V, 9 = IX, 10 = X, 40 = XL, 50 = L,
 * 90 = XC, 100 = C, 400 = CD, 500 = D, 900 = CM, 1000 = M.
 * Например: XXIII = 23, XLIV = 44, C = 100
 *
 * Вернуть -1, если roman не является корректным римским числом
 */
fun fromRoman(roman: String): Int {
    if (roman.isEmpty()) return -1

    val values = mapOf(
        'I' to 1, 'V' to 5, 'X' to 10,
        'L' to 50, 'C' to 100, 'D' to 500,
        'M' to 1000
    )

    // Проверка допустимых символов
    for (char in roman) {
        if (char !in values) return -1
    }

    var result = 0
    var i = 0

    while (i < roman.length) {
        val currentValue = values[roman[i]] ?: return -1

        if (i + 1 < roman.length) {
            val nextValue = values[roman[i + 1]] ?: return -1

            if (currentValue < nextValue) {
                // Проверка допустимых вычитаний
                val validSubtractions = mapOf(
                    'I' to setOf('V', 'X'),
                    'X' to setOf('L', 'C'),
                    'C' to setOf('D', 'M')
                )

                if (validSubtractions[roman[i]]?.contains(roman[i + 1]) != true) {
                    return -1
                }

                result += (nextValue - currentValue)
                i += 2
            } else {
                result += currentValue
                i += 1
            }
        } else {
            result += currentValue
            i += 1
        }
    }

    // Проверка на повторение символов (не более 3 подряд)
    for (char in roman) {
        if (roman.contains("$char$char$char$char")) {
            return -1
        }
    }

    // Проверка порядка (V, L, D не могут повторяться)
    for (char in "VLD") {
        if (roman.count { it == char } > 1) {
            return -1
        }
    }

    return result
}

/**
 * Очень сложная (7 баллов)
 *
 * Имеется специальное устройство, представляющее собой
 * конвейер из cells ячеек (нумеруются от 0 до cells - 1 слева направо) и датчик, двигающийся над этим конвейером.
 * Строка commands содержит последовательность команд, выполняемых данным устройством, например +>+>+>+>+
 * Каждая команда кодируется одним специальным символом:
 *	> - сдвиг датчика вправо на 1 ячейку;
 *  < - сдвиг датчика влево на 1 ячейку;
 *	+ - увеличение значения в ячейке под датчиком на 1 ед.;
 *	- - уменьшение значения в ячейке под датчиком на 1 ед.;
 *	[ - если значение под датчиком равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей следующей командой ']' (с учётом вложенности);
 *	] - если значение под датчиком не равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей предыдущей командой '[' (с учётом вложенности);
 *      (комбинация [] имитирует цикл)
 *  пробел - пустая команда
 *
 * Изначально все ячейки заполнены значением 0 и датчик стоит на ячейке с номером N/2 (округлять вниз)
 *
 * После выполнения limit команд или всех команд из commands следует прекратить выполнение последовательности команд.
 * Учитываются все команды, в том числе несостоявшиеся переходы ("[" при значении под датчиком не равном 0 и "]" при
 * значении под датчиком равном 0) и пробелы.
 *
 * Вернуть список размера cells, содержащий элементы ячеек устройства после завершения выполнения последовательности.
 * Например, для 10 ячеек и командной строки +>+>+>+>+ результат должен быть 0,0,0,0,0,1,1,1,1,1
 *
 * Все прочие символы следует считать ошибочными и формировать исключение IllegalArgumentException.
 * То же исключение формируется, если у символов [ ] не оказывается пары.
 * Выход за границу конвейера также следует считать ошибкой и формировать исключение IllegalStateException.
 * Считать, что ошибочные символы и непарные скобки являются более приоритетной ошибкой чем выход за границу ленты,
 * то есть если в программе присутствует некорректный символ или непарная скобка, то должно быть выброшено
 * IllegalArgumentException.
 * IllegalArgumentException должен бросаться даже если ошибочная команда не была достигнута в ходе выполнения.
 *
 */
fun computeDeviceCells(cells: Int, commands: String, limit: Int): List<Int> {
    // Проверка допустимых символов
    val allowedChars = setOf('>', '<', '+', '-', '[', ']', ' ')
    for (char in commands) {
        if (char !in allowedChars) {
            throw IllegalArgumentException("Недопустимый символ: $char")
        }
    }

    // Проверка парности скобок
    var bracketCount = 0
    for (char in commands) {
        when (char) {
            '[' -> bracketCount++
            ']' -> {
                bracketCount--
                if (bracketCount < 0) throw IllegalArgumentException("Непарная скобка ']'")
            }
        }
    }
    if (bracketCount != 0) throw IllegalArgumentException("Непарные скобки '['")

    // Инициализация
    val memory = MutableList(cells) { 0 }
    var pointer = cells / 2
    var commandIndex = 0
    var commandsExecuted = 0

    // Создаем map соответствия скобок
    val bracketPairs = mutableMapOf<Int, Int>()
    val stack = mutableListOf<Int>()

    for (i in commands.indices) {
        when (commands[i]) {
            '[' -> stack.add(i)
            ']' -> {
                if (stack.isEmpty()) throw IllegalArgumentException("Непарная скобка ']'")
                val openIndex = stack.removeAt(stack.size - 1)
                bracketPairs[openIndex] = i
                bracketPairs[i] = openIndex
            }
        }
    }

    // Выполнение команд
    while (commandIndex < commands.length && commandsExecuted < limit) {
        when (val command = commands[commandIndex]) {
            '>' -> {
                pointer++
                if (pointer >= cells || pointer < 0) {
                    throw IllegalStateException("Выход за границу конвейера")
                }
                commandIndex++
            }
            '<' -> {
                pointer--
                if (pointer >= cells || pointer < 0) {
                    throw IllegalStateException("Выход за границу конвейера")
                }
                commandIndex++
            }
            '+' -> {
                memory[pointer]++
                commandIndex++
            }
            '-' -> {
                memory[pointer]--
                commandIndex++
            }
            '[' -> {
                if (memory[pointer] == 0) {
                    commandIndex = bracketPairs[commandIndex]!! + 1
                } else {
                    commandIndex++
                }
            }
            ']' -> {
                if (memory[pointer] != 0) {
                    commandIndex = bracketPairs[commandIndex]!! + 1
                } else {
                    commandIndex++
                }
            }
            ' ' -> commandIndex++
            else -> throw IllegalArgumentException("Недопустимая команда: $command")
        }
        commandsExecuted++
    }

    return memory
}

/**
 * Тестирующая функция
 */
fun main() {
    println("Тестирование функций работы с датами:")
    println("dateStrToDigit(\"15 июля 2016\") = ${dateStrToDigit("15 июля 2016")}")
    println("dateDigitToStr(\"15.07.2016\") = ${dateDigitToStr("15.07.2016")}")
    println("dateStrToDigit(\"30 февраля 2009\") = ${dateStrToDigit("30 февраля 2009")}")

    println("\nТестирование функции flattenPhoneNumber:")
    println("flattenPhoneNumber(\"+7 (921) 123-45-67\") = ${flattenPhoneNumber("+7 (921) 123-45-67")}")
    println("flattenPhoneNumber(\"12 --  34- 5 -- 67 -89\") = ${flattenPhoneNumber("12 --  34- 5 -- 67 -89")}")

    println("\nТестирование функций для спортивных результатов:")
    println("bestLongJump(\"706 - % 717 % 703\") = ${bestLongJump("706 - % 717 % 703")}")
    println("bestHighJump(\"220 + 224 %+ 228 %- 230 + 232 %%- 234 %\") = ${bestHighJump("220 + 224 %+ 228 %- 230 + 232 %%- 234 %")}")

    println("\nТестирование функции plusMinus:")
    println("plusMinus(\"2 + 31 - 40 + 13\") = ${plusMinus("2 + 31 - 40 + 13")}")

    println("\nТестирование функции firstDuplicateIndex:")
    println("firstDuplicateIndex(\"Он пошёл в в школу\") = ${firstDuplicateIndex("Он пошёл в в школу")}")

    println("\nТестирование функции mostExpensive:")
    val desc = "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9"
    println("mostExpensive(\"$desc\") = ${mostExpensive(desc)}")

    println("\nТестирование функции fromRoman:")
    println("fromRoman(\"XXIII\") = ${fromRoman("XXIII")}")
    println("fromRoman(\"XLIV\") = ${fromRoman("XLIV")}")
    println("fromRoman(\"C\") = ${fromRoman("C")}")

    println("\nТестирование функции computeDeviceCells:")
    println("computeDeviceCells(10, \"+>+>+>+>+\", 100) = ${computeDeviceCells(10, "+>+>+>+>+", 100)}")
}