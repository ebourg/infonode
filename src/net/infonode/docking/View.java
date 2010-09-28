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


// $Id: View.java,v 1.39 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.internal.ReadContext;
import net.infonode.docking.internal.WriteContext;
import net.infonode.docking.internalutil.DropAction;
import net.infonode.docking.location.NullLocation;
import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.model.ViewItem;
import net.infonode.docking.model.ViewWriter;
import net.infonode.docking.properties.ViewProperties;
import net.infonode.gui.ComponentUtil;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.properties.base.Property;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapManager;
import net.infonode.properties.propertymap.PropertyMapWeakListenerManager;
import net.infonode.properties.util.PropertyChangeListener;
import net.infonode.util.StreamUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.*;
import java.lang.ref.WeakReference;

/**
 * A view is a docking window containing a component.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.39 $
 */
public class View extends DockingWindow {
  private Component lastFocusedComponent;
  private HierarchyListener focusComponentListener = new HierarchyListener() {
    public void hierarchyChanged(HierarchyEvent e) {
      checkLastFocusedComponent();
    }
  };
  private SimplePanel contentPanel = new SimplePanel();
  private ViewProperties rootProperties = new ViewProperties();
  private WeakReference lastRootWindow;
  private PropertyChangeListener listener = new PropertyChangeListener() {
    public void propertyChanged(Property property, Object valueContainer, Object oldValue, Object newValue) {
      fireTitleChanged();
    }
  };

  /**
   * Constructor.
   *
   * @param title     the title of the view
   * @param icon      the icon for the view
   * @param component the component to place inside the view
   */
  public View(String title, Icon icon, Component component) {
    super(new ViewItem());

    rootProperties.setTitle(title);
    rootProperties.setIcon(icon);
    getViewProperties().addSuperObject(rootProperties);
    super.setComponent(contentPanel);
    contentPanel.setComponent(component);

    PropertyMapWeakListenerManager.addWeakPropertyChangeListener(getViewProperties().getMap(),
                                                                 ViewProperties.TITLE,
                                                                 listener);
    PropertyMapWeakListenerManager.addWeakPropertyChangeListener(getViewProperties().getMap(),
                                                                 ViewProperties.ICON,
                                                                 listener);
    init();
  }

  /**
   * <p>
   * Returns a list containing the custom window tab components. Changes to the list will be propagated to the tab.
   * </p>
   * <p>
   * The custom tab components will be shown after the window title when the window tab is highlighted. The
   * components are shown in the same order as they appear in the list. The custom tab components container layout is
   * rotated with the tab direction.
   * </p>
   *
   * @return a list containing the custom tab components, list elements are of type {@link JComponent}
   * @since IDW 1.3.0
   */
  public java.util.List getCustomTabComponents() {
    return getTab().getCustomTabComponentsList();
  }

  /**
   * Gets the component inside the view.
   *
   * @return the component inside the view
   * @since IDW 1.1.0
   */
  public Component getComponent() {
    return contentPanel.getComponent(0);
  }

  /**
   * Sets the component inside the view.
   *
   * @param component the component to place inside the view
   * @since IDW 1.1.0
   */
  public void setComponent(Component component) {
    contentPanel.setComponent(component);
  }

  /**
   * Returns the property values for this view.
   *
   * @return the property values for this view
   */
  public ViewProperties getViewProperties() {
    return ((ViewItem) getWindowItem()).getViewProperties();
  }

  protected void update() {
    // TODO:
  }

  public DockingWindow getChildWindow(int index) {
    return null;
  }

  public int getChildWindowCount() {
    return 0;
  }

  protected WindowLocation getWindowLocation(DockingWindow window) {
    return NullLocation.INSTANCE;
  }

  void setLastFocusedComponent(Component component) {
    if (component != lastFocusedComponent) {
      if (lastFocusedComponent != null)
        lastFocusedComponent.removeHierarchyListener(focusComponentListener);

//      System.out.println("Focus: " + this + ", " + component.getClass() + " " + System.identityHashCode(component));
      lastFocusedComponent = component;

      if (lastFocusedComponent != null)
        lastFocusedComponent.addHierarchyListener(focusComponentListener);
    }
  }

  Component getFocusComponent() {
    checkLastFocusedComponent();
    return lastFocusedComponent;
  }

  public boolean isFocusCycleRoot() {
    return true;
  }

  /**
   * Restores focus to the last focused child component or, if no child component has had focus,
   * the first focusable component inside the view.
   *
   * @since IDW 1.1.0
   */
  public void restoreFocus() {
    makeVisible();
    checkLastFocusedComponent();

    if (lastFocusedComponent == null) {
      ComponentUtil.smartRequestFocus(contentPanel);
    }
    else {
//      System.out.println("Restore: " + this + ", " + lastFocusedComponent.getClass() + " " + System.identityHashCode(lastFocusedComponent));
      lastFocusedComponent.requestFocusInWindow();
    }
  }

  public Icon getIcon() {
    return getViewProperties().getIcon();
  }

  protected void doReplace(DockingWindow oldWindow, DockingWindow newWindow) {
    throw new RuntimeException(View.class + ".replaceChildWindow called!");
  }

  protected void doRemoveWindow(DockingWindow window) {
    throw new RuntimeException(View.class + ".removeChildWindow called!");
  }

  protected void write(ObjectOutputStream out, WriteContext context) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    context.getViewSerializer().writeView(this, oos);
    getWindowItem().writeSettings(oos, context);
    oos.close();
    out.writeInt(baos.size());
    baos.writeTo(out);
  }

  static View read(ObjectInputStream in, ReadContext context) throws IOException {
    int size = in.readInt();
    byte[] viewData = new byte[size];
    StreamUtil.readAll(in, viewData);
    ObjectInputStream viewIn = new ObjectInputStream(new ByteArrayInputStream(viewData));
    View view = context.getViewSerializer().readView(viewIn);

    if (view != null)
      view.getWindowItem().readSettings(viewIn, context);

    return view;
  }

  protected DropAction doAcceptDrop(Point p, DockingWindow window) {
    return getWindowParent() instanceof TabWindow && getWindowParent().getChildWindowCount() == 1 ?
           null :
           super.doAcceptDrop(p, window);
  }

  public String toString() {
    return getTitle();
  }

  void setRootWindow(RootWindow newRoot) {
    if (newRoot != null) {
      if (lastRootWindow != null) {
        RootWindow last = (RootWindow) lastRootWindow.get();

        if (last != null)
          last.removeView(this);
      }

      lastRootWindow = new WeakReference(newRoot);
      newRoot.addView(this);
    }

  }

  protected void rootChanged(final RootWindow oldRoot, final RootWindow newRoot) {
    super.rootChanged(oldRoot, newRoot);
    setRootWindow(newRoot);

    PropertyMapManager.runBatch(new Runnable() {
      public void run() {
        if (oldRoot != null)
          rootProperties.removeSuperObject(oldRoot.getRootWindowProperties().getViewProperties());

        if (newRoot != null) {
          rootProperties.addSuperObject(newRoot.getRootWindowProperties().getViewProperties());
        }
      }
    });
  }

  protected PropertyMap getPropertyObject() {
    return getViewProperties().getMap();
  }

  protected PropertyMap createPropertyObject() {
    return new ViewProperties().getMap();
  }

  protected boolean needsTitleWindow() {
    return getViewProperties().getAlwaysShowTitle();
  }

  private void checkLastFocusedComponent() {
    if (lastFocusedComponent != null && !SwingUtilities.isDescendingFrom(lastFocusedComponent, this)) {
      lastFocusedComponent.removeHierarchyListener(focusComponentListener);
      lastFocusedComponent = null;
    }
  }

  void removeWindowComponent(DockingWindow window) {
  }

  void restoreWindowComponent(DockingWindow window) {
  }

  protected void write(ObjectOutputStream out, WriteContext context, ViewWriter viewWriter) throws IOException {
    out.writeInt(WindowIds.VIEW);
    viewWriter.writeView(this, out, context);
  }

}
