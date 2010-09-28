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


// $Id: View.java,v 1.29 2004/11/11 14:09:46 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.internalutil.DropAction;
import net.infonode.docking.location.NullLocation;
import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.properties.ViewProperties;
import net.infonode.gui.ComponentUtil;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.properties.base.Property;
import net.infonode.properties.propertymap.PropertyMap;
import net.infonode.properties.propertymap.PropertyMapManager;
import net.infonode.properties.propertymap.PropertyMapWeakListenerManager;
import net.infonode.properties.util.PropertyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;

/**
 * A view is a docking window containing a component.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.29 $
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
  private ViewProperties viewProperties = new ViewProperties(rootProperties);
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
    rootProperties.setTitle(title);
    rootProperties.setIcon(icon);
    super.setComponent(contentPanel);
    contentPanel.setComponent(component);

    PropertyMapWeakListenerManager.addWeakPropertyChangeListener(viewProperties.getMap(),
                                                                 ViewProperties.TITLE,
                                                                 listener);
    PropertyMapWeakListenerManager.addWeakPropertyChangeListener(viewProperties.getMap(),
                                                                 ViewProperties.ICON,
                                                                 listener);
    init();
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
    return viewProperties;
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

  public String getTitle() {
    return getViewProperties().getTitle();
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
    out.writeInt(WindowIds.VIEW);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);

    context.getViewSerializer().writeView(this, oos);
    super.write(oos, context);

    oos.close();
    out.writeInt(baos.size());
    baos.writeTo(out);
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
          rootProperties.getMap().removeSuperMap();

        if (newRoot != null) {
          rootProperties.addSuperObject(newRoot.getRootWindowProperties().getViewProperties());
        }
      }
    });
  }

  protected PropertyMap getPropertyObject() {
    return viewProperties.getMap();
  }

  protected PropertyMap createPropertyObject() {
    return new ViewProperties().getMap();
  }

  protected boolean needsTitleWindow() {
    return viewProperties.getAlwaysShowTitle();
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
}
