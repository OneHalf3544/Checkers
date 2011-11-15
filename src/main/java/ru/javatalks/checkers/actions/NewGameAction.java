package ru.javatalks.checkers.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.Dialog;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Date: 12.11.11
 * Time: 21:14
 *
 * @author OneHalf
 */
@Component
public class NewGameAction extends AbstractAction {

    @Autowired
    private Dialog dialog;

    @Autowired
    ResourceBundle bundle;

    @PostConstruct
    public void initialise() {
        putValue(NAME, bundle.getString("newGameTitle"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.restartGame();
    }
}
