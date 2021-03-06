package com.thoughtworks.homework.trains;

/**
 * 线的抽象类
 */
public class Edge {
    private char startPoint;
    private char endPoint;
    private int length;

    public char getStartPoint() {
        return startPoint;
    }

    public char getEndPoint() {
        return endPoint;
    }

    public int getLength() {
        return length;
    }

    public Edge(char startPoint, char endPoint, int length) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.length = length;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", length=" + length +
                '}';
    }
}
