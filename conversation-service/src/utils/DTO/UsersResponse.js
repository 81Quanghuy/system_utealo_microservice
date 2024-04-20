class UsersResponse {
  constructor({
    userId,
    username,
    background,
    avatar,
    isOnline,
  }) {
    this.userId = userId;
    this.username = username;
    this.background = background;
    this.avatar = avatar;
    this.isOnline = isOnline;
  }
}

module.exports = UsersResponse;
