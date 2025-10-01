package me.bottdev.breezeapi.commons.structures.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class GraphNode<T> {

    private final Graph<T> graph;

    private T value;

    private Map<String, Object> attributes;

}
