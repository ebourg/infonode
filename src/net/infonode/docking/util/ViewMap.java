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


// $Id: ViewMap.java,v 1.3 2004/08/11 09:15:17 jesper Exp $
package net.infonode.docking.util;

import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A map of views that handles view serialization by assigning an integer id to each view.
 * The id is unique for each view in the map. To guarantee serialization compatibility a view id must remain constant.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
public class ViewMap implements ViewSerializer, ViewFactoryManager {
  private HashMap viewMap = new HashMap();
  private ArrayList views = new ArrayList();

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
    for (int i=0; i<views.length; i++)
      addView(i, views[i]);
  }

  /**
   * Adds a view to the map.
   *
   * @param id the view id
   * @param view the view
   */
  public void addView(int id, View view) {
    Object oldView = viewMap.put(new Integer(id), view);

    if (oldView != null)
      views.remove(oldView);

    views.add(view);
  }

  /**
   * Removes a view with a specific id from the map.
   *
   * @param id the view id
   */
  public void removeView(int id) {
    Object view = viewMap.remove(new Integer(id));

    if (view != null)
      views.remove(view);
  }

  /**
   * Returns the view with a specific id.
   *
   * @param id the view id
   * @return the view with the id
   */
  public View getView(int id) {
    return (View) viewMap.get(new Integer(id));
  }

  /**
   * Returns the number of views in this map.
   *
   * @return the number of views in this map
   */
  public int getViewCount() {
    return viewMap.size();
  }

  /**
   * Returns the view at a specific index.
   * The view index is the same as the number of views in the map when the view was added to the map.
   *
   * @param index the view index
   * @return the view at the index
   */
  public View getViewAtIndex(int index) {
    return (View) views.get(index);
  }

  public ViewFactory[] getViewFactories() {
    ArrayList f = new ArrayList();

    for (int i = 0; i < views.size(); i++) {
      final View view = (View) views.get(i);

      if (view.getRootWindow() == null)
        f.add(new ViewFactory() {
          public Icon getIcon() {
            return view.getIcon();
          }

          public String getTitle() {
            return view.getTitle();
          }

          public View createView() {
            return view;
          }
        });
    }

    return (ViewFactory[]) f.toArray(new ViewFactory[f.size()]);
  }

  public void writeView(View view, ObjectOutputStream out) throws IOException {
    for (Iterator it = viewMap.entrySet().iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();

      if (entry.getValue() == view) {
        out.writeInt(((Integer) entry.getKey()).intValue());
        return;
      }
    }

    throw new IOException("Serialization of unknown view!");
  }

  public View readView(ObjectInputStream in) throws IOException {
    int id = in.readInt();
    return (View) viewMap.get(new Integer(id));
  }
}
