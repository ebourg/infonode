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


// $Id: WindowDecoder.java,v 1.8 2004/08/11 09:15:17 jesper Exp $
package net.infonode.docking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.8 $
 */
class WindowDecoder {
  static DockingWindow decodeWindow(ObjectInputStream in, ReadContext context) throws IOException {
    int id = in.readInt();

    switch (id) {
      case WindowIds.VIEW: {
        int size = in.readInt();
        byte[] viewData = new byte[size];
        in.read(viewData);
        ObjectInputStream viewIn = new ObjectInputStream(new ByteArrayInputStream(viewData));
        View view = context.getViewSerializer().readView(viewIn);

        if (view != null)
          view.read(viewIn, context);
        
        return view;
      }

      case WindowIds.SPLIT:
        {
          SplitWindow w = new SplitWindow(true);
          return w.read(in, context);
        }

      case WindowIds.TAB:
        {
          TabWindow w = new TabWindow();
          return w.read(in, context);
        }

      default:
        throw new IOException("Invalid window ID: " + id + "!");
    }
  }
}
