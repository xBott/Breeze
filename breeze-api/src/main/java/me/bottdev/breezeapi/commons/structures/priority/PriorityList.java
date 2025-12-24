package me.bottdev.breezeapi.commons.structures.priority;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class PriorityList<T> implements Iterable<T> {
    
    private final List<PriorityWrapper<T>> wrappers;
    
    public PriorityList() {
        this.wrappers = new ArrayList<>();
    }

    public void add(T value, int priority) {
        PriorityWrapper<T> wrapper = new PriorityWrapper<>(value, priority);
        
        int index = 0;
        while (index < wrappers.size() && wrappers.get(index).getPriority() <= priority) {
            index++;
        }
        
        wrappers.add(index, wrapper);
    }

    public T get(int index) {
        return wrappers.get(index).getValue();
    }

    public PriorityWrapper<T> getWrapper(int index) {
        return wrappers.get(index);
    }

    public T remove(int index) {
        return wrappers.remove(index).getValue();
    }

    public boolean remove(T value) {
        for (int i = 0; i < wrappers.size(); i++) {
            if (wrappers.get(i).getValue().equals(value)) {
                wrappers.remove(i);
                return true;
            }
        }
        return false;
    }

    public int size() {
        return wrappers.size();
    }

    public boolean isEmpty() {
        return wrappers.isEmpty();
    }

    public void clear() {
        wrappers.clear();
    }
    
    public boolean contains(T value) {
        for (PriorityWrapper<T> wrapper : wrappers) {
            if (wrapper.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public List<T> getValues() {
        List<T> values = new ArrayList<>(wrappers.size());
        for (PriorityWrapper<T> wrapper : wrappers) {
            values.add(wrapper.getValue());
        }
        return values;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < wrappers.size();
            }

            @Override
            public T next() {
                return wrappers.get(currentIndex++).getValue();
            }

            @Override
            public void remove() {
                wrappers.remove(--currentIndex);
            }
        };
    }

    public Iterator<PriorityWrapper<T>> wrapperIterator() {
        return wrappers.iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PriorityList[");
        for (int i = 0; i < wrappers.size(); i++) {
            PriorityWrapper<T> wrapper = wrappers.get(i);
            sb.append(wrapper.getValue())
              .append("(p:")
              .append(wrapper.getPriority())
              .append(")");
            if (i < wrappers.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public Stream<T> stream() {
        return wrappers.stream().map(PriorityWrapper::getValue);
    }

    public Stream<T> parallelStream() {
        return wrappers.parallelStream().map(PriorityWrapper::getValue);
    }

    public Stream<PriorityWrapper<T>> wrapperStream() {
        return wrappers.stream();
    }

    public Stream<PriorityWrapper<T>> parallelWrapperStream() {
        return wrappers.parallelStream();
    }

}