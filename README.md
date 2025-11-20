# 🧵 Task Scheduler with Load Balancing (Java, Multithreading)

A lightweight **task scheduling system** built using **Java multithreading**, **load balancing**, **SQLite database**, and a **simple REST API + dashboard UI**.  
The scheduler distributes tasks to worker threads based on real-time load, similar to how OS or microservice schedulers work.

---

## 🚀 Features

### ✅ Task Scheduling
- Each task has:
  - **Priority**
  - **Processing time**
  - **Status** (PENDING → RUNNING → COMPLETED)

### ✅ Load Balancing
- System automatically assigns each task to the **worker thread with the lowest load**.
- Load = running tasks + queued tasks per worker.

### ✅ Multithreaded Worker Execution
- Multiple worker threads run tasks in parallel.
- Worker queue architecture simulates a real distributed worker system.

### ✅ SQLite Database
- Stores:
  - Task ID
  - Priority
  - Processing time
  - Status updates

### ✅ REST API (Java HttpServer)
- `POST /task?priority=X&time=Y` → Create task  
- `GET /tasks` → List all tasks  
- `GET /workers` → Worker load overview  

### ✅ Simple Dashboard UI (HTML + TailwindCSS)
- Create tasks
- View workers' live load  
- View task status in real-time  
- Auto-refresh every 500ms  
- No external frameworks needed

---

## 📂 Project Structure

  ## 📂 Project Structure

```
src/main/java/com/scheduler/
│── Main.java           # Application entry point
│── Scheduler.java      # Load balancer
│── Worker.java         # Worker thread with task queue
│── Task.java           # Task model
│── ApiServer.java      # REST API + frontend handler
│── Database.java       # SQLite setup + queries

frontend/
│── index.html          # Dashboard UI

pom.xml                 # Dependencies + shade plugin
```
---

## 🛠 Tech Used

- **Java 17**
- **Multithreading (Thread, BlockingQueue)**
- **SQLite (sqlite-jdbc)**
- **Java HttpServer (REST API)**
- **TailwindCSS (Frontend UI)**
- **Maven (Build + Fat JAR using Shade Plugin)**

---

---

## 🎯 How It Works (Simple Explanation)

1. User creates tasks from the frontend.  
2. API receives the task → stores it in SQLite → sends to Scheduler.  
3. Scheduler assigns task to the worker with **least load**.  
4. Worker runs the task and updates the database.  
5. Dashboard polls `/workers` and `/tasks` every 500ms to show live updates.

---

## 💡 Why I Built This

> To understand **how schedulers, load balancers, and multithreaded systems work** — similar to OS job schedulers or server load balancers.

This project helped me learn:
- Thread synchronization  
- Priority queue scheduling  
- REST API design  
- Database persistence  
- Real-time UI updates  

---

## Author
- SreeHarshini
- GitHub: [@Harshini V](https://github.com/harshin-i)
