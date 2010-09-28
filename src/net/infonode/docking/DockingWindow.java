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


// $Id: DockingWindow.java,v 1.45 2004/09/28 15:07:29 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.location.LocationDecoder;
import net.infonode.docking.location.NullLocation;
import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.properties.DockingWindowProperties;
import net.infonode.gui.ComponentUtil;
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
import java.util.ArrayList;
import java.util.Map;

/**
 * This is the base class for all types of docking windows. The windows are structured in a tree, typically with a
 * {@link RootWindow} at the root. Each DockingWindow has a window parent and a number of child windows.
 * <p>
 * <b>Warning: </b> the non-public methods in this class can be changed in non-compatible ways in future versions.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.45 $
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

  abstract void removeWindowComponent(DockingWindow window);

  abstract void restoreWindowComponent(DockingWindow window);

  private DockingWindow windowParent;
  private DockingWindowProperties rootProperties = new DockingWindowProperties();
  private DockingWindowProperties properties = new DockingWindowProperties(rootProperties);
  private WindowLocation lastLocation = NullLocation.INSTANCE;
  private WindowTab tab;
  private DockingWindow lastFocusedChildWindow;
  private WindowPopupMenuFactory popupMenuFactory;
  private ArrayList listeners;
  private Direction lastMinimizedDirection;

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
   * Adds a listener which will reveive events for this window and all child windows.
   *
   * @param listener the listener
   * @since IDW 1.1.0
   */
  public void addListener(DockingWindowListener listener) {
    if (listeners == null)
      listeners = new ArrayList(2);

    listeners.add(listener);
  }

  /**
   * Removes a previously added listener.
   *
   * @param listener the listener
   * @since IDW 1.1.0
   */
  public void removeListener(DockingWindowListener listener) {
    if (listeners != null) {
      listeners.remove(listener);

      if (listeners.size() == 0)
        listeners = null;
    }
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
   * @return the resulting split window
   */
  public SplitWindow split(final DockingWindow splitWithWindow,
                           final Direction direction,
                           final float dividerLocation) {
    final SplitWindow w = new SplitWindow(direction == Direction.RIGHT || direction == Direction.LEFT);

    optimizeAfter(splitWithWindow.getWindowParent(), new Runnable() {
      public void run() {
        getWindowParent().replaceChildWindow(DockingWindow.this, w);
        w.setWindows(direction == Direction.DOWN || direction == Direction.RIGHT ? DockingWindow.this : splitWithWindow,
                     direction == Direction.UP || direction == Direction.LEFT ? DockingWindow.this : splitWithWindow);
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
   * Returns the {@link RootWindow} which contains this window, null if there is none.
   *
   * @return the {@link RootWindow}, null if there is none
   */
  public RootWindow getRootWindow() {
    return windowParent == null ? null : windowParent.getRootWindow();
  }

  /**
   * Restores this window to the location before it was minimized, maximized or closed.
   * If the window can't be restored to the exact same location, a good approximation is performed. It's not guaranteed
   * that the window is shown anywhere after this method has returned.
   */
  public void restore() {
    if (isMaximized()) {
      getRootWindow().setMaximizedWindow(null);
    }
    else {
      DockingWindow w = this;

      while (w.lastLocation == NullLocation.INSTANCE && w.windowParent != null)
        w = w.windowParent;

      w.lastLocation.set(this);
    }

    if (getRootWindow() != null)
      FocusManager.focusWindow(this);
  }

  /**
   * <p>Removes this window from it's window parent. If the window parent is a split window or a tab window with
   * one child, it will be removed as well.</p>
   *
   * <p>The location of this window is saved and the window can be restored to that location using the
   * {@link #restore()} method.</p>
   *
   * <p>This method will call the {@link DockingWindowListener#windowClosed(DockingWindow)} method of all the listeners
   * of this window and all window ancestors. The listeners of child windows will not be notified, for example closing
   * a tab window containing views will not notify the listeners of views in that tab window.</p>
   */
  public void close() {
    if (windowParent != null) {
      DockingWindow[] ancestors = getAncestors();
      optimizeAfter(windowParent, new Runnable() {
        public void run() {
          storeLocation();
          windowParent.removeChildWindow(DockingWindow.this);
        }
      });

      for (int i = ancestors.length - 1; i >= 0; i--)
        ancestors[i].fireClosed(this);
    }
  }

  /**
   * Same as {@link #close()}, but the {@link DockingWindowListener#windowClosing(DockingWindow)} method of
   * the window listeners will be called before closing the window, giving them the possibility to abort the close
   * operation.
   *
   * @throws OperationAbortedException if the close operation was aborted by a window listener
   * @see #close()
   * @see DockingWindowListener#windowClosing(DockingWindow)
   * @since IDW 1.1.0
   */
  public void closeWithAbort() throws OperationAbortedException {
    fireClosing(this);
    close();
  }

  /**
   * Returns the index of a child windows.
   *
   * @param window the child window
   * @return the index of the child window, -1 if the window is not a child of this window
   */
  public int getChildWindowIndex(DockingWindow window) {
    for (int i = 0; i < getChildWindowCount(); i++)
      if (getChildWindow(i) == window)
        return i;

    return -1;
  }

  /**
   * Returns the popup menu factory for this window. If it's null the window parent popup menu factory will be used
   * when the mouse popup trigger is activated on this window.
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
   * @return the child window that last contained focus, null if no child window has contained focus or the child
   *         has been removed from this window
   */
  public DockingWindow getLastFocusedChildWindow() {
    return lastFocusedChildWindow;
  }

  /**
   * Maximizes this window in it's root window. If this window has no root window nothing happens.
   * This method takes the window component and displays it at the top in the root window. It does NOT modify the
   * window tree structure, ie the window parent remains the unchanged.
   *
   * <p>The location of this window is saved and the window can be restored to that location using the
   * {@link #restore()} method.</p>
   *
   * @since IDW 1.1.0
   */
  public final void maximize() {
    RootWindow rootWindow = getRootWindow();

    if (rootWindow != null)
      rootWindow.setMaximizedWindow(this);
  }

  /**
   * Returns true if this window has a root window and is maximized in that root window.
   *
   * @return true if this window has a root window and is maximized in that root window
   * @since IDW 1.1.0
   */
  public boolean isMaximized() {
    RootWindow rootWindow = getRootWindow();
    return rootWindow != null && rootWindow.getMaximizedWindow() == this;
  }

  /**
   * Minimizes this window. The window is minimized to the {@link WindowBar} where it was last placed.
   * If it hasn't been minimized before it is placed on the closest enabled {@link WindowBar}.
   * If no suitable {@link WindowBar} was found or this window already is minimized, no action is performed.
   *
   * <p>The location of this window is saved and the window can be restored to that location using the
   * {@link #restore()} method.</p>
   */
  public void minimize() {
    getOptimizedWindow().doMinimize();
  }

  private void doMinimize() {
    doMinimize(lastMinimizedDirection != null &&
               getRootWindow().getWindowBar(lastMinimizedDirection).isEnabled() ?
               lastMinimizedDirection :
               getRootWindow().getClosestWindowBar(this));
  }

  /**
   * Minimizes this window to a {@link WindowBar}located in <tt>direction</tt>. If no suitable {@link WindowBar}was
   * found or this window already is minimized, no action is performed.
   *
   * <p>The location of this window is saved and the window can be restored to that location using the
   * {@link #restore()} method.</p>
   *
   * @param direction the direction in which the window bar is located
   */
  public void minimize(Direction direction) {
    getOptimizedWindow().doMinimize(direction);
  }

  private void doMinimize(Direction direction) {
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

        if (oldWindow == lastFocusedChildWindow)
          lastFocusedChildWindow = null;

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
    StringBuffer title = new StringBuffer(40);

    DockingWindow window;
    for (int i = 0; i < getChildWindowCount(); i++) {
      window = getChildWindow(i);

      if (i > 0)
        title.append(", ");

      title.append(window != null ? window.getTitle() : "");
    }

    return title.toString();
  }

  public String toString() {
    return getTitle();
  }

  /**
   * Makes this window visible. This causes the tabs of all {@link TabWindow} parents containing this
   * window to be selected.
   *
   * @since IDW 1.1.0
   */
  public void makeVisible() {
    showChildWindow(null);
  }

  /**
   * Requests that the last focused child window becomes visible and that focus is restored to the last focused
   * component in that window. If no child window has had focus or the child window has been removed from this window,
   * focus is transferred to a child component of this window.
   *
   * @since IDW 1.1.0
   */
  public void restoreFocus() {
    if (lastFocusedChildWindow != null)
      lastFocusedChildWindow.restoreFocus();
    else {
      DockingWindow w = getPreferredFocusChild();

      if (w != null)
        w.restoreFocus();
      else
        ComponentUtil.smartRequestFocus(this);
    }
  }

  protected DockingWindow getPreferredFocusChild() {
    return getChildWindowCount() > 0 ? getChildWindow(0) : null;
  }

  /**
   * Returns the result after removing unnecessary tab windows which contains only one tab.
   *
   * @return the result after removing unnecessary tab windows which contains only one tab
   */
  protected DockingWindow getOptimizedWindow() {
    return this;
  }

  protected void internalClose() {
    optimizeAfter(windowParent, new Runnable() {
      public void run() {
        windowParent.removeChildWindow(DockingWindow.this);
      }
    });
  }

  protected void showChildWindow(DockingWindow window) {
    if (windowParent != null && !isMaximized())
      windowParent.showChildWindow(this);
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

  private DockingWindow[] getAncestors() {
    DockingWindow w = this;
    int count = 0;

    while (w != null) {
      w = w.getWindowParent();
      count++;
    }

    DockingWindow[] windows = new DockingWindow[count];
    w = this;

    while (w != null) {
      windows[--count] = w;
      w = w.getWindowParent();
    }

    return windows;
  }

  private void fireClosing(DockingWindow window) throws OperationAbortedException {
    if (listeners != null) {
      DockingWindowListener[] l = (DockingWindowListener[]) listeners.toArray(new DockingWindowListener[listeners.size()]);

      for (int i = 0; i < l.length; i++)
        l[i].windowClosing(window);
    }

    if (windowParent != null)
      windowParent.fireClosing(window);
  }

  private void fireClosed(DockingWindow window) {
    if (listeners != null) {
      DockingWindowListener[] l = (DockingWindowListener[]) listeners.toArray(new DockingWindowListener[listeners.size()]);

      for (int i = 0; i < l.length; i++)
        l[i].windowClosed(window);
    }
  }

  protected void setLastMinimizedDirection(Direction direction) {
    lastMinimizedDirection = direction;
  }

  protected void maximized(boolean maximized) {
  }

  /**
 *
   */
  protected void clearChildrenFocus(DockingWindow child, View view) {
    for (int i = 0; i < getChildWindowCount(); i++)
      if (child != getChildWindow(i))
        getChildWindow(i).clearFocus(view);
  }

  void childGainedFocus(DockingWindow child, View view) {
    if (child != null)
      lastFocusedChildWindow = child;

    clearChildrenFocus(child, view);

    if (windowParent != null)
      windowParent.childGainedFocus(this, view);
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

    if (version > 1) {
      int index = in.readInt();
      lastFocusedChildWindow = index == -1 ? null : getChildWindow(index);
    }

    for (int i = 0; i < getChildWindowCount(); i++)
      getChildWindow(i).readLocations(in, rootWindow, version);
  }

  /**
 *
   */
  protected void writeLocations(ObjectOutputStream out) throws IOException {
    lastLocation.write(out);
    out.writeInt(lastFocusedChildWindow == null ? -1 : getChildWindowIndex(lastFocusedChildWindow));

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
  protected static void optimizeAfter(final DockingWindow window, final Runnable runnable) {
    FocusManager.getInstance().pinFocus(new Runnable() {
      public void run() {
        beginOptimize(window);

        try {
          runnable.run();
        }
        finally {
          endOptimize();
        }
      }
    });
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
        window.setWindowParent(null);

        if (lastFocusedChildWindow == window)
          lastFocusedChildWindow = null;

        doRemoveWindow(window);
        fireTitleChanged();
      }
    });
  }

  final protected DockingWindow addWindow(DockingWindow window) {
    if (window == null)
      return null;

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
    for (int i = 0; i < getChildWindowCount(); i++)
      getChildWindow(i).clearFocus(view);
  }

  private void setWindowParent(DockingWindow window) {
    if (window == windowParent)
      return;

    final RootWindow oldRoot = getRootWindow();

    if (windowParent != null) {
      if (isMaximized())
        getRootWindow().setMaximizedWindow(null);

      windowParent.childRemoved(this);
      clearFocus(null);

      if (tab != null)
        tab.setContentComponent(this);
    }

    windowParent = window;
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

    if (acceptsCenterDrop() && dist[ArrayUtil.findSmallest(dist)] >
                               getRootWindow().getRootWindowProperties().getEdgeSplitDistance())
      return null;

    double[] relativeDist = new double[]{p.getX() / getWidth(),
                                         (getWidth() - p.getX()) / getWidth(), p.getY() / getHeight(),
                                         (getHeight() - p.getY()) / getHeight()};
    int index = ArrayUtil.findSmallest(relativeDist);
    return index == 0 ? Direction.LEFT : index == 1 ? Direction.RIGHT : index == 2 ? Direction.UP : Direction.DOWN;
  }

  DockingWindow acceptDrop(Point p, DockingWindow window) {
    if (!getRootWindow().getRootWindowProperties().getRecursiveTabsEnabled() && insideTab())
      return getWindowParent().acceptDrop(p, window);

    if (!isSplittable() || hasParent(window))
      return null;

    Direction splitDir = getSplitDirection(p);

    if (splitDir == null) {
      getRootWindow().setRectangle(SwingUtilities.convertRectangle(this,
                                                                   new Rectangle(0, 0, getWidth(), getHeight()),
                                                                   getRootWindow()));
    }
    else {
      int width = splitDir == Direction.LEFT || splitDir == Direction.RIGHT ? getWidth() / 3
                  : getWidth();
      int height = splitDir == Direction.DOWN || splitDir == Direction.UP ? getHeight() / 3
                   : getHeight();
      int x = splitDir == Direction.RIGHT ? getWidth() - width : 0;
      int y = splitDir == Direction.DOWN ? getHeight() - height : 0;

      Rectangle rect = new Rectangle(x, y, width, height);
      getRootWindow().setRectangle(SwingUtilities.convertRectangle(this, rect, getRootWindow()));
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
    return getWindowParent() == null ? false : getWindowParent() == w || getWindowParent().hasParent(w);
  }

  void doDrop(Point p, final DockingWindow window) {
    Direction splitDir = getSplitDirection(p);

    if (splitDir == null)
      optimizeAfter(window.getWindowParent(), new Runnable() {
        public void run() {
          TabWindow tabWindow = new TabWindow();
          windowParent.replaceChildWindow(DockingWindow.this, tabWindow);
          tabWindow.addTab(DockingWindow.this);
          tabWindow.addTab(window);
        }
      });
    else
      split(window, splitDir, 0.5F);

    FocusManager.focusWindow(window);
  }

  /**
 *
   */
  protected boolean isSplittable() {
    return true;//!isMaximized();
  }

  /**
 *
   */
  protected void write(ObjectOutputStream out, WriteContext context) throws IOException {
    out.writeInt(lastMinimizedDirection == null ? -1 : lastMinimizedDirection.getValue());

    if (context.getWritePropertiesEnabled()) {
      properties.getMap().write(out, true);
      getPropertyObject().write(out, true);
    }
  }

  /**
 *
   */
  protected DockingWindow read(ObjectInputStream in, ReadContext context) throws IOException {
    if (context.getVersion() > 1) {
      int dir = in.readInt();
      lastMinimizedDirection = dir == -1 ? null : Direction.getDirections()[dir];
    }

    if (context.isPropertyValuesAvailable()) {
      (context.getReadPropertiesEnabled() ? properties : new DockingWindowProperties()).getMap().read(in);
      (context.getReadPropertiesEnabled() ? getPropertyObject() : createPropertyObject()).read(in);
    }

    return this;
  }

  protected void storeLocation() {
    if (!isMinimized() && windowParent != null) {
      lastLocation = windowParent.getWindowLocation(this);

      for (int i = 0; i < getChildWindowCount(); i++)
        getChildWindow(i).storeLocation();
    }
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

  protected void setFocused(boolean focused) {
    if (tab != null)
      tab.setFocused(focused);
  }

}
