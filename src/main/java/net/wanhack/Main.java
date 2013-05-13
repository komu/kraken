/*
 *  Copyright 2005-2006 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AccessControlException;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import net.wanhack.common.Version;
import net.wanhack.model.DefaultGameRef;
import net.wanhack.model.Game;
import net.wanhack.model.GameConfiguration;
import net.wanhack.model.GameRef;
import net.wanhack.model.IGame;
import net.wanhack.model.SimpleQueryCallback;
import net.wanhack.model.common.Direction;
import net.wanhack.model.common.Updateable;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.item.Item;
import net.wanhack.service.ServiceProvider;
import net.wanhack.service.config.ObjectFactory;
import net.wanhack.service.region.RegionLoader;
import net.wanhack.ui.common.ErrorDialog;
import net.wanhack.ui.console.ConsoleView;
import net.wanhack.ui.debug.LogFrame;
import net.wanhack.ui.debug.ScriptFrame;
import net.wanhack.ui.game.InventoryView;
import net.wanhack.ui.game.RegionView;
import net.wanhack.ui.game.StartGameDialog;
import net.wanhack.ui.game.StatisticsView;
import net.wanhack.ui.game.action.GameAction;
import net.wanhack.ui.game.action.GameActionSet;
import net.wanhack.utils.SystemAccess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Main implements Updateable {
    
    private final JFrame frame = new JFrame("Wanhack");
    private final ConsoleView consoleView = new ConsoleView();
    private final InventoryView inventoryView = new InventoryView();
    private final StatisticsView statisticsView = new StatisticsView();
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final RegionLoader regionLoader;
    private DefaultGameRef gameRef;
    private final RegionView regionView = new RegionView();
    private final Log log = LogFactory.getLog(getClass());
    private LogFrame logFrame;
    private ScriptFrame scriptFrame;
    private final boolean wizardMode;
    private JFileChooser fileChooser;
    private final GameActionSet gameActions = new GameActionSet();
    
    public Main(boolean wizardMode) throws Exception {
        JOptionPane.setRootFrame(frame);
        
        objectFactory.parse("/items/items.xml", Item.class, "item");
        objectFactory.parse("/items/weapons.xml", Item.class, "item");
        objectFactory.parse("/creatures/creatures.xml", Creature.class, "creature");
        this.regionLoader = new RegionLoader(objectFactory);

        try {
            fileChooser = new JFileChooser();
        } catch (AccessControlException e) {
            // We're inside WebStart and have no permission for JFileChooser
            fileChooser = null;
        }
        
        ServiceProvider.setConsole(consoleView);
        ServiceProvider.setObjectFactory(objectFactory);
        ServiceProvider.setRegionLoader(regionLoader);
        
        // Initialize the load/save dialog
        if (fileChooser != null) {
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".wh");
                }
            
                @Override
                public String getDescription() {
                    return "Wanhack game files";
                }
            });
        }
        
        this.wizardMode = wizardMode;
        
        if (wizardMode) {
            logFrame = new LogFrame();
        }
        
        initializeMenuBar();
        initializeInputMap();
        initializeActionMap();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(1);
        Container contentPane = frame.getContentPane();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(consoleView, BorderLayout.NORTH);
        mainPanel.add(regionView, BorderLayout.CENTER);
        mainPanel.add(statisticsView, BorderLayout.SOUTH);
        splitPane.setLeftComponent(mainPanel);
        splitPane.setRightComponent(inventoryView);
        
        contentPane.add(splitPane, BorderLayout.CENTER);
     
        frame.pack();
        frame.setLocationByPlatform(true);

        initUncaughtExceptionHandler();
        
        frame.setVisible(true);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        
        startNewGame();
    }
    
    public GameRef getGameRef() {
        return gameRef;
    }
    
    public void exit() {
        int res = JOptionPane.showConfirmDialog(
                frame, "Really exit Wanhack?", 
                "Are you sure?", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void initializeMenuBar() {
        log.debug("Initializing menu bar");
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setMnemonic('g');
        gameMenu.add(new NewGameAction());
        if (fileChooser != null) {
            gameMenu.add(new LoadGameAction());
            gameMenu.add(new SaveGameAction());
        }
        gameMenu.addSeparator();
        gameMenu.add(new ExitAction());
        menuBar.add(gameMenu);
        
        JMenu actionMenu = new JMenu("Action");
        actionMenu.setMnemonic('a');
        for (GameAction action : gameActions.getActions()) {
            actionMenu.add(action);
        }
        menuBar.add(actionMenu);
        
        if (wizardMode) {
            JMenu debugMenu = new JMenu("Debug");
            debugMenu.setMnemonic('d');
            debugMenu.add(new RevealRegionAction());
            debugMenu.add(new ShowLogFrameAction().getMenuItem());
            debugMenu.add(new ShowScriptingFrameAction().getMenuItem());
            
            menuBar.add(debugMenu);
        }
        
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');
        helpMenu.add(new AboutAction());
        menuBar.add(helpMenu);
        
        frame.setJMenuBar(menuBar);
    }
    
    private void initializeInputMap() {
        log.debug("Initializing input map");
        
        // Movement with numlock off 
        
        addInput("UP",          Direction.NORTH);
        addInput("DOWN",        Direction.SOUTH);
        addInput("LEFT",        Direction.WEST);
        addInput("RIGHT",       Direction.EAST);
        addInput("control UP",    "run " + Direction.NORTH);
        addInput("control DOWN",  "run " + Direction.SOUTH);
        addInput("control LEFT",  "run " + Direction.WEST);
        addInput("control RIGHT", "run " + Direction.EAST);
        
        // Movement with numlock on
        addInput("NUMPAD1",   Direction.SW);
        addInput("NUMPAD2",   Direction.SOUTH);
        addInput("NUMPAD3",   Direction.SE);
        addInput("NUMPAD4",   Direction.WEST);
        addInput("NUMPAD6",   Direction.EAST);
        addInput("NUMPAD7",   Direction.NW);
        addInput("NUMPAD8",   Direction.NORTH);
        addInput("NUMPAD9",   Direction.NE);
        addInput("control NUMPAD1",   "run " + Direction.SW);
        addInput("control NUMPAD2",   "run " + Direction.SOUTH);
        addInput("control NUMPAD3",   "run " + Direction.SE);
        addInput("control NUMPAD4",   "run " + Direction.WEST);
        addInput("control NUMPAD6",   "run " + Direction.EAST);
        addInput("control NUMPAD7",   "run " + Direction.NW);
        addInput("control NUMPAD8",   "run " + Direction.NORTH);
        addInput("control NUMPAD9",   "run " + Direction.NE);
        
        // Up/down
        addInput("typed >",   "down");
        addInput("typed <",   "up");
        
        // Other controls
        addInput("NUMPAD5",   "rest");
        addInput("SPACE",     "rest");
        addInput("typed .",   "rest");
        addInput("M",   "map");
        
        // Scrolling history
        addInput("PAGE_UP", "history up");
        addInput("PAGE_DOWN", "history down");
    }
    
    private void initializeActionMap() {
        log.debug("Initializing action map");
        
        ActionMap actionMap = regionView.getActionMap();
        
        for (Direction direction : Direction.values()) {
            actionMap.put(direction, new MovePlayerAction(direction, false));
        }
        
        for (Direction direction : Direction.values()) {
            actionMap.put("run " + direction, new MovePlayerAction(direction, true));
        }
        
        actionMap.put("down", new VerticalMoveAction(false));
        actionMap.put("up",   new VerticalMoveAction(true));
        
        actionMap.put("rest", new SkipTurnAction());
        actionMap.put("history up", new HistoryScrollAction(true));
        actionMap.put("history down", new HistoryScrollAction(false));
        actionMap.put("map",  new MapAction());
    }
    
    private void startNewGame() {
        log.debug("starting new game");
        
        StartGameDialog dialog = new StartGameDialog(frame);
        GameConfiguration config = dialog.showDialog();
        
        if (config != null) {
            try {
                Game game = new Game(config, wizardMode);
                consoleView.clear();
                setGame(game);
                gameRef.getAutolockingGame().start();
                
            } catch (Exception e) {
                log.error("Failed to start new game", e);
                JOptionPane.showMessageDialog(
                        frame, e, "Failed to start new game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void setGame(Game game) {
        if (game != null) {
            game.setListener(this);
            this.gameRef = game.getSelfRef();
        } else {
            this.gameRef = null;
        }
        
        gameActions.setGameRef(gameRef);
        regionView.setGameRef(gameRef);
        inventoryView.setGameRef(gameRef);
    }
    
    private void addInput(String keyStroke, Object actionMapKey) {
        InputMap inputMap = regionView.getInputMap();
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionMapKey);
    }
    
    public void update() {
        if (gameRef != null) {
            gameRef.executeQuery(new SimpleQueryCallback() {
                public void execute(IGame game) {
                    consoleView.turnEnd();
                    regionView.repaint();
                    statisticsView.updateStatistics(game);
                    inventoryView.update(game);
                }
            });
            
            gameRef.yieldWriteLock();
        }
    }

    private void initUncaughtExceptionHandler() {
        setAWTUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception", e);
                
                ErrorDialog.show(frame, "Unexpected Error", e);
                
                // The thread is about to die, set this same handler 
                // for the newly created AWT event handler thread.
                setAWTUncaughtExceptionHandler(this);
            }
        });
    }
    
    private static void setAWTUncaughtExceptionHandler(
            final Thread.UncaughtExceptionHandler eh) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Thread.currentThread().setUncaughtExceptionHandler(eh);
            }
        });
    }
    
    private class SaveGameAction extends AbstractAction {
        public SaveGameAction() {
            super("Save...");
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                int status = fileChooser.showSaveDialog(frame);
                if (status == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    OutputStream out = new FileOutputStream(file);
                    try {
                        final ObjectOutputStream oos = new ObjectOutputStream(out);
                        gameRef.getAutolockingGame().save(oos);
                        oos.flush();
                    } finally {
                        out.close();
                    }

                    // If game was saved succesfully, quit it
                    setGame(null);
                }
                
            } catch (Exception ex) {
                ErrorDialog.show(frame, "Saving failed", ex);
            }
        }
    }
    
    private class LoadGameAction extends AbstractAction {
        public LoadGameAction() {
            super("Load...");
            putValue(MNEMONIC_KEY, KeyEvent.VK_L);
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                int status = fileChooser.showOpenDialog(frame);
                if (status == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    InputStream in = new FileInputStream(file);
                    try {
                        ObjectInputStream ois = new ObjectInputStream(in);
                        consoleView.clear();
                        setGame((Game) ois.readObject());
                        
                    } finally {
                        in.close();
                    }
                    
                    // if file was loaded succesfully, delete the file
                    file.delete();
                }
            } catch (Exception ex) {
                ErrorDialog.show(frame, "Loading failed", ex);
            }
        }
    }

    private class MovePlayerAction extends AbstractAction {
        private final Direction direction;
        private final boolean run;

        public MovePlayerAction(Direction direction, boolean run) {
            this.direction = direction;
            this.run = run;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (gameRef != null) {
                IGame game = gameRef.getAutolockingGame();
                if (run) {
                    game.runTowards(direction);
                } else {
                    game.movePlayer(direction);
                }
            }
        }
    }

    private class VerticalMoveAction extends AbstractAction {
        private final boolean up;

        public VerticalMoveAction(boolean up) {
            this.up = up;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (gameRef != null) {
                gameRef.getAutolockingGame().movePlayerVertically(up);
            }
        }
    }
    
    private class SkipTurnAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            if (gameRef != null) {
                gameRef.getAutolockingGame().skipTurn();
            }
        }
    }

    private class HistoryScrollAction extends AbstractAction {
        private final boolean up;
        
        public HistoryScrollAction(boolean up) {
            this.up = up;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (up) {
                consoleView.scrollUp();
            } else {
                consoleView.scrollDown();
            }
        }
    }

    private class MapAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            regionView.setTranslate(!regionView.isTranslate());
            regionView.repaint();
        }
    }
    
    private class NewGameAction extends AbstractAction {
        public NewGameAction() {
            super("New Game");
            putValue(MNEMONIC_KEY, KeyEvent.VK_N);
        }
        
        public void actionPerformed(ActionEvent e) {
            startNewGame();
        }
    }
    
    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super("Exit");
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
        }
       
        public void actionPerformed(ActionEvent e) {
            exit();
        }
    }
    
    private class AboutAction extends AbstractAction {
        public AboutAction() {
            super("About Wanhack");
            putValue(MNEMONIC_KEY, KeyEvent.VK_A);
        }
        
        public void actionPerformed(ActionEvent e) {
            String message =
                "Wanhack " + Version.getFullVersion() + "\n" +
                "\n" +
                "Copyright 2005-2006 The Wanhack Team\n" +
                "\n" +
                "This product includes software developed by the\n" + 
                "Apache Software Foundation http://www.apache.org/";
            
            JOptionPane.showMessageDialog(
                    frame, message, 
                    "About Wanhack", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private class RevealRegionAction extends AbstractAction {
        public RevealRegionAction() {
            super("Reveal Current Region");
            putValue(MNEMONIC_KEY, KeyEvent.VK_R);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (gameRef != null) {
                gameRef.getAutolockingGame().revealCurrentRegion();
                regionView.repaint();
            }
        }
    }
    
    private class ShowLogFrameAction extends AbstractAction {
        private final JCheckBoxMenuItem menuItem =
            new JCheckBoxMenuItem(this);
        
        public ShowLogFrameAction() {
            super("Log");
            
            putValue(MNEMONIC_KEY, KeyEvent.VK_L);
            
            logFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    menuItem.setSelected(false);
                }
            });
        }
        
        public JMenuItem getMenuItem() {
            return menuItem;
        }
        
        public void actionPerformed(ActionEvent e) {
            boolean newState = !logFrame.isVisible();
            logFrame.setVisible(newState);
            menuItem.setSelected(newState);
        }
    }

    private class ShowScriptingFrameAction extends AbstractAction {
        private final JCheckBoxMenuItem menuItem =
            new JCheckBoxMenuItem(this);
        
        public ShowScriptingFrameAction() {
            super("Scripting Console");
            
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (scriptFrame == null) {
                Cursor oldCursor = frame.getCursor();
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    scriptFrame = new ScriptFrame();
                } finally {
                    frame.setCursor(oldCursor);
                }

                scriptFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        menuItem.setSelected(false);
                    }
                });
            }

            scriptFrame.setGame(gameRef.getAutolockingGame());
            
            boolean newState = !scriptFrame.isVisible();
            scriptFrame.setVisible(newState);
            menuItem.setSelected(newState);
        }
        
        public JMenuItem getMenuItem() {
            return menuItem;
        }
    }
    
    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
            boolean wizardMode = SystemAccess.getSystemProperty("wizard") != null; 
            
            new Main(wizardMode);
            
        } catch (Throwable e) {
            e.printStackTrace();
            ErrorDialog.show(null, "Unexpected Error", e);
            System.exit(1);
        }
    }
}
