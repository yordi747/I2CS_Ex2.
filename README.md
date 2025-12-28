# I2CS_Ex2

Third assignment in Java at Ariel University

Ex2 – 2D Maze Algorithms using BFS

Course: Introduction to Computer Science – Ariel University
Assignment: Ex2 – Basic Object-Oriented Programming

Overview

This project implements a 2D grid-based map (maze) and demonstrates several fundamental Breadth-First Search (BFS) algorithms using Object-Oriented Programming (OOP) principles.

The system operates on a discrete int[][] representation of a map and includes a graphical visualization based on the StdDraw library.

Implemented Algorithms
Area Filling (Flood Fill)

A BFS-based flood fill algorithm that fills a connected component starting from a given cell.
The algorithm replaces all reachable cells of the same value with a new value.

Distance Mapping

A BFS-based exploration that computes the minimum distance from a given start cell to all other reachable cells in the map, while avoiding obstacles.
Unreachable cells remain marked as such.

Shortest Path (Pathfinding)

A shortest path algorithm based on BFS.
The algorithm computes a distance map and then reconstructs the shortest path between two points by backtracking from the destination to the source.

Graphical User Interface (GUI)

A simple graphical user interface was implemented using StdDraw in order to visualize the map and the BFS-based algorithms on a grid of cells.

Each cell in the grid represents a single location in the map and is displayed using colors and numeric values.

GUI Controls

The GUI allows interactive activation of the implemented algorithms:

M – Generate a new random maze

F – Activate Flood Fill mode (click on a cell to fill a connected area)

D – Activate Distance Mapping mode (click on a cell to compute BFS distances)

P – Activate Shortest Path mode (click once for start point, click again for end point)

C – Clear algorithm overlays (distance/path visualization)

R – Reset filled cells while keeping obstacles

Mouse Click – Apply the currently selected algorithm on the chosen cell