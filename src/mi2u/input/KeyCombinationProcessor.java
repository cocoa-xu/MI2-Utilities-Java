package mi2u.input;

import arc.Core;
import arc.input.InputProcessor;
import arc.input.KeyCode;
import arc.util.Log;
import mi2u.io.MI2USettings;

import java.util.HashMap;

public class KeyCombinationProcessor implements InputProcessor {
    private static KeyCombinationProcessor INSTANCE;
    private final HashMap<KeyCombination, KeyCombinationAction> combinations = new HashMap<>();
    private KeyCombinationProcessor() {}

    public static KeyCombinationProcessor getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new KeyCombinationProcessor();
        }
        return INSTANCE;
    }

    public static void init() {
        KeyCombinationProcessor processor = getInstance();

        // TODO: 从设置里读出来所有的快捷键
        processor.setKeyCombination(new KeyCombination(KeyCode.c, KeyCode.controlLeft), () -> {
            Log.info("callback executed");
            boolean disableBuilding = MI2USettings.getBool("disableBuilding");
            MI2USettings.putBool("disableBuilding",!disableBuilding);
        });

        Core.input.getInputMultiplexer().addProcessor(0, processor);
    }

    public void setKeyCombination(KeyCombination key, KeyCombinationAction action) {
        this.combinations.put(key, action);
    }

    public void removeKeyCombination(KeyCombination key) {
        this.combinations.remove(key);
    }

    @Override
    public boolean keyDown(KeyCode keycode) {
        KeyCombination key = new KeyCombination(keycode);

        if (Core.input.keyDown(KeyCode.controlLeft)) {
            key.modifier = KeyCode.controlLeft;
            key.hasModifier = true;
        }

        Log.info("keyDown hashCode: " + key.hashCode() + ", <" + key.toString() + ">");
        Log.info("this.combinations.size()=" + this.combinations.size());

        KeyCombinationAction action = this.combinations.get(key);

        if(action != null){
            action.execute();
            return true;
        } else if(keycode == KeyCode.c){
            Log.info("hello orld");
            return true;
        }
        return false;
    }
}
