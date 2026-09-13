package best.aps.carpetaddition.config;

import best.aps.carpetaddition.CarpetAPSAddition;
import best.aps.carpetaddition.command.SpectatorCommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class CommandAliasConfig {
    private static final String COMMAND_ALIASES = "command_aliases";
    private static final Pattern VALID_ALIAS = Pattern.compile("[a-zA-Z0-9_.-]+");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, List<String>> DEFAULTS = defaults();
    private static final Map<String, List<String>> ALIASES = new HashMap<>(DEFAULTS);

    private CommandAliasConfig() {
    }

    private static Map<String, List<String>> defaults() {
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put(SpectatorCommand.NAME, List.of("spec"));
        return Collections.unmodifiableMap(map);
    }

    public static void load() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve(CarpetAPSAddition.MOD_ID + ".json");
        if (!Files.isRegularFile(file)) {
            write(file);
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json != null && json.get(COMMAND_ALIASES) instanceof JsonObject section) {
                for (Map.Entry<String, JsonElement> entry : section.entrySet()) {
                    ALIASES.put(entry.getKey(), parse(entry.getKey(), entry.getValue()));
                }
            }
        } catch (IOException | RuntimeException e) {
            CarpetAPSAddition.LOGGER.warn("Unable to read the config file, using default command aliases", e);
        }
    }

    private static List<String> parse(String command, JsonElement element) {
        List<String> aliases = new ArrayList<>();
        List<JsonElement> elements = switch (element) {
            case JsonArray array -> array.asList();
            case JsonPrimitive primitive -> List.of(primitive);
            case null, default -> List.of();
        };
        for (JsonElement value : elements) {
            if (value instanceof JsonPrimitive primitive && primitive.isString()) {
                String alias = primitive.getAsString();
                if (VALID_ALIAS.matcher(alias).matches() && !alias.equals(command) && !aliases.contains(alias)) {
                    aliases.add(alias);
                    continue;
                }
            }
            CarpetAPSAddition.LOGGER.warn("Ignoring invalid alias {} of command /{}", value, command);
        }
        return aliases;
    }

    private static void write(Path file) {
        JsonObject section = new JsonObject();
        DEFAULTS.forEach((command, aliases) -> {
            JsonArray array = new JsonArray();
            aliases.forEach(array::add);
            section.add(command, array);
        });
        JsonObject json = new JsonObject();
        json.add(COMMAND_ALIASES, section);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(json), StandardCharsets.UTF_8);
        } catch (IOException e) {
            CarpetAPSAddition.LOGGER.warn("Unable to create the config file", e);
        }
    }

    public static List<String> aliasesOf(String command) {
        return ALIASES.getOrDefault(command, List.of());
    }
}
