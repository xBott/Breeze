package me.bottdev.breezeapi.commons.structures.graph;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GraphNode<T> {

    private Graph<T> graph;

    private T value;

    private Map<String, Object> attributes;

    @Override
    public boolean equals(Object o) {
        if (o instanceof GraphNode<?> other) {
            return other.getValue().equals(getValue());
        }
        return false;
    }

}
