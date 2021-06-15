package at.tuwien.reflect;

import org.joor.Reflect;
import org.springframework.stereotype.Component;

@Component
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
