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


// $Id: ComponentUtils.java,v 1.3 2004/07/06 15:08:44 jesper Exp $
package net.infonode.gui;

import net.infonode.util.Direction;

import java.awt.*;

public class ComponentUtils {
  public static final int getComponentIndex(Component component) {
    if (component != null && component.getParent() != null) {
      Component[] c = component.getParent().getComponents();
      for (int i = 0; i < c.length; i++) {
        if (c[i] == component)
          return i;
      }
    }

    return -1;
  }

  public static final String getBorderLayoutOrientation(Direction d) {
    return d == Direction.UP ? BorderLayout.NORTH :
            d == Direction.LEFT ? BorderLayout.WEST :
            d == Direction.DOWN ? BorderLayout.SOUTH :
            BorderLayout.EAST;
  }

  public static Color getBackgroundColor(Component component) {
    return component == null ? null : component.isOpaque() ? component.getBackground() : getBackgroundColor(component.getParent());
  }

  public static int countComponents(Container c) {
    int num = 1;
    for (int i = 0; i < c.getComponentCount(); i++) {
      Component comp = c.getComponent(i);
      if (comp instanceof Container)
        num += countComponents((Container)comp);
      else
        num++;
    }

    return num;
  }
}
