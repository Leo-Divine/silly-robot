# Silly Robot Overview

## Introduction

**Silly Robot** is a project that lets you control a Sphero robot by stacking colorful coding blocks on a computer, instead of typing traditional code.

The project has two main parts:

1. **Application** — A Java program on your computer (called `silly-bot`). It looks a lot like Scratch: you drag blocks, connect them, and press play.
2. **Arduino** — Code that runs on an Arduino board attached to the Sphero. The Arduino talks to the Sphero’s motors, lights, sensors, and buzzer, and also talks to the computer over Wi‑Fi.

Here is the big idea of how they work together:

```
You build a program with blocks
        ↓
The Application turns those blocks into short text commands
        ↓
Commands travel over Wi‑Fi to the Arduino
        ↓
The Arduino tells the Sphero what to do
```

Think of the Application as the “brain that plans,” and the Arduino as the “hands that move the robot.”

---

## How the Application Works

The Application lives in the `Application/silly-bot` folder. It is a JavaFX app, which means it draws a window with graphics and reacts to mouse and keyboard input.

### The main pieces


| File              | Job                                                                                      |
| ----------------- | ---------------------------------------------------------------------------------------- |
| `App.java`        | Starts the program, redraws the screen, waits for the robot, and runs your block program |
| `Editor.java`     | The coding workspace: menu, canvas, dragging, snapping, deleting                         |
| `Block.java`      | Defines block types, shapes, colors, and how blocks draw themselves                      |
| `BlockPaths.java` | Draws the puzzle-piece outlines of each block shape                                      |
| `Server.java`     | Opens a network “door” (port 9090) so the robot can connect and receive commands         |
| `NotePicker.java` | A tiny piano menu for choosing musical notes                                             |


### Starting up and redrawing the screen

When you open the app:

1. `App` creates a `Server` that listens for the robot on port **9090**.
2. It creates an `Editor` canvas (a big drawing surface).
3. An `AnimationTimer` runs over and over, many times per second. Each loop:
  - Clears the canvas
  - Draws the background and block menu
  - Draws all blocks on the workspace
  - Draws the play button
  - Shows popups like “Searching for Robot...” or “Running Code...”
  - Checks whether the robot is connected

This constant redraw is why the blocks look smooth when you drag them.

### The block menu and the workspace

The left side of the screen is the **menu**. It shows blocks grouped by category:

- **Movement** — drive and turn
- **Display** — anything to do with appearance, mostly with the lights
- **Sound** — play or stop notes
- **Sensors** — read distance
- **Control** — wait, loops, if statements
- **Operands** — compare values (`=`, `<`, `>`)

The right side is the **workspace**. A special **Start** block (“On Program Start”) is already there. Your program is the chain of blocks hanging under Start.

### How blocks are drawn

Blocks are not stored as image files, that would be horrific for loading. Instead they are drawn with paths, very similar to SVG shapes.

`BlockPaths.java` has drawing recipes for each shape:

- **Default** — normal puzzle blocks that snap above/below each other (move, wait, turn, etc.)
- **Start** — rounded top; only one of these
- **Value** — rounded “pill” shape that fits into a parameter (like “Get Front Distance”)
- **Operand** — pointed shape for comparisons (like `=` or `>`)
- **Nesting** — C-shaped block that holds other blocks inside (If, Loop)
- **DoubleNesting** — nesting but with two pockets (If/Else style)

When the app draws a block, it:

1. Builds a path for that block’s shape and size
2. Turns the path into drawing instructions
3. Fills it with the category color and outlines it
4. Writes the label text in white

**Parameters:**

Some labels use a special marker,  `α` (alpha), as a placeholder for a **parameter** — a slot for a number, color, note, or another block.

You never see the α on screen. It only exists in the block’s label text in the code. For example, the Move Forward block’s label is stored as:

`Move At α Speed For α Seconds`

When the app draws that block, it **splits** the label wherever it finds an α. That turns the string into pieces of normal text, with a parameter slot between each piece:

1. Draw `"Move At "`
2. Draw the first parameter (speed)
3. Draw `" Speed For "`
4. Draw the second parameter (duration)
5. Draw `" Seconds"`

So each α means “put parameter number 0, 1, 2… here, in order.” The first α matches `parameters[0]`, the second matches `parameters[1]`, and so on. If a label has no α at all (like `"Turn Left"`), the app just draws the whole text and skips parameters.

### Dragging, snapping, and deleting

**Dragging from the menu:**  
Click a menu block, and the editor creates a new copy on the workspace and starts dragging it.

**Moving blocks:**  
While you drag, the block follows the mouse. Connected blocks (below it, nested inside it, or stuck in its parameters) move with it.

**Snapping blocks together:**  
When you release the mouse, the editor checks nearby blocks:

- Normal blocks snap **under** another block if they are close enough.
- Nesting blocks accept blocks **inside** their pocket.
- Value and Operand blocks can snap **into parameter slots**.

Blocks remember links with pointers like `aboveBlock`, `belowBlock`, `parentBlock`, and `nestedBlock`. That linked list *is* your program.

**Deleting:**  
Drag a block (and its connected pieces) back over the menu area on the left. Those blocks get removed.

### Parameters (the editable slots)

Many blocks need extra info, such as speed, wait time, or color.

- Click a number parameter and type digits (or Backspace). Numbers are capped at 255.
- Click a color parameter to open a color picker.
- Click a note parameter to open the piano-style note picker.
- Drag a Value block into a number slot, or an Operand block into a condition slot (for example, on an If block).

Parameters can grow wider when their text or child block is larger, so the parent block stretches to fit.

### Running your program

1. The robot must be connected (the “Searching for Robot...” popup should be gone).
2. Press the round **play** button in the top-right.
3. `App` starts a background thread that calls `runBlockCode` beginning at the Start block.
4. That function walks down the chain of blocks:
  - Reads each block’s parameters
  - For robot actions, sends a command through `Server`
  - Waits for the robot’s reply before continuing
  - For If / Loop / comparisons, decides what to run next in software on the computer

Because the arduino has the worst storage in history and you can't store anything that could scale, only the physical actions are sent to the arduino. If's, loops, everything else is handled in the application. If it could be done in the app, do it in the app.

---

## How the Robot (Arduino) Works

The robot code lives in the `Arduino` folder.

### The main pieces


| File                      | Job                                                                               |
| ------------------------- | --------------------------------------------------------------------------------- |
| `Arduino.ino`             | Main file. Connects to Wi‑Fi, connects to the computer, reads commands in a loop. |
| `Sphero.h` / `Sphero.cpp` | A class for the robot. Handles all that affects the robot                         |


### Hardware the Arduino talks to

- **Sphero RVR** — through the Sphero RVR library (`rvr`), for driving and LEDs
- **Wi‑Fi module** — through `WiFiEsp` on a serial link (pins 2 and 3)
- **Ultrasonic distance sensor** — trig pin 9, echo pin 10
- **Buzzer** — pin 8, for musical notes

[Brooke - Prolly will add a picture here]

### Startup (`setup`)

When the Arduino powers on, it:

1. Starts serial communication and initializes the Sphero (`sphero.initialize()`)
2. Starts the Wi‑Fi module
3. Connects to the configured Wi‑Fi network
4. Connects to the computer’s IP address on port **9090**
5. Sends a greeting: `"Howdy!"`

The computer’s IP address and Wi‑Fi settings are written in `Arduino.ino` and must match your home network and the machine running the Application.

### The main loop (`loop`)

Forever, the Arduino:

1. Calls `rvr.poll()` so the Sphero library stays updated
2. Reads incoming text from the computer
3. Looks at the first five characters (`R_000`, `R_002`, etc.)
4. Runs the matching Sphero action
5. Replies so the app knows it can send the next command

If nothing useful arrives for about **15 seconds**, the robot gets mad, sends `"FCKOFF"`, disconnects, and tries to reconnect.

If you leave the connection on forever and you close the application, it won't be able to reconnect without restarting. This prevents that from happening.

### What each Sphero helper does

In `Sphero.cpp`:

- `moveForward(speed, length)` — spins both motors forward, waits, then stops
- `rotateRight()` **/** `rotateLeft()` — uses the RVR drive control to turn about 90°
- `setColor(...)` — sets left and right LED colors
- `getSensorData()` — pings the ultrasonic sensor and returns distance in centimeters
- `playTone` **/** `stopTone` — plays or stops a tone on the buzzer

So the Arduino’s job is simple: **listen for a command code → do one robot action → say “ready.”**

---

## Communication

Here's how the robot and app communicate:

### Step 1 — The app opens a door and waits

```
App starts
   ↓
Server opens port 9090
   ↓
App shows “Searching for Robot...”
   ↓
App waits until something connects
```

When the Application starts, `Server.java` opens a network “door” on port **9090**. Until the robot connects, the app keeps showing the searching popup and will not run your block program.

### Step 2 — The robot joins Wi‑Fi and knocks

```
Arduino powers on
   ↓
Connects to Wi‑Fi
   ↓
Connects to the computer’s IP on port 9090
   ↓
Sends greeting: "Howdy!"
```

The Arduino uses settings in `Arduino.ino` (Wi‑Fi name/password and the computer’s IP). Once connected, it greets the app. From that moment, both sides share one open chat line over Wi‑Fi.

### Step 3 — The command language (what messages look like)

If we want to send a command to the arduino to move the robot move at speed 128 for 2 seconds, it would look like:

`R_000 | 128 | 002`

R_000 is the code. The code specifies what action the robot takes. This one is specificaly to move forward.

The rest after the code are parameters. 128 is the speed, and 002 is the duration.

The full command would look like: `R_000128002`


| Code    | Meaning             | Extra data                                    |
| ------- | ------------------- | --------------------------------------------- |
| `R_000` | Move forward        | speed (3 digits) + duration (3 digits)        |
| `R_001` | Rotate left         | none                                          |
| `R_002` | Rotate right        | none                                          |
| `R_003` | Set lights          | left RGB + right RGB (each color is 9 digits) |
| `R_004` | Get distance sensor | robot replies with a number                   |
| `R_005` | Play note           | frequency (4 digits) + duration (4 digits)    |
| `R_006` | Stop note           | none                                          |
| `R_007` | Wait                | seconds (3 digits)                            |


The robot reads the first five characters to learn *which* action to do, then reads the digits after that for *how* to do it.

### Step 4 — Running a program (one command at a time)

```
You press Play
   ↓
App starts at the Start block and walks the chain
   ↓
For a robot action block:
      App sends one R_00X... line
            ↓
      Arduino does that one action
            ↓
      Arduino replies to signal it's done. 
            ↓
      App reads the reply, then continues to the next block
   ↓
Repeat until the chain is finished
```

The robot's reply is the return value of the command, or "Next Bitch" if there's no return value.

The app sends **one** command, then **waits** for a reply before sending another, because god forbit a white robo boy have a little bit more swag that he needs. The arduino sucks, don't know why we used one.

### Step 5 — Special replies and reconnecting


| Message / situation             | What it means                                            |
| ------------------------------- | -------------------------------------------------------- |
| `"Howdy!"`                      | Robot just connected                                     |
| `"Next Bitch"`                  | Robot finished the last command and is ready for another |
| A number (after `R_004`)        | Distance sensor reading the app can use in code          |
| `"FCKOFF"`                      | Robot is giving up on this connection                    |
| Connection drops / null message | App disconnects and goes back to searching               |


```
No useful messages for ~15 seconds (variable to change, I like a minute personally)
   ↓
Robot sends "FCKOFF", closes the connection, tries again
   ↓
App notices, disconnects, shows “Searching for Robot...” again
```

---

## Setup Instructions

> *This section will be filled in later.*
>
> Planned topics may include: installing Java / Maven, running the Application, flashing the Arduino, setting Wi‑Fi and IP address, wiring the sensor and buzzer, and connecting the Sphero RVR.

---

## Possible Addons/Modifications

I wouldn't be supprised if Mr. Hankerchief or The L Man decide they want to have someone improve on the robot. I have heard stories of their(mostly L Man's) conquests to ruin absolutely everything. I'm going to detail some actually good ideas of what should be added, and what explicitly not to touch.

First and foremost, unless you know what the fuck you're doing, don't try to go crazy changing the core functions. Unless you are Arielle, do not go in and mess with something like how blocks conenct or parameters. If you do, make a copy of your code, or a new branch. Something, please.  

Also, try your best to avoid the BlockPaths. I spent way too long stealing the block shapes from Scratch and recreating them step by step. I'm not detailing how I did that, it was tedious af. 

Anyway, here are some actually good ideas.

### Idea 1) Variables

This one is definatly the most advanced programatically, not for the faint of heart.

I tried doing this back in school, but I didn't have time after starting the Co-op. It's definatly possible, I probably could've done it, but I chose to try creating some technical document that detailed all the functions and everything and it was shitty. To this date I have never needed to write any document before writing/making a change, so screw you L Man.  

It would probably stem down to making a variable manager class of some sort that would exist as an object/field in the editor class. Because there could be multiple variables, it would be wise to have a central storage location that can handle providing all the data and calculations for all of them. That's why you make a class.  

You would need to make it so the menu updates/creates new blocks for new variables created. Do not create a new type of block, just make the chosen variable a parameter.  

The last thing I can plan out now is that you should store the values of the variable in the object, then pass that object into the runBlockCode command. Somehow it needs to be accessed in the app. I can't get it into words, but the one thing I know for sure is do not handle variables in the arduino.  

Good luck with this one, it's a big one.

### Idea 2) Robot Discoverability

Right now, the arduino needs the IP address of the computer hosting the app to connect. What you could do is have the app scan the wifi for the arduino and ask it to connect/send it a request to connect to the computers IP.