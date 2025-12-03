import java.util.*
class Graph {
    private data class Vertex(val name: String) {
        val neighbors = mutableSetOf<Vertex>()
    }

    private val vertices = mutableMapOf<String, Vertex>()

    private operator fun get(name: String) = vertices[name] ?: throw IllegalArgumentException()

    fun addVertex(name: String) {
        vertices[name] = Vertex(name)
    }

    private fun connect(first: Vertex, second: Vertex) {
        first.neighbors.add(second)
        second.neighbors.add(first)
    }

    fun connect(first: String, second: String) = connect(this[first], this[second])

    /**
     * Пример
     *
     * По двум вершинам рассчитать расстояние между ними = число дуг на самом коротком пути между ними.
     * Вернуть -1, если пути между вершинами не существует.
     *
     * Используется поиск в ширину
     */
    fun bfs(start: String, finish: String) = bfs(this[start], this[finish])

    private fun bfs(start: Vertex, finish: Vertex): Int {
        val queue = ArrayDeque<Vertex>()
        queue.add(start)
        val visited = mutableMapOf(start to 0)
        while (queue.isNotEmpty()) {
            val next = queue.poll()
            val distance = visited[next]!!
            if (next == finish) return distance
            for (neighbor in next.neighbors) {
                if (neighbor in visited) continue
                visited[neighbor] = distance + 1
                queue.add(neighbor)
            }
        }
        return -1
    }

    /**
     * Пример
     *
     * По двум вершинам рассчитать расстояние между ними = число дуг на самом коротком пути между ними.
     * Вернуть -1, если пути между вершинами не существует.
     *
     * Используется поиск в глубину
     */
    fun dfs(start: String, finish: String): Int = dfs(this[start], this[finish], setOf()) ?: -1

    private fun dfs(start: Vertex, finish: Vertex, visited: Set<Vertex>): Int? =
        if (start == finish) 0
        else {
            val min = start.neighbors
                .filter { it !in visited }
                .mapNotNull { dfs(it, finish, visited + start) }
                .minOrNull()
            if (min == null) null else min + 1
        }
}

fun testGraphFunctions() {
    println("   Создание графа и добавление вершин:")

    val graph = Graph()

    // Добавляем вершины
    graph.addVertex("Moscow")
    graph.addVertex("St.Petersburg")
    graph.addVertex("Novosibirsk")
    graph.addVertex("Yekaterinburg")
    graph.addVertex("Kazan")
    graph.addVertex("Sochi")

    // Создаем связи между городами
    graph.connect("Moscow", "St.Petersburg")
    graph.connect("Moscow", "Yekaterinburg")
    graph.connect("Moscow", "Kazan")
    graph.connect("St.Petersburg", "Novosibirsk")
    graph.connect("Yekaterinburg", "Novosibirsk")
    graph.connect("Kazan", "Sochi")
    graph.connect("Yekaterinburg", "Sochi")

    println("   Граф создан с 6 вершинами и 7 ребрами")

    println("\n   Поиск кратчайшего пути (BFS):")
    println("   Москва -> Новосибирск: ${graph.bfs("Moscow", "Novosibirsk")} переходов")
    println("   Москва -> Сочи: ${graph.bfs("Moscow", "Sochi")} переходов")
    println("   Казань -> Новосибирск: ${graph.bfs("Kazan", "Novosibirsk")} переходов")
    println("   Санкт-Петербург -> Сочи: ${graph.bfs("St.Petersburg", "Sochi")} переходов")

    println("\n   Поиск кратчайшего пути (DFS):")
    println("   Москва -> Новосибирск: ${graph.dfs("Moscow", "Novosibirsk")} переходов")
    println("   Москва -> Сочи: ${graph.dfs("Moscow", "Sochi")} переходов")

    println("\n   Тест несвязанных вершин:")
    graph.addVertex("Vladivostok")
    println("   Москва -> Владивосток: ${graph.bfs("Moscow", "Vladivostok")} (не связаны)")
    println("   Москва -> Владивосток (DFS): ${graph.dfs("Moscow", "Vladivostok")}")

    // Дополнительный тест: шахматная доска как граф для коня
    println("\n   Пример: шахматная доска как граф для коня")
    val chessGraph = Graph()

    // Добавляем все клетки шахматной доски
    for (col in 1..8) {
        for (row in 1..8) {
            val squareName = "${'a' + col - 1}$row"
            chessGraph.addVertex(squareName)
        }
    }

    // Создаем связи для ходов коня
    val knightMoves = listOf(
        Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1),
        Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2)
    )

    for (col in 1..8) {
        for (row in 1..8) {
            val from = "${'a' + col - 1}$row"
            for ((dx, dy) in knightMoves) {
                val newCol = col + dx
                val newRow = row + dy
                if (newCol in 1..8 && newRow in 1..8) {
                    val to = "${'a' + newCol - 1}$newRow"
                    if (!chessGraph.bfs(from, to).toString().startsWith("Vertex")) { // Проверяем, что функция существует
                        chessGraph.connect(from, to)
                    }
                }
            }
        }
    }

    println("   Шахматный граф создан с 64 вершинами")
    println("   Кратчайший путь коня от a1 до h8: ${chessGraph.bfs("a1", "h8")} ходов")
    println("   Кратчайший путь коня от a1 до b3: ${chessGraph.bfs("a1", "b3")} ходов")
}
fun main() {
    println("=== ТЕСТИРОВАНИЕ ШАХМАТНЫХ ФУНКЦИЙ ===\n")
    println("\nТЕСТИРОВАНИЕ КЛАССА GRAPH:")
    testGraphFunctions()

    println("\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ ===")
}
