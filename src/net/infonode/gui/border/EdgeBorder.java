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


// $Id: EdgeBorder.java,v 1.6 2004/09/08 14:53:06 jesper Exp $
package net.infonode.gui.border;

import net.infonode.gui.ComponentUtil;
import net.infonode.util.ColorUtil;

import javax.swing.border.Border;
import java.awt.*;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public class EdgeBorder implements Border {
  private Color color;
  private boolean drawTop;
  private boolean drawBottom;
  private boolean drawLeft;
  private boolean drawRight;
  private Insets insets;

  public EdgeBorder(boolean drawTop, boolean drawBottom, boolean drawLeft, boolean drawRight) {
    this(null, drawTop, drawBottom, drawLeft, drawRight);
  }

  public EdgeBorder(Color color, boolean drawTop, boolean drawBottom, boolean drawLeft, boolean drawRight) {
    this.color = color;
    this.drawTop = drawTop;
    this.drawBottom = drawBottom;
    this.drawLeft = drawLeft;
    this.drawRight = drawRight;
    insets = new Insets(drawTop ? 1 : 0, drawLeft ? 1 : 0, drawBottom ? 1 : 0, drawRight ? 1 : 0);
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    if (color == null) {
      Color background = ComponentUtil.getBackgroundColor(c);

      if (background == null)
        return;

      g.setColor(ColorUtil.mult(background, 0.7f));
    }
    else
      g.setColor(color);

    if (drawTop)
      g.drawLine(x, y, x + width - 1, y);

    if (drawLeft)
      g.drawLine(x, y, x, y + height - 1);

    if (drawRight)
      g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

    if (drawBottom)
      g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
  }

  public Insets getBorderInsets(Component c) {
    return insets;
  }

  public boolean isBorderOpaque() {
    return false;
  }
}
