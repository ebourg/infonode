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


// $Id: Utils.java,v 1.4 2005/01/26 12:50:31 jesper Exp $
package net.infonode.util;

public class Utils {
  private Utils() {
  }

  public static final short unsigned(byte b) {
    return (short) (b & 0xff);
  }

  public static final boolean equals(Object o1, Object o2) {
    return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
  }

}
