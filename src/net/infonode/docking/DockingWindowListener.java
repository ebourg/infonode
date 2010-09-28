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


// $Id: DockingWindowListener.java,v 1.6 2004/09/28 15:07:29 jesper Exp $
package net.infonode.docking;

/**
 * A listener for {@link DockingWindow} events.
 *
 * Note: New methods might be added to this interface in the future. To ensure future compatibility inherit from
 * {@link DockingWindowAdapter} instead of directly implementing this interface.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 * @since IDW 1.1.0
 */
public interface DockingWindowListener {
  /**
   * Called before a view is closed.
   * Note that this method is only called when {@link DockingWindow#closeWithAbort()} is called explicitly, not
   * when a window is implicitly closed as a result of another method call. Throwing an {@link OperationAbortedException}
   * will cause the close operation to be aborted.
   *
   * @param window the window that is closing
   * @throws OperationAbortedException if this exception is thrown the close operation will be aborted
   */
  void windowClosing(DockingWindow window) throws OperationAbortedException;

  /**
   * Called after a view has been closed.
   * Note that this method is only called when {@link DockingWindow#close()} or {@link DockingWindow#closeWithAbort()}
   * is called explicitly, not when a window is implicitly closed as a result of another method call.
   *
   * @param window the window that has been closed
   */
  void windowClosed(DockingWindow window);
}
