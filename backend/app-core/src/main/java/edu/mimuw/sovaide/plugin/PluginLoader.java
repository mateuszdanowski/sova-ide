package edu.mimuw.sovaide.plugin;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import edu.mimuw.sovaide.domain.plugin.PluginSova;
import edu.mimuw.sovaide.domain.repository.ProjectRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PluginLoader {
    private static final String PLUGIN_DIR = "plugins";

    private final ProjectRepository repository;

    @PostConstruct
    public void loadPlugins() {
        File pluginDir = new File(PLUGIN_DIR);
        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            System.out.println("No plugins directory found.");
            return;
        }
        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null) return;
        for (File jar : jars) {
            try {
                loadPluginFromJar(jar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadPluginFromJar(File jarFile) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        try (JarFile jar = new JarFile(jarFile)) {
            URL[] urls = { new URL("jar:file:" + jarFile.getAbsolutePath() + "!/") };
            try (URLClassLoader cl = new URLClassLoader(urls, this.getClass().getClassLoader())) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName().replace('/', '.').replace(".class", "");
                        Class<?> clazz = cl.loadClass(className);
                        if (PluginSova.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                            PluginSova pluginSova = (PluginSova) clazz.getDeclaredConstructor().newInstance();
                            System.out.println("Executing pluginSova: " + className);
                            pluginSova.execute(repository);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
