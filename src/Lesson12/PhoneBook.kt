/**
 * Класс "Телефонная книга".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 14.
 * Объект класса хранит список людей и номеров их телефонов,
 * при чём у каждого человека может быть более одного номера телефона.
 * Человек задаётся строкой вида "Фамилия Имя".
 * Телефон задаётся строкой из цифр, +, *, #, -.
 * Поддерживаемые методы: добавление / удаление человека,
 * добавление / удаление телефона для заданного человека,
 * поиск номера(ов) телефона по заданному имени человека,
 * поиск человека по заданному номеру телефона.
 *
 * Класс должен иметь конструктор по умолчанию (без параметров).
 */
class PhoneBook {

    // Основная структура данных: Map<Имя, Set<Телефоны>>
    private val phoneBook: MutableMap<String, MutableSet<String>> = mutableMapOf()

    // Обратное отображение для быстрого поиска человека по телефону: Map<Телефон, Имя>
    private val reversePhoneBook: MutableMap<String, String> = mutableMapOf()

    /**
     * Проверка валидности имени
     */
    private fun isValidName(name: String): Boolean {
        // Имя должно содержать хотя бы одну букву и не быть пустым
        return name.isNotBlank() && name.any { it.isLetter() }
    }

    /**
     * Проверка валидности телефона
     */
    private fun isValidPhone(phone: String): Boolean {
        // Телефон должен содержать только разрешенные символы: цифры, +, *, #, -
        return phone.isNotBlank() && phone.all {
            it.isDigit() || it == '+' || it == '*' || it == '#' || it == '-'
        }
    }

    /**
     * Добавить человека.
     * Возвращает true, если человек был успешно добавлен,
     * и false, если человек с таким именем уже был в телефонной книге
     * (во втором случае телефонная книга не должна меняться).
     */
    fun addHuman(name: String): Boolean {
        if (!isValidName(name)) {
            return false
        }

        return if (name in phoneBook) {
            false
        } else {
            phoneBook[name] = mutableSetOf()
            true
        }
    }

    /**
     * Убрать человека.
     * Возвращает true, если человек был успешно удалён,
     * и false, если человек с таким именем отсутствовал в телефонной книге
     * (во втором случае телефонная книга не должна меняться).
     */
    fun removeHuman(name: String): Boolean {
        if (!isValidName(name)) {
            return false
        }

        val phones = phoneBook[name] ?: return false

        // Удаляем все телефоны этого человека из обратного отображения
        for (phone in phones) {
            reversePhoneBook.remove(phone)
        }

        // Удаляем человека из основной книги
        phoneBook.remove(name)

        return true
    }

    /**
     * Добавить номер телефона.
     * Возвращает true, если номер был успешно добавлен,
     * и false, если человек с таким именем отсутствовал в телефонной книге,
     * либо у него уже был такой номер телефона,
     * либо такой номер телефона зарегистрирован за другим человеком.
     */
    fun addPhone(name: String, phone: String): Boolean {
        if (!isValidName(name) || !isValidPhone(phone)) {
            return false
        }

        // Проверяем, существует ли человек
        val phones = phoneBook[name] ?: return false

        // Проверяем, не зарегистрирован ли телефон за другим человеком
        val existingOwner = reversePhoneBook[phone]
        if (existingOwner != null && existingOwner != name) {
            return false // Телефон уже зарегистрирован за другим человеком
        }

        // Проверяем, нет ли уже такого телефона у этого человека
        if (phone in phones) {
            return false
        }

        // Добавляем телефон
        phones.add(phone)
        reversePhoneBook[phone] = name

        return true
    }

    /**
     * Убрать номер телефона.
     * Возвращает true, если номер был успешно удалён,
     * и false, если человек с таким именем отсутствовал в телефонной книге
     * либо у него не было такого номера телефона.
     */
    fun removePhone(name: String, phone: String): Boolean {
        if (!isValidName(name) || !isValidPhone(phone)) {
            return false
        }

        // Проверяем, существует ли человек
        val phones = phoneBook[name] ?: return false

        // Проверяем, есть ли такой телефон у человека
        if (!phones.contains(phone)) {
            return false
        }

        // Удаляем телефон
        phones.remove(phone)
        reversePhoneBook.remove(phone)

        // Если у человека не осталось телефонов, можно оставить его в книге

        return true
    }

    /**
     * Вернуть все номера телефона заданного человека.
     * Если этого человека нет в книге, вернуть пустой список
     */
    fun phones(name: String): Set<String> {
        if (!isValidName(name)) {
            return emptySet()
        }

        return phoneBook[name]?.toSet() ?: emptySet()
    }

    /**
     * Вернуть имя человека по заданному номеру телефона.
     * Если такого номера нет в книге, вернуть null.
     */
    fun humanByPhone(phone: String): String? {
        if (!isValidPhone(phone)) {
            return null
        }

        return reversePhoneBook[phone]
    }

    /**
     * Получить всех людей в телефонной книге
     */
    fun allHumans(): Set<String> {
        return phoneBook.keys.toSet()
    }

    /**
     * Получить все телефоны в телефонной книге
     */
    fun allPhones(): Set<String> {
        return reversePhoneBook.keys.toSet()
    }

    /**
     * Очистить телефонную книгу
     */
    fun clear() {
        phoneBook.clear()
        reversePhoneBook.clear()
    }

    /**
     * Получить количество людей в книге
     */
    fun humansCount(): Int = phoneBook.size

    /**
     * Получить общее количество телефонов в книге
     */
    fun phonesCount(): Int = reversePhoneBook.size

    /**
     * Две телефонные книги равны, если в них хранится одинаковый набор людей,
     * и каждому человеку соответствует одинаковый набор телефонов.
     * Порядок людей / порядок телефонов в книге не должен иметь значения.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneBook) return false

        // Проверяем, что набор людей одинаков
        if (this.phoneBook.keys != other.phoneBook.keys) return false

        // Проверяем, что у каждого человека одинаковый набор телефонов
        for ((name, phones) in this.phoneBook) {
            val otherPhones = other.phoneBook[name]
            if (otherPhones == null || phones != otherPhones) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = 0
        // Хэш-код должен учитывать всех людей и их телефоны
        for ((name, phones) in phoneBook.entries.sortedBy { it.key }) {
            result = 31 * result + name.hashCode()
            for (phone in phones.sorted()) {
                result = 31 * result + phone.hashCode()
            }
        }
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("PhoneBook(\n")
        for ((name, phones) in phoneBook.entries.sortedBy { it.key }) {
            sb.append("  $name: ${phones.sorted().joinToString()}\n")
        }
        sb.append(")")
        return sb.toString()
    }
}

fun main() {
    val phoneBook = PhoneBook()

    println("=== Тестирование PhoneBook ===")

    // Добавление людей
    println("1. Добавление людей:")
    println("addHuman(\"Иванов Иван\"): ${phoneBook.addHuman("Иванов Иван")}")
    println("addHuman(\"Петров Петр\"): ${phoneBook.addHuman("Петров Петр")}")
    println("addHuman(\"Иванов Иван\"): ${phoneBook.addHuman("Иванов Иван")}")
    println("addHuman(\"\"): ${phoneBook.addHuman("")}")
    println("Текущая книга: $phoneBook")

    // Добавление телефонов
    println("\n2. Добавление телефонов:")
    println("addPhone(\"Иванов Иван\", \"+79161234567\"): ${phoneBook.addPhone("Иванов Иван", "+79161234567")}")
    println("addPhone(\"Иванов Иван\", \"+79161234567\"): ${phoneBook.addPhone("Иванов Иван", "+79161234567")}")
    println("addPhone(\"Иванов Иван\", \"8-916-123-45-67\"): ${phoneBook.addPhone("Иванов Иван", "8-916-123-45-67")}")
    println("addPhone(\"Петров Петр\", \"+79161234567\"): ${phoneBook.addPhone("Петров Петр", "+79161234567")}")
    println("addPhone(\"Петров Петр\", \"+79169876543\"): ${phoneBook.addPhone("Петров Петр", "+79169876543")}")
    println("addPhone(\"Сидоров Сидор\", \"+79160000000\"): ${phoneBook.addPhone("Сидоров Сидор", "+79160000000")}")
    println("addPhone(\"Иванов Иван\", \"abc\"): ${phoneBook.addPhone("Иванов Иван", "abc")}")
    println("Текущая книга: $phoneBook")

    // Получение телефонов человека
    println("\n3. Получение телефонов:")
    println("phones(\"Иванов Иван\"): ${phoneBook.phones("Иванов Иван")}")
    println("phones(\"Петров Петр\"): ${phoneBook.phones("Петров Петр")}")
    println("phones(\"Сидоров Сидор\"): ${phoneBook.phones("Сидоров Сидор")}")

    // Поиск человека по телефону
    println("\n4. Поиск человека по телефону:")
    println("humanByPhone(\"+79161234567\"): ${phoneBook.humanByPhone("+79161234567")}")
    println("humanByPhone(\"8-916-123-45-67\"): ${phoneBook.humanByPhone("8-916-123-45-67")}")
    println("humanByPhone(\"+79169876543\"): ${phoneBook.humanByPhone("+79169876543")}")
    println("humanByPhone(\"+79160000000\"): ${phoneBook.humanByPhone("+79160000000")}")

    // Удаление телефонов
    println("\n5. Удаление телефонов:")
    println("removePhone(\"Иванов Иван\", \"+79161234567\"): ${phoneBook.removePhone("Иванов Иван", "+79161234567")}")
    println("removePhone(\"Иванов Иван\", \"+79161234567\"): ${phoneBook.removePhone("Иванов Иван", "+79161234567")}")
    println("После удаления: $phoneBook")
    println("humanByPhone(\"+79161234567\"): ${phoneBook.humanByPhone("+79161234567")}")

    // Удаление людей
    println("\n6. Удаление людей:")
    println("removeHuman(\"Петров Петр\"): ${phoneBook.removeHuman("Петров Петр")}")
    println("removeHuman(\"Петров Петр\"): ${phoneBook.removeHuman("Петров Петр")}")
    println("После удаления: $phoneBook")
    println("humanByPhone(\"+79169876543\"): ${phoneBook.humanByPhone("+79169876543")}")

    // Проверка равенства
    println("\n7. Проверка равенства:")
    val phoneBook2 = PhoneBook()
    phoneBook2.addHuman("Иванов Иван")
    phoneBook2.addPhone("Иванов Иван", "8-916-123-45-67")

    val phoneBook3 = PhoneBook()
    phoneBook3.addHuman("Иванов Иван")
    phoneBook3.addPhone("Иванов Иван", "8-916-123-45-67")

    println("phoneBook == phoneBook2: ${phoneBook == phoneBook2}")
    println("phoneBook2 == phoneBook3: ${phoneBook2 == phoneBook3}")

    val phoneBook4 = PhoneBook()
    phoneBook4.addHuman("Иванов Иван")
    phoneBook4.addPhone("Иванов Иван", "+79161234567")

    println("phoneBook == phoneBook4: ${phoneBook == phoneBook4}")

    // Дополнительные методы
    println("\n8. Дополнительные методы:")
    println("allHumans(): ${phoneBook.allHumans()}")
    println("allPhones(): ${phoneBook.allPhones()}")
    println("humansCount(): ${phoneBook.humansCount()}")
    println("phonesCount(): ${phoneBook.phonesCount()}")
    println("isEmpty: ${phoneBook.humansCount() == 0}")

    // Очистка
    println("\n9. Очистка:")
    phoneBook.clear()
    println("После clear: $phoneBook")
    println("humansCount(): ${phoneBook.humansCount()}")
    println("phonesCount(): ${phoneBook.phonesCount()}")

    // Тестирование с несколькими телефонами у одного человека
    println("\n10. Несколько телефонов у одного человека:")
    val multiPhoneBook = PhoneBook()
    multiPhoneBook.addHuman("Смирнов Алексей")
    multiPhoneBook.addPhone("Смирнов Алексей", "+79161112233")
    multiPhoneBook.addPhone("Смирнов Алексей", "+79162223344")
    multiPhoneBook.addPhone("Смирнов Алексей", "+79163334455")
    println("multiPhoneBook: $multiPhoneBook")
    println("phones(\"Смирнов Алексей\"): ${multiPhoneBook.phones("Смирнов Алексей")}")
}