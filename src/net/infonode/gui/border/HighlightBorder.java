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


// $Id: HighlightBorder.java,v 1.2 2004/06/17 13:01:11 johan Exp $
package net.infonode.gui.border;

import net.infonode.util.ColorUtil;

import javax.swing.border.Border;
import java.awt.*;

public class HighlightBorder implements Border {
  private static final Insets INSETS = new Insets(1, 1, 0, 0);
  private boolean lowered;
  private Color color;

  public HighlightBorder() {
    this(false);
  }

  public HighlightBorder(boolean lowered) {
    this(lowered, null);
  }

  public HighlightBorder(boolean lowered, Color color) {
    this.lowered = lowered;
    this.color = color;
  }

  public Insets getBorderInsets(Component c) {
    return INSETS;
  }

  public boolean isBorderOpaque() {
    return false;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    g.setColor(color == null ? ColorUtil.mult(c.getBackground(), lowered ? 0.7 : 1.70) : color);
    g.drawLine(x, y, x + width - (lowered ? 1 : 2), y);
    g.drawLine(x, y, x, y + height - (lowered ? 1 : 2));
  }
}
