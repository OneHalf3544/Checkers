package ru.javatalks.checkers.actions;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javatalks.checkers.Dialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Date: 12.11.11
 * Time: 21:14
 *
 * @author OneHalf
 */
public class NewGameAction extends AbstractAction {

    @Autowired
    private Dialog dialog;

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.restartGame();
    }
}
