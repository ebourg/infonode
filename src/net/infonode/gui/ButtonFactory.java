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


// $Id: ButtonFactory.java,v 1.4 2004/07/06 15:08:44 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.border.HighlightBorder;
import net.infonode.util.ColorUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class ButtonFactory {
  private static class ButtonHighlighter extends MouseAdapter implements ComponentListener, HierarchyListener {
    private static final Color HIGHLIGHTED_COLOR = new Color(140, 160, 255);
    private static final Color PRESSED_COLOR = new Color(60, 80, 200);

    private JButton button;
    private Border pressedBorder;
    private Border highlightedBorder;
    private Border normalBorder;
    private boolean pressed;

    public ButtonHighlighter(JButton button, int padding) {
      this.button = button;

      normalBorder = new EmptyBorder(padding + 2, padding + 2, padding + 2, padding + 2);
      pressedBorder = new CompoundBorder(new LineBorder(ColorUtil.mult(PRESSED_COLOR, 0.5f)),
                                         new EmptyBorder(padding + 2, padding + 2, padding, padding));
      highlightedBorder = new CompoundBorder(new LineBorder(ColorUtil.mult(HIGHLIGHTED_COLOR, 0.5f)),
                                             new EmptyBorder(padding + 1, padding + 1, padding + 1, padding + 1));

      button.setContentAreaFilled(false);
      setNormalState();

      button.addHierarchyListener(this);
      button.addMouseListener(this);
      button.addComponentListener(this);
    }

    private void setNormalState() {
      button.setBackground(null);
      button.setOpaque(false);
      button.setBorder(normalBorder);
    }

    public void componentHidden(ComponentEvent e) {
      setNormalState();
    }

    public void componentMoved(ComponentEvent e) {
      setNormalState();
    }

    public void componentResized(ComponentEvent e) {
      setNormalState();
    }

    public void componentShown(ComponentEvent e) {
      setNormalState();
    }

    public void hierarchyChanged(HierarchyEvent e) {
      setNormalState();
    }

    private void update(Point point) {
      if (button.isEnabled() && button.contains(point) && button.getBackground() != null) {
        button.setOpaque(true);
        Color backgroundColor = ComponentUtils.getBackgroundColor(button.getParent());
        button.setBackground(ColorUtil.blend(pressed ? PRESSED_COLOR : HIGHLIGHTED_COLOR,
                                             backgroundColor == null ? UIManager.getColor("control") : backgroundColor,
                                             0.5f));
        button.setBorder(pressed ? pressedBorder : highlightedBorder);
      }
      else {
        setNormalState();
      }
    }

    public void mouseEntered(MouseEvent e) {
      if (pressed || (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0)
        update(e.getPoint());
    }

    public void mouseExited(MouseEvent e) {
      update(e.getPoint());
    }

    public void mousePressed(MouseEvent e) {
      pressed = true;
      update(e.getPoint());
    }

    public void mouseReleased(MouseEvent e) {
      pressed = false;
      update(e.getPoint());
    }

  }

  private static final Border normalBorder = new CompoundBorder(new LineBorder(new Color(70, 70, 70)),
                                                                new CompoundBorder(new HighlightBorder(), new EmptyBorder(1, 6, 1, 6)));
  private static final Border pressedBorder = new CompoundBorder(new LineBorder(new Color(70, 70, 70)),
                                                                 new CompoundBorder(new HighlightBorder(true), new EmptyBorder(2, 7, 0, 5)));

  private static final JButton initButton(final JButton button) {
    button.setMargin(null);
    button.setBorder(normalBorder);
    button.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        button.setBorder(pressedBorder);
      }

      public void mouseReleased(MouseEvent e) {
        button.setBorder(normalBorder);
      }
    });

    return button;
  }

  private static final JButton newButton(String text) {
    return initButton(new JButton(text));
  }

  private static final JButton newButton(Icon icon) {
    return initButton(new JButton(icon));
  }

  private static final JButton newButton(Icon icon, String text) {
    return initButton(new JButton(text, icon));
  }

  public static final JButton createDialogButton(String text, ActionListener action) {
    JButton b = new JButton(text);
    b.setFont(b.getFont().deriveFont(Font.BOLD));
    b.addActionListener(action);
    return b;
  }

  public static final JButton createButton(String text, ActionListener action) {
    JButton b = newButton(text);
    b.addActionListener(action);
    return b;
  }

  public static final JButton createButton(String iconResource, String text, ActionListener action) {
    URL iconURL = ButtonFactory.class.getClassLoader().getResource(iconResource);
    return createButton(iconURL == null ? null : new ImageIcon(iconURL), text, action);
  }

  public static final JButton createButton(Icon icon, String text, ActionListener action) {
    JButton b;

    if (icon != null) {
      b = newButton(icon);
      b.setToolTipText(text);
    }
    else {
      b = newButton(text);
    }

    b.addActionListener(action);
    return b;
  }

  public static final JButton createButton(Icon icon, String tooltipText, boolean opaque, ActionListener action) {
    JButton b = newButton(icon);
    b.setToolTipText(tooltipText);
    b.addActionListener(action);
    b.setOpaque(opaque);
    return b;
  }

  public static final JButton createFlatHighlightButton(Icon icon, String tooltipText, final int padding, final ActionListener action) {
    final JButton b = new JButton(icon);
    b.setVerticalAlignment(SwingConstants.CENTER);
    b.setToolTipText(tooltipText);
    b.setMargin(new Insets(0, 0, 0, 0));
    new ButtonHighlighter(b, padding);
    b.addActionListener(action);
    return b;
  }

  public static final JButton createHighlightButton(String text, ActionListener action) {
    JButton b = newButton(text);
    b.addActionListener(action);
    return b;
  }

  public static final JButton createHighlightButton(Icon icon, ActionListener action) {
    JButton b = newButton(icon);
    b.addActionListener(action);
    return b;
  }

  public static final JButton createHighlightButton(Icon icon, String text, ActionListener action) {
    JButton b = newButton(icon, text);
    b.addActionListener(action);
    return b;
  }
}
