import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class Multimap<K, V> implements Map<K, List<V>> {
    private final Map<K, List<V>> map = new HashMap<>();
    private int totalSize = 0;

    @Override
    public int size() {
        return totalSize;
    }

    @Override
    public boolean isEmpty() {
        return totalSize == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        for (List<V> values : map.values()) {
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<V> get(Object key) {
        return map.getOrDefault(key, Collections.emptyList());
    }

    @Override
    public List<V> put(K key, List<V> values) {
        List<V> oldValues = map.put(key, new ArrayList<>(values));
        totalSize += values.size();
        if (oldValues != null) {
            totalSize -= oldValues.size();
        }
        return oldValues;
    }

    public void putSingle(K key, V value) {
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        totalSize++;
    }

    @Override
    public List<V> remove(Object key) {
        List<V> removed = map.remove(key);
        if (removed != null) {
            totalSize -= removed.size();
        }
        return removed;
    }

    public boolean removeSingle(K key, V value) {
        List<V> values = map.get(key);
        if (values != null && values.remove(value)) {
            totalSize--;
            if (values.isEmpty()) {
                map.remove(key);
            }
            return true;
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends List<V>> m) {
        for (Map.Entry<? extends K, ? extends List<V>> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
        totalSize = 0;
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<List<V>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}


