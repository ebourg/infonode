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


// $Id: Direction.java,v 1.6 2004/09/28 15:07:29 jesper Exp $
package net.infonode.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * An enum class for directions, up, down, left, right.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public enum Direction implements Writable {

  /** Up direction. */
  UP(false),

  /** Right direction. */
  RIGHT(true),

  /** Down direction. */
  DOWN(false),

  /** Left direction. */
  LEFT(true);

  static {
    UP.rotateCW = RIGHT;
    RIGHT.rotateCW = DOWN;
    DOWN.rotateCW = LEFT;
    LEFT.rotateCW = UP;
  }

  private transient Direction rotateCW;
  private transient boolean isHorizontal;

  private Direction(boolean isHorizontal) {
    this.isHorizontal = isHorizontal;
  }

  /**
   * Returns the direction that is one quarter of a revolution clock wise.
   *
   * @return the direction that is one quarter of a revolution clock wise
   */
  public Direction getNextCW() {
    return rotateCW;
  }

  /**
   * Returns the direction that is one quarter of a revolution counter clock wise.
   *
   * @return the direction that is one quarter of a revolution counter clock wise
   */
  public Direction getNextCCW() {
    return rotateCW.rotateCW.rotateCW;
  }

  /**
   * Returns true if the direction is horizontal.
   *
   * @return true if the direction is horizontal
   */
  public boolean isHorizontal() {
    return isHorizontal;
  }

  /**
   * Returns the opposite direction.
   *
   * @return the opposite direction
   */
  public Direction getOpposite() {
    return getNextCW().getNextCW();
  }

  /**
   * Decodes a direction from a stream.
   *
   * @param in the stream containing the direction
   * @return the direction
   * @throws IOException if there is a stream error
   */
  public static Direction decode(ObjectInputStream in) throws IOException {
    int ordinal = in.readShort();
    return values()[ordinal];
  }

  public void write(ObjectOutputStream out) throws IOException {
    out.writeShort(ordinal());
  }
}
