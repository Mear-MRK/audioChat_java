import javax.sound.sampled.AudioSystem;


public class SoundDevInfo{

    public static void showInfo() {
        for (var mi : AudioSystem.getMixerInfo()) {
            System.out.println("-- " + mi.toString());
            var mixer = AudioSystem.getMixer(mi);
            for (var li : mixer.getSourceLineInfo()) {
                System.out.println("> SL: " + li.toString());
            }
            for (var li : mixer.getTargetLineInfo()) {
                System.out.println("> TL: " + li.toString());
            }
            System.out.println();
        }
    }

}
