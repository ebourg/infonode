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


// $Id: ScrollButtonBox.java,v 1.10 2004/09/22 14:35:05 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.icon.button.ArrowIcon;
import net.infonode.gui.icon.button.BorderIcon;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.util.ColorUtil;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

public class ScrollButtonBox extends JPanel {

  private JButton button1;
  private JButton button2;
  private boolean vertical;
  private int iconSize;

  private ArrayList listeners;

  private ActionListener button1Listener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      fireButton1();
    }
  };

  private ActionListener button2Listener = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      fireButton2();
    }
  };

  public ScrollButtonBox(final boolean vertical, int iconSize) {
    this.vertical = vertical;
    this.iconSize = iconSize;

    addMouseWheelListener(new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0)
          fireButton1();
        else
          fireButton2();
      }
    });

    initialize();
  }

  public void setButton1Enabled(boolean enabled) {
    button1.setEnabled(enabled);
  }

  public void setButton2Enabled(boolean enabled) {
    button2.setEnabled(enabled);
  }

  public boolean isButton1Enabled() {
    return button1.isEnabled();
  }

  public boolean isButton2Enabled() {
    return button2.isEnabled();
  }

  public void addListener(ScrollButtonBoxListener listener) {
    if (listeners == null)
      listeners = new ArrayList(2);

    listeners.add(listener);
  }

  public void removeListener(ScrollButtonBoxListener listener) {
    if (listeners != null) {
      listeners.remove(listener);

      if (listeners.size() == 0)
        listeners = null;
    }
  }

  public boolean isVertical() {
    return vertical;
  }

  public void setVertical(boolean vertical) {
    if (vertical != this.vertical) {
      this.vertical = vertical;
      update();
      ((DirectionLayout) getLayout()).setDirection(vertical ? Direction.DOWN : Direction.RIGHT);
    }
  }

  public void updateUI() {
    super.updateUI();

    if (button1 != null) {
      update();
    }
  }

  private void fireButton1() {
    if (listeners != null) {
      Object[] l = listeners.toArray();

      for (int i = 0; i < l.length; i++)
        ((ScrollButtonBoxListener) l[i]).scrollButton1();
    }
  }

  private void fireButton2() {
    if (listeners != null) {
      Object[] l = listeners.toArray();

      for (int i = 0; i < l.length; i++)
        ((ScrollButtonBoxListener) l[i]).scrollButton2();
    }
  }

  private void initialize() {
    setLayout(new DirectionLayout(vertical ? Direction.DOWN : Direction.RIGHT));
    button1 = ButtonFactory.createFlatHighlightButton(null,
                                                      null,
                                                      0,
                                                      button1Listener);

    button2 = ButtonFactory.createFlatHighlightButton(null,
                                                      null,
                                                      0,
                                                      button2Listener);

    button1.setFocusable(false);
    button2.setFocusable(false);

    setOpaque(false);

    add(button1);
    add(button2);

    update();
  }

  private void update() {
    Color c1 = UIManager.getColor("Button.foreground");
    Color c2 = UIManager.getColor("Button.disabledForeground");

    if (c2 == null)
      c2 = ColorUtil.blend(c1, UIManager.getColor("Panel.background"), 0.7f);

    button1.setIcon(new ArrowIcon(c1, iconSize, vertical ? Direction.UP : Direction.LEFT));
    button2.setIcon(new ArrowIcon(c1, iconSize, vertical ? Direction.DOWN : Direction.RIGHT));

    ArrowIcon icon = new ArrowIcon(c2, iconSize - 2, vertical ? Direction.UP : Direction.LEFT);
    icon.setShadowEnabled(false);
    button1.setDisabledIcon(new BorderIcon(icon, 1));

    icon = new ArrowIcon(c2, iconSize - 2, vertical ? Direction.DOWN : Direction.RIGHT);
    icon.setShadowEnabled(false);
    button2.setDisabledIcon(new BorderIcon(icon, 1));
  }
}
