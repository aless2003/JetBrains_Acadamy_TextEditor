package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {
    static Path lastLoadedFile;
    static Matcher matcher;
    static int matchCounter;
    static boolean secondOrBigger = false;
    JFileChooser jFileChooser = new JFileChooser();

    public TextEditor() {
        //Actual Window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setVisible(true);
        setTitle("Text Editor");

        //UserFeld zum Eintippen
        JTextArea TextField = new JTextArea();
        TextField.setName("TextArea");
        //Scrolling für das Textfeld
        JScrollPane ScrollPane = new JScrollPane(TextField);
        ScrollPane.setName("ScrollPane");
        ScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        ScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        //Hinzufügen zum Fenster
        add(ScrollPane, BorderLayout.CENTER);
        //Filename eintragen


        //Menu
        JMenuBar menuBar = new JMenuBar();
        //add Menubar to Window
        setJMenuBar(menuBar);

        //Acutal File Menu
        JMenu menu = new JMenu("File");
        menu.setName("MenuFile");
        menu.setMnemonic(KeyEvent.VK_F1);
        menuBar.add(menu);

        //MenuItems


        //saveItem
        JMenuItem save = new JMenuItem("Save");
        save.setName("MenuSave");
        save.addActionListener(actionEvent -> save(TextField));
        menu.add(save);

        //loadItem
        JMenuItem open = new JMenuItem("Open");
        open.setName("MenuOpen");
        open.addActionListener(actionEvent -> open(TextField));
        menu.add(open);

        //Seperator
        menu.add(new JSeparator());

        //exitItem
        JMenuItem exit = new JMenuItem("exit");
        exit.setName("MenuExit");
        exit.addActionListener(actionEvent -> {
            dispose();
            System.exit(0);
        });
        menu.add(exit);

        //Icons
        ImageIcon openIcon = new ImageIcon("./Text Editor/task/src/resources/openIcon.png");
        ImageIcon saveIcon = new ImageIcon("./Text Editor/task/src/resources/saveIcon.png");
        ImageIcon searchIcon = new ImageIcon("./Text Editor/task/src/resources/searchIcon.png");
        ImageIcon nextIcon = new ImageIcon("./Text Editor/task/src/resources/nextIcon.png");
        ImageIcon prevIcon = new ImageIcon("./Text Editor/task/src/resources/previousIcon.png");

        //Save and Open Buttons
        JButton SaveButton = new JButton(saveIcon);
        JButton OpenButton = new JButton(openIcon);
        SaveButton.setName("SaveButton");
        OpenButton.setName("OpenButton");

        //Search

        JTextField Search = new JTextField(30);
        JButton StartSearchButton = new JButton(searchIcon);
        JButton NextMatchButton = new JButton(nextIcon);
        JButton PreviousMatchButton = new JButton(prevIcon);
        JCheckBox UseRegExCheckbox = new JCheckBox("Use Regex?");
        UseRegExCheckbox.setName("UseRegExCheckbox");
        StartSearchButton.setName("StartSearchButton");
        NextMatchButton.setName("NextMatchButton");
        PreviousMatchButton.setName("PreviousMatchButton");
        Search.setName("SearchField");
        //Formatierung
        JPanel x = new JPanel();
        //Logik der Knöpfe

        SaveButton.addActionListener(actionEvent -> {
            save(TextField);
        });

        OpenButton.addActionListener(actionEvent -> {
            open(TextField);
        });

        StartSearchButton.addActionListener(actionEvent -> {
            startSearch(TextField, Search, UseRegExCheckbox);
        });

        NextMatchButton.addActionListener(actionEvent -> {
            matchNext(TextField);
        });

        PreviousMatchButton.addActionListener(actionEvent -> {
            matchPrev(TextField);
        });


        //Search Menu
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_F2);
        menuBar.add(searchMenu);

        //Start Search Menu
        JMenuItem startSearchMenu = new JMenuItem("Start search");
        startSearchMenu.setName("MenuStartSearch");
        startSearchMenu.addActionListener(actionEvent -> startSearch(TextField, Search, UseRegExCheckbox));
        searchMenu.add(startSearchMenu);

        //Next Match Menu
        JMenuItem nextMatchMenu = new JMenuItem("next Match");
        nextMatchMenu.setName("MenuNextMatch");
        nextMatchMenu.addActionListener(actionEvent -> matchNext(TextField));
        searchMenu.add(nextMatchMenu);

        //Previous Match Menu
        JMenuItem prevMatchMenu = new JMenuItem("previous Match");
        prevMatchMenu.setName("MenuPreviousMatch");
        prevMatchMenu.addActionListener(actionEvent -> matchPrev(TextField));
        searchMenu.add(prevMatchMenu);

        //Use RegExMenu
        JMenuItem RegExMenu = new JMenuItem("Use RegEx");
        RegExMenu.setName("MenuUseRegExp");
        RegExMenu.addActionListener(actionEvent -> {
            UseRegExCheckbox.setSelected(true);
        });
        searchMenu.add(RegExMenu);


        jFileChooser.setName("FileChooser");
        jFileChooser.setVisible(false);

        //hinzufügen zum FormatierungsPanel
        x.add(SaveButton);
        x.add(OpenButton);
        x.add(Search);
        x.add(StartSearchButton);
        x.add(PreviousMatchButton);
        x.add(NextMatchButton);
        x.add(UseRegExCheckbox);
        x.add(jFileChooser);
        //Formatierungspanel zum Fenster hinzufügen
        add(x, BorderLayout.NORTH);


    }

    private void matchNext(JTextArea textField) {
        String temp = textField.getText();
        int index = 0;
        String foundString = null;
        if (matcher.find()) {
            foundString = matcher.group();

            if (!secondOrBigger) {
                index = temp.indexOf(foundString);
                secondOrBigger = true;
            } else {
                int startPoint = matcher.start();
                if (matcher.find(startPoint)) {
                    index = temp.indexOf(foundString, startPoint);
                }
            }
            textField.setCaretPosition(index + foundString.length());
            textField.select(index, index + foundString.length());
            textField.grabFocus();
        } else if (matcher.find(0)) {
            foundString = matcher.group();
            index = temp.indexOf(foundString);
            textField.setCaretPosition(index + foundString.length());
            textField.select(index, index + foundString.length());
            textField.grabFocus();
        }
    }


    //TODO Previous Match machen -> fix Tab or Space at beginning of File
    private void matchPrev(JTextArea textField) {
        String temp = textField.getText();
        int index = 0;
        int endPoint = matcher.start();
        String foundString = matcher.group();
        int counter = 0;
        if (endPoint != 0) {
            if (matcher.find(0)) {
                int tempIndex = 0;
                int firstIndex = matcher.start();
                counter = 0;
                if (firstIndex == endPoint) {
                    while (matcher.find()) {
                        index = matcher.start();
                    }
                    matcher.find(index);
                } else {
                    do {
                        if (counter != 0) {
                            if (matcher.find()) {
                                tempIndex = matcher.start();
                                if (tempIndex < endPoint) {
                                    index = tempIndex;
                                }
                            }
                        } else {
                            counter++;
                            tempIndex = matcher.start();
                            if (tempIndex < endPoint) {
                                index = tempIndex;
                            }
                        }
                    } while (tempIndex < endPoint);
                }

                matcher.find(index);
            }
        } else {
            while (matcher.find()) {
                index = matcher.start();
            }
            matcher.find(index);
        }
        textField.setCaretPosition(index + foundString.length());
        textField.select(index, index + foundString.length());
        textField.grabFocus();
    }


    /*private void matchPrev(JTextArea textField) {
        String temp = textField.getText();
        String foundString = matcher.group();
        int index = temp.indexOf(foundString);
        if (index - 1 < 0) {
            index = 1;
        }
        if (matcher.find(index - 1)) {
            temp = textField.getText();
            foundString = matcher.group();
            index = temp.indexOf(foundString);
            for (int i = 1; i < matchCounter; i++) {
                index = temp.indexOf(foundString, index + 1);
            }
            } else if (matchCounter < 1) {
                matchCounter++;
                while (matcher.find()) {
                    matchCounter++;
                    index = temp.indexOf(foundString ,index + 1);
                }
                matcher.find(0);
            }

            textField.setCaretPosition(index + foundString.length());
            textField.select(index, index + foundString.length());
            textField.grabFocus();
        }
*/


    private void startSearch(JTextArea textField, JTextField search, JCheckBox useRegExCheckbox) {
        if (useRegExCheckbox.isSelected()) {
            Thread searchThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    matchCounter = 0;
                    Pattern searchPattern = Pattern.compile(search.getText());
                    matcher = searchPattern.matcher(textField.getText());
                    matchNext(textField);
                }
            });
            searchThread.setDaemon(true);
            searchThread.start();
        } else {
            Thread searchThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    matchCounter = 0;
                    String tempString = search.getText();
                    String[] forbidden = {"\\", ".", "^", "[", "]", "$", "-", "(", ")", "{", "}", "|", "?", "*", "+", "!"};
                    String replacement;
                    for (String curForb : forbidden) {
                        replacement = "\\" + curForb;
                        tempString = tempString.replace(curForb, replacement);
                    }
                    Pattern searchPattern = Pattern.compile(tempString);
                    matcher = searchPattern.matcher(textField.getText());
                    matchNext(textField);
                }
            });
            searchThread.setDaemon(true);
            searchThread.start();
        }
    }


    private void open(JTextArea textField) {
        jFileChooser.setVisible(true);
        if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File selectedFile = jFileChooser.getSelectedFile();
            try {
                lastLoadedFile = Paths.get(selectedFile.getAbsolutePath());

                // deepcode ignore ReplaceBoxedConstructor~java.lang.String: Not working int this case
                textField.setText(new String(Files.readAllBytes(lastLoadedFile)));
            } catch (IOException e) {
                System.out.println("Warning: Error in the open Function!");
                textField.setText("");
            }
        }
        jFileChooser.setVisible(false);
    }

    private void save(JTextArea textField) {
        jFileChooser.setVisible(true);
        if (false) {
            File saveFile = lastLoadedFile.toFile();
            try (PrintWriter writer = new PrintWriter(saveFile);) {
                writer.write(textField.getText());
            } catch (FileNotFoundException e) {
                System.out.println("Error: File not found");
            }
        } else {
            if (jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jFileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(selectedFile)) {
                    lastLoadedFile = Paths.get(selectedFile.getAbsolutePath());
                    writer.write(textField.getText());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        jFileChooser.setVisible(false);
    }
}
