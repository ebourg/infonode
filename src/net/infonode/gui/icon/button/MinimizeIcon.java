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


//$Id: MinimizeIcon.java,v 1.10 2005/12/04 13:46:03 jesper Exp $
package net.infonode.gui.icon.button;

import net.infonode.gui.GraphicsUtil;

import java.awt.*;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.10 $
 */
public class MinimizeIcon extends AbstractButtonIcon {
  private static final long serialVersionUID = 6993801965272908275L;

  public MinimizeIcon() {
    super();
  }

  public MinimizeIcon(Color color) {
    super(color);
  }

  public MinimizeIcon(Color color, int size) {
    super(color, size);
  }

  public MinimizeIcon(int size) {
    super(size);
  }

  protected void paintIcon(Component c, Graphics g, int x1, int y1, int x2, int y2) {
    GraphicsUtil.drawOptimizedLine(g, x1, y2 - 1, x2, y2 - 1);
    GraphicsUtil.drawOptimizedLine(g, x1, y2, x2, y2);
  }
}
