package at.tuwien.utils;

import at.tuwien.userdb.UserTable;
import org.springframework.stereotype.Component;

@Component
public class HibernateClassLoader extends ClassLoader {

    public Class<? extends UserTable> defineClass(String name, byte[] definition) {
        return (Class<? extends UserTable>) defineClass(name, definition, 0, definition.length);
    }

}
