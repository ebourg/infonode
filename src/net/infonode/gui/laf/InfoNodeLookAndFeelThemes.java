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


// $Id: InfoNodeLookAndFeelThemes.java,v 1.3 2004/09/28 15:07:29 jesper Exp $
package net.infonode.gui.laf;

import net.infonode.gui.Colors;

import java.awt.*;

/**
 * Contains some predefined InfoNode look and feel themes.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
public class InfoNodeLookAndFeelThemes {
  private InfoNodeLookAndFeelThemes() {
  }

  /**
   * A theme with dark blue controls and green selection.
   *
   * @return the theme
   */
  public static InfoNodeLookAndFeelTheme getDarkBlueGreenTheme() {
    float hue = Colors.BLUE_HUE;
    float saturation = 0.20f;

    return new InfoNodeLookAndFeelTheme("Dark Blue Green Theme",
                                        Color.getHSBColor(hue, saturation, 0.5f),
                                        Color.getHSBColor(hue, saturation + 0.1f, 0.9f),
                                        Color.getHSBColor(Colors.SAND_HUE, 0.04f, 0.5f),
                                        Color.WHITE,
                                        new Color(0, 170, 0),
                                        Color.WHITE,
                                        0.8);
  }

  /**
   * A theme with light gray controls and blue selection.
   *
   * @return the theme
   */
  public static InfoNodeLookAndFeelTheme getGrayTheme() {
    float hue = Colors.BLUE_HUE;
    float saturation = 0.4f;

    return new InfoNodeLookAndFeelTheme("Gray Theme",
                                        new Color(220, 220, 220),
                                        Color.getHSBColor(hue, saturation + 0.3f, 1f),
                                        InfoNodeLookAndFeelTheme.DEFAULT_BACKGROUND_COLOR,
                                        InfoNodeLookAndFeelTheme.DEFAULT_TEXT_COLOR,
                                        Color.getHSBColor(hue, saturation, 1.0f),
                                        Color.BLACK);
  }

}
