const jwt = require("jsonwebtoken");
const { promisify } = require("util");
const crypto = require("crypto");
const fs = require("fs");

async function getSecretKey() {
  try {
    const keyPath = '../conversation-service/src/secret.key'; // Đường dẫn đến file chứa key
    const keyBytes = await fs.promises.readFile(keyPath); // Đọc key từ file
    return crypto.createHmac('sha256', keyBytes).digest(); // Tạo secret key từ key bytes
  } catch (error) {
    throw error;
  }
}
let secretKey = null;
getSecretKey()
    .then(key => {
        console.log('secretKey:', key);
        secretKey = key;
    })
    .catch(error => {
      console.error('Error:', error);
    });
exports.extractUserIdFromToken = async (token) => {
  try {
    const decoded = await promisify(jwt.verify)(token, secretKey);
    return decoded.userId;
  } catch (error) {
    throw error;
  }
}
exports.verifyToken = async (token) => {
    try {
        await promisify(jwt.verify)(token, secretKey);
        return true;
    } catch (error) {
        return false;
    }
}