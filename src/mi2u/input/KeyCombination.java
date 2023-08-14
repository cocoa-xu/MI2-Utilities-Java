package mi2u.input;

import arc.input.KeyCode;
import arc.util.Log;

public class KeyCombination {
    public KeyCode key;
    public KeyCode modifier;
    public boolean hasModifier;

    public KeyCombination() {}
    public KeyCombination(KeyCode code) {
        this.key = code;
        this.modifier = null;
        this.hasModifier = false;
    }
    public KeyCombination(KeyCode code, KeyCode m) {
        this.key = code;
        this.modifier = m;
        this.hasModifier = true;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        KeyCombination key = (KeyCombination)o;
        return this.key == key.key
                && this.modifier == key.modifier
                && this.hasModifier == key.hasModifier;
    }

    @Override
    public String toString() {
        if (!this.hasModifier) {
            return this.key.toString();
        } else {
            return this.modifier.toString() + "+" + this.key.toString();
        }
    }

    public static KeyCombination fromString(String string) {
        KeyCombination key = new KeyCombination();
        String[] combinations = string.split("\\+");
        try {
            if (combinations.length == 2) {
                key.hasModifier = true;
                key.modifier = KeyCode.valueOf(combinations[0]);
                key.key = KeyCode.valueOf(combinations[1]);
            } else if (combinations.length == 1) {
                key.hasModifier = false;
                key.key = KeyCode.valueOf(combinations[0]);
            } else {
                Log.err("Invalid KeyCombination: "+ string);
                return null;
            }
            return key;
        } catch (Exception e) {
            Log.err("Invalid KeyCombination: " + string);
            return null;
        }
    }
}
