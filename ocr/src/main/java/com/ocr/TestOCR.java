package com.ocr;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TestOCR {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        ITesseract tesseract = new Tesseract();
            
        try {

            // Caminho tessdata (Tesseract)
            tesseract.setDatapath("E:\\OCR\\tessdata");
            tesseract.setLanguage("por");
            tesseract.setTessVariable("tessedit_char_blacklist","|-—");

            // Caminho arquivo para leitura (Acordão)
            // String scan = tesseract.doOCR(new File("E:\\GITHUB\\tesseract-acordao\\ocr\\src\\acordaos\\image.png"));
            // String texto = scan.replace("\n\n", "\n").replace("|", "");
            // System.out.println(texto);

            BufferedImage imagem = ImageIO.read(new File("E:\\GITHUB\\tesseract-acordao\\ocr\\src\\acordaos\\imagecompleta3.png"));
            
            BufferedImage area1 = imagem.getSubimage(585, 385, 915, 45);
            String acordao = tesseract.doOCR(area1).trim();
            BufferedImage area2 = imagem.getSubimage(585, 430, 915, 45);
            String processo = tesseract.doOCR(area2).trim();
            BufferedImage area3 = imagem.getSubimage(585, 465, 915, 45);
            String recorrente = tesseract.doOCR(area3).trim();
            BufferedImage area4 = imagem.getSubimage(585, 505, 915, 45);
            String advogado = tesseract.doOCR(area4).trim();
            BufferedImage area5 = imagem.getSubimage(585, 545, 915, 45);
            String recorrido = tesseract.doOCR(area5).trim();
            BufferedImage area6 = imagem.getSubimage(585, 585, 915, 45);
            String procurador_do_estado = tesseract.doOCR(area6).trim();
            BufferedImage area7 = imagem.getSubimage(585, 625, 915, 45);
            String relator = tesseract.doOCR(area7).trim();
            BufferedImage area8 = imagem.getSubimage(585, 660, 915, 45);
            String data_de_publicacao = tesseract.doOCR(area8).trim();

            String texto = acordao + "\n" + 
                           processo + "\n" + 
                           recorrente + "\n" + 
                           advogado + "\n" + 
                           recorrido + "\n" + 
                           procurador_do_estado + "\n" + 
                           relator + "\n" + 
                           data_de_publicacao;

            // Cria um arquivo de texto com a data e hora do momento
            LocalDateTime data = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String atual = data.format(formato);
            FileWriter fileWriter = new FileWriter("E:\\GITHUB\\tesseract-acordao\\ocr\\src\\acordaos-specs\\" + atual + ".txt");

            // Armazena o arquivo no buffer
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Salva o texto lido no arquivo criado.
            bufferedWriter.write(texto);

            // Limpa o buffer
            bufferedWriter.close();

            System.out.println("O texto foi salvo com sucesso.");

        } catch (TesseractException e) {
            System.err.println("Erro ao carregar a imagem: " + e.getMessage());
        } catch (IOException e){
            System.err.println("Erro ao executar OCR: " + e.getMessage());
        } catch (java.awt.image.RasterFormatException e) {
            System.err.println("Coordenadas fora dos limites da imagem: " + e.getMessage());
        }
    }
}
