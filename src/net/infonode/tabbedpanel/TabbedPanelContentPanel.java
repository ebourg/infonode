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


// $Id: TabbedPanelContentPanel.java,v 1.11 2004/06/17 11:29:51 johan Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.draggable.DraggableComponentBoxAdapter;
import net.infonode.gui.draggable.DraggableComponentBoxEvent;
import net.infonode.properties.propertymap.PropertyMapTreeListener;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * A TabbedPanelContentPanel is a component that holds a container for tab content
 * components. It can be configured using properties that specifies the look for
 * the content panel.
 *
 * @see TabbedPanel
 * @see Tab
 * @author $Author: johan $
 * @version $Revision: 1.11 $
 */
public class TabbedPanelContentPanel extends JPanel {
  private TabbedPanel tabbedPanel;

  /**
   * Constructs a TabbedPanelContentPanel
   *
   * @param tabbedPanel the TabbedPanel that this content panel should be the content
   *                    area component for
   * @param component   a component used as container for the tabs' content components
   */
  public TabbedPanelContentPanel(TabbedPanel tabbedPanel, JComponent component) {
    super(new BorderLayout());
    add(component, BorderLayout.CENTER);
    this.tabbedPanel = tabbedPanel;
    setOpaque(true);
    update();

    getProperties().getMap().addTreeListener(new PropertyMapTreeListener() {
      public void propertyValuesChanged(Map changes) {
        update();
      }
    });

    tabbedPanel.getDraggableComponentBox().addListener(new DraggableComponentBoxAdapter() {
      public void changed(DraggableComponentBoxEvent event) {
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
    getProperties().getComponentProperties().applyTo(this);
  }

  private void repaintBorder() {
    Rectangle r;

    Direction d = tabbedPanel.getProperties().getTabAreaOrientation();

    if (d == Direction.UP)
      r = new Rectangle(0, 0, getWidth(), getInsets().top);
    else if (d == Direction.LEFT)
      r = new Rectangle(0, 0, getInsets().left, getHeight());
    else if (d == Direction.DOWN)
      r = new Rectangle(0, getHeight() - getInsets().bottom - 1, getWidth(), getHeight());
    else
      r = new Rectangle(getWidth() - getInsets().right - 1, 0, getWidth(), getHeight());

    repaint(r);
  }
}
