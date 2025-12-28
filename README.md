# I2CS_Ex2

Third assignment in Java at Ariel University


The project implements a 2D integer grid and uses **BFS (Breadth-First Search)** to support:
- Flood Fill
- Distance Map
- Shortest Path

Includes a simple GUI built with **StdDraw**.

---

## Files Overview

- **Pixel2D.java**  
  Interface for a 2D grid coordinate (x,y).

- **Index2D.java**  
  Implementation of `Pixel2D`.  
  Supports distance calculation, equality, and string representation.

- **Index2DTest.java**  
  JUnit tests for `Index2D`.

- **Map.java**  
  Main implementation of `Map2D` using `int[][] _map[x][y]`.  
  Uses BFS for:
    - `fill` – flood fill of a connected component
    - `allDistance` – minimum distance from a start cell
    - `shortestPath` – path reconstruction using the distance map

- **MapTest.java**  
  JUnit tests for map initialization, fill, distances, and shortest path.

- **Ex2_GUI.java**  
  Interactive GUI to visualize:
    - Flood fill
    - Distance map
    - Shortest path

- **StdDraw.java**  
  Drawing and input utility library used by the GUI.

---

## GUI Controls

**Keyboard**
- `M` – new random maze
- `F` – fill mode
- `D` – distance mode
- `P` – path mode
- `C` – clear overlays
- `R` – reset filled cells

**Mouse**
- Click on grid to apply the selected mode.


## Screenshots

### Flood Fill (BFS)
Flood fill of a connected component.
All reachable FREE cells (0) from the selected cell are filled with value 2 (red).

![Flood Fill](screenshots/fill.png)

### Distance Map (BFS)
Each cell shows the minimum number of steps from the selected start cell.
Walls are blocked and unreachable cells remain unvisited.

![Distance Map](screenshots/distance.png)

### Shortest Path (BFS)
The shortest path between a start cell (green) and an end cell (orange),
computed using BFS and displayed in blue.

![Shortest Path](screenshots/path.png)



