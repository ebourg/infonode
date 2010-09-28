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


// $Id: WriteContext.java,v 1.4 2004/06/24 13:24:08 jesper Exp $
package net.infonode.docking;

/**
 * Contains information used when writing a docking window state.
 *
 * @author $Author: jesper $
 * @version $Revision: 1.4 $
 */
class WriteContext {
  private boolean writePropertiesEnabled;

  /**
   * Constructor.
   *
   * @param writeProperties true if property values should be written
   */
  WriteContext(boolean writeProperties) {
    this.writePropertiesEnabled = writeProperties;
  }

  /**
   * Returns true if property values should be written.
   * @return true if property values should be written
   */
  boolean getWritePropertiesEnabled() {
    return writePropertiesEnabled;
  }
}
