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


// $Id: RootWindow.java,v 1.19 2004/08/11 13:47:58 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.location.WindowRootLocation;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.gui.layout.BorderLayout2;
import net.infonode.gui.layout.LayoutUtil;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.util.ArrayUtil;
import net.infonode.util.Direction;
import net.infonode.util.ReadWritable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The root window is a top level container for docking windows. Docking windows can't be dragged outside of their root
 * window. The property values of a root window is inherited to the docking windows inside it.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.19 $
 */
public class RootWindow extends DockingWindow implements ReadWritable {
  private static final int SERIALIZE_VERSION = 1;

  private class SingleComponentLayout implements LayoutManager {
    public void addLayoutComponent(String name, Component comp) {
    }

    public void layoutContainer(Container parent) {
      Dimension size = LayoutUtil.getInteriorSize(parent);
      Insets insets = parent.getInsets();
      windowPanel.setBounds(insets.left, insets.top, size.width, size.height);

      int w1 = windowBars[Direction.LEFT.getValue()].getPreferredSize().width;
      int w2 = windowBars[Direction.RIGHT.getValue()].getPreferredSize().width;
      int h1 = windowBars[Direction.UP.getValue()].getPreferredSize().height;
      int h2 = windowBars[Direction.DOWN.getValue()].getPreferredSize().height;

      for (int i = 0; i < windowBars.length; i++) {
        Component panel = windowBars[i].getEdgePanel();

        if (panel.isVisible()) {
          Direction dir = Direction.DIRECTIONS[i];
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
      return LayoutUtil.add(windowPanel.getMinimumSize(), parent.getInsets());
    }

    public Dimension preferredLayoutSize(Container parent) {
      return LayoutUtil.add(windowPanel.getPreferredSize(), parent.getInsets());
    }

    public void removeLayoutComponent(Component comp) {
    }

  }

  private SingleComponentLayout layerLayout = new SingleComponentLayout();
  private JLayeredPane layeredPane = new JLayeredPane();
  private SimplePanel windowPanel = new SimplePanel();
  private SimplePanel centerPanel = new SimplePanel();
  private ViewSerializer viewSerializer;
  private DockingWindow window;
  private JLabel textComponent = new JLabel();
  private RectangleBorderComponent rectangleComponent = new RectangleBorderComponent(0);
  private RootWindowProperties properties;
//  private View lastFocusedView;
  private View focusedView;
  private int ignoreFocusChanges;
  private boolean focusListenerAdded = false;
  private java.beans.PropertyChangeListener focusListener = new java.beans.PropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent evt) {
      if (ignoreFocusChanges > 0)
        return;

      ignoreFocusChanges++;

      try {
        updateFocus();
      }
      finally {
        ignoreFocusChanges--;
      }
    }
  };
  private WindowBar[] windowBars = new WindowBar[Direction.DIRECTIONS.length];

  /**
   * Constructor.
   *
   * @param viewSerializer used when reading and writing views
   * @param window         the window that is placed inside the root window
   */
  public RootWindow(ViewSerializer viewSerializer, DockingWindow window) {
    this.properties = RootWindowProperties.createDefault();

    getWindowProperties().addSuperObject(properties.getDockingWindowProperties());

    windowPanel.setLayout(new BorderLayout2());
    windowPanel.add(centerPanel, new Point(1, 1));

    createWindowBars();

    layeredPane.add(windowPanel);
    layeredPane.setLayout(layerLayout);
    setComponent(layeredPane);

    this.viewSerializer = viewSerializer;
    setWindow(window);

    textComponent.setOpaque(true);

    addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        if (getRootPane() != null)
          if (getRootPane().getLayeredPane() != textComponent.getParent())
            addComponents();
      }
    });

    init();
    registerFocusListener();
  }

  private void registerFocusListener() {
    addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        if (isDisplayable()) {
          if (!focusListenerAdded) {
            focusListenerAdded = true;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner",
                                                                                            focusListener);
            updateFocus();
          }
        }
        else if (focusListenerAdded) {
          KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener("focusOwner",
                                                                                             focusListener);
          focusListenerAdded = false;
        }
      }
    });
  }

  private void updateFocus() {
    Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
//    System.out.println("Focus: " + c);

    if (c == null) {
      return;
    }

    View view = getViewContaining(c);

    if (view != null) {
      view.setLastFocusedComponent(c);
    }

    if (view != focusedView) {
//      System.out.println("View: " + focusedView + " -> " + view);

      if (view != null) {
        view.childGainedFocus(null, view);
//              lastFocusedView = view;
      }
      else {
        clearFocus(null);
      }

      focusedView = view;
    }
  }

  /**
   * Returns the view that currently contains the focus.
   *
   * @return The currently focused view, null if no view has focus
   */
  public View getFocusedView() {
    return focusedView;
  }

  /**
   * Returns the property values for this root window. The property values will be inherited to docking windows inside
   * this root window.
   *
   * @return the property values for this root window
   */
  public RootWindowProperties getRootWindowProperties() {
    return properties;
  }

  /**
   * Returns the direction of the closest enabled window bar to a docking window. The distance is measured from the
   * window edge that is furthest away from the bar.
   *
   * @param window the docking window
   *
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
   *
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
      doReplace(null, addWindow(newWindow));
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
    return window == null ? 4 : 5;
  }

  public Icon getIcon() {
    return null;
  }

  /**
   * Writes the state of this root window and all child windows.
   *
   * @param out the stream on which to write the state
   *
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
   *
   * @throws IOException if there is a stream error
   */
  public void write(ObjectOutputStream out, boolean writeProperties) throws IOException {
    out.writeInt(SERIALIZE_VERSION);
    out.writeBoolean(writeProperties);
    out.writeBoolean(window != null);

    WriteContext context = new WriteContext(writeProperties);

    if (window != null)
      window.write(out, context);

    for (int i = 0; i < 4; i++) {
      windowBars[i].write(out, context);
    }

    super.write(out, context);
    writeLocations(out);
  }

  /**
   * Reads a previously written state. This will create child windows and read their state.
   *
   * @param in the stream from which to read the state
   *
   * @throws IOException if there is a stream error
   */
  public void read(ObjectInputStream in) throws IOException {
    read(in, true);
  }

  /**
   * Reads a previously written state. This will create child windows and read their state.
   *
   * @param in             the stream from which to read the state
   * @param readProperties true if the property values for all child windows should be read. This parameter can be set
   *                       to true or false regardless of if the property values was included when the state was
   *                       written, though obviously no property values are read if there aren't any in the stream.
   *
   * @throws IOException if there is a stream error
   */
  public void read(ObjectInputStream in, boolean readProperties) throws IOException {
    int serializeVersion = in.readInt();

    if (serializeVersion > SERIALIZE_VERSION)
      throw new IOException(
          "Can't read serialized data because the serialized version is greater than current version!");

    ReadContext context = new ReadContext(viewSerializer, serializeVersion, in.readBoolean(), readProperties);
    setWindow(in.readBoolean() ? WindowDecoder.decodeWindow(in, context) : null);

    for (int i = 0; i < 4; i++) {
      in.readInt(); // DockingWindow bar ID
      windowBars[i].read(in, context);
    }

    super.read(in, context);
    readLocations(in, this, serializeVersion);
  }

  void setText(Point textPoint, String text) {
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

  private void createWindowBars() {
    for (int i = 0; i < Direction.DIRECTIONS.length; i++) {
      windowBars[i] = new WindowBar(this, Direction.DIRECTIONS[i]);
      windowBars[i].setEnabled(false);
      addWindow(windowBars[i]);
      layeredPane.add(windowBars[i].getEdgePanel(), new Integer(1));

      windowPanel.add(windowBars[i],
                      new Point(Direction.DIRECTIONS[i] == Direction.LEFT ?
                                0 :
                                Direction.DIRECTIONS[i] == Direction.RIGHT ? 2 : 1,
                                Direction.DIRECTIONS[i] == Direction.UP ?
                                0 :
                                Direction.DIRECTIONS[i] == Direction.DOWN ? 2 : 1));

      windowBars[i].addPropertyChangeListener("enabled", new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          updateMinimizable();
        }
      });
    }
  }

  protected void update() {
    properties.getComponentProperties().applyTo(this);
    properties.getWindowAreaProperties().applyTo(centerPanel);
    properties.getDragLabelProperties().applyTo(textComponent);

    rectangleComponent.setLineWidth(properties.getDragRectangleBorderWidth());
  }

  private View getViewContaining(Component c) {
    return c == null ?
           null :
           c instanceof View && ((View) c).getRootWindow() == this ? (View) c : getViewContaining(c.getParent());
  }

  private void addComponents() {
    getRootPane().getLayeredPane().add(rectangleComponent);
    getRootPane().getLayeredPane().setPosition(rectangleComponent, 1);
    getRootPane().getLayeredPane().add(textComponent);
    getRootPane().getLayeredPane().setPosition(textComponent, 0);
  }

  void setRectangle(Rectangle rect) {
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

  protected void doReplace(DockingWindow oldWindow, DockingWindow newWindow) {
    window = newWindow;
    centerPanel.setComponent(newWindow);
    revalidate();
  }

  protected void doRemoveWindow(DockingWindow window) {
    centerPanel.remove(window);
    this.window = null;
    repaint();
  }

  public RootWindow getRootWindow() {
    return this;
  }

  protected boolean isSplittable() {
    return false;
  }

  DockingWindow acceptDrop(Point p, DockingWindow window) {
    return this.window == null ? this : null;
  }

  void doDrop(Point p, DockingWindow window) {
    if (this.window == null)
      setWindow(window);
  }

  protected WindowLocation getWindowLocation(DockingWindow window) {
    return new WindowRootLocation(this);
  }

  protected PropertyMap getPropertyObject() {
    return properties.getMap();
  }

  protected PropertyMap createPropertyObject() {
    return new RootWindowProperties().getMap();
  }

  void startIgnoreFocusChanges() {
    ignoreFocusChanges++;
  }

  void stopIgnoreFocusChanges() {
    if (--ignoreFocusChanges == 0)
      updateFocus();
  }

  void ignoreFocusChanges(final Runnable runnable) {
    startIgnoreFocusChanges();

    try {
      runnable.run();
    }
    finally {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          stopIgnoreFocusChanges();
        }
      });
    }
  }

  boolean windowBarEnabled() {
    for (int i = 0; i < windowBars.length; i++)
      if (windowBars[i].isEnabled())
        return true;

    return false;
  }

}
