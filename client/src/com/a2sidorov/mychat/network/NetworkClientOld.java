package com.a2sidorov.mychat.network;

public class NetworkClientOld {
    /*

    private SocketChannel channel;
    private String serverAddress;
    private int serverPort;
    private String nickname = "Noname";
    private ByteBuffer buffer;

    public NetworkClientOld(String serverAddress, int serverPort) throws Exception {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        buffer = ByteBuffer.allocate(256);

    }

    public void disconnect() throws IOException {
        channel.close();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public void connect() throws IOException {
        Selector selector = Selector.open();
        channel = SocketChannel.open(new InetSocketAddress(serverAddress, serverPort));
        channel.configureBlocking(false);
        //sendNickname();

        new Thread(() -> {
            while (true) {

                try {
                    selector.select();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();
                while (iter.hasNext()) {
                    System.out.println("looping");

                    SelectionKey key = iter.next();

                    if (key.isAcceptable()) {
                        try {
                            register(selector, channel);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("NetworkClient connected.");
                    }

                    if (key.isReadable()) {
                        try {
                            read(selector, key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    writeToSockets();

                    if (key.isWritable()) {
                        try {
                            write(key);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    iter.remove();
                }
            }
        }).start();
    }

    private void sendNickname() throws IOException {
        buffer.putChar('c');
        buffer.put(nickname.getBytes());
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
    }

    private void process() throws IOException {
        channel.read(buffer);
        buffer.flip();
        char packetType = buffer.getChar();
        String data = drain(buffer);
        buffer.clear();

        if (packetType == 'l') {
            String[] userList = data.split(",");
            MyChatClient.listUsers.setListData(userList);
        }

        if (packetType == 'm') {
            MyChatClient.textChat.append(data + '\n');
        }

    }

    public void sendMessage(String message) throws IOException {
        buffer.putChar('m');
        String data = nickname + ": " + message;
        buffer.put(data.getBytes());
        buffer.flip();
        channel.write(buffer);
        buffer.clear();

    }

    private String drain(ByteBuffer buffer) {
        StringBuffer sb = new StringBuffer();
        while (buffer.hasRemaining()) {
            sb.append((char)buffer.get());
        }
        return sb.toString();
    }

    public boolean isConnected() {
        return channel.isConnected();
    }
    */

}
