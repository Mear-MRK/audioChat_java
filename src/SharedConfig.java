import javax.sound.sampled.AudioFormat;

public interface SharedConfig {
	int port = 54321;

	int sampleRate = 16000;
	int bitsPerSample = 8;
	int numChannels = 1;
	AudioFormat format = new AudioFormat(sampleRate, bitsPerSample, numChannels, false, false);

	int bytesPerFrame = bitsPerSample * numChannels / 8;

	int delay_ms = 80;
	int buffSize = (int) Math.round(delay_ms/1000.0 * sampleRate) * bytesPerFrame;

}