# Simple DLT (Distributed Ledger Technology)

A simplified blockchain implementation for payment transactions, built in Java. This project demonstrates core distributed ledger concepts — block chaining via SHA-256 hashes, tamper detection, and peer-to-peer block synchronization — with a web UI for submitting and viewing payments.

## How It Works

### Blockchain

- Each payment is wrapped in a `PaymentBlock` containing the payment data, a reference to the previous block's hash, and its own SHA-256 hash.
- Blocks are persisted as individual JSON files on disk (one file per block, named by timestamp).
- The chain starts from a genesis block (initial hash: `"Genesis"`).
- Tamper detection: when reading the chain, each block's hash is recalculated and compared. Blocks are flagged with a status:
  - `0` — Valid
  - `1` — Block hash mismatch (data was tampered with)
  - `2` — Previous hash mismatch (chain link broken)

### Peer-to-Peer Synchronization

- Uses [JGroups](http://www.jgroups.org/) for cluster communication.
- When a new payment is submitted, the block is broadcast to all nodes in the `"dlt"` JGroups cluster.
- Each node that receives the message persists the block to its own local ledger.

### Web Interface

- Built with [Spark Java](http://sparkjava.com/) (lightweight HTTP server).
- A Bootstrap-based frontend allows users to submit payments (account number, bank name, currency, amount) and view all blocks in a DataTable that auto-refreshes every 10 seconds.
- Tampered blocks are highlighted in red (hash mismatch) or orange (broken chain link).

## Project Structure

```
src/main/java/dlt/
├── PaymentChain.java        # Entry point — starts HTTP server, handles routes
├── PaymentBlock.java        # Block implementation with SHA-256 hashing
├── Ledger.java              # Reads/writes blocks to disk, validates chain integrity
├── common/
│   ├── Block.java           # Block interface
│   └── Utils.java           # SHA-256 hashing, JSON serialization, UUID generation
├── model/
│   ├── Payment.java         # Payment data model
│   ├── PaymentModel.java    # Payment view model (includes block metadata + status)
│   └── Payments.java        # Wrapper for list of payments (JSON serialization)
└── msg/
    └── DltBroadCaster.java  # JGroups-based peer-to-peer block broadcasting

src/main/resources/public/   # Static web UI (HTML, JS, CSS)
```

## API Endpoints

| Method | Path              | Description                          |
|--------|-------------------|--------------------------------------|
| POST   | `/new-payment`    | Submit a new payment (JSON body)     |
| GET    | `/poll-payments`  | Retrieve all blocks with validation  |

### Payment JSON Format

```json
{
  "accountNumber": "1234567890",
  "bankName": "SCB",
  "currency": "SGD",
  "amount": "1000"
}
```

## Prerequisites

- Java 8+
- Gradle

## Build & Run

```bash
# Build the fat JAR
./gradlew shadowJar

# Run (specify the block storage path and HTTP port)
java -Ddlt.path=/path/to/block/storage -Dport=8080 -jar build/libs/PaymentChain-1.0-all.jar
```

To run multiple nodes, start additional instances on different ports pointing to separate block storage directories. JGroups will automatically discover peers on the same network.

## Dependencies

- **Spark Java** — Embedded HTTP server
- **JGroups** — Cluster communication and block broadcasting
- **Gson** — JSON serialization/deserialization
- **Google Guice** — Dependency injection
- **SLF4J** — Logging
