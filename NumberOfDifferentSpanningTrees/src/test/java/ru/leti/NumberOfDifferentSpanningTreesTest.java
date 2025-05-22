package ru.leti;
//import ru.leti.NumberOfDifferentSpanningTrees;
import org.junit.jupiter.api.Test;
import ru.leti.wise.task.graph.util.FileLoader;
import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class NumberOfDifferentSpanningTreesTest {

    @Test
    public void testTriangleGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph = FileLoader.loadGraphFromJson("src/test/resources/graph1.json");
        assertThat(counter.run(graph)).isEqualTo(3);
    }

    @Test
    public void testSimpleGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph2 = FileLoader.loadGraphFromJson("src/test/resources/graph2.json");
        assertThat(counter.run(graph2)).isEqualTo(1);
    }

    @Test
    public void testEmptyGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph3 = FileLoader.loadGraphFromJson("src/test/resources/graph3.json");
        assertThat(counter.run(graph3)).isEqualTo(0);
    }

    @Test
    public void testEmptyEdgesGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph3 = FileLoader.loadGraphFromJson("src/test/resources/graph4.json");
        assertThat(counter.run(graph3)).isEqualTo(0);
    }

    @Test
    public void testDifficultGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph5 = FileLoader.loadGraphFromJson("src/test/resources/graph5.json");
        assertThat(counter.run(graph5)).isEqualTo(79);
    }

    @Test
    public void testFullGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph6 = FileLoader.loadGraphFromJson("src/test/resources/graph6.json");
        assertThat(counter.run(graph6)).isEqualTo(11);
    }

    @Test
    public void testStarGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph7 = FileLoader.loadGraphFromJson("src/test/resources/graph7.json");
        assertThat(counter.run(graph7)).isEqualTo(729);
    }

    @Test
    public void testFlowerGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph8 = FileLoader.loadGraphFromJson("src/test/resources/graph8.json");
        assertThat(counter.run(graph8)).isEqualTo(2000);
    }

//    @Test
//    public void testMultiGraph() throws FileNotFoundException {
//        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
//        var graph9 = FileLoader.loadGraphFromJson("src/test/resources/graph9.json");
//        assertThat(counter.run(graph9)).isEqualTo(2000);
//    }

//    @Test
//    public void testOrientedGraph() throws FileNotFoundException {
//        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
//        var graph10 = FileLoader.loadGraphFromJson("src/test/resources/graph10.json");
//        assertThat(counter.run(graph10)).isEqualTo(120);
//    }

    @Test
    public void testBipartiteGraph() throws FileNotFoundException {
        NumberOfDifferentSpanningTrees counter = new NumberOfDifferentSpanningTrees();
        var graph11 = FileLoader.loadGraphFromJson("src/test/resources/graph11.json");
        assertThat(counter.run(graph11)).isEqualTo(12);
    }
}