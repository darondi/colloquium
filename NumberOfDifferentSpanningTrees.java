import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Матричная теорема о деревьях или теорема Кирхгофа - даёт выражение на число остовных деревьев графа через определитель матрицы.
// Пусть G — связный помеченный граф с матрицей Кирхгофа M.
// Все алгебраические дополнения матрицы Кирхгофа M равны между собой и их общее значение равно количеству остовных деревьев графа G.
// Число остовов в связном неодноэлементном обыкновенном графе G
// равно алгебраическому дополнению любого элемента матрицы Кирхгофа B(G).
// Количество остовных деревьев в графе с одной вершиной равно 1.
// Матрица Кирхгофа (Лапласиан) строится по правилам:
// Диагональные элементы matrix[i][i] = степень вершины i.
// Недиагональные элементы matrix[i][j] = −1, если есть ребро между i и j, иначе 0.

/**
 * Реализация подсчёта количества различных остовных деревьев в графе с использованием матричной теоремы Кирхгофа.
 * Теорема утверждает, что для связного помеченного графа G с матрицей Кирхгофа M:
 * - Все алгебраические дополнения M равны между собой
 * - Их значение равно количеству остовных деревьев графа G
 * Для графа с n вершинами:
 * - Матрица Кирхгофа: L = D - A (D - матрица степеней, A - матрица смежности)
 * - Диагональные элементы: L[i][i] = степень вершины i
 * - Недиагональные: L[i][j] = -1 если есть ребро (i, j), иначе 0
 */
public class NumberOfDifferentSpanningTrees implements GraphCharacteristic {
    /**
     * Основной метод вычисления количества остовных деревьев
     * Возвращает:
     * - 0 для пустого или несвязного графа
     * - 1 для графа с одной вершиной
     * - Для связных графов: определитель минора матрицы Кирхгофа
     */
    @Override
    public int run(Graph graph) {
        if (graph.isDirect()) {
            throw new IllegalArgumentException("Метод поддерживает только неориентированные графы");
        }
        // Если граф не содержит вершин, то количество остовных деревьев равно 0
        if (graph.getVertexList().isEmpty()) {
            return 0;
        }
        // У несвязных графов также количество остовных деревьев равно 0
        if (!isGraphConnected(graph)) {
            return 0;
        }
        int vertexCount = graph.getVertexList().size();
        if (vertexCount <= 1) return 1; // Граф с одной вершиной имеет одно остовное дерево

        Map<Integer, Integer> vertexIndexMap = createVertexIndexMap(graph);
        // Матрица Кирхгофа
        double[][] kirchhoffMatrix = buildKirchhoffMatrix(graph, vertexCount, vertexIndexMap);
        // Чтобы найти количество остовных деревьев, нужно взять алгебраическое дополнение любого элемента матрицы Кирхгофа
        double[][] minor = new double[vertexCount - 1][vertexCount - 1];
        for (int i = 0; i < vertexCount - 1; i++) {
            System.arraycopy(kirchhoffMatrix[i], 0, minor[i], 0, vertexCount - 1);
        }
        return (int) Math.round(determinant(minor));
    }


    // ID вершины - индекс
    private Map<Integer, Integer> createVertexIndexMap(Graph graph) {
        Map<Integer, Integer> vertexIndexMap = new HashMap<>();
        List<Vertex> vertices = graph.getVertexList();
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndexMap.put(vertices.get(i).getId(), i);
        }
        return vertexIndexMap;
    }

    // Проверка связности графа через обход в глубину, возвращает true если все вершины достижимы из стартовой
    private boolean isGraphConnected(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        if (vertices.isEmpty()) return true;
        boolean[] visited = new boolean[vertices.size()];
        Map<Integer, Integer> vertexIndexMap = createVertexIndexMap(graph);
        dfs(vertexIndexMap.get(vertices.get(0).getId()), visited, graph, vertexIndexMap);
        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    // Обход в глубину
    private void dfs(int v, boolean[] visited, Graph graph, Map<Integer, Integer> vertexIndexMap) {
        visited[v] = true;
        Vertex current = graph.getVertexList().get(v);

        for (Edge edge : graph.getEdgeList()) {
            int otherId = -1;
            if (edge.getSource() == current.getId()) {
                otherId = edge.getTarget();
            } else if (edge.getTarget() == current.getId()) {
                otherId = edge.getSource();
            }

            if (otherId != -1) {
                Integer neighborIndex = vertexIndexMap.get(otherId);
                if (neighborIndex != null && !visited[neighborIndex]) {
                    dfs(neighborIndex, visited, graph, vertexIndexMap);
                }
            }
        }
    }

    /**
     * Строит матрицу Кирхгофа для графа
     * Алгоритм:
     * 1. Инициализирует нулевую матрицу n×n
     * 2. Для каждого ребра (u, v):
     * - Увеличивает degree[u][u] и degree[v][v] на 1
     * - Уменьшает degree[u][v] и degree[v][u] на 1 для неориентированного графа
     * По итогу возвращает построенную матрицу Кирхгофа
     */
    private double[][] buildKirchhoffMatrix(Graph graph, int n, Map<Integer, Integer> vertexIndexMap) {
        double[][] matrix = new double[n][n];
        for (Edge edge : graph.getEdgeList()) {
            Integer u = vertexIndexMap.get(edge.getSource());
            Integer v = vertexIndexMap.get(edge.getTarget());
            if (u != null && v != null && !u.equals(v)) {
                matrix[u][u]++;
                matrix[v][v]++;
                matrix[u][v]--;
                matrix[v][u]--;
            }
        }
        return matrix;
    }

    /**
     * Вычисляет определитель матрицы методом Гаусса с выбором главного элемента
     * Алгоритм:
     * 1. Приведение матрицы к верхнетреугольному виду
     * 2. Выбор максимального элемента в столбце для устойчивости
     * 3. Перестановка строк при необходимости
     * 4. Нулевой определитель возвращается при обнаружении вырожденности
     * По итогу возвращает значение определителя
     */
    private double determinant(double[][] matrix) {
        int n = matrix.length;
        double det = 1;
        double epsilon = 1e-10;
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(matrix[k][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = k;
                }
            }
            if (maxRow != i) {
                double[] temp = matrix[i];
                matrix[i] = matrix[maxRow];
                matrix[maxRow] = temp;
                det = -det;
            }
            if (Math.abs(matrix[i][i]) < epsilon) {
                return 0;
            }
            for (int k = i + 1; k < n; k++) {
                double factor = matrix[k][i] / matrix[i][i];
                for (int j = i; j < n; j++) {
                    matrix[k][j] -= factor * matrix[i][j];
                }
            }
        }
        for (int i = 0; i < n; i++) {
            det *= matrix[i][i];
        }
        return det;
    }
}
