package me.bottdev.breezeapi.commons.structures.graph.directed;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.bottdev.breezeapi.commons.structures.graph.GraphEdge;
import me.bottdev.breezeapi.commons.structures.graph.GraphNode;

@Getter
@Setter
@RequiredArgsConstructor
public class DirectedGraphEdge<T> implements GraphEdge<T> {

    private final GraphNode<T> from;
    private final GraphNode<T> to;

    private boolean doubleSided = false;

}
