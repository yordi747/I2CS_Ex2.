# I2CS_Ex2-

Third assignment in Java at Ariel University

# Ex2 – OOP & 2D Maze Algorithms

## Course
Introduction to Computer Science  
Ariel University – School of Computer Science (2026)

---

## About the Assignment
This assignment focuses on Object-Oriented Programming (OOP) and algorithms
on 2D arrays that represent a maze or an image.

The main algorithms are based on **Breadth-First Search (BFS)**:
- Fill (flood fill)
- All distances from a source
- Shortest path

The main logic is implemented in the `Map` class, according to the given
`Map2D` interface.

---

## Project Structure

### Map
Implements the `Map2D` interface (given and not modified).

Main responsibilities:
- Manage a 2D integer map (`int[][]`)
- Set and get pixel values
- Draw shapes on the map:
    - Line
    - Circle
    - Rectangle
- BFS-based algorithms:
    - `fill`
    - `allDistance`
    - `shortestPath`
- Supports cyclic and non-cyclic maps

---

### Index2D
Implements the `Pixel2D` interface (interface not modified).

Represents a single pixel with `(x, y)` coordinates.

---

### Ex2_GUI
Simple graphical interface implemented using **StdDraw**.

- Displays the map on the screen
- Each cell is drawn as a colored square
- Includes save and load functions
- The `main` method is used only as a demo for visualization

The GUI does not contain logic – all algorithms are implemented in `Map`.

---

### MapTest
JUnit test class for the `Map` class.

Tests include:
- Map initialization
- Pixel access and update
- Flood fill
- Distance calculation
- Shortest path
- Performance on large maps

