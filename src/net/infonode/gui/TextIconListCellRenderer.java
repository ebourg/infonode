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


// $Id: TextIconListCellRenderer.java,v 1.7 2004/09/28 11:39:33 johan Exp $
package net.infonode.gui;

import net.infonode.gui.icon.IconProvider;

import javax.swing.*;
import java.awt.*;

/**
 * @author johan
 */
public class TextIconListCellRenderer extends DefaultListCellRenderer {
  private ListCellRenderer renderer;
  private Icon emptyIcon;
  private int width;
  private boolean iconFound;

  public TextIconListCellRenderer(ListCellRenderer renderer) {
    this.renderer = renderer;
  }

  public void calculateMaximumIconWidth(Object[] list) {
    width = -1;
    Icon icon;
    iconFound = false;
    for (int i = 0; i < list.length; i++) {
      Object o = list[i];
      if (o instanceof IconProvider) {
        icon = ((IconProvider) o).getIcon();
        if (icon != null) {
          iconFound = true;
          if (icon.getIconWidth() > width)
            width = icon.getIconWidth();
        }
      }
    }
    if (iconFound)
      emptyIcon = new Icon() {
        public int getIconHeight() {
          return 1;
        }

        public int getIconWidth() {
          return width;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
        }
      };
  }

  public void setRenderer(ListCellRenderer renderer) {
    this.renderer = renderer;
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    if (index == -1)
      return null;

    Icon icon = null;
    JLabel label = (JLabel) renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

    if (iconFound) {
      if (value instanceof IconProvider)
        icon = ((IconProvider) value).getIcon();
      if (icon == null) {
        label.setIcon(emptyIcon);
      }
      else {
        label.setIcon(icon);
        if (icon.getIconWidth() < width)
          label.setIconTextGap(label.getIconTextGap() + width - icon.getIconWidth());
      }
    }

    return label;
  }
}