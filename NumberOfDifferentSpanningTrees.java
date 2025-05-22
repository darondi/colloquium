package ru.leti;

import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.*;

public class NumberOfDifferentSpanningTrees implements GraphCharacteristic {

    @Override
//    public int run(Graph graph) {
//        // пустой граф
//        if (graph.getVertexList().isEmpty()) {
//            return 0;
//        }
//        // связность графа
//        if (!isGraphConnected(graph)) {
//            return 0;
//        }
//
//        // Если граф ориентированный → преобразуем в неориентированный
//        if (graph.isDirect()) {
//            graph = makeUndirected(graph);
//        }
//
//        int n = graph.getVertexList().size();
//        if (n <= 1) return 1; // граф с одной вершиной это одно дерево
//        // карта для поиска индексов вершин
//        Map<Integer, Integer> vertexIndexMap = createVertexIndexMap(graph);
//        // матрица Кирхгофа
//        double[][] kirchhoffMatrix = buildKirchhoffMatrix(graph, n, vertexIndexMap);
//        // минор матрицы Кирхгофа (удаление последней строки и столбца - не принципиально)
//        double[][] minor = new double[n - 1][n - 1];
//        for (int i = 0; i < n - 1; i++) {
//            System.arraycopy(kirchhoffMatrix[i], 0, minor[i], 0, n - 1);
//        }
//        return (int) Math.round(determinant(minor));
//    }
    public int run(Graph graph) {
        // Проверка на пустой граф
        if (graph.getVertexList().isEmpty()) {
            return 0;
        }

        // Нормализация графа (удаление кратных ребер и приведение к неориентированному виду)
        Graph normalizedGraph = normalizeGraph(graph);

        // Проверка связности
        if (!isGraphConnected(normalizedGraph)) {
            return 0;
        }

        int n = normalizedGraph.getVertexList().size();
        if (n <= 1) return 1;

        // Построение матрицы Кирхгофа
        Map<Integer, Integer> vertexIndexMap = createVertexIndexMap(normalizedGraph);
        double[][] kirchhoffMatrix = buildKirchhoffMatrix(normalizedGraph, n, vertexIndexMap);

        // Вычисление минора
        double[][] minor = new double[n - 1][n - 1];
        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(kirchhoffMatrix[i], 0, minor[i], 0, n - 1);
        }

        return (int) Math.round(determinant(minor));
    }

//    // Преобразование ориентированного графа в неориентированный
//    private Graph makeUndirected(Graph graph) {
//        List<Edge> undirectedEdges = new ArrayList<>();
//        for (Edge edge : graph.getEdgeList()) {
//            // Добавляем ребро в исходном направлении
//            undirectedEdges.add(edge);
//            // Если граф ориентированный, добавляем обратное ребро (если его ещё нет)
//            Edge reverseEdge = Edge.builder()
//                    .source(edge.getTarget())
//                    .target(edge.getSource())
//                    .color(edge.getColor())
//                    .weight(edge.getWeight())
//                    .label(edge.getLabel())
//                    .build();
//            // Проверяем, есть ли уже такое ребро (сравниваем по source и target)
//            boolean exists = undirectedEdges.stream()
//                    .anyMatch(e ->
//                            (e.getSource() == reverseEdge.getSource() &&
//                                    e.getTarget() == reverseEdge.getTarget()) ||
//                                    (e.getSource() == reverseEdge.getTarget() &&
//                                            e.getTarget() == reverseEdge.getSource()));
//            if (!exists) {
//                undirectedEdges.add(reverseEdge);
//            }
//        }
//        // Создаём новый неориентированный граф
//        return Graph.builder()
//                .vertexList(graph.getVertexList())
//                .edgeList(undirectedEdges)
//                .isDirect(false) // Теперь граф неориентированный
//                .vertexCount(graph.getVertexCount())
//                .edgeCount(undirectedEdges.size())
//                .build();
//    }
    private Graph normalizeGraph(Graph graph) {
        // Удаляем кратные ребра и нормализуем направление
        Set<Edge> uniqueEdges = new HashSet<>();
        for (Edge edge : graph.getEdgeList()) {
            // Нормализуем направление ребра
            int source = Math.min(edge.getSource(), edge.getTarget());
            int target = Math.max(edge.getSource(), edge.getTarget());

            Edge normalizedEdge = new Edge(source, target, edge.getColor(),
                    edge.getWeight(), edge.getLabel());
            uniqueEdges.add(normalizedEdge);
        }

        return new Graph(
                graph.getVertexCount(),
                uniqueEdges.size(),
                false, // всегда неориентированный после нормализации
                new ArrayList<>(uniqueEdges),
                new ArrayList<>(graph.getVertexList())
        );
    }

    // айдишка + индекс
    private Map<Integer, Integer> createVertexIndexMap(Graph graph) {
        Map<Integer, Integer> vertexIndexMap = new HashMap<>();
        List<Vertex> vertices = graph.getVertexList();
        for (int i = 0; i < vertices.size(); i++) {
            vertexIndexMap.put(vertices.get(i).getId(), i);
        }
        return vertexIndexMap;
    }
    // проверка связности
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
    // обход в глубину
    private void dfs(int v, boolean[] visited, Graph graph, Map<Integer, Integer> vertexIndexMap) {
        visited[v] = true;
        Vertex current = graph.getVertexList().get(v);

        for (Edge edge : graph.getEdgeList()) {
            int otherId = -1;
            if (edge.getSource() == current.getId()) {
                otherId = edge.getTarget();
            } else if (!graph.isDirect() && edge.getTarget() == current.getId()) {
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
    // построение матрицы
    private double[][] buildKirchhoffMatrix(Graph graph, int n, Map<Integer, Integer> vertexIndexMap) {
        double[][] matrix = new double[n][n];
        for (Edge edge : graph.getEdgeList()) {
            Integer u = vertexIndexMap.get(edge.getSource());
            Integer v = vertexIndexMap.get(edge.getTarget());

            if (u != null && v != null) {
                matrix[u][u]++;
                matrix[v][v]++;
                if (!graph.isDirect()) {
                    matrix[u][v]--;
                    matrix[v][u]--;
                }
            }
        }
        return matrix;
    }
    // для поиска определителя матрицы
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