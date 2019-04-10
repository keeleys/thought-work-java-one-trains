package com.thoughtworks.homework.trains;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Graph {
    private final Map<Character, List<Line>> lineMap;
    private static final String JOIN_CHAR = "-";

    /**
     * ([A-Z]{2}[0-9],?)+
     * 构造连接点和线图
     * @param graph  like "AB5,BC4,CD8,DC8,DE6,AD5,CE2,EB3,AE7"
     */
    public Graph(String graph) {
        final Pattern r = Pattern.compile("^[A-Z]{2}[0-9]$");
        this.lineMap = Pattern.compile(",")
                .splitAsStream(graph)
                .filter(x -> r.matcher(x).matches())
                .distinct()
                .map(x -> new Line(x.charAt(0), x.charAt(1), Character.getNumericValue(x.charAt(2))))
                .collect(Collectors.groupingBy(Line::getStartPoint));
    }

    /**
     * 计算路线的长度, 循环累加..
     * @param route 路线 例如 A-D
     * @return
     */
    public int routeLength(String route) {
        String[] points = route.split(JOIN_CHAR);
        // A-B-C
        int result = 0;
        for (int i = 0; i < points.length - 1; i++) {
            char start = points[i].charAt(0);
            char end = points[i + 1].charAt(0);
            int length = this.getLineLength(start, end);
            if (length == -1) return -1;
            result += length;
        }
        return result;
    }

    /**
     * 找到满足最大停留数的可到达的路径数量
     * @param startPoint 开始点
     * @param endPoint 目的点
     * @param stops 最大停留数
     * @return
     */
    public int routeCountStopsSize(char startPoint, char endPoint, int stops) {
        int extra = startPoint == endPoint ? -1 : 0;
        return this.maximumStops(startPoint, endPoint, stops).size() + extra;
    }

    /**
     * 找到满足指定停留数的可到达的路径数量
     * @param startPoint 开始点
     * @param endPoint 结束点
     * @param stops 指定的停留数
     * @return
     */
    public int routeEqualsStopsSize(char startPoint, char endPoint, int stops) {
        return this.equalsStops(startPoint, endPoint, stops).size();
    }

    /**
     * 找到最短路径的长度 (思路,最短路径不会超过所有节点,适用于节点少的时候..)
     * @param startPoint 开始点
     * @param endPoint 目的点
     * @return
     */
    public int routeShortest(char startPoint,char endPoint) {
        List<String> list = this.maximumStops(startPoint, endPoint, lineMap.size());
        int shortLength = Integer.MAX_VALUE;
        for(String s : list) {
            if(s.length()==1) continue;
            int result = this.routeLength(s);
            if(shortLength > result) {
                shortLength = result;
            }
        }
        return shortLength;
    }

    /**
     * 找到小于指定线路长度的2点的线路数量
     * @param startPoint 开始点
     * @param endPoint 目的点
     * @param maxLength 最大的长度
     * @return
     */

    public int routLessThenLength(char startPoint,char endPoint,int maxLength){
        List<String> result = new ArrayList<>();
        for (Line line : lineMap.get(startPoint)) {
            String currentPath = startPoint + JOIN_CHAR + line.getEndPoint();
            List<String> validRoutes = lessThenLength(line.getEndPoint(), endPoint, currentPath, maxLength);
            for (String route : validRoutes) {
                result.add(startPoint + "-" + route);
            }
        }
        return result.size();
    }


    /**
     * 得到满足最大停留的路径,用递归搜索的
     * @param startPoint 开始节点
     * @param endPoint 目的节点
     * @param stops 最大停留数目
     * @return
     */
    private List<String> maximumStops(char startPoint, char endPoint, int stops) {
        // 先找出下一个点
        List<String> routesAvailable = new ArrayList<>();

        if (startPoint == endPoint && stops >= 0) {
            routesAvailable.add(endPoint + "");
        }
        if (stops == 0) {
            return routesAvailable;
        }

        final int stopsLeft = stops - 1;
        if (stops >= 0) {
            for (Line line : lineMap.get(startPoint)) {
                List<String> validRoutes = maximumStops(line.getEndPoint(), endPoint, stopsLeft);
                for (String route : validRoutes) {
                    routesAvailable.add(startPoint + JOIN_CHAR + route);
                }
            }
        }
        return routesAvailable;
    }

    /**
     * 得到满足停留数一致的路径
     * @param startPoint 开始节点
     * @param endPoint 目的节点
     * @param stops 停留数目
     * @return
     */
    private List<String> equalsStops(char startPoint, char endPoint, int stops) {
        // 先找出下一个点
        List<String> routesAvailable = new ArrayList<>();
        if (startPoint == endPoint && stops == 0) {
            routesAvailable.add(endPoint + "");
            return routesAvailable;
        }
        final int stopsLeft = stops - 1;
        if (stopsLeft >= 0) {
            for (Line line : lineMap.get(startPoint)) {
                List<String> validRoutes = equalsStops(line.getEndPoint(), endPoint, stopsLeft);
                for (String route : validRoutes) {
                    routesAvailable.add(startPoint + JOIN_CHAR + route);
                }
            }
        }
        return routesAvailable;
    }

    /**
     * 找到小于指定线路长度的2点的所有路线
     * @param startPoint 开始节点
     * @param endPoint 结束节点
     * @param currentPath 搜索的路线 like 'A-B-C'
     * @param maxLength 最大线路长度
     * @return
     */
    private List<String> lessThenLength(char startPoint, char endPoint, String currentPath, int maxLength) {
        List<String> routesAvailable = new ArrayList<>();
        int currentLength = this.routeLength(currentPath);
        // less then maxLength not less equals, so >= maxLength
        if(currentLength >= maxLength){
            return routesAvailable;
        }
        if (startPoint == endPoint) {
            routesAvailable.add(endPoint + "");
        }
        for (Line line : lineMap.get(startPoint)) {
            final String nextPath = currentPath + JOIN_CHAR + line.getEndPoint();
            List<String> validRoutes = this.lessThenLength(line.getEndPoint(), endPoint, nextPath, maxLength);
            for (String route : validRoutes) {
                routesAvailable.add(startPoint +JOIN_CHAR + route);
            }
        }
        return routesAvailable;
    }

    /**
     * 得到2个点的长度
     * @param start 起点
     * @param end 终点
     * @return 如果有连接,返回连接,如果没连接返回-1
     */
    private int getLineLength(char start, char end) {
        return lineMap.values().stream().flatMap(Collection::stream)
                .filter(x -> x.getStartPoint() == start && x.getEndPoint() == end)
                .findAny().map(Line::getLength).orElse(-1);
    }

}
