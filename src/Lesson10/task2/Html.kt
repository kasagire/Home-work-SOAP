/**
 * Средняя (5 баллов)
 *
 * Преобразовать заданный список строк в нумерованный список HTML.
 * К примеру, из ["Alpha", "Beta", "Omega"] мы должны получить следующее
 * <html><body>
 * <ol>
 *     <li>Alpha</li>
 *     <li>Beta</li>
 *     <li>Omega</li>
 * </ol>
 * </body></html>
 *
 * В этом задании вы должны заменить на реальный код содержимое функций myList, myItem, unaryPlus
 * и использовать их в функции generateSimpleHtml
 *
 * Пробелы и переводы строк между тегами в этом задании значения не имеют.
 */
fun generateListHtml(list: List<String>): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            myList {
                for (item in list) {
                    myItem {
                        +item
                    }
                }
            }
        }
    }
    return sb.toString()
}

private class HTML(val sb: StringBuilder) {
    fun myBody(init: HTMLBody.() -> Unit): HTMLBody {
        val body = HTMLBody(sb)
        sb.append("<body>")
        body.init()
        sb.append("</body>")
        return body
    }
}

private class HTMLBody(val sb: StringBuilder) {
    operator fun String.unaryPlus() {
        sb.append(this)
    }

    fun myList(init: HTMLList.() -> Unit): HTMLList {
        val list = HTMLList(sb)
        sb.append("<ol>")
        list.init()
        sb.append("</ol>")
        return list
    }

    fun myUnorderedList(init: HTMLList.() -> Unit): HTMLList {
        val list = HTMLList(sb)
        sb.append("<ul>")
        list.init()
        sb.append("</ul>")
        return list
    }
}

private class HTMLList(val sb: StringBuilder) {
    fun myItem(init: HTMLItem.() -> Unit): HTMLItem {
        val item = HTMLItem(sb)
        sb.append("<li>")
        item.init()
        sb.append("</li>")
        return item
    }
}

private class HTMLItem(val sb: StringBuilder) {
    operator fun String.unaryPlus() {
        sb.append(this)
    }
}

private fun StringBuilder.myHtml(init: HTML.() -> Unit): HTML {
    val html = HTML(this)
    append("<html>")
    html.init()
    append("</html>")
    return html
}

/**
 * Дополнительные функции для расширения функциональности
 */

/**
 * Генерация ненумерованного списка (ul)
 */
fun generateUnorderedListHtml(list: List<String>): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            myUnorderedList {
                for (item in list) {
                    myItem {
                        +item
                    }
                }
            }
        }
    }
    return sb.toString()
}

/**
 * Генерация HTML с заголовком
 */
fun generateListWithTitleHtml(title: String, list: List<String>): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            +"<h1>$title</h1>"
            myList {
                for (item in list) {
                    myItem {
                        +item
                    }
                }
            }
        }
    }
    return sb.toString()
}

/**
 * Генерация HTML с заголовком для неупорядоченного списка
 */
fun generateUnorderedListWithTitleHtml(title: String, list: List<String>): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            +"<h1>$title</h1>"
            myUnorderedList {
                for (item in list) {
                    myItem {
                        +item
                    }
                }
            }
        }
    }
    return sb.toString()
}

/**
 * Генерация HTML с вложенными списками
 */
fun generateNestedListHtml(mainItems: List<Pair<String, List<String>>>): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            myList {
                for ((mainItem, subItems) in mainItems) {
                    myItem {
                        +mainItem
                        if (subItems.isNotEmpty()) {
                            myUnorderedList {
                                for (subItem in subItems) {
                                    myItem {
                                        +subItem
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return sb.toString()
}

/**
 * Генерация описательного списка (dl)
 */
fun generateDescriptionListHtml(items: List<Pair<String, String>>): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            sb.append("<dl>")
            for ((term, description) in items) {
                sb.append("<dt>$term</dt>")
                sb.append("<dd>$description</dd>")
            }
            sb.append("</dl>")
        }
    }
    return sb.toString()
}

/**
 * Генерация списка с классами CSS
 */
fun generateStyledListHtml(list: List<String>, listClass: String = "", itemClass: String = ""): String {
    val sb = StringBuilder()
    sb.myHtml {
        myBody {
            val listAttr = if (listClass.isNotEmpty()) " class=\"$listClass\"" else ""
            sb.append("<ol$listAttr>")
            for (item in list) {
                val itemAttr = if (itemClass.isNotEmpty()) " class=\"$itemClass\"" else ""
                sb.append("<li$itemAttr>$item</li>")
            }
            sb.append("</ol>")
        }
    }
    return sb.toString()
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== ТЕСТИРОВАНИЕ ГЕНЕРАЦИИ HTML СТРОК ===")

    // 1. Тестирование основной функции
    println("\n1. ТЕСТИРОВАНИЕ generateListHtml (упорядоченный список):")
    val simpleList = listOf("Alpha", "Beta", "Omega", "Gamma", "Delta")
    val simpleHtml = generateListHtml(simpleList)
    println("Входной список: $simpleList")
    println("Сгенерированный HTML:")
    println(simpleHtml)

    // 2. Тестирование неупорядоченного списка
    println("\n2. ТЕСТИРОВАНИЕ generateUnorderedListHtml (неупорядоченный список):")
    val unorderedList = listOf("Красный", "Зеленый", "Синий")
    val unorderedHtml = generateUnorderedListHtml(unorderedList)
    println("Входной список: $unorderedList")
    println("Сгенерированный HTML:")
    println(unorderedHtml)

    // 3. Тестирование с пустым списком
    println("\n3. ТЕСТИРОВАНИЕ С ПУСТЫМ СПИСКОМ:")
    val emptyList = emptyList<String>()
    val emptyHtml = generateListHtml(emptyList)
    val emptyUnorderedHtml = generateUnorderedListHtml(emptyList)
    println("Упорядоченный список (пустой): $emptyHtml")
    println("Неупорядоченный список (пустой): $emptyUnorderedHtml")

    // 4. Тестирование с одним элементом
    println("\n4. ТЕСТИРОВАНИЕ С ОДНИМ ЭЛЕМЕНТОМ:")
    val singleList = listOf("Единственный элемент")
    val singleHtml = generateListHtml(singleList)
    val singleUnorderedHtml = generateUnorderedListHtml(singleList)
    println("Упорядоченный список: $singleHtml")
    println("Неупорядоченный список: $singleUnorderedHtml")

    // 5. Тестирование функции с заголовком
    println("\n5. ТЕСТИРОВАНИЕ СПИСКОВ С ЗАГОЛОВКОМ:")
    val shoppingList = listOf("Молоко", "Хлеб", "Яйца", "Фрукты")

    val titledOrderedHtml = generateListWithTitleHtml("Список покупок (упорядоченный)", shoppingList)
    println("Упорядоченный список с заголовком:")
    println(titledOrderedHtml)

    val titledUnorderedHtml = generateUnorderedListWithTitleHtml("Список покупок (неупорядоченный)", shoppingList)
    println("\nНеупорядоченный список с заголовком:")
    println(titledUnorderedHtml)

    // 6. Тестирование функции с вложенными списками
    println("\n6. ТЕСТИРОВАНИЕ ВЛОЖЕННЫХ СПИСКОВ:")
    val nestedData = listOf(
        "Фрукты" to listOf("Яблоки", "Бананы", "Апельсины"),
        "Овощи" to listOf("Морковь", "Картофель", "Помидоры"),
        "Молочные продукты" to listOf("Молоко", "Сыр", "Йогурт"),
        "Напитки" to emptyList()
    )
    val nestedHtml = generateNestedListHtml(nestedData)
    println("Вложенные данные: ")
    nestedData.forEach { (main, sub) ->
        println("  $main: $sub")
    }
    println("\nСгенерированный HTML:")
    println(nestedHtml)

    // 7. Тестирование описательного списка
    println("\n7. ТЕСТИРОВАНИЕ ОПИСАТЕЛЬНОГО СПИСКА (dl):")
    val terms = listOf(
        "HTML" to "HyperText Markup Language",
        "CSS" to "Cascading Style Sheets",
        "JS" to "JavaScript"
    )
    val descriptionHtml = generateDescriptionListHtml(terms)
    println("Термины и определения: $terms")
    println("Сгенерированный HTML:")
    println(descriptionHtml)

    // 8. Тестирование списка с CSS классами
    println("\n8. ТЕСТИРОВАНИЕ СПИСКА С CSS КЛАССАМИ:")
    val styledList = listOf("Первый пункт", "Второй пункт", "Третий пункт")
    val styledHtml = generateStyledListHtml(styledList, "my-list", "my-item")
    println("Список с классами CSS:")
    println(styledHtml)

    // 9. Тестирование корректности HTML структуры
    println("\n9. ПРОВЕРКА КОРРЕКТНОСТИ HTML СТРУКТУРЫ:")

    fun validateHtml(html: String, expectedTags: List<String>): Boolean {
        val hasAllTags = expectedTags.all { tag -> html.contains(tag) }
        val tagBalance = html.count { it == '<' } == html.count { it == '>' }
        return hasAllTags && tagBalance
    }

    val testOrderedHtml = generateListHtml(listOf("Test1", "Test2"))
    val orderedTags = listOf("<html>", "</html>", "<body>", "</body>", "<ol>", "</ol>", "<li>", "</li>")
    val isOrderedValid = validateHtml(testOrderedHtml, orderedTags)
    println("Упорядоченный список корректен: $isOrderedValid")

    val testUnorderedHtml = generateUnorderedListHtml(listOf("Test1", "Test2"))
    val unorderedTags = listOf("<html>", "</html>", "<body>", "</body>", "<ul>", "</ul>", "<li>", "</li>")
    val isUnorderedValid = validateHtml(testUnorderedHtml, unorderedTags)
    println("Неупорядоченный список корректен: $isUnorderedValid")

    // 10. Сравнение результатов
    println("\n10. СРАВНЕНИЕ РЕЗУЛЬТАТОВ:")
    val compareList = listOf("Один", "Два", "Три")

    println("Исходный список: $compareList")
    println("\nУпорядоченный список (ol):")
    println(generateListHtml(compareList))

    println("\nНеупорядоченный список (ul):")
    println(generateUnorderedListHtml(compareList))

    println("\n=== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ===")
}