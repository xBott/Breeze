package me.bottdev.breezeapi.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.*;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ObjectNode {

    public static ObjectNode fromJsonNode(JsonNode jsonNode) {
        if (jsonNode == null) return null;

        ObjectNode.ObjectNodeBuilder builder = ObjectNode.builder();

        if (jsonNode.isObject()) {

            Iterator<String> fieldKeys = jsonNode.fieldNames();

            while (fieldKeys.hasNext()) {
                String key = fieldKeys.next();
                JsonNode nextNode = jsonNode.get(key);
                ObjectNode newObjectNode = ObjectNode.fromJsonNode(nextNode);
                newObjectNode.setName(key);
                builder.child(newObjectNode);
            }

        } else if (jsonNode.isArray()) {
            ArrayNode array = (ArrayNode) jsonNode;
            ArrayList<Object> objects = new ArrayList<>();
            for (JsonNode element : array) {
                Object value = getNodeValue(element);
                objects.add(value);
            }
            builder.value(objects);

        } else {
            Object value = getNodeValue(jsonNode);
            builder.value(value);
        }

        return builder.build();
    }

    private static Object getNodeValue(JsonNode node) {
        if (node.isTextual()) return node.asText();
        if (node.isInt()) return node.asInt();
        if (node.isLong()) return node.asLong();
        if (node.isDouble()) return node.asDouble();
        if (node.isBoolean()) return node.asBoolean();
        return node.asText();
    }


    private String name = null;
    private Object value = null;
    @Singular
    private List<ObjectNode> children = new ArrayList<>();

    public Optional<ObjectNode> getChild(String name) {
        return children.stream().filter(child -> child.name.equals(name)).findFirst();
    }
}
