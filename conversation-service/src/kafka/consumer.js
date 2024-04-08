const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  brokers: [process.env.KAFKA_HOST],
});

const consumer = kafka.consumer({ groupId: "group_id" });

const runConsumer = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: "topic_name" });
  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      console.log(`Received message: ${message.value.toString()}`);
      // Process the message here
    },
  });
};

module.exports = {
  runConsumer,
};
