package com.shade.platform.ui.editors.lazy;

import com.shade.platform.ui.editors.Editor;
import com.shade.platform.ui.editors.EditorInput;
import com.shade.platform.ui.editors.EditorProvider;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.swing.*;

public class LazyEditorProvider implements EditorProvider {
    @NotNull
    @Override
    public Editor createEditor(@NotNull EditorInput input) {
        return new LazyEditor((LazyEditorInput) input);
    }

    @Override
    public boolean supports(@NotNull EditorInput input) {
        return input instanceof LazyEditorInput;
    }

    @NotNull
    @Override
    public String getName() {
        return "Lazy Editor";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return UIManager.getIcon("Tree.leafIcon");
    }
}
