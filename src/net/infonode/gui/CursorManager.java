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


// $Id: CursorManager.java,v 1.2 2004/06/30 08:19:34 jesper Exp $
package net.infonode.gui;

import net.infonode.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.2 $
 */
public class CursorManager {
  private static HashMap oldCursors = new HashMap();

  public static void setGlobalCursor(JComponent component, Cursor cursor) {
    Container root = component.getTopLevelAncestor();

    if (root == null)
      return;

    if (!oldCursors.containsKey(root)) {
      oldCursors.put(root, root.getCursor());
      component.getRootPane().getGlassPane().setVisible(true);
    }

    if (!Utils.equals(root.getCursor(), cursor))
      root.setCursor(cursor);
  }

  public static void resetGlobalCursor(JComponent component) {
    Container root = component.getTopLevelAncestor();

    if (root != null && oldCursors.containsKey(root)) {
      root.setCursor((Cursor) oldCursors.remove(root));
      component.getRootPane().getGlassPane().setVisible(false);
    }
  }
}
