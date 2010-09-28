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


// $Id: ResizablePanel.java,v 1.10 2005/02/16 11:28:13 jesper Exp $
package net.infonode.gui.panel;

import net.infonode.gui.CursorManager;
import net.infonode.util.Direction;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.10 $
 */
public class ResizablePanel extends SimplePanel {
  private Direction direction;
  private int resizeWidth = 4;
  private boolean cursorChanged;
  private int offset = -1;
  private boolean mouseInside;

  public ResizablePanel(Direction _direction) {
    this(_direction, null);
  }

  public ResizablePanel(Direction _direction, Component mouseListenComponent) {
    this.direction = _direction;
    if (mouseListenComponent == null)
      mouseListenComponent = this;

    mouseListenComponent.addMouseListener(new MouseAdapter() {
      public void mouseExited(MouseEvent e) {
        if (offset == -1)
          resetCursor();

        mouseInside = false;
      }

      public void mouseEntered(MouseEvent e) {
        mouseInside = true;
      }

      public void mousePressed(MouseEvent e) {
        if (cursorChanged) {
          offset = direction == Direction.LEFT ? e.getPoint().x :
                   direction == Direction.RIGHT ? getWidth() - e.getPoint().x :
                   direction == Direction.UP ? e.getPoint().y :
                   getHeight() - e.getPoint().y;
        }
      }

      public void mouseReleased(MouseEvent e) {
        offset = -1;
        checkCursor(e.getPoint());
      }
    });

    mouseListenComponent.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        checkCursor(e.getPoint());
      }

      public void mouseDragged(MouseEvent e) {
        if (offset != -1) {
          int size = direction.isHorizontal() ?
                     (direction == Direction.LEFT ? getWidth() - e.getPoint().x + offset : e.getPoint().x + offset) :
                     (direction == Direction.UP ? getHeight() - e.getPoint().y + offset : e.getPoint().y + offset);
          setPreferredSize(getBoundedSize(size));
          revalidate();
        }
      }
    });
  }

  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    return getBoundedSize(direction.isHorizontal() ? d.width : d.height);
  }

  private Dimension getBoundedSize(int size) {
    if (direction.isHorizontal()) {
      return new Dimension(Math.max(getMinimumSize().width, Math.min(size, getMaximumSize().width)), 0);
    }
    else {
      return new Dimension(0, Math.max(getMinimumSize().height, Math.min(size, getMaximumSize().height)));
    }
  }

  public void setResizeWidth(int width) {
    this.resizeWidth = width;
  }

  public int getResizeWidth() {
    return resizeWidth;
  }

  private void checkCursor(Point point) {
    if (offset != -1)
      return;

    int dist = direction == Direction.UP ? point.y :
               direction == Direction.DOWN ? getHeight() - point.y :
               direction == Direction.LEFT ? point.x :
               getWidth() - point.x;

    if (dist >= 0 && dist < resizeWidth && mouseInside) {
      if (!cursorChanged) {
        cursorChanged = true;
        CursorManager.setGlobalCursor(this, new Cursor(direction == Direction.LEFT ? Cursor.W_RESIZE_CURSOR :
                                                       direction == Direction.RIGHT ? Cursor.E_RESIZE_CURSOR :
                                                       direction == Direction.UP ? Cursor.N_RESIZE_CURSOR :
                                                       Cursor.S_RESIZE_CURSOR));
      }
    }
    else
      resetCursor();
  }

  private void resetCursor() {
    CursorManager.resetGlobalCursor(this);
    cursorChanged = false;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setVisible(boolean aFlag) {
    super.setVisible(aFlag);
  }
}
