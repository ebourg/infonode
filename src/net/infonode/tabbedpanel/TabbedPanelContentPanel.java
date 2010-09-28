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


// $Id: TabbedPanelContentPanel.java,v 1.37 2005/02/16 11:28:15 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.componentpainter.ComponentPainter;
import net.infonode.gui.componentpainter.SolidColorComponentPainter;
import net.infonode.gui.draggable.DraggableComponentBoxAdapter;
import net.infonode.gui.draggable.DraggableComponentBoxEvent;
import net.infonode.gui.hover.HoverListener;
import net.infonode.gui.hover.panel.HoverableShapedPanel;
import net.infonode.properties.gui.util.ShapedPanelProperties;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.properties.propertymap.PropertyMapWeakListenerManager;
import net.infonode.tabbedpanel.internal.TabbedHoverUtil;
import net.infonode.util.Direction;
import net.infonode.util.ValueChange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;

/**
 * A TabbedPanelContentPanel is a component that holds a container for tab content
 * components. It can be configured using properties that specifies the look for
 * the content panel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.37 $
 * @see TabbedPanel
 * @see Tab
 */
public class TabbedPanelContentPanel extends JPanel {
  private TabbedPanel tabbedPanel;
  private HoverableShapedPanel shapedPanel;

  private PropertyMapTreeListener propertiesListener = new PropertyMapTreeListener() {
    public void propertyValuesChanged(Map changes) {
      boolean update = false;

      Map m = (Map) changes.get(tabbedPanel.getProperties().getContentPanelProperties().getMap());
      if (m != null) {
        update = true;

        if (m.keySet().contains(TabbedPanelContentPanelProperties.HOVER_LISTENER)) {
          HoverListener oldHoverListener = shapedPanel.getHoverListener();
          shapedPanel.setHoverListener(
              (HoverListener) ((ValueChange) m.get(TabbedPanelContentPanelProperties.HOVER_LISTENER)).getNewValue());
        }

        repaint();
      }

      m = (Map) changes.get(tabbedPanel.getProperties().getContentPanelProperties().getComponentProperties().getMap());
      if (m != null)
        update = true;

      m =
      (Map) changes.get(tabbedPanel.getProperties().getContentPanelProperties().getShapedPanelProperties().getMap());
      if (m != null)
        update = true;

      m = (Map) changes.get(tabbedPanel.getProperties().getMap());
      if (m != null && m.keySet().contains(TabbedPanelProperties.TAB_AREA_ORIENTATION)) {
        shapedPanel.setDirection(
            ((Direction) ((ValueChange) m.get(TabbedPanelProperties.TAB_AREA_ORIENTATION)).getNewValue()).getNextCW());

        repaint();
      }

      if (update) {
        update();
        repaint();
      }
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
    this.tabbedPanel = tabbedPanel;

    shapedPanel = new HoverableShapedPanel(new BorderLayout(),
                                           tabbedPanel.getProperties().getContentPanelProperties().getHoverListener(),
                                           tabbedPanel) {
      public boolean acceptHover(ArrayList enterableHoverables) {
        return TabbedHoverUtil.acceptTabbedPanelHover(getTabbedPanel().getProperties().getHoverPolicy(),
                                                      enterableHoverables,
                                                      getTabbedPanel(),
                                                      this);
      }

      protected void processMouseEvent(MouseEvent event) {
        super.processMouseEvent(event);
        if (getTabbedPanel().hasContentArea())
          getTabbedPanel().doProcessMouseEvent(event);
        else
          doProcessMouseEvent(SwingUtilities.convertMouseEvent(this, event, TabbedPanelContentPanel.this));
      }

      protected void processMouseMotionEvent(MouseEvent event) {
        super.processMouseMotionEvent(event);

        if (getTabbedPanel().hasContentArea())
          getTabbedPanel().doProcessMouseMotionEvent(event);
        else
          doProcessMouseMotionEvent(SwingUtilities.convertMouseEvent(this, event, TabbedPanelContentPanel.this));
      }
    };

    shapedPanel.add(component, BorderLayout.CENTER);
    add(shapedPanel, BorderLayout.CENTER);
    update();

    PropertyMapWeakListenerManager.addWeakTreeListener(tabbedPanel.getProperties().getMap(), propertiesListener);

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

  private void doProcessMouseEvent(MouseEvent event) {
    processMouseEvent(event);
  }

  private void doProcessMouseMotionEvent(MouseEvent event) {
    processMouseMotionEvent(event);
  }
}
