package best.aps.carpetaddition.command;

import best.aps.carpetaddition.CarpetAPSAddition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public final class SpectatorReturnPoints {
    private static final int CURRENT_VERSION = 1;
    private static final String SPECTATOR = "spectator";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private SpectatorReturnPoints() {
    }

    private record ReturnPoint(ServerLevel world, double x, double y, double z, float yaw, float pitch, GameType gameMode) {
    }

    public static boolean has(MinecraftServer server, ServerPlayer player) {
        return Files.isRegularFile(file(server, player));
    }

    public static boolean save(MinecraftServer server, ServerPlayer player) {
        JsonObject json = new JsonObject();
        json.addProperty("data_version", CURRENT_VERSION);
        JsonObject pos = new JsonObject();
        pos.addProperty("x", player.getX());
        pos.addProperty("y", player.getY());
        pos.addProperty("z", player.getZ());
        json.add("pos", pos);
        JsonObject direction = new JsonObject();
        direction.addProperty("yaw", player.getYRot());
        direction.addProperty("pitch", player.getXRot());
        json.add("direction", direction);
        json.addProperty("dimension", player.level().dimension().identifier().toString());
        json.addProperty("game_mode", player.gameMode().getSerializedName());
        Path file = file(server, player);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(json), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            CarpetAPSAddition.LOGGER.warn("Unable to write the location information of {} to the file normally", name(player), e);
            return false;
        }
    }

    public static boolean restore(MinecraftServer server, ServerPlayer player) {
        Path file = file(server, player);
        if (!Files.isRegularFile(file)) {
            return false;
        }

        ReturnPoint point = read(server, file, player);
        if (point == null) {
            return false;
        }
        player.teleportTo(point.world(), point.x(), point.y(), point.z(), Set.of(), point.yaw(), point.pitch(), true);
        player.setGameMode(point.gameMode());
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            CarpetAPSAddition.LOGGER.warn("Unable to delete the location information of {}", name(player), e);
        }
        return true;
    }

    public static void onPlayerLoggedIn(MinecraftServer server, ServerPlayer player) {
        if (player.isSpectator() || !has(server, player)) {
            return;
        }
        restore(server, player);
    }

    private static ReturnPoint read(MinecraftServer server, Path file, ServerPlayer player) {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            JsonObject pos = json.getAsJsonObject("pos");
            double x = pos.get("x").getAsDouble();
            double y = pos.get("y").getAsDouble();
            double z = pos.get("z").getAsDouble();
            JsonObject direction = json.getAsJsonObject("direction");
            float yaw = direction.get("yaw").getAsFloat();
            float pitch = direction.get("pitch").getAsFloat();
            String dimension = json.get("dimension").getAsString();
            ServerLevel world = server.getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(dimension)));
            if (world == null) {
                throw new IllegalStateException("Unknown dimension: " + dimension);
            }
            GameType gameMode = GameType.byName(json.get("game_mode").getAsString(), GameType.SURVIVAL);
            return new ReturnPoint(world, x, y, z, yaw, pitch, gameMode);
        } catch (IOException | RuntimeException e) {
            CarpetAPSAddition.LOGGER.warn("Unable to read the location information of {} normally: {}", name(player), file, e);
            return null;
        }
    }

    private static Path file(MinecraftServer server, ServerPlayer player) {
        return server.getWorldPath(LevelResource.ROOT)
                .resolve("config")
                .resolve(CarpetAPSAddition.MOD_ID)
                .resolve(SPECTATOR)
                .resolve(player.getStringUUID() + ".json");
    }

    private static String name(ServerPlayer player) {
        return player.getGameProfile().name();
    }
}
