package me.bottdev.breezeapi.commons.structures.graph;

public interface GraphEdge<T> {

    GraphNode<T> getFrom();

    GraphNode<T> getTo();

}
