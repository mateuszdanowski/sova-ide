package edu.mimuw.sovaide.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.springframework.stereotype.Service;

import edu.mimuw.sovaide.domain.plugin.PluginSova;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Getter
public class PluginLoader {
    private static final String PLUGIN_DIR = "plugins";

    private final List<PluginSova> loadedPlugins = new ArrayList<>();

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
                log.error("Failed to load plugin from {}: {}", jar.getName(), e.getMessage());
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
                            PluginSova plugin = (PluginSova) clazz.getDeclaredConstructor().newInstance();
                            loadedPlugins.add(plugin);
                            log.info("Loaded plugin: {}", className);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
