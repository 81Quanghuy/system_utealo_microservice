const mongoose = require("mongoose");
const mongooseDelete = require("mongoose-delete");
const mongoosePaginate = require("mongoose-paginate-v2");

const ReactMessageSchema = new mongoose.Schema(
    {
        react: {
            type: String,
            enum: [  "Heart", "Haha", "Sad", "Wow", "Angry", "Like"],
            required: true,
        },
        userId: {
            type: String,
        },
        createdAt: {
            type: Date,
            default: Date.now,
        },
        updatedAt: {
            type: Date,
        },
    },
    { timestamps: true, collection: "reactMessage" }
);

// soft delete
ReactMessageSchema.plugin(mongooseDelete, {
    deletedAt: true,
    overrideMethods: "all",
});

// paginate
ReactMessageSchema.plugin(mongoosePaginate);

module.exports = mongoose.model("ReactMessage", ReactMessageSchema);
