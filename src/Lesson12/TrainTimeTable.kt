/**
 * Время (часы, минуты)
 */
data class Time(val hour: Int, val minute: Int) : Comparable<Time> {

    init {
        require(hour in 0..23) { "Hour must be between 0 and 23" }
        require(minute in 0..59) { "Minute must be between 0 and 59" }
    }

    /**
     * Преобразование времени в минуты с начала суток
     */
    private val totalMinutes: Int = hour * 60 + minute

    /**
     * Сравнение времён на больше/меньше
     */
    override fun compareTo(other: Time): Int {
        return this.totalMinutes.compareTo(other.totalMinutes)
    }

    operator fun plus(minutes: Int): Time {
        val total = this.totalMinutes + minutes
        val newHour = (total / 60) % 24
        val newMinute = total % 60
        return Time(newHour, newMinute)
    }

    operator fun minus(other: Time): Int {
        return this.totalMinutes - other.totalMinutes
    }

    operator fun minus(minutes: Int): Time {
        return this + (-minutes)
    }

    fun isBefore(other: Time): Boolean = this < other
    fun isAfter(other: Time): Boolean = this > other
    fun isSame(other: Time): Boolean = this == other

    override fun toString(): String = String.format("%02d:%02d", hour, minute)
}

/**
 * Остановка (название, время прибытия)
 */
data class Stop(val name: String, val time: Time)

/**
 * Поезд (имя, список остановок, упорядоченный по времени).
 * Первой идёт начальная остановка, последней конечная.
 */
data class Train(val name: String, val stops: List<Stop>) {
    constructor(name: String, vararg stops: Stop) : this(name, stops.asList())

    init {
        require(stops.isNotEmpty()) { "Train must have at least one stop" }
        // Проверяем, что остановки упорядочены по времени
        for (i in 0 until stops.size - 1) {
            require(stops[i].time <= stops[i + 1].time) {
                "Stops must be ordered by time. Stop ${i+1} (${stops[i]}) is after stop ${i+2} (${stops[i+1]})"
            }
        }
    }

    val firstStop: Stop get() = stops.first()
    val lastStop: Stop get() = stops.last()

    fun hasStop(stopName: String): Boolean = stops.any { it.name == stopName }

    fun getStop(stopName: String): Stop? = stops.find { it.name == stopName }

    fun getStopTime(stopName: String): Time? = getStop(stopName)?.time

    fun isIntermediateStop(stopName: String): Boolean {
        if (!hasStop(stopName)) return false
        val index = stops.indexOfFirst { it.name == stopName }
        return index > 0 && index < stops.size - 1
    }

    fun withStopAdded(stop: Stop): Train {
        // Вставляем остановку в правильную позицию по времени
        val newStops = stops.toMutableList()
        val insertIndex = newStops.indexOfFirst { it.time > stop.time }

        if (insertIndex == -1) {
            newStops.add(stop)
        } else {
            newStops.add(insertIndex, stop)
        }

        return Train(name, newStops)
    }

    fun withStopRemoved(stopName: String): Train {
        require(isIntermediateStop(stopName)) { "Can only remove intermediate stops" }
        val newStops = stops.filterNot { it.name == stopName }
        return Train(name, newStops)
    }

    fun withStopUpdated(stop: Stop): Train {
        val index = stops.indexOfFirst { it.name == stop.name }
        require(index != -1) { "Stop ${stop.name} not found in train $name" }

        val newStops = stops.toMutableList()
        newStops[index] = stop

        // Пересортируем, если время изменилось
        val sortedStops = newStops.sortedBy { it.time }
        return Train(name, sortedStops)
    }
}

/**
 * Класс "расписание поездов".
 *
 * Общая сложность задания -- средняя, общая ценность в баллах -- 16.
 * Объект класса хранит расписание поездов для определённой станции отправления.
 * Для каждого поезда хранится конечная станция и список промежуточных.
 * Поддерживаемые методы:
 * добавить новый поезд, удалить поезд,
 * добавить / удалить промежуточную станцию существующему поезду,
 * поиск поездов по времени.
 *
 * В конструктор передаётся название станции отправления для данного расписания.
 */
class TrainTimeTable(val baseStationName: String) {

    private val trains: MutableMap<String, Train> = mutableMapOf()

    /**
     * Проверка валидности времени для добавления остановки
     */
    private fun validateStopTime(train: Train, newStop: Stop): Boolean {
        val baseTime = train.firstStop.time
        val destinationTime = train.lastStop.time

        // Время должно быть между временем отправления и прибытия
        if (newStop.time < baseTime || newStop.time > destinationTime) {
            return false
        }

        // Время не должно совпадать с другими остановками
        return train.stops.none { it.time == newStop.time && it.name != newStop.name }
    }

    /**
     * Добавить новый поезд.
     *
     * Если поезд с таким именем уже есть, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @param depart время отправления с baseStationName
     * @param destination конечная станция
     * @return true, если поезд успешно добавлен, false, если такой поезд уже есть
     */
    fun addTrain(train: String, depart: Time, destination: Stop): Boolean {
        if (train in trains) return false

        // Создаем остановки: начальная и конечная
        val baseStop = Stop(baseStationName, depart)
        val stops = listOf(baseStop, destination)

        // Проверяем, что время прибытия на конечную станцию не раньше времени отправления
        require(destination.time >= depart) {
            "Arrival time at destination must be after departure time"
        }

        trains[train] = Train(train, stops)
        return true
    }

    /**
     * Удалить существующий поезд.
     *
     * Если поезда с таким именем нет, следует вернуть false и ничего не изменять в таблице
     *
     * @param train название поезда
     * @return true, если поезд успешно удалён, false, если такой поезд не существует
     */
    fun removeTrain(train: String): Boolean {
        return trains.remove(train) != null
    }

    /**
     * Добавить/изменить начальную, промежуточную или конечную остановку поезду.
     *
     * Если у поезда ещё нет остановки с названием stop, добавить её и вернуть true.
     * Если stop.name совпадает с baseStationName, изменить время отправления с этой станции и вернуть false.
     * Если stop совпадает с destination данного поезда, изменить время прибытия на неё и вернуть false.
     * Если stop совпадает с одной из промежуточных остановок, изменить время прибытия на неё и вернуть false.
     *
     * Функция должна сохранять инвариант: время прибытия на любую из промежуточных станций
     * должно находиться в интервале между временем отправления с baseStation и временем прибытия в destination,
     * иначе следует бросить исключение IllegalArgumentException.
     * Также, время прибытия на любую из промежуточных станций не должно совпадать с временем прибытия на другую
     * станцию или с временем отправления с baseStation, иначе бросить то же исключение.
     *
     * @param train название поезда
     * @param stop начальная, промежуточная или конечная станция
     * @return true, если поезду была добавлена новая остановка, false, если было изменено время остановки на старой
     */
    fun addStop(trainName: String, stop: Stop): Boolean {
        val train = trains[trainName] ?: throw IllegalArgumentException("Train $trainName not found")

        return when {
            // Если это базовая станция
            stop.name == baseStationName -> {
                val newTrain = train.withStopUpdated(stop)
                // Проверяем, что новое время отправления не позже времени первой промежуточной остановки
                if (newTrain.stops.size > 1 && newTrain.stops[0].time > newTrain.stops[1].time) {
                    throw IllegalArgumentException("Departure time cannot be after first stop time")
                }
                trains[trainName] = newTrain
                false
            }

            // Если это конечная станция
            stop.name == train.lastStop.name -> {
                val newTrain = train.withStopUpdated(stop)
                // Проверяем, что новое время прибытия не раньше времени последней промежуточной остановки
                if (newTrain.stops.size > 1 && newTrain.stops.last().time < newTrain.stops[newTrain.stops.size - 2].time) {
                    throw IllegalArgumentException("Arrival time cannot be before last intermediate stop")
                }
                trains[trainName] = newTrain
                false
            }

            // Если это существующая промежуточная станция
            train.hasStop(stop.name) -> {
                if (!validateStopTime(train, stop)) {
                    throw IllegalArgumentException("Invalid stop time: must be between departure and arrival times and not overlap with other stops")
                }
                val newTrain = train.withStopUpdated(stop)
                trains[trainName] = newTrain
                false
            }

            // Новая промежуточная станция
            else -> {
                if (!validateStopTime(train, stop)) {
                    throw IllegalArgumentException("Invalid stop time: must be between departure and arrival times and not overlap with other stops")
                }
                val newTrain = train.withStopAdded(stop)
                trains[trainName] = newTrain
                true
            }
        }
    }

    /**
     * Удалить одну из промежуточных остановок.
     *
     * Если stopName совпадает с именем одной из промежуточных остановок, удалить её и вернуть true.
     * Если у поезда нет такой остановки, или stopName совпадает с начальной или конечной остановкой, вернуть false.
     *
     * @param train название поезда
     * @param stopName название промежуточной остановки
     * @return true, если удаление успешно
     */
    fun removeStop(trainName: String, stopName: String): Boolean {
        val train = trains[trainName] ?: return false

        if (stopName == baseStationName || stopName == train.lastStop.name) {
            return false
        }

        // Проверяем, что это промежуточная остановка
        if (!train.isIntermediateStop(stopName)) {
            return false
        }

        val newTrain = train.withStopRemoved(stopName)
        trains[trainName] = newTrain
        return true
    }

    /**
     * Вернуть список всех поездов, упорядоченный по времени отправления с baseStationName
     */
    fun trains(): List<Train> {
        return trains.values.sortedBy { it.firstStop.time }
    }

    /**
     * Вернуть список всех поездов, отправляющихся не ранее currentTime
     * и имеющих остановку (начальную, промежуточную или конечную) на станции destinationName.
     * Список должен быть упорядочен по времени прибытия на станцию destinationName
     */
    fun trains(currentTime: Time, destinationName: String): List<Train> {
        return trains.values
            .filter { train ->
                // Поезд отправляется не ранее currentTime
                train.firstStop.time >= currentTime &&
                        // И имеет остановку на станции destinationName
                        train.hasStop(destinationName)
            }
            .sortedBy { train ->
                // Сортируем по времени прибытия на destinationName
                train.getStopTime(destinationName)
            }
    }

    /**
     * Получить поезд по имени
     */
    fun getTrain(trainName: String): Train? = trains[trainName]

    /**
     * Получить все станции, на которых останавливается поезд
     */
    fun getTrainStops(trainName: String): List<Stop>? = trains[trainName]?.stops

    /**
     * Очистить расписание
     */
    fun clear() {
        trains.clear()
    }

    /**
     * Получить количество поездов
     */
    fun size(): Int = trains.size

    /**
     * Проверить, пусто ли расписание
     */
    fun isEmpty(): Boolean = trains.isEmpty()

    /**
     * Сравнение на равенство.
     * Расписания считаются одинаковыми, если содержат одинаковый набор поездов,
     * и поезда с тем же именем останавливаются на одинаковых станциях в одинаковое время.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TrainTimeTable) return false
        if (baseStationName != other.baseStationName) return false

        // Проверяем, что набор поездов одинаков
        if (trains.keys != other.trains.keys) return false

        // Проверяем, что поезда с одинаковыми именами имеют одинаковые остановки
        for ((name, train) in trains) {
            val otherTrain = other.trains[name]
            if (otherTrain == null || train != otherTrain) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = baseStationName.hashCode()
        for ((name, train) in trains.entries.sortedBy { it.key }) {
            result = 31 * result + name.hashCode()
            result = 31 * result + train.hashCode()
        }
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("TrainTimeTable(baseStation='$baseStationName', trains=[\n")
        for (train in trains().sortedBy { it.firstStop.time }) {
            sb.append("  ${train.name}: ")
            sb.append(train.stops.joinToString(" -> ") { "${it.name} (${it.time})" })
            sb.append("\n")
        }
        sb.append("])")
        return sb.toString()
    }
}

fun main() {
    println("=== Тестирование TrainTimeTable ===")

    // Создаем расписание для станции Москва
    val timetable = TrainTimeTable("Москва")

    // Добавляем поезда
    println("1. Добавление поездов:")
    println("addTrain('Сапсан-1', Time(8, 0), Stop('Санкт-Петербург', Time(11, 30))): " +
            timetable.addTrain("Сапсан-1", Time(8, 0), Stop("Санкт-Петербург", Time(11, 30))))
    println("addTrain('Сапсан-2', Time(10, 0), Stop('Санкт-Петербург', Time(13, 30))): " +
            timetable.addTrain("Сапсан-2", Time(10, 0), Stop("Санкт-Петербург", Time(13, 30))))
    println("addTrain('Ласточка', Time(9, 30), Stop('Тверь', Time(10, 45))): " +
            timetable.addTrain("Ласточка", Time(9, 30), Stop("Тверь", Time(10, 45))))
    println("addTrain('Сапсан-1', Time(12, 0), Stop('Нижний Новгород', Time(15, 0))): " +
            timetable.addTrain("Сапсан-1", Time(12, 0), Stop("Нижний Новгород", Time(15, 0))))

    println("\nТекущее расписание:")
    println(timetable)

    // Добавляем промежуточные остановки
    println("\n2. Добавление промежуточных остановок:")

    // Добавляем остановку Вышний Волочек к Сапсану-1
    println("addStop('Сапсан-1', Stop('Вышний Волочек', Time(9, 45))): " +
            timetable.addStop("Сапсан-1", Stop("Вышний Волочек", Time(9, 45))))

    // Изменяем время отправления из Москвы
    println("addStop('Сапсан-2', Stop('Москва', Time(10, 30))): " +
            timetable.addStop("Сапсан-2", Stop("Москва", Time(10, 30))))

    // Изменяем время прибытия в Санкт-Петербург
    println("addStop('Сапсан-2', Stop('Санкт-Петербург', Time(14, 0))): " +
            timetable.addStop("Сапсан-2", Stop("Санкт-Петербург", Time(14, 0))))

    // Добавляем еще одну промежуточную остановку
    println("addStop('Сапсан-1', Stop('Бологое', Time(10, 30))): " +
            timetable.addStop("Сапсан-1", Stop("Бологое", Time(10, 30))))

    println("\nТекущее расписание:")
    println(timetable)

    // Удаление остановок
    println("\n3. Удаление промежуточных остановок:")
    println("removeStop('Сапсан-1', 'Вышний Волочек'): " +
            timetable.removeStop("Сапсан-1", "Вышний Волочек"))
    println("removeStop('Сапсан-1', 'Москва'): " + // Нельзя удалить начальную
            timetable.removeStop("Сапсан-1", "Москва"))
    println("removeStop('Сапсан-1', 'Санкт-Петербург'): " + // Нельзя удалить конечную
            timetable.removeStop("Сапсан-1", "Санкт-Петербург"))

    // Получение списка поездов
    println("\n4. Получение списка поездов:")
    val allTrains = timetable.trains()
    println("Все поезда (отсортированы по времени отправления):")
    allTrains.forEach { train ->
        println("  ${train.name}: ${train.stops.joinToString(" -> ") { "${it.name} (${it.time})" }}")
    }

    // Поиск поездов по времени и станции назначения
    println("\n5. Поиск поездов:")
    val trainsToSPb = timetable.trains(Time(9, 0), "Санкт-Петербург")
    println("Поезда в Санкт-Петербург после 09:00:")
    trainsToSPb.forEach { train ->
        val arrivalTime = train.getStopTime("Санкт-Петербург")
        println("  ${train.name}: отправление ${train.firstStop.time}, прибытие $arrivalTime")
    }

    val trainsToTver = timetable.trains(Time(8, 0), "Тверь")
    println("\nПоезда в Тверь после 08:00:")
    trainsToTver.forEach { train ->
        val arrivalTime = train.getStopTime("Тверь")
        println("  ${train.name}: отправление ${train.firstStop.time}, прибытие $arrivalTime")
    }

    // Тестирование ошибок
    println("\n6. Тестирование ошибок:")

    // Попытка добавить остановку с неверным временем
    try {
        println("Попытка добавить остановку раньше времени отправления:")
        timetable.addStop("Сапсан-1", Stop("Новая остановка", Time(7, 0)))
    } catch (e: IllegalArgumentException) {
        println("  Ошибка: ${e.message}")
    }

    try {
        println("Попытка добавить остановку позже времени прибытия:")
        timetable.addStop("Сапсан-1", Stop("Новая остановка", Time(12, 0)))
    } catch (e: IllegalArgumentException) {
        println("  Ошибка: ${e.message}")
    }

    // Удаление поезда
    println("\n7. Удаление поезда:")
    println("removeTrain('Ласточка'): ${timetable.removeTrain("Ласточка")}")
    println("removeTrain('Несуществующий'): ${timetable.removeTrain("Несуществующий")}")

    println("\nТекущее расписание после удаления:")
    println(timetable)

    // Тестирование равенства
    println("\n8. Тестирование равенства расписаний:")
    val timetable2 = TrainTimeTable("Москва")
    timetable2.addTrain("Сапсан-1", Time(8, 0), Stop("Санкт-Петербург", Time(11, 30)))
    timetable2.addStop("Сапсан-1", Stop("Бологое", Time(10, 30)))
    timetable2.addTrain("Сапсан-2", Time(10, 30), Stop("Санкт-Петербург", Time(14, 0)))

    val timetable3 = TrainTimeTable("Москва")
    timetable3.addTrain("Сапсан-2", Time(10, 30), Stop("Санкт-Петербург", Time(14, 0)))
    timetable3.addTrain("Сапсан-1", Time(8, 0), Stop("Санкт-Петербург", Time(11, 30)))
    timetable3.addStop("Сапсан-1", Stop("Бологое", Time(10, 30)))

    println("timetable == timetable2: ${timetable == timetable2}")
    println("timetable == timetable3: ${timetable == timetable3}")

    // Дополнительные методы
    println("\n9. Дополнительные методы:")
    println("Количество поездов: ${timetable.size()}")
    println("Пустое ли расписание: ${timetable.isEmpty()}")

    val train = timetable.getTrain("Сапсан-1")
    println("Информация о поезде Сапсан-1: ${train?.stops}")

    // Очистка
    println("\n10. Очистка расписания:")
    timetable.clear()
    println("После clear: ${timetable}")
    println("Пустое ли расписание: ${timetable.isEmpty()}")
}