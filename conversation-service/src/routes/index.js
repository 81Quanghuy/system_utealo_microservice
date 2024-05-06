const createError = require("http-errors");
const { v4: uuidv4 } = require("uuid");

const Conversation = require('./conversation');
const Message = require('./messages');
const Notification = require('./notification');
const logEvents = require("../Helpers/logEvents");
const bot = require("../utils/SlackLogger/bot");
function route(app) {

  // limit access to 20 requests per 1 minutes
 // app.use(limiter);
  app.use("/api/v1/conversation", Conversation);
  app.use("/api/v1/messages", Message);
  app.use("/api/v1/notification", Notification);
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
