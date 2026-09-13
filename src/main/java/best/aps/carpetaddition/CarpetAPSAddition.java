package best.aps.carpetaddition;

import best.aps.carpetaddition.config.CommandAliasConfig;
import carpet.CarpetServer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetAPSAddition implements ModInitializer {
    public static final String MOD_ID = "carpet-aps-addition";
    public static final String MOD_NAME = "Carpet APS Addition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        CommandAliasConfig.load();
        CarpetServer.manageExtension(new CarpetAPSAdditionExtension());
    }
}
