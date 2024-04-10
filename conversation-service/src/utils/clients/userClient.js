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
