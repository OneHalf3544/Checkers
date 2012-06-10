package ru.javatalks.checkers.gui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.gui.language.L10nBundle;
import ru.javatalks.checkers.logic.CheckerStepException;
import ru.javatalks.checkers.logic.ChessBoardListener;
import ru.javatalks.checkers.logic.ChessBoardModel;
import ru.javatalks.checkers.logic.CheckerStepException.Cause;
import ru.javatalks.checkers.model.*;

import javax.annotation.PostConstruct;

/**
 * Listener of game steps. Write log to game window.
 */
@Service
public class StepLogger implements ChessBoardListener {

    @Autowired
    private Dialog dialog;

    @Autowired
    private L10nBundle bundle;

    @Autowired
    private ChessBoardModel chessBoardModel;

    private final StringBuilder stringBuilder = new StringBuilder();

    @PostConstruct
    private void registerSelf() {
        chessBoardModel.addListener(this);
    }

    @Override
    public void boardChanged() {}

    @Override
    public void moved(StepDescription step) {
        addStepToLog(step, bundle.getString(step.getPlayer() == Player.USER
                ? "stepUserText" : "stepCompText"));
        dialog.setText(getText());
    }

    /**
     * Add step description to log
     * @param step recorded step
     * @param playerText message code of current user
     */
    private void addStepToLog(StepDescription step, String playerText) {
        stringBuilder.append(playerText).append('\n');
        stringBuilder.append(step).append('\n');
    }

    /**
     * Get log content (used for write result to gui)
     * @return log content
     */
    public String getText() {
        return stringBuilder.toString();
    }

    /**
     * Clear log content
     */
    public void clear() {
        stringBuilder.setLength(0);
    }

    /**
     * Add error description to game log
     * @param exception
     */
    public void logErrorCause(CheckerStepException exception) {
        Cause cause = exception.getCauseOfError();
        switch (cause) {
            case WRONG_STEP:
                stringBuilder.append(bundle.getString("wrongNextCellText"));
                break;
            case MUST_FIGHT:
                stringBuilder.append(bundle.getString("userMustFightText"));
                break;
        }
        stringBuilder.append('\n');
        dialog.setText(getText());
    }
}
