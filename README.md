# 🧩 Multithreading & Interprocess Communication in Java

This repository contains four independent programming exercises demonstrating **multithreading**, **synchronization**, and **interprocess communication (IPC)** using **TCP sockets** in Java.  
Each subproject focuses on a different aspect of concurrent and distributed programming.

---

## 🚀 Project Overview

### 🧮 Subproject 1 – Matrix–Vector Multiplication (Threads)
**Folder:** `MatrixVectorMultiplication_Threads`  
Implements the multiplication of a matrix by a vector using multiple threads.  
The computation workload is distributed among `k` threads (where `k` is a power of 2), and performance measurements are taken for 1, 2, 4, and 8 threads to compare execution times.

---

### 🏥 Subproject 2 – Hospital Simulation (Threads & Synchronization)
**Folder:** `HospitalSimulation_Threads`  
Simulates a simplified ICU (Intensive Care Unit) system handling infection cases.  
Two main threads are involved:
- A **Disease** thread that periodically generates new infection cases.
- A **Hospital** thread that periodically heals patients and frees ICU beds.  
The solution uses synchronization (`synchronized` blocks) to ensure mutual exclusion and correct shared data handling.

---

### 🔐 Subproject 3 – Key-Value Server (TCP Sockets)
**Folder:** `KeyValueServer_ClientCommunication`  
Implements a basic **client–server** system using TCP sockets.  
The server maintains a hash table that stores `(key, value)` pairs and supports the following operations:
- **Insert** a key–value pair  
- **Delete** a pair by key  
- **Search** for a key’s value  
- **Quit** the connection  

The server can handle multiple clients simultaneously using multithreading.

---

### ⚙️ Subproject 4 – Producer–Consumer–Server System (TCP Sockets)
**Folder:** `ProducerConsumerServer_System`  
Implements a distributed system where:
- Multiple **servers** each maintain an integer storage variable.  
- **Producers** connect to random servers and add random amounts (10–100).  
- **Consumers** connect to random servers and remove random amounts (10–100).  

Each server handles multiple producers and consumers concurrently using threads.  
The storage value is kept within bounds (`1 ≤ storage ≤ 1000`) with proper synchronization to prevent race conditions.

---

## 🧠 Concepts Demonstrated
- Thread creation and management (`Thread`, `Runnable`)
- Synchronization and mutual exclusion (`synchronized`)
- Interprocess communication with **TCP sockets**
- Client–Server architecture
- Randomized simulation and timing control
- Concurrent data access and thread safety

---

## 🛠️ Technologies Used
- **Java SE 17+**
- **IntelliJ IDEA**
- **TCP/IP Networking**
- **Multithreading API**

---

## ⚙️ How to Run

All necessary arguments (such as ports and IP addresses) are **defined directly through IntelliJ IDEA Run Configurations** for each subproject.

To execute:
1. Open the desired project folder in IntelliJ.
2. Create a **Run Configuration** for each class (`Server`, `Producer`, `Consumer`, etc.).
3. Specify the required arguments in the **Program arguments** field (e.g. ports or IPs).
4. Enable **“Allow parallel run”** to launch multiple producers and consumers simultaneously.
5. Run each configuration — servers first, then producers and consumers.

This setup allows complete control over parameters without modifying the source code.
