package com.downn_falls.manager;

import com.downn_falls.PaymentBot;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class YamlManager {
    public static <T> T getConfig(String keyPath, Class<T> clazz) {
        // Read from YAML file

        File file = new File("config.yml");
        if (file.exists() && file.isFile()) {
            try (Reader reader = new FileReader("config.yml")) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(reader);

                // Traverse the YAML structure using keyPath
                String[] keys = keyPath.split("\\.");
                Object value = traverseYaml(data, keys);

                // Print the value if found
                if (value != null) {
                    return clazz.cast(value);
                } else {
                    return getResourceConfig(keyPath, clazz);
                }
            } catch (IOException e) {
                return getResourceConfig(keyPath, clazz);
            }
        } else {
            return getResourceConfig(keyPath, clazz);
        }
    }

    public static Set<String> keySet(String path) {
        File file = new File("config.yml");
        if (file.exists() && file.isFile()) {
            try (FileReader reader = new FileReader("config.yml")) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(reader);
                if (data != null) {

                    String[] keys = path.split("\\.");
                    Object result = traverseYaml(data, keys);

                    if (result instanceof Map) {
                        return ((Map<?, ?>) result).keySet().stream().map(Object::toString).collect(Collectors.toSet());
                    } else {
                        return getResourceKeySet(path);
                    }
                } else {
                    return getResourceKeySet(path);
                }
            } catch (IOException e) {
                return getResourceKeySet(path);
            }
        } else {
            return getResourceKeySet(path);
        }
    }

    private static Set<String> getResourceKeySet(String path) {
        try (InputStream inputStream = PaymentBot.class.getResourceAsStream("config.yml")) {

            if (inputStream == null) return null;

            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            if (data != null) {

                String[] keys = path.split("\\.");
                Object result = traverseYaml(data, keys);

                if (result instanceof Map) {
                    return ((Map<?, ?>) result).keySet().stream().map(Object::toString).collect(Collectors.toSet());
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static <T> T getResourceConfig(String keyPath, Class<T> clazz) {
        try (InputStream inputStream = PaymentBot.class.getResourceAsStream("config.yml")) {
            if (inputStream == null) return null;
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);

            // Traverse the YAML structure using keyPath
            String[] keys = keyPath.split("\\.");
            Object value = traverseYaml(data, keys);

            // Print the value if found
            if (value != null) {
                return clazz.cast(value);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }


    public static void saveDefaultConfig() {

        File file = new File("config.yml");
        if (file.exists() && file.isFile()) return;

        // Read YAML from resource
        try (InputStream inputStream = PaymentBot.class.getClassLoader().getResourceAsStream("config.yml");
             OutputStream outputStream = new FileOutputStream("config.yml")) {
            if (inputStream == null) {
                return;
            }

            // Copy YAML to destination
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Object traverseYaml(Map<String, Object> data, String[] keys) {
        Map<String, Object> current = data;
        for (String key : keys) {
            if (current.containsKey(key)) {
                Object value = current.get(key);
                if (value instanceof Map) {
                    current = (Map<String, Object>) value;
                } else {
                    return value;
                }
            } else {
                return null;
            }
        }
        return null;
    }
}
