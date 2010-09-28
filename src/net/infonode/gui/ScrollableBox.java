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


// $Id: ScrollableBox.java,v 1.6 2004/07/06 15:08:44 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.layout.DirectionLayout;
import net.infonode.gui.layout.LayoutUtil;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.gui.panel.StretchPanel;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class ScrollableBox extends SimplePanel {
  private SimplePanel viewport = new SimplePanel(new LayoutManager() {
    public void addLayoutComponent(String name, Component comp) {
    }

    public void layoutContainer(Container parent) {
      if (parent.getComponentCount() > 0) {
        Component panel = parent.getComponent(0);
//      panel.setSize(DimensionUtil.max(panel.getPreferredSize(), new Dimension(parent.getWidth() - panel.getLocation().x, parent.getHeight() - panel.getLocation().y)));
        panel.setBounds(0, 0, panel.getPreferredSize().width, panel.getPreferredSize().height);
        panel.validate();
        update();
      }
    }

    public Dimension minimumLayoutSize(Container parent) {
      Dimension min = parent.getComponentCount() == 0 ? new Dimension(0, 0) :
          parent.getComponent(0).getMinimumSize();
      return LayoutUtil.add(vertical ? new Dimension(min.width, 0) : new Dimension(0, min.height), parent.getInsets());
    }

    public Dimension preferredLayoutSize(Container parent) {
      return parent.getComponentCount() == 0 ? new Dimension(0, 0) : parent.getComponent(0).getPreferredSize();
    }

    public void removeLayoutComponent(Component comp) {
    }
  });

  private ScrollButtonBox scrollButtonBox;
  private SimplePanel buttonContainer = new SimplePanel();
  private int leftIndex;
  private boolean vertical;
  private int scrollOffset;
  private MouseWheelListener mouseWheelListener = new MouseWheelListener() {
    public void mouseWheelMoved(MouseWheelEvent e) {
      setLeftIndex(leftIndex + e.getWheelRotation());
    }
  };

  public ScrollableBox(boolean vertical, String scrollButtonPanelDirection, int scrollOffset) {
    this(new SimplePanel(new DirectionLayout(vertical ? Direction.DOWN : Direction.RIGHT)), vertical, scrollButtonPanelDirection, scrollOffset);
  }

  public ScrollableBox(JComponent scrollingContainer, boolean _vertical, String scrollButtonPanelDirection, int scrollOffset) {
    this.vertical = _vertical;
    this.scrollOffset = scrollOffset;
    scrollButtonBox = new ScrollButtonBox(vertical);
    scrollButtonBox.setVisible(false);

    buttonContainer = new SimplePanel() {
      public Dimension getPreferredSize() {
        return !scrollButtonBox.isVisible() ? new Dimension(0, 0) :
            vertical ? new Dimension(0, scrollButtonBox.getPreferredSize().height) :
            new Dimension(scrollButtonBox.getPreferredSize().width, 0);
      }
    };
    buttonContainer.add(scrollButtonBox, scrollButtonPanelDirection);
    add(new StretchPanel(buttonContainer, vertical, !vertical), vertical ? BorderLayout.SOUTH : BorderLayout.EAST);
    buttonContainer.setMinimumSize(new Dimension(0, 0));

    scrollButtonBox.addListener(new ScrollButtonBoxListener() {
      public void scrollButton1() {
        setLeftIndex(leftIndex - 1);
      }

      public void scrollButton2() {
        setLeftIndex(leftIndex + 1);
      }
    });

    SimplePanel viewPanel = new SimplePanel(new LayoutManager() {
      public void addLayoutComponent(String name, Component comp) {
      }

      public void layoutContainer(Container parent) {
        Dimension size = LayoutUtil.getInteriorSize(parent);
        viewport.setBounds(parent.getInsets().left, parent.getInsets().top, size.width, size.height);
      }

      public Dimension minimumLayoutSize(Container parent) {
        return LayoutUtil.add(viewport.getMinimumSize(), parent.getInsets());
      }

      public Dimension preferredLayoutSize(Container parent) {
        Dimension d = LayoutUtil.add(viewport.getPreferredSize(), parent.getInsets());
        return vertical ? new Dimension(d.width, 0) : new Dimension(0, d.height);
      }

      public void removeLayoutComponent(Component comp) {
      }
    });

    viewPanel.add(viewport);
    setComponent(viewport);
    setScrollingContainer(scrollingContainer);
  }

  public void setScrollingContainer(final JComponent component) {
    if (viewport.getComponentCount() > 0)
      viewport.remove(0);

    viewport.add(component);
    component.addMouseWheelListener(mouseWheelListener);
    component.addHierarchyListener(new HierarchyListener() {
      public void hierarchyChanged(HierarchyEvent e) {
        if (component.getParent() != viewport) {
          component.removeHierarchyListener(this);
          component.removeMouseWheelListener(mouseWheelListener);
        }
      }
    });
  }

  public JComponent getScrollingComponent() {
    return viewport.getComponentCount() == 0 ? null : (JComponent) viewport.getComponent(0);
  }

  public void ensureVisible(int index) {
    if (leftIndex > index)
      setLeftIndex(index);
    else if (leftIndex < index) {
      int newLeftIndex = findFitIndex(index);

      if (newLeftIndex > leftIndex) {
        setLeftIndex(newLeftIndex);
      }
    }
  }

  private int getInteriorSize() {
    return getDimensionSize(getSize()) - getInsetsSize(getInsets());
  }

  private int getDimensionSize(Dimension d) {
    return (int) (vertical ? d.getHeight() : d.getWidth());
  }

  private Point createPos(int p) {
    return vertical ? new Point(0, p) : new Point(p, 0);
  }

  private int getPos(Point p) {
    return vertical ? p.y : p.x;
  }

  private int getInsetsSize(Insets insets) {
    return vertical ? insets.top + insets.bottom : insets.left + insets.right;
  }

  private int getScrollOffset(int index) {
    return index == 0 ? 0 : Math.min(scrollOffset, getDimensionSize(getScrollingComponents()[index - 1].getPreferredSize()) / 2);
  }

  private Component[] getScrollingComponents() {
    JComponent c = getScrollingComponent();
    return c == null ? new Component[0] : c.getComponents();
  }

  private int getScrollingComponentCount() {
    JComponent c = getScrollingComponent();
    return c == null ? 0 : c.getComponentCount();
  }

  private int findFitIndex(int lastIndex) {
    int fitSize = getDimensionSize(viewport.getSize());

    if (fitSize == 0 || lastIndex < 0)
      return 0;

    Component[] c = getScrollingComponents();
    int endPos = getPos(c[lastIndex].getLocation()) + getDimensionSize(c[lastIndex].getSize());

    for (int i = lastIndex; i >= 0; i--) {
      if (endPos - getPos(c[i].getLocation()) + getScrollOffset(i) > fitSize)
        return Math.min(c.length - 1, i + 1);
    }

    return 0;
  }

  public void update() {
    setLeftIndex(leftIndex);
  }

  public void setLeftIndex(int index) {
    JComponent scrollingComponent = getScrollingComponent();

    if (scrollingComponent == null) {
      scrollButtonBox.setVisible(false);
    }
    else {
      int count = getScrollingComponentCount();
      int fitIndex = findFitIndex(count - 1);
      leftIndex = Math.min(fitIndex, Math.max(0, index));

      if (count > 1 && getDimensionSize(scrollingComponent.getPreferredSize()) > getInteriorSize()) {
        if (!scrollButtonBox.isVisible()) {
          scrollButtonBox.setVisible(true);
          validate();
        }
      }
      else if (scrollButtonBox.isVisible()) {
        scrollButtonBox.setVisible(false);
        validate();
      }

      scrollingComponent.setLocation(createPos((count == 0 ? 0 : -getPos(getScrollingComponents()[leftIndex].getLocation())) + getScrollOffset(leftIndex)));
      scrollButtonBox.setButton1Enabled(leftIndex > 0);
      scrollButtonBox.setButton2Enabled(leftIndex < fitIndex);
    }
  }

/*  private void updateScrollButtonSize() {
    scrollButtonBox.setPreferredSize(null);
    scrollButtonBox.setPreferredSize(new Dimension((int)(vertical ? Math.min(scrollButtonBox.getPreferredSize().getWidth(), panel.getWidth()) :
                                                   scrollButtonBox.getPreferredSize().getWidth()),
                                                   (int)(vertical ? scrollButtonBox.getPreferredSize().getHeight() :
                                                   Math.min(scrollButtonBox.getPreferredSize().getHeight(), panel.getHeight()))));
    buttonContainer.revalidate();
  }*/
}
