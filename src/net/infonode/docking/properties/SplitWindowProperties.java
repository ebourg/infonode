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


// $Id: SplitWindowProperties.java,v 1.15 2004/11/11 14:09:46 jesper Exp $
package net.infonode.docking.properties;

import net.infonode.properties.propertymap.*;
import net.infonode.properties.types.BooleanProperty;
import net.infonode.properties.types.IntegerProperty;

/**
 * Properties and property values for split windows.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.15 $
 */
public class SplitWindowProperties extends PropertyMapContainer {
  /**
   * Property group containing all split window properties.
   */
  public static final PropertyMapGroup PROPERTIES = new PropertyMapGroup("Split Window Properties", "");

  /**
   * When enabled causes the windows to change size continuously while dragging the split window divider.
   *
   * @since IDW 1.1.0
   */
  public static final BooleanProperty CONTINUOUS_LAYOUT_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Continuous Layout Enabled",
                          "When enabled causes the windows to change size continuously while dragging the split window divider.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * The split pane divider size.
   */
  public static final IntegerProperty DIVIDER_SIZE =
      IntegerProperty.createPositive(PROPERTIES,
                                     "Divider Size",
                                     "The split pane divider size.",
                                     2,
                                     PropertyMapValueHandler.INSTANCE);

  /**
   * When enabled the user can drag the SplitWindow divider to a new location.
   *
   * @since IDW 1.2.0
   */
  public static final BooleanProperty DIVIDER_LOCATION_DRAG_ENABLED =
      new BooleanProperty(PROPERTIES,
                          "Divider Location Drag Enabled",
                          "When enabled the user can drag the SplitWindow divider to a new location.",
                          PropertyMapValueHandler.INSTANCE);

  /**
   * Creates an empty property object.
   */
  public SplitWindowProperties() {
    super(PropertyMapFactory.create(PROPERTIES));
  }

  /**
   * Creates a property map containing the map.
   *
   * @param map the property map
   */
  public SplitWindowProperties(PropertyMap map) {
    super(map);
  }

  /**
   * Creates a property object that inherit values from another property object.
   *
   * @param inheritFrom the object from which to inherit property values
   */
  public SplitWindowProperties(SplitWindowProperties inheritFrom) {
    super(PropertyMapFactory.create(inheritFrom.getMap()));
  }

  /**
   * Adds a super object from which property values are inherited.
   *
   * @param properties the object from which to inherit property values
   * @return this
   */
  public SplitWindowProperties addSuperObject(SplitWindowProperties properties) {
    getMap().addSuperMap(properties.getMap());
    return this;
  }

  /**
   * Removes the last added super object.
   *
   * @return this
   * @since IDW 1.1.0
   */
  public SplitWindowProperties removeSuperObject() {
    getMap().removeSuperMap();
    return this;
  }

  /**
   * Sets the split pane divider size.
   *
   * @param size the split pane divider size
   * @return this
   */
  public SplitWindowProperties setDividerSize(int size) {
    DIVIDER_SIZE.set(getMap(), size);
    return this;
  }

  /**
   * Returns the split pane divider size.
   *
   * @return the split pane divider size
   */
  public int getDividerSize() {
    return DIVIDER_SIZE.get(getMap());
  }

  /**
   * Returns true if continuous layout is enabled.
   *
   * @return true if continuous layout is enabled
   * @since IDW 1.1.0
   */
  public boolean getContinuousLayoutEnabled() {
    return CONTINUOUS_LAYOUT_ENABLED.get(getMap());
  }

  /**
   * Enables/disables continuous layout.
   *
   * @param enabled if true continuous layout is enabled
   * @return this
   * @since IDW 1.1.0
   */
  public SplitWindowProperties setContinuousLayoutEnabled(boolean enabled) {
    CONTINUOUS_LAYOUT_ENABLED.set(getMap(), enabled);
    return this;
  }

  /**
   * Returns true if the user can drag the SplitWindow divider to a new location.
   *
   * @return true if the user can drag the SplitWindow divider to a new location
   * @since IDW 1.2.0
   */
  public boolean getDividerLocationDragEnabled() {
    return DIVIDER_LOCATION_DRAG_ENABLED.get(getMap());
  }

  /**
   * When enabled the user can drag the SplitWindow divider to a new location.
   *
   * @param enabled if true the user can drag the SplitWindow divider to a new location
   * @return this
   * @since IDW 1.2.0
   */
  public SplitWindowProperties setDividerLocationDragEnabled(boolean enabled) {
    DIVIDER_LOCATION_DRAG_ENABLED.set(getMap(), enabled);
    return this;
  }

}
