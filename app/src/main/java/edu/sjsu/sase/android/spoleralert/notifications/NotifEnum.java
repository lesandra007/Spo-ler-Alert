package edu.sjsu.sase.android.spoleralert.notifications;

import androidx.annotation.NonNull;

public enum NotifEnum {
    DAYS("days before"),
    WEEKS("weeks before"),
    MONTHS("months before");

    private final String displayName;

    NotifEnum(String displayName) {
        this.displayName = displayName;
    }

    public static NotifEnum fromString(String text) {
        for (NotifEnum e : NotifEnum.values()) {
            if (e.displayName.equalsIgnoreCase(text)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No matching enum found for: " + text);
    }


    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
