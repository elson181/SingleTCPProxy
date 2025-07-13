# SingleTCPProxy Project

This project implements a custom HTTP proxy system designed to forward browser or curl-based web requests over a **single TCP connection** to an offshore server. The proxy client accepts standard HTTP requests and tunnels them through one persistent TCP socket, where the offshore server processes the requests and fetches the actual web content from the internet.

This architecture simulates how a proxy gateway can centralize outbound HTTP access over a controlled connection — a common approach in firewalls, secure VPNs, and enterprise-controlled environments.

The system is built using **Java (without external web frameworks)** to handle low-level socket communication and is containerized with **Docker** for easy deployment and testing.

Key goals of the assessment:
- Build a **client-side proxy** that listens for browser/curl traffic and manages request queuing
- Establish a **single custom TCP connection** with a remote server
- Implement a **server-side proxy** that receives raw HTTP, executes the actual requests, and returns responses
- Package both components using **Docker** and validate the end-to-end flow

The project supports HTTP requests and demonstrates basic proxy queueing, tunneling, and custom TCP stream handling.

---


## 🏗 Architecture

```text
[curl/browser]
        ↓
  localhost:8080
[ShipProxy - Client] (single TCP)
        ↓  
   server:9090
[OffshoreProxy - Server]
        ↓
[Internet Website]

````


---

## 📦 Modules

| Module        | Description                             |
|---------------|-----------------------------------------|
| `client`      | Listens on port 8080, queues HTTP requests, and forwards them over a persistent TCP socket |
| `server`      | Accepts one TCP connection, reads requests, performs HTTP calls, sends responses back |

---

## 🚀 How to Run
### 1. Docker Run Commands

#### Create Docker Network:
````
docker network create proxy-net
````
#### Then
#### Run the Offshore Server First (Server Proxy):

````bash
docker run -d --name offshore-proxy --network=proxy-net -p 9090:9090 elsong/server
 ````
#### Then
#### Run the Ship Server (Client Proxy):

````bash
docker run -d --name ship-proxy --network=proxy-net -p 8080:8080 elsong/client
````


### 2. Test with curl

After containers are running, open a new terminal and run:

````bash
 curl.exe -x http://localhost:8080 http://httpforever.com/
````

### 3. Test with Browser

You can also test using any web browser (e.g., Chrome, Edge, Firefox):

Set HTTP Proxy to:

Address: localhost

Port: 8080

#### Then visit:
````bash
 http://httpforever.com
 ````

### 4. To stop and remove the containers:

````bash
docker stop ship-proxy offshore-proxy
docker rm ship-proxy offshore-proxy
docker network rm proxy-net
````


