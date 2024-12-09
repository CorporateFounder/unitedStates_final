package unitted_states_of_mankind;

import java.io.*;

public class GPUTest {

    public record GPUResult(String hash, long proof) {
    }



    // Metoda pro načtení obsahu kernelu ze souboru

    private String loadKernelSource() throws IOException {
        // Pevná cesta ke kernelu
        String kernelPath = "/kernels/kernel.cl";
        InputStream inputStream = getClass().getResourceAsStream(kernelPath);
        if (inputStream == null) {
            throw new FileNotFoundException("Kernel file not found at: " + kernelPath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder source = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                source.append(line).append("\n");
            }
            return source.toString();
        }
    }


}
