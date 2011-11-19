package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.*;

import javax.annotation.PostConstruct;

@Service
public class StepLogger implements ChessBoardListener {

    @Autowired
    private Dialog dialog;

    @Autowired
    private L10nBundleBundle bundle;

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
        if (step.getPlayer() == Player.USER) {
            userStep(step);
        }
        else {
            compStep(step);
        }
        dialog.setText(getText());
    }

    private void userStep(StepDescription step) {
        stringBuilder.append(bundle.getString("stepUserText")).append('\n');
        stringBuilder.append(step).append('\n');

//        stringBuilder.append(bundle.getString("userHasFighterText")).append(from.getIndex()).append("\n");
//        stringBuilder.append(bundle.getString("userMustFightText")).append('\n');
//        stringBuilder.append(bundle.getString("wrongNextCellText")).append('\n');
    }

    private void compStep(StepDescription step) {
        stringBuilder.append(bundle.getString("stepCompText")).append('\n');
        stringBuilder.append(step).append('\n');
    }

    public String getText() {
        return stringBuilder.toString();
    }

    public void clear() {
        stringBuilder.setLength(0);
    }
}
