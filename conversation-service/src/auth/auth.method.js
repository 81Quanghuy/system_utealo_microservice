const jwt = require('jsonwebtoken');
const fs = require('fs');
const util = require('util');

// Đọc secret key từ file


exports.extractUserIdFromToken = async (token) => {
  try {
      const readFileAsync = util.promisify(fs.readFile);
      const secretKeyPath =  process.env.KEY_PATH;
      const secretKey = await readFileAsync(secretKeyPath);
      return jwt.verify(token, secretKey);
  } catch (error) {
    throw error;
  }
};