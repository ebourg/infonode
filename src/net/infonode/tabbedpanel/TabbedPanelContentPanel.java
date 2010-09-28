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


// $Id: TabbedPanelContentPanel.java,v 1.24 2004/11/11 14:10:33 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.componentpainter.ComponentPainter;
import net.infonode.gui.componentpainter.SolidColorComponentPainter;
import net.infonode.gui.draggable.DraggableComponentBoxAdapter;
import net.infonode.gui.draggable.DraggableComponentBoxEvent;
import net.infonode.gui.shaped.panel.ShapedPanel;
import net.infonode.properties.base.Property;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.properties.propertymap.PropertyMapWeakListenerManager;
import net.infonode.properties.util.PropertyChangeListener;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * A TabbedPanelContentPanel is a component that holds a container for tab content
 * components. It can be configured using properties that specifies the look for
 * the content panel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.24 $
 * @see TabbedPanel
 * @see Tab
 */
public class TabbedPanelContentPanel extends JPanel {
  private TabbedPanel tabbedPanel;
  private ShapedPanel shapedPanel = new ShapedPanel();
  private PropertyMapTreeListener propertiesListener = new PropertyMapTreeListener() {
    public void propertyValuesChanged(Map changes) {
      update();
      repaint();
    }
  };

  private PropertyChangeListener tabbedPanelPropertyListener = new PropertyChangeListener() {
    public void propertyChanged(Property property, Object valueContainer, Object oldValue, Object newValue) {
      shapedPanel.setDirection(((Direction) newValue).getNextCW());
      repaint();
    }
  };

  /**
   * Constructs a TabbedPanelContentPanel
   *
   * @param tabbedPanel the TabbedPanel that this content panel should be the content
   *                    area component for
   * @param component   a component used as container for the tabs' content components
   */
  public TabbedPanelContentPanel(TabbedPanel tabbedPanel, JComponent component) {
    super(new BorderLayout());
    setOpaque(false);
    shapedPanel.add(component, BorderLayout.CENTER);
    add(shapedPanel, BorderLayout.CENTER);
    this.tabbedPanel = tabbedPanel;
    update();

    PropertyMapWeakListenerManager.addWeakTreeListener(getProperties().getMap(), propertiesListener);
    PropertyMapWeakListenerManager.addWeakPropertyChangeListener(tabbedPanel.getProperties().getMap(),
                                                                 TabbedPanelProperties.TAB_AREA_ORIENTATION,
                                                                 tabbedPanelPropertyListener);

    tabbedPanel.getDraggableComponentBox().addListener(new DraggableComponentBoxAdapter() {
      public void changed(DraggableComponentBoxEvent event) {
        repaintBorder();
      }
    });

    tabbedPanel.addTabListener(new TabAdapter() {
      public void tabAdded(TabEvent event) {
        repaintBorder();
      }

      public void tabRemoved(TabRemovedEvent event) {
        repaintBorder();
      }

      public void tabSelected(TabStateChangedEvent event) {
        repaintBorder();
      }

      public void tabDeselected(TabStateChangedEvent event) {
        repaintBorder();
      }

      public void tabHighlighted(TabStateChangedEvent event) {
        repaintBorder();
      }

      public void tabDehighlighted(TabStateChangedEvent event) {
        repaintBorder();
      }

      public void tabMoved(TabEvent event) {
        repaintBorder();
      }
    });
  }

  /**
   * Gets the tabbed panel that this component is the content area component for
   *
   * @return the tabbed panel
   */
  public TabbedPanel getTabbedPanel() {
    return tabbedPanel;
  }

  /**
   * Gets the properties for this component
   *
   * @return the properties for this TabbedPanelContentPanel
   */
  public TabbedPanelContentPanelProperties getProperties() {
    return tabbedPanel.getProperties().getContentPanelProperties();
  }

  private void update() {
    getProperties().getComponentProperties().applyTo(shapedPanel);
    shapedPanel.setOpaque(false);

    ShapedPanelProperties shapedPanelProperties = getProperties().getShapedPanelProperties();
    ComponentPainter painter = shapedPanelProperties.getComponentPainter();
    if (painter != null)
      shapedPanel.setComponentPainter(painter);
    else if (getProperties().getComponentProperties().getBackgroundColor() != null)
      shapedPanel.setComponentPainter(SolidColorComponentPainter.BACKGROUND_COLOR_PAINTER);
    else
      shapedPanel.setComponentPainter(null);
    shapedPanel.setVerticalFlip(shapedPanelProperties.getVerticalFlip());
    shapedPanel.setHorizontalFlip(shapedPanelProperties.getHorizontalFlip());
    shapedPanel.setDirection(tabbedPanel.getProperties().getTabAreaOrientation().getNextCW());
    shapedPanel.setClipChildren(shapedPanelProperties.getClipChildren());
  }

  private void repaintBorder() {
    Rectangle r;

    Direction d = tabbedPanel.getProperties().getTabAreaOrientation();

    if (d == Direction.UP)
      r = new Rectangle(0, 0, shapedPanel.getWidth(), shapedPanel.getInsets().top);
    else if (d == Direction.LEFT)
      r = new Rectangle(0, 0, shapedPanel.getInsets().left, shapedPanel.getHeight());
    else if (d == Direction.DOWN)
      r = new Rectangle(0,
                        shapedPanel.getHeight() - shapedPanel.getInsets().bottom - 1,
                        shapedPanel.getWidth(),
                        shapedPanel.getHeight());
    else
      r = new Rectangle(shapedPanel.getWidth() - shapedPanel.getInsets().right - 1,
                        0,
                        shapedPanel.getWidth(),
                        shapedPanel.getHeight());

    shapedPanel.repaint(r);
  }
}
