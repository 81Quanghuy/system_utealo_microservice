const Eureka = require("eureka-js-client").Eureka;
const eurekaHost = "localhost"; // Địa chỉ của máy chủ Eureka
const eurekaPort = 8761; // Cổng của máy chủ Eureka
const hostName = process.env.HOSTNAME || "localhost";
const os = require("os");

// Lấy địa chỉ IP của máy chủ hiện tại
function getIpAddress() {
  const interfaces = os.networkInterfaces();
  for (const interfaceName of Object.keys(interfaces)) {
    for (const details of interfaces[interfaceName]) {
      // Lọc ra địa chỉ IPv4 và không phải là địa chỉ nội bộ
      if (details.family === "IPv4" && !details.internal) {
        return details.address;
      }
    }
  }
  return null; // Trả về null nếu không tìm thấy địa chỉ IP hợp lệ
}

// Sử dụng địa chỉ IP trong cấu hình của Eureka Client
const ipAddr = getIpAddress();
console.log(ipAddr);
exports.registerWithEureka = function (appName, PORT) {
  const client = new Eureka({
    instance: {
      app: appName,
      hostName: hostName,
      ipAddr: ipAddr,
      statusPageUrl: `http://${ipAddr}:${PORT}/actuator/info`,
      port: {
        $: PORT,
        "@enabled": "true",
      },
      vipAddress: appName,
      dataCenterInfo: {
        '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
        name: 'MyOwn',
      },
    },
    eureka: {
      host: eurekaHost,
      port: eurekaPort,
      servicePath: "/eureka/apps/",
      maxRetries: 10,
      requestRetryDelay: 2000,
    },
  });

  client.logger.level("debug");

  client.start((error) => {
    console.log(error || "user service registered");
  });

  function exitHandler(options, exitCode) {
    if (options.cleanup) {
    }
    if (exitCode || exitCode === 0) console.log(exitCode);
    if (options.exit) {
      client.stop();
    }
  }

  client.on("deregistered", () => {
    process.exit();
    console.log("after deregistered");
  });

  client.on("started", () => {
    console.log("eureka host  " + eurekaHost);
  });

  process.on("SIGINT", exitHandler.bind(null, { exit: true }));
};
