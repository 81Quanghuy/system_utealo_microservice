const axios = require("axios");
exports.populateMedia = async (mediaId) => {
    try {
        return await axios.get(`${process.env.AXIOS_API_SERVICE}/files/getMedia/${mediaId}`);
    } catch (error) {
        console.log(error);
        return null;
    }
}
exports.populateListMedia = async ({mediaIds,type,page,size}) => {
    try {

        return await axios.post(`${process.env.AXIOS_API_SERVICE}/files/getMediaList?page=${page}&size=${size}`,{mediaIds,type});
    } catch (error) {
        console.log(error);
        return null;
    }
}