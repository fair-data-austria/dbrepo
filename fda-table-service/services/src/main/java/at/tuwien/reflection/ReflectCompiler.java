package at.tuwien.reflection;

import at.tuwien.exception.FileStorageException;
import lombok.extern.log4j.Log4j2;
import org.openjdk.btrace.compiler.MemoryJavaFileManager;

import javax.tools.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * https://javamana.com/2021/02/20210208140115286u.html
 */
@Log4j2
public class ReflectCompiler {

    public static byte[] compile(String name, String code) throws FileStorageException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        final JavaFileObject file = new JavaSourceFromString(name, code);
        final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.defaultCharset());
        final MemoryJavaFileManager memoryManager = new MemoryJavaFileManager(manager, List.of());

        final Iterable<? extends JavaFileObject> compilationUnits = Collections.singletonList(file);
        final JavaCompiler.CompilationTask task = compiler.getTask(null, memoryManager, diagnostics, null, null, compilationUnits);

        if (task.call()) {
            log.debug("compiled class {} successfully, keys {}", name, memoryManager.getClassBytes().keySet());
            log.trace("class {} with code {}", name, code);
            return memoryManager.getClassBytes().get(name);
        }
        log.warn("compiler task ended unsuccessful: {}", diagnostics.getDiagnostics());
        throw new FileStorageException("not able to compile");
    }

    static class JavaSourceFromString extends SimpleJavaFileObject {

        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

}
