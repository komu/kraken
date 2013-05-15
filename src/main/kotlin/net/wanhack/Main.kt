/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack

import javax.swing.UIManager
import net.wanhack.ui.common.ErrorDialog
import net.wanhack.utils.SystemAccess
import net.wanhack.ui.debug.ScriptFrame
import javax.swing.JFrame
import net.wanhack.ui.console.ConsoleView
import net.wanhack.ui.game.InventoryView
import net.wanhack.ui.game.StatisticsView
import javax.swing.JOptionPane
import net.wanhack.ui.debug.LogFrame
import net.wanhack.model.item.Item
import net.wanhack.model.creature.Creature
import net.wanhack.service.config.ObjectFactory
import net.wanhack.service.region.RegionLoader
import net.wanhack.service.ServiceProvider
import net.wanhack.model.DefaultGameRef
import net.wanhack.ui.game.RegionView
import org.apache.commons.logging.LogFactory
import net.wanhack.ui.game.action.GameActionSet
import javax.swing.JSplitPane
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.WindowConstants
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JMenu
import javax.swing.JMenuBar
import java.awt.event.KeyEvent
import javax.swing.AbstractAction
import java.awt.event.ActionEvent
import javax.swing.Action.*
import net.wanhack.common.Version
import javax.swing.JCheckBoxMenuItem
import java.awt.Cursor
import net.wanhack.model.common.Direction
import javax.swing.KeyStroke
import net.wanhack.model.Game
import javax.swing.SwingUtilities
import net.wanhack.ui.game.StartGameDialog

class Main(val wizardMode: Boolean) {

    private val frame = JFrame("Wanhack")
    private val consoleView = ConsoleView()
    private val inventoryView = InventoryView()
    private val statisticsView = StatisticsView()

    private val regionLoader: RegionLoader
    private var gameRef: DefaultGameRef? = null
    private val regionView = RegionView()
    private val log = LogFactory.getLog(javaClass<Main>())!!
    private var logFrame: LogFrame? = null
    private val gameActions = GameActionSet();

    {
        JOptionPane.setRootFrame(frame)

        val objectFactory = ObjectFactory()
        objectFactory.parse("/items/items.xml", javaClass<Item>(), "item")
        objectFactory.parse("/items/weapons.xml", javaClass<Item>(), "item")
        objectFactory.parse("/creatures/creatures.xml", javaClass<Creature>(), "creature")
        this.regionLoader = RegionLoader(objectFactory)

        ServiceProvider.console = consoleView
        ServiceProvider.objectFactory = objectFactory
        ServiceProvider.regionLoader = regionLoader

        if (wizardMode) {
            logFrame = LogFrame()
        }

        initializeMenuBar()
        initializeInputMap()
        initializeActionMap()

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT)
        splitPane.setDividerSize(1)
        val contentPane = frame.getContentPane()!!
        val mainPanel = JPanel(BorderLayout())
        mainPanel.add(consoleView, BorderLayout.NORTH)
        mainPanel.add(regionView, BorderLayout.CENTER)
        mainPanel.add(statisticsView, BorderLayout.SOUTH)
        splitPane.setLeftComponent(mainPanel)
        splitPane.setRightComponent(inventoryView)

        contentPane.add(splitPane, BorderLayout.CENTER)

        frame.pack()
        frame.setLocationByPlatform(true)

        initUncaughtExceptionHandler()

        frame.setVisible(true);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
        frame.addWindowListener(object : WindowAdapter() {

            override fun windowClosing(e: WindowEvent) {
                exit()
            }
        })

        startNewGame()
    }

    fun exit() {
        val res = JOptionPane.showConfirmDialog(frame, "Really exit Wanhack?", "Are you sure?", JOptionPane.YES_NO_OPTION)
        if (res == JOptionPane.YES_OPTION) {
            System.exit(0)
        }
    }

    fun initializeMenuBar() {
        log.debug("Initializing menu bar");

        val menuBar = JMenuBar()

        val gameMenu = JMenu("Game")
        gameMenu.setMnemonic('g')
        gameMenu.add(NewGameAction())

        gameMenu.addSeparator()
        gameMenu.add(ExitAction())
        menuBar.add(gameMenu)

        val actionMenu = JMenu("Action")
        actionMenu.setMnemonic('a')
        for (action in gameActions.actions)
            actionMenu.add(action)

        menuBar.add(actionMenu);

        if (wizardMode) {
            val debugMenu = JMenu("Debug");
            debugMenu.setMnemonic('d');
            debugMenu.add(RevealRegionAction());
            debugMenu.add(ShowLogFrameAction(logFrame!!).menuItem);
            debugMenu.add(ShowScriptingFrameAction().menuItem);

            menuBar.add(debugMenu);
        }

        val helpMenu = JMenu("Help")
        helpMenu.setMnemonic('h')
        helpMenu.add(AboutAction())
        menuBar.add(helpMenu)

        frame.setJMenuBar(menuBar)
    }

    fun initializeInputMap() {
        log.debug("Initializing input map")

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

    fun initializeActionMap() {
        log.debug("Initializing action map");

        val actionMap = regionView.getActionMap()!!

        for (direction in Direction.values()) {
            actionMap.put(direction, MovePlayerAction(direction, false));
        }

        for (direction in Direction.values()) {
            actionMap.put("run " + direction, MovePlayerAction(direction, true));
        }

        actionMap.put("down", VerticalMoveAction(false));
        actionMap.put("up",   VerticalMoveAction(true));

        actionMap.put("rest", SkipTurnAction());
        actionMap.put("history up", HistoryScrollAction(true));
        actionMap.put("history down", HistoryScrollAction(false));
        actionMap.put("map",  MapAction());
    }

    fun startNewGame() {
        log.debug("starting new game")

        val dialog = StartGameDialog(frame);
        val config = dialog.showDialog();

        if (config != null) {
            try {
                val game = Game(config, wizardMode)
                consoleView.clear();
                setGame(game);
                gameRef!!.getAutoLockingGame().start();

            } catch (e: Exception) {
                log.error("Failed to start new game", e);
                JOptionPane.showMessageDialog(
                        frame, e, "Failed to start new game", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private fun setGame(game: Game?) {
        if (game != null) {
            game.listener = { update() }
            this.gameRef = game.selfRef
        } else {
            this.gameRef = null
        }

        gameActions.gameRef = gameRef
        regionView.gameRef = gameRef
        inventoryView.gameRef = gameRef
    }

    fun addInput(keyStroke: String, actionMapKey: Any) {
        val inputMap = regionView.getInputMap()!!
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionMapKey)
    }

    private fun update() {
        val ref = gameRef
        if (ref != null) {
            ref.executeQuery { game ->
                consoleView.turnEnd()
                regionView.repaint()
                statisticsView.updateStatistics(game)
                inventoryView.update(game)
            }

            ref.yieldWriteLock()
        }
    }

    fun initUncaughtExceptionHandler() {
        setAWTUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(t: Thread, e: Throwable) {
                log.error("Uncaught exception", e);

                ErrorDialog.show(frame, "Unexpected Error", e);

                // The thread is about to die, set this same handler
                // for the newly created AWT event handler thread.
                setAWTUncaughtExceptionHandler(this);
            }
        });
    }

    fun setAWTUncaughtExceptionHandler(eh: Thread.UncaughtExceptionHandler) {
        SwingUtilities.invokeLater(Runnable {
            Thread.currentThread().setUncaughtExceptionHandler(eh)
        });
    }

    inner class MovePlayerAction(val direction: Direction, val run: Boolean) : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            val game = gameRef?.getAutoLockingGame()
            if (game != null) {
                if (run) {
                    game.runTowards(direction);
                } else {
                    game.movePlayer(direction);
                }
            }
        }
    }

    inner class VerticalMoveAction(val up: Boolean) : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            gameRef?.getAutoLockingGame()?.movePlayerVertically(up);
        }
    }

    inner class SkipTurnAction : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            gameRef?.getAutoLockingGame()?.skipTurn()
        }
    }

    inner class HistoryScrollAction(val up: Boolean) : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            if (up)
                consoleView.scrollUp()
            else
                consoleView.scrollDown()
        }
    }

    inner class MapAction : AbstractAction() {

        public override fun actionPerformed(e: ActionEvent) {
            regionView.translate = !regionView.translate
            regionView.repaint()
        }
    }

    inner class NewGameAction : AbstractAction("New Game") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_N)
        }

        public override fun actionPerformed(e: ActionEvent) {
            startNewGame()
        }
    }

    inner class ExitAction : AbstractAction("Exit") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_X);
        }

        public override fun actionPerformed(e: ActionEvent) {
            exit();
        }
    }

    inner class AboutAction : AbstractAction("About Wanhack") {
        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_A)
        }

        public override fun actionPerformed(e: ActionEvent) {
            val message =
                "Wanhack ${Version.fullVersion}\n" +
                "\n" +
                "Copyright 2005-2013 The Wanhack Team\n" +
                "\n" +
                "This product includes software developed by the\n" +
                "Apache Software Foundation http://www.apache.org/"

            JOptionPane.showMessageDialog(
                    frame, message,
                    "About Wanhack", JOptionPane.INFORMATION_MESSAGE)
        }
    }

    inner class RevealRegionAction : AbstractAction("Reveal Current Region") {

        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_R)
        }

        public override fun actionPerformed(e: ActionEvent) {
            val game = gameRef
            if (game != null) {
                game.getAutoLockingGame().revealCurrentRegion()
                regionView.repaint()
            }
        }
    }

    class ShowLogFrameAction(val frame: LogFrame) : AbstractAction("Log") {

        val menuItem = JCheckBoxMenuItem(this);

        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_L);
            frame.addWindowListener(object : WindowAdapter() {
                public override fun windowClosing(e: WindowEvent) {
                    menuItem.setSelected(false);
                }
            });
        }

        public override fun actionPerformed(e: ActionEvent) {
            val newState = !frame.isVisible()
            frame.setVisible(newState)
            menuItem.setSelected(newState)
        }
    }

    inner class ShowScriptingFrameAction : AbstractAction("Scripting Console") {
        var scriptFrame: ScriptFrame? = null
        val menuItem = JCheckBoxMenuItem(this);

        {
            putValue(MNEMONIC_KEY, KeyEvent.VK_S);
        }

        public override fun actionPerformed(e: ActionEvent) {
            if (scriptFrame == null) {
                val oldCursor = frame.getCursor()
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR))
                try {
                    scriptFrame = ScriptFrame()
                } finally {
                    frame.setCursor(oldCursor)
                }

                scriptFrame!!.addWindowListener(object : WindowAdapter() {
                    public override fun windowClosing(e: WindowEvent) {
                        menuItem.setSelected(false);
                    }
                })
            }

            scriptFrame!!.setGame(gameRef!!.getAutoLockingGame())

            val newState = !scriptFrame!!.isVisible()
            scriptFrame!!.setVisible(newState)
            menuItem.setSelected(newState)
        }
    }
}

fun main(args: Array<String>) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        val wizardMode = SystemAccess.getSystemProperty("wizard") != null

        Main(wizardMode)

    } catch (e: Exception) {
        e.printStackTrace()
        ErrorDialog.show(null, "Unexpected Error", e)
        System.exit(1)
    }
}
