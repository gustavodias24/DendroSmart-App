package benicio.soluces.dimensional.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioIA {

    // Um único MediaPlayer compartilhado
    private static MediaPlayer mediaPlayer;

    /**
     * Toca um áudio pelo id de recurso em uma thread separada.
     * Ex: AudioIA.tocarAudio(context, R.raw.meu_audio);
     */
    public static void tocarAudio(Context context, int audioResId) {
        // garante que não trava a UI
        new Thread(() -> {
            synchronized (AudioIA.class) {
                // Sempre parar qualquer áudio anterior
                pararAudioInternal();

                Context appContext = context.getApplicationContext();
                mediaPlayer = MediaPlayer.create(appContext, audioResId);

                if (mediaPlayer != null) {
                    mediaPlayer.setOnCompletionListener(mp -> {
                        synchronized (AudioIA.class) {
                            pararAudioInternal();
                        }
                    });

                    try {
                        mediaPlayer.start();
                    } catch (IllegalStateException e) {
                        // Se der algum erro de estado, limpa o player
                        pararAudioInternal();
                    }
                }
            }
        }).start();
    }

    /**
     * Para o áudio atual (se existir) e libera o MediaPlayer.
     */
    public static void pararAudio() {
        synchronized (AudioIA.class) {
            pararAudioInternal();
        }
    }

    // Método interno NÃO sincronizado; sincronização é feita nos métodos públicos
    private static void pararAudioInternal() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (IllegalStateException ignored) {
                // às vezes o MediaPlayer já está em estado finalizado
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
