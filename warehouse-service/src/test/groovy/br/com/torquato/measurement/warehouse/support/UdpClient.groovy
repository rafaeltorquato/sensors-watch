package br.com.torquato.measurement.warehouse.support

class UdpClient {

    private DatagramSocket socket
    private InetAddress address
    private final int port

    UdpClient(String host, int port) {
        this.port = port
        socket = new DatagramSocket()
        address = InetAddress.getByName(host)
    }

    def sendEcho(String msg) {
        final byte[] buf = msg.getBytes()
        socket.send(new DatagramPacket(buf, buf.length, address, port))
    }

    def close() {
        socket.close()
    }
}
