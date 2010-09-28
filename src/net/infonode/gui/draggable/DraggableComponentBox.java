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


// $Id: DraggableComponentBox.java,v 1.36 2005/02/16 11:28:11 jesper Exp $

package net.infonode.gui.draggable;

import net.infonode.gui.*;
import net.infonode.gui.layout.DirectionLayout;
import net.infonode.gui.panel.SimplePanel;
import net.infonode.util.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class DraggableComponentBox extends SimplePanel {
  private JComponent componentBox;
  private JComponent componentContainer;

  private JComponent outerParentArea = this;
  private Direction componentDirection = Direction.UP;
  private boolean scrollEnabled = false;
  private boolean ensureSelectedVisible;
  private boolean autoSelect = true;
  private boolean descendingSortOrder = true;

  private int scrollOffset;
  private int iconSize;
  private DraggableComponent selectedComponent;
  private DraggableComponent topComponent;
  private ArrayList listeners;
  private ArrayList componentList = new ArrayList(4);

  private ArrayList layoutOrderList = new ArrayList();
  private ScrollButtonBox scrollButtonBox;

  private boolean useDefaultScrollButtons = true;

  private DraggableComponentListener draggableComponentListener = new DraggableComponentListener() {
    public void changed(DraggableComponentEvent event) {
      if (event.getType() == DraggableComponentEvent.TYPE_MOVED)
        sortComponentList(!descendingSortOrder);
      fireChangedEvent(event);
    }

    public void selected(DraggableComponentEvent event) {
      doSelectComponent(event.getSource());
    }

    public void dragged(DraggableComponentEvent event) {
      fireDraggedEvent(event);
    }

    public void dropped(DraggableComponentEvent event) {
      fireDroppedEvent(event);
    }

    public void dragAborted(DraggableComponentEvent event) {
      fireNotDroppedEvent(event);
    }
  };

  public DraggableComponentBox(int iconSize) {
    this(iconSize, true);
  }

  public DraggableComponentBox(int iconSize, boolean useDefaultScrollButtons) {
    this.iconSize = iconSize;
    this.useDefaultScrollButtons = useDefaultScrollButtons;
    // Fix minimum size when flipping direction
    final DirectionLayout layout = new DirectionLayout(
        componentDirection == Direction.UP ?
        Direction.RIGHT :
        componentDirection == Direction.LEFT
        ?
        Direction.DOWN
        :
        componentDirection == Direction.DOWN ?
        Direction.RIGHT :
        Direction.DOWN) {
      public Dimension minimumLayoutSize(Container parent) {
        Dimension min = super.minimumLayoutSize(parent);
        Dimension pref = super.preferredLayoutSize(parent);
        return componentDirection.isHorizontal() ?
               new Dimension(pref.width, min.height) : new Dimension(min.width, pref.height);
      }
    };
    layout.setLayoutOrderList(layoutOrderList);

    componentBox = new SimplePanel(layout) {
      public boolean isOptimizedDrawingEnabled() {
        return DraggableComponentBox.this != null && getComponentSpacing() >= 0;
      }
    };

    componentBox.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        fireChangedEvent();
      }

      public void componentMoved(ComponentEvent e) {
        fireChangedEvent();
      }
    });

    initialize();
  }

  public void addListener(DraggableComponentBoxListener listener) {
    if (listeners == null)
      listeners = new ArrayList(2);

    listeners.add(listener);
  }

  public void removeListener(DraggableComponentBoxListener listener) {
    if (listeners != null) {
      listeners.remove(listener);

      if (listeners.size() == 0)
        listeners = null;
    }
  }


  public void addDraggableComponent(DraggableComponent component) {
    insertDraggableComponent(component, -1);
  }

  public void insertDraggableComponent(DraggableComponent component, int index) {
    component.setLayoutOrderList(layoutOrderList);

    component.addListener(draggableComponentListener);
    if (index < 0) {
      layoutOrderList.add(component.getComponent());
      componentBox.add(component.getComponent());
    }
    else {
      layoutOrderList.add(index, component.getComponent());
      componentBox.add(component.getComponent(), index);
    }

    sortComponentList(!descendingSortOrder);

    componentList.add(component);
    component.setOuterParentArea(outerParentArea);
    fireAddedEvent(component);
    if (autoSelect && layoutOrderList.size() == 1 && selectedComponent == null && component.isEnabled())
      doSelectComponent(component);
  }

  public void insertDraggableComponent(DraggableComponent component, Point p) {
    int componentIndex = getComponentIndexAtPoint(p);
    if (componentIndex != -1 && layoutOrderList.size() > 0)
      insertDraggableComponent(component, componentIndex);
    else
      insertDraggableComponent(component, -1);
  }

  public void selectDraggableComponent(DraggableComponent component) {
    if (component == null) {
      if (selectedComponent != null) {
        DraggableComponent oldSelected = selectedComponent;
        selectedComponent = null;
        fireSelectedEvent(selectedComponent, oldSelected);
        componentBox.repaint();
      }
    }
    else
      component.select();
  }

  public void removeDraggableComponent(DraggableComponent component) {
    if (component != null && component.getComponent().getParent() == componentBox) {
      //component.abortDrag();
      int index = layoutOrderList.indexOf(component.getComponent());
      component.removeListener(draggableComponentListener);
      if (component == topComponent)
        topComponent = null;
      if (layoutOrderList.size() > 1 && selectedComponent != null) {
        if (selectedComponent == component) {
          if (autoSelect) {
            int selectIndex = findSelectableComponentIndex(index);
            if (selectIndex > -1)
              selectDraggableComponent(findDraggableComponent((Component) layoutOrderList.get(selectIndex)));
            else
              selectedComponent = null;
          }
          else {
            selectDraggableComponent(null);
          }
        }
      }
      else {
        if (selectedComponent != null) {
          DraggableComponent oldSelected = selectedComponent;
          selectedComponent = null;
          fireSelectedEvent(selectedComponent, oldSelected);
        }
      }
      componentList.remove(component);
      layoutOrderList.remove(component.getComponent());
      componentBox.remove(component.getComponent());
      componentBox.validate();
      component.setLayoutOrderList(null);

      sortComponentList(!descendingSortOrder);

      fireRemovedEvent(component);
    }
  }

  public DraggableComponent getSelectedDraggableComponent() {
    return selectedComponent;
  }

  public int getDraggableComponentCount() {
    return layoutOrderList.size();
  }

  public DraggableComponent getDraggableComponentAt(int index) {
    return findDraggableComponent((Component) layoutOrderList.get(index));
  }

  public int getDraggableComponentIndex(DraggableComponent component) {
    return layoutOrderList.indexOf(component.getComponent());
  }

  public boolean getDepthSortOrder() {
    return descendingSortOrder;
  }

  public void setDepthSortOrder(boolean descending) {
    this.descendingSortOrder = descending;
    sortComponentList(!descending);
  }

  public boolean isScrollEnabled() {
    return scrollEnabled;
  }

  public void setScrollEnabled(boolean scrollEnabled) {
    if (scrollEnabled != this.scrollEnabled) {
      this.scrollEnabled = scrollEnabled;
      initialize();
    }
  }

  public int getScrollOffset() {
    return scrollOffset;
  }

  public void setScrollOffset(int scrollOffset) {
    if (scrollOffset != this.scrollOffset) {
      this.scrollOffset = scrollOffset;
      if (scrollEnabled)
        ((ScrollableBox) componentContainer).setScrollOffset(scrollOffset);
    }
  }

  public int getComponentSpacing() {
    return getDirectionLayout().getComponentSpacing();
  }

  public void setComponentSpacing(int componentSpacing) {
    if (componentSpacing != getDirectionLayout().getComponentSpacing()) {
      if (getComponentSpacing() < 0 && componentSpacing >= 0) {
        DraggableComponent tmp = topComponent;
        sortComponentList(false);
        topComponent = tmp;
      }
      getDirectionLayout().setComponentSpacing(componentSpacing);
      sortComponentList(!descendingSortOrder);
      componentBox.revalidate();
    }
  }

  public boolean isEnsureSelectedVisible() {
    return ensureSelectedVisible;
  }

  public void setEnsureSelectedVisible(boolean ensureSelectedVisible) {
    this.ensureSelectedVisible = ensureSelectedVisible;
  }

  public boolean isAutoSelect() {
    return autoSelect;
  }

  public void setAutoSelect(boolean autoSelect) {
    this.autoSelect = autoSelect;
  }

  public Direction getComponentDirection() {
    return componentDirection;
  }

  public void setComponentDirection(Direction componentDirection) {
    if (componentDirection != this.componentDirection) {
      this.componentDirection = componentDirection;
      getDirectionLayout().setDirection(
          componentDirection == Direction.UP ?
          Direction.RIGHT :
          componentDirection == Direction.LEFT ?
          Direction.DOWN :
          componentDirection == Direction.DOWN
          ?
          Direction.RIGHT
          :
          Direction.DOWN);
      if (scrollEnabled) {
        scrollButtonBox.setVertical(componentDirection.isHorizontal());
        ((ScrollableBox) componentContainer).setVertical(componentDirection.isHorizontal());
      }
    }
  }

  public void setTopComponent(DraggableComponent topComponent) {
    if (topComponent != this.topComponent) {
      this.topComponent = topComponent;
      sortComponentList(!descendingSortOrder);
    }
  }

  public ScrollButtonBox getScrollButtonBox() {
    return scrollButtonBox;
  }

  public JComponent getOuterParentArea() {
    return outerParentArea;
  }

  public void setOuterParentArea(JComponent outerParentArea) {
    this.outerParentArea = outerParentArea;
  }

  public void dragDraggableComponent(DraggableComponent component, Point p) {
    component.drag(SwingUtilities.convertPoint(this, p, component.getComponent()));
  }

  public Dimension getMaximumSize() {
    if (scrollEnabled)
      return getPreferredSize();

    if (componentDirection == Direction.LEFT || componentDirection == Direction.RIGHT)
      return new Dimension((int) getPreferredSize().getWidth(), (int) super.getMaximumSize().getHeight());

    return new Dimension((int) super.getMaximumSize().getWidth(), (int) getPreferredSize().getHeight());

  }

  public Dimension getInnerSize() {
    return scrollEnabled ? componentBox.getPreferredSize() : componentBox.getSize();
  }

  private void sortComponentList(boolean reverseSort) {
    if (getComponentSpacing() < 0) {
      Component c;
      Component tc = topComponent != null ? topComponent.getComponent() : null;

      componentBox.removeAll();

      if (tc != null)
        componentBox.add(tc);

      int size = layoutOrderList.size();
      for (int i = 0; i < size; i++) {
        c = (Component) layoutOrderList.get(reverseSort ? size - i - 1 : i);
        if (c != tc)
          componentBox.add(c);
      }
    }
  }

  private int getComponentIndexAtPoint(Point p) {
    JComponent c = null;
    Point p2 = SwingUtilities.convertPoint(this, p, componentBox);
    Point p3 = SwingUtilities.convertPoint(componentBox, p, outerParentArea);
    if (outerParentArea.contains(p3))
      c = (JComponent) ComponentUtil.getChildAtLine(componentBox,
                                                    p2,
                                                    getDirectionLayout().getDirection().isHorizontal());

    return layoutOrderList.indexOf(c);
  }

  private void doSelectComponent(DraggableComponent component) {
    if (selectedComponent != null) {
      DraggableComponent oldSelected = selectedComponent;
      selectedComponent = component;
      ensureSelectedVisible();
      fireSelectedEvent(selectedComponent, oldSelected);
    }
    else {
      selectedComponent = component;
      ensureSelectedVisible();
      fireSelectedEvent(selectedComponent, null);
    }
  }

  private int findSelectableComponentIndex(int index) {
    int selectIndex = -1;
    int k;
    for (int i = 0; i < layoutOrderList.size(); i++) {
      if ((findDraggableComponent((Component) layoutOrderList.get(i))).isEnabled() && i != index) {
        k = selectIndex;
        selectIndex = i;
        if (k < index && selectIndex > index)
          return selectIndex;
        else if (k > index && selectIndex > index)
          return k;
      }
    }

    return selectIndex;
  }

  private DraggableComponent findDraggableComponent(Component c) {
    for (int i = 0; i < componentList.size(); i++)
      if (((DraggableComponent) componentList.get(i)).getComponent() == c)
        return (DraggableComponent) componentList.get(i);

    return null;
  }

  private DirectionLayout getDirectionLayout() {
    return (DirectionLayout) componentBox.getLayout();
  }

  private void initialize() {
    if (componentContainer != null)
      remove(componentContainer);

    DirectionLayout layout = getDirectionLayout();
    layout.setCompressing(!scrollEnabled);

    if (scrollEnabled) {
      if (useDefaultScrollButtons)
        scrollButtonBox = new ScrollButtonBox(componentDirection.isHorizontal(), iconSize);
      else
        scrollButtonBox = new ScrollButtonBox(componentDirection.isHorizontal(), null, null, null, null);

      final ScrollableBox scrollableBox = new ScrollableBox(componentBox,
                                                            componentDirection.isHorizontal(),
                                                            scrollOffset);
      scrollableBox.setLayoutOrderList(layoutOrderList);
      scrollButtonBox.addListener(new ScrollButtonBoxListener() {
        public void scrollButton1() {
          scrollableBox.scrollLeft(1);
        }

        public void scrollButton2() {
          scrollableBox.scrollRight(1);
        }
      });

      scrollButtonBox.setButton1Enabled(!scrollableBox.isLeftEnd());
      scrollButtonBox.setButton2Enabled(!scrollableBox.isRightEnd());

      scrollableBox.addScrollableBoxListener(new ScrollableBoxListener() {
        public void scrolledLeft(ScrollableBox box) {
          scrollButtonBox.setButton1Enabled(!box.isLeftEnd());
          scrollButtonBox.setButton2Enabled(true);
        }

        public void scrolledRight(ScrollableBox box) {
          scrollButtonBox.setButton1Enabled(true);
          scrollButtonBox.setButton2Enabled(!box.isRightEnd());
        }

        public void changed(ScrollableBox box) {
          fireChangedEvent();
        }
      });
      componentContainer = scrollableBox;
    }
    else {
      scrollButtonBox = null;
      componentContainer = componentBox;
    }

    componentContainer.setAlignmentY(0);
    add(componentContainer, BorderLayout.CENTER);
  }

  private void ensureSelectedVisible() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (scrollEnabled && ensureSelectedVisible && selectedComponent != null)
          ((ScrollableBox) componentContainer).ensureVisible(layoutOrderList.indexOf(selectedComponent.getComponent()));
      }
    });
  }

  private void fireDraggedEvent(DraggableComponentEvent e) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this,
                                                                        e.getSource(),
                                                                        e,
                                                                        SwingUtilities.convertPoint(
                                                                            e.getSource().getComponent(),
                                                                            e.getMouseEvent().getPoint(),
                                                                            this));
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).componentDragged(event);
    }
  }

  private void fireDroppedEvent(DraggableComponentEvent e) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this,
                                                                        e.getSource(),
                                                                        e,
                                                                        SwingUtilities.convertPoint(
                                                                            e.getSource().getComponent(),
                                                                            e.getMouseEvent().getPoint(),
                                                                            this));
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).componentDropped(event);
    }
  }

  private void fireNotDroppedEvent(DraggableComponentEvent e) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, e.getSource(), e);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).componentDragAborted(event);
    }
  }

  private void fireSelectedEvent(DraggableComponent component, DraggableComponent oldDraggableComponent) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, component, oldDraggableComponent);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).componentSelected(event);
    }
  }

  private void fireAddedEvent(DraggableComponent component) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, component);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).componentAdded(event);
    }
  }

  private void fireRemovedEvent(DraggableComponent component) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, component);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).componentRemoved(event);
    }
  }

  private void fireChangedEvent(DraggableComponentEvent e) {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this, e.getSource(), e);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).changed(event);
    }
  }

  private void fireChangedEvent() {
    if (listeners != null) {
      DraggableComponentBoxEvent event = new DraggableComponentBoxEvent(this);
      Object[] l = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentBoxListener) l[i]).changed(event);
    }
  }
}