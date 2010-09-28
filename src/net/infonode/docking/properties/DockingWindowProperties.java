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


// $Id: DockingWindowProperties.java,v 1.12 2004/11/11 14:09:46 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.BooleanProperty;

/**
 * Properties and property values common for all docking windows.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.12 $
 */
public class DockingWindowProperties extends PropertyMapContainer {
  /**
   * Property group containing all docking window properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Docking Window Properties", "");

  /**
   * Property values for the window tab when the window is located in a TabWindow or a WindowBar.
   */
  public static final PropertyMapProperty TAB_PROPERTIES =
      new PropertyMapProperty(PROPERTIES,
                              "Tab Properties",
                              "Property values for the window tab when the window is located in a TabWindow or a WindowBar.",
                              WindowTabProperties.PROPERTIES);

  /**
   * Enables/disables window drag by the user.
   *
   * @since IDW 1.2.0
   */
  public static final BooleanProperty DRAG_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Drag Enabled",
                          "Enables/disables window drag by the user.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Enables/disables window minimize by the user.
   *
   * @since IDW 1.2.0
   */
  public static final BooleanProperty MINIMIZE_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Minimize Enabled",
                          "Enables/disables window minimize by the user.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Enables/disables window close by the user.
   *
   * @since IDW 1.2.0
   */
  public static final BooleanProperty CLOSE_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Close Enabled",
                          "Enables/disables window close by the user.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Enables/disables window restore by the user.
   *
   * @since IDW 1.2.0
   */
  public static final BooleanProperty RESTORE_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Restore Enabled",
                          "Enables/disables window restore by the user.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Enables/disables window maximize by the user.
   *
   * @since IDW 1.2.0
   */
  public static final BooleanProperty MAXIMIZE_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Maximize Enabled",
                          "Enables/disables window maximize by the user.",
                          PropertyMapValueHandler.INSTANCE);


  /**
   * Creates an empty property object.
   */
  public DockingWindowProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property map containing the map.
   *
   * @param map the property map
   */
  public DockingWindowProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public DockingWindowProperties(DockingWindowProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public DockingWindowProperties addSuperObject(DockingWindowProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Removes the last added super object.
   *
   * @return this
   * @since IDW 1.1.0
   */
  public DockingWindowProperties removeSuperObject() {
    getMap().removeSuperMap();
    return this;
  }

  /**
   * Returns the property values for the window tab when the window is located in a TabWindow or a WindowBar.
   *
   * @return the property values for the window tab when the window is located in a TabWindow or a WindowBar
   */
  public WindowTabProperties getTabProperties() {
    return new WindowTabProperties(TAB_PROPERTIES.get(getMap()));
  }

  /**
   * Returns true if the window drag by the user is enabled.
   *
   * @return true if the window drag is enabled
   * @since IDW 1.2.0
   */
  public boolean getDragEnabled() {
    return DRAG_ENABLED.get(getMap());
  }

  /**
   * Enables/disables window drag by the user.
   *
   * @param enabled if true, drag is enabled, otherwise it's disabled
   * @return this
   * @since IDW 1.2.0
   */
  public DockingWindowProperties setDragEnabled(boolean enabled) {
    DRAG_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Returns true if the window minimize by the user is enabled.
   *
   * @return true if the window minimize is enabled
   * @since IDW 1.2.0
   */
  public boolean getMinimizeEnabled() {
    return MINIMIZE_ENABLED.get(getMap());
  }

  /**
   * Enables/disables window minimize by the user.
   *
   * @param enabled if true, minimize is enabled, otherwise it's disabled
   * @return this
   * @since IDW 1.2.0
   */
  public DockingWindowProperties setMinimizeEnabled(boolean enabled) {
    MINIMIZE_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Returns true if the window maximize by the user is enabled.
   *
   * @return true if the window maximize is enabled
   * @since IDW 1.2.0
   */
  public boolean getMaximizeEnabled() {
    return MAXIMIZE_ENABLED.get(getMap());
  }

  /**
   * Enables/disables window maximize by the user.
   *
   * @param enabled if true, maximize is enabled, otherwise it's disabled
   * @return this
   * @since IDW 1.2.0
   */
  public DockingWindowProperties setMaximizeEnabled(boolean enabled) {
    MAXIMIZE_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Returns true if the window close by the user is enabled.
   *
   * @return true if the window close is enabled
   * @since IDW 1.2.0
   */
  public boolean getCloseEnabled() {
    return CLOSE_ENABLED.get(getMap());
  }

  /**
   * Enables/disables window close by the user.
   *
   * @param enabled if true, close is enabled, otherwise it's disabled
   * @return this
   * @since IDW 1.2.0
   */
  public DockingWindowProperties setCloseEnabled(boolean enabled) {
    CLOSE_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Returns true if the window restore by the user is enabled.
   *
   * @return true if the window restore is enabled
   * @since IDW 1.2.0
   */
  public boolean getRestoreEnabled() {
    return RESTORE_ENABLED.get(getMap());
  }

  /**
   * Enables/disables window restore by the user.
   *
   * @param enabled if true, restore is enabled, otherwise it's disabled
   * @return this
   * @since IDW 1.2.0
   */
  public DockingWindowProperties setRestoreEnabled(boolean enabled) {
    RESTORE_ENABLED.set(getMap(), enabled);
    return this;
  }

}
