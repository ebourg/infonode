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


// $Id: PopupList.java,v 1.8 2004/11/11 14:11:14 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.panel.SimplePanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PopupList extends SimplePanel {
  private static class PopupButtonModel extends DefaultButtonModel {
    private boolean pressed;

    public boolean isPressed() {
      return super.isPressed() || pressed;
    }

    public boolean isArmed() {
      return super.isArmed() || pressed;
    }

    public void setPressedInternal(boolean pressed) {
      this.pressed = pressed;
      fireStateChanged();
    }
  }

  private class Popup extends JPopupMenu {
    private JList list = new JList();
    private JScrollPane scrollPane = new JScrollPane(list,
                                                     JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                     JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private int oldIndex;

    Popup() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      scrollPane.setBorder(null);
      setBorderPainted(true);
      setBorder(new LineBorder(UIManager.getColor("controlDkShadow"), 1));

      add(scrollPane);
      scrollPane.getViewport().setOpaque(false);
      list.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (!e.getValueIsAdjusting())
            setVisible(false);
        }
      });

      update();
    }

    public MouseMotionListener getMouseMotionListener() {
      return new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e)) {
            Component c = (Component) e.getSource();
            Point p = SwingUtilities.convertPoint(c, e.getPoint(), scrollPane);
            int index = list.locationToIndex(SwingUtilities.convertPoint(scrollPane, p, list));
            if (!c.contains(e.getPoint()) && (scrollPane.contains(p) || (p.getY() > scrollPane.getY() + scrollPane.getHeight()) || p.getY() < scrollPane.getY())) {
              list.setSelectedIndex(index);
              list.ensureIndexIsVisible(index);
            }
          }
        }
      };
    }

    public MouseListener getMouseListener() {
      return new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e)) {
            if (isVisible()) {
              setVisible(false);
              return;
            }
            update();
            scrollPane.setViewportView(null);
            list.setValueIsAdjusting(true);
            fireWillBecomeVisible();
            list.setVisibleRowCount(Math.min(list.getModel().getSize(), 8));
            oldIndex = list.getSelectedIndex();
            list.ensureIndexIsVisible(oldIndex);
            scrollPane.setViewportView(list);
            Component c = (Component) e.getSource();
            show(c, 0, c.getHeight());
          }
        }

        public void mouseReleased(MouseEvent e) {
          if (SwingUtilities.isLeftMouseButton(e)) {
            if (!isVisible())
              return;

            Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), scrollPane);
            if (scrollPane.contains(p)) {
              list.setValueIsAdjusting(false);
            }
            else if (!((Component) e.getSource()).contains(e.getPoint())) {
              list.setSelectedIndex(oldIndex);
              list.setValueIsAdjusting(false);
            }
          }
        }
      };
    }

    public JList getList() {
      return list;
    }

    public void updateUI() {
      super.updateUI();
      setBorder(new LineBorder(UIManager.getColor("controlDkShadow"), 1));
      if (list != null)
        update();
    }

    private void update() {
      list.setFont(UIManager.getFont("ComboBox.font"));
      list.setForeground(UIManager.getColor("ComboBox.foreground"));
      list.setBackground(UIManager.getColor("ComboBox.background"));
      list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
      list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
      list.setBorder(null);
      scrollPane.getViewport().setOpaque(false);
      scrollPane.setBorder(null);
    }
  }

  private Popup popup = new Popup();
  private ArrayList listeners = new ArrayList(1);
  private PopupButtonModel buttonModel = new PopupButtonModel();

  public PopupList(AbstractButton component) {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(component);
    component.setModel(buttonModel);
    component.setAutoscrolls(true);
    component.setFocusable(false);
    component.addMouseListener(popup.getMouseListener());
    component.addMouseMotionListener(popup.getMouseMotionListener());

    popup.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuCanceled(PopupMenuEvent e) {
      }

      public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        buttonModel.setPressedInternal(false);
      }

      public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        buttonModel.setPressedInternal(true);
      }
    });
  }

  public JList getList() {
    return popup.getList();
  }

  public void updateUI() {
    super.updateUI();
    if (popup != null)
      SwingUtilities.updateComponentTreeUI(popup);
  }

  public void addPopupListListener(PopupListListener l) {
    listeners.add(l);
  }

  public void removePopupListListener(PopupListListener l) {
    listeners.remove(l);
  }

  public void addListSelectionListener(ListSelectionListener l) {
    getList().addListSelectionListener(l);
  }

  public void removeListSelectionListener(ListSelectionListener l) {
    getList().removeListSelectionListener(l);
  }

  private void fireWillBecomeVisible() {
    Object[] l = listeners.toArray();
    for (int i = 0; i < l.length; i++)
      ((PopupListListener) l[i]).willBecomeVisible(this);
  }
}