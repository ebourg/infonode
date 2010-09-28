/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R) 
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA 02111-1307, USA.
 */


// $Id: TabWindow.java,v 1.19 2004/09/24 16:29:56 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.internalutil.*;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapManager;
import net.infonode.tabbedpanel.TabAdapter;
import net.infonode.tabbedpanel.TabEvent;
import net.infonode.tabbedpanel.TabRemovedEvent;
import net.infonode.util.ArrayUtil;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * A docking window containing a tabbed panel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.19 $
 */
public class TabWindow extends AbstractTabWindow {
  private static final ButtonInfo[] buttonInfos = {
    new MinimizeButtonInfo(TabWindowProperties.MINIMIZE_BUTTON_PROPERTIES),
    new MaximizeButtonInfo(TabWindowProperties.MAXIMIZE_BUTTON_PROPERTIES),
    new RestoreButtonInfo(TabWindowProperties.RESTORE_BUTTON_PROPERTIES) {
      public boolean isVisible(DockingWindow window) {
        return super.isVisible(window) || window.isMaximized();
      }
    },
    new CloseButtonInfo(TabWindowProperties.CLOSE_BUTTON_PROPERTIES)
  };

  private TabWindowProperties parentProperties = new TabWindowProperties();
  private WindowMover windowMover;
  private AbstractButton[] buttons = new AbstractButton[buttonInfos.length];

  /**
   * Creates an empty tab window.
   */
  public TabWindow() {
    super(true);
    setTabWindowProperties(new TabWindowProperties(parentProperties));

    windowMover = new WindowMover(this, new WindowProvider() {
      public DockingWindow getWindow(Point point) {
        Point p = SwingUtilities.convertPoint(TabWindow.this, point, getTabbedPanel());
        return getTabbedPanel().tabAreaContainsPoint(p) ?
               (getChildWindowCount() == 1 ? getChildWindow(0) : TabWindow.this) :
               null;
      }
    });
    init();

    getTabbedPanel().addTabListener(new TabAdapter() {
      public void tabAdded(TabEvent event) {
        update();
      }

      public void tabRemoved(TabRemovedEvent event) {
        update();
      }
    });
  }

  /**
   * Creates a tab window with a tab containing the child window.
   *
   * @param window the child window
   */
  public TabWindow(DockingWindow window) {
    this();
    addTab(window);
  }

  /**
   * Creates a tab window with tabs for the child windows.
   *
   * @param windows the child windows
   */
  public TabWindow(DockingWindow[] windows) {
    this();

    for (int i = 0; i < windows.length; i++)
      addTab(windows[i]);
  }

  protected void maximized(boolean maximized) {
    super.maximized(maximized);
    update();
  }

  protected void update() {
    updateBorder();

    if (getRootWindow() != null)
      windowMover.setAbortDragKey(getRootWindow().getRootWindowProperties().getAbortDragKey());

    if (InternalDockingUtil.updateButtons(buttonInfos, buttons, null, this, getTabWindowProperties().getMap())) {
      JComponent[] components = new JComponent[ArrayUtil.countNotNull(buttons)];
      int j = 0;

      for (int i = 0; i < buttons.length; i++)
        if (buttons[i] != null)
          components[j++] = buttons[i];

      getTabbedPanel().setTabAreaComponents(components);
    }
  }

  private void updateBorder() {
    Direction dir = getTabbedPanel().getProperties().getTabAreaOrientation();
    setBorder(new EmptyBorder(dir == Direction.UP ? 3 : 0,
                              dir == Direction.LEFT ? 3 : 0,
                              dir == Direction.DOWN ? 3 : 0,
                              dir == Direction.RIGHT ? 3 : 0));
  }

  protected void optimizeWindowLayout() {
    if (getWindowParent() == null)
      return;

    if (getTabbedPanel().getTabCount() == 0)
      internalClose();
    else if (getTabbedPanel().getTabCount() == 1 &&
             (getWindowParent().showsWindowTitle() || !getChildWindow(0).needsTitleWindow())) {
      getWindowParent().replaceChildWindow(this, getChildWindow(0));
    }
  }

  public int addTab(DockingWindow w, int index) {
    int actualIndex = super.addTab(w, index);
    setSelectedTab(actualIndex);
    return actualIndex;
  }

  protected void rootChanged(final RootWindow oldRoot, final RootWindow newRoot) {
    super.rootChanged(oldRoot, newRoot);
    PropertyMapManager.runBatch(new Runnable() {
      public void run() {
        if (oldRoot != null)
          parentProperties.getMap().removeSuperMap();

        if (newRoot != null) {
          parentProperties.addSuperObject(newRoot.getRootWindowProperties().getTabWindowProperties());
        }
      }
    });
  }

  protected PropertyMap getPropertyObject() {
    return getTabWindowProperties().getMap();
  }

  protected PropertyMap createPropertyObject() {
    return new TabWindowProperties().getMap();
  }

  protected void updateMinimizable() {
    update();
    super.updateMinimizable();
  }

  protected DockingWindow getOptimizedWindow() {
    return getChildWindowCount() == 1 ? getChildWindow(0).getOptimizedWindow() : super.getOptimizedWindow();
  }

  void doDrop(Point p, DockingWindow window) {
    super.doDrop(p, window);
    FocusManager.focusWindow(window);
  }

}
