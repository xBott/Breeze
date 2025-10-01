package me.bottdev.breezeapi.commons.structures.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface Graph<T> {

    List<GraphNode<T>> getNodes();

    List<GraphEdge<T>> getEdges();

    default Optional<GraphNode<T>> getFirstNodeByValue(T value) {
        return getNodes()
                .stream()
                .filter(node -> node.getValue().equals(value))
                .findFirst();
    }

    default boolean hasNode(T value) {
        return getFirstNodeByValue(value).isPresent();
    }

    default void addNode(T value) {
        addNode(value, new HashMap<>());
    }

    void addNode(T value, Map<String, Object> attributes);

    void removeNode(T value);

    default Optional<GraphEdge<T>> getFirstEdgeByValues(T from, T to) {
        return getEdges()
                .stream()
                .filter(edge ->
                        edge.getFrom().getValue().equals(from) &&
                        edge.getTo().getValue().equals(to)
                )
                .findFirst();
    }

    default boolean hasEdge(T from, T to) {
        return getFirstEdgeByValues(from, to).isPresent();
    }

    void addEdge(T from, T to);

    void removeEdge(T from, T to);

    List<GraphNode<T>> getNeighbors(T value);

    List<GraphNode<T>> getNeighbors(GraphNode<T> node);

    default void dfs(T startValue, Consumer<GraphNode<T>> joinHandler) {
        Optional<GraphNode<T>> startNodeOptional = getFirstNodeByValue(startValue);
        if (startNodeOptional.isEmpty()) return;
        dfs(startNodeOptional.get(), joinHandler);
    }

    default void dfsFromFirst(Consumer<GraphNode<T>> joinHandler) {
        GraphNode<T> startNode = getNodes().getFirst();
        dfs(startNode.getValue(), joinHandler);
    }

    void dfs(GraphNode<T> startNode, Consumer<GraphNode<T>> joinHandler);

}
