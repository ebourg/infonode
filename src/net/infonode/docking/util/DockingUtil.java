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


// $Id: DockingUtil.java,v 1.7 2004/08/11 09:15:17 jesper Exp $
package net.infonode.docking.util;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.TabWindow;

/**
 * Class that contains utility methods for docking windows.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.7 $
 */
public final class DockingUtil {
  private DockingUtil() {
  }

  /**
   * Creates a root window with support for view serialization and popup menues.
   *
   * @param views                 the views that can be shown inside the root window
   * @param createWindowPopupMenu true if a standard window popup menu should be created
   *
   * @return the created root window
   */
  public static RootWindow createRootWindow(ViewMap views, boolean createWindowPopupMenu) {
    TabWindow tabWindow = new TabWindow();

    for (int i = 0; i < views.getViewCount(); i++)
      tabWindow.addTab(views.getViewAtIndex(i));

    tabWindow.setSelectedTab(0);
    RootWindow rootWindow = new RootWindow(views, tabWindow);

    if (createWindowPopupMenu)
      rootWindow.setPopupMenuFactory(WindowMenuUtil.createWindowMenuFactory(views, true));

    return rootWindow;
  }

  /**
   * Returns true if <tt>ancestor</tt> is an ancestor of <tt>child</tt> or the windows are the same.
   *
   * @param ancestor the ancestor window
   * @param child    the child window
   *
   * @return true if <tt>ancestor</tt> is an ancestor of <tt>child</tt> or the windows are the same
   */
  public static boolean isAncestor(DockingWindow ancestor, DockingWindow child) {
    return child != null && (ancestor == child || isAncestor(ancestor, child.getWindowParent()));
  }

}
