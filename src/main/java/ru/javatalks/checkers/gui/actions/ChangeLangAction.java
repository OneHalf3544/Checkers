package ru.javatalks.checkers.gui.actions;

import ru.javatalks.checkers.gui.Dialog;
import ru.javatalks.checkers.gui.language.Language;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Date: 12.11.11
 * Time: 21:15
 *
 * @author OneHalf
 */
public class ChangeLangAction extends AbstractAction {

    private final Language language;

    private final Dialog dialog;

    public ChangeLangAction(Dialog dialog, Language language) {
        super(language.getNameForMenu());
        this.language = language;
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.setLanguage(language);
    }
}
