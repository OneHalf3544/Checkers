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
public class AboutAction extends AbstractAction {

    @Autowired
    private L10nBundleBundle bundle;

    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,
                bundle.getString("aboutDeveloperText"),
                bundle.getString("aboutTitle"), JOptionPane.INFORMATION_MESSAGE);
    }
}
