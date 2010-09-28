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


// $Id: SimplePanel.java,v 1.3 2004/09/22 14:35:04 jesper Exp $
package net.infonode.gui.panel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class SimplePanel extends JPanel {
  private Component comp;

  public SimplePanel() {
    this(new BorderLayout());
  }

  public SimplePanel(Border border, Component comp) {
    this(comp);
    setBorder(border);
  }

  public SimplePanel(LayoutManager layoutManager) {
    setLayout(layoutManager);
    setOpaque(false);
  }

  public SimplePanel(Component c) {
    this();
    setComponent(c);
  }

  public SimplePanel(Component c, Component northComponent) {
    this(c);
    add(northComponent, BorderLayout.NORTH);
  }

  public SimplePanel(Border border, Component c, Component northComponent) {
    this(border, c);
    add(northComponent, BorderLayout.NORTH);
  }

  public void setComponent(Component c) {
    if (comp != null)
      remove(comp);

    if (c != null) {
      add(c, BorderLayout.CENTER);
      c.repaint();
    }

    comp = c;
  }

  public void setSouthComponent(Component c) {
    add(c, BorderLayout.SOUTH);
  }

}
