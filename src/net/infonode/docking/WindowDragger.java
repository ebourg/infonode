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


// $Id: WindowDragger.java,v 1.6 2004/07/06 15:00:02 jesper Exp $
package net.infonode.docking;

import net.infonode.gui.CursorManager;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DragSource;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
class WindowDragger {
  private DockingWindow dragWindow;
  private DockingWindow previousDropWindow;
  private RootWindow rootWindow;

  WindowDragger(DockingWindow dragWindow) {
    this.dragWindow = dragWindow;
    rootWindow = dragWindow.getRootWindow();
  }

  void undoDrag() {
    if (previousDropWindow != null)
      previousDropWindow.abortDrop();
  }

  private void stopDrag() {
    CursorManager.resetGlobalCursor(dragWindow);
    rootWindow.setText(null, null);
    rootWindow.setRectangle(null);
  }

  void abort() {
    stopDrag();

    if (previousDropWindow != null) {
      previousDropWindow.abortDrop();
      previousDropWindow = null;
    }
  }

  void drop(Point point) {
    stopDrag();

    if (dragWindow != null && previousDropWindow != null)
      previousDropWindow.doDrop(SwingUtilities.convertPoint(rootWindow, point, previousDropWindow), dragWindow);
  }

  void dragTo(Point point) {
    rootWindow.setRectangle(null);
    rootWindow.setText(point, dragWindow.getTitle());

    DockingWindow dropWindow = getDeepestWindowAt(rootWindow, point.x, point.y);

    if (previousDropWindow != null && previousDropWindow != dropWindow)
      undoDrag();

    if (dropWindow == dragWindow)
      dropWindow = null;

    if (dropWindow != null)
      dropWindow = dropWindow.acceptDrop(SwingUtilities.convertPoint(rootWindow, point, dropWindow),
                                         dragWindow);

    CursorManager.setGlobalCursor(dragWindow,
                                  dropWindow == null ? DragSource.DefaultMoveNoDrop : DragSource.DefaultMoveDrop);
    previousDropWindow = dropWindow;
  }

  private DockingWindow getDeepestWindowAt(Component c, int x, int y) {
    if (c == null || !c.isVisible() || !c.contains(x, y))
      return null;

    if (c instanceof Container) {
      Component components[] = ((Container) c).getComponents();

      for (int i = 0; i < components.length; i++) {
        DockingWindow w = getDeepestWindowAt(components[i], x - components[i].getX(), y - components[i].getY());

        if (w != null)
          return w;
      }
    }

    if (c instanceof DockingWindow) {
      DockingWindow w = (DockingWindow) c;
      return w.getRootWindow() == rootWindow ? w : null;
    }
    else
      return null;
  }

}
