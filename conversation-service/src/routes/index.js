const createError = require("http-errors");
const { v4: uuidv4 } = require("uuid");

const Conversation = require('./conversation');
const Message = require('./messages');
const logEvents = require("../Helpers/logEvents");
const bot = require("../utils/SlackLogger/bot");

function route(app) {
  // cors handle
  app.use((req, res, next) => {
    const allowedOrigins = [
      "http://localhost:5173",
      "http://localhost:5174",
      "https://tana.social",
      "https://tana-admin.vercel.app",
      "https://tana.social",
    ];
    const { origin } = req.headers;
    if (allowedOrigins.includes(origin)) {
      res.setHeader("Access-Control-Allow-Origin", origin);
    }
    // res.setHeader('Access-Control-Allow-Origin', '*');
    // res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
    res.header(
      "Access-Control-Allow-Headers",
      "Origin, X-Requested-With, Content-Type, Accept"
    );
    if (req.method === "OPTIONS") {
      res.header(
        "Access-Control-Allow-Methods",
        "PUT, POST, PATCH, DELETE, GET"
      );
      return res.status(200).json({});
    }
    next();
  });

  // limit access to 20 requests per 1 minutes
 // app.use(limiter);
  app.use("/api/v1/conversation", Conversation);
  app.use("/api/v1/messages", Message);
  // get error 404
  app.use((req, res, next) => {
    next(
      createError(404, `Method: ${req.method} of ${req.originalUrl}  not found`)
    );
  });
  // get all errors
  // eslint-disable-next-line no-unused-vars
  app.use((error, req, res, next) => {
    logEvents(`idError: ${uuidv4()} - ${error.message}`);
    bot.sendNotificationToBotty(
      `Method: ${req.method} of ${req.originalUrl}  not found\n${error.message}`
    );
    res.status(error.status || 500);
    res.json({
      error: {
        message: error.message,
      },
    });
  });
}

module.exports = route;