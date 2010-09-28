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


// $Id: PropertyMapWeakListenerManager.java,v 1.6 2005/02/16 11:28:15 jesper Exp $
package net.infonode.properties.propertymap;

import net.infonode.properties.base.Property;
import net.infonode.properties.util.PropertyChangeListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Handles weak {@link PropertyMap} listeners which are garbage collected and removed from the {@link PropertyMap}
 * object on which it listens when there are no strong or soft references to the listeners.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 * @since IDW 1.2.0
 */
public class PropertyMapWeakListenerManager {
  private PropertyMapWeakListenerManager() {
  }

  private static class ListenerRef extends WeakReference {
    private PropertyMap map;

    protected ListenerRef(Object referent, ReferenceQueue q, PropertyMap map) {
      super(referent, q);
      this.map = map;
    }

    public PropertyMap getMap() {
      return map;
    }

    public void removeFromMap() {
      map = null;
    }

  }

  private static class MapListenerRef extends ListenerRef implements PropertyMapListener {
    MapListenerRef(PropertyMapListener referent, ReferenceQueue q, PropertyMap map) {
      super(referent, q, map);
      map.addListener(this);
    }

    public void removeFromMap() {
      getMap().removeListener(this);
      super.removeFromMap();
    }

    public void propertyValuesChanged(PropertyMap propertyMap, Map changes) {
      PropertyMapListener l = (PropertyMapListener) get();

      if (l != null)
        l.propertyValuesChanged(propertyMap, changes);
    }
  }

  private static class PropertyChangeListenerRef extends ListenerRef implements PropertyChangeListener {
    private Property property;

    PropertyChangeListenerRef(PropertyChangeListener referent, ReferenceQueue q, PropertyMap map, Property property) {
      super(referent, q, map);
      this.property = property;
      map.addPropertyChangeListener(property, this);
    }

    public Property getProperty() {
      return property;
    }

    public void removeFromMap() {
      getMap().removePropertyChangeListener(property, this);
      super.removeFromMap();
    }

    public void propertyChanged(Property property, Object valueContainer, Object oldValue, Object newValue) {
      PropertyChangeListener l = (PropertyChangeListener) get();

      if (l != null)
        l.propertyChanged(property, valueContainer, oldValue, newValue);
    }
  }

  private static class TreeListenerRef extends ListenerRef implements PropertyMapTreeListener {
    TreeListenerRef(PropertyMapTreeListener referent, ReferenceQueue q, PropertyMap map) {
      super(referent, q, map);
      map.addTreeListener(this);
    }

    public void removeFromMap() {
      getMap().removeTreeListener(this);
      super.removeFromMap();
    }

    public void propertyValuesChanged(Map changes) {
      PropertyMapTreeListener l = (PropertyMapTreeListener) get();

      if (l != null)
        l.propertyValuesChanged(changes);
    }
  }

  private static WeakHashMap listenerMap = new WeakHashMap();
  private static WeakHashMap propertyChangeListenerMap = new WeakHashMap();
  private static WeakHashMap treeListenerMap = new WeakHashMap();
  private static ReferenceQueue refQueue = new ReferenceQueue();

  static {
    Timer timer = new Timer(200, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ListenerRef ref;

        while ((ref = (ListenerRef) refQueue.poll()) != null) {
          ref.removeFromMap();
        }
      }
    });
    timer.start();
  }

  private static void addToMap(WeakHashMap map, Object key, Object value) {
    ArrayList l = (ArrayList) map.get(key);

    if (l == null) {
      l = new ArrayList(2);
      map.put(key, l);
    }

    l.add(value);
  }

  private static void removeFromMap(WeakHashMap map, Object key, PropertyMap propertyMap) {
    ArrayList l = (ArrayList) map.get(key);

    if (l != null) {
      for (int i = 0; i < l.size(); i++) {
        ListenerRef ref = (ListenerRef) l.get(i);

        if (ref.getMap() == propertyMap) {
          ref.removeFromMap();
          l.remove(i);

          if (l.size() == 0) {
            map.remove(key);
          }

          return;
        }
      }
    }
  }

  private static void removeFromMap(WeakHashMap map, Object key, PropertyMap propertyMap, Property property) {
    ArrayList l = (ArrayList) map.get(key);

    if (l != null) {
      for (int i = 0; i < l.size(); i++) {
        PropertyChangeListenerRef ref = (PropertyChangeListenerRef) l.get(i);

        if (ref.getMap() == propertyMap && ref.getProperty() == property) {
          ref.removeFromMap();
          l.remove(i);

          if (l.size() == 0) {
            map.remove(key);
          }

          return;
        }
      }
    }
  }

  /**
   * Adds a weak listener to a {@link PropertyMap}.
   *
   * @param map      the {@link PropertyMap}
   * @param listener the listener
   */
  public static void addWeakListener(PropertyMap map, PropertyMapListener listener) {
    MapListenerRef l = new MapListenerRef(listener, refQueue, map);
    addToMap(listenerMap, listener, l);
  }

  /**
   * Adds a weak property change listener to a {@link PropertyMap}.
   *
   * @param map      the {@link PropertyMap}
   * @param property the property to listen to changes on
   * @param listener the listener
   */
  public static void addWeakPropertyChangeListener(PropertyMap map,
                                                   Property property,
                                                   PropertyChangeListener listener) {
    PropertyChangeListenerRef l = new PropertyChangeListenerRef(listener, refQueue, map, property);
    addToMap(propertyChangeListenerMap, listener, l);
  }

  /**
   * Adds a weak tree listener to a {@link PropertyMap}.
   *
   * @param map      the {@link PropertyMap}
   * @param listener the listener
   */
  public static void addWeakTreeListener(PropertyMap map, PropertyMapTreeListener listener) {
    TreeListenerRef l = new TreeListenerRef(listener, refQueue, map);
    addToMap(treeListenerMap, listener, l);
  }

  /**
   * Removes a listener previously added with {@link #addWeakListener(PropertyMap, PropertyMapListener)}.
   *
   * @param map      the map on which the listener was added
   * @param listener the listener
   */
  public static void removeWeakListener(PropertyMap map, PropertyMapListener listener) {
    removeFromMap(listenerMap, listener, map);
  }

  /**
   * Removes a listener previously added with
   * {@link #addWeakPropertyChangeListener(PropertyMap, net.infonode.properties.base.Property, net.infonode.properties.util.PropertyChangeListener)}.
   *
   * @param map      the map on which the listener was added
   * @param property the property on which the listener listens to changes
   * @param listener the listener
   */
  public static void removeWeakPropertyChangeListener(PropertyMap map,
                                                      Property property,
                                                      PropertyChangeListener listener) {
    removeFromMap(propertyChangeListenerMap, listener, map, property);
  }

  /**
   * Removes a listener previously added with {@link #addWeakTreeListener(PropertyMap, PropertyMapTreeListener)}.
   *
   * @param map      the map on which the listener was added
   * @param listener the listener
   */
  public static void removeWeakTreeListener(PropertyMap map, PropertyMapTreeListener listener) {
    removeFromMap(treeListenerMap, listener, map);
  }

}
