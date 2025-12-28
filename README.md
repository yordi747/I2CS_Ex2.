# I2CS_Ex2

Third assignment in Java at Ariel University:


The project implements Basic Object-Oriented Programming & 2D integer grid and uses **BFS (Breadth-First Search)** to support:
- Flood Fill
- Distance Map
- Shortest Path

Includes a simple GUI built with **StdDraw**.

---

## Project Structure

- **Pixel2D.java**  
  Interface for a 2D grid coordinate (x,y).
  
     (*Provided by the assignment – not modified.*)

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

 - **Map2D.java**
   
    Interface defining the required operations for a 2D map.
    
     (*Provided by the assignment – not modified.*)

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

## How to Run 

- Open the project in IntelliJ IDEA.
- Make sure all files are under the same package.
- Locate the file `Ex2_GUI.java`.
- Right-click the file and select **Run 'Ex2_GUI'**.
- The GUI window will open and display the map.

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

## JUnit Tests


The project includes **JUnit tests** to ensure correctness and reliability.

- **Index2DTest**
  - Tests constructors
  - Tests `getX`, `getY`
  - Tests `distance2D`
  - Tests `equals` and `toString`

- **MapTest**
  - Tests map initialization
  - Tests flood fill (BFS)
  - Tests distance map generation
  - Tests shortest path reconstruction


---

## Visualization 

### Flood Fill (BFS)
Flood fill of a connected component.
All reachable FREE cells (0) from the selected cell are filled with value 2 (red).

<img width="815" height="698" alt="Screenshot 2025-12-28 183959" src="https://github.com/user-attachments/assets/47dc4e44-1824-4e82-9257-9603b067653d" />

### Distance Map (BFS)
Each cell shows the minimum number of steps from the selected start cell.
Walls are blocked and unreachable cells remain unvisited.

<img width="816" height="697" alt="Screenshot 2025-12-28 182407" src="https://github.com/user-attachments/assets/f1043af8-7387-417b-b1fe-0791d3dea403" />


### Shortest Path (BFS)
The shortest path between a start cell (green) and an end cell (orange),
computed using BFS and displayed in blue.

<img width="816" height="698" alt="Screenshot 2025-12-28 182529" src="https://github.com/user-attachments/assets/4158e824-a1af-484a-af3e-468d5906ade5" />

---

## Author

 Yordanos Semie

