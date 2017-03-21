package gr.ru.processData;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * Created by
 */
public class AttributeMock<T> implements Attribute<T> {

    private T value;

    @Override
    public AttributeKey<T> key() {
        return null;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    @Override
    public T getAndSet(T value) {
        return null;
    }

    @Override
    public T setIfAbsent(T value) {
        return null;
    }

    @Override
    public T getAndRemove() {
        return null;
    }

    @Override
    public boolean compareAndSet(T oldValue, T newValue) {
        return false;
    }

    @Override
    public void remove() {

    }
}
