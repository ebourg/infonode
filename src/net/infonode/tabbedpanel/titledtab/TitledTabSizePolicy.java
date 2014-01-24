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


// $Id: TitledTabSizePolicy.java,v 1.6 2004/09/28 15:07:29 jesper Exp $
package net.infonode.tabbedpanel.titledtab;

/**
 * TitledTabSizePolicy defines how TitledTab should calculate its size.
 * If the different tab states results in different tab sizes, then TitledTab
 * can calculate the maximum size for the states and use that size for all
 * the states.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public enum TitledTabSizePolicy {

  /**
   * Equal size policy. This menas that if the different tab states results in
   * different tab sizes, then titled tab will calculate the maximum size for the
   * states and use that size for all the states.
   */
  EQUAL_SIZE,

  /**
   * Individual size policy. This means that if the different tab states have
   * different sizes then titled tab will have different size for the states.
   */
  INDIVIDUAL_SIZE
}
