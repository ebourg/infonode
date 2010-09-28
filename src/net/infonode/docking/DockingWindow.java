/** 
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


// $Id: DockingWindow.java,v 1.22 2004/08/11 13:47:58 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.location.LocationDecoder;
import net.infonode.docking.location.NullLocation;
import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.properties.DockingWindowProperties;
import net.infonode.gui.panel.BasePanel;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapManager;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.util.ArrayUtil;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * This is the base class for all types of docking windows. The windows are structured in a tree, typically with a
 * {@link RootWindow} at the root. Each DockingWindow has a window parent and a number of child windows.
 * <p/>
 * <p/>
 * <b>Warning: </b> the non-public methods in this class can be changed in non-compatible ways in future versions. </p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.22 $
 */
abstract public class DockingWindow extends BasePanel {
  /**
   * Returns the icon for this window.
   *
   * @return the icon
   */
  abstract public Icon getIcon();

  /**
   * Returns the child window with index <tt>index</tt>.
   *
   * @param index the child window index
   *
   * @return the child window
   */
  abstract public DockingWindow getChildWindow(int index);

  /**
   * Returns the number of child windows.
   *
   * @return the number of child windows
   */
  abstract public int getChildWindowCount();

  /**
 *
   */
  abstract protected WindowLocation getWindowLocation(DockingWindow window);

  /**
 *
   */
  abstract protected void doReplace(DockingWindow oldWindow,
                                    DockingWindow newWindow);

  /**
 *
   */
  abstract protected void doRemoveWindow(DockingWindow window);

  /**
 *
   */
  abstract protected void update();

  private DockingWindow windowParent;
  private DockingWindowProperties rootProperties = new DockingWindowProperties();
  private DockingWindowProperties properties = new DockingWindowProperties(rootProperties);
  private WindowLocation lastLocation = NullLocation.INSTANCE;
  private WindowTab tab;
  private DockingWindow lastFocusedChildWindow;
  private WindowPopupMenuFactory popupMenuFactory;

  private static DockingWindow optimizeWindow;
  private static int optimizeDepth;

  /**
 *
   */
  protected DockingWindow() {
    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showMenu(e);
        }
      }

      public void mouseReleased(MouseEvent e) {
        mousePressed(e);
      }
    });

  }

  /**
 *
   */
  protected void init() {
    getPropertyObject().addTreeListener(new PropertyMapTreeListener() {
      public void propertyValuesChanged(Map changes) {
        update();
      }
    });

    update();
  }

  /**
   * Returns the window parent of this window.
   *
   * @return the window parent of this window
   */
  public DockingWindow getWindowParent() {
    return windowParent;
  }

  /**
   * Splits this window in the given direction. If this window is a View which is contained in a TabWindow with a single
   * tab, the TabWindow will splitted instead of this View.
   *
   * @param splitWithWindow the splitWithWindow which to split with
   * @param direction       the split direction
   * @param dividerLocation the relative split divider location (0 - 1)
   *
   * @return the resulting split window
   */
  public SplitWindow split(final DockingWindow splitWithWindow,
                           final Direction direction,
                           final float dividerLocation) {
    final SplitWindow w = new SplitWindow(direction == Direction.RIGHT
                                          || direction == Direction.LEFT);

    optimizeAfter(splitWithWindow.getWindowParent(), new Runnable() {
      public void run() {
        getWindowParent().replaceChildWindow(DockingWindow.this, w);
        w
            .setWindows(direction == Direction.DOWN
                        || direction == Direction.RIGHT ? DockingWindow.this
                        : splitWithWindow,
                        direction == Direction.UP || direction == Direction.LEFT ? DockingWindow.this
                        : splitWithWindow);
        w.setDividerLocation(dividerLocation);
        w.getWindowParent().optimizeWindowLayout();
      }
    });

    return w;
  }

  /**
   * Returns the properties for this window.
   *
   * @return the properties for this window
   */
  public DockingWindowProperties getWindowProperties() {
    return properties;
  }

  /**
   * Returns the RootWindow which contains this window.
   *
   * @return The RootWindow
   */
  public RootWindow getRootWindow() {
    return windowParent == null ? null : windowParent.getRootWindow();
  }

  /**
   * Restores this window to the last saved location. If the window can't be restored to the exact same location, a good
   * approximation is performed. This method will always succeed in displaying the window somewhere.
   */
  public void restore() {
    if (lastLocation == null) {
      RootWindow rootWindow = getRootWindow();

      if (rootWindow == null)
        return;

      DockingWindow w = rootWindow.getWindow();

      if (w == null)
        rootWindow.setWindow(this);
      else
        w.split(this, Direction.RIGHT, 0.5F);
    }
    else
      lastLocation.set(this);
  }

  /**
   * Removes this window from it's window parent. The location of this window is saved and it can be
   * restored to that location using the restore() method.
   * If the window parent is a split window or a tab window with one child, it will be removed as well.
   */
  public void close() {
    if (windowParent != null) {
      optimizeAfter(windowParent, new Runnable() {
        public void run() {
          windowParent.removeChildWindow(DockingWindow.this);
        }
      });
    }
  }

  /**
   * Returns the index of a child windows.
   *
   * @return the index of the child window, -1 if the window is not a child of this window
   */
  public int getChildWindowIndex(DockingWindow window) {
    for (int i = 0; i < getChildWindowCount(); i++)
      if (getChildWindow(i) == window)
        return i;

    return -1;
  }

  /**
   * Returns the popup menu factory for this window. If it's not null a popup menu will be created and shown when the
   * mouse popup trigger is activated on this window.
   *
   * @return the popup menu factory for this window, null if there is none
   */
  public WindowPopupMenuFactory getPopupMenuFactory() {
    return popupMenuFactory;
  }

  /**
   * Sets the popup menu factory for this window. If it's not null a popup menu will be created and shown when the mouse
   * popup trigger is activated on this window.
   *
   * @param popupMenuFactory the popup menu factory, null if no popup menu should be shown
   */
  public void setPopupMenuFactory(WindowPopupMenuFactory popupMenuFactory) {
    this.popupMenuFactory = popupMenuFactory;
  }

  /**
   * Returns true if this window is minimized, ie located in a {@link WindowBar}.
   *
   * @return true if this window is minimized
   */
  public boolean isMinimized() {
    return windowParent != null && windowParent.isMinimized();
  }

  /**
   * Returns the child window that last contained focus.
   *
   * @return the child window that last contained focus, null if no child window has contained focus
   */
  public DockingWindow getLastFocusedChildWindow() {
    return lastFocusedChildWindow;
  }

  /**
   * Minimizes this window. The window is minimized to the closest active {@link WindowBar}. If no suitable {@link
   * WindowBar}was found or this window already is minimized, no action is performed.
   */
  public void minimize() {
    if (isMinimized())
      return;

    minimize(getRootWindow().getClosestWindowBar(this));
  }

  /**
   * Minimizes this window to a {@link WindowBar}located in <tt>direction</tt>. If no suitable {@link WindowBar}was
   * found or this window already is minimized, no action is performed.
   *
   * @param direction the direction in which the window bar is located
   */
  public void minimize(Direction direction) {
    if (direction == null || isMinimized())
      return;

    WindowBar bar = getRootWindow().getWindowBar(direction);

    if (bar != null) {
      bar.addTab(this);
    }
  }

  /**
   * Returns true if this window can be minimized.
   *
   * @return true if this window can be minimized
   *
   * @see #minimize()
   */
  public boolean isMinimizable() {
    return getRootWindow() != null && getRootWindow().windowBarEnabled();
  }

  /**
   * Replaces a child window with another window.
   *
   * @param oldWindow the child window to replaceChildWindow
   * @param newWindow the window to replaceChildWindow it with
   */
  public void replaceChildWindow(final DockingWindow oldWindow,
                                 final DockingWindow newWindow) {
    optimizeAfter(newWindow, new Runnable() {
      public void run() {
        DockingWindow nw = newWindow.getContentWindow(DockingWindow.this);

        if (nw.getWindowParent() != null)
          nw.getWindowParent().removeChildWindow(nw);

        nw.setWindowParent(DockingWindow.this);
        oldWindow.setWindowParent(null);
        doReplace(oldWindow, nw);
        fireTitleChanged();
      }
    });
  }

  /**
   * Returns the title of this window.
   *
   * @return the window title
   */
  public String getTitle() {
    String title = "";

    DockingWindow window;
    for (int i = 0; i < getChildWindowCount(); i++) {
      window = getChildWindow(i);
      title += (i > 0 ? ", " : "") + (window != null ? window.getTitle() : "");
    }

    return title;
  }

  public String toString() {
    return getTitle();
  }

  /**
   * @return true if this window is inside a tab __exclude__
   */
  protected boolean insideTab() {
    return windowParent == null ? false : windowParent.childInsideTab();
  }

  /**
   * @return true if the child windows are inside tabs __exclude__
   */
  protected boolean childInsideTab() {
    return windowParent == null ? false : windowParent.childInsideTab();
  }

  /**
 *
   */
  protected void clearChildren(DockingWindow child, View view) {
    for (int i = 0; i < getChildWindowCount(); i++)
      if (child != getChildWindow(i))
        getChildWindow(i).clearFocus(view);
  }

  void childGainedFocus(DockingWindow child, View view) {
    if (child != null)
      lastFocusedChildWindow = child;

    if (tab != null)
      tab.setFocused(true);

    clearChildren(child, view);

    if (windowParent != null)
      windowParent.childGainedFocus(this, view);
  }

  void restoreFocus() {
    if (lastFocusedChildWindow != null) {
      lastFocusedChildWindow.restoreFocus();
    }
    //    else
    //      getChildWindow(0).restoreFocus();
  }

  WindowTab getTab() {
    if (tab == null) {
      tab = new WindowTab(this, false);
    }

    return tab;
  }

  /**
 *
   */
  protected void childRemoved(DockingWindow child) {
    if (lastFocusedChildWindow == child)
      lastFocusedChildWindow = null;
  }

  /**
 *
   */
  protected WindowLocation getWindowLocation() {
    return windowParent == null ? NullLocation.INSTANCE
           : windowParent.getWindowLocation(this);
  }

  /**
 *
   */
  protected void updateMinimizable() {
    if (tab != null)
      tab.updateButtons();

    for (int i = 0; i < getChildWindowCount(); i++)
      getChildWindow(i).updateMinimizable();
  }

  /**
 *
   */
  protected void readLocations(ObjectInputStream in, RootWindow rootWindow,
                               int version) throws IOException {
    lastLocation = LocationDecoder.decode(in, rootWindow);

    for (int i = 0; i < getChildWindowCount(); i++)
      getChildWindow(i).readLocations(in, rootWindow, version);
  }

  /**
 *
   */
  protected void writeLocations(ObjectOutputStream out) throws IOException {
    lastLocation.write(out);

    for (int i = 0; i < getChildWindowCount(); i++)
      getChildWindow(i).writeLocations(out);
  }

  /**
 *
   */
  protected static void beginOptimize(DockingWindow window) {
    if (optimizeDepth++ == 0)
      optimizeWindow = window;
  }

  /**
 *
   */
  protected static void endOptimize() {
    if (--optimizeDepth == 0) {
      if (optimizeWindow != null)
        optimizeWindow.optimizeWindowLayout();

      optimizeWindow = null;
    }
  }

  /**
 *
   */
  protected static void optimizeAfter(DockingWindow window, Runnable runnable) {
    beginOptimize(window);

    try {
      runnable.run();
    }
    finally {
      endOptimize();
    }
  }

  /**
 *
   */
  protected boolean needsTitleWindow() {
    return false;
  }

  /**
 *
   */
  protected boolean showsWindowTitle() {
    return false;
  }

  /**
 *
   */
  protected void optimizeWindowLayout() {
  }

  /**
 *
   */
  protected DockingWindow getLocationWindow() {
    return this;
  }

  /**
 *
   */
  protected void fireTitleChanged() {
    if (tab != null)
      tab.windowTitleChanged();

    if (windowParent != null)
      windowParent.fireTitleChanged();
  }

  private DockingWindow getContentWindow(DockingWindow parent) {
    return needsTitleWindow() && !parent.showsWindowTitle() ? new TabWindow(this) : this;
  }

  protected final void removeChildWindow(final DockingWindow window) {
    optimizeAfter(window.getWindowParent(), new Runnable() {
      public void run() {
        if (!window.isMinimized())
          window.lastLocation = getWindowLocation(window);

        window.setWindowParent(null);
        doRemoveWindow(window);
        fireTitleChanged();
      }
    });
  }

  final protected DockingWindow addWindow(DockingWindow window) {
    DockingWindow w = window.getContentWindow(this);
    DockingWindow oldParent = w.getWindowParent();

    if (oldParent != null) {
      oldParent.removeChildWindow(w);
    }

    w.setWindowParent(this);
    fireTitleChanged();
    return w;
  }

  /**
 *
   */
  protected void rootChanged(final RootWindow oldRoot, final RootWindow newRoot) {
    PropertyMapManager.runBatch(new Runnable() {
      public void run() {
        if (oldRoot != null)
          rootProperties.getMap().removeSuperMap();

        if (newRoot != null) {
          rootProperties.addSuperObject(newRoot.getRootWindowProperties()
                                        .getDockingWindowProperties());
        }
      }
    });

    for (int i = 0; i < getChildWindowCount(); i++)
      if (getChildWindow(i) != null)
        getChildWindow(i).rootChanged(oldRoot, newRoot);
  }

  /**
 *
   */
  protected void clearFocus(View view) {
    if (tab != null) {
      tab.setFocused(false);
    }

    for (int i = 0; i < getChildWindowCount(); i++)
      getChildWindow(i).clearFocus(view);
  }

  private void setWindowParent(DockingWindow window) {
    final RootWindow oldRoot = getRootWindow();

    if (windowParent != null) {
      windowParent.childRemoved(this);
      clearFocus(null);
    }

    this.windowParent = window;
    final RootWindow newRoot = getRootWindow();

    if (oldRoot != newRoot) {
      rootChanged(oldRoot, newRoot);
    }
  }

  /**
 *
   */
  protected Direction getSplitDirection(Point p) {
    int[] dist = new int[]{p.x, getWidth() - p.x, p.y, getHeight() - p.y};

    if (acceptsCenterDrop()
        &&
        dist[ArrayUtil.findSmallest(dist)] >
        getRootWindow()
        .getRootWindowProperties()
        .getEdgeSplitDistance())
      return null;

    double[] relativeDist = new double[]{p.getX() / getWidth(),
                                         (getWidth() - p.getX()) / getWidth(), p.getY() / getHeight(),
                                         (getHeight() - p.getY()) / getHeight()};
    int index = ArrayUtil.findSmallest(relativeDist);
    return index == 0 ? Direction.LEFT
           : index == 1 ? Direction.RIGHT
             : index == 2 ? Direction.UP : Direction.DOWN;
  }

  DockingWindow acceptDrop(Point p, DockingWindow window) {
    if (!getRootWindow().getRootWindowProperties().getRecursiveTabsEnabled()
        && insideTab())
      return getWindowParent().acceptDrop(p, window);

    if (!isSplittable() || hasParent(window))
      return null;

    Direction d = getSplitDirection(p);

    if (d == null) {
      getRootWindow()
          .setRectangle(SwingUtilities
                        .convertRectangle(this,
                                          new Rectangle(0,
                                                        0,
                                                        getWidth(),
                                                        getHeight()),
                                          getRootWindow()));
    }
    else {
      int width = d == Direction.LEFT || d == Direction.RIGHT ? getWidth() / 3
                  : getWidth();
      int height = d == Direction.DOWN || d == Direction.UP ? getHeight() / 3
                   : getHeight();
      int x = d == Direction.RIGHT ? getWidth() - width : 0;
      int y = d == Direction.DOWN ? getHeight() - height : 0;

      Rectangle rect = new Rectangle(x, y, width, height);
      getRootWindow()
          .setRectangle(SwingUtilities
                        .convertRectangle(this,
                                          rect,
                                          getRootWindow()));
    }

    return this;
  }

  /**
 *
   */
  protected boolean acceptsCenterDrop() {
    return false;
  }

  void abortDrop() {
    // Ignore
  }

  /**
 *
   */
  protected boolean hasParent(DockingWindow w) {
    return getWindowParent() == null ? false
           : getWindowParent() == w
             || getWindowParent().hasParent(w);
  }

  void doDrop(Point p, final DockingWindow window) {
    Direction d = getSplitDirection(p);

    if (d == null)
      optimizeAfter(window.getWindowParent(), new Runnable() {
        public void run() {
          TabWindow tabWindow = new TabWindow();
          windowParent.replaceChildWindow(DockingWindow.this, tabWindow);
          tabWindow.addTab(DockingWindow.this);
          tabWindow.addTab(window);
        }
      });
    else
      split(window, d, 0.5F);

    window.restoreFocus();
  }

  /**
 *
   */
  protected boolean isSplittable() {
    return true;
  }

  /**
 *
   */
  protected void write(ObjectOutputStream out, WriteContext context) throws IOException {
    if (context.getWritePropertiesEnabled()) {
      properties.getMap().write(out, true);
      getPropertyObject().write(out, true);
    }
  }

  /**
 *
   */
  protected DockingWindow read(ObjectInputStream in, ReadContext context) throws IOException {
    if (context.isPropertyValuesAvailable()) {
      (context.getReadPropertiesEnabled() ? properties : new DockingWindowProperties()).getMap().read(in);
      (context.getReadPropertiesEnabled() ? getPropertyObject() : createPropertyObject()).read(in);
    }

    return this;
  }

  /**
 *
   */
  abstract protected PropertyMap getPropertyObject();

  /**
 *
   */
  abstract protected PropertyMap createPropertyObject();

  void showMenu(MouseEvent event) {
    DockingWindow w = this;

    while (w.popupMenuFactory == null) {
      w = w.getWindowParent();

      if (w == null)
        return;
    }

    JPopupMenu popupMenu = w.popupMenuFactory.createPopupMenu(this);

    if (popupMenu != null)
      popupMenu.show(event.getComponent(), event.getX(), event.getY());
  }

}