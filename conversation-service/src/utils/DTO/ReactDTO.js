class ReactDTO {
    constructor(react,message) {
        this.react = react.react;
        this.userId = react.userId || null;
        this.conversation = message.conversation || null;
        this.bio = group.bio || null;
        this.isPublic = group.isPublic || null;
        this.createAt = message.conversation || null;
    }
}

module.exports = ReactDTO;
