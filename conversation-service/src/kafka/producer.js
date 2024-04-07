const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  brokers: [process.env.KAFKA_HOST],
});

const producer = kafka.producer();

const sendMessage = async (message) => {
  await producer.send({
    topic: "topic_name",
    messages: [{ value: JSON.stringify(message) }],
  });
};

const runProducer = async () => {
  await producer.connect();
  console.log("Producer connected to Kafka");
};

module.exports = {
  sendMessage,
  runProducer,
};
