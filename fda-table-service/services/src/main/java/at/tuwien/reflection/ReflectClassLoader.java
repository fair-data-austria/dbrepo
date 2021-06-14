package at.tuwien.reflection;

import at.tuwien.exception.FileStorageException;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Custom class loader
 * TODO: improvements possible: cache the bytecode instead of compile it everytime
 */
@Log4j2
public class ReflectClassLoader extends URLClassLoader {

    private final byte[] bytecode;
    private static final String className = "Table";
    private static final String classNameFull = "at.tuwien.userdb." + className;

    public ReflectClassLoader(byte[] bytecode) {
        super(new URL[0], ReflectClassLoader.class.getClassLoader());
        this.bytecode = bytecode;
    }

    public static Class<?> load(String code) throws ClassNotFoundException, FileStorageException {
        final byte[] bytecode = ReflectCompiler.compile(className, code);
        final ReflectClassLoader loader = new ReflectClassLoader(bytecode);
        return loader.loadClass(classNameFull);
    }

    public Class<?> find(String name) throws ClassNotFoundException {
        if (bytecode == null) {
            return super.findClass(name);
        }
        return defineClass(name, bytecode, 0, bytecode.length);
    }

}
