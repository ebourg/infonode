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


// $Id: GradientPainter.java,v 1.6 2004/09/28 15:07:29 jesper Exp $
package net.infonode.gui;

import net.infonode.gui.colorprovider.ColorProvider;
import net.infonode.gui.colorprovider.FixedColorProvider;
import net.infonode.util.ColorUtil;
import net.infonode.util.Direction;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * @author $Author: jesper $
 * @version $Revision: 1.6 $
 */
public class GradientPainter implements Serializable {
  private static final long serialVersionUID = 1;

  private ColorProvider[] colorProviders = new ColorProvider[4];
  private transient Color[] colors;
  private int size = 128;
  private transient Image[] images;

  public GradientPainter() {
    this(FixedColorProvider.BLACK, FixedColorProvider.BLACK, FixedColorProvider.BLACK, FixedColorProvider.BLACK);
  }

  public GradientPainter(Color topLeftColor, Color topRightColor, Color bottomLeftColor,
                         Color bottomRightColor) {
    this(new FixedColorProvider(topLeftColor), new FixedColorProvider(topRightColor), new FixedColorProvider(bottomLeftColor),
         new FixedColorProvider(bottomRightColor));
  }

  public GradientPainter(ColorProvider topLeftColor, ColorProvider topRightColor, ColorProvider bottomLeftColor,
                         ColorProvider bottomRightColor) {
    colorProviders[0] = topLeftColor;
    colorProviders[1] = topRightColor;
    colorProviders[2] = bottomLeftColor;
    colorProviders[3] = bottomRightColor;
  }

  public void paint(Component component, Graphics g, int x, int y, int width, int height) {
    paint(Direction.RIGHT, component, g, x, y, width, height);
  }

  public void paint(Direction direction, Component component, Graphics g, int x, int y, int width, int height) {
    if (images == null)
      images = new BufferedImage[4];

    if (colors == null)
      colors = new Color[4];

    for (int i = 0; i < colors.length; i++) {
      Color c = colorProviders[i].getColor(component);

      if (!c.equals(colors[i])) {
        for (int j = 0; j < 4; j++) {
          images[j] = null;
        }
      }

      colors[i] = c;
    }

    if (colors[0].equals(colors[2]) && colors[1].equals(colors[3])) {
      if (colors[0].equals(colors[1])) {
        g.setColor(colors[0]);
        g.fillRect(x, y, width, height);
        return;
      }
      else {
        drawLines(direction, g, x, y, width, height, colors[0], colors[1]);
      }
    }
    else if (colors[0].equals(colors[1]) && colors[2].equals(colors[3]))
      drawLines(direction.getNextCW(), g, x, y, width, height, colors[0], colors[2]);
    else {
      if (images[direction.getValue()] == null) {
        images[direction.getValue()] = createImage(direction);
      }

      g.drawImage(images[direction.getValue()], x, y, width, height, null);
    }

  }

  private Image createImage(Direction direction) {
    return createGradientImage(direction);
  }

/*  private Image createLineImage(Direction direction, Color c1, Color c2) {
    BufferedImage image = new BufferedImage(direction.isHorizontal() ? size : 1,
                                            direction.isHorizontal() ? 1 : size,
                                            BufferedImage.TYPE_INT_RGB);

    int dx = direction == Direction.RIGHT ? 1 :
             direction == Direction.LEFT ? -1 : 0;
    int dy = direction == Direction.DOWN ? 1 :
             direction == Direction.UP ? -1 : 0;

    int x = direction == Direction.LEFT ? size - 1 : 0;
    int y = direction == Direction.UP ? size - 1 : 0;

    for (int i = 0; i < size; i++) {
      Color c = ColorUtil.blend(c1, c2, (double) i / size);
      image.setRGB(x, y, c.getRGB());
      x += dx;
      y += dy;
    }

    return image;
  }
*/
  private static void drawLines(Direction direction, Graphics g, int x, int y, int width, int height, Color c1, Color c2) {
    int size = direction.isHorizontal() ? width : height;

    for (int i = 0; i < size; i++) {
      float f = (float) i / size;
      g.setColor(ColorUtil.blend(c1, c2, f));

      if (direction == Direction.RIGHT)
        g.drawLine(x + i, y, x + i, y + height - 1);
      else if (direction == Direction.DOWN)
        g.drawLine(x, y + i, x + width - 1, y + i);
      else if (direction == Direction.LEFT)
        g.drawLine(x + width - 1 - i, y, x + width - 1 - i, y + height - 1);
      else
        g.drawLine(x, y + height - 1 - i, x + width - 1, y + height - 1 - i);
    }
  }

  private Image createGradientImage(Direction direction) {
    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

    int dx = direction == Direction.RIGHT ? 1 :
             direction == Direction.LEFT ? -1 : 0;

    int dy = direction == Direction.DOWN ? 1 :
             direction == Direction.UP ? -1 : 0;

    for (int j = 0; j < size; j++) {
      int x = direction == Direction.RIGHT ? 0 :
              direction == Direction.LEFT ? size - 1 :
              direction == Direction.DOWN ? size - 1 - j :
              j;

      int y = direction == Direction.RIGHT ? j :
              direction == Direction.LEFT ? size - 1 - j :
              direction == Direction.DOWN ? 0 :
              size - 1;

      Color c1 = ColorUtil.blend(colors[0], colors[2], (double) j / size);
      Color c2 = ColorUtil.blend(colors[1], colors[3], (double) j / size);

      for (int i = 0; i < size; i++) {
        Color c = ColorUtil.blend(c1, c2, (double) i / size);
        image.setRGB(x, y, c.getRGB());
        x += dx;
        y += dy;
      }
    }

    return image;
  }

}
