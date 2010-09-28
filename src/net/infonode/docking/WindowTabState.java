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


// $Id: WindowTabState.java,v 1.3 2004/09/23 15:32:13 jesper Exp $
package net.infonode.docking;

import net.infonode.util.Enum;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.3 $
 */
final class WindowTabState extends Enum {
  private static final long serialVersionUID = 1L;

  static final WindowTabState NORMAL = new WindowTabState(0, "Normal");
  static final WindowTabState HIGHLIGHTED = new WindowTabState(1, "Highlighted");
  static final WindowTabState FOCUSED = new WindowTabState(2, "Focused");

  static final WindowTabState[] STATES = {NORMAL, HIGHLIGHTED, FOCUSED};

  WindowTabState(int value, String name) {
    super(value, name);
  }
}
