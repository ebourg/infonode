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


// $Id: ConstChangeNotifyVectorMap.java,v 1.6 2004/11/11 14:11:14 jesper Exp $
package net.infonode.util.collection.notifymap;

import net.infonode.util.ValueChange;
import net.infonode.util.collection.map.ConstVectorMap;
import net.infonode.util.collection.map.MapAdapter;
import net.infonode.util.collection.map.base.ConstMap;
import net.infonode.util.collection.map.base.ConstMapIterator;

import java.util.ArrayList;

public class ConstChangeNotifyVectorMap extends AbstractConstChangeNotifyMap {
  private ConstVectorMap vectorMap = new ConstVectorMap();
  private ArrayList mapListeners;

  public void addListener(ChangeNotifyMapListener listener) {
    if (!hasListeners()) {
      mapListeners = new ArrayList(vectorMap.getMapCount() + 2);

      for (int i = 0; i < vectorMap.getMapCount(); i++) {
        addMapListener(i);
      }
    }

    super.addListener(listener);
  }

/*  public void addWeakListener(ChangeNotifyMapListener listener) {
    if (!hasListeners()) {
      mapListeners = new ArrayList(vectorMap.getMapCount() + 2);

      for (int i = 0; i < vectorMap.getMapCount(); i++) {
        addMapListener(i);
      }
    }

    super.addWeakListener(listener);
  }
*/
  public boolean removeListener(ChangeNotifyMapListener listener) {
    boolean result = super.removeListener(listener);

    if (result && !hasListeners() && mapListeners != null) {
      for (int i = vectorMap.getMapCount() - 1; i >= 0; i--) {
        removeMapListener(i);
      }

      mapListeners = null;
    }

    return result;
  }

  private Object getValue(Object key, int fromIndex, int toIndex) {
    for (int i = fromIndex; i < toIndex; i++) {
      Object value = getMap(i).get(key);

      if (value != null)
        return value;
    }

    return null;
  }

  public int getMapIndex(ConstMap map) {
    return vectorMap.getMapIndex(map);
  }

  public void addMap(ConstChangeNotifyMap map) {
    vectorMap.addMap(map);
  }

  public void addMap(int index, ConstChangeNotifyMap map) {
    vectorMap.addMap(index, map);

    if (hasListeners()) {
      addMapListener(index);
      MapAdapter changes = new MapAdapter();

      for (ConstMapIterator iterator = map.constIterator(); iterator.atEntry(); iterator.next()) {
        Object value = getValue(iterator.getKey(), 0, index);

        if (value == null) {
          Object mapValue = iterator.getValue();
          changes.put(iterator.getKey(),
                      new ValueChange(getValue(iterator.getKey(), index + 1, getMapCount()), mapValue));
        }
      }

      fireEntriesChanged(changes);
    }
  }

  private void addMapListener(int index) {
    if (mapListeners == null)
      mapListeners = new ArrayList(index + 2);

    final ConstChangeNotifyMap map = getMap(index);

    ChangeNotifyMapListener mapListener = new ChangeNotifyMapListener() {
      public void entriesChanged(ConstMap changes) {
        MapAdapter changes2 = new MapAdapter();
        int index = getMapIndex(map);

        for (ConstMapIterator iterator = changes.constIterator(); iterator.atEntry(); iterator.next()) {
          Object value = getValue(iterator.getKey(), 0, index);

          if (value == null) {
            ValueChange vc = (ValueChange) iterator.getValue();
            changes2.put(iterator.getKey(), vc.getOldValue() == null ? new ValueChange(
                getValue(iterator.getKey(), index + 1, getMapCount()), vc.getNewValue()) :
                                            vc.getNewValue() == null ? new ValueChange(vc.getOldValue(),
                                                                                       getValue(iterator.getKey(),
                                                                                                index + 1,
                                                                                                getMapCount())) :
                                            vc);
          }
        }

        fireEntriesChanged(changes2);
      }
    };

    mapListeners.add(index, mapListener);
    map.addListener(mapListener);
  }

  private void removeMapListener(int index) {
    ConstChangeNotifyMap map = getMap(index);
    map.removeListener((ChangeNotifyMapListener) mapListeners.get(index));
    mapListeners.remove(index);
  }

  public int getMapCount() {
    return vectorMap.getMapCount();
  }

  public void removeMap(int index) {
    ConstChangeNotifyMap map = getMap(index);
    vectorMap.removeMap(index);

    if (hasListeners()) {
      MapAdapter changes = new MapAdapter();

      for (ConstMapIterator iterator = map.constIterator(); iterator.atEntry(); iterator.next()) {
        Object value = getValue(iterator.getKey(), 0, index);

        if (value == null) {
          Object mapValue = iterator.getValue();
          changes.put(iterator.getKey(), new ValueChange(mapValue, getValue(iterator.getKey(), index, getMapCount())));
        }
      }

      fireEntriesChanged(changes);
    }
  }

  public Object get(Object key) {
    return vectorMap.get(key);
  }

  public boolean containsKey(Object key) {
    return vectorMap.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return vectorMap.containsValue(value);
  }

  public boolean isEmpty() {
    return vectorMap.isEmpty();
  }

  public ConstChangeNotifyMap getMap(int index) {
    return (ConstChangeNotifyMap) vectorMap.getMap(index);
  }

  public ConstMapIterator constIterator() {
    return vectorMap.constIterator();
  }
}
