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


// $Id: View.java,v 1.13 2004/08/11 12:22:56 jesper Exp $
package net.infonode.docking;

import net.infonode.docking.location.NullLocation;
import net.infonode.docking.location.WindowLocation;
import net.infonode.docking.properties.ViewProperties;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.properties.base.Property;
import net.infonode.properties.util.PropertyChangeListener;
import net.infonode.properties.propertymap.PropertyMapManager;
import net.infonode.properties.propertymap.PropertyMap;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A view is a docking window containing a component.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.13 $
 */
public class View extends DockingWindow {
  private Component lastFocusedComponent;
  private SimplePanel contentPanel = new SimplePanel();
  private ViewProperties rootProperties = new ViewProperties();
  private ViewProperties viewProperties = new ViewProperties(rootProperties);

  /**
   * Constructor.
   *
   * @param title the title of the view
   * @param icon the icon for the view
   * @param component the component to place inside the view
   */
  public View(String title, Icon icon, Component component) {
    rootProperties.setTitle(title);
    rootProperties.setIcon(icon);
    setComponent(contentPanel);
    contentPanel.setComponent(component);
    
    PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChanged(Property property, Object valueContainer, Object oldValue, Object newValue) {
				fireTitleChanged();
			}
    };
    
    getViewProperties().getMap().addPropertyChangeListener(ViewProperties.TITLE, listener);
    getViewProperties().getMap().addPropertyChangeListener(ViewProperties.ICON, listener);
    init();
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
    lastFocusedComponent = component;
  }

  public boolean isFocusCycleRoot() {
    return true;
  }

  void restoreFocus() {
    if (lastFocusedComponent != null) {
      lastFocusedComponent.requestFocusInWindow();
    }
    else {
      contentPanel.requestFocusInWindow();
    }

    childGainedFocus(null, this);
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

    getRootWindow().getViewSerializer().writeView(this, oos);
    super.write(oos, context);

    oos.close();
    out.writeInt(baos.size());
    baos.writeTo(out);
  }

  DockingWindow acceptDrop(Point p, DockingWindow window) {
    return (getWindowParent() instanceof TabWindow) && getWindowParent().getChildWindowCount() == 1 ?
        getWindowParent().acceptDrop(p, window) :
        super.acceptDrop(p, window);
  }

  public String toString() {
    return getTitle();
  }

  protected void rootChanged(final RootWindow oldRoot, final RootWindow newRoot) {
    super.rootChanged(oldRoot, newRoot);
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
}
