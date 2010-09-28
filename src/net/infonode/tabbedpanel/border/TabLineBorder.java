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


// $Id: TabLineBorder.java,v 1.9 2004/06/23 12:25:39 johan Exp $
package net.infonode.tabbedpanel.border;

import net.infonode.gui.ComponentUtils;
import net.infonode.tabbedpanel.Tab;
import net.infonode.tabbedpanel.TabbedPanel;
import net.infonode.tabbedpanel.TabbedUtils;
import net.infonode.util.Direction;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

/**
 * TabLineBorder draws a 1 pixel wide line around a {@link Tab}. If tab spacing in the
 * tabbed panel is 0 then the border will only draw a single line between two adjacent tabs.
 *
 * @see Tab
 * @see TabbedPanel
 * @author $Author: johan $
 * @version $Revision: 1.9 $
 */
public class TabLineBorder implements Border {
  private Color color;
  private Border border;
  private boolean last;
  private boolean afterHighlighted;
  private boolean highlighted;
  private boolean drawTopLine;
  private boolean drawBottomLine;
  private int index;
  private boolean tabSpacing = false;

  private class LineBorder implements Border {

    public LineBorder() {
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      Tab tab = TabbedUtils.getParentTab(c);
      if (tab != null && tab.getTabbedPanel() != null) {
        Direction d = tab.getTabbedPanel().getProperties().getTabAreaOrientation();
        tabSpacing = tab.getTabbedPanel().getProperties().getTabSpacing() > 0;
        initialize(tab);
        if (d == Direction.UP)
          paintUpBorder(g, x, y, width, height);
        else if (d == Direction.LEFT)
          paintLeftBorder(g, x, y, width, height);
        else if (d == Direction.DOWN)
          paintDownBorder(g, x, y, width, height);
        else // RIGHT
          paintRightBorder(g, x, y, width, height);
      }
    }

    public Insets getBorderInsets(Component c) {
      Tab tab = TabbedUtils.getParentTab(c);
      if (tab != null && tab.getTabbedPanel() != null && tab.getParent() != null) {
        int top, left, bottom, right;
        Direction d = tab.getTabbedPanel().getProperties().getTabAreaOrientation();
        initialize(tab);
        if (d == Direction.UP) {
          top = drawTopLine ? 1 : 0;
          left = 1;
          bottom = 0;
          right = 1;
        } else if (d == Direction.LEFT) {
          top = 1;
          left = drawTopLine ? 1 : 0;
          bottom = 1;
          right = 0;
        } else if (d == Direction.DOWN) {
          top = 0;
          left = 1;
          bottom = drawTopLine ? 1 : 0;
          right = 1;
        } else {
          top = 1;
          left = 0;
          bottom = 1;
          right = drawTopLine ? 1 : 0;
        }

        return new Insets(top,
                          left,
                          bottom,
                          right);
      }

      return new Insets(0, 0, 0, 0);
    }

    public boolean isBorderOpaque() {
      return false;
    }

    private void paintUpBorder(Graphics g, int x, int y, int width, int height) {
      g.setColor(color);

      // Left Line
      if (!afterHighlighted || tabSpacing)
        g.drawLine(x, y, x, y + height - 1);

      // Right line
      if (highlighted || last || tabSpacing)
        g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

      // Top Line
      if (drawTopLine)
        g.drawLine(x, y, x + width - 1, y);

      if (drawBottomLine)
        g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
    }

    private void paintLeftBorder(Graphics g, int x, int y, int width, int height) {
      g.setColor(color);

      // Top Line
      if (!afterHighlighted || tabSpacing)
        g.drawLine(x, y, x + width - 1, y);

      // Left Line
      if (drawTopLine)
        g.drawLine(x, y, x, y + height - 1);

      // Bottom Line
      if (highlighted || last || tabSpacing)
        g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

      if (drawBottomLine)
        g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
    }

    private void paintDownBorder(Graphics g, int x, int y, int width, int height) {
      g.setColor(color);

      // Left Line
      if (!afterHighlighted || tabSpacing)
        g.drawLine(x, y, x, y + height - 1);

      // Right line
      if (highlighted || last || tabSpacing)
        g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

      // Bottom Line
      if (drawTopLine)
        g.drawLine(x, y + height - 1, x + width, y + height - 1);

      if (drawBottomLine)
        g.drawLine(x, y, x + width, y);
    }

    private void paintRightBorder(Graphics g, int x, int y, int width, int height) {
      g.setColor(color);

      // Top Line
      if (!afterHighlighted || tabSpacing)
        g.drawLine(x, y, x + width - 1, y);

      // Right Line
      if (drawTopLine)
        g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

      // Bottom Line
      if (highlighted || last || tabSpacing)
        g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);

      if (drawBottomLine)
        g.drawLine(x, y, x, y + height - 1);
    }
  }

  /**
   * Constructs a TabLineBorder that draws lines on three sides of the tab.
   * No line will be drawn on the side towards the TabbedPanel's content area.
   *
   * @param color the line color
   */
  public TabLineBorder(Color color) {
    this(color, false);
  }

  /**
   * Constructs a TabLineBorder that draws lines on three or four sides of the tab.
   *
   * @param color           the line color
   * @param drawBottomLine  true if a line should be drawn on the side towards the
   *                        tabbed panel's content area, otherwise false
   */
  public TabLineBorder(Color color, boolean drawBottomLine) {
    this(color, drawBottomLine, true);
  }

  /**
   * Constructs a TabLineBorder that draws lines on two, three or four sides of the
   * tab.
   *
   * @param color           the line color
   * @param drawBottomLine  true if a line should be drawn on the side towards the
   *                        tabbed panel's content area, otherwise false
   * @param drawTopLine     true if a line should be drawn on the side opposite to
   *                        the tabbed panel's content area, otherwise false
   */
  public TabLineBorder(Color color, boolean drawBottomLine, boolean drawTopLine) {
    this.color = color;
    border = new LineBorder();
    this.drawBottomLine = drawBottomLine;
    this.drawTopLine = drawTopLine;
  }

  /**
   * Constructs a TabLineBorder that draws lines on three sides of the tab.
   * No line will be drawn on the side towards the tabbed panel's content area.
   * The inner border will be drawn inside of this TabLineBorder.
   *
   * @param color       the line color
   * @param innerBorder border to draw inside of this TabLineBorder
   */
  public TabLineBorder(Color color, Border innerBorder) {
    this(color, innerBorder, false);
  }

  /**
   * Constructs a TabLineBorder that draws lines on three or four sides of the tab.
   * The inner border will be drawn inside of this TabLineBorder.
   *
   * @param color           the line color
   * @param innerBorder     border to draw inside of this TabLineBorder
   * @param drawBottomLine  true if a line should be drawn on the side towards the
   *                        tabbed panel's content area, otherwise false
   */
  public TabLineBorder(Color color, Border innerBorder, boolean drawBottomLine) {
    this(color, drawBottomLine);
    if (innerBorder != null)
      border = new CompoundBorder(border, innerBorder);
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    border.paintBorder(c, g, x, y, width, height);
  }

  public Insets getBorderInsets(Component c) {
    return border.getBorderInsets(c);
  }

  public boolean isBorderOpaque() {
    return false;
  }

  private void initialize(Tab tab) {
    index = ComponentUtils.getComponentIndex(tab);
    last = ComponentUtils.getComponentIndex(tab) == tab.getParent().getComponentCount() - 1;
    afterHighlighted = index > 0 && ((Tab)tab.getParent().getComponent(index - 1)) == tab.getTabbedPanel().getHighlightedTab();
    highlighted = tab == tab.getTabbedPanel().getHighlightedTab();
  }
}
