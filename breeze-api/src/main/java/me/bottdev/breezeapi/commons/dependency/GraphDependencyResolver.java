package me.bottdev.breezeapi.commons.dependency;

import me.bottdev.breezeapi.commons.dependency.exceptions.DependencyResolvationException;
import me.bottdev.breezeapi.commons.structures.graph.GraphEdge;
import me.bottdev.breezeapi.commons.structures.graph.GraphNode;
import me.bottdev.breezeapi.commons.structures.graph.directed.DirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public interface GraphDependencyResolver<D extends Dependent, C extends DependentContainer<D>> extends DependencyResolver<D, C> {

    @Override
    default List<D> resolve(C container) {

        Map<String, D> dependentMap = container.getDependentWithIds();
        DirectedGraph<String> graph = buildGraph(container);

        List<String> sortedIds = applyKahnAlgorithm(graph);

        return sortedIds.stream().map(dependentMap::get).toList();
    }

    private DirectedGraph<String> buildGraph(C container) {

        DirectedGraph<String> graph = new DirectedGraph<>();
        Map<String, D> dependentMap = container.getDependentWithIds();

        dependentMap.forEach((id, dependent) -> {
            HashMap<String, Object> attributes = createNodeAttributes(dependent);
            graph.addNode(id, attributes);
        });

        dependentMap.forEach((id, dependent) -> {
            dependent.getDependencies().forEach(dependency -> {
                graph.addEdge(id, dependency);
            });
        });

        return graph;
    }

    HashMap<String, Object> createNodeAttributes(D entry);

    private List<String> applyKahnAlgorithm(DirectedGraph<String> graph) {

        List<String> sorted = new ArrayList<>();

        HashMap<GraphNode<String>, Integer> inDegree = new HashMap<>();

        for (GraphNode<String> node : graph.getNodes()) {
            inDegree.put(node, 0);
        }

        for (GraphEdge<String> edge : graph.getEdges()) {
            GraphNode<String> from = edge.getFrom();
            GraphNode<String> to = edge.getTo();
            inDegree.put(to, inDegree.getOrDefault(to, 0));
            inDegree.put(from, inDegree.getOrDefault(from, 0) + 1);
        }

        LinkedBlockingQueue<GraphNode<String>> queue = new LinkedBlockingQueue<>();
        for (GraphNode<String> node : graph.getNodes()) {
            int degree = inDegree.get(node);
            if (degree == 0) {
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            GraphNode<String> node = queue.poll();

            sorted.add(node.getValue());

            for (GraphNode<String> neighbor : graph.getIncomingNeighbors(node)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (sorted.size() < graph.getNodes().size()) {

            List<String> cycleNodes = new ArrayList<>();
            for (GraphNode<String> node : graph.getNodes()) {
                if (inDegree.get(node) > 0) {
                    cycleNodes.add(node.getValue());
                }
            }

            throw new DependencyResolvationException(
                    "Failed to build topographical sort: graph contains a cycle involving nodes: "
                            + cycleNodes
            );

        }

        return sorted;
    }

}
