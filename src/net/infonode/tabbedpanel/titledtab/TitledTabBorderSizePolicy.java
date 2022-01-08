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


// $Id: TitledTabBorderSizePolicy.java,v 1.8 2005/02/16 11:28:15 jesper Exp $
package net.infonode.tabbedpanel.titledtab;

/**
 * TitledTabBorderSizePolicy defines how the insets for the titled tab
 * should be calculated based on the borders for the different tab states.
 * If the states have borders with different insets titled tab can use the same
 * insets for each state based on the maximum insets (top, left, bottom,
 * right) for each state. The compensated insets will be added on the inside
 * of the borders.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.8 $
 * @see TitledTab
 * @see TitledTabProperties
 */
public enum TitledTabBorderSizePolicy {

  /**
   * Equal size policy. This means that if the different tab states have
   * borders with different insets titled tab will use the same insets for
   * each state based on the maximum insets (top, left, bottom, right)
   * for each state. The compensated insets will be added on the inside of
   * the borders.
   */
  EQUAL_SIZE,

  /**
   * Individual size policy. This means that titled tab will use the borders
   * for each state as they are and not modify any insets. If the borders for
   * the different states have different insets, then the titled tab's insets
   * will be different depending on the state the tab is currently in.
   */
  INDIVIDUAL_SIZE
}
