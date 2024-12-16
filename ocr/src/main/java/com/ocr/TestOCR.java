package com.ocr;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class TestOCR {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        ITesseract tesseract = new Tesseract();
            
        try {

            // Recebe o arquivo PDF
            File pdf = new File("E:\\GITHUB\\tesseract-acordao\\ocr\\src\\acordaos\\200 - 2024 GUILHERME SALMAZO 2021-81-06021.pdf");
            PDDocument documento = PDDocument.load(pdf);
            PDFRenderer pdfRenderer = new PDFRenderer(documento);
            BufferedImage imagem = pdfRenderer.renderImageWithDPI(0, 300);

            ImageIO.write(imagem, "PNG", new File("C:\\Users\\samue\\Desktop\\1.png"));

            // TESTE

             // 1. Aumentar resolução da imagem
             BufferedImage imagemRedimensionada = new BufferedImage(2480, 3521, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = imagemRedimensionada.createGraphics();
            graphics2D.drawImage(imagem, 0, 0, 2480, 3521, null);
            graphics2D.dispose();
 
             // 2. Converter para escala de cinza
             BufferedImage imagemEscalaCinza = new BufferedImage(2480, 3521, BufferedImage.TYPE_BYTE_GRAY);
             Graphics2D g2dCinza = imagemEscalaCinza.createGraphics();
             g2dCinza.drawImage(imagemRedimensionada, 0, 0, null);
             g2dCinza.dispose();
 
             // 3. Aplicar binarização (thresholding)
             BufferedImage imagemPosProcessamento = new BufferedImage(2480, 3521, BufferedImage.TYPE_BYTE_BINARY);
             for (int y = 0; y < 3521; y++) {
                 for (int x = 0; x < 2480; x++) {
                     // Obter intensidade do pixel
                     int rgb = imagemEscalaCinza.getRGB(x, y) & 0xFF; // Obter valor de 0-255 (escala de cinza)
                     int cor = (rgb < 200) ? 0x000000 : 0xFFFFFF; // Aplicar threshold (128 é o ponto médio)
                     imagemPosProcessamento.setRGB(x, y, cor);
                 }
             }

            // TESTE

            // Pós-processamento da imagem (Redimensionamento e Padrão monocromático)
            // BufferedImage imagemPosProcessamento = new BufferedImage(2480, 3521, BufferedImage.TYPE_BYTE_BINARY);
            // Graphics2D graphics2D = imagemPosProcessamento.createGraphics();
            // graphics2D.drawImage(imagem, 0, 0, 2480, 3521, null);
            // graphics2D.dispose();

            ImageIO.write(imagemPosProcessamento, "PNG", new File("C:\\Users\\samue\\Desktop\\2.png"));

            // Caminho tessdata (Tesseract)
            tesseract.setDatapath("E:\\OCR\\tessdata");
            tesseract.setLanguage("por");
            tesseract.setTessVariable("tessedit_char_blacklist","|[]-»");

            // Caminho arquivo para leitura (Acordão)
            // String scan = tesseract.doOCR(new File("E:\\GITHUB\\tesseract-acordao\\ocr\\src\\acordaos\\image.png"));
            // String texto = scan.replace("\n\n", "\n").replace("|", "");
            // System.out.println(texto);

            // BufferedImage imagem = ImageIO.read(new File("E:\\GITHUB\\tesseract-acordao\\ocr\\src\\acordaos\\teste.png"));
            
            BufferedImage area = imagemPosProcessamento.getSubimage(840, 560, 1480, 710);
            String[] campos = tesseract.doOCR(area).split("\n");
            String texto = "";
            int cont = 0;
            boolean status = false;
            for (int i = 0; i < campos.length; i++){
                if (campos[i].contains("/20")){
                    status = true;
                } else if (campos[i].contains("EMENTA") || cont == 8){
                    break;
                }
                if (status) {
                    texto = texto + campos[i].trim() + "\n";
                    cont = cont + 1;
                }
            };
            // String texto = tesseract.doOCR(area).replace("\n\n", "\n").trim();
            ImageIO.write(area, "PNG", new File("C:\\Users\\samue\\Desktop\\3.png"));

            // BufferedImage area1 = imagem.getSubimage(585, 385, 915, 45);
            // String acordao = tesseract.doOCR(area1).trim();
            // BufferedImage area2 = imagem.getSubimage(585, 430, 915, 45);
            // String processo = tesseract.doOCR(area2).trim();
            // BufferedImage area3 = imagem.getSubimage(585, 465, 915, 45);
            // String recorrente = tesseract.doOCR(area3).trim();
            // BufferedImage area4 = imagem.getSubimage(585, 505, 915, 45);
            // String advogado = tesseract.doOCR(area4).trim();
            // BufferedImage area5 = imagem.getSubimage(585, 545, 915, 45);
            // String recorrido = tesseract.doOCR(area5).trim();
            // BufferedImage area6 = imagem.getSubimage(585, 585, 915, 45);
            // String procurador_do_estado = tesseract.doOCR(area6).trim();
            // BufferedImage area7 = imagem.getSubimage(585, 625, 915, 45);
            // String relator = tesseract.doOCR(area7).trim();
            // BufferedImage area8 = imagem.getSubimage(585, 660, 915, 45);
            // String data_de_publicacao = tesseract.doOCR(area8).trim();

            // String texto = acordao + "\n" + 
            //                processo + "\n" + 
            //                recorrente + "\n" + 
            //                advogado + "\n" + 
            //                recorrido + "\n" + 
            //                procurador_do_estado + "\n" + 
            //                relator + "\n" + 
            //                data_de_publicacao;

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

        } catch (TesseractException | IOException e) {
            System.err.println("Erro ao carregar a imagem/executar OCR: " + e.getMessage());
        } catch (java.awt.image.RasterFormatException e) {
            System.err.println("Coordenadas fora dos limites da imagem: " + e.getMessage());
        }
    }
}
