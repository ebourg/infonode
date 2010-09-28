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


// $Id: EtchedLineBorder.java,v 1.6 2004/11/11 09:51:46 jesper Exp $
package net.infonode.gui.border;

import net.infonode.gui.ComponentUtil;
import net.infonode.util.ColorUtil;

import javax.swing.border.Border;
import java.awt.*;
import java.io.Serializable;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public class EtchedLineBorder implements Border, Serializable {
  private static final long serialVersionUID = 1;

  private boolean drawTop;
  private boolean drawBottom;
  private boolean drawLeft;
  private boolean drawRight;
  private Insets insets;
  private Color highlightColor;
  private Color shadowColor;

  public EtchedLineBorder() {
    this(true, true, true, true);
  }

  public EtchedLineBorder(boolean drawTop, boolean drawLeft, boolean drawBottom, boolean drawRight) {
    this(drawTop, drawLeft, drawBottom, drawRight, null, null);
  }

  public EtchedLineBorder(boolean drawTop, boolean drawLeft, boolean drawBottom, boolean drawRight,
                          Color highlightColor, Color shadowColor) {
    this.drawBottom = drawBottom;
    this.drawLeft = drawLeft;
    this.drawRight = drawRight;
    this.drawTop = drawTop;
    insets = new Insets(drawTop ? 2 : 0, drawLeft ? 2 : 0, drawBottom ? 2 : 0, drawRight ? 2 : 0);
    this.highlightColor = highlightColor;
    this.shadowColor = shadowColor;
  }

  public Insets getBorderInsets(Component c) {
    return insets;
  }

  public boolean isBorderOpaque() {
    return false;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Color c1 = highlightColor == null ? ColorUtil.mult(ComponentUtil.getBackgroundColor(c), 1.70) : highlightColor;
    Color c2 = shadowColor == null ? ColorUtil.mult(ComponentUtil.getBackgroundColor(c), 0.5) : shadowColor;
    g.setColor(c1);

    if (drawTop)
      g.drawLine(x, y + 1, x + width - 1, y + 1);

    if (drawLeft)
      g.drawLine(x + 1, y, x + 1, y + height - 1);

    g.setColor(c2);

    if (drawBottom)
      g.drawLine(x, y + height - 2, x + width - 1, y + height - 2);

    if (drawRight)
      g.drawLine(x + width - 2, y, x + width - 2, y + height - 1);

    g.setColor(c1);

    if (drawBottom)
      g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

    if (drawRight)
      g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

    g.setColor(c2);

    if (drawTop)
      g.drawLine(x, y, x + width - 1, y);

    if (drawLeft)
      g.drawLine(x, y, x, y + height - 1);

    g.setColor(ComponentUtil.getBackgroundColor(c));

    if (drawTop && drawRight)
      g.drawLine(x + width - 2, y + 1, x + width - 1, y);

    if (drawBottom && drawLeft)
      g.drawLine(x, y + height - 1, x + 1, y + height - 2);
  }
}
