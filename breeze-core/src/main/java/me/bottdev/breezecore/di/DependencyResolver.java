package me.bottdev.breezecore.di;

import me.bottdev.breezeapi.commons.structures.graph.GraphNode;
import me.bottdev.breezeapi.commons.structures.graph.directed.DirectedGraph;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DependencyResolver {

    private static final BreezeLogger logger = new SimpleLogger("DependencyResolver");

    public static List<ResolvedDependency> resolve(ComponentIndex index) {
        logger.info("Resolving dependencies...");
        DirectedGraph<String> graph = buildGraph(index);
        if (graph.hasCycle()) {
            throw new IllegalArgumentException("Failed to resolve dependencies: found cycle dependencies.");
        }
        logger.info("Successfully resolved dependencies.");
        return applyTopographicalSort(graph);
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

        logger.info("Making topographical sort using DFS...");

        List<ResolvedDependency> sorted = new ArrayList<>();

        Optional<GraphNode<String>> leastEdgesNodeOptional = graph.getNodeWithLeastEdges();
        if (leastEdgesNodeOptional.isEmpty()) {
            throw new IllegalArgumentException("Failed to build topographical sort using DFS.");
        }

        GraphNode<String> leastEdgesNode = leastEdgesNodeOptional.get();

        graph.dfs(leastEdgesNode, node -> {
                sorted.add(
                        new ResolvedDependency(
                                node.getValue(),
                                (SupplyType) node.getAttributes().get("supplyType")
                        )
                );
                logger.info("- Added dependency {} to sort list.", node.getValue());
        });

        logger.info("Successfully built topographical sort using DFS.");

        return sorted;
    }

}
