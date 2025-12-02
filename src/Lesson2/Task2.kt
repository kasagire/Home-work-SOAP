/**
 * Простая (2 балла)
 *
 * Четырехзначное число назовем счастливым, если сумма первых двух ее цифр равна сумме двух последних.
 * Определить, счастливое ли заданное число, вернуть true, если это так.
 */
fun isNumberHappy(number: Int): Boolean {
    val first = number / 1000
    val second = (number / 100) % 10
    val third = (number / 10) % 10
    val fourth = number % 10

    return (first + second) == (third + fourth)
}

/**
 * Простая (2 балла)
 *
 * На шахматной доске стоят два ферзя (ферзь бьет по вертикали, горизонтали и диагоналям).
 * Определить, угрожают ли они друг другу. Вернуть true, если угрожают.
 * Считать, что ферзи не могут загораживать друг друга.
 */
fun queenThreatens(x1: Int, y1: Int, x2: Int, y2: Int): Boolean {
    // Проверяем совпадение координат
    val sameRowOrColumn = (x1 == x2) || (y1 == y2)

    // Проверяем диагональ
    val sameDiagonal = Math.abs(x1 - x2) == Math.abs(y1 - y2)

    return sameRowOrColumn || sameDiagonal
}

/**
 * Простая (2 балла)
 *
 * Дан номер месяца (от 1 до 12 включительно) и год (положительный).
 * Вернуть число дней в этом месяце этого года по григорианскому календарю.
 */
fun daysInMonth(month: Int, year: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear(year)) 29 else 28
        else -> throw IllegalArgumentException("Номер месяца должен быть от 1 до 12")
    }
}

/**
 * Вспомогательная функция для определения високосного года
 */
fun isLeapYear(year: Int): Boolean {
    return (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)
}

/**
 * Простая (2 балла)
 *
 * Проверить, лежит ли окружность с центром в (x1, y1) и радиусом r1 целиком внутри
 * окружности с центром в (x2, y2) и радиусом r2.
 * Вернуть true, если утверждение верно
 */
fun circleInside(
    x1: Double, y1: Double, r1: Double,
    x2: Double, y2: Double, r2: Double
): Boolean {
    // Расстояние между центрами окружностей
    val distance = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0))

    // Окружность лежит внутри, если расстояние между центрами + r1 <= r2
    return distance + r1 <= r2
}

/**
 * Средняя (3 балла)
 *
 * Определить, пройдет ли кирпич со сторонами а, b, c сквозь прямоугольное отверстие в стене со сторонами r и s.
 * Стороны отверстия должны быть параллельны граням кирпича.
 * Считать, что совпадения длин сторон достаточно для прохождения кирпича, т.е., например,
 * кирпич 4 х 4 х 4 пройдёт через отверстие 4 х 4.
 * Вернуть true, если кирпич пройдёт
 */
fun brickPasses(a: Int, b: Int, c: Int, r: Int, s: Int): Boolean {
    // Сортируем стороны отверстия для удобства
    val holeSide1 = minOf(r, s)
    val holeSide2 = maxOf(r, s)

    // Проверяем все возможные ориентации кирпича
    // Кирпич пройдет, если две наименьшие стороны кирпича помещаются в отверстие
    val brickSides = listOf(a, b, c).sorted()

    return (brickSides[0] <= holeSide1 && brickSides[1] <= holeSide2)
}

/**
 * Тестирующая функция для проверки всех решений
 */
fun main() {
    println("Тестирование всех функций:\n")

    // Тест 1: isNumberHappy
    println("1. isNumberHappy:")
    println("   isNumberHappy(1234) = ${isNumberHappy(1234)} ")
    println("   isNumberHappy(1230) = ${isNumberHappy(1230)} ")
    println("   isNumberHappy(1323) = ${isNumberHappy(1323)} ")
    println("   isNumberHappy(2213) = ${isNumberHappy(2213)} ")
    println("   isNumberHappy(9999) = ${isNumberHappy(9999)} ")

    // Тест 2: queenThreatens
    println("\n2. queenThreatens:")
    println("   queenThreatens(1, 1, 1, 5) = ${queenThreatens(1, 1, 1, 5)} ")
    println("   queenThreatens(1, 1, 5, 1) = ${queenThreatens(1, 1, 5, 1)} ")
    println("   queenThreatens(1, 1, 3, 3) = ${queenThreatens(1, 1, 3, 3)} ")
    println("   queenThreatens(1, 1, 3, 2) = ${queenThreatens(1, 1, 3, 2)} ")
    println("   queenThreatens(4, 4, 2, 2) = ${queenThreatens(4, 4, 2, 2)} ")

    // Тест 3: daysInMonth
    println("\n3. daysInMonth:")
    println("   daysInMonth(1, 2023) = ${daysInMonth(1, 2023)} ")
    println("   daysInMonth(2, 2023) = ${daysInMonth(2, 2023)} ")
    println("   daysInMonth(2, 2024) = ${daysInMonth(2, 2024)} )")
    println("   daysInMonth(2, 2000) = ${daysInMonth(2, 2000)} ")
    println("   daysInMonth(2, 2100) = ${daysInMonth(2, 2100)} ")
    println("   daysInMonth(4, 2023) = ${daysInMonth(4, 2023)} ")
    println("   daysInMonth(12, 2023) = ${daysInMonth(12, 2023)} ")

    // Тест 4: circleInside
    println("\n4. circleInside:")
    println("   circleInside(0.0, 0.0, 1.0, 0.0, 0.0, 2.0) = ${circleInside(0.0, 0.0, 1.0, 0.0, 0.0, 2.0)} ")
    println("   circleInside(0.0, 0.0, 3.0, 0.0, 0.0, 2.0) = ${circleInside(0.0, 0.0, 3.0, 0.0, 0.0, 2.0)} ")
    println("   circleInside(1.0, 1.0, 1.0, 0.0, 0.0, 3.0) = ${circleInside(1.0, 1.0, 1.0, 0.0, 0.0, 3.0)} ")
    println("   circleInside(5.0, 5.0, 2.0, 0.0, 0.0, 8.0) = ${circleInside(5.0, 5.0, 2.0, 0.0, 0.0, 8.0)} ")
    println("   circleInside(5.0, 5.0, 4.0, 0.0, 0.0, 8.0) = ${circleInside(5.0, 5.0, 4.0, 0.0, 0.0, 8.0)} ")

    // Тест 5: brickPasses
    println("\n5. brickPasses:")
    println("   brickPasses(3, 4, 5, 3, 4) = ${brickPasses(3, 4, 5, 3, 4)} ")
    println("   brickPasses(3, 4, 5, 2, 4) = ${brickPasses(3, 4, 5, 2, 4)} ")
    println("   brickPasses(4, 4, 4, 4, 4) = ${brickPasses(4, 4, 4, 4, 4)} ")
    println("   brickPasses(1, 2, 10, 1, 2) = ${brickPasses(1, 2, 10, 1, 2)} ")
    println("   brickPasses(1, 2, 10, 1, 1) = ${brickPasses(1, 2, 10, 1, 1)} ")
    println("   brickPasses(8, 2, 3, 2, 3) = ${brickPasses(8, 2, 3, 2, 3)} ")
    println("   brickPasses(8, 2, 3, 2, 2) = ${brickPasses(8, 2, 3, 2, 2)} ")

    println("\n=== Все тесты завершены ===")
}