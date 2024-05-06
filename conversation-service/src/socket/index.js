/* eslint-disable import/newline-after-import */
const SocketManager = require('./SocketManager');
const RoomManager = require('./RoomManager');
const authMethod = require('../auth/auth.method');
const {updateOnlineUser} = require("../utils/clients/userClient");
function socket(io) {
    io.on('connection', (sk) => {
        console.log('New WS Connection...', sk.id);
        let userID;
        let accessToken;
        sk.on('login', async (token) => {
            accessToken = token;
            const verified = await authMethod.extractUserIdFromToken(token);
            if (!verified) {
                console.log('Invalid token');
                return;
            }
            userID = verified.sub;
            console.log('login', userID)
            try {
               const user = await updateOnlineUser(token,true);
                console.log('User online', user);
                // Add user to socket manager
                SocketManager.addUser(userID, sk);
                // Send user online
               SocketManager.sendAll(`online:${userID}`, userID);
            } catch (err) {
                console.log(err);
            }
        });

        RoomManager(sk, io);

        // Runs when client disconnects
        sk.on('disconnect', async () => {
            console.log('Client disconnected', userID);
            try {
                // Update user offline
                const user = await updateOnlineUser(accessToken,false);
                console.log('User offline', user);

                // Remove user from socket manager
                SocketManager.removeUser(userID);
                SocketManager.sendAll(`offline:${userID}`, userID);
            } catch (err) {
                console.log(err);
            }
        });
    });
}

module.exports = socket;
