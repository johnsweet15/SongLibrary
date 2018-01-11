import static org.junit.Assert.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.junit.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyDialog;
import edu.cnu.cs.gooey.GooeyFrame;

public class SongLibraryTest {

    @Test
    public void testLayoutAboutandExit() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                SongLibrary.main(new String[] {});
            }

            @Override
            public void test(JFrame frame) {
                JMenuBar menu = Gooey.getMenuBar(frame);
                JMenu sl = Gooey.getMenu(menu, "SongLibrary");
                JMenuItem about = Gooey.getMenu(sl, "About...");

                JButton add = Gooey.getButton(frame, "Add");
                assertTrue(add.isEnabled());
                JButton delete = Gooey.getButton(frame, "Delete");
                assertFalse(delete.isEnabled());

                JTable table = Gooey.getComponent(frame, JTable.class);
                assertTrue(table.getRowCount() < 1);

                assertEquals(frame.getTitle(), "SongLibrary");

                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        about.doClick();
                    }

                    @Override
                    public void test(JDialog dialog) {
                        assertEquals("Incorrect title", "About...",
                                dialog.getTitle());
                        assertTrue(dialog.isShowing());
                        JButton ok = Gooey.getButton(dialog, "OK");
                        ok.doClick();

                        JMenuBar menubar = Gooey.getMenuBar(frame);
                        JMenu program = Gooey.getMenu(menubar, "SongLibrary");
                        JMenuItem exit = Gooey.getMenu(program, "Exit");

                        Gooey.capture(new GooeyDialog() {
                            @Override
                            public void invoke() {
                                exit.doClick();
                            }

                            @Override
                            public void test(JDialog dialog) {
                                assertEquals("Incorrect title", "Confirm Quit",
                                        dialog.getTitle());
                                Gooey.getLabel(dialog, "Do you want to exit?");
                                assertTrue(dialog.isShowing());
                                JButton yes = Gooey.getButton(dialog, "Yes");
                                yes.doClick();
                                assertFalse(dialog.isShowing());
                                JButton no = Gooey.getButton(dialog, "No");
                                no.doClick();

                            }
                        });

                        Gooey.capture(new GooeyDialog() {
                            @Override
                            public void invoke() {
                                exit.doClick();
                            }

                            @Override
                            public void test(JDialog dialog) {
                                JButton cancel = Gooey.getButton(dialog,
                                        "Cancel");
                                cancel.doClick();
                                assertFalse(dialog.isShowing());
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void testAdd() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                SongLibrary.main(new String[] {});
            }

            @Override
            public void test(JFrame frame) {
                JButton add = Gooey.getButton(frame, "Add");
                add.doClick();
                JTable table = Gooey.getComponent(frame, JTable.class);
                assertTrue(table.getRowCount() >= 1);
            }
        });
    }

    @Test
    public void testDelete() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                SongLibrary.main(new String[] {});
            }

            @Override
            public void test(JFrame frame) {
                JButton delete = Gooey.getButton(frame, "Delete");
                JTable table = Gooey.getComponent(frame, JTable.class);
                int current = table.getRowCount();
                int selected = table.getSelectedRowCount();
                delete.doClick();
                assertEquals(current - selected, table.getRowCount());
            }
        });
    }

    @Test
    public void testOpen() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                SongLibrary.main(new String[] {});
            }

            @Override
            public void test(JFrame frame) {
                JMenuBar menu = Gooey.getMenuBar(frame);
                JMenu table = Gooey.getMenu(menu, "Table");
                JMenuItem open = Gooey.getMenu(table, "Open...");

                JTable tbl = Gooey.getComponent(frame, JTable.class);
                File file = new File("src/hello11.txt");

                Gooey.capture(new GooeyDialog() {

                    @Override
                    public void invoke() {
                        open.doClick();
                    }

                    @Override
                    public void test(JDialog d) {
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    JFileChooser choose = Gooey.getComponent(d,
                                            JFileChooser.class);
                                    choose.setSelectedFile(file);
                                    choose.approveSelection();
                                }
                            });
                        }
                        catch (InvocationTargetException
                                | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                TableModel tbl2 = tbl.getModel();
                assertEquals(2, tbl2.getRowCount());

            }
        });
    }

    
    @Test
    public void testSave() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                SongLibrary.main(new String[] {});
            }

            @Override
            public void test(JFrame frame) {
                JMenuBar menu = Gooey.getMenuBar(frame);
                JMenu table = Gooey.getMenu(menu, "Table");
                JMenuItem save = Gooey.getMenu(table, "Save As...");

                JTable tbl = Gooey.getComponent(frame, JTable.class);

                Gooey.capture(new GooeyDialog() {

                    @Override
                    public void invoke() {
                        save.doClick();
                    }

                    @Override
                    public void test(JDialog d) {
                        try {
                            SwingUtilities.invokeAndWait(new Runnable() {
                                @Override
                                public void run() {
                                    JFileChooser choose = Gooey.getComponent(d,
                                            JFileChooser.class);
                                    choose.setSelectedFile(new File("src/hello10.txt"));
                                    choose.approveSelection();
                                }
                            });
                        }
                        catch (InvocationTargetException
                                | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    
    
    @Test
    public void testNew() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                SongLibrary.main(new String[] {});
            }

            @Override
            public void test(JFrame frame) {
                JMenuBar menu = Gooey.getMenuBar(frame);
                JMenu table = Gooey.getMenu(menu, "Table");
                JMenuItem new1 = Gooey.getMenu(table, "New");

                JTable tbl = Gooey.getComponent(frame, JTable.class);

                Gooey.capture(new GooeyDialog() {
                    @Override
                    public void invoke() {
                        new1.doClick();
                    }

                    @Override
                    public void test(JDialog dialog) {
                        assertEquals("Incorrect title", "Select an option",
                                dialog.getTitle());
                        Gooey.getLabel(dialog, "Clear all table data?");
                        assertTrue(dialog.isShowing());
                        JButton yes = Gooey.getButton(dialog, "Yes");
                        yes.doClick();
                        assertFalse(dialog.isShowing());
                        JButton no = Gooey.getButton(dialog, "No");
                        no.doClick();
                        assertTrue(tbl.getRowCount() >= 0);
                    }
                });
            }
        });
    }
}
