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


// $Id: DraggableComponent.java,v 1.3 2004/06/18 14:04:44 jesper Exp $
package net.infonode.gui.draggable;

import net.infonode.gui.ComponentUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class DraggableComponent {
  private JComponent component;
  private JComponent[] eventComponents;

  private boolean reorderEnabled = true;
  private boolean enabled = true;
  private boolean reorderRestoreOnDrag = false;
  private boolean detectOuterAreaAsLine = true;
  private boolean enableInsideDrag = false;

  private boolean mousePressed = false;
  private boolean dragEventFired = false;
  private boolean dragStarted = false;

  private int dragIndex;
  private int dragFromIndex;
  private int abortDragKeyCode = KeyEvent.VK_ESCAPE;

  private ArrayList listeners;
  private JComponent outerParentArea;

  private KeyEventDispatcher abortDragKeyDispatcher = new KeyEventDispatcher() {
    public boolean dispatchKeyEvent(KeyEvent e) {
      if (mousePressed && e.getKeyCode() == abortDragKeyCode) {
        if (e.getID() == KeyEvent.KEY_PRESSED)
          dragCompleted(null);
        return true;
      }
      return false;
    }
  };

  private MouseInputListener mouseInputListener = new MouseInputAdapter() {
    public void mousePressed(MouseEvent e) {
      pressed(e);
    }

    public void mouseReleased(MouseEvent e) {
      released(e);
    }

    public void mouseDragged(MouseEvent e) {
      dragged(e);
    }
  };

  public DraggableComponent(JComponent component) {
    this(component, component);
  }

  public DraggableComponent(JComponent component, JComponent eventComponent) {
    this(component, new JComponent[]{eventComponent});
  }

  public DraggableComponent(JComponent component, JComponent[] eventComponents) {
    this.component = component;
    setEventComponents(eventComponents);
  }

  public void addListener(DraggableComponentListener l) {
    if (listeners == null)
      listeners = new ArrayList(2);

    listeners.add(l);
  }

  public void removeListener(DraggableComponentListener l) {
    if (listeners != null) {
      listeners.remove(l);

      if (listeners.size() == 0)
        listeners = null;
    }
  }

  public JComponent getComponent() {
    return component;
  }

  public JComponent[] getEventComponents() {
    return eventComponents;
  }

  public void setEventComponents(JComponent[] eventComponents) {
    if (this.eventComponents != null) {
      for (int i = 0; i < this.eventComponents.length; i++) {
        this.eventComponents[i].removeMouseListener(mouseInputListener);
        this.eventComponents[i].removeMouseMotionListener(mouseInputListener);
      }
    }

    this.eventComponents = eventComponents;

    if (this.eventComponents != null) {
      for (int i = 0; i < this.eventComponents.length; i++) {
        this.eventComponents[i].addMouseListener(mouseInputListener);
        this.eventComponents[i].addMouseMotionListener(mouseInputListener);
      }
    }
  }

  public int getAbortDragKeyCode() {
    return abortDragKeyCode;
  }

  public void setAbortDragKeyCode(int abortDragKeyCode) {
    this.abortDragKeyCode = abortDragKeyCode;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    if (this.enabled != enabled) {
      this.enabled = enabled;
      fireChangedEvent(enabled ? DraggableComponentEvent.TYPE_ENABLED : DraggableComponentEvent.TYPE_DISABLED);
    }
  }

  public boolean isReorderEnabled() {
    return reorderEnabled;
  }

  public void setReorderEnabled(boolean reorderEnabled) {
    this.reorderEnabled = reorderEnabled;
  }

  public boolean isReorderRestoreOnDrag() {
    return reorderRestoreOnDrag;
  }

  public void setReorderRestoreOnDrag(boolean reorderRestoreOnDrag) {
    this.reorderRestoreOnDrag = reorderRestoreOnDrag;
  }

  public boolean isDetectOuterAreaAsLine() {
    return detectOuterAreaAsLine;
  }

  public void setDetectOuterAreaAsLine(boolean detectOuterAreaAsLine) {
    this.detectOuterAreaAsLine = detectOuterAreaAsLine;
  }

  public boolean isEnableInsideDrag() {
    return enableInsideDrag;
  }

  public void setEnableInsideDrag(boolean enableInsideDrag) {
    this.enableInsideDrag = enableInsideDrag;
  }

  public void drag(Point p) {
    if (enabled) {
      dragIndex = ComponentUtils.getComponentIndex(component);
      dragFromIndex = dragIndex;
      doDrag(p);
    }
  }

  public void select() {
    if (enabled)
      fireSelectedEvent();
  }

  public void setOuterParentArea(JComponent outerParentArea) {
    this.outerParentArea = outerParentArea;
  }

  private void pressed(MouseEvent e) {
    if (enabled && e.getButton() == MouseEvent.BUTTON1) {
      dragStarted = false;
      KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(abortDragKeyDispatcher);
      mousePressed = true;
      dragIndex = ComponentUtils.getComponentIndex(component);
      dragFromIndex = dragIndex;

      fireChangedEvent(DraggableComponentEvent.TYPE_PRESSED);
    }
  }

  private void released(MouseEvent e) {
    if (mousePressed) {
      if (e.getButton() == MouseEvent.BUTTON1)
        dragCompleted(e);
      else {
        dragCompleted(null);
        e.consume();
      }
    }
  }

  private void dragged(MouseEvent e) {
    if (enabled && mousePressed) {
      Point p = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), component);
      if (dragStarted || enableInsideDrag || !component.contains(p)) {
        doDrag(p);
        fireDraggedEvent(p);
      }
    }
  }

  private void dragCompleted(MouseEvent e) {
    mousePressed = false;
    dragStarted = false;

    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(abortDragKeyDispatcher);

    Point p = null;
    Point p2 = null;
    if (e != null) {
      p = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), component);
      p2 = SwingUtilities.convertPoint((JComponent) e.getSource(), e.getPoint(), component.getParent());
    }

    if (e == null) {
      restoreComponentOrder();
      fireNotDroppedEvent();
    }
    else if (!checkParentContains(p2)) {
      restoreComponentOrder();
      fireDroppedEvent(p);
    }
    else {
      fireDroppedEvent(p);
      //if (component.contains(p))
      fireSelectedEvent();
    }

    fireChangedEvent(DraggableComponentEvent.TYPE_RELEASED);
  }

  private void updateParent() {
    if (component.getParent() != null) {
      ((JComponent) component.getParent()).revalidate();
      component.getParent().repaint();
    }
  }

  private void doDrag(Point p) {
    dragStarted = true;
    JComponent parent = (JComponent) component.getParent();

    if (parent.getComponentCount() == 1)
      return;

    Point p2 = SwingUtilities.convertPoint(component, p, parent);
    int toIndex = getMoveComponentIndex(p2);
    if (toIndex != -1) {
      toIndex = Math.min(toIndex, parent.getComponentCount() - 1);
      Component fromComponent = parent.getComponent(dragIndex);
      int fromDimension;
      int toPos;
      int toDimension;

      if (isVerticalDrag()) {
        fromDimension = fromComponent.getHeight();
        toPos = (int) SwingUtilities.convertPoint(parent, p2, parent.getComponent(toIndex)).getY();
        toDimension = parent.getComponent(toIndex).getHeight();
      }
      else {
        fromDimension = fromComponent.getWidth();
        toPos = (int) SwingUtilities.convertPoint(parent, p2, parent.getComponent(toIndex)).getX();
        toDimension = parent.getComponent(toIndex).getWidth();
      }

      if ((toIndex > dragIndex && toDimension - toPos > fromDimension) ||
          ((dragIndex == -1 || toIndex < dragIndex) && toPos > fromDimension))
        return;

      if (reorderEnabled && dragIndex != -1 && dragIndex != toIndex) {
        parent.remove(dragIndex);
        parent.add(fromComponent, toIndex);
        fireChangedEvent(DraggableComponentEvent.TYPE_MOVED);
      }
    }

    if (toIndex < 0) {
      if (reorderRestoreOnDrag)
        restoreComponentOrder();
    } else if (reorderEnabled)
      dragIndex = toIndex;
  }

  private boolean isVerticalDrag() {
    JComponent parent = (JComponent)component.getParent();
    if (parent.getComponentCount() > 1)
      return parent.getComponent(0).getY() < parent.getComponent(1).getY();

    return false;
  }

  private boolean checkParentContains(Point p) {
    if (outerParentArea == null)
      return component.getParent().contains(p);

    Point p2 = SwingUtilities.convertPoint(component.getParent(), p, outerParentArea);
    if (detectOuterAreaAsLine) {
      Insets i = outerParentArea.getInsets();
      return component.getParent().contains(p) || (outerParentArea.contains(p2) &&
              (isVerticalDrag() ? (p2.getX() >= i.left && p2.getX() < (outerParentArea.getWidth() - i.right)) :
              (p2.getY() >= i.top && p2.getY() < (outerParentArea.getHeight() - i.bottom))));
    }

    return component.getParent().contains(p) || outerParentArea.contains(p2);
  }

  private int getMoveComponentIndex(Point p) {
    JComponent parent = (JComponent)component.getParent();
    if (checkParentContains(p)) {
      boolean vertical = isVerticalDrag();
      for (int i = 0; i < parent.getComponentCount() - 1; i++) {
        Point p2 = parent.getComponent(i + 1).getLocation();

        if (vertical) {
          if (p.getY() >= 0 && p.getY() < p2.getY())
            return i;
        } else {
          if (p.getX() >= 0 && p.getX() < p2.getX())
            return i;
        }
      }

      if (dragIndex == -1)
        return parent.getComponentCount();
      else if (vertical)
        return p.getY() < 0 ? 0 : parent.getComponentCount() - 1;
      else
        return p.getX() < 0 ? 0 : parent.getComponentCount() - 1;
    }

    return -1;
  }

  private void restoreComponentOrder() {
    if (reorderEnabled && dragIndex != -1 &&
            dragFromIndex != -1 && dragIndex != dragFromIndex) {
      Container parent = component.getParent();
      Component comp = parent.getComponent(dragIndex);
      parent.remove(comp);
      dragIndex = dragFromIndex;
      parent.add(comp, dragIndex);
      fireChangedEvent(DraggableComponentEvent.TYPE_MOVED);
    }
  }

  private void fireChangedEvent(int type) {
    updateParent();

    if (listeners != null) {
      DraggableComponentEvent event = new DraggableComponentEvent(this, type);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentListener)l[i]).changed(event);
    }
  }

  private void fireSelectedEvent() {
    updateParent();

    if (listeners != null) {
      DraggableComponentEvent event = new DraggableComponentEvent(this);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentListener)l[i]).selected(event);
    }
  }

  private void fireDraggedEvent(Point p) {
    dragEventFired = true;
    if (listeners != null) {
      DraggableComponentEvent event = new DraggableComponentEvent(this, p);
      Object l[] = listeners.toArray();
      for (int i = 0; i < l.length; i++)
        ((DraggableComponentListener)l[i]).dragged(event);
    }
  }

  private void fireDroppedEvent(Point p) {
    updateParent();

    if (dragEventFired) {
      dragEventFired = false;
      if (listeners != null) {
        DraggableComponentEvent event = new DraggableComponentEvent(this, p);
        Object l[] = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((DraggableComponentListener)l[i]).dropped(event);
      }
    }
  }

  private void fireNotDroppedEvent() {
    updateParent();

    if (dragEventFired) {
      dragEventFired = false;
      if (listeners != null) {
        DraggableComponentEvent event = new DraggableComponentEvent(this);
        Object l[] = listeners.toArray();
        for (int i = 0; i < l.length; i++)
          ((DraggableComponentListener)l[i]).dragAborted(event);
      }
    }
  }
}
