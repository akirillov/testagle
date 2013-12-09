package io.testagle.core.logic;

import io.testagle.api.LoadTest;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestLoader {
    public LoadTest uploadTestJarFromBytes(byte[] jarBytes, String className) throws ClassNotFoundException, IOException {

        URI uri = writeFile(jarBytes);

        ClassLoader loader = null;

        try {
            loader = URLClassLoader.newInstance(
                    new URL[]{uri.toURL()},
                    getClass().getClassLoader()
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Class<?> clazz = Class.forName(className, true, loader);
        Class<? extends LoadTest> testClass = clazz.asSubclass(LoadTest.class);

        // Avoid Class.newInstance, for it is evil.
        Constructor<? extends LoadTest> ctor = null;

        try {
            ctor = testClass.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        LoadTest testInstance = null;
        try {
            testInstance = ctor.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return testInstance;
    }

    public URI writeFile(byte[] bytes) throws IOException {
        return writeFile(bytes, "/tmp/test.jar");
    }

    public URI writeFile(byte[] bytes, String path) throws IOException {
        return Files.write(Paths.get(path), bytes).toUri();
    }
}
