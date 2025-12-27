# I2CS_Ex2

Third assignment in Java at Ariel University

# Ex2 – OOP & 2D Maps

---

## What is this project?
This project is about working with a 2D map (`int[][]`) using Object-Oriented Programming.

The map can represent an image or a maze.
On this map we implemented several algorithms based on **BFS (Breadth-First Search)**.

---

## Main Parts

### Map
The main class of the project.  
Implements the given `Map2D` interface (the interface was not changed).

What it does:
- Stores a 2D map
- Get and set pixel values
- Draw shapes on the map:
    - Line
    - Circle
    - Rectangle
- BFS algorithms:
    - `fill`
    - `allDistance`
    - `shortestPath`
- Supports cyclic and non-cyclic maps

---

### Index2D
Implements the given `Pixel2D` interface.

Represents a single pixel with `(x, y)` coordinates.

---

### Ex2_GUI
Simple GUI using **StdDraw**.

- Displays the map on the screen
- Each cell is shown as a colored square
- Includes save and load functions
- The `main` method is only for demo and testing

All logic is in the `Map` class, not in the GUI.

---

### MapTest
JUnit tests for the `Map` class.

Tests include:
- Initialization
- Pixel get/set
- Fill
- Distance map
- Shortest path
- Large map test

