package com.indi.llk.Game_Core_Controller;

import java.awt.Point; // 使用 java.awt.Point 来表示坐标
import java.util.ArrayList;
import java.util.List;

/**
 * 表示两个图块之间的连接路径。
 */
public class connectionPath {

    private List<Point> points; // 存储路径上的点 (行, 列)

    /**
     * 构造一个新的、空的连接路径。
     */
    public connectionPath() {
        this.points = new ArrayList<>();
    }

    /**
     * 向路径中添加一个点。
     *
     * @param row 点的行坐标。
     * @param col 点的列坐标。
     */
    public void addPoint(int row, int col) {
        // 确保不添加重复的连续点 (可选优化)
        if (points.isEmpty() || !points.get(points.size() - 1).equals(new Point(row, col))) {
             points.add(new Point(row, col));
        }
    }

    /**
     * 向路径中添加一个现有的 Point 对象。
     * @param point 要添加的点。
     */
     public void addPoint(Point point) {
        if (point != null) {
             // 确保不添加重复的连续点 (可选优化)
             if (points.isEmpty() || !points.get(points.size() - 1).equals(point)) {
                 points.add(point);
             }
        }
     }


    /**
     * 获取构成路径的点列表。
     * 列表中的点按顺序排列，从起始图块中心到结束图块中心。
     *
     * @return 包含路径上所有点的列表。
     */
    public List<Point> getPoints() {
        return points;
    }

    /**
     * 检查路径是否为空（即不包含任何点）。
     *
     * @return 如果路径为空则返回 true，否则返回 false。
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /**
     * 获取路径中的点的数量。
     *
     * @return 路径中的点的数量。
     */
     public int size() {
        return points.size();
     }

    /**
     * 清除路径中的所有点。
     */
     public void clear() {
        points.clear();
     }
}