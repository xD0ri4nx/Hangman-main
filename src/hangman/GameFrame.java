package hangman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

public class GameFrame extends JFrame {

    private static GameFrame instance;
    private BackgroundPanel backgroundPanel;
    private ImagePanel imagePanel;
    private ButtonPanel buttonPanel;
    private JLabel displayedWord;
    private JLayeredPane layeredPane;
    private JPanel contentPanel;

    private static final int BASE_WIDTH = 1200;
    private static final int BASE_HEIGHT = 800;
    private double scaleFactor = 1.0;

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }

    public void toggleFullScreen() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if (getWindowListeners().length == 0) {
            dispose();
            setUndecorated(true);
            setResizable(false);
            device.setFullScreenWindow(this);
            revalidate();
            repaint();
            setVisible(true);
        } else {
            device.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setResizable(true);
            setSize(BASE_WIDTH, BASE_HEIGHT);
            setLocationRelativeTo(null);
            scaleFactor = 1.0;
            revalidate();
            repaint();
            setVisible(true);
        }
    }

    GameFrame() {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        
        setTitle("Hangman");
        setSize(BASE_WIDTH, BASE_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        setFocusable(true);
        requestFocusInWindow();

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
        setContentPane(layeredPane);

        setupKeyBindings();
        initComponents();
        
        addComponentListener(new ComponentAdapter() {
            private volatile boolean isResizing = false;
            
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isResizing) {
                    isResizing = true;
                    SwingUtilities.invokeLater(() -> {
                        scaleToWindow();
                        isResizing = false;
                    });
                }
            }
        });

        setVisible(true);
    }

    private void setupKeyBindings() {
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullScreen");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exitFullScreen");
        am.put("toggleFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleFullScreen();
            }
        });
        am.put("exitFullScreen", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (getWindowListeners().length > 0) {
                    toggleFullScreen();
                }
            }
        });
    }

    private void initComponents() {
        String iconImagePath = "./src/hangman/images/iconImage.png";
        Image icon = new ImageIcon(iconImagePath).getImage();
        setIconImage(icon);

        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setBounds(0, 0, BASE_WIDTH, BASE_HEIGHT);
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setBounds(0, 0, BASE_WIDTH, BASE_HEIGHT);
        contentPanel.setLayout(null);

        imagePanel = ImagePanel.getInstance();
        imagePanel.setBounds(0, 0, BASE_WIDTH, (int)(BASE_HEIGHT * 0.65));
        imagePanel.setOpaque(false);
        contentPanel.add(imagePanel);

        displayedWord = new JLabel(Game.getInstance().getDisplayedWord());
        displayedWord.setHorizontalAlignment(SwingConstants.CENTER);
        displayedWord.setFont(new Font("Arial", Font.BOLD, 40));
        displayedWord.setForeground(Color.WHITE);
        displayedWord.setOpaque(true);
        displayedWord.setBackground(new Color(0, 0, 0, 150));
        displayedWord.setBounds(0, (int)(BASE_HEIGHT * 0.65), BASE_WIDTH, 80);
        contentPanel.add(displayedWord);

        buttonPanel = new ButtonPanel();
        buttonPanel.setBounds(0, (int)(BASE_HEIGHT * 0.65) + 80, BASE_WIDTH, 120);
        contentPanel.add(buttonPanel);

        layeredPane.add(contentPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void scaleToWindow() {
        if (!isDisplayable()) return;
        
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        
        if (currentWidth <= 0 || currentHeight <= 0) return;
        
        scaleFactor = Math.min((double) currentWidth / BASE_WIDTH, (double) currentHeight / BASE_HEIGHT);
        
        backgroundPanel.setBounds(0, 0, currentWidth, currentHeight);
        contentPanel.setBounds(0, 0, currentWidth, currentHeight);
        
        int scaledImageHeight = (int)(currentHeight * 0.65);
        imagePanel.setBounds(0, 0, currentWidth, scaledImageHeight);
        
        int scaledWordY = scaledImageHeight;
        displayedWord.setBounds(0, scaledWordY, currentWidth, (int)(80 * scaleFactor));
        
        int scaledButtonY = scaledWordY + (int)(80 * scaleFactor);
        buttonPanel.setBounds(0, scaledButtonY, currentWidth, (int)(120 * scaleFactor));
        
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public static GameFrame getInstance() {
        if(instance == null) {
            instance = new GameFrame();
        }
        return instance;
    }

    public void update() {
        if (imagePanel != null) {
            imagePanel.updateImagePanel();
        }
        if (displayedWord != null && Game.getInstance() != null) {
            displayedWord.setText(Game.getInstance().getDisplayedWord());
            displayedWord.setFont(new Font("Arial", Font.BOLD, (int)(40 * scaleFactor)));
        }
        if (buttonPanel != null) {
            buttonPanel.updateButtonPanel();
        }
    }

    public double getScaleFactor() {
        return scaleFactor;
    }
}
