package mi2u.input;

import arc.Core;
import arc.input.InputProcessor;
import arc.input.KeyCode;
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

    private boolean isTypingKeyCombinations = false;
    private KeyCombinationListener keyCombinationListener = null;

    // TODO: 添加支持快捷键的设置

    public enum KeyCombinationSettings {
        disableBuildings("keyCombinations.disableBuildings");

        KeyCombinationSettings(String name) {
            this.name = name;
            this.defaultAction = KeyCombinationActionDefault.defaultActionFor(this.name);
        }

        public final String name;
        public final KeyCombinationActionDefault defaultAction;
        public static final KeyCombinationSettings[] all = {
            disableBuildings
        };
    }

    public static void init() {
        KeyCombinationProcessor processor = getInstance();

        for (KeyCombinationSettings s : KeyCombinationSettings.all) {
            processor.setKeyCombinationActionDefault(s.defaultAction);
        }

        Core.input.getInputMultiplexer().addProcessor(0, processor);
    }

    public static class KeyCombinationActionDefault {
        KeyCombination key;
        KeyCombinationAction action;
        public static KeyCombinationActionDefault defaultActionFor(String name){
            KeyCombinationActionDefault d = new KeyCombinationActionDefault();
            d.action = KeyCombinationAction.kNop;
            d.key = MI2USettings.getKeyCombination(name);

            if(d.key != null){
                if(name.equals("keyCombinations.disableBuildings")){
                    d.action = KeyCombinationAction.kDisableBuildings;
                }
            }

            return d;
        }
    }

    public void setKeyCombination(KeyCombination key, KeyCombinationAction action) {
        if(key != null) this.combinations.put(key, action);
    }

    public void setKeyCombinationActionDefault(KeyCombinationActionDefault d) {
        if(d.key != null) setKeyCombination(d.key, d.action);
    }

    public void waitingForKeyCombination(KeyCombinationListener listener) {
        this.isTypingKeyCombinations = true;
        this.keyCombinationListener = listener;
    }

    public void cancelWaitingForKeyCombination() {
        this.isTypingKeyCombinations = false;
        this.keyCombinationListener = null;
    }

    private boolean isModifier(KeyCode keycode){
        return keycode == KeyCode.controlLeft ||
                keycode == KeyCode.controlRight ||
                keycode == KeyCode.altLeft ||
                keycode == KeyCode.altRight;
    }

    private static final KeyCode[] modifiers = {
            KeyCode.controlLeft,
            KeyCode.controlRight,
            KeyCode.altLeft,
            KeyCode.altRight
    };

    @Override
    public boolean keyDown(KeyCode keycode) {
        if (isModifier(keycode)) return false;

        KeyCombination key = new KeyCombination(keycode);
        for (KeyCode m : modifiers) {
            if (Core.input.keyDown(m)) {
                key.modifier = m;
                key.hasModifier = true;
                break;
            }
        }

        if (this.isTypingKeyCombinations) {
            this.isTypingKeyCombinations = false;
            if (this.keyCombinationListener != null) {
                this.keyCombinationListener.run(key);
            }
            this.keyCombinationListener = null;
            return true;
        }

        KeyCombinationAction action = this.combinations.get(key);
        if (action != null) {
            action.execute();
            return true;
        }

        return false;
    }
}
