package ru.javatalks.checkers.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.javatalks.checkers.L10nBundleBundle;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Date: 12.11.11
 * Time: 21:13
 *
 * @author OneHalf
 */
@Component
public class ExitAction extends AbstractAction {

    @Autowired
    L10nBundleBundle bundle;

    @PostConstruct
    public void initialise() {
        putValue(NAME, bundle.getString("exitTitle"));
    }

    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
