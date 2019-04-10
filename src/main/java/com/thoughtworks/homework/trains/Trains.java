package com.thoughtworks.homework.trains;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Trains {


    private static String parseRouteLength(int distance){
        return distance == -1? "NO SUCH ROUTE" : Integer.toString(distance);
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println ("No arguments Supplied! Need a file path parameter.");
            System.exit(0);
        }
        String graphStr = new String(Files.readAllBytes(Paths.get(args[0])));
        Graph graph = new Graph(graphStr);
        System.out.println("Output #1: " + parseRouteLength(graph.routeLength("A-B-C")));
        System.out.println("Output #2: " + parseRouteLength(graph.routeLength("A-D")));
        System.out.println("Output #3: " + parseRouteLength(graph.routeLength("A-D-C")));
        System.out.println("Output #4: " + parseRouteLength(graph.routeLength("A-E-B-C-D")));
        System.out.println("Output #5: " + parseRouteLength(graph.routeLength("A-E-D")));

        System.out.println("Output #6: " + graph.routeCountStopsSize('C','C',3));
        System.out.println("Output #7: " + graph.routeEqualsStopsSize('A','C',4));
        System.out.println("Output #8: " + graph.routeShortest('A','C'));
        System.out.println("Output #9: " + graph.routeShortest('B','B'));
        System.out.println("Output #10: " + graph.routLessThenLength('C','C',30));
    }

}
