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


// $Id: PropertyMapManager.java,v 1.8 2004/11/11 14:10:12 jesper Exp $
package net.infonode.properties.propertymap;

import net.infonode.properties.propertymap.value.PropertyValue;
import net.infonode.util.ValueChange;
import net.infonode.util.collection.map.base.ConstMap;
import net.infonode.util.collection.map.base.ConstMapIterator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Utility class for performing multiple modifications to {@link PropertyMap}'s and merging change notifications to
 * optimize performance.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.8 $
 */
public class PropertyMapManager {
  private static final PropertyMapManager INSTANCE = new PropertyMapManager();

  private HashMap changes;
  private int batchCounter;

  public static PropertyMapManager getInstance() {
    return INSTANCE;
  }

  void addMapChanges(PropertyMapImpl propertyMap, ConstMap mapChanges) {
    HashMap map = (HashMap) changes.get(propertyMap);

    if (map == null) {
      map = new HashMap();
      changes.put(propertyMap, map);
    }

    for (ConstMapIterator iterator = mapChanges.constIterator(); iterator.atEntry(); iterator.next()) {
      ValueChange vc = (ValueChange) iterator.getValue();
      Object newValue = vc.getNewValue() == null ? null : ((PropertyValue) vc.getNewValue()).get(propertyMap);
      Object value = map.get(iterator.getKey());
      map.put(iterator.getKey(),
              value == null ? new ValueChange(
                  vc.getOldValue() == null ? null : ((PropertyValue) vc.getOldValue()).get(propertyMap),
                  newValue) :
              new ValueChange(((ValueChange) value).getOldValue(), newValue));
    }
  }

  /**
   * Invokes {@link Runnable#run}, stores and merges all change notifications occuring in all property maps during
   * this invokation. When {@link Runnable#run} returns, all stored change notifications are triggered at once.
   * This method is re-entrant, so it's safe to call it from inside {@link Runnable#run}. Only when exiting from the
   * outermost {@link #runBatch} will the changes be propagated to the listeners.
   *
   * @param runnable the runnable to invoke
   */
  public static void runBatch(Runnable runnable) {
    getInstance().beginBatch();

    try {
      runnable.run();
    }
    finally {
      getInstance().endBatch();
    }
  }

  void beginBatch() {
    if (batchCounter++ == 0)
      changes = new HashMap();
  }

  private void addTreeChanges(PropertyMapImpl map, PropertyMapImpl modifiedMap, HashMap changes, HashMap treeChanges) {
    HashMap changeMap = (HashMap) treeChanges.get(map);

    if (changeMap == null) {
      changeMap = new HashMap();
      treeChanges.put(map, changeMap);
    }

    changeMap.put(modifiedMap, changes);

    if (map.getParent() != null)
      addTreeChanges(map.getParent(), modifiedMap, changes, treeChanges);
  }

  void endBatch() {
    if (--batchCounter == 0) {
      HashMap treeChanges = new HashMap();
      HashMap localChanges = changes;
      changes = null;

      for (Iterator iterator = localChanges.entrySet().iterator(); iterator.hasNext();) {
        Map.Entry entry = (Map.Entry) iterator.next();
        PropertyMapImpl object = (PropertyMapImpl) entry.getKey();
        HashMap objectChanges = (HashMap) entry.getValue();
        object.firePropertyValuesChanged(Collections.unmodifiableMap(objectChanges));
        addTreeChanges(object, object, objectChanges, treeChanges);
      }

      for (Iterator iterator = treeChanges.entrySet().iterator(); iterator.hasNext();) {
        Map.Entry entry = (Map.Entry) iterator.next();
        PropertyMapImpl object = (PropertyMapImpl) entry.getKey();
        HashMap objectChanges = (HashMap) entry.getValue();
        object.firePropertyTreeValuesChanged(Collections.unmodifiableMap(objectChanges));
      }
    }
  }
}
