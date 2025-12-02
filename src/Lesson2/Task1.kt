/**
 * Простая (2 балла)
 *
 * Мой возраст. Для заданного 0 < n < 200, рассматриваемого как возраст человека,
 * вернуть строку вида: «21 год», «32 года», «12 лет».
 */
fun ageDescription(age: Int): String {
    val lastDigit = age % 10
    val lastTwoDigits = age % 100

    return when {
        lastTwoDigits in 11..14 -> "$age лет"
        lastDigit == 1 -> "$age год"
        lastDigit in 2..4 -> "$age года"
        else -> "$age лет"
    }
}

/**
 * Простая (2 балла)
 *
 * Путник двигался t1 часов со скоростью v1 км/час, затем t2 часов — со скоростью v2 км/час
 * и t3 часов — со скоростью v3 км/час.
 * Определить, за какое время он одолел первую половину пути?
 */
fun timeForHalfWay(
    t1: Double, v1: Double,
    t2: Double, v2: Double,
    t3: Double, v3: Double
): Double {
    // Общий путь
    val totalDistance = t1 * v1 + t2 * v2 + t3 * v3
    val halfDistance = totalDistance / 2.0

    // Проверяем, где находится половина пути
    var distanceCovered = 0.0
    var timeUsed = 0.0

    // Этап 1
    val distance1 = t1 * v1
    if (distanceCovered + distance1 >= halfDistance) {
        // Половина пути на первом этапе
        val remainingDistance = halfDistance - distanceCovered
        return timeUsed + remainingDistance / v1
    }
    distanceCovered += distance1
    timeUsed += t1

    // Этап 2
    val distance2 = t2 * v2
    if (distanceCovered + distance2 >= halfDistance) {
        // Половина пути на втором этапе
        val remainingDistance = halfDistance - distanceCovered
        return timeUsed + remainingDistance / v2
    }
    distanceCovered += distance2
    timeUsed += t2

    // Этап 3
    val remainingDistance = halfDistance - distanceCovered
    return timeUsed + remainingDistance / v3
}

/**
 * Простая (2 балла)
 *
 * Нa шахматной доске стоят черный король и две белые ладьи (ладья бьет по горизонтали и вертикали).
 * Определить, не находится ли король под боем, а если есть угроза, то от кого именно.
 * Вернуть 0, если угрозы нет, 1, если угроза только от первой ладьи, 2, если только от второй ладьи,
 * и 3, если угроза от обеих ладей.
 * Считать, что ладьи не могут загораживать друг друга
 */
fun whichRookThreatens(
    kingX: Int, kingY: Int,
    rookX1: Int, rookY1: Int,
    rookX2: Int, rookY2: Int
): Int {
    val threatens1 = (kingX == rookX1) || (kingY == rookY1)
    val threatens2 = (kingX == rookX2) || (kingY == rookY2)

    return when {
        threatens1 && threatens2 -> 3
        threatens1 -> 1
        threatens2 -> 2
        else -> 0
    }
}

/**
 * Простая (2 балла)
 *
 * На шахматной доске стоят черный король и белые ладья и слон
 * (ладья бьет по горизонтали и вертикали, слон — по диагоналям).
 * Проверить, есть ли угроза королю и если есть, то от кого именно.
 * Вернуть 0, если угрозы нет, 1, если угроза только от ладьи, 2, если только от слона,
 * и 3, если угроза есть и от ладьи и от слона.
 * Считать, что ладья и слон не могут загораживать друг друга.
 */
fun rookOrBishopThreatens(
    kingX: Int, kingY: Int,
    rookX: Int, rookY: Int,
    bishopX: Int, bishopY: Int
): Int {
    // Проверяем ладью (по горизонтали или вертикали)
    val rookThreatens = (kingX == rookX) || (kingY == rookY)

    // Проверяем слона (по диагонали)
    val bishopThreatens = Math.abs(kingX - bishopX) == Math.abs(kingY - bishopY)

    return when {
        rookThreatens && bishopThreatens -> 3
        rookThreatens -> 1
        bishopThreatens -> 2
        else -> 0
    }
}

/**
 * Простая (2 балла)
 *
 * Треугольник задан длинами своих сторон a, b, c.
 * Проверить, является ли данный треугольник остроугольным (вернуть 0),
 * прямоугольным (вернуть 1) или тупоугольным (вернуть 2).
 * Если такой треугольник не существует, вернуть -1.
 */
fun triangleKind(a: Double, b: Double, c: Double): Int {
    // Проверка существования треугольника
    if (a + b <= c || a + c <= b || b + c <= a) {
        return -1
    }

    // Находим наибольшую сторону
    val sides = listOf(a, b, c).sorted()
    val smallest = sides[0]
    val middle = sides[1]
    val largest = sides[2]

    // Проверяем теорему косинусов
    val cosAngle = (smallest * smallest + middle * middle - largest * largest) / (2 * smallest * middle)

    return when {
        cosAngle == 0.0 -> 1 // Прямоугольный (cos = 0)
        cosAngle > 0 -> 0   // Остроугольный (cos > 0, угол < 90)
        else -> 2          // Тупоугольный (cos < 0, угол > 90)
    }
}

/**
 * Средняя (3 балла)
 *
 * Даны четыре точки на одной прямой: A, B, C и D.
 * Координаты точек a, b, c, d соответственно, b >= a, d >= c.
 * Найти длину пересечения отрезков AB и CD.
 * Если пересечения нет, вернуть -1.
 */
fun segmentLength(a: Int, b: Int, c: Int, d: Int): Int {
    // Проверяем, есть ли пересечение
    val maxStart = maxOf(a, c)
    val minEnd = minOf(b, d)

    // Если начало пересечения больше конца, пересечения нет
    return if (maxStart <= minEnd) {
        minEnd - maxStart
    } else {
        -1
    }
}

/**
 * Тестирующая функция для проверки всех решений
 */
fun main() {
    println("Тестирование всех функций:\n")

    // Тест 1: ageDescription
    println("1. ageDescription:")
    println("   ageDescription(21) = \"${ageDescription(21)}\" ")
    println("   ageDescription(32) = \"${ageDescription(32)}\" ")
    println("   ageDescription(12) = \"${ageDescription(12)}\" ")
    println("   ageDescription(11) = \"${ageDescription(11)}\" ")
    println("   ageDescription(111) = \"${ageDescription(111)}\"")

    // Тест 2: timeForHalfWay
    println("\n2. timeForHalfWay:")
    println("   timeForHalfWay(2.0, 5.0, 3.0, 4.0, 1.0, 6.0) = ${String.format("%.2f",
        timeForHalfWay(2.0, 5.0, 3.0, 4.0, 1.0, 6.0))}")

    // Тест 3: whichRookThreatens
    println("\n3. whichRookThreatens:")
    println("   whichRookThreatens(3, 3, 3, 5, 5, 3) = ${whichRookThreatens(3, 3, 3, 5, 5, 3)} ")
    println("   whichRookThreatens(3, 3, 3, 5, 1, 1) = ${whichRookThreatens(3, 3, 3, 5, 1, 1)} ")
    println("   whichRookThreatens(3, 3, 1, 1, 5, 3) = ${whichRookThreatens(3, 3, 1, 1, 5, 3)} ")
    println("   whichRookThreatens(3, 3, 1, 2, 2, 1) = ${whichRookThreatens(3, 3, 1, 2, 2, 1)} ")

    // Тест 4: rookOrBishopThreatens
    println("\n4. rookOrBishopThreatens:")
    println("   rookOrBishopThreatens(4, 4, 4, 2, 2, 2) = ${rookOrBishopThreatens(4, 4, 4, 2, 2, 2)} ")
    println("   rookOrBishopThreatens(4, 4, 4, 2, 1, 1) = ${rookOrBishopThreatens(4, 4, 4, 2, 1, 1)} ")
    println("   rookOrBishopThreatens(4, 4, 1, 1, 2, 2) = ${rookOrBishopThreatens(4, 4, 1, 1, 2, 2)} ")
    println("   rookOrBishopThreatens(4, 4, 1, 2, 2, 1) = ${rookOrBishopThreatens(4, 4, 1, 2, 2, 1)} (")

    // Тест 5: triangleKind
    println("\n5. triangleKind:")
    println("   triangleKind(3.0, 4.0, 5.0) = ${triangleKind(3.0, 4.0, 5.0)} ")
    println("   triangleKind(5.0, 5.0, 5.0) = ${triangleKind(5.0, 5.0, 5.0)} ")
    println("   triangleKind(3.0, 4.0, 6.0) = ${triangleKind(3.0, 4.0, 6.0)} ")
    println("   triangleKind(1.0, 2.0, 3.0) = ${triangleKind(1.0, 2.0, 3.0)} ")

    // Тест 6: segmentLength
    println("\n6. segmentLength:")
    println("   segmentLength(1, 5, 3, 7) = ${segmentLength(1, 5, 3, 7)} ")
    println("   segmentLength(3, 7, 1, 5) = ${segmentLength(3, 7, 1, 5)} ")
    println("   segmentLength(1, 3, 5, 7) = ${segmentLength(1, 3, 5, 7)} ")
    println("   segmentLength(1, 5, 5, 5) = ${segmentLength(1, 5, 5, 5)} ")
}