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


// $Id: OpenContentBorder.java,v 1.17 2004/09/28 14:55:27 jesper Exp $
package net.infonode.tabbedpanel.border;

import net.infonode.gui.colorprovider.ColorProvider;
import net.infonode.gui.colorprovider.ColorProviderUtil;
import net.infonode.gui.colorprovider.FixedColorProvider;
import net.infonode.gui.colorprovider.UIManagerColorProvider;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TabbedUtils;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.Serializable;

/**
 * OpenContentBorder is a border that draws a 1 pixel wide line border around a
 * component that is used as content area component in a tabbed panel. The border
 * also draws a highlight inside the line on the top and left sides of the
 * component. It is open, i.e. no content border will be drawn where the highlighted
 * tab in the tabbed panel is located.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.17 $
 * @see TabbedPanel
 */
public class OpenContentBorder implements Border, Serializable {
  private static final long serialVersionUID = 1;

  private ColorProvider color;
  private ColorProvider highlightColor;
  private int tabLeftInset = 1;

  /**
   * Constructor.
   *
   * @param color        the line color
   * @param tabLeftInset the left border inset of the tab
   */
  public OpenContentBorder(Color color, int tabLeftInset) {
    this(color);
    this.tabLeftInset = tabLeftInset;
  }

  /**
   * Constructor. Uses the TabbedPane.darkShadow color from the UIManager as line color.
   */
  public OpenContentBorder() {
    this(null);
  }

  /**
   * Constructs a OpenContentBorder without highlight and with the given color as line
   * color.
   *
   * @param color the line color
   */
  public OpenContentBorder(Color color) {
    this(color, null);
  }

  /**
   * Constructs a OpenContentBorder with highlight and with the given colors as line
   * color and highlight color.
   *
   * @param color          the line color
   * @param highlightColor the highlight color
   */
  public OpenContentBorder(Color color, Color highlightColor) {
    this(ColorProviderUtil.getColorProvider(color, UIManagerColorProvider.TABBED_PANE_DARK_SHADOW),
         highlightColor == null ? null : new FixedColorProvider(highlightColor),
         1);
  }

  /**
   * Constructs a OpenContentBorder with highlight and with the given colors as line
   * color and highlight color.
   *
   * @param colorProvider          the line color provider
   * @param highlightColorProvider the highlight color provider
   * @param tabLeftInset           the left border inset of the tab
   */
  public OpenContentBorder(ColorProvider colorProvider, ColorProvider highlightColorProvider, int tabLeftInset) {
    this.color = colorProvider;
    this.highlightColor = highlightColorProvider;
    this.tabLeftInset = tabLeftInset;
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
      Direction orientation = Direction.UP;

      if (tab != null) {
        Rectangle visible = tab.getVisibleRect();
        Point p = SwingUtilities.convertPoint(tab, visible.x, visible.y, c);

        tabX = Math.max(0, (int) p.getX());
        tabY = Math.max(0, (int) p.getY());

        tabWidth = (int) visible.getWidth();
        tabHeight = (int) visible.getHeight();

        clipX = tab.getWidth() > tabWidth ? 0 : 1;
        clipY = tab.getHeight() > tabHeight ? 0 : 1;

        orientation = tabbedPanel.getProperties().getTabAreaOrientation();
      }

      Color lineColor = color.getColor(c);

      if (orientation == Direction.UP && tab != null) {
        g.setColor(lineColor);
        drawLine(g, x, y, tabX - 1 + tabLeftInset, y);
        drawLine(g, tabX + tabWidth - clipX, y, x + width - 1, y);

        if (highlightColor != null) {
          g.setColor(highlightColor.getColor(c));
          drawLine(g, x + 1, y + 1, tabX + tabLeftInset, y + 1);

          if (tabWidth > 0)
            drawLine(g, tabX + tabLeftInset, y, tabX + tabLeftInset, y);

          drawLine(g, tabX + tabWidth, y + 1, x + width - 3, y + 1);
        }
      }
      else {
        g.setColor(lineColor);
        drawLine(g, x, y, x + width - 1, y);

        if (highlightColor != null) {
          g.setColor(highlightColor.getColor(c));
          drawLine(g, x + 1, y + 1, x + width - (orientation == Direction.RIGHT && tabY == 0 ? 1 : 3), y + 1);
        }
      }

      if (orientation == Direction.LEFT && tab != null) {
        g.setColor(lineColor);
        drawLine(g, x, y + 1, x, tabY - 1 + tabLeftInset);
        drawLine(g, x, tabY + tabHeight - clipY, x, y + height - 1);

        if (highlightColor != null) {
          g.setColor(highlightColor.getColor(c));
          drawLine(g, x + 1, y + 2, x + 1, tabY + tabLeftInset);

          if (tabHeight > 0)
            drawLine(g, x, tabY + tabLeftInset, x, tabY + tabLeftInset);

          drawLine(g, x + 1, tabY + tabHeight, x + 1, y + height - 3);
        }
      }
      else {
        g.setColor(lineColor);
        drawLine(g, x, y + 1, x, y + height - 1);

        if (highlightColor != null) {
          g.setColor(highlightColor.getColor(c));
          drawLine(g, x + 1, y + 2, x + 1, y + height - (orientation == Direction.DOWN && tabX == 0 ? 1 : 3));
        }
      }

      g.setColor(lineColor);

      if (orientation == Direction.RIGHT && tab != null) {
        drawLine(g, x + width - 1, y + 1, x + width - 1, tabY - 1 + tabLeftInset);
        drawLine(g, x + width - 1, tabY + tabHeight - clipY, x + width - 1, y + height - 1);
      }
      else {
        drawLine(g, x + width - 1, y + 1, x + width - 1, y + height - 1);
      }

      if (orientation == Direction.DOWN && tab != null) {
        drawLine(g, x + 1, y + height - 1, tabX - 1 + tabLeftInset, y + height - 1);
        drawLine(g, tabX + tabWidth - clipX, y + height - 1, x + width - 2, y + height - 1);
      }
      else {
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
