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


// $Id: DockingWindowListener.java,v 1.10 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking;

/**
 * <p>
 * A listener for {@link DockingWindow} events. All events are propagated upwards in the window tree, so
 * a listener will receive events for the window that it was added to and all descendants of that window.
 * </p>
 *
 * <p>
 * Note: New methods might be added to this interface in the future. To ensure future compatibility inherit from
 * {@link DockingWindowAdapter} instead of directly implementing this interface.
 * </p>
 *
 * @author $Author: jesper $
 * @version $Revision: 1.10 $
 * @since IDW 1.1.0
 */
public interface DockingWindowListener {
  /**
   * Called when a window has been added.
   *
   * @param addedToWindow the parent window that the window was added to
   * @param addedWindow   the window that was added
   * @since IDW 1.3.0
   */
  void windowAdded(DockingWindow addedToWindow, DockingWindow addedWindow);

  /**
   * Called when a window has been removed.
   *
   * @param removedFromWindow the parent window that the window was removed from
   * @param removedWindow     the window that was removed
   * @since IDW 1.3.0
   */
  void windowRemoved(DockingWindow removedFromWindow, DockingWindow removedWindow);

  /**
   * Called when a window is shown, for example when it is selected in a TabWindow.
   *
   * @param window the window that was shown
   * @since IDW 1.3.0
   */
  void windowShown(DockingWindow window);

  /**
   * Called when a is hidden, for example when it is deselected in a TabWindow.
   *
   * @param window the window that was shown
   * @since IDW 1.3.0
   */
  void windowHidden(DockingWindow window);

  /**
   * Called when the focus moves from one view to another view.
   *
   * @param previouslyFocusedView the view that had focus before the focus moved, null means no view had focus
   * @param focusedView           the view that got focus, null means no view got focus
   * @since IDW 1.3.0
   */
  void viewFocusChanged(View previouslyFocusedView, View focusedView);

  /**
   * <p>
   * Called before the window that this listener is added to, or a child window of that window, is closed.
   * </p>
   *
   * <p>
   * Note that this method is only called when {@link DockingWindow#closeWithAbort()} is called explicitly, not
   * when a window is implicitly closed as a result of another method call. Throwing an {@link OperationAbortedException}
   * will cause the close operation to be aborted.
   * </p>
   *
   * @param window the window that is closing
   * @throws OperationAbortedException if this exception is thrown the close operation will be aborted
   */
  void windowClosing(DockingWindow window) throws OperationAbortedException;

  /**
   * <p>
   * Called after the window that this listener is added to, or a child window of that window, has been closed.
   * </p>
   *
   * <p>
   * Note that this method is only called when {@link DockingWindow#close()} or {@link DockingWindow#closeWithAbort()}
   * is called explicitly, not when a window is implicitly closed as a result of another method call.
   * </p>
   *
   * @param window the window that has been closed
   */
  void windowClosed(DockingWindow window);
}
