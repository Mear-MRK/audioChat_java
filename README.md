### Audio Chat App

This is a peer-to-peer (P2P) audio chat command-line application written in Java. It allows two users to have a real-time audio conversation over a network. The application is designed to be simple and easy to use, while still providing good audio quality.

#### Features

*   Real-time P2P audio communication
*   Configurable audio settings
*   Cross-platform compatibility (Windows, macOS, Linux)

#### Getting Started

1.  **Prerequisites:**
    *   Java Development Kit (JDK) 8 or later
    *   A microphone and speakers

2.  **Compilation:**
    *   Open a terminal or command prompt.
    *   Navigate to the project directory.
    *   Compile the Java files using the following command:
        ```bash
        javac -d bin src/*.java
        ```

3.  **Running the Application:**
    *   **To start the chat:**
        ```bash
        java -cp bin AudioChatApp <duration_in_seconds> <destination_IP_address>
        ```
        Replace `<duration_in_seconds>` with the desired duration of the chat and `<destination_IP_address>` with the IP address of the person you want to chat with.


#### Code Structure

*   `AudioChatApp.java`: The main class that starts the chat application. It handles the recording and playback of audio, as well as the sending and receiving of audio data over the network.
*   `PlayApp.java`: A simplified version of the application that only plays back audio received from the network.
*   `PlayingDatagramPacket.java`: This class is responsible for playing back audio data received in the form of datagram packets.
*   `ReceiveDatagramService.java`: This class provides a service for receiving datagram packets over the network.
*   `RecordApp.java`: A simplified version of the application that only records audio and sends it over the network.
*   `RecordToDatagram.java`: This class is responsible for recording audio data and converting it into datagram packets.
*   `SendDatagramService.java`: This class provides a service for sending datagram packets over the network.
*   `SharedConfig.java`: This interface defines shared configuration parameters for the application, such as the port number, audio format, and buffer size.
*   `SoundDevInfo.java`: This class provides information about the available audio devices on the system.

#### Additional Notes

*   The application uses UDP for audio transmission, which is a connectionless protocol that is well-suited for real-time audio communication.
*   The audio format is set to 16 kHz, 8-bit, mono, which is a common format for voice communication.
*   The buffer size is set to 80 ms, which provides a good balance between latency and audio quality.
