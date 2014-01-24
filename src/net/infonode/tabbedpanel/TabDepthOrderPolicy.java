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


// $Id: TabDepthOrderPolicy.java,v 1.4 2004/11/11 14:10:33 jesper Exp $
package net.infonode.tabbedpanel;

/**
 * TabDepthOrderPolicy defines the depth order for the tabs in a tabbed panel's
 * tab area when tab spacing is negative i.e. the tabs are overlapping.
 *
 * @author johan
 * @version $Revision: 1.4 $
 * @see TabbedPanel
 * @see TabbedPanelProperties
 * @since ITP 1.2.0
 */
public enum TabDepthOrderPolicy {

  /**
   * Descending depth order policy. This means that the first tab will be the
   * top most and the last tab will be the bottom most. Note that if a tab is
   * highlighted, it will always be on top of the other tabs.
   */
  DESCENDING,

  /**
   * Ascending depth order policy. This means that the first tab will be the
   * bottom most and the last tab will be the top most. Note that if a tab is
   * highlighted, it will always be on top of the other tabs.
   */
  ASCENDING
}
