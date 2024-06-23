// Sử dụng axios để gọi API từ user-service
// Lấy thông tin user từ userId
const axios = require("axios");
exports.populateUser = async (userId) => {
	try {
		const user = await axios.get(`${process.env.AXIOS_API_SERVICE}/user/getUser/${userId}`);
		return user.data;
	} catch (error) {
		console.log(error);
		return null;
	}
};
//lay tat ca id user
exports.getAllUserId = async () => {
	try {
		const users = await axios.get(`${process.env.AXIOS_API_SERVICE}/user/getAllUserId`);
		return users.data;
	} catch (error) {
		console.log(error);
		return null;
	}
};
//cap nhat online user
exports.updateOnlineUser = async (accessToken ,isOnline) => {
	try {
		const headers = {
			'Content-Type': 'application/json',
			Authorization: `Bearer ${accessToken}`,
		}
		console.log("accessToken",accessToken);
		const user = await axios.put(`${process.env.AXIOS_API_SERVICE}/user/
		updateOnline?isOnline=${isOnline}`, {}, {headers});
		return user.data;
	} catch (error) {
		console.log(error);
		return null;
	}
};
