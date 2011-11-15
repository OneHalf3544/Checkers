package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.model.Cell;
import ru.javatalks.checkers.model.ChessBoardListener;
import ru.javatalks.checkers.model.ChessBoardModel;
import ru.javatalks.checkers.model.Player;

import javax.annotation.PostConstruct;
import java.util.ResourceBundle;

@Service
public class StepLogger implements ChessBoardListener {

    @Autowired
    private ResourceBundle bundle;

    @Autowired
    private ChessBoardModel chessBoardModel;

    private StringBuilder stringBuilder = new StringBuilder();

    @PostConstruct
    private void registerSelf() {
        chessBoardModel.addListener(this);
    }

    @Override
    public void boardChanged() {}

    @Override
    public void moved(Cell from, Cell to, Cell victimCell, Player player) {
        if (player == Player.USER) {
            userStep(from, to, victimCell);
        }
        else {
            compStep(from, to, victimCell);
        }
    }

    private void userStep(Cell from, Cell to, Cell victimCell) {
        stringBuilder.append(bundle.getString("stepUserText") + '\n');

//        stringBuilder.append(bundle.getString("userHasFighterText")).append(from.getIndex()).append("\n");
//        stringBuilder.append(bundle.getString("userMustFightText")).append('\n');

        if (from.hasUserChecker()) {
            stringBuilder.append(from.getIndex()).append(":").append(to.getIndex()).append("\n");
        } else {
            stringBuilder.append(bundle.getString("wrongNextCellText")).append("\n");
        }
    }

    private void compStep(Cell from, Cell to, Cell victimCell) {
        stringBuilder.append(bundle.getString("stepCompText")).append('\n');
        stringBuilder.append(from.getIndex()).append(':').append('\n');
        stringBuilder.append(from.getIndex()).append(':').append(to.getIndex()).append('\n');
    }
}
