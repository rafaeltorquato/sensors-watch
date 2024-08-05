package br.com.torquato.measurement.warehouse.support

class UdpClient {

    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf;
    private final int port

    UdpClient(String host, int port) {
        this.port = port
        socket = new DatagramSocket();
        address = InetAddress.getByName(host);
    }

    def sendEcho(String msg) {
        buf = msg.getBytes();
        def packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }

    def close() {
        socket.close();
    }
}
