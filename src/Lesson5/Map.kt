
/**
 * Простая (2 балла)
 *
 * По заданному ассоциативному массиву "студент"-"оценка за экзамен" построить
 * обратный массив "оценка за экзамен"-"список студентов с этой оценкой".
 *
 * Например:
 *   buildGrades(mapOf("Марат" to 3, "Семён" to 5, "Михаил" to 5))
 *     -> mapOf(5 to listOf("Семён", "Михаил"), 3 to listOf("Марат"))
 */
fun buildGrades(grades: Map<String, Int>): Map<Int, List<String>> {
    val result = mutableMapOf<Int, MutableList<String>>()

    for ((student, grade) in grades) {
        result.getOrPut(grade) { mutableListOf() }.add(student)
    }

    return result
}

/**
 * Простая (2 балла)
 *
 * Определить, входит ли ассоциативный массив a в ассоциативный массив b;
 * это выполняется, если все ключи из a содержатся в b с такими же значениями.
 *
 * Например:
 *   containsIn(mapOf("a" to "z"), mapOf("a" to "z", "b" to "sweet")) -> true
 *   containsIn(mapOf("a" to "z"), mapOf("a" to "zee", "b" to "sweet")) -> false
 */
fun containsIn(a: Map<String, String>, b: Map<String, String>): Boolean {
    for ((key, value) in a) {
        if (b[key] != value) {
            return false
        }
    }
    return true
}

/**
 * Простая (2 балла)
 *
 * Удалить из изменяемого ассоциативного массива все записи,
 * которые встречаются в заданном ассоциативном массиве.
 * Записи считать одинаковыми, если и ключи, и значения совпадают.
 *
 * ВАЖНО: необходимо изменить переданный в качестве аргумента
 *        изменяемый ассоциативный массив
 *
 * Например:
 *   subtractOf(a = mutableMapOf("a" to "z"), mapOf("a" to "z"))
 *     -> a changes to mutableMapOf() aka becomes empty
 */
fun subtractOf(a: MutableMap<String, String>, b: Map<String, String>) {
    val toRemove = mutableListOf<String>()

    for ((key, value) in a) {
        if (b[key] == value) {
            toRemove.add(key)
        }
    }

    for (key in toRemove) {
        a.remove(key)
    }
}

/**
 * Простая (2 балла)
 *
 * Для двух списков людей найти людей, встречающихся в обоих списках.
 * В выходном списке не должно быть повторяющихся элементов,
 * т. е. whoAreInBoth(listOf("Марат", "Семён, "Марат"), listOf("Марат", "Марат")) == listOf("Марат")
 */
fun whoAreInBoth(a: List<String>, b: List<String>): List<String> {
    return a.toSet().intersect(b.toSet()).toList()
}

/**
 * Средняя (3 балла)
 *
 * Объединить два ассоциативных массива `mapA` и `mapB` с парами
 * "имя"-"номер телефона" в итоговый ассоциативный массив, склеивая
 * значения для повторяющихся ключей через запятую.
 * В случае повторяющихся *ключей* значение из mapA должно быть
 * перед значением из mapB.
 *
 * Повторяющиеся *значения* следует добавлять только один раз.
 *
 * Например:
 *   mergePhoneBooks(
 *     mapOf("Emergency" to "112", "Police" to "02"),
 *     mapOf("Emergency" to "911", "Police" to "02")
 *   ) -> mapOf("Emergency" to "112, 911", "Police" to "02")
 */
fun mergePhoneBooks(mapA: Map<String, String>, mapB: Map<String, String>): Map<String, String> {
    val result = mutableMapOf<String, String>()

    // Все ключи из обоих массивов
    val allKeys = mapA.keys + mapB.keys

    for (key in allKeys) {
        val values = mutableSetOf<String>()

        // Добавляем значение из mapA если есть
        mapA[key]?.let { values.add(it) }
        // Добавляем значение из mapB если есть
        mapB[key]?.let { values.add(it) }

        // Склеиваем уникальные значения через запятую
        result[key] = values.joinToString(", ")
    }

    return result
}

/**
 * Средняя (4 балла)
 *
 * Для заданного списка пар "акция"-"стоимость" вернуть ассоциативный массив,
 * содержащий для каждой акции ее усредненную стоимость.
 *
 * Например:
 *   averageStockPrice(listOf("MSFT" to 100.0, "MSFT" to 200.0, "NFLX" to 40.0))
 *     -> mapOf("MSFT" to 150.0, "NFLX" to 40.0)
 */
fun averageStockPrice(stockPrices: List<Pair<String, Double>>): Map<String, Double> {
    val result = mutableMapOf<String, Double>()
    val counts = mutableMapOf<String, Int>()

    for ((stock, price) in stockPrices) {
        result[stock] = result.getOrDefault(stock, 0.0) + price
        counts[stock] = counts.getOrDefault(stock, 0) + 1
    }

    // Вычисляем средние
    for ((stock, total) in result) {
        result[stock] = total / counts[stock]!!
    }

    return result
}

/**
 * Средняя (4 балла)
 *
 * Входными данными является ассоциативный массив
 * "название товара"-"пара (тип товара, цена товара)"
 * и тип интересующего нас товара.
 * Необходимо вернуть название товара заданного типа с минимальной стоимостью
 * или null в случае, если товаров такого типа нет.
 *
 * Например:
 *   findCheapestStuff(
 *     mapOf("Мария" to ("печенье" to 20.0), "Орео" to ("печенье" to 100.0)),
 *     "печенье"
 *   ) -> "Мария"
 */
fun findCheapestStuff(stuff: Map<String, Pair<String, Double>>, kind: String): String? {
    var minPrice = Double.MAX_VALUE
    var cheapestItem: String? = null

    for ((item, pair) in stuff) {
        val (itemKind, price) = pair
        if (itemKind == kind && price < minPrice) {
            minPrice = price
            cheapestItem = item
        }
    }
    return cheapestItem
}

/**
 * Средняя (3 балла)
 *
 * Для заданного набора символов определить, можно ли составить из него
 * указанное слово (регистр символов игнорируется)
 *
 * Например:
 *   canBuildFrom(listOf('a', 'b', 'o'), "baobab") -> true
 */
fun canBuildFrom(chars: List<Char>, word: String): Boolean {
    val availableChars = chars.map { it.lowercaseChar() }.toMutableList()

    for (char in word.lowercase()) {
        val index = availableChars.indexOf(char)
        if (index == -1) {
            return false
        }
        availableChars.removeAt(index)
    }

    return true
}

/**
 * Средняя (4 балла)
 *
 * Найти в заданном списке повторяющиеся элементы и вернуть
 * ассоциативный массив с информацией о числе повторений
 * для каждого повторяющегося элемента.
 * Если элемент встречается только один раз, включать его в результат
 * не следует.
 *
 * Например:
 *   extractRepeats(listOf("a", "b", "a")) -> mapOf("a" to 2)
 */
fun extractRepeats(list: List<String>): Map<String, Int> {
    val counts = mutableMapOf<String, Int>()
    val result = mutableMapOf<String, Int>()

    // Подсчет повторений
    for (item in list) {
        counts[item] = counts.getOrDefault(item, 0) + 1
    }

    // Добавляем только те, что повторяются
    for ((item, count) in counts) {
        if (count > 1) {
            result[item] = count
        }
    }

    return result
}

/**
 * Средняя (3 балла)
 *
 * Для заданного списка слов определить, содержит ли он анаграммы.
 * Два слова здесь считаются анаграммами, если они имеют одинаковую длину
 * и одно можно составить из второго перестановкой его букв.
 * Скажем, тор и рот или роза и азор это анаграммы,
 * а поле и полено -- нет.
 *
 * Например:
 *   hasAnagrams(listOf("тор", "свет", "рот")) -> true
 */
fun hasAnagrams(words: List<String>): Boolean {
    val normalizedWords = mutableSetOf<String>()

    for (word in words) {
        // Нормализуем слово: сортируем буквы
        val normalized = word.lowercase().toCharArray().sorted().joinToString("")

        if (normalizedWords.contains(normalized)) {
            return true
        }
        normalizedWords.add(normalized)
    }

    return false
}

/**
 * Сложная (5 баллов)
 *
 * Для заданного ассоциативного массива знакомых через одно рукопожатие `friends`
 * необходимо построить его максимальное расширение по рукопожатиям, то есть,
 * для каждого человека найти всех людей, с которыми он знаком через любое
 * количество рукопожатий.
 *
 * Считать, что все имена людей являются уникальными, а также что рукопожатия
 * являются направленными, то есть, если Марат знает Свету, то это не означает,
 * что Света знает Марата.
 *
 * Оставлять пустой список знакомых для людей, которые их не имеют (см. EvilGnome ниже),
 * в том числе для случая, когда данного человека нет в ключах, но он есть в значениях
 * (см. GoodGnome ниже).
 *
 * Например:
 *   propagateHandshakes(
 *     mapOf(
 *       "Marat" to setOf("Mikhail", "Sveta"),
 *       "Sveta" to setOf("Marat"),
 *       "Mikhail" to setOf("Sveta"),
 *       "Friend" to setOf("GoodGnome"),
 *       "EvilGnome" to setOf()
 *     )
 *   ) -> mapOf(
 *          "Marat" to setOf("Mikhail", "Sveta"),
 *          "Sveta" to setOf("Marat", "Mikhail"),
 *          "Mikhail" to setOf("Sveta", "Marat"),
 *          "Friend" to setOf("GoodGnome"),
 *          "EvilGnome" to setOf(),
 *          "GoodGnome" to setOf()
 *        )
 */
fun propagateHandshakes(friends: Map<String, Set<String>>): Map<String, Set<String>> {
    val result = mutableMapOf<String, MutableSet<String>>()

    // Собираем всех людей
    val allPeople = mutableSetOf<String>()
    allPeople.addAll(friends.keys)
    for (knows in friends.values) {
        allPeople.addAll(knows)
    }

    // Инициализируем результат для всех людей
    for (person in allPeople) {
        result[person] = mutableSetOf()
        // Прямые знакомства
        friends[person]?.let { result[person]!!.addAll(it) }
    }

    // Проходим по всем людям и ищем косвенные знакомства
    var changed: Boolean
    do {
        changed = false
        for ((person, knows) in result) {
            val currentSize = knows.size
            // Добавляем знакомых знакомых
            for (friend in knows.toList()) {
                result[friend]?.let { knows.addAll(it) }
            }
            // Убираем себя из списка знакомых
            knows.remove(person)
            if (knows.size > currentSize) {
                changed = true
            }
        }
    } while (changed)

    return result
}

/**
 * Сложная (6 баллов)
 *
 * Для заданного списка неотрицательных чисел и числа определить,
 * есть ли в списке пара чисел таких, что их сумма равна заданному числу.
 * Если да, верните их индексы в виде Pair<Int, Int>;
 * если нет, верните пару Pair(-1, -1).
 *
 * Индексы в результате должны следовать в порядке (меньший, больший).
 *
 * Постарайтесь сделать ваше решение как можно более эффективным,
 * используя то, что вы узнали в данном уроке.
 *
 * Например:
 *   findSumOfTwo(listOf(1, 2, 3), 4) -> Pair(0, 2)
 *   findSumOfTwo(listOf(1, 2, 3), 6) -> Pair(-1, -1)
 */
fun findSumOfTwo(list: List<Int>, number: Int): Pair<Int, Int> {
    val seen = mutableMapOf<Int, Int>()

    for ((index, value) in list.withIndex()) {
        val complement = number - value
        if (complement in seen) {
            val firstIndex = seen[complement]!!
            return if (firstIndex < index) Pair(firstIndex, index) else Pair(index, firstIndex)
        }
        seen[value] = index
    }

    return Pair(-1, -1)
}

/**
 * Очень сложная (8 баллов)
 *
 * Входными данными является ассоциативный массив
 * "название сокровища"-"пара (вес сокровища, цена сокровища)"
 * и вместимость вашего рюкзака.
 * Необходимо вернуть множество сокровищ с максимальной суммарной стоимостью,
 * которые вы можете унести в рюкзаке.
 *
 * Перед решением этой задачи лучше прочитать статью Википедии "Динамическое программирование".
 *
 * Например:
 *   bagPacking(
 *     mapOf("Кубок" to (500 to 2000), "Слиток" to (1000 to 5000)),
 *     850
 *   ) -> setOf("Кубок")
 *   bagPacking(
 *     mapOf("Кубок" to (500 to 2000), "Слиток" to (1000 to 5000)),
 *     450
 *   ) -> emptySet()
 */
fun bagPacking(treasures: Map<String, Pair<Int, Int>>, capacity: Int): Set<String> {
    // Преобразуем в списки для удобства
    val items = treasures.entries.toList()
    val n = items.size

    // Создаем таблицу для ДП: dp[i][w] = максимальная стоимость для первых i предметов и вместимости w
    val dp = Array(n + 1) { IntArray(capacity + 1) }

    // Заполняем таблицу
    for (i in 1..n) {
        val entry = items[i - 1]
        val name = entry.key
        val (weight, value) = entry.value

        for (w in 0..capacity) {
            if (weight <= w) {
                dp[i][w] = maxOf(dp[i - 1][w], dp[i - 1][w - weight] + value)
            } else {
                dp[i][w] = dp[i - 1][w]
            }
        }
    }

    // Восстанавливаем решение
    val result = mutableSetOf<String>()
    var w = capacity

    for (i in n downTo 1) {
        if (dp[i][w] != dp[i - 1][w]) {
            val entry = items[i - 1]
            val name = entry.key
            val (weight, _) = entry.value
            result.add(name)
            w -= weight
        }
    }

    return result
}

/**
 * Тестирующая функция для проверки всех решений
 */
fun main() {
    println("Тестирование всех функций:\n")

    // Тест 1: buildGrades
    println("1. buildGrades:")
    val grades = mapOf("Марат" to 3, "Семён" to 5, "Михаил" to 5)
    println("   buildGrades($grades) = ${buildGrades(grades)}")

    // Тест 2: containsIn
    println("\n2. containsIn:")
    val test2a = mapOf("a" to "z")
    val test2b = mapOf("a" to "z", "b" to "sweet")
    val test2c = mapOf("a" to "zee", "b" to "sweet")
    println("   containsIn($test2a, $test2b) = ${containsIn(test2a, test2b)}")
    println("   containsIn($test2a, $test2c) = ${containsIn(test2a, test2c)}")

    // Тест 3: subtractOf
    println("\n3. subtractOf:")
    val mutableMap = mutableMapOf("a" to "z", "b" to "y", "c" to "x")
    val toRemove = mapOf("a" to "z", "b" to "w")
    subtractOf(mutableMap, toRemove)
    println("   После subtractOf: $mutableMap (ожидается: {b=y, c=x})")

    // Тест 4: whoAreInBoth
    println("\n4. whoAreInBoth:")
    val list4a = listOf("Марат", "Семён", "Марат")
    val list4b = listOf("Марат", "Марат")
    println("   whoAreInBoth($list4a, $list4b) = ${whoAreInBoth(list4a, list4b)}")

    // Тест 5: mergePhoneBooks
    println("\n5. mergePhoneBooks:")
    val phoneBook1 = mapOf("Emergency" to "112", "Police" to "02")
    val phoneBook2 = mapOf("Emergency" to "911", "Police" to "02")
    println("   mergePhoneBooks($phoneBook1, $phoneBook2) = ${mergePhoneBooks(phoneBook1, phoneBook2)}")

    // Тест 6: averageStockPrice
    println("\n6. averageStockPrice:")
    val stocks = listOf("MSFT" to 100.0, "MSFT" to 200.0, "NFLX" to 40.0)
    println("   averageStockPrice($stocks) = ${averageStockPrice(stocks)}")

    // Тест 7: findCheapestStuff
    println("\n7. findCheapestStuff:")
    val stuff = mapOf("Мария" to ("печенье" to 20.0), "Орео" to ("печенье" to 100.0))
    println("   findCheapestStuff($stuff, \"печенье\") = ${findCheapestStuff(stuff, "печенье")}")
    println("   findCheapestStuff($stuff, \"торт\") = ${findCheapestStuff(stuff, "торт")}")

    // Тест 8: canBuildFrom
    println("\n8. canBuildFrom:")
    val chars = listOf('a', 'b', 'o')
    val word = "baobab"
    println("   canBuildFrom($chars, \"$word\") = ${canBuildFrom(chars, word)}")
    println("   canBuildFrom(listOf('a', 'b'), \"abc\") = ${canBuildFrom(listOf('a', 'b'), "abc")}")

    // Тест 9: extractRepeats
    println("\n9. extractRepeats:")
    val list9 = listOf("a", "b", "a")
    println("   extractRepeats($list9) = ${extractRepeats(list9)}")
    println("   extractRepeats(listOf(\"a\", \"b\", \"c\")) = ${extractRepeats(listOf("a", "b", "c"))}")

    // Тест 10: hasAnagrams
    println("\n10. hasAnagrams:")
    val words1 = listOf("тор", "свет", "рот")
    val words2 = listOf("поле", "полено")
    println("   hasAnagrams($words1) = ${hasAnagrams(words1)}")
    println("   hasAnagrams($words2) = ${hasAnagrams(words2)}")

    // Тест 11: propagateHandshakes
    println("\n11. propagateHandshakes:")
    val handshakes = mapOf(
        "Marat" to setOf("Mikhail", "Sveta"),
        "Sveta" to setOf("Marat"),
        "Mikhail" to setOf("Sveta"),
        "Friend" to setOf("GoodGnome"),
        "EvilGnome" to setOf()
    )
    println("   propagateHandshakes($handshakes) = ${propagateHandshakes(handshakes)}")

    // Тест 12: findSumOfTwo
    println("\n12. findSumOfTwo:")
    val list12 = listOf(1, 2, 3)
    println("   findSumOfTwo($list12, 4) = ${findSumOfTwo(list12, 4)}")
    println("   findSumOfTwo($list12, 6) = ${findSumOfTwo(list12, 6)}")
    println("   findSumOfTwo(listOf(3, 1, 4, 1, 5), 8) = ${findSumOfTwo(listOf(3, 1, 4, 1, 5), 8)}")

    // Тест 13: bagPacking
    println("\n13. bagPacking:")
    val treasures = mapOf("Кубок" to (500 to 2000), "Слиток" to (1000 to 5000))
    println("   bagPacking($treasures, 850) = ${bagPacking(treasures, 850)}")
    println("   bagPacking($treasures, 450) = ${bagPacking(treasures, 450)}")

}