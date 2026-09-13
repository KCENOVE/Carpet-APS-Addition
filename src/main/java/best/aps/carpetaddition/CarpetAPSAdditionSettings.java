package best.aps.carpetaddition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;

public class CarpetAPSAdditionSettings {
    public static final String APS = "APS";

    private CarpetAPSAdditionSettings() {
    }

    /**
     * 易碎深板岩
     */
    @Rule(categories = {APS, RuleCategory.SURVIVAL})
    public static boolean softDeepslate = false;

    /**
     * 抑制方块破坏位置不匹配警告
     */
    @Rule(categories = {APS, RuleCategory.EXPERIMENTAL})
    public static boolean suppressionMismatchInDestroyBlockPosWarn = false;
}
