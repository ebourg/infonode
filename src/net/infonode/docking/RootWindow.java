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


// $Id: RootWindow.java,v 1.68 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.action.*;
import net.infonode.docking.internal.ReadContext;
import net.infonode.docking.internal.WriteContext;
import net.infonode.docking.internalutil.DropAction;
import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.location.WindowRootLocation;
import net.infonode.docking.model.*;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.gui.componentpainter.RectangleComponentPainter;
import net.infonode.gui.layout.BorderLayout2;
import net.infonode.gui.layout.LayoutUtil;
import net.infonode.gui.layout.StretchLayout;
import net.infonode.gui.mouse.MouseButtonListener;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.gui.shaped.panel.ShapedPanel;
import net.infonode.properties.gui.InternalPropertiesUtil;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapManager;
import net.infonode.util.ArrayUtil;
import net.infonode.util.Direction;
import net.infonode.util.ReadWritable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The root window is a top level container for docking windows. Docking windows can't be dragged outside of their root
 * window. The property values of a root window is inherited to the docking windows inside it.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.68 $
 */
public class RootWindow extends DockingWindow implements ReadWritable {
  private static final int SERIALIZE_VERSION = 3;

  private class SingleComponentLayout implements LayoutManager {
    public void addLayoutComponent(String name, Component comp) {
    }

    public void layoutContainer(Container parent) {
      Dimension size = LayoutUtil.getInteriorSize(parent);
      Insets insets = parent.getInsets();
      mainPanel.setBounds(insets.left, insets.top, size.width, size.height);

      int w1 = windowBars[Direction.LEFT.getValue()].getPreferredSize().width;
      int w2 = windowBars[Direction.RIGHT.getValue()].getPreferredSize().width;
      int h1 = windowBars[Direction.UP.getValue()].getPreferredSize().height;
      int h2 = windowBars[Direction.DOWN.getValue()].getPreferredSize().height;

      final Direction[] directions = Direction.getDirections();

      for (int i = 0; i < windowBars.length; i++) {
        Component panel = windowBars[i].getEdgePanel();

        if (panel.isVisible()) {
          Direction dir = directions[i];
          int maxWidth = size.width - w1 - w2;
          int maxHeight = size.height - h1 - h2;

          if (dir == Direction.RIGHT) {
            int rightX = parent.getWidth() - insets.right - w2 + windowBars[dir.getValue()].getInsets().left;
            int width = Math.min(panel.getPreferredSize().width,
                                 maxWidth + windowBars[dir.getValue()].getInsets().left);
            panel.setBounds(rightX - width, insets.top + h1, width, maxHeight);
          }
          else if (dir == Direction.LEFT) {
            int x = insets.left + w1 - windowBars[dir.getValue()].getInsets().right;
            int width = Math.min(panel.getPreferredSize().width,
                                 maxWidth + windowBars[dir.getValue()].getInsets().right);
            panel.setBounds(x, insets.top + h1, width, maxHeight);
          }
          else if (dir == Direction.DOWN) {
            int bottomY = parent.getHeight() - insets.bottom - h2 + windowBars[dir.getValue()].getInsets().top;
            int height = Math.min(panel.getPreferredSize().height,
                                  maxHeight + windowBars[dir.getValue()].getInsets().top);
            panel.setBounds(insets.left + w1, bottomY - height, maxWidth, height);
          }
          else {
            int y = insets.top + h1 - windowBars[dir.getValue()].getInsets().bottom;
            int height = Math.min(panel.getPreferredSize().height,
                                  maxHeight + windowBars[dir.getValue()].getInsets().bottom);
            panel.setBounds(insets.left + w1, y, maxWidth, height);
          }
        }
      }
    }

    public Dimension minimumLayoutSize(Container parent) {
      return LayoutUtil.add(mainPanel.getMinimumSize(), parent.getInsets());
    }

    public Dimension preferredLayoutSize(Container parent) {
      return LayoutUtil.add(mainPanel.getPreferredSize(), parent.getInsets());
    }

    public void removeLayoutComponent(Component comp) {
    }

  }

  private SimplePanel layeredPane = new SimplePanel() {
    public boolean isOptimizedDrawingEnabled() {
      return false;
    }
  };
  private ShapedPanel shapedPanel = new ShapedPanel(layeredPane);
  private SimplePanel mainPanel = new SimplePanel();
  private ShapedPanel windowPanel = new ShapedPanel(new StretchLayout(true, true));
  private ViewSerializer viewSerializer;
  private DockingWindow window;
  private JLabel textComponent = new JLabel();
  private ShapedPanel rectangleComponent = new ShapedPanel();
//  private View lastFocusedView;
  private WindowBar[] windowBars = new WindowBar[Direction.getDirections().length];
  private DockingWindow maximizedWindow;
  private View focusedView;
  private ArrayList lastFocusedWindows = new ArrayList(4);
  private ArrayList focusedWindows = new ArrayList(4);
  private ArrayList views = new ArrayList();
  private boolean cleanUpModel;
  private Runnable modelCleanUpEvent = new Runnable() {
    public void run() {
      if (cleanUpModel) {
        cleanUpModel = false;
        getWindowItem().cleanUp();
      }
    }
  };

  /**
   * Creates an empty root window.
   *
   * @param viewSerializer used when reading and writing views
   * @since IDW 1.1.0
   */
  public RootWindow(ViewSerializer viewSerializer) {
    super(new RootWindowItem());

    getWindowProperties().addSuperObject(getRootWindowProperties().getDockingWindowProperties());

    mainPanel.setLayout(new BorderLayout2());
    mainPanel.add(windowPanel, new Point(1, 1));

    createWindowBars();

    layeredPane.add(mainPanel);
    layeredPane.setLayout(new SingleComponentLayout());
    setComponent(shapedPanel);

    this.viewSerializer = viewSerializer;

    textComponent.setOpaque(true);

    addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        if (getRootPane() != null)
          if (getRootPane().getLayeredPane() != textComponent.getParent())
            addComponents();
      }
    });

    init();
    FocusManager.getInstance();

    addTabMouseButtonListener(new MouseButtonListener() {
      public void mouseButtonEvent(MouseEvent event) {
        if (event.isConsumed())
          return;

        DockingWindow window = (DockingWindow) event.getSource();

        if (event.getID() == MouseEvent.MOUSE_PRESSED &&
            event.getButton() == MouseEvent.BUTTON1 &&
            !event.isShiftDown() &&
            window.isShowing()) {
          RestoreFocusWindowAction.INSTANCE.perform(window);
        }
        else if (event.getID() == MouseEvent.MOUSE_CLICKED && event.getButton() == MouseEvent.BUTTON1) {
          if (event.getClickCount() == 2) {
            if ((window.getWindowParent() instanceof WindowBar) &&
                getRootWindowProperties().getDoubleClickRestoresWindow())
              RestoreWindowAction.INSTANCE.perform(window);
            else {
              new StateDependentWindowAction(MaximizeWindowAction.INSTANCE,
                                             NullWindowAction.INSTANCE,
                                             RestoreParentWindowAction.INSTANCE).perform(window);
            }
          }
        }
      }
    });
  }

  /**
   * Constructor.
   *
   * @param viewSerializer used when reading and writing views
   * @param window         the window that is placed inside the root window
   */
  public RootWindow(ViewSerializer viewSerializer, DockingWindow window) {
    this(viewSerializer);
    setWindow(window);
  }

  /**
   * Returns the view that currently contains the focus.
   *
   * @return The currently focused view, null if no view has focus
   */
  public View getFocusedView() {
    return focusedView;
  }

  void addFocusedWindow(DockingWindow window) {
    for (int i = 0; i < lastFocusedWindows.size(); i++) {
      if (((SoftReference) lastFocusedWindows.get(i)).get() == window)
        return;
    }

    lastFocusedWindows.add(new SoftReference(window));
  }

  void setFocusedView(View view) {
    if (view == focusedView)
      return;

//    System.out.println(focusedView + " -> " + view);

    View previouslyFocusedView = focusedView;
    focusedView = view;

    for (DockingWindow w = view; w != null; w = w.getWindowParent()) {
      focusedWindows.add(new SoftReference(w));

      for (int i = 0; i < lastFocusedWindows.size(); i++) {
        if (((SoftReference) lastFocusedWindows.get(i)).get() == w) {
          lastFocusedWindows.remove(i);
          break;
        }
      }
    }

    for (int i = 0; i < lastFocusedWindows.size(); i++) {
      DockingWindow w = (DockingWindow) ((SoftReference) lastFocusedWindows.get(i)).get();

      if (w != null) {
        w.setFocused(false);
      }
    }

    ArrayList temp = lastFocusedWindows;
    lastFocusedWindows = focusedWindows;
    focusedWindows = temp;

    for (int i = 0; i < lastFocusedWindows.size(); i++) {
      DockingWindow w = (DockingWindow) ((SoftReference) lastFocusedWindows.get(i)).get();

      if (w != null)
        w.setFocused(true);
    }

    if (view != null) {
      view.childGainedFocus(null, view);
    }
    else {
      clearFocus(null);
    }

    // Notify windows that are not ancestors of the new focused view
    for (int i = 0; i < focusedWindows.size(); i++) {
      DockingWindow w = (DockingWindow) ((SoftReference) focusedWindows.get(i)).get();

      if (w != null) {
        w.fireViewFocusChanged(previouslyFocusedView, focusedView);
      }
    }

    // Notify windows that are ancestors of the new focused view
    for (int i = 0; i < lastFocusedWindows.size(); i++) {
      DockingWindow w = (DockingWindow) ((SoftReference) lastFocusedWindows.get(i)).get();

      if (w != null) {
        w.fireViewFocusChanged(previouslyFocusedView, focusedView);
      }
    }

    focusedWindows.clear();
  }

  /**
   * Returns the property values for this root window. The property values will be inherited to docking windows inside
   * this root window.
   *
   * @return the property values for this root window
   */
  public RootWindowProperties getRootWindowProperties() {
    return ((RootWindowItem) getWindowItem()).getRootWindowProperties();
  }

  /**
   * Returns the direction of the closest enabled window bar to a docking window. The distance is measured from the
   * window edge that is furthest away from the bar.
   *
   * @param window the docking window
   * @return the direction of the closest enabled window bar to a docking window
   */
  public Direction getClosestWindowBar(DockingWindow window) {
    Point pos = SwingUtilities.convertPoint(window.getParent(), window.getLocation(), this);

    int[] distances = new int[]{
      getWindowBar(Direction.UP).isEnabled() ? pos.y + window.getHeight() : Integer.MAX_VALUE,
      getWindowBar(Direction.DOWN).isEnabled() ? getHeight() - pos.y : Integer.MAX_VALUE,
      getWindowBar(Direction.LEFT).isEnabled() ? pos.x + window.getWidth() : Integer.MAX_VALUE,
      getWindowBar(Direction.RIGHT).isEnabled() ? getWidth() - pos.x : Integer.MAX_VALUE};

    Direction dir = new Direction[]{Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT}[ArrayUtil.findSmallest(
        distances)];
    return getWindowBar(dir).isEnabled() ? dir : null;
  }

  /**
   * Returns the window bar in the direction.
   *
   * @param direction the direction
   * @return the window bar in the direction
   */
  public WindowBar getWindowBar(Direction direction) {
    return windowBars[direction.getValue()];
  }

  /**
   * Sets the top level docking window inside this root window.
   *
   * @param newWindow the top level docking window
   */
  public void setWindow(DockingWindow newWindow) {
    if (window == newWindow)
      return;

    if (window == null) {
      DockingWindow actualWindow = addWindow(newWindow);
      doReplace(null, actualWindow);

      if (getUpdateModel() && actualWindow.getWindowItem().getRootItem() != getWindowItem()) {
        getWindowItem().removeAll();
        getWindowItem().addWindow(actualWindow.getWindowItem());
      }
    }
    else if (newWindow == null) {
      removeChildWindow(window);
      window = null;
    }
    else
      replaceChildWindow(window, newWindow);
  }

  /**
   * Returns the top level docking window inside this root window.
   *
   * @return the top level docking window inside this root window
   */
  public DockingWindow getWindow() {
    return window;
  }

  /**
   * Returns the view serializer object for the views inside this root window.
   *
   * @return the view serializer object for the views inside this root window
   */
  public ViewSerializer getViewSerializer() {
    return viewSerializer;
  }

  public DockingWindow getChildWindow(int index) {
    return index < 4 ? windowBars[index] : window;
  }

  public int getChildWindowCount() {
    return 4 + (window == null ? 0 : 1);
  }

  public Icon getIcon() {
    return null;
  }

  /**
   * Writes the state of this root window and all child windows.
   *
   * @param out the stream on which to write the state
   * @throws IOException if there is a stream error
   */
  public void write(ObjectOutputStream out) throws IOException {
    write(out, true);
  }

  /**
   * Writes the state of this root window and all child windows.
   *
   * @param out             the stream on which to write the state
   * @param writeProperties true if the property values for all docking windows should be written to the stream
   * @throws IOException if there is a stream error
   */
  public void write(ObjectOutputStream out, boolean writeProperties) throws IOException {
    cleanUpModel();
    out.writeInt(SERIALIZE_VERSION);
    out.writeBoolean(writeProperties);
    WriteContext context = new WriteContext(writeProperties, getViewSerializer());

    final ArrayList v = new ArrayList();

    for (int i = 0; i < views.size(); i++) {
      View view = (View) ((WeakReference) views.get(i)).get();

      if (view != null)
        v.add(view);
    }

    writeViews(v, out, context);
    ViewWriter viewWriter = new ViewWriter() {
      public void writeWindowItem(WindowItem windowItem, ObjectOutputStream out, WriteContext context) throws IOException {
        if (windowItem.getRootItem() == getWindowItem()) {
          out.writeBoolean(true);
          writeWindowItemIndex(windowItem, out);
          out.writeInt(-1);
        }
        else {
          out.writeBoolean(false);
          windowItem.writeSettings(out, context);
        }
      }

      public void writeView(View view, ObjectOutputStream out, WriteContext context) throws IOException {
        for (int i = 0; i < v.size(); i++) {
          if (v.get(i) == view) {
            out.writeInt(i);
            return;
          }
        }

        out.writeInt(-1);
      }
    };
    getWindowItem().write(out, context, viewWriter);

    for (int i = 0; i < 4; i++)
      windowBars[i].write(out, context, viewWriter);

    writeLocations(out);

    if (maximizedWindow != null)
      writeMaximized(maximizedWindow, out);

    out.writeInt(-1);
  }

  private void writeWindowItemIndex(WindowItem item, ObjectOutputStream out) throws IOException {
    if (item.getParent() == null)
      return;

    writeWindowItemIndex(item.getParent(), out);
    int index = item.getParent().getWindowIndex(item);
    out.writeInt(index);
  }

  private void writeMaximized(DockingWindow window, ObjectOutputStream out) throws IOException {
    DockingWindow parent = window.getWindowParent();

    if (parent != null) {
      writeMaximized(parent, out);
      out.writeInt(parent.getChildWindowIndex(window));
    }
  }

  private void writeViews(ArrayList views, ObjectOutputStream out, WriteContext context) throws IOException {
    out.writeInt(views.size());

    for (int i = 0; i < views.size(); i++)
      ((View) views.get(i)).write(out, context);
  }

  /**
   * Reads a previously written window state. This will create child windows and read their state.
   *
   * @param in the stream from which to read the state
   * @throws IOException if there is a stream error
   */
  public void read(ObjectInputStream in) throws IOException {
    read(in, true);
  }

  private void oldInternalRead(ObjectInputStream in, ReadContext context) throws IOException {
    setWindow(in.readBoolean() ? WindowDecoder.decodeWindow(in, context) : null);

    for (int i = 0; i < 4; i++) {
      in.readInt(); // DockingWindow bar ID
      windowBars[i].oldRead(in, context);
    }

    super.oldRead(in, context);
    readLocations(in, this, context.getVersion());

    if (context.getVersion() > 1) {
      int viewCount = in.readInt();

      for (int i = 0; i < viewCount; i++) {
        View view = (View) WindowDecoder.decodeWindow(in, context);
        view.setRootWindow(this);
        view.readLocations(in, this, context.getVersion());
      }
    }
  }

  private void newInternalRead(ObjectInputStream in, ReadContext context) throws IOException {
    beginUpdateModel();

    try {
      int viewCount = in.readInt();
      final View[] views = new View[viewCount];

      for (int i = 0; i < viewCount; i++) {
        views[i] = View.read(in, context);

        if (views[i] != null)
          views[i].setRootWindow(this);
      }

      ViewReader viewReader = new ViewReader() {
        public ViewItem readViewItem(ObjectInputStream in, ReadContext context) throws IOException {
          View view = readView(in, context);
          return view == null ? new ViewItem() : (ViewItem) view.getWindowItem();
        }

        public WindowItem readWindowItem(ObjectInputStream in, ReadContext context) throws IOException {
          if (in.readBoolean()) {
            int index;
            WindowItem item = getWindowItem();

            while ((index = in.readInt()) != -1) {
              item = item.getWindow(index);
            }

            return item;
          }
          else
            return null;
        }

        public TabWindow createTabWindow(DockingWindow[] childWindows, TabWindowItem windowItem) {
          TabWindow tabWindow = new TabWindow(childWindows, windowItem);
          tabWindow.updateSelectedTab();
          return tabWindow;
        }

        public SplitWindow createSplitWindow(DockingWindow leftWindow,
                                             DockingWindow rightWindow,
                                             SplitWindowItem windowItem) {
          return new SplitWindow(windowItem.isHorizontal(),
                                 windowItem.getDividerLocation(),
                                 leftWindow,
                                 rightWindow,
                                 windowItem);
        }

        public View readView(ObjectInputStream in, ReadContext context) throws IOException {
          int id = in.readInt();
          return id == -1 ? null : (View) views[id];
        }
      };
      setWindow(getWindowItem().read(in, context, viewReader));

      for (int i = 0; i < 4; i++)
        windowBars[i].newRead(in, context, viewReader);

      readLocations(in, this, context.getVersion());
    }
    finally {
      endUpdateModel();
    }
  }

  /**
   * Reads a previously written window state. This will create child windows and read their state.
   *
   * @param in             the stream from which to read the state
   * @param readProperties true if the property values for all child windows should be read. This parameter can be set
   *                       to true or false regardless of if the property values was included when the state was
   *                       written, though obviously no property values are read if there aren't any in the stream.
   * @throws IOException if there is a stream error
   */
  public void read(ObjectInputStream in, boolean readProperties) throws IOException {
    FocusManager.getInstance().startIgnoreFocusChanges();
    PropertyMapManager.getInstance().beginBatch();

    try {
      setWindow(null);
      int serializeVersion = in.readInt();

      if (serializeVersion > SERIALIZE_VERSION)
        throw new IOException(
            "Can't read serialized data because it was written by a later version of InfoNode Docking Windows!");

      ReadContext context = new ReadContext(viewSerializer, serializeVersion, in.readBoolean(), readProperties);

      if (context.getVersion() < 3)
        oldInternalRead(in, context);
      else
        newInternalRead(in, context);

      if (serializeVersion > 1)
        readMaximized(in);

      FocusManager.focusWindow(this);
    }
    finally {
      PropertyMapManager.getInstance().endBatch();
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          FocusManager.getInstance().stopIgnoreFocusChanges();
        }
      });
    }
  }

  private void readMaximized(ObjectInputStream in) throws IOException {
    int index;
    DockingWindow w = this;

    while ((index = in.readInt()) != -1) {
      if (index >= w.getChildWindowCount()) {
        while (in.readInt() != -1) ;
        return;
      }

      w = w.getChildWindow(index);
    }

    if (w != this)
      setMaximizedWindow(w);
  }

  /**
   * Returns the maximized window in this root window, or null if there no maximized window.
   *
   * @return the maximized window in this root window, or null if there no maximized window
   * @since IDW 1.1.0
   */
  public DockingWindow getMaximizedWindow() {
    return maximizedWindow;
  }

  /**
   * Sets the maximized window in this root window.
   * This method takes the window component and displays it at the top in the root window. It does NOT modify the
   * window tree structure, ie the window parent remains the unchanged.
   *
   * @param window the maximized window in this root window, null means no maximized window
   * @since IDW 1.1.0
   */
  public void setMaximizedWindow(DockingWindow window) {
    if (window == maximizedWindow)
      return;

    if (window != null && window.isMinimized())
      return;

    internalSetMaximizedWindow(window);

    if (window != null)
      FocusManager.focusWindow(window);
  }

  void addView(View view) {
    int freeIndex = views.size();

    for (int i = 0; i < views.size(); i++) {
      View v = (View) ((WeakReference) views.get(i)).get();

      if (v == view)
        return;

      if (v == null)
        freeIndex = i;
    }

    views.add(freeIndex, new WeakReference(view));
  }

  void removeView(View view) {
    for (int i = 0; i < views.size(); i++) {
      View v = (View) ((WeakReference) views.get(i)).get();

      if (v == view) {
        views.remove(i);
        return;
      }
    }
  }

  private void internalSetMaximizedWindow(DockingWindow window) {
    if (window == maximizedWindow)
      return;

    if (maximizedWindow != null) {
      DockingWindow oldMaximized = maximizedWindow;
      maximizedWindow = null;

      if (oldMaximized.getWindowParent() != null)
        oldMaximized.getWindowParent().restoreWindowComponent(oldMaximized);

      oldMaximized.maximized(false);

      if (oldMaximized != this.window)
        windowPanel.remove(oldMaximized);
    }

    maximizedWindow = window;

    if (maximizedWindow != null) {
      if (maximizedWindow.getWindowParent() != null)
        maximizedWindow.getWindowParent().removeWindowComponent(maximizedWindow);

      if (maximizedWindow != this.window) {
        windowPanel.add(maximizedWindow);

        if (this.window != null)
          this.window.setVisible(false);
      }

      maximizedWindow.maximized(true);
      maximizedWindow.setVisible(true);
    }
    else if (this.window != null) {
      this.window.setVisible(true);
    }
  }

  void setDragText(Point textPoint, String text) {
    if (textPoint != null) {
      if (textComponent.getParent() == null) {
        addComponents();
      }

      textComponent.setVisible(true);
      textComponent.setText(text);
      textComponent.setSize(textComponent.getPreferredSize());
      Point p2 = SwingUtilities.convertPoint(this, textPoint, textComponent.getParent());
      textComponent.setLocation((int) (p2.getX() - textComponent.getWidth() / 2),
                                (int) (p2.getY() - textComponent.getHeight()));
    }
    else {
      textComponent.setVisible(false);
    }
  }

  void setDragRectangle(Rectangle rect) {
    if (rect != null) {
      if (textComponent.getParent() == null) {
        addComponents();
      }

      rectangleComponent.setVisible(true);
      rectangleComponent.setBounds(SwingUtilities.convertRectangle(this, rect, rectangleComponent.getParent()));
    }
    else {
      rectangleComponent.setVisible(false);
    }
  }

  private void createWindowBars() {
    final Direction[] directions = Direction.getDirections();

    for (int i = 0; i < directions.length; i++) {
      windowBars[i] = new WindowBar(this, directions[i]);
      windowBars[i].setEnabled(false);
      addWindow(windowBars[i]);
      layeredPane.add(windowBars[i].getEdgePanel());

      mainPanel.add(windowBars[i],
                    new Point(directions[i] == Direction.LEFT ?
                              0 :
                              directions[i] == Direction.RIGHT ? 2 : 1,
                              directions[i] == Direction.UP ?
                              0 :
                              directions[i] == Direction.DOWN ? 2 : 1));

      windowBars[i].addPropertyChangeListener("enabled", new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          updateMinimizable();
        }
      });
    }
  }

  protected void showChildWindow(DockingWindow window) {
    if (maximizedWindow != null && window == this.window)
      setMaximizedWindow(null);

    super.showChildWindow(window);
  }

  protected void update() {
    RootWindowProperties properties = getRootWindowProperties();
    properties.getComponentProperties().applyTo(shapedPanel);
    InternalPropertiesUtil.applyTo(properties.getShapedPanelProperties(), shapedPanel);
    properties.getWindowAreaProperties().applyTo(windowPanel);
    InternalPropertiesUtil.applyTo(properties.getWindowAreaShapedPanelProperties(), windowPanel);
    properties.getDragLabelProperties().applyTo(textComponent);

    InternalPropertiesUtil.applyTo(properties.getDragRectangleShapedPanelProperties(), rectangleComponent);

    if (rectangleComponent.getComponentPainter() == null)
      rectangleComponent.setComponentPainter(new RectangleComponentPainter(Color.BLACK,
                                                                           Color.WHITE,
                                                                           properties.getDragRectangleBorderWidth()));
  }

  private void addComponents() {
    getRootPane().getLayeredPane().add(rectangleComponent);
    getRootPane().getLayeredPane().setPosition(rectangleComponent, 1);
    getRootPane().getLayeredPane().add(textComponent);
    getRootPane().getLayeredPane().setPosition(textComponent, 0);
  }

  protected void doReplace(DockingWindow oldWindow, DockingWindow newWindow) {
    if (oldWindow == window) {
      if (window != null) {
        windowPanel.remove(window);
        window.setVisible(true);
      }

      window = newWindow;

      if (window != null) {
        if (maximizedWindow != null)
          window.setVisible(false);

        windowPanel.add(window);
        revalidate();
      }
    }
  }

  protected void doRemoveWindow(DockingWindow window) {
    if (window == this.window) {
      windowPanel.remove(window);
      this.window.setVisible(true);
      this.window = null;
    }

    repaint();
  }

  public RootWindow getRootWindow() {
    return this;
  }

  protected boolean acceptsSplitWith(DockingWindow window) {
    return false;
  }

  protected DropAction doAcceptDrop(Point p, DockingWindow window) {
    if (maximizedWindow != null) {
      Point p2 = SwingUtilities.convertPoint(this, p, maximizedWindow);

      if (maximizedWindow.contains(p2)) {
        DropAction da = maximizedWindow.acceptDrop(p2, window);

        if (da != null)
          return da;
      }
    }

    return super.doAcceptDrop(p, window);
  }

  protected DropAction acceptInteriorDrop(Point p, DockingWindow window) {
    if (this.window != null)
      return null;

    return new DropAction() {
      public void execute(DockingWindow window, MouseEvent mouseEvent) {
        setWindow(window);
      }
    };
  }

  protected WindowLocation getWindowLocation(DockingWindow window) {
    return new WindowRootLocation(this);
  }

  protected PropertyMap getPropertyObject() {
    return getRootWindowProperties().getMap();
  }

  protected PropertyMap createPropertyObject() {
    return new RootWindowProperties().getMap();
  }

  boolean windowBarEnabled() {
    for (int i = 0; i < windowBars.length; i++)
      if (windowBars[i].isEnabled())
        return true;

    return false;
  }

  void removeWindowComponent(DockingWindow window) {
  }

  void restoreWindowComponent(DockingWindow window) {
  }

  protected void cleanUpModel() {
    if (!cleanUpModel) {
      cleanUpModel = true;
      SwingUtilities.invokeLater(modelCleanUpEvent);
    }
  }

  protected boolean isShowingInRootWindow() {
    return true;
  }
}
