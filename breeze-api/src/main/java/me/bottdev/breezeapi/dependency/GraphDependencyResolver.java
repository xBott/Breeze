package me.bottdev.breezeapi.dependency;

import me.bottdev.breezeapi.commons.structures.graph.GraphEdge;
import me.bottdev.breezeapi.commons.structures.graph.GraphNode;
import me.bottdev.breezeapi.commons.structures.graph.directed.DirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public interface GraphDependencyResolver<D extends Dependent, C extends DependentContainer<D>> extends DependencyResolver<D, C> {

    @SuppressWarnings("unchecked")
    @Override
    default List<D> resolve(C container) {
        getLogger().info("Resolving dependencies...");

        Map<String, D> dependentMap = container.getDependentWithIds();
        DirectedGraph<String> graph = buildGraph(container);
        if (graph.hasCycle()) {
            throw new IllegalArgumentException("Failed to resolve dependencies: found cycle dependencies.");
        }

        List<String> sortedIds = applyKahnAlgorithm(graph);
        List<D> resolved = sortedIds.stream().map(dependentMap::get).toList();

        getLogger().info("Successfully resolved dependencies.");

        return resolved;
    }

    private DirectedGraph<String> buildGraph(C container) {

        getLogger().info("Building graph out of component index...");

        DirectedGraph<String> graph = new DirectedGraph<>();
        Map<String, D> dependentMap = container.getDependentWithIds();

        dependentMap.forEach((id, dependent) -> {
            HashMap<String, Object> attributes = createNodeAttributes(dependent);
            graph.addNode(id, attributes);

            getLogger().info("- Added graph node: {}", dependent);
        });

        dependentMap.forEach((id, dependent) -> {
            dependent.getDependencies().forEach(dependency -> {
                graph.addEdge(id, dependency);
                getLogger().info("- Added graph edge: {} -> {}", dependent, dependency);
            });
        });

        getLogger().info("Successfully built graph out of component index.");

        return graph;
    }

    HashMap<String, Object> createNodeAttributes(D entry);

    private List<String> applyKahnAlgorithm(DirectedGraph<String> graph) {

        getLogger().info("Making topographical sort using Kahn's algorithm...");

        List<String> sorted = new ArrayList<>();

        HashMap<GraphNode<String>, Integer> inDegree = new HashMap<>();

        for (GraphEdge<String> edge : graph.getEdges()) {
            GraphNode<String> from = edge.getFrom();
            GraphNode<String> to = edge.getTo();
            inDegree.put(to, inDegree.getOrDefault(to, 0));
            inDegree.put(from, inDegree.getOrDefault(from, 0) + 1);
        }

        LinkedBlockingQueue<GraphNode<String>> queue = new LinkedBlockingQueue<>();
        for (GraphNode<String> node : graph.getNodes()) {
            int degree = inDegree.get(node);
            getLogger().info("- Graph node {} has {} dependencies", node.getValue(), degree);
            if (degree == 0) {
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            GraphNode<String> node = queue.poll();

            sorted.add(node.getValue());
            getLogger().info("- Added dependency {} to sort list.", node.getValue());

            for (GraphNode<String> neighbor : graph.getIncomingNeighbors(node)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (sorted.size() < graph.getNodes().size()) {
            throw new IllegalArgumentException("Failed to build topographical sort: graph contains a cycle.");
        }

        getLogger().info("Successfully built topographical sort using Kahn's algorithm.");

        return sorted;
    }

}
