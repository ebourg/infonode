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


// $Id: AbstractConstChangeNotifyMap.java,v 1.8 2004/11/11 14:11:14 jesper Exp $
package net.infonode.util.collection.notifymap;

import net.infonode.util.ValueChange;
import net.infonode.util.collection.map.MapAdapter;
import net.infonode.util.collection.map.base.ConstMap;
import net.infonode.util.collection.map.base.Map;

import java.util.ArrayList;

abstract public class AbstractConstChangeNotifyMap implements ConstChangeNotifyMap {
  private ArrayList listeners;
//  private WeakSet weakListeners;

  public void addListener(ChangeNotifyMapListener listener) {
    removeListener(listener);

    if (listeners == null)
      listeners = new ArrayList(2);

    listeners.add(listener);
  }

  public boolean removeListener(ChangeNotifyMapListener listener) {
    if (listeners != null && listeners.remove(listener)) {
      if (listeners.size() == 0)
        listeners = null;

      return true;
    }
/*    else if (weakListeners != null)
      return weakListeners.remove(listener);
  */
    return false;
  }

/*  public void addWeakListener(ChangeNotifyMapListener listener) {
    removeListener(listener);

    if (weakListeners == null)
      weakListeners = new WeakSet(2);

    weakListeners.add(listener);
  }
*/
  protected void fireEntryRemoved(Object key, Object value) {
    fireEntryChanged(key, value, null);
  }

  protected void fireEntryChanged(Object key, Object oldValue, Object newValue) {
    Map map = new MapAdapter();
    map.put(key, new ValueChange(oldValue, newValue));
    fireEntriesChanged(map);
  }

  protected void fireEntriesChanged(final ConstMap changes) {
    if (changes.isEmpty())
      return;

    if (listeners != null) {
      ChangeNotifyMapListener[] l = (ChangeNotifyMapListener[]) listeners.toArray(
          new ChangeNotifyMapListener[listeners.size()]);

      for (int i = 0; i < l.length; i++)
        l[i].entriesChanged(changes);
    }

/*    if (weakListeners != null) {
      weakListeners.each(new Closure() {
        public void apply(Object object) {
          ((ChangeNotifyMapListener) object).entriesChanged(changes);
        }
      });
    }*/
  }

  protected boolean hasListeners() {
    return (listeners != null && !listeners.isEmpty());
    //||    (weakListeners != null && !weakListeners.isEmpty());
  }

}
