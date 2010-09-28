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


// $Id: TabbedUIDefaults.java,v 1.11 2004/09/22 14:33:49 jesper Exp $
package net.infonode.tabbedpanel;

import net.infonode.gui.FontUtil;
import net.infonode.gui.InsetsUtil;
import net.infonode.util.ColorUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Methods for retrieving UI defaults for the current "Look and Feel" from the
 * UIManager. The values are adapted to be used with classes in the TabbedPanel package.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.11 $
 */
public class TabbedUIDefaults {
  private static final int BUTTON_ICON_SIZE = 11;

  private TabbedUIDefaults() {
  }

  /**
   * Gets the content area background color
   *
   * @return a copy of the color
   */
  public static Color getContentAreaBackground() {
    return ColorUtil.copy(UIManager.getColor("Panel.background"));
  }

  /**
   * Gets the tab normal state foreground color
   *
   * @return a copy of the color
   */
  public static Color getNormalStateForeground() {
    return ColorUtil.copy(UIManager.getColor("TabbedPane.foreground"));
  }

  /**
   * Gets the tab normal state background color
   *
   * @return a copy of the color
   */
  public static Color getNormalStateBackground() {
    return ColorUtil.copy(UIManager.getColor("TabbedPane.background"));
  }

  /**
   * Gets the tab highlighted state foreground color
   *
   * @return a copy of the color
   */
  public static Color getHighlightedStateForeground() {
    return getNormalStateForeground();
  }

  /**
   * Gets the tab highlighted state backgound color
   *
   * @return a copy of the color
   */
  public static Color getHighlightedStateBackground() {
    return ColorUtil.copy(UIManager.getColor("Panel.background"));
  }

  /**
   * Gets the tab disabled state foreground color
   *
   * @return a copy of the color
   */
  public static Color getDisabledForeground() {
    return ColorUtil.copy(UIManager.getColor("inactiveCaptionText"));
  }

  /**
   * Gets the tab disabled state background color
   *
   * @return a copy of the color
   */
  public static Color getDisabledBackground() {
    return ColorUtil.mult(UIManager.getColor("TabbedPane.background"), 0.9);
  }

  /**
   * Gets the (border) dark shadow color
   *
   * @return a copy of the color
   */
  public static Color getDarkShadow() {
    return ColorUtil.copy(UIManager.getColor("TabbedPane.darkShadow"));
  }

  /**
   * Gets the (border) highlight color
   *
   * @return a copy of the color
   */
  public static Color getHighlight() {
    return ColorUtil.copy(UIManager.getColor("TabbedPane.highlight"));
  }

  /**
   * Gets the font
   *
   * @return a copy of the font
   */
  public static Font getFont() {
    return FontUtil.copy(UIManager.getFont("TabbedPane.font"));
  }

  /**
   * Gets the icon text gap
   *
   * @return the icon text gap
   */
  public static int getIconTextGap() {
    return UIManager.getInt("TabbedPane.textIconGap");
  }

  /**
   * Gets the tab insets
   *
   * @return a copy of the insets
   */
  public static Insets getTabInsets() {
    return InsetsUtil.copy(UIManager.getInsets("TabbedPane.tabInsets"));
  }

  /**
   * Gets the insets for the content area
   *
   * @return a copy of the insets
   */
  public static Insets getContentAreaInsets() {
    return InsetsUtil.copy(UIManager.getInsets("TabbedPane.contentBorderInsets"));
  }

  /**
   * Gets the default icon size for buttons
   *
   * @return icon size in pixels
   */
  public static int getButtonIconSize() {
    return BUTTON_ICON_SIZE;
  }
}
