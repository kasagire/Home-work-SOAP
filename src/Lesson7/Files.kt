import java.io.File

/**
 * Простая (8 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * Некоторые его строки помечены на удаление первым символом _ (подчёркивание).
 * Перенести в выходной файл с именем outputName все строки входного файла, убрав при этом помеченные на удаление.
 * Все остальные строки должны быть перенесены без изменений, включая пустые строки.
 * Подчёркивание в середине и/или в конце строк значения не имеет.
 */
fun deleteMarked(inputName: String, outputName: String) {
    File(outputName).bufferedWriter().use { writer ->
        File(inputName).forEachLine { line ->
            if (line.isNotEmpty() && line[0] != '_') {
                writer.write(line)
                writer.newLine()
            } else if (line.isEmpty()) {
                writer.newLine()
            }
        }
    }
}

/**
 * Средняя (14 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст.
 * На вход подаётся список строк substrings.
 * Вернуть ассоциативный массив с числом вхождений каждой из строк в текст.
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 *
 */
fun countSubstrings(inputName: String, substrings: List<String>): Map<String, Int> {
    val text = File(inputName).readText().lowercase()
    val result = mutableMapOf<String, Int>()

    for (substring in substrings) {
        val lowerSubstring = substring.lowercase()
        var count = 0
        var index = text.indexOf(lowerSubstring)

        while (index != -1) {
            count++
            index = text.indexOf(lowerSubstring, index + 1)
        }

        result[substring] = count
    }

    return result
}

/**
 * Средняя (12 баллов)
 *
 * В русском языке, как правило, после букв Ж, Ч, Ш, Щ пишется И, А, У, а не Ы, Я, Ю.
 * Во входном файле с именем inputName содержится некоторый текст на русском языке.
 * Проверить текст во входном файле на соблюдение данного правила и вывести в выходной
 * файл outputName текст с исправленными ошибками.
 *
 * Регистр заменённых букв следует сохранять.
 *
 * Исключения (жюри, брошюра, парашют) в рамках данного задания обрабатывать не нужно
 *
 */
fun sibilants(inputName: String, outputName: String) {
    val sibilants = setOf('ж', 'ч', 'ш', 'щ', 'Ж', 'Ч', 'Ш', 'Щ')
    val replacements = mapOf(
        'ы' to 'и', 'я' to 'а', 'ю' to 'у',
        'Ы' to 'И', 'Я' to 'А', 'Ю' to 'У'
    )

    File(outputName).bufferedWriter().use { writer ->
        File(inputName).forEachLine { line ->
            if (line.isEmpty()) {
                writer.newLine()
                return@forEachLine
            }

            val result = StringBuilder()
            for (i in line.indices) {
                val currentChar = line[i]

                if (i > 0 && line[i - 1] in sibilants && currentChar in replacements) {
                    result.append(replacements[currentChar])
                } else {
                    result.append(currentChar)
                }
            }

            writer.write(result.toString())
            writer.newLine()
        }
    }
}

/**
 * Средняя (15 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 * Вывести его в выходной файл с именем outputName, выровняв по центру
 * относительно самой длинной строки.
 *
 * Выравнивание следует производить путём добавления пробелов в начало строки.
 *
 *
 * Следующие правила должны быть выполнены:
 * 1) Пробелы в начале и в конце всех строк не следует сохранять.
 * 2) В случае невозможности выравнивания строго по центу, строка должна быть сдвинута в ЛЕВУЮ сторону
 * 3) Пустые строки не являются особым случаем, их тоже следует выравнивать
 * 4) Число строк в выходном файле должно быть равно числу строк во входном (в т. ч. пустых)
 *
 */
fun centerFile(inputName: String, outputName: String) {
    val lines = File(inputName).readLines()
    val trimmedLines = lines.map { it.trim() }
    val maxLength = trimmedLines.maxOfOrNull { it.length } ?: 0

    File(outputName).bufferedWriter().use { writer ->
        for (trimmedLine in trimmedLines) {
            val spacesToAdd = maxLength - trimmedLine.length
            val leftSpaces = spacesToAdd / 2
            val centeredLine = " ".repeat(leftSpaces) + trimmedLine
            writer.write(centeredLine)
            writer.newLine()
        }
    }
}

/**
 * Сложная (20 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 * Вывести его в выходной файл с именем outputName, выровняв по левому и правому краю относительно
 * самой длинной строки.
 * Выравнивание производить, вставляя дополнительные пробелы между словами: равномерно по всей строке
 *
 * Слова внутри строки отделяются друг от друга одним или более пробелом.
 *
 * Следующие правила должны быть выполнены:
 * 1) Каждая строка входного и выходного файла не должна начинаться или заканчиваться пробелом.
 * 2) Пустые строки или строки из пробелов трансформируются в пустые строки без пробелов.
 * 3) Строки из одного слова выводятся без пробелов.
 * 4) Число строк в выходного файла должно быть равно числу строк во входном (в т. ч. пустых).
 *
 * Равномерность определяется следующими формальными правилами:
 * 5) Число пробелов между каждыми двумя парами соседних слов не должно отличаться более, чем на 1.
 * 6) Число пробелов между более левой парой соседних слов должно быть больше или равно числу пробелов
 *    между более правой парой соседних слов.
 *
 * Следует учесть, что входной файл может содержать последовательности из нескольких пробелов  между слвоами. Такие
 * последовательности следует учитывать при выравнивании и при необходимости избавляться от лишних пробелов.
 * Из этого следуют следующие правила:
 * 7) В самой длинной строке каждая пара соседних слов должна быть отделена В ТОЧНОСТИ одним пробелом
 * 8) Если входной файл удовлетворяет требованиям 1-7, то он должен быть в точности идентичен выходному файлу
 */
fun alignFileByWidth(inputName: String, outputName: String) {
    val lines = File(inputName).readLines()
    val processedLines = mutableListOf<List<String>>()
    var maxLength = 0

    // Обработка строк
    for (line in lines) {
        val trimmedLine = line.trim()
        if (trimmedLine.isEmpty()) {
            processedLines.add(emptyList())
        } else {
            val words = trimmedLine.split(Regex("\\s+"))
            processedLines.add(words)

            // Рассчитываем длину строки с одним пробелом между словами
            val lineLength = words.sumOf { it.length } + (words.size - 1)
            if (lineLength > maxLength) {
                maxLength = lineLength
            }
        }
    }

    File(outputName).bufferedWriter().use { writer ->
        for (words in processedLines) {
            if (words.isEmpty()) {
                writer.newLine()
                continue
            }

            if (words.size == 1) {
                writer.write(words[0])
                writer.newLine()
                continue
            }

            val totalSpacesNeeded = maxLength - words.sumOf { it.length }
            val gaps = words.size - 1
            val baseSpaces = totalSpacesNeeded / gaps
            var extraSpaces = totalSpacesNeeded % gaps

            val result = StringBuilder(words[0])
            for (i in 1 until words.size) {
                val spaces = baseSpaces + if (extraSpaces > 0) 1 else 0
                if (extraSpaces > 0) extraSpaces--

                result.append(" ".repeat(spaces))
                result.append(words[i])
            }

            writer.write(result.toString())
            writer.newLine()
        }
    }
}

/**
 * Средняя (14 баллов)
 *
 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 *
 * Вернуть ассоциативный массив, содержащий 20 наиболее часто встречающихся слов с их количеством.
 * Если в тексте менее 20 различных слов, вернуть все слова.
 * Вернуть ассоциативный массив с числом слов больше 20, если 20-е, 21-е, ..., последнее слова
 * имеют одинаковое количество вхождений (см. также тест файла input/onegin.txt).
 *
 * Словом считается непрерывная последовательность из букв (кириллических,
 * либо латинских, без знаков препинания и цифр).
 * Цифры, пробелы, знаки препинания считаются разделителями слов:
 * Привет, привет42, привет!!! -привет?!
 * ^ В этой строчке слово привет встречается 4 раза.
 *
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 * Ключи в ассоциативном массиве должны быть в нижнем регистре.
 *
 */
fun top20Words(inputName: String): Map<String, Int> {
    val text = File(inputName).readText()
    val words = text.lowercase()
        .split(Regex("[^а-яёa-z]+"))
        .filter { it.isNotEmpty() }

    val wordCount = mutableMapOf<String, Int>()
    for (word in words) {
        wordCount[word] = wordCount.getOrDefault(word, 0) + 1
    }

    return if (wordCount.size <= 20) {
        wordCount
    } else {
        val sortedEntries = wordCount.entries.sortedWith(
            compareByDescending<Map.Entry<String, Int>> { it.value }
                .thenBy { it.key }
        )

        val result = mutableMapOf<String, Int>()
        val thresholdValue = sortedEntries[19].value

        for (entry in sortedEntries) {
            result[entry.key] = entry.value
            if (result.size >= 20 && entry.value < thresholdValue) {
                break
            }
        }

        result
    }
}

/**
 * Средняя (14 баллов)
 *
 * Реализовать транслитерацию текста из входного файла в выходной файл посредством динамически задаваемых правил.

 * Во входном файле с именем inputName содержится некоторый текст (в том числе, и на русском языке).
 *
 * В ассоциативном массиве dictionary содержится словарь, в котором некоторым символам
 * ставится в соответствие строчка из символов, например
 * mapOf('з' to "zz", 'р' to "r", 'д' to "d", 'й' to "y", 'М' to "m", 'и' to "yy", '!' to "!!!")
 *
 * Необходимо вывести в итоговый файл с именем outputName
 * содержимое текста с заменой всех символов из словаря на соответствующие им строки.
 *
 * При этом регистр символов в словаре должен игнорироваться,
 * но при выводе символ в верхнем регистре отображается в строку, начинающуюся с символа в верхнем регистре.
 *
 * Пример.
 * Входной текст: Здравствуй, мир!
 *
 * заменяется на
 *
 * Выходной текст: Zzdrавствуy, mир!!!
 *
 * Пример 2.
 *
 * Входной текст: Здравствуй, мир!
 * Словарь: mapOf('з' to "zZ", 'р' to "r", 'д' to "d", 'й' to "y", 'М' to "m", 'и' to "YY", '!' to "!!!")
 *
 * заменяется на
 *
 * Выходной текст: Zzdrавствуy, mир!!!
 *
 * Обратите внимание: данная функция не имеет возвращаемого значения
 */
fun transliterate(inputName: String, dictionary: Map<Char, String>, outputName: String) {
    val lowerDictionary = dictionary.mapKeys { it.key.lowercaseChar() }

    File(outputName).bufferedWriter().use { writer ->
        File(inputName).forEachLine { line ->
            val result = StringBuilder()

            for (char in line) {
                val lowerChar = char.lowercaseChar()
                val replacement = lowerDictionary[lowerChar]

                if (replacement != null) {
                    if (char.isUpperCase()) {
                        val capitalized = replacement.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecaseChar() else it
                        }
                        result.append(capitalized)
                    } else {
                        result.append(replacement)
                    }
                } else {
                    result.append(char)
                }
            }

            writer.write(result.toString())
            writer.newLine()
        }
    }
}

/**
 * Средняя (12 баллов)
 *
 * Во входном файле с именем inputName имеется словарь с одним словом в каждой строчке.
 * Выбрать из данного словаря наиболее длинное слово,
 * в котором все буквы разные, например: Неряшливость, Четырёхдюймовка.
 * Вывести его в выходной файл с именем outputName.
 * Если во входном файле имеется несколько слов с одинаковой длиной, в которых все буквы разные,
 * в выходной файл следует вывести их все через запятую.
 * Регистр букв игнорировать, то есть буквы е и Е считать одинаковыми.
 *
 * Пример входного файла:
 * Карминовый
 * Боязливый
 * Некрасивый
 * Остроумный
 * БелогЛазый
 * ФиолетОвый

 * Соответствующий выходной файл:
 * Карминовый, Некрасивый
 *
 * Обратите внимание: данная функция не имеет возвращаемого значения
 */
fun chooseLongestChaoticWord(inputName: String, outputName: String) {
    val words = File(inputName).readLines()
    val chaoticWords = mutableListOf<String>()
    var maxLength = 0

    for (word in words) {
        val lowerWord = word.lowercase()
        val uniqueChars = lowerWord.toSet()

        if (uniqueChars.size == lowerWord.length) {
            if (word.length > maxLength) {
                maxLength = word.length
                chaoticWords.clear()
                chaoticWords.add(word)
            } else if (word.length == maxLength) {
                chaoticWords.add(word)
            }
        }
    }

    File(outputName).writeText(chaoticWords.joinToString(", "))
}

/**
 * Главная функция для демонстрации работы всех функций
 */
fun main() {
    println("=== Демонстрация работы первых 8 функций ===\n")

    // Создаем тестовые файлы
    createTestFiles()

    // 1. deleteMarked
    println("1. Функция deleteMarked:")
    deleteMarked("test_input.txt", "test_output_delete.txt")
    println("   Входной файл 'test_input.txt' обработан.")
    println("   Результат сохранен в 'test_output_delete.txt'")
    println("   Содержимое результата:")
    println("   " + "-".repeat(40))
    File("test_output_delete.txt").readLines().forEach { println("   $it") }
    println("   " + "-".repeat(40))

    // 2. countSubstrings
    println("\n2. Функция countSubstrings:")
    val substrings = listOf("тест", "пример", "строка", "удалить")
    val substringCounts = countSubstrings("test_input.txt", substrings)
    println("   Поиск подстрок: $substrings")
    println("   Результат: $substringCounts")

    // 3. sibilants
    println("\n3. Функция sibilants:")
    sibilants("test_russian.txt", "test_output_sibilants.txt")
    println("   Исправлены ошибки после Ж, Ч, Ш, Щ")
    println("   Результат сохранен в 'test_output_sibilants.txt'")
    println("   Содержимое результата:")
    println("   " + "-".repeat(40))
    File("test_output_sibilants.txt").readLines().forEach { println("   $it") }
    println("   " + "-".repeat(40))

    // 4. centerFile
    println("\n4. Функция centerFile:")
    centerFile("test_center.txt", "test_output_center.txt")
    println("   Текст выровнен по центру")
    println("   Результат сохранен в 'test_output_center.txt'")
    println("   Содержимое результата:")
    println("   " + "-".repeat(40))
    File("test_output_center.txt").readLines().forEach { println("   '$it'") }
    println("   " + "-".repeat(40))

    // 5. alignFileByWidth
    println("\n5. Функция alignFileByWidth:")
    alignFileByWidth("test_align.txt", "test_output_align.txt")
    println("   Текст выровнен по ширине")
    println("   Результат сохранен в 'test_output_align.txt'")
    println("   Содержимое результата:")
    println("   " + "-".repeat(40))
    File("test_output_align.txt").readLines().forEach { println("   '$it'") }
    println("   " + "-".repeat(40))

    // 6. top20Words
    println("\n6. Функция top20Words:")
    val topWords = top20Words("test_words.txt")
    println("   Найдены 20 самых частых слов (или меньше, если слов меньше)")
    println("   Результат (первые 10 слов):")
    topWords.entries.take(10).forEachIndexed { index, (word, count) ->
        println("   ${index + 1}. '$word' - $count раз")
    }
    if (topWords.size > 10) {
        println("   ... и еще ${topWords.size - 10} слов")
    }

    // 7. transliterate
    println("\n7. Функция transliterate:")
    val dictionary = mapOf(
        'з' to "z", 'р' to "r", 'д' to "d", 'й' to "y",
        'М' to "m", 'и' to "i", '!' to "!!!", 'а' to "a",
        'т' to "t", 'с' to "s", 'в' to "v", 'у' to "u"
    )
    transliterate("test_transliterate.txt", dictionary, "test_output_transliterate.txt")
    println("   Выполнена транслитерация по словарю")
    println("   Результат сохранен в 'test_output_transliterate.txt'")
    println("   Содержимое результата:")
    println("   " + "-".repeat(40))
    File("test_output_transliterate.txt").readLines().forEach { println("   $it") }
    println("   " + "-".repeat(40))

    // 8. chooseLongestChaoticWord
    println("\n8. Функция chooseLongestChaoticWord:")
    chooseLongestChaoticWord("test_words_dict.txt", "test_output_chaotic.txt")
    println("   Найдены самые длинные слова с разными буквами")
    println("   Результат сохранен в 'test_output_chaotic.txt'")
    println("   Содержимое результата:")
    println("   " + "-".repeat(40))
    println("   '${File("test_output_chaotic.txt").readText()}'")
    println("   " + "-".repeat(40))

    // Очищаем тестовые файлы
    cleanUpTestFiles()

    println("\n=== Демонстрация завершена ===")
}

/**
 * Функция для создания тестовых файлов
 */
fun createTestFiles() {
    // 1. Файл для deleteMarked и countSubstrings
    File("test_input.txt").writeText("""
        Это тестовая строка для проверки.
        _Эта строка должна быть удалена.
        Еще один пример строки с тестом.
        _И эта строка тоже удалится.
        Последняя строка без подчеркивания.
        _Удалить эту
        Оставить эту
        
        Пустая строка выше должна сохраниться.
    """.trimIndent())

    // 2. Файл для sibilants
    File("test_russian.txt").writeText("""
        Жызнь прекрасна и удивительна.
        Чясто мы делаем ошибки в написании.
        Шырокие просторы и щявель в поле.
        ЖЮри конкурса, брошЮра и парашЮт - это исключения.
        ЧЯстный человек, ШЯпка на голове, ЩЮка в реке.
        Пример предложения: "Жыраф чявкает шыпучую воду".
    """.trimIndent())

    // 3. Файл для centerFile
    File("test_center.txt").writeText("""
        Короткая строка
        Более длинная строка для примера
        Самая длинная строка в этом тестовом файле
        Еще одна строка
        Строка средней длины
        Маленькая
    """.trimIndent())

    // 4. Файл для alignFileByWidth
    File("test_align.txt").writeText("""
        Это пример текста который
        нужно выровнять по ширине
        чтобы все строки имели
        одинаковую длину
        максимально возможную
    """.trimIndent())

    // 5. Файл для top20Words
    File("test_words.txt").writeText("""
        Привет, привет42, привет!!! -привет?!
        Как дела? Дела хорошо, очень хорошо!
        Тест тест тест пример пример.
        Hello world, hello kotlin, kotlin is great!
        Один два три четыре пять шесть семь восемь девять десять.
        Еще слова для разнообразия текста и теста.
        Программирование это интересно и увлекательно.
        Котлин современный язык программирования.
    """.trimIndent())

    // 6. Файл для transliterate
    File("test_transliterate.txt").writeText("""
        Здравствуй, мир!
        Это тестовая строка для транслитерации.
        Привет! Как дела?
        Программирование на Kotlin.
    """.trimIndent())

    // 7. Файл для chooseLongestChaoticWord
    File("test_words_dict.txt").writeText("""
        Карминовый
        Боязливый
        Некрасивый
        Остроумный
        БелогЛазый
        ФиолетОвый
        Абракадабра
        Компьютер
        Программирование
        Лимонад
        Фотография
        Телевизор
    """.trimIndent())

    println("Созданы тестовые файлы для демонстрации.")
}

/**
 * Функция для очистки тестовых файлов
 */
fun cleanUpTestFiles() {
    val files = listOf(
        "test_input.txt", "test_output_delete.txt",
        "test_russian.txt", "test_output_sibilants.txt",
        "test_center.txt", "test_output_center.txt",
        "test_align.txt", "test_output_align.txt",
        "test_words.txt", "test_transliterate.txt",
        "test_output_transliterate.txt", "test_words_dict.txt",
        "test_output_chaotic.txt"
    )

    var deletedCount = 0
    files.forEach { fileName ->
        val file = File(fileName)
        if (file.exists()) {
            file.delete()
            deletedCount++
        }
    }

    println("\nУдалено тестовых файлов: $deletedCount")
}