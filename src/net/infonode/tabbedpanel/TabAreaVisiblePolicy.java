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


// $Id: TabAreaVisiblePolicy.java,v 1.3 2005/12/04 13:46:05 jesper Exp $
package net.infonode.tabbedpanel;

/**
 * TabAreaVisiblePolicy defines the visibility policies for the tab area of a tabbed panel.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 * @see TabbedPanel
 * @see TabbedPanelProperties
 * @since ITP 1.4.0
 */
public enum TabAreaVisiblePolicy {

  /** Always visible policy. This means that the tab area is always visible. */
  ALWAYS,

  /** Never visible policy. This means that the tab area is never visible. */
  NEVER,

  /** Tabs exist visible policy. This means that the tab area will only be visible if it contains tabs. */
  TABS_EXIST,

  /**
   * More than one visible policy. This means that the tab area is visible when the tabbed
   * panel contains more than one tab.
   */
  MORE_THAN_ONE_TAB
}
