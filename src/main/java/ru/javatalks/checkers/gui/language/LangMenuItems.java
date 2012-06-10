package ru.javatalks.checkers.gui.language;

import org.apache.log4j.Logger;
import ru.javatalks.checkers.gui.Dialog;
import ru.javatalks.checkers.gui.actions.ChangeLangAction;

import javax.swing.*;
import java.util.Arrays;

/**
 * <p/>
 * <p/>
 * Created: 06.05.12 13:46
 * <p/>
 *
 * @author OneHalf
 */
public class LangMenuItems extends JMenu {

    private static final Logger log = Logger.getLogger(LangMenuItems.class);

    public LangMenuItems(L10nBundle l10nBundle, Dialog dialog) {
        log.debug("add lang menu items: " + Arrays.toString(l10nBundle.getLanguages()));

        ButtonGroup buttonGroup = new ButtonGroup();
        for (Language language : l10nBundle.getLanguages()) {
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(new ChangeLangAction(dialog, language));
            buttonGroup.add(menuItem);
            buttonGroup.setSelected(menuItem.getModel(), language == l10nBundle.getCurrentLanguage());
            this.add(menuItem);
        }
    }
}
