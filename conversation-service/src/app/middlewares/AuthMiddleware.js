const authMethod = require('../../auth/auth.method');
const {populateUser} = require("../../utils/clients/userClient");
const getAccessTokenFromHeader =(req) => {
	const accessTokenFromHeader = req.headers.authorization
	// Remove "Bearer " in the Authorization header
	return accessTokenFromHeader?.replace('Bearer ', '');
}

exports.isAuth = async (req, res, next) => {
	try {
		// Lấy access token từ header
		const accessToken = getAccessTokenFromHeader(req);
		if (!accessToken) {
			return res.status(401).json('Không tìm thấy access token!');
		}

		const verified = await authMethod.extractUserIdFromToken(accessToken);
		if (!verified) {
			return res.status(401).json("Token không hợp lệ!");
		}
		return next();
	} catch (error) {
		console.log(error);
		return res.status(500).json(error);
	}
};
exports.getUser = async (req, res, next) => {
	try {
		console.log(req.headers)
		// Lấy access token từ header
		const accessToken = getAccessTokenFromHeader(req);

		if (!accessToken) {
			return res.status(401).json('Không tìm thấy access token!');
		}

		const verified = await authMethod.extractUserIdFromToken(accessToken);
		if (!verified) {
			return res.status(401).json("Token không hợp lệ!");
		}
		req.user = await populateUser(verified.sub);
		return next();

	}catch (e) {
		console.log(e);
		return res.status(500).json(e);
	}
}
