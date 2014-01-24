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


// $Id: Alignment.java,v 1.5 2004/09/28 15:07:29 jesper Exp $
package net.infonode.util;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * An enum class for alignments, left, center, right, top, bottom.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.5 $
 */
public enum Alignment {

  /** Left alignment. */
  LEFT, 

  /** Center alignment. */
  CENTER,

  /** Right alignment. */
  RIGHT,

  /** Top alignment. */
  TOP,

  /** Bottom alignment. */
  BOTTOM;

  /**
   * Array containing all horizontal alignments..
   */
  public static final Alignment[] HORIZONTAL_ALIGNMENTS = {LEFT, CENTER, RIGHT};

  /**
   * Array containing all vertical alignments..
   */
  public static final Alignment[] VERTICAL_ALIGNMENTS = {TOP, CENTER, BOTTOM};

  /**
   * Gets the horizontal alignments.
   *
   * @return the horizontal alignments
   * @since 1.1.0
   */
  public static Alignment[] getHorizontalAlignments() {
    return HORIZONTAL_ALIGNMENTS.clone();
  }

  /**
   * Gets the vertical alignments.
   *
   * @return the vertical alignments
   * @since 1.1.0
   */
  public static Alignment[] getVerticalAlignments() {
    return VERTICAL_ALIGNMENTS.clone();
  }
}
