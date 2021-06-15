package at.tuwien.reflect;

import org.joor.Reflect;

public class ClassLoader<T> {

    private Reflect reflect;

    public T compile(String name, String code) {
        reflect = Reflect.compile(name, code)
                .create();
        return getInstance();
    }

    public T getInstance() {
        return reflect.get();
    }

}
