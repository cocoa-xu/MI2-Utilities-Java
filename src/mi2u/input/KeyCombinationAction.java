package mi2u.input;

import mi2u.io.MI2USettings;

public interface KeyCombinationAction {
    void execute();
    KeyCombinationAction kDisableBuildings = () -> {
        boolean disableBuilding = MI2USettings.getBool("disableBuilding");
        MI2USettings.putBool("disableBuilding",!disableBuilding);
    };

    KeyCombinationAction kNop = () -> {};
}
