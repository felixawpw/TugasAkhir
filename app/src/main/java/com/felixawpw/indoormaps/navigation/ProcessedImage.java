package com.felixawpw.indoormaps.navigation;

import android.graphics.Color;

public final class ProcessedImage {
    //Original Image Color Rules
    public static final int ACCESSABLE_COLOR = Color.WHITE;

    //Processed Image 2d Array Rules
    public static final int MAP_ACCESSABLE = 1;
    public static final int MAP_OBSTACLE = 2;

    //Processing Rules SI (CGS, )
    public static final float MIN_OBSTACLE_LENGTH = 0.1f;

    ImageCustom image;
    int[][] map; //map = [width,height]
    int[][] normalizedMap;

    public int[][] getNormalizedMap() {
        return this.normalizedMap;
    }

    public ProcessedImage() {

    }

    public ProcessedImage(ImageCustom image) throws Exception {
        this.image = image;
        map = new int[image.getWidth()][image.getHeight()];
        processImage();
    }

    public void processImage() {
        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++) {
                if (isMatch(new Color(floorPlan.getRGB(i, j)), ACCESSABLE_COLOR)) {
                    map[i][j] = MAP_ACCESSABLE;
                }
                else {
                    map[i][j] = MAP_OBSTACLE;
                }
            }

        normalize();
    }

    public void normalize()
    {
        normalizedMap = new int[image.getWidth()][image.getHeight()];

        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                normalizedMap[i][j] = map[i][j];

        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++)
                if (!isObstacle(normalizedMap, i, j))
                    normalizedMap[i][j] = MAP_ACCESSABLE;
    }

    public boolean isObstacle(int[][] arr, int x, int y)
    {
        final int treshold = (int)Math.ceil(MIN_OBSTACLE_LENGTH / image.getHeightCM() * image.getHeight());
        int minX = (x - treshold) >= 0 ? x - treshold : 0;
        int minY = (y - treshold) >= 0 ? y - treshold : 0;
        int maxX = (x + treshold) < image.getWidth() ? x + treshold : image.getWidth() - 1;
        int maxY = (y + treshold) < image.getHeight()? y + treshold : image.getHeight() - 1;
        int area = 0;
        for (int i = minX; i <= maxX; i++)
            for (int j = minY; j <= maxY; j++)
                if (arr[i][j] == MAP_OBSTACLE)
                    area += 1;

        return area >= treshold * treshold;
    }

    public Map<Integer, Integer> populateAreaMap(int[][] arr) {
        Map<Integer, Integer> areaMap = new HashMap<>();

        for (int i = 0; i < image.getWidth(); i++)
            for (int j = 0; j < image.getHeight(); j++)
            {
                int area = countArea(arr, i, j);
                if (areaMap.containsKey(area))
                    areaMap.put(area, areaMap.get(area) + 1);
                else
                    areaMap.put(area, 1);
            }
        return areaMap;
    }

    public int countArea(int[][] arr, int x, int y) {
        final int treshold = (int)Math.ceil(MIN_OBSTACLE_LENGTH / image.getHeightCM() * image.getHeight());

        int minX = (x - treshold) >= 0 ? x - treshold : 0;
        int minY = (y - treshold) >= 0 ? y - treshold : 0;
        int maxX = (x + treshold) < image.getWidth() ? x + treshold : image.getWidth() - 1;
        int maxY = (y + treshold) < image.getHeight()? y + treshold : image.getHeight() - 1;

        int area = 0;
        for (int i = minX; i <= maxX; i++)
            for (int j = minY; j <= maxY; j++)
                if (arr[i][j] == MAP_OBSTACLE)
                    area += 1;
        return area;
    }

    boolean isMatch(Color pixel, Color toMatch) {
        final int delta = 25;

        return (Math.abs(pixel.getRed() - toMatch.getRed()) <= delta &&
                Math.abs(pixel.getBlue() - toMatch.getBlue()) <= delta &&
                Math.abs(pixel.getGreen() - toMatch.getGreen()) <= delta);
    }

    public void printMapToImageFile() throws IOException {
        BufferedImage createdMapImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (map[j][i] == MAP_ACCESSABLE)
                    createdMapImage.setRGB(j, i, Color.WHITE.getRGB());
                else
                    createdMapImage.setRGB(j, i, Color.BLACK.getRGB());
            }
        }

        ImageIO.write(createdMapImage, "jpg", new File("created_map.jpg"));
    }

    public void printNormalizedMapToFile() throws IOException {
        BufferedImage createdMapImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (normalizedMap[j][i] == MAP_ACCESSABLE)
                    createdMapImage.setRGB(j, i, Color.WHITE.getRGB());
                else
                    createdMapImage.setRGB(j, i, Color.BLACK.getRGB());
            }
        }

        ImageIO.write(createdMapImage, "jpg", new File("normalized_map.jpg"));
    }
}