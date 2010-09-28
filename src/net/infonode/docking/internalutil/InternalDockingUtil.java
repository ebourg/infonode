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


// $Id: InternalDockingUtil.java,v 1.4 2004/08/11 13:47:58 jesper Exp $
package net.infonode.docking.internalutil;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.IntList;
import net.infonode.util.Printer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
public class InternalDockingUtil {
  public static IntList getWindowPath(DockingWindow window) {
    return getWindowPath(window, IntList.EMPTY_LIST);
  }

  /**
   * Returns the window located at <tt>windowPath</tt>.
   *
   * @param relativeToWindow the window the path is relative to
   * @param windowPath the window path
   * @return the window located at <tt>windowPath</tt>
   */
  public static DockingWindow getWindow(DockingWindow relativeToWindow, IntList windowPath) {
    return windowPath.isEmpty() ? relativeToWindow : windowPath.getValue() >= relativeToWindow.getChildWindowCount() ? null :
        getWindow(relativeToWindow.getChildWindow(windowPath.getValue()), windowPath.getNext());
  }

  private static IntList getWindowPath(DockingWindow window, IntList tail) {
    DockingWindow parent = window.getWindowParent();
    return parent == null ? tail : getWindowPath(parent, new IntList(parent.getChildWindowIndex(window), tail));
  }

  public static void addDebugMenuItems(JPopupMenu menu, final DockingWindow window) {
    menu.add("Dump Tree").addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dump(window, new Printer());
      }
    });
  }

  public static void dump(DockingWindow window, Printer printer) {
    DockingWindow parent = window.getWindowParent();

    printer.println(window.getClass().getName() + ", '" + window.getTitle() + "', parent: '" +
                    (parent == null ? "null" : parent.toString()) + "'" + (window.getChildWindowCount() > 0 ? ":" : ""));

    if (window.getChildWindowCount() > 0) {
      printer.beginSection();

      for (int i = 0; i < window.getChildWindowCount(); i++) {
        if (window.getChildWindow(i) == null)
          printer.println("null");
        else
          dump(window.getChildWindow(i), printer);
      }

      printer.endSection();
    }
  }

  public static RootWindow createInnerRootWindow(View[] views) {
    RootWindow rootWindow = DockingUtil.createRootWindow(new ViewMap(views), true);
    rootWindow.getRootWindowProperties().getWindowAreaProperties().setBackgroundColor(null);
//    rootWindow.getRootWindowProperties().getWindowAreaProperties().setBorder(null);
    rootWindow.getRootWindowProperties().getComponentProperties().setBackgroundColor(null);
    rootWindow.getRootWindowProperties().getComponentProperties().setBorder(null);
    return rootWindow;
  }

}
