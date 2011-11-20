package ru.javatalks.checkers.gui.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.gui.L10nBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Date: 12.11.11
 * Time: 21:14
 *
 * @author OneHalf
 */
@Component
public class RulesAction extends AbstractAction {

    @Autowired
    private L10nBundle bundle;

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,
                bundle.getString("rulesText"),
                bundle.getString("rulesTitle"), JOptionPane.INFORMATION_MESSAGE);
    }
}
