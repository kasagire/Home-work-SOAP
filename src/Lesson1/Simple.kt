/**
 * Тривиальная (3 балла).
 *
 * Пользователь задает время в часах, минутах и секундах, например, 8:20:35.
 * Рассчитать время в секундах, прошедшее с начала суток (30035 в данном случае).
 */
fun seconds(hours: Int, minutes: Int, seconds: Int): Int {
    return hours * 3600 + minutes * 60 + seconds
}

/**
 * Тривиальная (1 балл)
 *
 * Пользователь задает длину отрезка в саженях, аршинах и вершках (например, 8 саженей 2 аршина 11 вершков).
 * Определить длину того же отрезка в метрах (в данном случае 18.98).
 * 1 сажень = 3 аршина = 48 вершков, 1 вершок = 4.445 см.
 */
fun lengthInMeters(sagenes: Int, arshins: Int, vershoks: Int): Double {
    // Переводим всё в вершки
    val totalVershoks = sagenes * 48 + arshins * 16 + vershoks
    // 1 вершок = 4.445 см = 0.04445 м
    return totalVershoks * 0.04445
}

/**
 * Тривиальная (1 балл)
 *
 * Пользователь задает угол в градусах, минутах и секундах (например, 36 градусов 14 минут 35 секунд).
 * Вывести значение того же угла в радианах (например, 0.63256).
 */
fun angleInRadian(deg: Int, min: Int, sec: Int): Double {
    // Переводим всё в градусы с дробной частью
    val totalDegrees = deg + min / 60.0 + sec / 3600.0
    // 1 градус = π/180 радиан
    return totalDegrees * Math.PI / 180
}

/**
 * Тривиальная (1 балл)
 *
 * Найти длину отрезка, соединяющего точки на плоскости с координатами (x1, y1) и (x2, y2).
 * Например, расстояние между (3, 0) и (0, 4) равно 5
 */
fun trackLength(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0))
}

/**
 * Простая (2 балла)
 *
 * Пользователь задает целое число, больше или равно 100 (например, 3801).
 * Определить третью цифру справа в этом числе (в данном случае 8).
 */
fun thirdDigit(number: Int): Int {
    return (number / 100) % 10
}

/**
 * Простая (2 балла)
 *
 * Поезд вышел со станции отправления в h1 часов m1 минут (например в 9:25) и
 * прибыл на станцию назначения в h2 часов m2 минут того же дня (например в 13:01).
 * Определите время поезда в пути в минутах (в данном случае 216).
 */
fun travelMinutes(hoursDepart: Int, minutesDepart: Int, hoursArrive: Int, minutesArrive: Int): Int {
    val departureInMinutes = hoursDepart * 60 + minutesDepart
    val arrivalInMinutes = hoursArrive * 60 + minutesArrive
    return arrivalInMinutes - departureInMinutes
}

/**
 * Простая (2 балла)
 *
 * Человек положил в банк сумму в s рублей под p% годовых (проценты начисляются в конце года).
 * Сколько денег будет на счету через 3 года (с учётом сложных процентов)?
 * Например, 100 рублей под 10% годовых превратятся в 133.1 рубля
 */
fun accountInThreeYears(initial: Int, percent: Int): Double {
    val rate = 1 + percent / 100.0
    return initial * rate * rate * rate
}

/**
 * Простая (2 балла)
 *
 * Пользователь задает целое трехзначное число (например, 478).
 * Необходимо вывести число, полученное из заданного перестановкой цифр в обратном порядке (например, 874).
 */
fun numberRevert(number: Int): Int {
    val hundreds = number / 100
    val tens = (number / 10) % 10
    val units = number % 10
    return units * 100 + tens * 10 + hundreds
}

/**
 * Тестирующая функция для проверки всех решений
 */
fun main() {
    println("Тестирование всех функций:\n")

    // Тест 1: seconds
    println("1. seconds(8, 20, 35) = ${seconds(8, 20, 35)} ")

    // Тест 2: lengthInMeters
    println("2. lengthInMeters(8, 2, 11) = ${String.format("%.2f", lengthInMeters(8, 2, 11))} ")

    // Тест 3: angleInRadian
    println("3. angleInRadian(36, 14, 35) = ${String.format("%.5f", angleInRadian(36, 14, 35))} ")

    // Тест 4: trackLength
    println("4. trackLength(3.0, 0.0, 0.0, 4.0) = ${trackLength(3.0, 0.0, 0.0, 4.0)} ")

    // Тест 5: thirdDigit
    println("5. thirdDigit(3801) = ${thirdDigit(3801)} ")

    // Тест 6: travelMinutes
    println("6. travelMinutes(9, 25, 13, 1) = ${travelMinutes(9, 25, 13, 1)} ")

    // Тест 7: accountInThreeYears
    println("7. accountInThreeYears(100, 10) = ${String.format("%.1f", accountInThreeYears(100, 10))} ")

    // Тест 8: numberRevert
    println("8. numberRevert(478) = ${numberRevert(478)} ")

    println("\nДополнительные тесты:")

    // Дополнительные тесты
    println("seconds(0, 0, 0) = ${seconds(0, 0, 0)} ")
    println("seconds(23, 59, 59) = ${seconds(23, 59, 59)} ")
    println("thirdDigit(100) = ${thirdDigit(100)} ")
    println("thirdDigit(9999) = ${thirdDigit(9999)} ")
    println("travelMinutes(23, 59, 0, 0) = ${travelMinutes(23, 59, 0, 0)}")
    println("numberRevert(123) = ${numberRevert(123)} ")
    println("numberRevert(900) = ${numberRevert(900)}")
}