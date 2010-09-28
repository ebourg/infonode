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


// $Id: CloseButtonInfo.java,v 1.4 2004/09/24 16:29:56 jesper Exp $
package net.infonode.docking.internalutil;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.properties.propertymap.PropertyMapProperty;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
public class CloseButtonInfo extends AbstractButtonInfo {
  public CloseButtonInfo(PropertyMapProperty property) {
    super(property);
  }

  public boolean isVisible(DockingWindow window) {
    return true;
  }

  public ActionListener getActionListener(final DockingWindow window) {
    return new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          window.closeWithAbort();
        }
        catch (OperationAbortedException e1) {
          // Ignore
        }
      }
    };
  }

}
