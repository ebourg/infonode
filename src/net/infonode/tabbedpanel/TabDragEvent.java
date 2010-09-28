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


// $Id: TabDragEvent.java,v 1.8 2004/06/24 13:49:50 johan Exp $
package net.infonode.tabbedpanel;

import java.awt.*;

/**
 * TabDragEvent is an event that contains information about the tab that is
 * beeing dragged from a tabbed panel and a point specifying the mouse
 * coordinates.
 *
 * @see TabbedPanel
 * @see Tab
 * @author $Author: johan $
 * @version $Revision: 1.8 $
 */
public class TabDragEvent extends TabEvent {
  private Point point;

  /**
   * Constructs a TabDragEvent
   *
   * @param source  the Tab or TabbedPanel that is the source for this
   *                event
   * @param tab     the Tab that is beeing dragged
   * @param point   the mouse coordinates relative to the Tab that is beeing
   *                dragged
   */
  public TabDragEvent(Object source, Tab tab, Point point) {
    super(source, tab);
    this.point = point;
  }

  /**
   * Gets the mouse coordinates
   *
   * @return  the mouse coordinats relative to the Tab that is beeing
   *          dragged
   */
  public Point getPoint() {
    return point;
  }
}
