package com.thoughtworks.homework.trains;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Graph {
    private final Map<Character, List<Edge>> lineMap;
    private static final String JOIN_CHAR = "-";

    /**
     * ([A-Z]{2}[0-9],?)+
     * 构造连接点和线图
     *
     * @param graph like "AB5,BC4,CD8,DC8,DE6,AD5,CE2,EB3,AE7"
     */
    public Graph(String graph) {
        final Pattern r = Pattern.compile("^[A-Z]{2}[0-9]$");
        this.lineMap = Pattern.compile(",")
                .splitAsStream(graph)
                .filter(x -> r.matcher(x).matches())
                .distinct()
                .map(x -> new Edge(x.charAt(0), x.charAt(1), Character.getNumericValue(x.charAt(2))))
                .collect(Collectors.groupingBy(Edge::getStartPoint));
    }

    /**
     * 计算路线的长度, 计算方法1-5 循环累加..
     * 1324 2033
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
     * 计算方法6
     * @param startPoint 开始点
     * @param endPoint   目的点
     * @param stops      最大停留数
     * @return
     */
    public int routeCountStopsSize(char startPoint, char endPoint, int stops) {
        List<String> result= this.maximumStops(startPoint, endPoint, stops,startPoint+"", new ArrayList<>());
        return result.size();
    }

    /**
     * 找到满足指定停留数的可到达的路径数量
     * 计算方法7
     * @param startPoint 开始点
     * @param endPoint   结束点
     * @param stops      指定的停留数
     * @return
     */
    public int routeEqualsStopsSize(char startPoint, char endPoint, int stops) {
        List<String> result = this.equalsStops(startPoint, endPoint, stops, startPoint+"",new ArrayList<>());
        return result.size();
    }

    /**
     * 找到最短路径的长度 (思路,最短路径不会超过所有节点,适用于节点少的时候..)
     * 计算方法 8-9
     * @param startPoint 开始点
     * @param endPoint   目的点
     * @return
     */
    public int routeShortest(char startPoint, char endPoint) {
        Map<Character,Integer> maps = new HashMap<>();
        lineMap.keySet().forEach(it->maps.put(it,Integer.MAX_VALUE));

        LinkedList<Character> link = new LinkedList();

        lineMap.get(startPoint).forEach(it->{
            maps.put(it.getEndPoint(),it.getLength());
            link.push(it.getEndPoint());
        });
        while (!link.isEmpty()){
            Character point = link.poll();
            lineMap.get(point).forEach(it->{
                final int length = maps.get(point) + it.getLength();
                if(maps.get(it.getEndPoint()) > length) {
                    maps.put(it.getEndPoint(), length);
                    link.push(it.getEndPoint());
                }
            });
        }
        return maps.get(endPoint);
    }

    /**
     * 找到小于指定线路长度的2点的线路数量
     * 计算方法 10
     * @param startPoint 开始点
     * @param endPoint   目的点
     * @param maxLength  最大的长度
     * @return
     */

    public int routLessThenLength(char startPoint, char endPoint, int maxLength) {
        List<String> result = this.lessThenLength(startPoint, endPoint, maxLength,startPoint+"",new ArrayList<>());
        return result.size();
    }


    /**
     * 得到满足最大停留的所有路径,用递归搜索的
     *
     * @param startPoint 开始节点
     * @param endPoint   目的节点
     * @param stops      最大停留数目
     * @return
     */
    private List<String> maximumStops(char startPoint, char endPoint, int stops, String path, List<String> result) {

        if (stops == 0) {
            return result;
        }
        for (Edge edge : lineMap.get(startPoint)) {
            final String nextPath =  path + JOIN_CHAR + edge.getEndPoint();
            if (edge.getEndPoint() == endPoint ) {
                result.add(nextPath);
            } else {
                this.maximumStops(edge.getEndPoint(), endPoint, stops - 1, nextPath, result);
            }
        }
        return result;
    }

    /**
     * 得到满足停留数一致的路径
     *
     * @param startPoint 开始节点
     * @param endPoint   目的节点
     * @param stops      停留数目
     * @return
     */
    private List<String> equalsStops(char startPoint, char endPoint, int stops, String path, List<String> result) {
        // 停留数达到并且最后一次刚好到目的地
        if (stops == 0) {
            return result;
        }
        for (Edge edge : lineMap.get(startPoint)) {
            final String nextPath = path +JOIN_CHAR + edge.getEndPoint();
            if(edge.getEndPoint() == endPoint && stops == 1) {
                result.add(nextPath);
            } else {
                this.equalsStops(edge.getEndPoint(), endPoint, stops -1, nextPath, result);
            }
        }
        return result;
    }

    /**
     * 找到小于指定线路长度的2点的所有路线
     *
     * @param startPoint  开始节点
     * @param endPoint    结束节点
     * @return
     */
    private List<String> lessThenLength(char startPoint, char endPoint, int left,String path, List<String> result) {
        if(left < 0) return result;

        for (Edge edge : lineMap.get(startPoint)) {
            final String nextPath = path + JOIN_CHAR + edge.getEndPoint();
            if(edge.getEndPoint() == endPoint && left > edge.getLength()) {
                result.add(nextPath);
            }
            this.lessThenLength(edge.getEndPoint(), endPoint, left - edge.getLength(), nextPath, result);
        }
        return result;
    }

    /**
     * 得到2个点的长度
     *
     * @param start 起点
     * @param end   终点
     * @return 如果有连接, 返回连接, 如果没连接返回-1
     */
    private int getLineLength(char start, char end) {
        return lineMap.values().stream().flatMap(Collection::stream)
                .filter(x -> x.getStartPoint() == start && x.getEndPoint() == end)
                .findAny().map(Edge::getLength).orElse(-1);
    }

}
