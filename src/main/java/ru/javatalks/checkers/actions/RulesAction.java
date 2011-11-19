package ru.javatalks.checkers.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.L10nBundleBundle;

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
public class RulesAction extends AbstractAction {

    @Autowired
    L10nBundleBundle bundle;

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,
                bundle.getString("rulesText"),
                bundle.getString("rulesTitle"), JOptionPane.INFORMATION_MESSAGE);
    }
}
