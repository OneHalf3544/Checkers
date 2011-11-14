package ru.javatalks.checkers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.javatalks.checkers.actions.*;
import ru.javatalks.checkers.model.ChessBoardModel;
import ru.javatalks.checkers.model.Player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.swing.*;

/**
 * This class provides GUI
 * 
 * @author Kapellan
 */
@Component
public class Dialog {

    @Autowired
    private ResourceBundle bundle;

    @Autowired
    private ChessBoardModel boardModel;

    @Autowired
    private ChessBoard chessBoardPainter;

    private Painter painter = new Painter(this);

    private ActionChessBoard act = new ActionChessBoard(chessBoardPainter, painter);
    
    private String resultBuf;
    private JTextArea tArea = new JTextArea(26, 12);
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

    @PostConstruct
    public void runDialog() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignore) {
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initializeComponent();
            }
        });
    }

    private void initializeComponent() {
        setLanguage(langFlag);
        tArea.append(bundle.getString("stepUserText") + '\n');

        chessBoardPainter.addMouseListener(act);

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

        labelUser.setText(bundle.getString("labelUserTitle") + boardModel.getUserCheckerNumber());
        labelComp.setText(bundle.getString("labelCompTitle") + boardModel.getCompCheckerNumber());
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

        mainPanel.add(chessBoardPainter);
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
        labelComp.setText(bundle.getString("labelCompTitle") + boardModel.getCompCheckerNumber());
        labelUser.setText(bundle.getString("labelUserTitle") + boardModel.getUserCheckerNumber());
    }

    void customResult() {
        labelUser.setText(bundle.getString("labelUserTitle") + boardModel.getUserCheckerNumber());
        labelComp.setText(bundle.getString("labelCompTitle") + boardModel.getCompCheckerNumber());
        String[] optionsDialog = {bundle.getString("dialogNewGame"), bundle.getString("dialogExit")};

        if (!boardModel.hasCheckerOf(Player.OPPONENT)) {
            notifyAboutGameEnd(optionsDialog, "userWon", "noCompCheckersText");
            return;
        }

        if (!boardModel.hasCheckerOf(Player.USER)) {
            notifyAboutGameEnd(optionsDialog, "userLost", "noUserCheckersText");
            return;
        }

        if (boardModel.canStep(Player.OPPONENT)) {
            notifyAboutGameEnd(optionsDialog, "userWon", "compIsBlockedText");
            return;
        }

        if (boardModel.canStep(Player.USER)) {
            notifyAboutGameEnd(optionsDialog, "userLost", "userIsBlockedText");
            return;
        }

        tArea.append(resultBuf);
        resultBuf = "";
        tArea.setCaretPosition(tArea.getDocument().getLength());
    }

    private void notifyAboutGameEnd(String[] optionsDialog, String titleKey, String textKey) {
        int userChoice = JOptionPane.showOptionDialog(null,
                bundle.getString(textKey),
                bundle.getString(titleKey),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, optionsDialog, optionsDialog[0]);
        if (userChoice == JOptionPane.YES_OPTION) {
            restartGame();
        }

        if (userChoice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public void restartGame() {
        runDialog();
        frame.dispose();
    }
}
