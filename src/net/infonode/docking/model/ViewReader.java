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


// $Id: ViewReader.java,v 1.3 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking.model;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.View;
import net.infonode.docking.internal.ReadContext;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
public interface ViewReader {
  ViewItem readViewItem(ObjectInputStream in, ReadContext context) throws IOException;

  View readView(ObjectInputStream in, ReadContext context) throws IOException;

  TabWindow createTabWindow(DockingWindow[] childWindows, TabWindowItem windowItem);

  SplitWindow createSplitWindow(DockingWindow leftWindow, DockingWindow rightWindow, SplitWindowItem windowItem);

  WindowItem readWindowItem(ObjectInputStream in, ReadContext context) throws IOException;

}
