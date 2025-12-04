/**
 * Класс "хеш-таблица с открытой адресацией"
 *
 * Общая сложность задания -- сложная, общая ценность в баллах -- 20.
 * Объект класса хранит данные типа T в виде хеш-таблицы.
 * Хеш-таблица не может содержать равные по equals элементы.
 * Подробности по организации см. статью википедии "Хеш-таблица", раздел "Открытая адресация".
 * Методы: добавление элемента, проверка вхождения элемента, сравнение двух таблиц на равенство.
 * В этом задании не разрешается использовать библиотечные классы HashSet, HashMap и им подобные,
 * а также любые функции, создающие множества (mutableSetOf и пр.).
 *
 * В конструктор хеш-таблицы передаётся её вместимость (максимальное количество элементов)
 */
class OpenHashSet<T>(val capacity: Int) {

    // Специальные значения для пометки удаленных элементов
    private companion object {
        private val DELETED = Any()
        private val EMPTY = null
    }

    /**
     * Массив для хранения элементов хеш-таблицы
     */
    internal val elements = Array<Any?>(capacity) { EMPTY }

    // Счетчик элементов
    private var _size = 0

    /**
     * Число элементов в хеш-таблице
     */
    val size: Int get() = _size

    /**
     * Признак пустоты
     */
    fun isEmpty(): Boolean = _size == 0

    /**
     * Хеш-функция, возвращающая индекс в массиве
     */
    private fun hash(element: T): Int {
        return (element.hashCode() and 0x7FFFFFFF) % capacity
    }

    /**
     * Вторичная хеш-функция для двойного хеширования
     */
    private fun hash2(element: T): Int {
        val h = element.hashCode()
        return 1 + (h and 0x7FFFFFFF) % (capacity - 1)
    }

    /**
     * Поиск индекса элемента в таблице
     * @return индекс элемента, если найден; -1 если не найден; -2 если таблица полная
     */
    private fun findIndex(element: T): Int {
        if (_size >= capacity) return -2 // Таблица полная

        val initialHash = hash(element)
        val step = hash2(element)
        var currentIndex = initialHash
        var firstDeletedIndex = -1

        // Максимальное количество попыток для избежания бесконечного цикла
        var attempts = 0
        val maxAttempts = capacity

        while (attempts < maxAttempts) {
            val currentElement = elements[currentIndex]

            when {
                currentElement == EMPTY -> {
                    // Нашли пустую ячейку
                    return if (firstDeletedIndex != -1) firstDeletedIndex else currentIndex
                }
                currentElement == DELETED -> {
                    // Запоминаем первую удаленную ячейку
                    if (firstDeletedIndex == -1) {
                        firstDeletedIndex = currentIndex
                    }
                }
                currentElement == element -> {
                    // Нашли элемент
                    return currentIndex
                }
                currentElement != null && currentElement != DELETED && currentElement == element -> {
                    // Нашли элемент через equals
                    return currentIndex
                }
            }

            // Линейное пробирование
            currentIndex = (currentIndex + 1) % capacity
            attempts++
        }

        return -2 // Не нашли подходящей ячейки
    }

    /**
     * Добавление элемента.
     * Вернуть true, если элемент был успешно добавлен,
     * или false, если такой элемент уже был в таблице, или превышена вместимость таблицы.
     */
    fun add(element: T): Boolean {
        if (_size >= capacity) return false // Таблица полная

        val index = findIndex(element)

        when (index) {
            -2 -> return false // Не нашли место
            else -> {
                if (index >= 0) {
                    if (elements[index] == element ||
                        (elements[index] != null && elements[index] != DELETED && elements[index] == element)) {
                        return false // Элемент уже существует
                    }

                    // Вставляем элемент
                    elements[index] = element
                    _size++
                    return true
                }
            }
        }

        return false
    }

    /**
     * Проверка, входит ли заданный элемент в хеш-таблицу
     */
    operator fun contains(element: T): Boolean {
        val initialHash = hash(element)
        val step = hash2(element)
        var currentIndex = initialHash
        var attempts = 0
        val maxAttempts = capacity

        while (attempts < maxAttempts) {
            val currentElement = elements[currentIndex]

            when {
                currentElement == EMPTY -> {
                    // Дошли до пустой ячейки
                    return false
                }
                currentElement == DELETED -> {
                    // Пропускаем удаленные
                }
                currentElement == element -> {
                    // Нашли точное совпадение
                    return true
                }
                currentElement != null && currentElement != DELETED && currentElement == element -> {
                    // Нашли через equals
                    return true
                }
            }

            // Линейное пробирование
            currentIndex = (currentIndex + 1) % capacity
            attempts++
        }

        return false
    }

    /**
     * Удаление элемента из таблицы
     * @return true если элемент был удален, false если элемент не найден
     */
    fun remove(element: T): Boolean {
        val initialHash = hash(element)
        val step = hash2(element)
        var currentIndex = initialHash
        var attempts = 0
        val maxAttempts = capacity

        while (attempts < maxAttempts) {
            val currentElement = elements[currentIndex]

            when {
                currentElement == EMPTY -> {
                    // Дошли до пустой ячейки, элемент не найден
                    return false
                }
                currentElement == DELETED -> {
                    // Пропускаем удаленные
                }
                currentElement == element ||
                        (currentElement != null && currentElement != DELETED && currentElement == element) -> {
                    // Нашли элемент, помечаем как удаленный
                    elements[currentIndex] = DELETED
                    _size--
                    return true
                }
            }

            // Линейное пробирование
            currentIndex = (currentIndex + 1) % capacity
            attempts++
        }

        return false
    }

    /**
     * Очистка таблицы
     */
    fun clear() {
        for (i in elements.indices) {
            elements[i] = EMPTY
        }
        _size = 0
    }

    /**
     * Получение всех элементов таблицы
     */
    fun toList(): List<T> {
        val result = mutableListOf<T>()
        for (element in elements) {
            if (element != null && element != DELETED && element != EMPTY) {
                @Suppress("UNCHECKED_CAST")
                result.add(element as T)
            }
        }
        return result
    }

    /**
     * Таблицы равны, если в них одинаковое количество элементов,
     * и любой элемент из второй таблицы входит также и в первую
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OpenHashSet<*>) return false

        // Проверяем размеры
        if (this._size != other._size) return false

        // Проверяем, что все элементы из other есть в this
        for (element in other.elements) {
            if (element != null && element != DELETED && element != EMPTY) {
                @Suppress("UNCHECKED_CAST")
                if (!this.contains(element as T)) {
                    return false
                }
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        for (element in elements) {
            if (element != null && element != DELETED && element != EMPTY) {
                result = 31 * result + element.hashCode()
            }
        }
        return result
    }

    override fun toString(): String {
        val elementsList = toList()
        return "OpenHashSet(size=$_size, capacity=$capacity, elements=${elementsList})"
    }
}

fun main() {
    // Создаем хеш-таблицу
    val hashSet = OpenHashSet<String>(10)

    println("Создали пустую таблицу: $hashSet")
    println("isEmpty: ${hashSet.isEmpty()}")
    println("size: ${hashSet.size}")

    // Добавляем элементы
    println("\nДобавляем элементы:")
    println("add(\"apple\"): ${hashSet.add("apple")}")
    println("add(\"banana\"): ${hashSet.add("banana")}")
    println("add(\"apple\"): ${hashSet.add("apple")}")
    println("add(\"orange\"): ${hashSet.add("orange")}")
    println("add(\"grape\"): ${hashSet.add("grape")}")

    println("\nПосле добавления:")
    println("hashSet = $hashSet")
    println("size: ${hashSet.size}")
    println("isEmpty: ${hashSet.isEmpty()}")

    // Проверка наличия элементов
    println("\nПроверка contains:")
    println("contains(\"apple\"): ${hashSet.contains("apple")}")
    println("contains(\"banana\"): ${hashSet.contains("banana")}")
    println("contains(\"pear\"): ${hashSet.contains("pear")}")

    // Удаление элементов
    println("\nУдаление элементов:")
    println("remove(\"banana\"): ${hashSet.remove("banana")}")
    println("remove(\"pear\"): ${hashSet.remove("pear")}")
    println("После удаления: $hashSet")
    println("size: ${hashSet.size}")

    // Добавляем снова
    println("\nДобавляем новый элемент на место удаленного:")
    println("add(\"peach\"): ${hashSet.add("peach")}")
    println("После добавления: $hashSet")

    // Создаем вторую таблицу для сравнения
    val hashSet2 = OpenHashSet<String>(10)
    hashSet2.add("apple")
    hashSet2.add("orange")
    hashSet2.add("grape")
    hashSet2.add("peach")

    println("\nВторая таблица: $hashSet2")

    // Сравнение таблиц
    println("\nСравнение таблиц:")
    println("hashSet == hashSet2: ${hashSet == hashSet2}")

    // Добавляем лишний элемент во вторую таблицу
    hashSet2.add("melon")
    println("После добавления 'melon' в hashSet2: $hashSet2")
    println("hashSet == hashSet2: ${hashSet == hashSet2}")

    // Очистка таблицы
    println("\nОчистка таблицы:")
    hashSet.clear()
    println("После clear: $hashSet")
    println("size: ${hashSet.size}")
    println("isEmpty: ${hashSet.isEmpty()}")

    // Тестирование с разными типами
    println("\n--- Тестирование с Int ---")
    val intSet = OpenHashSet<Int>(5)
    intSet.add(1)
    intSet.add(2)
    intSet.add(3)
    intSet.add(4)
    intSet.add(5)
    println("intSet: $intSet")
    println("add(6) при полной таблице: ${intSet.add(6)}")

    println("\n--- Тестирование с пользовательским классом ---")
    data class Person(val name: String, val age: Int)

    val personSet = OpenHashSet<Person>(5)
    val person1 = Person("Alice", 30)
    val person2 = Person("Bob", 25)
    val person3 = Person("Alice", 30)

    println("add(person1): ${personSet.add(person1)}")
    println("add(person2): ${personSet.add(person2)}")
    println("add(person3): ${personSet.add(person3)}")
    println("personSet: $personSet")
    println("contains(person1): ${personSet.contains(person1)}")
    println("contains(person3): ${personSet.contains(person3)}")
}