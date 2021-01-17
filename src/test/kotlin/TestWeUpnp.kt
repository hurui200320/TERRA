import org.bitlet.weupnp.GatewayDiscover
import org.bitlet.weupnp.PortMappingEntry
import org.slf4j.LoggerFactory


fun main() {
    val logger = LoggerFactory.getLogger("Application")
    val internalPort = 12345
    val externalPort = 54321
    val waitTime = 30L // in s

    logger.info("Program Start!")

    val discover = GatewayDiscover()
    logger.info("Looking for Gateway Devices")
    discover.discover()

    val gatewayDevice = discover.validGateway
    requireNotNull(gatewayDevice) { "No supported gateway device found..." }

    logger.info(
        "Found gateway device:\n{} ({})",
        gatewayDevice.modelName, gatewayDevice.modelDescription
    )

    val localAddress = gatewayDevice.localAddress
    logger.info("Using local address: {}", localAddress)
    val externalIPAddress = gatewayDevice.externalIPAddress
    logger.info("External address: {}", externalIPAddress)

    val portMapping = PortMappingEntry()

    logger.info("Attempting to map port {} to {}", internalPort, externalPort)

    logger.info(
        "Querying device to see if mapping for external port {} already exists",
        externalPort
    )

    require(
        !gatewayDevice.getSpecificPortMappingEntry(
            externalPort,
            "TCP",
            portMapping
        )
    ) { "External port was already mapped." }

    logger.info("Sending port mapping request...")
    require(
        gatewayDevice.addPortMapping(
            externalPort, internalPort,
            localAddress.hostAddress,
            "TCP", "TERRA"
        )
    ) { "Port mapping attempt failed." }


    logger.info(
        "Mapping successful: waiting {} seconds before removing.",
        waitTime
    )
    Thread.sleep(1000 * waitTime)
    gatewayDevice.deletePortMapping(internalPort, "TCP")

    logger.info("Port mapping should be removed")
    logger.info("Done!")
}