# UC Berkeley CS 61B: Data Structures and Algorithms (Java)

My self-paced work through UC Berkeley's CS 61B using the course's public materials (Spring 2021), completed on my own, not for credit, while building up my software engineering skills for a career change. The course covers data structures, algorithms, testing, and software design in Java. Starter code and skeletons come from the course staff; the implementations are my own.

## Highlights

### Gitlet: a version control system (`proj2`)

A working subset of Git, built from scratch in Java and run from the command line.

**Supported commands:** `init`, `add`, `commit`, `rm`, `log`, `global-log`, `find`, `status`, `checkout` (file, file-at-commit, and branch forms), `branch`, `rm-branch`, `reset`, and `merge`.

- Commits, file contents (blobs), branches, and the staging area are separate classes (`Commit`, `Blob`, `Branch`, `Stage`) serialized to a `.gitlet` directory, so repository state persists between runs.
- Commits form a history graph with parent links, including a second parent for merge commits.
- `merge` finds the split point between two branches and applies three-way merge rules across the files in the split, current, and given commits, flagging conflicts.

```bash
cd proj2
javac gitlet/*.java
java gitlet.Main init
java gitlet.Main add hello.txt
java gitlet.Main commit "first commit"
java gitlet.Main log
```

Run the commands from a directory containing the files you want to track.

### Build Your Own World: a procedural dungeon game (`proj3`)

A seeded, procedurally generated 2D dungeon with keyboard controls, built on the course's tile rendering engine. Find the key, then open the chest to win.

**How the world is generated** (`MapGenerator`)

1. Place up to 60 randomly sized rooms, rejecting any that overlap an existing room.
2. Run a **Delaunay triangulation** (via the [JTS](https://github.com/locationtech/jts) library) over the room centers to get a graph of sensible neighbors.
3. Reduce that graph to a **minimum spanning tree** using **Kruskal's algorithm** with a union-find structure (`DisjointSet`, with path compression), so every room is reachable with no redundant hallways.
4. Carve L-shaped hallways along the tree's edges, wall in rooms and hallways, then place the avatar, chest, and key.

**Design notes**

- The world is fully determined by a numeric seed (a seeded `Random`), so the same seed always produces the same dungeon.
- Game state is just the seed plus the movement history. Saving serializes that string to disk, and loading regenerates the world and replays the moves.
- The HUD shows objective text and a description of whichever tile the mouse is over.

**Controls:** `N` new game (type a seed, then `S`), `L` load a saved game, `W` `A` `S` `D` to move, `:Q` to save and quit.

**Running it:** clone with submodules (`git clone --recurse-submodules`), open `proj3` as a Maven project (JTS is declared in `pom.xml`), and run `byow.Core.Main`.

## Other Projects

| Project | What it is |
| --- | --- |
| `proj0`: 2048 | Implements the tilt and merge logic for the 2048 game board, following the game's rules (each tile merges at most once per move). |
| `proj1`: Deques | `LinkedListDeque` and `ArrayDeque` implementations of a shared `Deque` interface, a `MaxArrayDeque` that finds the maximum using a `Comparator`, and JUnit tests for each. Also includes a Karplus-Strong guitar string simulation (`gh2`) built on top of the deque. |
| `proj1ec`: Extra credit | A `StudentArrayDeque` testing exercise. |

## Labs

| Lab | Topic |
| --- | --- |
| `lab1` | Java basics (Collatz, command-line and environment setup) |
| `lab2` | Linked lists (`IntList`), debugging exercises, and unit tests |
| `lab3` | Randomized testing against a buggy `AList`, and timing tests for `AList` and `SLList` |
| `lab4` | Debugging and fixing the `Flik` utility |
| `lab6` | Persistence: serializing objects to disk in the Capers program |
| `lab7` | `BSTMap`, a binary search tree implementation of the `Map61B` interface |
| `lab8` | `MyHashMap`, a hash map with multiple bucket implementations (array list, linked list, hash set, priority queue, and tree set) |
| `proj3/byow/lab12` | `HexWorld`: tessellating hexagons with the tile engine |
| `proj3/byow/lab13` | `MemoryGame`: a memory-sequence game using the drawing library |

## Repository Layout

```
.
├── lab1 … lab8        # Weekly labs
├── proj0              # 2048
├── proj1, proj1ec     # Deques and Guitar Hero
├── proj2              # Gitlet
└── proj3              # Build Your Own World (plus lab12, lab13)
```

## Environment

- Java JDK 16 or later (`proj3/pom.xml` targets 16)
- Most labs and projects include a Maven `pom.xml` and open directly in IntelliJ IDEA. Tests are written with JUnit and can be run from the IDE.
- Gitlet is run from the command line (see above). The graphical projects need the course library, which is included as the `library-sp21` submodule.
