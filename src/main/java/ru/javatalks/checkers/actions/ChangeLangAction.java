package ru.javatalks.checkers.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.Language;
import ru.javatalks.checkers.Dialog;

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

    private Dialog dialog;

    public ChangeLangAction(Dialog dialog, Language language) {
        super(language.getNameForMenu());
        this.language = language;
        this.dialog = dialog;
    }

    public void actionPerformed(ActionEvent e) {
        dialog.setLanguage(language);
    }
}
