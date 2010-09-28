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


// $Id: ColorUtil.java,v 1.5 2004/09/22 14:35:05 jesper Exp $
package net.infonode.util;

import java.awt.*;

public class ColorUtil {
  public static Color getOpposite(Color c) {
    return isDark(c) ? Color.WHITE : Color.BLACK;
  }

  public static Color shade(Color c, double amount) {
    return blend(c, getOpposite(c), amount);
  }

  public static final Color mult(Color c, double amount) {
    return new Color(Math.min(255, (int) (c.getRed() * amount)),
                     Math.min(255, (int) (c.getGreen() * amount)),
                     Math.min(255, (int) (c.getBlue() * amount)));
  }

  public static Color setAlpha(Color c, int alpha) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
  }

  public static final Color add(Color c1, Color c2) {
    return new Color(Math.min(255, c1.getRed() + c2.getRed()),
                     Math.min(255, c1.getGreen() + c2.getGreen()),
                     Math.min(255, c1.getBlue() + c2.getBlue()));
  }

  public static Color blend(Color c1, Color c2, double v) {
    return add(mult(c1, 1.0 - v), mult(c2, v));
  }

  public static boolean isDark(Color c) {
    return c.getRed() + c.getGreen() + c.getBlue() < 3 * 180;
  }

  public static Color highlight(Color c) {
    return mult(c, isDark(c) ? 1.5F : 0.67F);
  }

  public static Color copy(Color c) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
  }

}
