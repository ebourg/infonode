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


// $Id: TabbedPanelHoverPolicy.java,v 1.6 2005/12/04 13:46:05 jesper Exp $
package net.infonode.tabbedpanel;

/**
 * TabbedPanelHoverPolicy defines the hover policy, i.e. when a tabbed panel should consider itself
 * hovered by the mouse and the HoverListener should be called. This policy affects the tabbed panel,
 * the tab area, the tab area components area and the content area (if the tabbed panel has a content
 * area).
 *
 * @author johan
 * @version $Revision: 1.6 $
 * @see net.infonode.gui.hover.HoverListener
 * @since ITP 1.3.0
 */
public enum TabbedPanelHoverPolicy {

  /**
   * Never hover policy. This means that the tabbed panel will nerver be considered hovered
   */
  NEVER,

  /**
   * Always hover policy. This means that the tabbed panel will always consider itself hovered
   * when the mouse is over the tabbed panel.
   */
  ALWAYS,

  /**
   * No hovered child hover policy. This means that the tabbed panel will consider itself hovered when
   * the mouse is over the tabbed panel and the content area doesn't contain any hovered tabbed panel.
   */
  NO_HOVERED_CHILD,

  /**
   * Only when deepest hover policy. This means that the tabbed panel will consider itself hovered when
   * the mouse is over the tabbed panel and there is no other tabbed panel in the tabbed panel's content area.
   */
  ONLY_WHEN_DEEPEST,

  /**
   * Always and exclude hover policy. This means that the tabbed panel will always consider itself hovered
   * when the mouse is over the tabbed panel but it will be excluded by other tabbed panels when their hover policies
   * are evaluated.
   *
   * @since ITP 1.4.0
   */
  ALWAYS_AND_EXCLUDE
}
