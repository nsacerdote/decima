package com.shade.decima.ui.data.handlers;

import com.shade.decima.model.rtti.RTTIType;
import com.shade.decima.model.rtti.objects.RTTIObject;
import com.shade.decima.model.rtti.path.PathElement;
import com.shade.decima.model.rtti.path.PathElementName;
import com.shade.decima.model.rtti.types.RTTITypeClass;
import com.shade.decima.ui.data.registry.Type;
import com.shade.decima.ui.data.ValueHandlerCollection;
import com.shade.decima.ui.data.registry.ValueHandlerRegistration;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.swing.*;
import java.util.Collection;

@ValueHandlerRegistration(value = @Type(type = RTTITypeClass.class), order = 1000)
public class ObjectValueHandler implements ValueHandlerCollection<RTTIObject, RTTITypeClass.Member> {
    @Nullable
    @Override
    public Decorator getDecorator(@NotNull RTTIType<?> type) {
        return null;
    }

    @NotNull
    @Override
    public Collection<RTTITypeClass.Member> getChildren(@NotNull RTTIType<?> type, @NotNull RTTIObject object) {
        return object.getMembers().keySet();
    }

    @NotNull
    @Override
    public String getChildName(@NotNull RTTIType<?> type, @NotNull RTTIObject object, @NotNull RTTITypeClass.Member member) {
        return member.name();
    }

    @NotNull
    @Override
    public Object getChildValue(@NotNull RTTIType<?> type, @NotNull RTTIObject object, @NotNull RTTITypeClass.Member member) {
        return object.get(member);
    }

    @NotNull
    @Override
    public RTTIType<?> getChildType(@NotNull RTTIType<?> type, @NotNull RTTIObject object, @NotNull RTTITypeClass.Member member) {
        return member.type();
    }

    @NotNull
    @Override
    public PathElement getChildElement(@NotNull RTTIType<?> type, @NotNull RTTIObject object, @NotNull RTTITypeClass.Member member) {
        return new PathElementName(member);
    }

    @Nullable
    @Override
    public Icon getIcon(@NotNull RTTIType<?> type) {
        return UIManager.getIcon("CoreEditor.objectIcon");
    }
}
