import kotlin.math.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая (2 балла)
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        val distanceBetweenCenters = center.distance(other.center)
        val sumOfRadii = radius + other.radius

        return if (distanceBetweenCenters > sumOfRadii) {
            distanceBetweenCenters - sumOfRadii
        } else {
            0.0
        }
    }

    /**
     * Тривиальная (1 балл)
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean {
        return center.distance(p) <= radius + 1e-9 // Добавляем небольшую погрешность для точности
    }
}

data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}
/**
 * Средняя (3 балла)
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    if (points.size < 2) {
        throw IllegalArgumentException("Need at least 2 points")
    }

    var maxDistance = -1.0
    var resultSegment = Segment(points[0], points[1])

    for (i in points.indices) {
        for (j in i + 1 until points.size) {
            val distance = points[i].distance(points[j])
            if (distance > maxDistance) {
                maxDistance = distance
                resultSegment = Segment(points[i], points[j])
            }
        }
    }

    return resultSegment
}

/**
 * Простая (2 балла)
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val center = Point(
        (diameter.begin.x + diameter.end.x) / 2,
        (diameter.begin.y + diameter.end.y) / 2
    )
    val radius = diameter.begin.distance(diameter.end) / 2
    return Circle(center, radius)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя (3 балла)
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        // Система уравнений:
        // cos1 * y = sin1 * x + b1  (1)
        // cos2 * y = sin2 * x + b2  (2)

        val cos1 = cos(angle)
        val sin1 = sin(angle)
        val cos2 = cos(other.angle)
        val sin2 = sin(other.angle)

        // Если прямые параллельны или совпадают
        val denominator = cos2 * sin1 - cos1 * sin2
        if (abs(denominator) < 1e-9) {
            // Прямые параллельны
            if (abs(b - other.b) < 1e-9) {
                // Прямые совпадают, возвращаем любую точку на прямой
                return Point(0.0, b / cos1)
            } else {
                throw IllegalArgumentException("Lines are parallel and don't intersect")
            }
        }

        val x = (cos1 * other.b - cos2 * b) / denominator
        val y = (sin1 * other.b - sin2 * b) / denominator

        return Point(x, y)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line {
    return lineByPoints(s.begin, s.end)
}

/**
 * Средняя (3 балла)
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    if (a == b) {
        throw IllegalArgumentException("Points must be different")
    }

    // Угол наклона прямой
    val angle = atan2(b.y - a.y, b.x - a.x)

    // Нормализуем угол в диапазон [0, PI)
    val normalizedAngle = if (angle < 0) angle + PI else angle

    return Line(a, normalizedAngle)
}

/**
 * Сложная (5 баллов)
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    // Находим середину отрезка
    val midpoint = Point(
        (a.x + b.x) / 2,
        (a.y + b.y) / 2
    )

    // Находим угол исходного отрезка
    val segmentAngle = atan2(b.y - a.y, b.x - a.x)

    // Угол перпендикуляра
    val perpendicularAngle = segmentAngle + PI / 2

    // Нормализуем угол в диапазон [0, PI)
    val normalizedAngle = if (perpendicularAngle < 0) {
        perpendicularAngle + PI
    } else if (perpendicularAngle >= PI) {
        perpendicularAngle - PI
    } else {
        perpendicularAngle
    }

    return Line(midpoint, normalizedAngle)
}

/**
 * Средняя (3 балла)
 *
 * Задан список из n окружностей на плоскости.
 * Найти пару наименее удалённых из них; расстояние между окружностями
 * рассчитывать так, как указано в Circle.distance.
 *
 * При наличии нескольких наименее удалённых пар,
 * вернуть первую из них по порядку в списке circles.
 *
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    if (circles.size < 2) {
        throw IllegalArgumentException("Need at least 2 circles")
    }

    var minDistance = Double.MAX_VALUE
    var resultPair = Pair(circles[0], circles[1])

    for (i in circles.indices) {
        for (j in i + 1 until circles.size) {
            val distance = circles[i].distance(circles[j])
            if (distance < minDistance) {
                minDistance = distance
                resultPair = Pair(circles[i], circles[j])
            }
        }
    }

    return resultPair
}

/**
 * Сложная (5 баллов)
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    // Проверяем, что точки не лежат на одной прямой
    val area = abs(
        a.x * (b.y - c.y) +
                b.x * (c.y - a.y) +
                c.x * (a.y - b.y)
    ) / 2

    if (area < 1e-9) {
        throw IllegalArgumentException("Points are collinear")
    }

    // Используем формулу центра описанной окружности через определители
    val d = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y))

    val centerX = (
            (a.x * a.x + a.y * a.y) * (b.y - c.y) +
                    (b.x * b.x + b.y * b.y) * (c.y - a.y) +
                    (c.x * c.x + c.y * c.y) * (a.y - b.y)
            ) / d

    val centerY = (
            (a.x * a.x + a.y * a.y) * (c.x - b.x) +
                    (b.x * b.x + b.y * b.y) * (a.x - c.x) +
                    (c.x * c.x + c.y * c.y) * (b.x - a.x)
            ) / d

    val center = Point(centerX, centerY)
    val radius = center.distance(a)

    return Circle(center, radius)
}

/**
 * Очень сложная (10 баллов)
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle {
    if (points.isEmpty()) {
        throw IllegalArgumentException("Points set is empty")
    }

    if (points.size == 1) {
        return Circle(points[0], 0.0)
    }

    // Используем подход с перебором всех возможных троек и пар точек

    var bestCircle: Circle? = null

    // Проверяем окружности, построенные на парах точек
    for (i in points.indices) {
        for (j in i + 1 until points.size) {
            val candidate = circleByDiameter(Segment(points[i], points[j]))
            if (bestCircle == null || candidate.radius < bestCircle.radius) {
                if (points.all { candidate.contains(it) }) {
                    bestCircle = candidate
                }
            }
        }
    }

    // Проверяем окружности, построенные на тройках точек
    for (i in points.indices) {
        for (j in i + 1 until points.size) {
            for (k in j + 1 until points.size) {
                try {
                    val candidate = circleByThreePoints(points[i], points[j], points[k])
                    if (bestCircle == null || candidate.radius < bestCircle.radius) {
                        if (points.all { candidate.contains(it) }) {
                            bestCircle = candidate
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    // Точки лежат на одной прямой, пропускаем
                }
            }
        }
    }

    return bestCircle ?: Circle(Point(0.0, 0.0), 0.0) // На всякий случай
}

/**
 * Вспомогательный класс Point (предполагается, что он уже определен)
 * Если его нет, вот простая реализация:
 */
data class Point(val x: Double, val y: Double) {
    fun distance(other: Point): Double {
        return sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
    }
}

/**
 * Главная функция для тестирования
 */
fun main() {
    println("=== Тестирование геометрических функций ===\n")

    // 1. Тестирование Circle.distance и Circle.contains
    println("1. Тестирование класса Circle:")
    val circle1 = Circle(Point(0.0, 0.0), 5.0)
    val circle2 = Circle(Point(10.0, 0.0), 3.0)
    val circle3 = Circle(Point(2.0, 2.0), 3.0)

    println("   Расстояние между окружностями:")
    println("   circle1.distance(circle2) = ${circle1.distance(circle2)}")
    println("   circle1.distance(circle3) = ${circle1.distance(circle3)}")

    val testPoint = Point(3.0, 4.0)
    println("   Проверка точки (3, 4) в окружности:")
    println("   circle1.contains($testPoint) = ${circle1.contains(testPoint)}")

    // 2. Тестирование diameter
    println("\n2. Тестирование функции diameter:")
    val points = arrayOf(
        Point(0.0, 0.0),
        Point(3.0, 4.0),
        Point(10.0, 0.0),
        Point(5.0, 12.0)
    )
    val longestSegment = diameter(*points)
    println("   Самый длинный отрезок: ${longestSegment.begin} - ${longestSegment.end}")
    println("   Длина: ${longestSegment.begin.distance(longestSegment.end)}")

    // 3. Тестирование circleByDiameter
    println("\n3. Тестирование функции circleByDiameter:")
    val diameterSegment = Segment(Point(0.0, 0.0), Point(6.0, 8.0))
    val circleFromDiameter = circleByDiameter(diameterSegment)
    println("   Окружность по диаметру: $circleFromDiameter")

    // 4. Тестирование Line и пересечения прямых
    println("\n4. Тестирование класса Line:")
    val line1 = Line(Point(0.0, 0.0), PI / 4) // y = x
    val line2 = Line(Point(0.0, 5.0), 0.0)    // y = 5
    try {
        val intersection = line1.crossPoint(line2)
        println("   Пересечение прямых: $intersection")
    } catch (e: IllegalArgumentException) {
        println("   Прямые параллельны")
    }

    // 5. Тестирование lineByPoints и bisectorByPoints
    println("\n5. Тестирование функций для работы с прямыми:")
    val pointA = Point(0.0, 0.0)
    val pointB = Point(4.0, 0.0)
    val lineAB = lineByPoints(pointA, pointB)
    val bisectorAB = bisectorByPoints(pointA, pointB)
    println("   Прямая через точки A и B: $lineAB")
    println("   Серединный перпендикуляр: $bisectorAB")

    // 6. Тестирование findNearestCirclePair
    println("\n6. Тестирование функции findNearestCirclePair:")
    val circles = arrayOf(
        Circle(Point(0.0, 0.0), 2.0),
        Circle(Point(5.0, 0.0), 2.0),
        Circle(Point(2.0, 2.0), 1.0),
        Circle(Point(2.5, 2.5), 0.5)
    )
    val nearestPair = findNearestCirclePair(*circles)
    println("   Ближайшая пара окружностей: ${nearestPair.first} и ${nearestPair.second}")
    println("   Расстояние между ними: ${nearestPair.first.distance(nearestPair.second)}")

    // 7. Тестирование circleByThreePoints
    println("\n7. Тестирование функции circleByThreePoints:")
    val point1 = Point(0.0, 0.0)
    val point2 = Point(4.0, 0.0)
    val point3 = Point(0.0, 3.0)
    val triangleCircle = circleByThreePoints(point1, point2, point3)
    println("   Окружность по трём точкам: $triangleCircle")

    // 8. Тестирование minContainingCircle
    println("\n8. Тестирование функции minContainingCircle:")
    val testPoints = arrayOf(
        Point(0.0, 0.0),
        Point(1.0, 2.0),
        Point(3.0, 1.0),
        Point(2.0, 3.0)
    )
    val minCircle = minContainingCircle(*testPoints)
    println("   Минимальная охватывающая окружность: $minCircle")

    // Проверяем, что все точки действительно содержатся в окружности
    val allContained = testPoints.all { minCircle.contains(it) }
    println("   Все точки содержатся в окружности: $allContained")

    println("\n=== Тестирование завершено ===")
}