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


// $Id: FocusBorder.java,v 1.7 2005/02/16 11:28:10 jesper Exp $
package net.infonode.gui.border;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import net.infonode.gui.UIManagerUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.7 $
 */
public class FocusBorder implements Border, Serializable {
  private static final long serialVersionUID = 1;

  private static final Insets INSETS = new Insets(1, 1, 1, 1);

  public FocusBorder() {
  }

  public FocusBorder(final Component component) {
    component.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        component.repaint();
      }

      public void focusLost(FocusEvent e) {
        component.repaint();
      }
    });
  }

  public Insets getBorderInsets(Component c) {
    return INSETS;
  }

  public boolean isBorderOpaque() {
    return false;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    if (c.hasFocus()) {
      g.setColor(UIManagerUtil.getColor("Button.focus", "TabbedPane.focus"));

      if (UIManager.getLookAndFeel().getClass() == WindowsLookAndFeel.class)
        BasicGraphicsUtils.drawDashedRect(g, x, y, width, height);
      else
        g.drawRect(x, y, width - 1, height - 1);
    }
  }
}
