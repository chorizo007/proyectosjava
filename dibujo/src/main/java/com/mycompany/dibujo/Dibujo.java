package com.mycompany.dibujo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Timer;
import net.sourceforge.tess4j.*;



public class Dibujo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DrawingWithRecognition gui = new DrawingWithRecognition();
                gui.setVisible(true);
                gui.startDrawing();
            }
        });
    }
}

class DrawingWithRecognition extends JFrame {

    private JPanel drawingPanel;
    private BufferedImage drawingImage;
    private Graphics2D g2d;
    private int lastX, lastY;
    private Timer recognitionTimer;

    public DrawingWithRecognition() {
        setTitle("Handwriting Recognition");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (drawingImage != null) {
                    g.drawImage(drawingImage, 0, 0, null);
                }
            }
        };
        drawingPanel.setPreferredSize(new Dimension(400, 400));
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (g2d != null) {
                    int x = e.getX();
                    int y = e.getY();
                    g2d.setColor(Color.BLACK);
                    // Aumenta el grosor de la línea
                    g2d.setStroke(new BasicStroke(5)); // Puedes ajustar el valor para cambiar el grosor
                    g2d.drawLine(lastX, lastY, x, y);
                    lastX = x;
                    lastY = y;
                    drawingPanel.repaint();
                }
            }

        });

        add(drawingPanel, BorderLayout.CENTER);

        JButton recognizeButton = new JButton("Recognize Handwriting");
        recognizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recognizeHandwriting();
            }
        });
        add(recognizeButton, BorderLayout.SOUTH);

        recognitionTimer = new Timer();
    }

    private void recognizeHandwriting() {
        try {
            if (drawingImage != null) {
                File outputfile = new File("handwriting.png");
                ImageIO.write(drawingImage, "png", outputfile);
                Tesseract tesseract = new Tesseract();
                tesseract.setLanguage("spa");
                tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");
                String result = tesseract.doOCR(outputfile);
                JOptionPane.showMessageDialog(this, "Recognized Text: " + result);
            } else {
                JOptionPane.showMessageDialog(this, "No drawing to recognize.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error recognizing handwriting: " + ex.getMessage());
        }
    }

    public void startDrawing() {
        drawingImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        g2d = drawingImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 400, 400);
    }
}
