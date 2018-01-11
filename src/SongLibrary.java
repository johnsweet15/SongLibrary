import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 * A customer wants an application called SongLibrary to store information about
 * songs. Song information include name, artist, album and year of release.
 * Users should be able to add/modify/remove songs, and should be able to
 * save/read information to/from text files.
 * 
 * @author John Sweet
 * @version 03.16.17
 *
 */
public class SongLibrary extends JFrame {

    private String[] HEADERS = { "Song", "Artist", "Album", "Year" };
    private String[] emptyArray = { "", "", "", "" };

    private Object[][] DATA = {};

    private JTable table;
    private DefaultTableModel defaultTable;

    private JMenuBar bar;
    private JMenu songMenu;
    private JMenu tableMenu;
    private JMenuItem about;
    private JMenuItem exit;
    private JMenuItem save;
    private JMenuItem new1;

    private JButton deleteButton;
    private JButton addButton;

    /**
     * Constructor that creates the SongLibrary Window including the menu, table, and buttons.
     */
    public SongLibrary() {
        super("SongLibrary");

        setLayout(new BorderLayout());

        defaultTable = new DefaultTableModel(DATA, HEADERS);

        bar = new JMenuBar();
        setJMenuBar(bar);

        songMenu = new JMenu("SongLibrary");
        tableMenu = new JMenu("Table");

        bar.add(songMenu);
        bar.add(tableMenu);

        about();
        exit();
        save();

        table = new JTable(DATA, HEADERS);
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
        table.setModel(defaultTable);

        Box box = Box.createVerticalBox();
        addButton = new JButton("Add");
        box.add(addButton, BorderLayout.NORTH);
        delete();
        box.add(deleteButton, BorderLayout.NORTH);

        add();
        add(box, BorderLayout.EAST);

        makeNewTable();
        open();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        checkWindow();
    }

    private void about() {
        about = new JMenuItem("About...");
        songMenu.add(about);
        about.addActionListener(new ActionListener() {
            /**
             * Method called when about action occurs
             */
            public void actionPerformed(ActionEvent event) {
                JOptionPane.showMessageDialog(null,
                        "SongLibrary\nby John Sweet", "About...",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void exit() {
        exit = new JMenuItem("Exit");
        songMenu.addSeparator();
        songMenu.add(exit);
        exit.addActionListener(new ActionListener() {
            /**
             * Method called when about exit occurs
             */
            public void actionPerformed(ActionEvent event) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Do you want to exit?", "Confirm Quit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }

    private void save() {
        save = new JMenuItem("Save As...");
        tableMenu.add(save);
        save.addActionListener(new ActionListener() {
            /**
             * Method called when save action occurs
             */
            public void actionPerformed(ActionEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    /**
                     * Saves current table to a text file
                     */
                    public void run() {
                        JFileChooser chooser = new JFileChooser();
                        int result = chooser.showSaveDialog(SongLibrary.this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            try (FileWriter fw = new FileWriter(
                                    chooser.getSelectedFile() + ".txt")) {
                                for (int i = 0; i < table.getRowCount(); i++) {
                                    for (int j = 0; j < table
                                            .getColumnCount(); j++) {
                                        Object obj = table.getValueAt(i, j);
                                        if (j == table.getColumnCount() - 1) {
                                            fw.write(obj.toString());
                                        }
                                        else {
                                            fw.write(obj.toString() + ",");
                                        }
                                    }
                                    fw.write("\r\n");
                                }
                                setTitle("SongLibrary ["
                                        + file.getAbsolutePath() + "]");

                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
            }
        });
    }

    private void delete() {
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            /**
             * Method called when delete action occurs
             */
            public void actionPerformed(ActionEvent event) {
                if (table.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(null, "No row selected",
                            "Message", JOptionPane.INFORMATION_MESSAGE);
                }

                int[] rowCount = table.getSelectedRows();
                for (int i = 0; i < rowCount.length; i++) {
                    defaultTable.removeRow(rowCount[i] - i);
                }
                isDeleteClickable(deleteButton);
            }
        });
    }

    private void add() {
        addButton.addActionListener(new ActionListener() {
            @Override
            /**
             * Method called when add action occurs
             */
            public void actionPerformed(ActionEvent event) {
                defaultTable.addRow(emptyArray);
                isDeleteClickable(deleteButton);
            }
        });
    }

    private void makeNewTable() {
        new1 = new JMenuItem("New");
        tableMenu.add(new1);
        new1.addActionListener(new ActionListener() {
            /**
             * Method called when new action occurs
             */
            public void actionPerformed(ActionEvent event) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Clear all table data?", "Select an option",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    newTable();
                    isDeleteClickable(deleteButton);
                }
            }
        });
    }

    private void open() {
        JMenuItem open = new JMenuItem("Open...");
        tableMenu.add(open);
        open.addActionListener(new ActionListener() {
            /**
             * Method called when open action occurs
             */
            public void actionPerformed(ActionEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    /**
                     * Opens selected text file and displays in the table
                     */
                    public void run() {
                        JFileChooser chooser = new JFileChooser();
                        int result = chooser.showOpenDialog(SongLibrary.this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            File file = chooser.getSelectedFile();
                            chooser.getSelectedFile();
                            String line;
                            try {
                                BufferedReader reader = new BufferedReader(
                                        new FileReader(file));
                                while ((line = reader.readLine()) != null) {
                                    defaultTable.addRow(line.split(","));
                                }
                                isDeleteClickable(deleteButton);
                                setTitle("SongLibrary ["
                                        + file.getAbsolutePath() + "]");
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
            }
        });
    }

    private void checkWindow() {
        addWindowListener(new WindowListener() {
            /**
             * Method called when window close action occurs
             */
            public void windowClosing(WindowEvent arg0) {
                int result = JOptionPane.showConfirmDialog(null,
                        "Do you want to exit?", "Confirm Quit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }

            /**
             * unused method that had to be implemented
             */
            public void windowActivated(WindowEvent arg0) {
            }

            /**
             * unused method that had to be implemented
             */
            public void windowClosed(WindowEvent arg0) {
            }

            /**
             * unused method that had to be implemented
             */
            public void windowDeactivated(WindowEvent arg0) {
            }

            /**
             * unused method that had to be implemented
             */
            public void windowDeiconified(WindowEvent arg0) {
            }

            /**
             * unused method that had to be implemented
             */
            public void windowIconified(WindowEvent arg0) {
            }

            /**
             * unused method that had to be implemented
             */
            public void windowOpened(WindowEvent arg0) {
            }
        });
    }

    private void newTable() {
        for (int i = 0; i < table.getRowCount(); i++) {
            defaultTable.removeRow(i);
        }
    }

    private void isDeleteClickable(JButton button) {
        if (table.getRowCount() < 1) {
            button.setEnabled(false);
        }
        else {
            button.setEnabled(true);
        }
    }

    /**
     * Runs the frame
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            /**
             * Runs the frame
             */
            public void run() {
                JFrame f = new SongLibrary();
                f.setVisible(true);
            }
        });
    }
}
