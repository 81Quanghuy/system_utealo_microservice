const http = require("http");
require("dotenv").config();
const socketIo = require('socket.io');

const app = require("./app");

const server = http.createServer(app);
const eurekaHelper = require("./configs/eureka/eureka-helper");
const HOSTS = require('./configs/cors');

// socket
const io = socketIo(server, {
  cors: {
    origin: HOSTS,
  },
});
const Socket = require('./socket/index');
Socket(io);
// require db
const db = require("./configs/db/index");
// connect to DB
db.connect();

// 127.0.0.1 - localhost
const PORT = process.env.PORT || 8089;
server.listen(PORT, () => {
  console.log(`Backend server is listening on port ${PORT}`);
});
eurekaHelper.registerWithEureka("conversation-service", PORT);
