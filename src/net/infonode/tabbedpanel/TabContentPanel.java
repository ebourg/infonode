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


// $Id: TabContentPanel.java,v 1.11 2004/06/24 12:50:50 johan Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.layout.StackableLayout;

import javax.swing.*;

/**
 * A TabContentPanel is a container for tabs' content components. It listens to
 * a tabbed panel and manages the tabs' content components by showing and hiding
 * the components based upon the selection of tabs in the tabbed panel.
 *
 * @see TabbedPanel
 * @see Tab
 * @author $Author: johan $
 * @version $Revision: 1.11 $
 */
public class TabContentPanel extends JPanel {
  private TabbedPanel tabbedPanel;

  /**
   * Constructs a TabContentPanel
   *
   * @param tabbedPanel the TabbedPanel for whom this component is the tabs' content
   *                    component container
   */
  public TabContentPanel(TabbedPanel tabbedPanel) {
    final StackableLayout layout = new StackableLayout(this);
    setLayout(layout);
    setOpaque(false);

    this.tabbedPanel = tabbedPanel;
    layout.setAutoShowFirstComponent(false);

    tabbedPanel.addTabListener(new TabAdapter() {
      public void tabSelected(TabStateChangedEvent event) {
        layout.showComponent(event.getTab() == null ? null : event.getTab().getContentComponent());
      }

      public void tabRemoved(TabRemovedEvent event) {
        if (event.getTab().getContentComponent() != null)
          remove(event.getTab().getContentComponent());
      }

      public void tabAdded(TabEvent event) {
        if (event.getTab().getContentComponent() != null)
          add(event.getTab().getContentComponent());
      }
    });
  }

  /**
   * Gets the TabbedPanel for whom this component is the tabs' content component
   * container
   *
   * @return the TabbedPanel
   */
  public TabbedPanel getTabbedPanel() {
    return tabbedPanel;
  }
}
