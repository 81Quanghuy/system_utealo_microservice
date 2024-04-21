/* eslint-disable import/newline-after-import */
const SocketManager = require('./SocketManager');
const RoomManager = require('./RoomManager');
const authMethod = require('../auth/auth.method');
function socket(io) {
    io.on('connection', (sk) => {
        console.log('New WS Connection...', sk.id);
        let userID;
        sk.on('login', async (token) => {
            console.log('login', token)
            const verified = await authMethod.extractUserIdFromToken(token);
            if (!verified) {
                console.log('Invalid token');
                return;
            }
            userID = verified.sub;
            try {
                // data is userID
               // await AccessController.updateAccessInDay();

                // Update user isOnline
                // const user = await User.findByIdAndUpdate(
                //     userID,
                //     {
                //         isOnline: true,
                //     },
                //     { new: true }
                // );

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
            // console.log('Client disconnected', userID);
            try {
                // Update user isOnline
                // const user = await User.findByIdAndUpdate(
                //     userID,
                //     {
                //         isOnline: false,
                //         lastAccess: Date.now(),
                //     },
                //     { new: true }
                // );

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
