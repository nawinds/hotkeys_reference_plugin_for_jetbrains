package com.nawinds.hotkeys_reference;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class HotKeysAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Create a dialog with the hotkeys table
        HotKeysDialog dialog = new HotKeysDialog();
        dialog.show();
    }
}
