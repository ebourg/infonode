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


// $Id: UIManagerUtil.java,v 1.6 2005/12/04 13:46:04 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.border.BorderUtil;
import net.infonode.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public class UIManagerUtil {
  private UIManagerUtil() {
  }

  public static Insets getInsets(String key) {
    return InsetsUtil.copy(UIManager.getInsets(key));
  }

  public static Insets getInsets(String key, Insets insets) {
    Insets i = getInsets(key);
    return i == null ? insets : i;
  }

  public static Insets getInsets(String key, String defaultKey) {
    Insets i = getInsets(key);

    if (i != null)
      return i;

    i = getInsets(defaultKey);
    return i == null ? new Insets(0, 0, 0, 0) : i;
  }

  public static Color getColor(String key) {
    return ColorUtil.copy(UIManager.getColor(key));
  }

  public static Color getColor(String key, String defaultKey) {
    return getColor(key, defaultKey, Color.BLACK);
  }

  public static Color getColor(String key, String defaultKey, Color defaultColor) {
    Color i = getColor(key);

    if (i != null)
      return i;

    i = getColor(defaultKey);
    return i == null ? defaultColor : i;
  }

  public static Border getBorder(String key) {
    return BorderUtil.copy(UIManager.getBorder(key));
  }

  public static Border getBorder(String key, String defaultKey) {
    Border i = getBorder(key);

    if (i != null)
      return i;

    i = getBorder(defaultKey);
    return i == null ? BorderFactory.createEmptyBorder() : i;
  }

  public static Font getFont(String key) {
    Font font = UIManager.getFont(key);
    if (font == null)
      font = new JLabel().getFont();
    return FontUtil.copy(font);
  }

  public static Font getFont(String key, String defaultKey) {
    Font i = getFont(key);

    if (i != null)
      return i;

    i = getFont(defaultKey);
    return i == null ? new Font("Dialog", 0, 11) : i;
  }

  public static Color getColor(String key, Color defaultColor) {
    Color c = getColor(key);
    return c == null ? defaultColor : c;
  }
}
