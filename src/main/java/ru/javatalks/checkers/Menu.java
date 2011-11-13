package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javatalks.checkers.actions.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * This class provides GUI
 * 
 * @author Kapellan
 */
public class Menu {

    @Autowired
    ResourceBundle bundle;

    ChessBoard chessBoard = new ChessBoard();
    Logic logic = new Logic(chessBoard, this);
    ActionChessBoard act = new ActionChessBoard(chessBoard, logic);
    String resultBuf;
    String stepUserText;
    String stepCompText;
    String userHasFighterText;
    String userMustFightText;
    String wrongNextCellText;
    JTextArea tArea = new JTextArea(26, 12);
    private Language langFlag = Language.RUSSIAN;

    private JFrame frame = new JFrame();
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menuGame = new JMenu();
    private JMenu menuSettings = new JMenu();
    private JMenu itemLanguage = new JMenu();
    private JCheckBoxMenuItem cbRusLang = new JCheckBoxMenuItem(new ChangeLangAction(Language.RUSSIAN));
    private JCheckBoxMenuItem cbEngLang = new JCheckBoxMenuItem(new ChangeLangAction(Language.ENGLISH));
    private JCheckBoxMenuItem cbUkrLang = new JCheckBoxMenuItem(new ChangeLangAction(Language.UKRAINIAN));
    private JMenu menuHelp = new JMenu();
    private JMenuItem itemNewGame = new JMenuItem(new NewGameAction());
    private JMenuItem itemExit = new JMenuItem(new ExitAction());
    private JMenuItem itemRules = new JMenuItem(new RulesAction());
    private JMenuItem itemAbout = new JMenuItem(new AboutAction());
    private JLabel labelComp = new JLabel();
    private JLabel labelUser = new JLabel();
    private JScrollPane scrollPane = new JScrollPane(tArea);
    private JPanel resultPanel = new JPanel();
    private BoxLayout boxL = new BoxLayout(resultPanel, BoxLayout.Y_AXIS);
    private JPanel mainPanel = new JPanel(new FlowLayout());

    private void setGui() {
        setLanguage(langFlag);
        tArea.append(stepUserText + "\n");
        
        chessBoard.addMouseListener(act);

        menuSettings.add(itemLanguage);

        itemLanguage.add(cbRusLang);
        itemLanguage.add(cbEngLang);
        itemLanguage.add(cbUkrLang);

        menuHelp.add(itemRules);
        menuHelp.add(itemAbout);
        menuBar.add(menuGame);

        menuBar.add(menuSettings);
        menuBar.add(menuHelp);

        tArea.setFont(new Font("Dialog", Font.PLAIN, 12));
        tArea.setLineWrap(true);
        tArea.setWrapStyleWord(true);
        tArea.setEditable(false);

        labelUser.setText(bundle.getString("labelUserTitle") + chessBoard.userCheckers);
        labelComp.setText(bundle.getString("labelCompTitle") + chessBoard.compCheckers);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        scrollPane.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        labelUser.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        labelComp.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        resultPanel.setLayout(boxL);
        resultPanel.add(labelUser);
        resultPanel.add(labelComp);
        resultPanel.add(Box.createVerticalStrut(10));
        resultPanel.add(scrollPane);
        resultPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(chessBoard);
        mainPanel.add(resultPanel);

        frame.setFocusable(true);
        frame.getRootPane().setOpaque(true);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(menuBar, BorderLayout.NORTH);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(700, 500));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void setLanguage(Language lang) {
        langFlag = lang;

        if (langFlag == Language.RUSSIAN) {
            cbEngLang.setSelected(false);
            cbUkrLang.setSelected(false);
        }
        if (langFlag == Language.ENGLISH) {
            cbRusLang.setSelected(false);
            cbUkrLang.setSelected(false);
        }
        if (langFlag == Language.UKRAINIAN) {
            cbEngLang.setSelected(false);
            cbRusLang.setSelected(false);
        }
        
        frame.setTitle(bundle.getString("frameTitle"));
        menuGame.setText(bundle.getString("gameTitle"));
        menuSettings.setText(bundle.getString("settingsTitle"));
        itemLanguage.setText(bundle.getString("languageTitle"));
        menuHelp.setText(bundle.getString("helpTitle"));
        itemNewGame.setText(bundle.getString("newGameTitle"));
        itemExit.setText(bundle.getString("exitTitle"));
        itemRules.setText(bundle.getString("rulesTitle"));
        itemAbout.setText(bundle.getString("aboutTitle"));
        labelComp.setText(bundle.getString("labelCompTitle") + chessBoard.compCheckers);
        labelUser.setText(bundle.getString("labelUserTitle") + chessBoard.userCheckers);
    }

    void customResult() {
        labelUser.setText(bundle.getString("labelUserTitle") + chessBoard.userCheckers);
        labelComp.setText(bundle.getString("labelCompTitle") + chessBoard.compCheckers);
        String optionsDialog[] = {bundle.getString("dialogNewGame"), bundle.getString("dialogExit")};

        if (chessBoard.compCheckers == 0) {
            int userChoice = JOptionPane.showOptionDialog(null,
                    bundle.getString("noCompCheckersText"),
                    bundle.getString("userWon"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsDialog, optionsDialog[0]);
            if (userChoice == JOptionPane.YES_OPTION) {
                restartGame();
            }
            
            if (userChoice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            return;
        }
        if (chessBoard.userCheckers == 0) {
            logic.nextStepCompFlag = false;
            int userChoice = JOptionPane.showOptionDialog(null,
                    bundle.getString("noUserCheckersText"),
                    bundle.getString("userLost"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsDialog, optionsDialog[0]);
            if (userChoice == JOptionPane.YES_OPTION) {
                restartGame();
            }
            
            if (userChoice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            return;
        }
        if (logic.getCompFighter().isEmpty()
                && logic.getCompStepper().isEmpty()
                && chessBoard.compCheckers != 0) {
            logic.nextStepCompFlag = false;
            int userChoice = JOptionPane.showOptionDialog(null,
                    bundle.getString("compIsBlockedText"),
                    bundle.getString("userWon"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsDialog, optionsDialog[0]);
            if (userChoice == JOptionPane.YES_OPTION) {
                restartGame();
            }
            if (userChoice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            return;
        }
        if (logic.getUserFighter().isEmpty()
                && logic.getUserStepper().isEmpty()
                && chessBoard.userCheckers != 0) {
            int userChoice = JOptionPane.showOptionDialog(null,
                    bundle.getString("userIsBlockedText"),
                    bundle.getString("userLost"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsDialog, optionsDialog[0]);
            if (userChoice == JOptionPane.YES_OPTION) {
                restartGame();
            }
            if (userChoice == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            return;
        }
        tArea.append(resultBuf);
        resultBuf = "";
        tArea.setCaretPosition(tArea.getDocument().getLength());
    }

    public void restartGame() {
        main(langFlag.name());
        frame.dispose();
    }

    private Menu(String langFlag) {
        this.langFlag = Language.getByCode(langFlag);
    }


    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignore) {
        }
        
        if (args.length > 0) {
            new Menu(args[0]).setGui();
        }
        else {
            new Menu("russian").setGui();
        }
    }
}
