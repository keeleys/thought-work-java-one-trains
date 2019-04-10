package com.thoughtworks.homework.trains;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestTrains {
    private static Graph graph;
    @BeforeClass
    public static void testInit(){
        String graphStr = "AB5,BC4,CD8,DC8,DE6,AD5,CE2,EB3,AE7";
        graph = new Graph(graphStr);
    }
    @Test
    public void test1(){
        Assert.assertEquals(graph.routeLength("A-B-C"), 9);
    }
    @Test
    public void test2(){
        Assert.assertEquals(graph.routeLength("A-D"), 5);
    }
    @Test
    public void test3(){
        Assert.assertEquals(graph.routeLength("A-D-C"), 13);
    }
    @Test
    public void test4(){
        Assert.assertEquals(graph.routeLength("A-E-B-C-D"), 22);
    }
    @Test
    public void test5(){
        Assert.assertEquals(graph.routeLength("A-E-D"), -1);
    }
    @Test
    public void test6() {
        Assert.assertEquals(graph.routeCountStopsSize('C','C',3),2);
    }
    @Test
    public void test7() {
        Assert.assertEquals(graph.routeEqualsStopsSize('A','C',4), 3);
    }
    @Test
    public void test8() {
        Assert.assertEquals(graph.routeShortest('A','C'), 9);
    }
    @Test
    public void test9() {
        Assert.assertEquals(graph.routeShortest('B','B'), 9);
    }
    @Test
    public void test10() {
        Assert.assertEquals(graph.routLessThenLength('C','C',30),7);
    }
}
