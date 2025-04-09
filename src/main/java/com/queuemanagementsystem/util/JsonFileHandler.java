package com.queuemanagementsystem.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase utilitaria para manejar operaciones con archivos JSON utilizando adaptadores de tipo personalizados.
 */
public class JsonFileHandler {
    private static final Gson gson = createGson();

    /**
     * Crea una instancia de Gson configurada con adaptadores personalizados.
     *
     * @return Instancia de Gson configurada.
     */
    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls(); // Opcional, pero útil
        gsonBuilder.disableHtmlEscaping(); // Opcional, mejora la legibilidad

        // Registrar adaptador para LocalDateTime
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

        // Registrar adaptador personalizado para User
        gsonBuilder.registerTypeAdapter(User.class, new UserAdapter());

        return gsonBuilder.create();
    }

    /**
     * Guarda una lista de objetos en un archivo JSON.
     *
     * @param <T> Tipo de los objetos en la lista
     * @param list Lista de objetos a guardar
     * @param filePath Ruta del archivo JSON
     * @return true si la operación fue exitosa, false en caso contrario
     */
    public static <T> boolean saveToFile(List<T> list, String filePath) {
        try {
            // Crear directorios principales si no existen
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(list, writer);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en el archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga una lista de objetos desde un archivo JSON.
     *
     * @param <T> Tipo de los objetos en la lista
     * @param filePath Ruta del archivo JSON
     * @param type Tipo de la lista (por ejemplo, new TypeToken<List<User>>(){}.getType())
     * @return Lista de objetos cargados del archivo, o una lista vacía si el archivo no existe
     */
    public static <T> List<T> loadFromFile(String filePath, Type type) {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException | JsonParseException e) {
            System.err.println("Error al cargar desde el archivo: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Adaptador personalizado para deserializar instancias de User.
     */
    public static class UserAdapter implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Determinar el tipo basado en campos específicos
            if (jsonObject.has("accessLevel")) {
                return context.deserialize(jsonObject, Administrator.class);
            } else if (jsonObject.has("availabilityStatus") || jsonObject.has("attendedTickets")) {
                return context.deserialize(jsonObject, Employee.class);
            } else {
                return context.deserialize(jsonObject, User.class);
            }
        }
    }

    /**
     * Adaptador personalizado para LocalDateTime.
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}