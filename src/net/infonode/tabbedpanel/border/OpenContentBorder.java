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


// $Id: OpenContentBorder.java,v 1.9 2004/06/30 12:59:07 jesper Exp $
package net.infonode.tabbedpanel.border;

import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TabbedUtils;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * OpenContentBorder is a border that draws a 1 pixel wide line border around a
 * component that is used as content area component in a tabbed panel. The border
 * also draws a highlight inside the line on the top and left sides of the
 * component. It is open, i.e. no content border will be drawn where the highlighted
 * tab in the tabbed panel is located.
 *
 * @see TabbedPanel
 * @author $Author: jesper $
 * @version $Revision: 1.9 $
 */
public class OpenContentBorder implements Border {
  private Color color;
  private Color highlightColor;

  /**
   * Constructs a OpenContentBorder without highlight and with the given color as line
   * color.
   *
   * @param color       the line color
   */
  public OpenContentBorder(Color color) {
    this(color, null);
  }

  /**
   * Constructs a OpenContentBorder with highlight and with the given colors as line
   * color and highlight color.
   *
   * @param color           the line color
   * @param highlightColor  the highlight color
   */
  public OpenContentBorder(Color color, Color highlightColor) {
    this.color = color;
    this.highlightColor = highlightColor;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    TabbedPanel tabbedPanel = TabbedUtils.getParentTabbedPanelContentPanel(c).getTabbedPanel();

    if (c != null && tabbedPanel != null) {
      Tab tab = tabbedPanel.getHighlightedTab();
      int tabX = -1;
      int tabY = -1;
      int tabWidth = 0;
      int tabHeight = 0;
      int clipX = 0;
      int clipY = 0;
      Direction d = Direction.UP;

      if (tab != null) {
        Rectangle visible = tab.getVisibleRect();
        Point p = SwingUtilities.convertPoint(tab, visible.x, visible.y, c);

        tabX = Math.max(0, (int)p.getX());
        tabY = Math.max(0, (int)p.getY());

        tabWidth = (int)visible.getWidth();
        tabHeight = (int)visible.getHeight();

        clipX = tab.getWidth() > tabWidth ? 0 : 1;
        clipY = tab.getHeight() > tabHeight ? 0 : 1;

        d = tabbedPanel.getProperties().getTabAreaOrientation();
      }

      if (d == Direction.UP && tab != null) {
        g.setColor(color);
        drawLine(g, x, y, tabX, y);
        drawLine(g, tabX + tabWidth - clipX, y, x + width - 1, y);

        if (highlightColor != null) {
          g.setColor(highlightColor);
          drawLine(g, x + 1, y + 1, tabX + 1, y + 1);

          if (tabWidth > 0)
            drawLine(g, tabX + 1, y, tabX + 1, y);

          drawLine(g, tabX + tabWidth, y + 1, x + width - 3, y + 1);
        }
      } else {
        g.setColor(color);
        drawLine(g, x, y, x + width - 1, y);

        if (highlightColor != null) {
          g.setColor(highlightColor);
          drawLine(g, x + 1, y + 1, x + width - (d == Direction.RIGHT && tabY == 0 ? 1 : 3), y + 1);
        }
      }

      if (d == Direction.LEFT && tab != null) {
        g.setColor(color);
        drawLine(g, x, y + 1, x, tabY);
        drawLine(g, x, tabY + tabHeight - clipY, x, y + height - 1);

        if (highlightColor != null) {
          g.setColor(highlightColor);
          drawLine(g, x + 1, y + 2, x + 1, tabY + 1);

          if (tabHeight > 0)
            drawLine(g, x, tabY + 1, x, tabY + 1);

          drawLine(g, x + 1, tabY + tabHeight, x + 1, y + height - 3);
        }
      } else {
        g.setColor(color);
        drawLine(g, x, y + 1, x, y + height - 1);

        if (highlightColor != null) {
          g.setColor(highlightColor);
          drawLine(g, x + 1, y + 2, x + 1, y + height - (d == Direction.DOWN && tabX == 0 ? 1 : 3));
        }
      }

      g.setColor(color);

      if (d == Direction.RIGHT && tab != null) {
        drawLine(g, x + width - 1, y + 1, x + width - 1, tabY);
        drawLine(g, x + width - 1, tabY + tabHeight - clipY, x + width - 1, y + height - 1);
      } else {
        drawLine(g, x + width - 1, y + 1, x + width - 1, y + height - 1);
      }

      if (d == Direction.DOWN && tab != null) {
        drawLine(g, x + 1, y + height - 1, tabX, y + height - 1);
        drawLine(g, tabX + tabWidth - clipX, y + height - 1, x + width - 2, y + height - 1);
      } else {
        drawLine(g, x + 1, y + height - 1, x + width - 2, y + height - 1);
      }
    }
  }

  private static void drawLine(Graphics graphics, int x1, int y1, int x2, int y2) {
    if (x2 < x1 || y2 < y1)
      return;

    graphics.drawLine(x1, y1, x2, y2);
  }

  public Insets getBorderInsets(Component c) {
    return highlightColor != null ? new Insets(2, 2, 1, 1) : new Insets(1, 1, 1, 1);
  }

  public boolean isBorderOpaque() {
    return false;
  }

}
