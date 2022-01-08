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


// $Id: ViewMap.java,v 1.7 2005/02/16 11:28:14 jesper Exp $
package net.infonode.docking.util;

import net.infonode.docking.View;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A map of views that handles view serialization by assigning an integer id to each view.
 * The id is unique for each view in the map. To guarantee serialization compatibility a view id must remain constant.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.7 $
 */
public class ViewMap extends AbstractViewMap {
  /**
   * Constructor.
   */
  public ViewMap() {
  }

  /**
   * Utility constructor that creates a map with a number of views.
   * A view gets it's index in the array as id.
   *
   * @param views the views to add to the map
   */
  public ViewMap(View[] views) {
    for (int i = 0; i < views.length; i++)
      addView(i, views[i]);
  }

  /**
   * Adds a view to the map.
   *
   * @param id   the view id
   * @param view the view
   */
  public void addView(int id, View view) {
    addView(new Integer(id), view);
  }

  /**
   * Removes a view with a specific id from the map.
   *
   * @param id the view id
   */
  public void removeView(int id) {
    removeView(new Integer(id));
  }

  /**
   * Returns the view with a specific id.
   *
   * @param id the view id
   * @return the view with the id
   */
  public View getView(int id) {
    return getView(new Integer(id));
  }

  protected void writeViewId(Object id, ObjectOutputStream out) throws IOException {
    out.writeInt(((Integer) id).intValue());
  }

  protected Object readViewId(ObjectInputStream in) throws IOException {
    return new Integer(in.readInt());
  }


}
