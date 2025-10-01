package me.bottdev.breezeapi.commons.structures.graph.directed;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.commons.structures.graph.Graph;
import me.bottdev.breezeapi.commons.structures.graph.GraphEdge;
import me.bottdev.breezeapi.commons.structures.graph.GraphNode;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Getter
@Setter
public class DirectedGraph<T> implements Graph<T> {

    private final List<GraphNode<T>> nodes = new ArrayList<>();
    private final List<GraphEdge<T>> edges = new ArrayList<>();

    public Optional<GraphNode<T>> getNodeWithLeastEdges() {
        return nodes.stream()
                .min(Comparator.comparingInt(node -> getOutgoingNeighbors(node).size()));
    }


    @Override
    public void addNode(T value, Map<String, Object> attributes) {
        if (hasNode(value)) return;
        GraphNode<T> node = new GraphNode<>(this, value, attributes);
        nodes.add(node);
    }

    @Override
    public void removeNode(T value) {
        if (!hasNode(value)) return;
        nodes.removeIf(node -> node.getValue().equals(value));
    }

    @Override
    public void addEdge(T from, T to) {

        Optional<GraphNode<T>> fromNodeOptional = getFirstNodeByValue(from);
        Optional<GraphNode<T>> toNodeOptional = getFirstNodeByValue(to);

        if (fromNodeOptional.isEmpty() || toNodeOptional.isEmpty()) return;

        GraphNode<T> fromNode = fromNodeOptional.get();
        GraphNode<T> toNode = toNodeOptional.get();

        DirectedGraphEdge<T> edge = new DirectedGraphEdge<>(fromNode, toNode);

        edges.add(edge);
    }

    @Override
    public void removeEdge(T from, T to) {
        if (!hasEdge(from, to)) return;
        edges.removeIf(edge ->
                edge.getFrom().getValue().equals(from) &&
                        edge.getTo().getValue().equals(to)
        );
    }

    @Override
    public List<GraphNode<T>> getNeighbors(T value) {
        Optional<GraphNode<T>> nodeOptional = getFirstNodeByValue(value);
        if (nodeOptional.isEmpty()) return new ArrayList<>();
        GraphNode<T> node = nodeOptional.get();
        return getNeighbors(node);
    }

    @Override
    public List<GraphNode<T>> getNeighbors(GraphNode<T> node) {
        return edges.stream()
                .filter(e -> e.getFrom().equals(node) || e.getTo().equals(node))
                .map(e -> e.getFrom().equals(node) ? e.getTo() : e.getFrom())
                .toList();
    }

    public List<GraphNode<T>> getOutgoingNeighbors(GraphNode<T> node) {
        return edges.stream()
                .filter(e -> e.getFrom().equals(node))
                .map(GraphEdge::getTo)
                .toList();
    }

    public List<GraphNode<T>> getIncomingNeighbors(GraphNode<T> node) {
        return edges.stream()
                .filter(e -> e.getTo().equals(node))
                .map(GraphEdge::getFrom)
                .toList();
    }

    public List<GraphNode<T>> getDirectionalNeighbors(GraphNode<T> node) {
        return edges.stream()
                .flatMap(edge -> {
                    if (((DirectedGraphEdge<T>)edge).isDoubleSided()) {
                        if (edge.getFrom().equals(node)) {
                            return Stream.of(edge.getTo());
                        } else if (edge.getTo().equals(node)) {
                            return Stream.of(edge.getFrom());
                        }
                    } else {
                        if (edge.getFrom().equals(node)) {
                            return Stream.of(edge.getTo());
                        }
                    }
                    return Stream.empty();
                })
                .toList();
    }


    @Override
    public void dfs(GraphNode<T> startNode, Consumer<GraphNode<T>> joinHandler) {

        List<GraphNode<T>> visitedNodes = new ArrayList<>();
        dfsRecursive(startNode, visitedNodes, joinHandler);

    }

    private void dfsRecursive(GraphNode<T> node, List<GraphNode<T>> visitedNodes, Consumer<GraphNode<T>> joinHandler) {

        if (visitedNodes.contains(node)) return;

        visitedNodes.add(node);
        joinHandler.accept(node);
        List<GraphNode<T>> neighbours = getNeighbors(node);

        for (GraphNode<T> neighbour : neighbours) {
            dfsRecursive(neighbour, visitedNodes, joinHandler);
        }

    }

    public boolean hasCycle() {
        Set<GraphNode<T>> visited = new HashSet<>();
        Set<GraphNode<T>> visiting = new HashSet<>();

        for (GraphNode<T> node : nodes) {
            if (!visited.contains(node)) {
                if (dfsDetectCycle(node, visited, visiting)) {
                    return true; // нашли цикл
                }
            }
        }
        return false; // циклов нет
    }

    private boolean dfsDetectCycle(GraphNode<T> node, Set<GraphNode<T>> visited, Set<GraphNode<T>> visiting) {
        visiting.add(node);

        for (GraphNode<T> neighbor : getOutgoingNeighbors(node)) {
            if (visiting.contains(neighbor)) {
                return true; // цикл найден
            }
            if (!visited.contains(neighbor)) {
                if (dfsDetectCycle(neighbor, visited, visiting)) {
                    return true;
                }
            }
        }

        visiting.remove(node);
        visited.add(node);
        return false;
    }

}
