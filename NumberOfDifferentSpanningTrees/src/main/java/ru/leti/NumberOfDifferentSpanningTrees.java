package ru.leti;

import ru.leti.wise.task.graph.model.Graph;
import ru.leti.wise.task.graph.model.Vertex;
import ru.leti.wise.task.graph.model.Edge;
import ru.leti.wise.task.plugin.graph.GraphCharacteristic;

import java.util.List;

public class NumberOfDifferentSpanningTrees implements GraphCharacteristic {

    @Override
    public int run(Graph graph) {
        // Проверка графа на пустоту и связность
        if (graph.getVertexList().isEmpty() || !isGraphConnected(graph)) {
            return 0;
        }

        int n = graph.getVertexList().size();
        if (n <= 1) return 1; // Для графа с одной вершиной - одно "дерево"

        double[][] kirchhoffMatrix = buildKirchhoffMatrix(graph, n);

        // Вычисляем минор матрицы Кирхгофа (удаляем последнюю строку и столбец)
        double[][] minor = new double[n - 1][n - 1];
        for (int i = 0; i < n - 1; i++) {
            System.arraycopy(kirchhoffMatrix[i], 0, minor[i], 0, n - 1);
        }

        return (int) Math.round(determinant(minor));
    }

    private boolean isGraphConnected(Graph graph) {
        List<Vertex> vertices = graph.getVertexList();
        if (vertices.isEmpty()) return true;

        boolean[] visited = new boolean[vertices.size()];
        dfs(0, visited, graph);

        for (boolean v : visited) {
            if (!v) return false;
        }
        return true;
    }

    private void dfs(int v, boolean[] visited, Graph graph) {
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
                for (int i = 0; i < graph.getVertexList().size(); i++) {
                    if (graph.getVertexList().get(i).getId() == otherId && !visited[i]) {
                        dfs(i, visited, graph);
                    }
                }
            }
        }
    }

    private double[][] buildKirchhoffMatrix(Graph graph, int n) {
        double[][] matrix = new double[n][n];

        for (Edge edge : graph.getEdgeList()) {
            int u = indexOfVertex(graph, edge.getSource());
            int v = indexOfVertex(graph, edge.getTarget());

            if (u != -1 && v != -1 && u != v) {
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


    private int indexOfVertex(Graph graph, int vertexId) {
        List<Vertex> vertices = graph.getVertexList();
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).getId() == vertexId) {
                return i;
            }
        }
        return -1;
    }

    // Более эффективное вычисление определителя через метод Гаусса
    private double determinant(double[][] matrix) {
        int n = matrix.length;
        double det = 1;

        for (int i = 0; i < n; i++) {
            // Поиск строки с максимальным элементом в текущем столбце
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(matrix[k][i]) > Math.abs(matrix[maxRow][i])) {
                    maxRow = k;
                }
            }

            // Перестановка строк
            if (maxRow != i) {
                double[] temp = matrix[i];
                matrix[i] = matrix[maxRow];
                matrix[maxRow] = temp;
                det = -det;
            }

            // Если ведущий элемент нулевой, то определитель 0
            if (matrix[i][i] == 0) {
                return 0;
            }

            // Приведение к треугольному виду
            for (int k = i + 1; k < n; k++) {
                double factor = matrix[k][i] / matrix[i][i];
                for (int j = i; j < n; j++) {
                    matrix[k][j] -= factor * matrix[i][j];
                }
            }
        }

        // Определитель - произведение элементов главной диагонали
        for (int i = 0; i < n; i++) {
            det *= matrix[i][i];
        }

        return det;
    }
}