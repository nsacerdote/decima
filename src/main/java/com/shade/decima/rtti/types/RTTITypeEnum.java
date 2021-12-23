package com.shade.decima.rtti.types;

import com.shade.decima.rtti.RTTIType;
import com.shade.decima.rtti.RTTITypeRegistry;
import com.shade.decima.util.NotNull;

import java.nio.ByteBuffer;
import java.util.Map;

public class RTTITypeEnum<T> implements RTTIType<String> {
    private final RTTIType<T> type;
    private final Map<T, String> members;

    public RTTITypeEnum(@NotNull RTTIType<T> type, @NotNull Map<T, String> members) {
        this.type = type;
        this.members = members;
    }

    @NotNull
    @Override
    public String read(@NotNull ByteBuffer buffer) {
        final T value = type.read(buffer);
        final String member = members.get(value);
        if (member == null) {
            throw new IllegalArgumentException("Enum '" + this + "' does not have a member with ordinal '" + value + "'");
        }
        return member;
    }

    @Override
    public void write(@NotNull ByteBuffer buffer, @NotNull String value) {
        for (Map.Entry<T, String> entry : members.entrySet()) {
            if (entry.getValue().equals(value)) {
                type.write(buffer, entry.getKey());
                return;
            }
        }
        throw new IllegalArgumentException("Enum '" + this + "' does not have a member called '" + value + "'");
    }


    @NotNull
    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String toString() {
        return RTTITypeRegistry.getName(this);
    }
}
