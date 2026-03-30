package hangman;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class ImageCompressor {
    
    public static void compressImage(String inputPath, String outputPath) throws IOException {
        System.out.println("========================================");
        System.out.println("    IMAGE COMPRESSION - HUFFMAN + RLE");
        System.out.println("========================================");
        
        File inputFile = new File(inputPath);
        long originalSize = inputFile.length();
        System.out.println("Input file: " + inputPath);
        System.out.println("Original size: " + originalSize + " bytes");
        
        long startTime = System.currentTimeMillis();
        
        BufferedImage image = ImageIO.read(inputFile);
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Image dimensions: " + width + "x" + height);
        
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                pixels[y * width + x] = gray;
            }
        }
        
        System.out.println("Converting to grayscale... Done");
        
        List<Integer> runValues = new ArrayList<>();
        List<Integer> runLengths = new ArrayList<>();
        
        int currentValue = pixels[0];
        int currentCount = 1;
        
        for (int i = 1; i < pixels.length; i++) {
            if (pixels[i] == currentValue && currentCount < 255) {
                currentCount++;
            } else {
                runValues.add(currentValue);
                runLengths.add(currentCount);
                currentValue = pixels[i];
                currentCount = 1;
            }
        }
        runValues.add(currentValue);
        runLengths.add(currentCount);
        
        System.out.println("RLE encoding... " + runValues.size() + " runs");
        
        byte[] runData = new byte[runValues.size() * 2];
        for (int i = 0; i < runValues.size(); i++) {
            runData[i * 2] = (byte) (int) runValues.get(i);
            runData[i * 2 + 1] = (byte) (int) runLengths.get(i);
        }
        
        Map<Byte, Integer> frequency = new HashMap<>();
        for (byte b : runData) {
            frequency.put(b, frequency.getOrDefault(b, 0) + 1);
        }
        
        PriorityQueue<HuffNode> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.freq));
        for (Map.Entry<Byte, Integer> entry : frequency.entrySet()) {
            pq.add(new HuffNode(entry.getKey(), entry.getValue()));
        }
        
        while (pq.size() > 1) {
            HuffNode left = pq.poll();
            HuffNode right = pq.poll();
            pq.add(new HuffNode(left.freq + right.freq, left, right));
        }
        
        Map<Byte, String> codes = new HashMap<>();
        generateCodes(pq.peek(), "", codes);
        
        System.out.println("Huffman encoding... Done");
        
        StringBuilder encoded = new StringBuilder();
        for (byte b : runData) {
            encoded.append(codes.get(b));
        }
        
        byte[] compressed = new byte[(encoded.length() + 7) / 8];
        for (int i = 0; i < encoded.length(); i++) {
            if (encoded.charAt(i) == '1') {
                compressed[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        
        FileOutputStream fos = new FileOutputStream(outputPath);
        DataOutputStream dos = new DataOutputStream(fos);
        
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeInt(runValues.size());
        
        dos.writeInt(frequency.size());
        for (Map.Entry<Byte, Integer> entry : frequency.entrySet()) {
            dos.writeByte(entry.getKey());
            dos.writeInt(entry.getValue());
        }
        
        dos.writeInt(encoded.length());
        dos.write(compressed);
        
        dos.close();
        
        long endTime = System.currentTimeMillis();
        
        File outputFile = new File(outputPath);
        long compressedSize = outputFile.length();
        
        System.out.println("----------------------------------------");
        System.out.println("COMPRESSION COMPLETE!");
        System.out.println("----------------------------------------");
        System.out.println("Output file: " + outputPath);
        System.out.println("Compressed size: " + compressedSize + " bytes");
        System.out.println("Size reduced: " + (originalSize - compressedSize) + " bytes");
        double ratio = (1 - (double)compressedSize / originalSize) * 100;
        System.out.println("Compression ratio: " + String.format("%.2f", ratio) + "%");
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("========================================");
    }
    
    private static void generateCodes(HuffNode node, String code, Map<Byte, String> codes) {
        if (node == null) return;
        
        if (node.byteValue != null) {
            codes.put(node.byteValue, code.isEmpty() ? "0" : code);
            return;
        }
        
        generateCodes(node.left, code + "0", codes);
        generateCodes(node.right, code + "1", codes);
    }
    
    private static class HuffNode {
        Byte byteValue;
        int freq;
        HuffNode left, right;
        
        HuffNode(Byte byteValue, int freq) {
            this.byteValue = byteValue;
            this.freq = freq;
        }
        
        HuffNode(int freq, HuffNode left, HuffNode right) {
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    }
}
