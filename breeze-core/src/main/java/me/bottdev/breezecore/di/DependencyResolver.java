package me.bottdev.breezecore.di;

import me.bottdev.breezeapi.commons.structures.graph.GraphEdge;
import me.bottdev.breezeapi.commons.structures.graph.GraphNode;
import me.bottdev.breezeapi.commons.structures.graph.directed.DirectedGraph;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class DependencyResolver {

    private static final BreezeLogger logger = new SimpleLogger("DependencyResolver");

    public static List<ResolvedDependency> resolve(ComponentIndex index) {
        logger.info("Resolving dependencies...");
        DirectedGraph<String> graph = buildGraph(index);
        if (graph.hasCycle()) {
            throw new IllegalArgumentException("Failed to resolve dependencies: found cycle dependencies.");
        }
        logger.info("Successfully resolved dependencies.");

        List<ResolvedDependency> resolved = new ArrayList<>();
        resolved.addAll(getIsolatedDependencies(graph));
        resolved.addAll(applyTopographicalSort(graph));

        return resolved;
    }

    private static DirectedGraph<String> buildGraph(ComponentIndex index) {

        logger.info("Building graph out of component index...");

        DirectedGraph<String> graph = new DirectedGraph<>();

        for (ComponentIndex.Entry entry : index.getEntries()) {
            HashMap<String, Object> attributes = new HashMap<>();
            attributes.put("supplyType", entry.getSupplyType());

            graph.addNode(entry.getClassPath(), attributes);

            logger.info("- Added graph node: {}", entry.getClassPath());
        }

        for (ComponentIndex.Entry entry : index.getEntries()) {
            for (String dependency : entry.getDependencies()) {
                graph.addEdge(entry.getClassPath(), dependency);
                logger.info("- Added graph edge: {} -> {}", entry.getClassPath(), dependency);
            }
        }

        logger.info("Successfully built graph out of component index.");

        return graph;
    }



    private static List<ResolvedDependency> applyTopographicalSort(DirectedGraph<String> graph) {

        logger.info("Making topographical sort using Kahn's algorithm...");

        List<ResolvedDependency> sorted = new ArrayList<>();

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
            logger.info("- Graph node {} has {} dependencies", node.getValue(), degree);
            if (degree == 0) {
                queue.add(node);
            }
        }

        while (!queue.isEmpty()) {
            GraphNode<String> node = queue.poll();

            sorted.add(new ResolvedDependency(
                    node.getValue(),
                    (SupplyType) node.getAttributes().get("supplyType")
            ));
            logger.info("- Added dependency {} to sort list.", node.getValue());

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

        logger.info("Successfully built topographical sort using Kahn's algorithm.");

        return sorted;
    }

    private static List<ResolvedDependency> getIsolatedDependencies(DirectedGraph<String> graph) {
        return graph.getIsolatedNodes().stream()
                .map(node ->
                        new ResolvedDependency(
                                node.getValue(),
                                (SupplyType) node.getAttributes().get("supplyType")
                        )
                )
                .toList();
    }

}
