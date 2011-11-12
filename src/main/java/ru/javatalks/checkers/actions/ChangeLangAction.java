package ru.javatalks.checkers.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javatalks.checkers.Language;
import ru.javatalks.checkers.Menu;

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

    @Autowired
    private Menu menu;

    public ChangeLangAction(Language language) {
        this.language = language;
    }

    public void actionPerformed(ActionEvent e) {
        menu.setLanguage(language);
    }
}
