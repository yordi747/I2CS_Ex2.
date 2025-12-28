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

---

## How to Run

- Run `Ex2_GUI.main()` to start the GUI.
- Run `Index2DTest` and `MapTest` to execute JUnit tests.

---

## Notes
- Grid is stored as `_map[x][y]`
- Neighbors are **4-connected** (up, down, left, right)
- BFS guarantees shortest paths in the grid